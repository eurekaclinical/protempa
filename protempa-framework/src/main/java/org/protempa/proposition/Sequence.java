package org.protempa.proposition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Set;


/**
 * A list of <code>Proposition</code>s sorted chronologically.
 * 
 * @author Andrew Post
 */
public final class Sequence<T extends TemporalProposition> implements List<T>,
		RandomAccess, PropositionVisitable {
	private static final long serialVersionUID = -6908483602466100689L;

	private final Set<String> propIds;

	private final List<T> l;

	private boolean sortNeeded;

	public Sequence(String propId) {
		this(propId, null);
	}

	public Sequence(String propId, int initialCapacity) {
		l = new ArrayList<T>(initialCapacity);
		this.propIds = Collections.singleton(propId);
	}

	public Sequence(Set<String> propIds) {
		this(propIds, null);
	}

	public Sequence(String propId, List<T> data) {
		this(Collections.singleton(propId), data);
	}

	/**
	 * 
	 */
	public Sequence(Set<String> propIds, List<T> data) {
		if (data != null) {
			l = new ArrayList<T>(data);
		} else {
			l = new ArrayList<T>();
		}
		this.propIds = propIds;
		if (l.size() > 1) {
			this.sortNeeded = true;
		}
	}

	public Set<String> getPropositionIds() {
		return propIds;
	}

	public boolean add(T o) {
		int lsize;
		if (!sortNeeded
				&& (lsize = l.size()) > 0
				&& PropositionUtil.TEMPORAL_PROPOSITION_COMPARATOR.compare(l
						.get(lsize - 1), o) > 0) {
			sortNeeded = true;
		}
		if (o == null) {
			throw new NullPointerException();
		}
		return l.add(o);
	}

	public boolean addAll(Collection<? extends T> c) {
		sortNeeded = true;
		for (T elt : c) {
			if (!add(elt)) {
				return false;
			}
		}
		return true;
	}

	public T get(int index) {
		sortIfNeeded();
		return l.get(index);
	}

	public int indexOf(Object elem) {
		sortIfNeeded();
		return Collections.binarySearch(l, (TemporalProposition) elem,
				PropositionUtil.TEMPORAL_PROPOSITION_COMPARATOR);
	}

	@Override
	public String toString() {
		sortIfNeeded();
		return (propIds != null ? (propIds.toString() + "; ") : "") + this.l;
	}

	public int size() {
		return l.size();
	}

	public boolean isEmpty() {
		return l.isEmpty();
	}

	public void add(int arg0, T arg1) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		l.clear();
	}

	public boolean contains(Object arg0) {
		return indexOf(arg0) >= 0;
	}

	public boolean containsAll(Collection arg0) {
		for (Iterator itr = arg0.iterator(); itr.hasNext();) {
			if (!contains(itr.next())) {
				return false;
			}
		}
		return true;
	}

	public Iterator<T> iterator() {
		sortIfNeeded();
		return l.iterator();
	}

	public int lastIndexOf(Object elem) {
		return indexOf(elem);
	}

	public ListIterator<T> listIterator() {
		sortIfNeeded();
		return l.listIterator();
	}

	public ListIterator<T> listIterator(int arg0) {
		sortIfNeeded();
		return l.listIterator(arg0);
	}

	public boolean remove(Object arg0) {
		int i = indexOf(arg0);
		if (i >= 0) {
			return remove(i) != null;
		} else {
			return false;
		}
	}

	public T remove(int arg0) {
		sortIfNeeded();
		return l.remove(arg0);
	}

	public boolean removeAll(Collection c) {
		boolean modified = false;
		for (Iterator itr = iterator(); itr.hasNext();) {
			if (c.contains(itr.next())) {
				itr.remove();
				modified = true;
			}
		}
		return modified;
	}

	public boolean retainAll(Collection c) {
		boolean modified = false;
		for (Iterator itr = iterator(); itr.hasNext();) {
			if (!c.contains(itr.next())) {
				itr.remove();
				modified = true;
			}
		}
		return modified;
	}

	public T set(int arg0, T arg1) {
		throw new UnsupportedOperationException();
	}

	public List<T> subList(int arg0, int arg1) {
		sortIfNeeded();
		return l.subList(arg0, arg1);
	}

	public Object[] toArray() {
		sortIfNeeded();
		return l.toArray();
	}

	public <E> E[] toArray(E[] arg0) {
		sortIfNeeded();
		return l.toArray(arg0);
	}

	private void sortIfNeeded() {
		if (sortNeeded) {
			if (l.size() > 1) {
				Collections.sort(l,
						PropositionUtil.TEMPORAL_PROPOSITION_COMPARATOR);
			}
			sortNeeded = false;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Sequence)) {
			return false;
		}
		Sequence<?> other = (Sequence<?>) obj;
		return this.l.equals(other.l)
				&& (this.propIds == other.propIds || (this.propIds != null && this.propIds
						.equals(other.propIds)));
	}

	@Override
	public int hashCode() {
		int result = this.l.hashCode();
		if (this.propIds != null) {
			result = 37 * result + this.propIds.hashCode();
		}
		return result;
	}

    public void accept(PropositionVisitor propositionVisitor) {
        for (Proposition prop : this) {
            prop.accept(propositionVisitor);
        }
    }

}
