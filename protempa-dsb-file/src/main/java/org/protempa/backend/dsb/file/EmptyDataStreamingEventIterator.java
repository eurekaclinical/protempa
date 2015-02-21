/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

import java.util.NoSuchElementException;
import org.protempa.DataStreamingEvent;
import org.protempa.DataStreamingEventIterator;

/**
 *
 * @author Andrew Post
 */
public class EmptyDataStreamingEventIterator<E> implements DataStreamingEventIterator<E> {

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public DataStreamingEvent<E> next() {
        throw new NoSuchElementException();
    }

    @Override
    public void close() {
    }
    
}
