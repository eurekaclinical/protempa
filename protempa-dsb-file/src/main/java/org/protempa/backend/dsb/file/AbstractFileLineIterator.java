package org.protempa.backend.dsb.file;

/*
 * #%L
 * Protempa File Data Source Backend
 * %%
 * Copyright (C) 2012 - 2015 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import au.com.bytecode.opencsv.CSVParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import org.arp.javautil.collections.Collections;
import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEvent;
import org.protempa.DataStreamingEventIterator;
import org.protempa.proposition.DefaultLocalUniqueId;
import org.protempa.SourceSystem;
import org.protempa.proposition.AbstractProposition;
import org.protempa.proposition.Constant;
import org.protempa.proposition.DataSourceBackendId;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.ValueType;

/**
 *
 * @author Andrew Post
 */
abstract class AbstractFileLineIterator implements DataStreamingEventIterator<Proposition> {

    private final LineNumberReader reader;
    private String currentLine;
    private int requiredRowLength;
    private Map<String, List<Proposition>> props;
    private Map<Proposition, Map<String, Set<UniqueId>>> refs;
    private String id;
    private int lineNo;
    private Date updateDate;
    private Date creationDate;
    private Date downloadDate;
    private final CSVParser referenceNameParser;
    private final SourceSystem sourceSystem;
    private final Long defaultPosition;
    private final Granularity defaultGranularity;
    private final IntervalFactory intervalFactory;

    protected AbstractFileLineIterator(FileDataSourceBackend backend, File file, Long defaultPosition) throws DataSourceReadException {
        this.id = backend.getId();

        try {
            this.reader = new LineNumberReader(new FileReader(file));
            while (this.reader.getLineNumber() < backend.getSkipLines()) {
                this.reader.readLine();
            }
        } catch (IOException ex) {
            throw new DataSourceReadException(ex);
        }
        this.props = new HashMap<>();
        this.refs = new HashMap<>();
        try {
            BasicFileAttributeView fileAttributeView = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
            BasicFileAttributes readAttributes = fileAttributeView.readAttributes();
            FileTime creationTime = readAttributes.creationTime();
            this.creationDate = new Date(creationTime.toMillis());
            FileTime lastModifiedTime = readAttributes.lastModifiedTime();
            this.updateDate = new Date(lastModifiedTime.toMillis());
            this.downloadDate = new Date();
        } catch (IOException ex) {
            throw new DataSourceReadException(ex);
        }

        this.lineNo = 1;

        this.referenceNameParser = new CSVParser(',');
        this.sourceSystem = backend.getSourceSystem();
        this.defaultPosition = defaultPosition;
        this.defaultGranularity = backend.getDefaultGranularity();
        this.intervalFactory = new IntervalFactory();
    }

    @Override
    public boolean hasNext() throws DataSourceReadException {
        try {
            if (this.currentLine == null) {
                do {
                    this.currentLine = this.reader.readLine();
                } while (this.currentLine != null && this.currentLine.length() < this.requiredRowLength);
            }
            return this.currentLine != null;
        } catch (IOException ex) {
            throw new DataSourceReadException(ex);
        }
    }

    @Override
    public DataStreamingEvent<Proposition> next() throws DataSourceReadException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        } else {
            DataStreamingEvent<Proposition> dse = dataStreamingEvent();
            this.currentLine = null;
            this.lineNo++;
            this.props.clear();
            this.refs.clear();
            return dse;
        }
    }

    protected abstract DataStreamingEvent<Proposition> dataStreamingEvent() throws DataSourceReadException;

    public int getLineNumber() {
        return this.lineNo;
    }

    public String getCurrentLine() {
        return this.currentLine;
    }

    public int getRequiredRowLength() {
        return requiredRowLength;
    }

    public void setRequiredRowLength(int requiredRowLength) {
        this.requiredRowLength = requiredRowLength;
    }

    protected void parseLinks(String links, String column, int colNum) throws DataSourceReadException {
        if (links != null) {
            String tokens = "[ ]>.$";
            char[] tokensArr = tokens.toCharArray();
            StringTokenizer st = new StringTokenizer(links, tokens, true);
            String lastToken = null;
            String propId = null;
            String propType = null;
            Proposition lastProposition = null;
            String referenceNames = null;
            String propertyName = null;
            boolean inPropSpec = false;
            int index = 0;
            OUTER:
            while (st.hasMoreTokens()) {
                String nextToken = st.nextToken();
                for (char token : tokensArr) {
                    if (nextToken.charAt(0) == token) {
                        lastToken = nextToken;
                        if (token == ']') {
                            inPropSpec = false;
                            Proposition tempProp = null;
                            List<Proposition> p = props.get(propId);
                            if (p != null && p.size() >= index) {
                                tempProp = p.get(index);
                            } else {
                                UniqueId uniqueId = new UniqueId(
                                        DataSourceBackendId.getInstance(this.id),
                                        new DefaultLocalUniqueId());
                                assert propType != null : "propType cannot be null";
                                switch (propType) {
                                    case "Constant":
                                        Constant c = new Constant(propId, uniqueId);
                                        c.setCreateDate(this.creationDate);
                                        c.setDownloadDate(this.downloadDate);
                                        c.setUpdateDate(this.updateDate);
                                        c.setSourceSystem(this.sourceSystem);
                                        tempProp = c;
                                        break;
                                    case "Observation":
                                        PrimitiveParameter pp = new PrimitiveParameter(propId, uniqueId);
                                        pp.setCreateDate(this.creationDate);
                                        pp.setDownloadDate(this.downloadDate);
                                        pp.setUpdateDate(this.updateDate);
                                        pp.setSourceSystem(this.sourceSystem);
                                        pp.setPosition(this.defaultPosition);
                                        pp.setGranularity(this.defaultGranularity);
                                        tempProp = pp;
                                        break;
                                    case "Event":
                                        Event e = new Event(propId, uniqueId);
                                        e.setCreateDate(this.creationDate);
                                        e.setDownloadDate(this.downloadDate);
                                        e.setUpdateDate(this.updateDate);
                                        e.setSourceSystem(this.sourceSystem);
                                        e.setInterval(this.intervalFactory.getInstance(this.defaultPosition, this.defaultGranularity));
                                        tempProp = e;
                                        break;
                                    default:
                                        throw new DataSourceReadException("Invalid proposition type " + propType);
                                }
                                Collections.putList(props, propId, tempProp);
                            }
                            propId = null;
                            propType = null;
                            if (referenceNames != null) {
                                try {
                                    String[] parseLine = this.referenceNameParser.parseLine(referenceNames);
                                    if (parseLine.length < 1 || parseLine.length > 2) {
                                        String msg = MessageFormat.format("Invalid reference in line {0}: expected referenceName[,backReferenceName] but was {1}", new Object[]{this.lineNo, referenceNames});
                                        throw new DataSourceReadException(msg);
                                    }
                                    setReferences(tempProp, lastProposition, parseLine[0], parseLine.length > 1 ? parseLine[1] : null);
                                } catch (IOException ioe) {
                                    throw new DataSourceReadException(ioe);
                                }
                                referenceNames = null;
                            }
                            lastProposition = tempProp;
                        }
                        continue OUTER;
                    }
                }
                switch (lastToken) {
                    case "[":
                        inPropSpec = true;
                        propId = nextToken;
                        break;
                    case " ":
                        if (inPropSpec) {
                            if (propType == null) {
                                propType = nextToken;
                            } else {
                                index = Integer.parseInt(nextToken);
                            }
                        }
                        break;
                    case ">":
                        referenceNames = nextToken;
                        break;
                    case ".":
                        propertyName = nextToken;
                        break;
                    case "$":
                        ValueType vt = ValueType.valueOf(nextToken);
                        if ("value".equals(propertyName) && lastProposition instanceof PrimitiveParameter) {
                            ((PrimitiveParameter) lastProposition).setValue(vt.parse(column));
                        } else {
                            assert lastProposition != null : "lastProposition cannot be null";
                            ((AbstractProposition) lastProposition).setProperty(propertyName, vt.parse(column));
                        }
                }
            }
        }
    }

    private void setReferences(Proposition tempProp, Proposition lastProposition, String referenceName, String backReferenceName) {
        assert tempProp != null : "tempProp cannot be null";
        assert lastProposition != null : "lastProposition cannot be null";
        UniqueId tempPropUid = tempProp.getUniqueId();
        Map<String, Set<UniqueId>> refToUids = refs.get(lastProposition);
        Set<UniqueId> uids;
        if (refToUids == null) {
            refToUids = new HashMap<>();
            refs.put(lastProposition, refToUids);
            uids = null;
        } else {
            uids = refToUids.get(referenceName);
        }
        if (uids == null || !uids.contains(tempPropUid)) {
            Collections.putSet(refToUids, referenceName, tempPropUid);
            ((AbstractProposition) lastProposition).addReference(referenceName, tempPropUid);
            if (backReferenceName != null) {
                ((AbstractProposition) tempProp).addReference(backReferenceName, lastProposition.getUniqueId());
            }
        }
    }

    public List<Proposition> getData() {
        List<Proposition> data = new ArrayList<>();
        for (List<Proposition> ps : this.props.values()) {
            data.addAll(ps);
        }
        return data;
    }

    @Override
    public void close() throws DataSourceReadException {
        try {
            this.reader.close();
        } catch (IOException ex) {
            throw new DataSourceReadException(ex);
        }
    }

}
