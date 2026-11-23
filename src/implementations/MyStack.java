package implementations;

import java.util.EmptyStackException;
import utilities.Iterator;
import utilities.StackADT;

/**
 * An array-backed implementation of the {@link StackADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class delegates all storage to an internal {@link MyArrayList},
 * treating the end of the list as the top of the stack. {@link #push(String)}
 * appends to the end; {@link #pop()} and {@link #peek()} access the last
 * element. A stack follows a <strong>Last-In, First-Out (LIFO)</strong>
 * ordering policy: the last element pushed is the first one returned.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyStack<E>}), allowing any object type to be stored in the stack.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyStack implements StackADT
{
    // Attributes
    /**
     * The {@link MyArrayList} that stores the elements of this stack.
     * Index {@code 0} is the bottom of the stack and the last index is the top of the stack.
     */
	
    private MyArrayList list;
    // Constructors
    /**
     * Constructs a new, empty {@link MyStack}.
     * <p>
     * After construction, {@link #isEmpty()} returns {@code true} and {@link #size()} returns {@code 0}.
     * </p>
     */
    
    public MyStack()
    {
        list = new MyArrayList();
    }
    
    // StackADT Interface Methods
    /**
     * {@inheritDoc}
     * <p>
     * Appends the element to the end of the internal {@link MyArrayList}, which is the top of the stack.
     * </p>
     */
    
    @Override
    public void push( String toAdd ) throws NullPointerException
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot push a null element." );
        }
        list.add( toAdd );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Removes and returns the last element of the internal {@link MyArrayList}, which is the top of the stack.
     * </p>
     */
    
    @Override
    public String pop() throws EmptyStackException
    {
        if( list.isEmpty() )
        {
            throw new EmptyStackException();
        }
        return list.remove( list.size() - 1 );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the last element of the internal {@link MyArrayList}, which is the top of the stack, without removing it.
     * </p>
     */
    
    @Override
    public String peek() throws EmptyStackException
    {
        if( list.isEmpty() )
        {
            throw new EmptyStackException();
        }
        return list.get( list.size() - 1 );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link MyArrayList#clear()}.
     * </p>
     */
    
    @Override
    public void clear()
    {
        list.clear();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link MyArrayList#isEmpty()}.
     * </p>
     */
    
    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link MyArrayList#toArray(String[])}. 
     * Because index {@code 0} of the internal list is the bottom of the stack, the returned array is already in bottom-to-top order.
     * </p>
     */
    
    @Override
    public String[] toArray( String[] holder ) throws NullPointerException
    {
        if( holder == null )
        {
            throw new NullPointerException( "Cannot copy to a null array." );
        }
        return list.toArray( holder );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link MyArrayList#contains(String)}.
     * </p>
     */
    
    @Override
    public boolean contains( String toFind ) throws NullPointerException
    {
        if( toFind == null )
        {
            throw new NullPointerException( "Cannot search for a null element." );
        }
        return list.contains( toFind );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Scans the internal {@link MyArrayList} from the last index (the top) down to index {@code 0} (the bottom), so the occurrence nearest the top is found first. 
     * The distance from the top is calculated as {@code size - index}, which makes the top element position {@code 1}.
     * </p>
     */
    
    @Override
    public int search( String toFind )
    {
        for( int i = list.size() - 1; i >= 0; i-- )
        {
            if( list.get( i ).equals( toFind ) )
            {
                return list.size() - i;
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link MyArrayList#iterator()}. 
     * The returned iterator works on a deep copy of the stack's elements and, because index {@code 0} of the internal list is the bottom of the stack, visits the elements in bottom-to-top order.
     * </p>
     */
    
    @Override
    public Iterator iterator()
    {
        return list.iterator();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns {@code false} if {@code that} is {@code null} or the two stacks have different sizes. 
     * Otherwise, an {@link Iterator} is taken from each stack and the elements are compared one pair at a time, from bottom to top, using {@link String#equals(Object)}.
     * </p>
     */
    
    @Override
    public boolean equals( StackADT that )
    {
        if( that == null )
        {
            return false;
        }
        if( this.size() != that.size() )
        {
            return false;
        }

        Iterator thisIt = this.iterator();
        Iterator thatIt = that.iterator();

        while( thisIt.hasNext() )
        {
            if( !thisIt.next().equals( thatIt.next() ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link MyArrayList#size()}.
     * </p>
     */
    
    @Override
    public int size()
    {
        return list.size();
    }
}
