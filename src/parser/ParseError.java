package parser;

/**
 * A single bracket error found by the {@link Parser}, for use in CPRG 304
 * at SAIT.
 * <p>
 * Each instance records the bracket character that caused the error and the
 * (one-based) line number of the file where that bracket appears. Instances
 * are immutable, so they can be safely handed to the application that
 * displays them.
 * </p>
 */
public class ParseError
{
    // Attributes

    /** The bracket character that caused this error. */
    private final char bracket;

    /** The one-based line number on which the bracket appears. */
    private final int lineNumber;

    // Constructors

    /**
     * Constructs a new {@link ParseError}.
     *
     * @param bracket     the bracket character that caused the error
     * @param lineNumber  the one-based line number on which the bracket appears
     */
    public ParseError( char bracket, int lineNumber )
    {
        this.bracket = bracket;
        this.lineNumber = lineNumber;
    }

    // Accessors

    /**
     * Returns the bracket character that caused this error.
     *
     * @return the offending bracket
     */
    public char getBracket()
    {
        return bracket;
    }

    /**
     * Returns the line number on which the offending bracket appears.
     *
     * @return the one-based line number
     */
    public int getLineNumber()
    {
        return lineNumber;
    }

    // Object Methods

    /**
     * Returns a console-ready description of this error, for example
     * {@code Line 4: Syntax error on "]".}
     *
     * @return the formatted error message
     */
    @Override
    public String toString()
    {
        return "Line " + lineNumber + ": Syntax error on \"" + bracket + "\".";
    }
}
