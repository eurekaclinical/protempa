/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.proposition.interval;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.protempa.proposition.value.Granularity;

/**
 * An Interval for completely specified minimum and maximum starts and finishes,
 * and 0 duration.
 * 
 * @author Andrew Post
 */
public final class SimpleInterval extends Interval {

    SimpleInterval(Long minStart, Long maxStart,
            Granularity startGranularity, Long minFinish, Long maxFinish,
            Granularity finishGranularity) {
        super(minStart, maxStart, startGranularity, minFinish, maxFinish,
                finishGranularity, null, null, null);
        if (minStart == null) {
            throw new IllegalArgumentException("minStart cannot be null");
        }
        if (maxStart == null) {
            throw new IllegalArgumentException("maxStart cannot be null");
        }
        if (minFinish == null) {
            throw new IllegalArgumentException("minFinish cannot be null");
        }
        if (maxFinish == null) {
            throw new IllegalArgumentException("maxFinish cannot be null");
        }
    }

    SimpleInterval(Long start, Granularity startGranularity, Long finish,
            Granularity finishGranularity) {
        super(start, startGranularity, finish, finishGranularity, null, null);
        if (start == null) {
            throw new IllegalArgumentException("start cannot be null");
        }
        if (finish == null) {
            throw new IllegalArgumentException("finish cannot be null");
        }
    }

    SimpleInterval(Long timestamp, Granularity granularity) {
        super(timestamp, granularity, timestamp, granularity, null, null);
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp cannot be null");
        }
    }
    
    private void writeObject(ObjectOutputStream s) throws IOException {
        long start = getMinStart();
        long finish = getMinFinish();
        Granularity startGran = getStartGranularity();
        Granularity finishGran = getFinishGranularity();
        if (start == finish && startGran == finishGran) {
            s.writeChar(0);
            s.writeLong(start);
            s.writeObject(startGran);
        } else {
            s.writeChar(1);
            s.writeLong(start);
            s.writeLong(finish);
            s.writeObject(startGran);
            s.writeObject(finishGran);
        }
        
    }
    
    private void readObject(ObjectInputStream s) throws IOException, 
            ClassNotFoundException {
        int mode = s.readChar();
        try {
            switch (mode) {
                case 0:
                    long tstamp = s.readLong();
                    Granularity gran = (Granularity) s.readObject();
                    try {
                        init(tstamp, gran, tstamp, gran, null, null);
                    } catch (IllegalArgumentException iae) {
                        throw new InvalidObjectException(
                                "Can't restore. Invalid interval arguments: " + 
                                iae.getMessage());
                    }
                    break;
                case 1:
                    long start = s.readLong();
                    long finish = s.readLong();
                    Granularity startGran = (Granularity) s.readObject();
                    Granularity finishGran = (Granularity) s.readObject();
                    try {
                        init(start, startGran, finish, finishGran, null, null);
                    } catch (IllegalArgumentException iae) {
                        throw new InvalidObjectException(
                                "Can't restore. Invalid interval arguments: " + 
                                iae.getMessage());
                    }
                    break;
                default:
                    throw new InvalidObjectException(
                            "Can't restore. Invalid mode: " + mode);
            }
        } catch (IllegalArgumentException iae) {
            throw new InvalidObjectException("Can't restore: " + 
                    iae.getMessage());
        }
    }
}
