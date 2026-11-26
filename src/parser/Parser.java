package parser;

import exceptions.EmptyQueueException;
import implementations.MyArrayList;
import implementations.MyQueue;
import implementations.MyStack;
import java.util.EmptyStackException;
import utilities.Iterator;

/**
 * A bracket parser for CPRG 304 at SAIT, built on {@link MyStack} and
 * {@link MyQueue} and following Kitty's parser algorithm.
 * <p>
 * The parser checks that every round bracket {@code ( )}, curly brace
 * {@code { }}, square bracket {@code [ ]} and angle bracket {@code < >} is
 * properly matched and nested. Every other character is ignored, although
 * brackets inside comments and string literals still count. Less-than and
 * greater-than signs used as comparisons (for example {@code a < b},
 * {@code a <= b} or {@code a >= b}) are not treated as angle brackets.
 * </p>
 *
 * <p><strong>Algorithm:</strong> opening brackets are pushed on a stack.
 * A closing bracket is resolved against the top of the stack, then the head
 * of {@code errorQ}, and otherwise either unwinds the stack to its partner or
 * is placed in {@code extrasQ}. After the last line, anything left on the
 * stack is moved to {@code errorQ} and the two queues are compared. Wherever
 * Kitty's algorithm would discard a matching pair of an abandoned opener and a
 * stray closer, both brackets are still reported, so no mistake is lost.</p>
 *
 * <p>This class does no file input and no console output. The calling
 * application supplies the text one line at a time, either through
 * {@link #parseLine(String, int)} followed by {@link #finish()}, or in one
 * call through {@link #parse(String[])}.</p>
 *
 * <p><strong>Design Note – String-Specific Data Structures:</strong><br>
 * {@link MyStack} and {@link MyQueue} only hold {@link String} elements, so
 * each bracket is stored together with its line number in a single String of
 * the form {@code bracket:line}, for example {@code [:17}.</p>
 */
public class Parser
{
    // Attributes

    /** Separator between the bracket and the line number in a stored String. */
    private static final char SEPARATOR = ':';

    /** Holds the opening brackets that have not been matched yet. */
    private MyStack stack;

    /** Holds opening brackets that were abandoned and unmatched closers found on an empty stack. */
    private MyQueue errorQ;

    /** Holds closing brackets that have no opening bracket anywhere on the stack. */
    private MyQueue extrasQ;

    /** Holds every error found, as encoded {@code bracket:line} Strings, in the order they were reported. */
    private MyArrayList reported;

    // Constructors

    /**
     * Constructs a new, empty {@link Parser} that is ready to parse a file.
     */
    public Parser()
    {
        reset();
    }

    // Public Methods

    /**
     * Discards all state so that the parser can be used for another file.
     */
    public void reset()
    {
        stack = new MyStack();
        errorQ = new MyQueue();
        extrasQ = new MyQueue();
        reported = new MyArrayList();
    }

    /**
     * Parses a whole file's worth of lines and returns the errors found.
     * <p>
     * The parser is reset first, so the same {@link Parser} can be reused.
     * Line numbers start at {@code 1}.
     * </p>
     *
     * @param lines  the lines of the file, in order
     * @return the errors sorted by line number; an empty array if the text is correct
     * @throws NullPointerException if {@code lines} is {@code null}
     */
    public ParseError[] parse( String[] lines ) throws NullPointerException
    {
        if( lines == null )
        {
            throw new NullPointerException( "Cannot parse a null array of lines." );
        }

        reset();
        for( int i = 0; i < lines.length; i++ )
        {
            parseLine( lines[i], i + 1 );
        }
        return finish();
    }

    /**
     * Scans one line of text and processes every bracket on it.
     * <p>
     * Call this once per line, in order, and then call {@link #finish()}.
     * A {@code null} line is treated as an empty line.
     * </p>
     *
     * @param line        the text of the line
     * @param lineNumber  the one-based number of the line, remembered with each bracket
     */
    public void parseLine( String line, int lineNumber )
    {
        if( line == null )
        {
            return;
        }

        boolean[] angle = classifyAngles( line );

        for( int i = 0; i < line.length(); i++ )
        {
            char c = line.charAt( i );

            try
            {
                if( isOpening( c ) || ( c == '<' && angle[i] ) )
                {
                    stack.push( encode( c, lineNumber ) );
                }
                else if( isClosing( c ) || ( c == '>' && angle[i] ) )
                {
                    processClosing( c, lineNumber );
                }
            }
            catch( EmptyStackException e )
            {
                // The stack was empty where a bracket was expected; the closer is an error.
                report( encode( c, lineNumber ) );
            }
            catch( EmptyQueueException e )
            {
                // A queue was empty where a bracket was expected; the closer is an error.
                report( encode( c, lineNumber ) );
            }
        }
    }

    /**
     * Completes the parse after the last line has been supplied and returns
     * every error found.
     * <p>
     * Anything still on the stack is moved to the error queue, and the two
     * queues are then compared. Both brackets of a matching pair are reported
     * rather than being cancelled out.
     * </p>
     *
     * @return the errors sorted by line number; an empty array if the text is correct
     */
    public ParseError[] finish()
    {
        try
        {
            while( !stack.isEmpty() )
            {
                errorQ.enqueue( stack.pop() );
            }

            while( !errorQ.isEmpty() || !extrasQ.isEmpty() )
            {
                if( errorQ.isEmpty() || extrasQ.isEmpty() )
                {
                    // Only one queue has anything left: every bracket in it is an error.
                    drain( errorQ );
                    drain( extrasQ );
                }
                else
                {
                    String errorItem = errorQ.peek();
                    String extraItem = extrasQ.peek();

                    if( matches( bracketOf( errorItem ), bracketOf( extraItem ) ) )
                    {
                        // Kitty's algorithm would cancel this pair; both are still mistakes.
                        report( errorQ.dequeue() );
                        report( extrasQ.dequeue() );
                    }
                    else
                    {
                        report( errorQ.dequeue() );
                    }
                }
            }
        }
        catch( EmptyStackException e )
        {
            // The stack was emptied by the loop above, so this cannot normally happen; stop safely.
            stack.clear();
        }
        catch( EmptyQueueException e )
        {
            // The queues were checked before use, so this cannot normally happen; stop safely.
            errorQ.dequeueAll();
            extrasQ.dequeueAll();
        }

        return sortedErrors();
    }

    // Private Helper Methods

    /**
     * Resolves one closing bracket using Kitty's algorithm.
     *
     * @param closer      the closing bracket
     * @param lineNumber  the line the bracket appears on
     * @throws EmptyStackException  if the stack is unexpectedly empty
     * @throws EmptyQueueException  if a queue is unexpectedly empty
     */
    private void processClosing( char closer, int lineNumber ) throws EmptyStackException, EmptyQueueException
    {
        String item = encode( closer, lineNumber );

        if( !stack.isEmpty() && matches( bracketOf( stack.peek() ), closer ) )
        {
            stack.pop();
        }
        else if( !errorQ.isEmpty() && matches( bracketOf( errorQ.peek() ), closer ) )
        {
            // Kitty's algorithm ignores this pair, but the abandoned opener and this closer are both mistakes.
            report( errorQ.dequeue() );
            report( item );
        }
        else if( stack.isEmpty() )
        {
            errorQ.enqueue( item );
        }
        else if( stackHasOpener( openerFor( closer ) ) )
        {
            // Everything above the partner was left unclosed, so each is an error.
            while( !matches( bracketOf( stack.peek() ), closer ) )
            {
                errorQ.enqueue( stack.pop() );
            }
            stack.pop();
        }
        else
        {
            extrasQ.enqueue( item );
        }
    }

    /**
     * Checks whether an opening bracket of the given kind is anywhere on the stack.
     *
     * @param opener  the opening bracket to look for
     * @return {@code true} if the stack holds at least one such bracket
     */
    private boolean stackHasOpener( char opener )
    {
        Iterator it = stack.iterator();
        while( it.hasNext() )
        {
            if( bracketOf( it.next() ) == opener )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Reports every element of a queue as an error, emptying the queue.
     *
     * @param queue  the queue to empty
     * @throws EmptyQueueException  if the queue is unexpectedly empty
     */
    private void drain( MyQueue queue ) throws EmptyQueueException
    {
        while( !queue.isEmpty() )
        {
            report( queue.dequeue() );
        }
    }

    /**
     * Records an encoded bracket as an error.
     *
     * @param item  the encoded {@code bracket:line} String
     */
    private void report( String item )
    {
        reported.add( item );
    }

    /**
     * Converts the recorded errors to {@link ParseError} objects sorted by line number.
     * <p>
     * An insertion sort is used, which keeps errors on the same line in the
     * order they were reported.
     * </p>
     *
     * @return the sorted errors
     */
    private ParseError[] sortedErrors()
    {
        ParseError[] errors = new ParseError[reported.size()];

        for( int i = 0; i < errors.length; i++ )
        {
            String item = reported.get( i );
            ParseError next = new ParseError( bracketOf( item ), lineOf( item ) );

            int j = i - 1;
            while( j >= 0 && errors[j].getLineNumber() > next.getLineNumber() )
            {
                errors[j + 1] = errors[j];
                j--;
            }
            errors[j + 1] = next;
        }
        return errors;
    }

    /**
     * Combines a bracket and its line number into one String.
     *
     * @param bracket     the bracket character
     * @param lineNumber  the line number
     * @return the encoded {@code bracket:line} String
     */
    private String encode( char bracket, int lineNumber )
    {
        return bracket + String.valueOf( SEPARATOR ) + lineNumber;
    }

    /**
     * Extracts the bracket from an encoded String.
     *
     * @param item  an encoded {@code bracket:line} String
     * @return the bracket character
     */
    private char bracketOf( String item )
    {
        return item.charAt( 0 );
    }

    /**
     * Extracts the line number from an encoded String.
     *
     * @param item  an encoded {@code bracket:line} String
     * @return the line number
     */
    private int lineOf( String item )
    {
        return Integer.parseInt( item.substring( 2 ) );
    }

    /**
     * Checks whether a character is a round, curly or square opening bracket.
     * Angle brackets are handled separately because they can be comparisons.
     *
     * @param c  the character to test
     * @return {@code true} if it is an opening round, curly or square bracket
     */
    private boolean isOpening( char c )
    {
        return c == '(' || c == '{' || c == '[';
    }

    /**
     * Checks whether a character is a round, curly or square closing bracket.
     * Angle brackets are handled separately because they can be comparisons.
     *
     * @param c  the character to test
     * @return {@code true} if it is a closing round, curly or square bracket
     */
    private boolean isClosing( char c )
    {
        return c == ')' || c == '}' || c == ']';
    }

    /**
     * Works out which {@code <} and {@code >} characters on a line are angle
     * brackets and which are comparisons, shifts or arrows.
     * <p>
     * A {@code <} is an angle bracket when it is not part of an operator such
     * as {@code <=} or {@code <<}, and either the line is an
     * {@code #include} line or a matching {@code >} follows it with only
     * name-like characters in between (so {@code <stdio.h>} and
     * {@code List<String>} count, but {@code i<n;} and {@code a<b && c>d} do
     * not). A {@code >} is an angle bracket when it closes one of those
     * openers, or when it stands on its own at the end of a name (for example
     * a stray {@code >} after {@code stdio.h}).
     * </p>
     *
     * @param line  the line to classify
     * @return an array with one entry per character, {@code true} where the
     *         character is an angle bracket
     */
    private boolean[] classifyAngles( String line )
    {
        boolean[] result = new boolean[line.length()];
        boolean includeLine = line.trim().startsWith( "#include" );
        int depth = 0;

        for( int i = 0; i < line.length(); i++ )
        {
            char c = line.charAt( i );

            if( c == '<' && !isOperator( line, i ) && ( includeLine || hasPartner( line, i ) ) )
            {
                result[i] = true;
                depth++;
            }
            else if( c == '>' && !isOperator( line, i ) )
            {
                if( depth > 0 )
                {
                    result[i] = true;
                    depth--;
                }
                else if( isStandaloneCloser( line, i ) )
                {
                    result[i] = true;
                }
            }
        }
        return result;
    }

    /**
     * Checks whether a {@code <} or {@code >} is part of an operator or has
     * spaces on both sides, which makes it a comparison rather than a bracket.
     * <p>
     * Covers {@code <=}, {@code >=}, {@code <<}, {@code ->}, {@code =>} and
     * comparisons such as {@code a < b}.
     * </p>
     *
     * @param line   the line containing the character
     * @param index  the position of the {@code <} or {@code >}
     * @return {@code true} if the character is an operator, not a bracket
     */
    private boolean isOperator( String line, int index )
    {
        char c = line.charAt( index );
        char prev = index > 0 ? line.charAt( index - 1 ) : ' ';
        char next = index < line.length() - 1 ? line.charAt( index + 1 ) : ' ';

        if( next == '=' )
        {
            return true;
        }
        if( c == '<' && ( next == '<' || prev == '<' ) )
        {
            return true;
        }
        if( c == '>' && ( prev == '-' || prev == '=' ) )
        {
            return true;
        }
        return Character.isWhitespace( prev ) && Character.isWhitespace( next );
    }

    /**
     * Checks whether a {@code <} has a {@code >} after it on the same line
     * with only name-like characters (letters, digits and a few symbols such
     * as {@code . , : * / -}) in between.
     *
     * @param line   the line containing the {@code <}
     * @param start  the position of the {@code <}
     * @return {@code true} if a partner {@code >} follows
     */
    private boolean hasPartner( String line, int start )
    {
        if( start + 1 >= line.length() || Character.isWhitespace( line.charAt( start + 1 ) ) )
        {
            return false;
        }

        for( int j = start + 1; j < line.length(); j++ )
        {
            char ch = line.charAt( j );

            if( ch == '>' )
            {
                return j > start + 1;
            }
            if( !( Character.isLetterOrDigit( ch ) || " _.,:*/-<[]".indexOf( ch ) >= 0 ) )
            {
                return false;
            }
        }
        return false;
    }

    /**
     * Checks whether a {@code >} that has no opener on its line should still
     * count as a (stray) closing angle bracket.
     * <p>
     * It does when it directly follows a non-space character and is followed
     * (after any further {@code >} characters) by the end of the line or
     * punctuation, as in {@code stdio.h>}. It does not when an operand
     * follows, as in {@code a>b} or the shift {@code a>>1}.
     * </p>
     *
     * @param line   the line containing the {@code >}
     * @param index  the position of the {@code >}
     * @return {@code true} if the {@code >} should be treated as a bracket
     */
    private boolean isStandaloneCloser( String line, int index )
    {
        if( index == 0 || Character.isWhitespace( line.charAt( index - 1 ) ) )
        {
            return false;
        }

        int after = index + 1;
        while( after < line.length() && line.charAt( after ) == '>' )
        {
            after++;
        }
        return after == line.length() || ";,)]}:{*&".indexOf( line.charAt( after ) ) >= 0;
    }

    /**
     * Returns the opening bracket that pairs with a closing bracket.
     *
     * @param closer  a closing bracket
     * @return the matching opening bracket
     */
    private char openerFor( char closer )
    {
        switch( closer )
        {
            case ')': return '(';
            case '}': return '{';
            case ']': return '[';
            default:  return '<';
        }
    }

    /**
     * Checks whether an opening bracket and a closing bracket form a pair.
     *
     * @param opener  the first bracket, expected to be an opening bracket
     * @param closer  the second bracket, expected to be a closing bracket
     * @return {@code true} if {@code closer} closes {@code opener}
     */
    private boolean matches( char opener, char closer )
    {
        return ( isClosing( closer ) || closer == '>' ) && openerFor( closer ) == opener;
    }
}
