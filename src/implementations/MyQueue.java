package implementations;

import exceptions.EmptyQueueException;
import utilities.Iterator;
import utilities.QueueADT;

/**
 * A doubly linked list-backed implementation of the {@link QueueADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class delegates all storage to an internal {@link MyDLL}, treating
 * index {@code 0} as the front of the queue and the last index as the back.
 * {@link #enqueue(String)} appends to the back; {@link #dequeue()} and
 * {@link #peek()} access the front. A queue follows a
 * <strong>First-In, First-Out (FIFO)</strong> ordering policy: the first
 * element enqueued is the first one returned.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyQueue<E>}), allowing any object type to be stored in the queue.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyQueue implements QueueADT
{
    private final MyDLL elements = new MyDLL();

    /** Creates an empty queue. */
    public MyQueue()
    {
    }

    /**
     * Adds an element to the back of the queue.
     *
     * @param toAdd the element to add
     * @throws NullPointerException if the element is null
     */
    @Override
    public void enqueue(String toAdd)
    {
        elements.add(toAdd);
    }

    /**
     * Removes and returns the front element.
     *
     * @return the element removed from the front
     * @throws EmptyQueueException if the queue is empty
     */
    @Override
    public String dequeue() throws EmptyQueueException
    {
        if (isEmpty())
        {
            throw new EmptyQueueException("Cannot dequeue from an empty queue.");
        }
        return elements.remove(0);
    }

    /**
     * Reads the front element without removing it.
     *
     * @return the element at the front
     * @throws EmptyQueueException if the queue is empty
     */
    @Override
    public String peek() throws EmptyQueueException
    {
        if (isEmpty())
        {
            throw new EmptyQueueException("Cannot peek at an empty queue.");
        }
        return elements.get(0);
    }

    /** Removes all elements from the queue. */
    @Override
    public void dequeueAll()
    {
        elements.clear();
    }

    /**
     * Checks whether the queue is empty.
     *
     * @return true if the queue has no elements
     */
    @Override
    public boolean isEmpty()
    {
        return elements.isEmpty();
    }

    /**
     * Checks whether an element appears in the queue.
     *
     * @param toFind the element to look for
     * @return true if the element is present
     * @throws NullPointerException if the requested element is null
     */
    @Override
    public boolean contains(String toFind)
    {
        return elements.contains(toFind);
    }

    /**
     * Finds the matching element closest to the front of the queue.
     *
     * @param toFind the element to look for
     * @return its one-based position from the front, or -1 if absent or null
     */
    @Override
    public int search(String toFind)
    {
        if (toFind == null)
        {
            return -1;
        }
        int index = elements.indexOf(toFind);
        return index < 0 ? -1 : index + 1;
    }

    /**
     * Creates an iterator over a snapshot of the queue.
     *
     * @return an iterator that visits the copied elements from front to back
     */
    @Override
    public Iterator iterator()
    {
        return elements.iterator();
    }

    /**
     * Compares two queues in front-to-back order.
     *
     * @param that the queue to compare with this queue
     * @return true if their elements and order match, or false if they differ
     *         or the other queue is null
     */
    @Override
    public boolean equals(QueueADT that)
    {
        if (that == this)
        {
            return true;
        }
        if (that == null || size() != that.size())
        {
            return false;
        }
        Iterator current = iterator();
        Iterator other = that.iterator();
        while (current.hasNext())
        {
            if (!current.next().equals(other.next()))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Copies the queue elements into an array in front-to-back order.
     *
     * @param holder the destination array to use if it is large enough
     * @return the supplied array or a new array if the supplied one is too small
     * @throws NullPointerException if the destination array is null
     */
    @Override
    public String[] toArray(String[] holder)
    {
        return elements.toArray(holder);
    }

    /**
     * Returns the number of elements in the queue.
     *
     * @return the current size of the queue
     */
    @Override
    public int size()
    {
        return elements.size();
    }
}