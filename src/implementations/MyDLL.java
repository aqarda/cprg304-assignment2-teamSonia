package implementations;

import java.util.NoSuchElementException;
import utilities.Iterator;
import utilities.ListADT;

/**
 * A doubly linked list (DLL) implementation of the {@link ListADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class stores {@link String} elements in a chain of {@link MyDLLNode}
 * objects, each of which holds a reference to both its predecessor ({@code pred})
 * and its successor ({@code succ}). The list maintains explicit {@code head}
 * and {@code tail} references for O(1) access to both ends of the chain.
 * Insertions and removals at arbitrary positions require O(n) traversal.
 * All index parameters are zero-based unless otherwise noted.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyDLL<E>}), allowing any object type to be stored in the list.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyDLL implements ListADT
{
    private MyDLLNode head;
    private MyDLLNode tail;
    private int size;

    /** Creates an empty list. */
    public MyDLL()
    {
    }

    /**
     * Returns the number of elements currently in the list.
     *
     * @return the current size of the list
     */
    @Override
    public int size()
    {
        return size;
    }

    /** Removes all elements from the list. */
    @Override
    public void clear()
    {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Inserts an element at the given position.
     *
     * @param index the zero-based insertion position, from 0 through size
     * @param toAdd the element to insert
     * @return true after insertion
     * @throws NullPointerException if the element is null
     * @throws IndexOutOfBoundsException if the insertion position is invalid
     */
    @Override
    public boolean add(int index, String toAdd)
    {
        if (toAdd == null)
        {
            throw new NullPointerException("Element must not be null.");
        }
        if (index < 0 || index > size)
        {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        MyDLLNode next = index == size ? null : nodeAt(index);
        MyDLLNode previous = next == null ? tail : next.getPred();
        MyDLLNode node = new MyDLLNode(toAdd);
        node.setPred(previous);
        node.setSucc(next);

        if (previous == null)
        {
            head = node;
        }
        else
        {
            previous.setSucc(node);
        }
        if (next == null)
        {
            tail = node;
        }
        else
        {
            next.setPred(node);
        }
        size++;
        return true;
    }

    /**
     * Appends an element to the end of the list.
     *
     * @param toAdd the element to append
     * @return true after insertion
     * @throws NullPointerException if the element is null
     */
    @Override
    public boolean add(String toAdd)
    {
        return add(size, toAdd);
    }

    /**
     * Appends the elements of another list in iterator order.
     *
     * @param toAdd the list to read from
     * @return true if at least one element was added
     * @throws NullPointerException if the source list or an element is null
     */
    @Override
    public boolean addAll(ListADT toAdd)
    {
        if (toAdd == null)
        {
            throw new NullPointerException("Source list must not be null.");
        }
        Iterator elements = toAdd.iterator();
        boolean changed = elements.hasNext();
        while (elements.hasNext())
        {
            add(elements.next());
        }
        return changed;
    }

    /**
     * Returns the element at a given position.
     *
     * @param index the zero-based position to read
     * @return the element at that position
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    @Override
    public String get(int index)
    {
        return nodeAt(index).getElement();
    }

    /**
     * Removes the element at a given position.
     *
     * @param index the zero-based position to remove
     * @return the removed element
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    @Override
    public String remove(int index)
    {
        return unlink(nodeAt(index));
    }

    /**
     * Removes the first occurrence of an element.
     *
     * @param toRemove the element to find and remove
     * @return the removed element, or null if it is absent
     * @throws NullPointerException if the requested element is null
     */
    @Override
    public String remove(String toRemove)
    {
        if (toRemove == null)
        {
            throw new NullPointerException("Element must not be null.");
        }
        for (MyDLLNode node = head; node != null; node = node.getSucc())
        {
            if (toRemove.equals(node.getElement()))
            {
                return unlink(node);
            }
        }
        return null;
    }

    /**
     * Replaces the element at a given position.
     *
     * @param index the zero-based position to update
     * @param toChange the new element
     * @return the element previously stored there
     * @throws NullPointerException if the new element is null
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    @Override
    public String set(int index, String toChange)
    {
        if (toChange == null)
        {
            throw new NullPointerException("Element must not be null.");
        }
        MyDLLNode node = nodeAt(index);
        String oldElement = node.getElement();
        node.setElement(toChange);
        return oldElement;
    }

    /**
     * Checks whether the list is empty.
     *
     * @return true if the list has no elements
     */
    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * Checks whether an element appears in the list.
     *
     * @param toFind the element to look for
     * @return true if the element is present
     * @throws NullPointerException if the requested element is null
     */
    @Override
    public boolean contains(String toFind)
    {
        return indexOf(toFind) >= 0;
    }

    /**
     * Finds the first occurrence of an element.
     *
     * @param toFind the element to look for
     * @return its zero-based index, or -1 if it is absent
     * @throws NullPointerException if the requested element is null
     */
    @Override
    public int indexOf(String toFind)
    {
        if (toFind == null)
        {
            throw new NullPointerException("Element must not be null.");
        }
        int index = 0;
        for (MyDLLNode node = head; node != null; node = node.getSucc())
        {
            if (toFind.equals(node.getElement()))
            {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Copies the list elements into an array in head-to-tail order.
     *
     * @param toHold the destination array to use if it is large enough
     * @return the supplied array or a new array if the supplied one is too small
     * @throws NullPointerException if the destination array is null
     */
    @Override
    public String[] toArray(String[] toHold)
    {
        if (toHold == null)
        {
            throw new NullPointerException("Destination array must not be null.");
        }
        String[] result = toHold.length < size ? new String[size] : toHold;
        int index = 0;
        for (MyDLLNode node = head; node != null; node = node.getSucc())
        {
            result[index++] = node.getElement();
        }
        return result;
    }

    /**
     * Creates an iterator over a snapshot of the list.
     *
     * @return an iterator that visits the copied elements from first to last
     */
    @Override
    public Iterator iterator()
    {
        return new SnapshotIterator();
    }

    /**
     * Finds a node by walking from the nearer end of the list.
     *
     * @param index the zero-based position to look up
     * @return the node at that position
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    private MyDLLNode nodeAt(int index)
    {
        if (index < 0 || index >= size)
        {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        if (index <= (size - 1) / 2)
        {
            MyDLLNode node = head;
            for (int position = 0; position < index; position++)
            {
                node = node.getSucc();
            }
            return node;
        }

        MyDLLNode node = tail;
        for (int position = size - 1; position > index; position--)
        {
            node = node.getPred();
        }
        return node;
    }

    /**
     * Removes a node and reconnects its neighbors or updates the endpoints.
     *
     * @param node an existing node in this list
     * @return the element stored in the removed node
     */
    private String unlink(MyDLLNode node)
    {
        MyDLLNode previous = node.getPred();
        MyDLLNode next = node.getSucc();
        if (previous == null)
        {
            head = next;
        }
        else
        {
            previous.setSucc(next);
        }
        if (next == null)
        {
            tail = previous;
        }
        else
        {
            next.setPred(previous);
        }
        size--;
        return node.getElement();
    }

    /** Traverses an independent snapshot of the list in head-to-tail order. */
    private final class SnapshotIterator implements Iterator
    {
        private final String[] elements;
        private int position;

        /** Copies the current list into a private array of immutable strings. */
        private SnapshotIterator()
        {
            elements = toArray(new String[size]);
        }

        /**
         * Checks whether the snapshot has another element.
         *
         * @return true when another element remains
         */
        @Override
        public boolean hasNext()
        {
            return position < elements.length;
        }

        /**
         * Returns the next element and advances the snapshot position.
         *
         * @return the next element
         * @throws NoSuchElementException when all elements have been returned
         */
        @Override
        public String next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException("No more list elements.");
            }
            return elements[position++];
        }
    }
}