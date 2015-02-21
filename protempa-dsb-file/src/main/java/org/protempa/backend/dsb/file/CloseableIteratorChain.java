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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEvent;
import org.protempa.DataStreamingEventIterator;

/**
 *
 * @author Andrew Post
 */
class CloseableIteratorChain<E> implements DataStreamingEventIterator<E> {
    /** The chain of iterators */
    private final Queue<DataStreamingEventIterator<E>> iteratorChain = new LinkedList<DataStreamingEventIterator<E>>();

    /** The current iterator */
    private DataStreamingEventIterator<E> currentIterator = null;

    /**
     * The "last used" Iterator is the Iterator upon which next() or hasNext()
     * was most recently called used for the remove() operation only
     */
    private DataStreamingEventIterator<E> lastUsedIterator = null;

    /**
     * ComparatorChain is "locked" after the first time compare(Object,Object)
     * is called
     */
    private boolean isLocked = false;

    //-----------------------------------------------------------------------
    /**
     * Construct an IteratorChain with no Iterators.
     * <p>
     * You will normally use {@link #addIterator(Iterator)} to add some
     * iterators after using this constructor.
     */
    CloseableIteratorChain() {
        super();
    }

    /**
     * Construct an IteratorChain with a single Iterator.
     * <p>
     * This method takes one iterator. The newly constructed iterator will
     * iterate through that iterator. Thus calling this constructor on its own
     * will have no effect other than decorating the input iterator.
     * <p>
     * You will normally use {@link #addIterator(Iterator)} to add some more
     * iterators after using this constructor.
     *
     * @param iterator the first child iterator in the IteratorChain, not null
     * @throws NullPointerException if the iterator is null
     */
    CloseableIteratorChain(final DataStreamingEventIterator<E> iterator) {
        super();
        addIterator(iterator);
    }

    /**
     * Constructs a new <code>IteratorChain</code> over the two given iterators.
     * <p>
     * This method takes two iterators. The newly constructed iterator will
     * iterate through each one of the input iterators in turn.
     *
     * @param first the first child iterator in the IteratorChain, not null
     * @param second the second child iterator in the IteratorChain, not null
     * @throws NullPointerException if either iterator is null
     */
    CloseableIteratorChain(final DataStreamingEventIterator<E> first, final DataStreamingEventIterator<E> second) {
        super();
        addIterator(first);
        addIterator(second);
    }

    /**
     * Constructs a new <code>IteratorChain</code> over the array of iterators.
     * <p>
     * This method takes an array of iterators. The newly constructed iterator
     * will iterate through each one of the input iterators in turn.
     *
     * @param iteratorChain the array of iterators, not null
     * @throws NullPointerException if iterators array is or contains null
     */
    CloseableIteratorChain(final DataStreamingEventIterator<E>... iteratorChain) {
        super();
        for (final DataStreamingEventIterator<E> element : iteratorChain) {
            addIterator(element);
        }
    }

    /**
     * Constructs a new <code>IteratorChain</code> over the collection of
     * iterators.
     * <p>
     * This method takes a collection of iterators. The newly constructed
     * iterator will iterate through each one of the input iterators in turn.
     *
     * @param iteratorChain the collection of iterators, not null
     * @throws NullPointerException if iterators collection is or contains null
     * @throws ClassCastException if iterators collection doesn't contain an
     * iterator
     */
    CloseableIteratorChain(final Collection<DataStreamingEventIterator<E>> iteratorChain) {
        super();
        for (final DataStreamingEventIterator<E> iterator : iteratorChain) {
            addIterator(iterator);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Add an Iterator to the end of the chain
     *
     * @param iterator Iterator to add
     * @throws IllegalStateException if I've already started iterating
     * @throws NullPointerException if the iterator is null
     */
    void addIterator(final DataStreamingEventIterator<E> iterator) {
        checkLocked();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        iteratorChain.add(iterator);
    }

    /**
     * Returns the remaining number of Iterators in the current IteratorChain.
     *
     * @return Iterator count
     */
    int size() {
        return iteratorChain.size();
    }

    /**
     * Determine if modifications can still be made to the IteratorChain.
     * IteratorChains cannot be modified once they have executed a method from
     * the Iterator interface.
     *
     * @return true if IteratorChain cannot be modified, false if it can
     */
    boolean isLocked() {
        return isLocked;
    }

    /**
     * Checks whether the iterator chain is now locked and in use.
     */
    private void checkLocked() {
        if (isLocked == true) {
            throw new UnsupportedOperationException(
                    "IteratorChain cannot be changed after the first use of a method from the Iterator interface");
        }
    }

    /**
     * Lock the chain so no more iterators can be added. This must be called
     * from all Iterator interface methods.
     */
    private void lockChain() {
        if (isLocked == false) {
            isLocked = true;
        }
    }

    /**
     * Updates the current iterator field to ensure that the current Iterator is
     * not exhausted
     */
    protected void updateCurrentIterator() throws DataSourceReadException {
        if (currentIterator == null) {
            if (iteratorChain.isEmpty()) {
                currentIterator = new EmptyDataStreamingEventIterator();
            } else {
                currentIterator = iteratorChain.remove();
            }
            // set last used iterator here, in case the user calls remove
            // before calling hasNext() or next() (although they shouldn't)
            lastUsedIterator = currentIterator;
        }

        while (currentIterator.hasNext() == false && !iteratorChain.isEmpty()) {
            currentIterator.close();
            currentIterator = iteratorChain.remove();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Return true if any Iterator in the IteratorChain has a remaining element.
     *
     * @return true if elements remain
     */
    @Override
    public boolean hasNext() throws DataSourceReadException {
        lockChain();
        updateCurrentIterator();
        lastUsedIterator = currentIterator;

        return currentIterator.hasNext();
    }

    /**
     * Returns the next Object of the current Iterator
     *
     * @return Object from the current Iterator
     * @throws java.util.NoSuchElementException if all the Iterators are
     * exhausted
     */
    @Override
    public DataStreamingEvent<E> next() throws DataSourceReadException {
        lockChain();
        updateCurrentIterator();
        lastUsedIterator = currentIterator;
        return currentIterator.next();
    }

    @Override
    public void close() throws DataSourceReadException {
        for (Iterator<DataStreamingEventIterator<E>> itr = this.iteratorChain.iterator(); itr.hasNext();) {
            DataStreamingEventIterator<E> next = itr.next();
            next.close();
            itr.remove();
        }
    }

}
