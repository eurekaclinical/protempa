/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arp.javautil.collections;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Andrew Post
 */
public class CompositeList<E> extends AbstractSequentialList<E> {

    private final Collection<List<E>> collOfLists;

    public CompositeList() {
        this(new ArrayList<List<E>>(0));
    }

    public CompositeList(Collection<List<E>> collOfLists) {
        if (collOfLists != null) {
            for (List<E> list : collOfLists) {
                if (list == null) {
                    throw new IllegalArgumentException(
                            "Cannot have any null lists");
                }
            }
            this.collOfLists = new ArrayList<List<E>>(collOfLists);
        } else {
            this.collOfLists = new ArrayList<List<E>>();
        }
        
    }

    public void addList(List<E> list) {
        this.collOfLists.add(list);
    }

    @Override
    public ListIterator<E> listIterator(int i) {
        return new CompositeListIterator(i);
    }

    @Override
    public int size() {
        int result = 0;
        for (List<E> list : this.collOfLists) {
            result += list.size();
        }
        return result;
    }

    private class CompositeListIterator implements ListIterator<E> {

        private List<ListIterator<E>> itrs;
        private int itrsIndex;
        private int itrsSize;
        private ListIterator<E> currentItr;
        private int index;

        private CompositeListIterator(int i) {
            this.itrs = new ArrayList<ListIterator<E>>(CompositeList.this.collOfLists.size());
            for (List<E> list : CompositeList.this.collOfLists) {
                itrs.add(list.listIterator());
            }
            this.index = 0;
            if (!itrs.isEmpty()) {
                this.currentItr = itrs.get(0);
            }
            this.itrsIndex = 0;
            this.itrsSize = this.itrs.size();
            while (this.index < i) {
                next();
            }
        }

        @Override
        public boolean hasNext() {
            boolean result = this.currentItr != null ? this.currentItr.hasNext() : false;
            int i = this.itrsIndex;
            while (!result && i < this.itrsSize - 1) {
                ListIterator<E> next = this.itrs.get(++i);
                result = next.hasNext();
            }
            return result;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            boolean hasNext = this.currentItr != null ? this.currentItr.hasNext() : false;
            while (!hasNext && this.itrsIndex < this.itrsSize - 1) {
                this.currentItr = this.itrs.get(++this.itrsIndex);
                hasNext = this.currentItr.hasNext();
            }
            E nextElt = this.currentItr.next();
            this.index++;
            return nextElt;
        }

        @Override
        public boolean hasPrevious() {
            if (this.currentItr == null) {
                return false;
            }
            boolean result = this.currentItr.hasPrevious();
            ListIterator<E> prev;
            int i = this.itrsIndex;
            while (!result && i > 0) {
                prev = this.itrs.get(--i);
                result = prev.hasPrevious();
            }
            return result;
        }

        @Override
        public E previous() {
            boolean hasPrev = this.currentItr != null ? this.currentItr.hasPrevious() : false;
            while (!hasPrev && this.itrsIndex > 0) {
                this.currentItr = this.itrs.get(--this.itrsIndex);
                hasPrev = this.currentItr.hasPrevious();
            }
            E prevElt = this.currentItr.previous();
            this.index--;
            return prevElt;
        }

        @Override
        public int nextIndex() {
            return this.currentItr != null ? this.index + 1 : 0;
        }

        @Override
        public int previousIndex() {
            return this.index - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void set(E e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
