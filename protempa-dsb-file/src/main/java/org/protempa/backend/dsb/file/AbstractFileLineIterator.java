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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collection;
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
import org.protempa.proposition.AbstractProposition;
import org.protempa.proposition.Constant;
import org.protempa.proposition.DataSourceBackendId;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
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
    
    protected AbstractFileLineIterator(File file, int skipLines, String id) throws DataSourceReadException {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;

        try {
            this.reader = new LineNumberReader(new FileReader(file));
            while (this.reader.getLineNumber() < skipLines) {
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
            String referenceName = null;
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
                                        new FileUniqueId(this.lineNo, colNum, propId, index));
                                switch (propType) {
                                    case "Constant":
                                        Constant c = new Constant(propId, uniqueId);
                                        c.setCreateDate(this.creationDate);
                                        c.setDownloadDate(this.downloadDate);
                                        c.setUpdateDate(this.updateDate);
                                        tempProp = c;
                                        break;
                                    case "Observation":
                                        PrimitiveParameter pp = new PrimitiveParameter(propId, uniqueId);
                                        pp.setCreateDate(this.creationDate);
                                        pp.setDownloadDate(this.downloadDate);
                                        pp.setUpdateDate(this.updateDate);
                                        tempProp = pp;
                                        break;
                                    case "Event":
                                        Event e = new Event(propId, uniqueId);
                                        e.setCreateDate(this.creationDate);
                                        e.setDownloadDate(this.downloadDate);
                                        e.setUpdateDate(this.updateDate);
                                        tempProp = e;
                                        break;
                                    default:
                                        throw new DataSourceReadException("Invalid proposition type " + propType);
                                }
                                Collections.putList(props, propId, tempProp);
                            }
                            propId = null;
                            propType = null;
                            if (referenceName != null) {
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
                                }
                                referenceName = null;
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
                        referenceName = nextToken;
                        break;
                    case ".":
                        propertyName = nextToken;
                        break;
                    case "$":
                        ValueType vt = ValueType.valueOf(nextToken);
                        if ("value".equals(propertyName) && lastProposition instanceof PrimitiveParameter) {
                            ((PrimitiveParameter) lastProposition).setValue(vt.parse(column));
                        } else {
                            ((AbstractProposition) lastProposition).setProperty(propertyName, vt.parse(column));
                        }
                }
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
