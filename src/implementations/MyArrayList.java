package implementations;

import java.util.Arrays;
import java.util.NoSuchElementException;
import utilities.Iterator;
import utilities.ListADT;

/**
 * A dynamic array-based implementation of the {@link ListADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class stores {@link String} elements in an internally managed array
 * that grows automatically when capacity is reached. The growth amount is
 * determined by a configurable growth rate expressed as a decimal percentage
 * of the current array length (e.g., {@code 0.25} means 25% growth).
 * All index parameters are zero-based unless otherwise noted.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyArrayList<E>}), allowing any object type to be stored in the list.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyArrayList implements ListADT
{
    // Constants
    /** The number of slots in the internal array when no capacity is given. */
    public static final int DEFAULT_CAPACITY = 10;

    /** The growth rate used when no growth rate is given. */
    public static final double DEFAULT_GROWTH_RATE = 0.25;

    // Attributes
    /**
     * The internal array that stores the {@link String} elements. 
     * Positions {@code 0} to {@code size - 1} hold elements, and any remaining positions are unused and hold {@code null}.
     */
    
    private String[] elements;
    /** The number of {@link String} elements currently stored in the list. */
    
    private int size;
    /**
     * This is the growth rate used to enlarge the internal array when it is full,
     */
    
    private double growthRate;
    // Constructors
    /**
     * Constructs new empty {@link MyArrayList} with the default capacity ({@link #DEFAULT_CAPACITY}) and the default growth rate ({@link #DEFAULT_GROWTH_RATE}).
     * <p>
     * After construction, {@link #isEmpty()} returns {@code true} and {@link #size()} returns {@code 0}.
     * </p>
     */
    
    public MyArrayList()
    {
        elements = new String[ DEFAULT_CAPACITY ];
        size = 0;
        growthRate = DEFAULT_GROWTH_RATE;
    }

    /**
     * This constructs a new, empty {@link MyArrayList} with the specified starting capacity and growth rate.
     * <p>
     * After construction, {@link #isEmpty()} returns {@code true} and {@link #size()} returns {@code 0}.
     * </p>
     *
     * @param initialCapacity the number of slots in the internal array when the list is created; must be at least {@code 1}
     * @param growthRate the decimal percentage by which the internal array grows when it is full (e.g., {@code 0.25} for 25%); must be greater than {@code 0}
     * @throws IllegalArgumentException if {@code initialCapacity} is less than {@code 1} or {@code growthRate} is not greater than {@code 0}
     */
    
    public MyArrayList( int initialCapacity, double growthRate ) throws IllegalArgumentException
    {
        if( initialCapacity < 1 )
        {
            throw new IllegalArgumentException( "Initial capacity must be at least 1: " + initialCapacity );
        }
        if( growthRate <= 0 )
        {
            throw new IllegalArgumentException( "Growth rate must be greater than 0: " + growthRate );
        }

        elements = new String[ initialCapacity ];
        size = 0;
        this.growthRate = growthRate;
    }

    // ListADT Interface Methods
    /**
     * {@inheritDoc}
     */
    
    @Override
    public int size()
    {
        return size;
    }
    /**
     * {@inheritDoc}
     * <p>
     * This sets every used position of the internal array back to {@code null} and resets the size counter to {@code 0}. 
     * The internal array keeps its current length so it can be reused.
     * </p>
     */
    
    @Override
    public void clear()
    {
        for( int i = 0; i < size; i++ )
        {
            elements[ i ] = null;
        }
        size = 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Grows the internal array first if it is full. Every element from {@code index} to the end of the list is then shifted one position to the right, starting from the last element so that no element is overwritten, and the new element is placed at {@code index}.
     * </p>
     */
    
    @Override
    public boolean add( int index, String toAdd )
            throws NullPointerException, IndexOutOfBoundsException
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot store a null element." );
        }
        if( index < 0 || index > size )
        {
            throw new IndexOutOfBoundsException( "Index outside bounds: " + index );
        }

        growIfFull();

        for( int i = size; i > index; i-- )
        {
            elements[ i ] = elements[ i - 1 ];
        }

        elements[ index ] = toAdd;
        size++;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link #add(int, String)} using {@link #size()} as the index, effectively appending the element to the end of the list.
     * </p>
     */
    
    @Override
    public boolean add( String toAdd ) throws NullPointerException
    {
        return add( size, toAdd );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses the provided list's {@link Iterator} to visit each element in order and appends it to the end of this list via {@link #add(String)}.
     * Because the iterator works on a copy of the provided list, adding a list to itself is also handled correctly.
     * </p>
     */
    
    @Override
    public boolean addAll( ListADT toAdd ) throws NullPointerException
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot add a null list." );
        }

        boolean changed = false;
        Iterator it = toAdd.iterator();

        while( it.hasNext() )
        {
            add( it.next() );
            changed = true;
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the element stored at {@code index} in the internal array directly, without any traversal.
     * </p>
     */
    
    @Override
    public String get( int index ) throws IndexOutOfBoundsException
    {
        if( index < 0 || index >= size )
        {
            throw new IndexOutOfBoundsException( "Index out of bounds: " + index );
        }
        return elements[ index ];
    }

    /**
     * {@inheritDoc}
     * <p>
     * Stores the element at {@code index}, then shifts every element after it one position to the left to close the gap. The last used position is set back to {@code null} and the size counter is decreased by one.
     * </p>
     */
    
    @Override
    public String remove( int index ) throws IndexOutOfBoundsException
    {
        if( index < 0 || index >= size )
        {
            throw new IndexOutOfBoundsException( "Index out of bounds: " + index );
        }

        String removed = elements[ index ];

        for( int i = index; i < size - 1; i++ )
        {
            elements[ i ] = elements[ i + 1 ];
        }

        elements[ size - 1 ] = null;
        size--;
        return removed;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses {@link #indexOf(String)} to locate the first matching element, then delegates to {@link #remove(int)} to perform the actual removal.
     * Returns {@code null} if the element is not found.
     * </p>
     */
    
    @Override
    public String remove( String toRemove ) throws NullPointerException
    {
        if( toRemove == null )
        {
            throw new NullPointerException( "Cannot remove a null element." );
        }

        int index = indexOf( toRemove );

        if( index > -1 )
        {
            return remove( index );
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Stores the existing element at {@code index}, replaces it with {@code toChange}, and returns the previous value. The size of the list is unaffected.
     * </p>
     */
    
    @Override
    public String set( int index, String toChange )
            throws NullPointerException, IndexOutOfBoundsException
    {
        if( toChange == null )
        {
            throw new NullPointerException( "Cannot store a null element." );
        }
        if( index < 0 || index >= size )
        {
            throw new IndexOutOfBoundsException( "Index out of bounds: " + index );
        }

        String old = elements[ index ];
        elements[ index ] = toChange;
        return old;
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link #indexOf(String)} and returns {@code true} if a matching element was found.
     * </p>
     */
    
    @Override
    public boolean contains( String toFind ) throws NullPointerException
    {
        if( toFind == null )
        {
            throw new NullPointerException( "Cannot search for a null element." );
        }
        return indexOf( toFind ) != -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Performs a linear scan of the internal array from position {@code 0} to {@code size - 1} using {@link String#equals(Object)}, returning the position of the first matching element.
     * </p>
     */
    
    @Override
    public int indexOf( String toFind ) throws NullPointerException
    {
        if( toFind == null )
        {
            throw new NullPointerException( "Cannot search for a null element." );
        }

        for( int i = 0; i < size; i++ )
        {
            if( toFind.equals( elements[ i ] ) )
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If {@code toHold} is smaller than {@link #size()}, a new {@link String} array of the required size is allocated using {@link Arrays#copyOf(Object[], int)}. 
     * The elements are then copied into the array in order, starting at position {@code 0}.
     * </p>
     */
    
    @Override
    public String[] toArray( String[] toHold ) throws NullPointerException
    {
        if( toHold == null )
        {
            throw new NullPointerException( "Cannot copy to a null array." );
        }
        if( toHold.length < size )
        {
            toHold = Arrays.copyOf( toHold, size );
        }

        for( int i = 0; i < size; i++ )
        {
            toHold[ i ] = elements[ i ];
        }
        return toHold;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a new {@link ArrayListIterator} that operates on a deep copy of the list's elements captured at the moment of this call.
     * Later changes to this list do not affect the returned iterator.
     * </p>
     */
    
    @Override
    public Iterator iterator()
    {
        return new ArrayListIterator();
    }
    // Helper Methods
    /**
     * Enlarges the internal array when every position is in use.
     * <p>
     * The new length is the current length plus the current length multiplied by the growth rate. 
     * Because the result is rounded down to a whole number, a small array could grow by {@code 0}; in that case the array grows by {@code 1} instead.
     * The elements are moved to the new array using {@link Arrays#copyOf(Object[], int)}.
     * </p>
     */
    private void growIfFull()
    {
        if( size == elements.length )
        {
            int extra = (int) ( elements.length * growthRate );

            if( extra < 1 )
            {
                extra = 1;
            }
            elements = Arrays.copyOf( elements, elements.length + extra );
        }
    }
    // Inner Classes
    /**
     * A private inner class that implements the {@link Iterator} interface for traversing the {@link String} elements of a {@link MyArrayList}.
     * <p>
     * On construction, a deep copy of the list's current elements is captured into an internal {@link String} array. 
     * All subsequent calls to {@link #hasNext()} and {@link #next()} operate exclusively on this snapshot, making the iterator independent of any structural changes made to the original list after the iterator was created.
     * </p>
     */
    private class ArrayListIterator implements Iterator
    {
        private String[] copyOfElements;

        /** Current position of the iterator cursor. */
        private int pos;

        /**
         * Constructs an {@link ArrayListIterator} by capturing a deep copy of the current elements in the enclosing {@link MyArrayList}.
         * <p>
         * Only the used positions ({@code 0} to {@code size - 1}) of the internal array are copied, using {@link Arrays#copyOf(Object[], int)}.
         * </p>
         */
        public ArrayListIterator()
        {
            copyOfElements = Arrays.copyOf( elements, size );
            pos = 0;
        }

        /**
         * Returns {@code true} if the iterator has more {@link String} elements remaining in the snapshot.
         * @return {@code true} if at least one more element exists in the snapshot; {@code false} otherwise
         */
        
        @Override
        public boolean hasNext()
        {
            return pos < copyOfElements.length;
        }

        /**
         * Returns the next {@link String} element in the traversal sequence and advances the iterator position by one.
         * @return the next {@link String} element in the snapshot @throws NoSuchElementException if no more elements remain in the snapshot
         */
        
        @Override
        public String next() throws NoSuchElementException
        {
            if( !hasNext() )
            {
                throw new NoSuchElementException( "No more elements in the iteration." );
            }
            return copyOfElements[ pos++ ];
        }
    }
}
