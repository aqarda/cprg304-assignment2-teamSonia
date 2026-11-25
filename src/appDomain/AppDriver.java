package appDomain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import parser.ParseError;
import parser.Parser;

/**
 * Main class for the bracket parser. It gets the file name from the command
 * line, reads the file line by line, sends each line to the Parser and prints
 * the errors it finds. All the file reading and printing is done here.
 *
 * @author Fernando
 * @version 1.0
 */
public class AppDriver
{
	/** Message that shows how to run the program. */
	private static final String USAGE = "Usage: java -jar Parser.jar <file name>";

	/**
	 * Private constructor so no AppDriver objects get made.
	 */
	private AppDriver()
	{
	}

	/**
	 * Runs the program. Checks the arguments and the file, parses the file and
	 * prints the results. Any problem prints a short message instead of crashing.
	 *
	 * @param args the command line arguments, should be one file name
	 */
	public static void main( String[] args )
	{
		if( args.length == 0 )
		{
			printProblem( "No file name was given." );
			System.out.println( USAGE );
			return;
		}

		if( args.length > 1 )
		{
			printProblem( "Too many arguments. Only one file name is allowed (put the name in quotes if it has spaces)." );
			System.out.println( USAGE );
			return;
		}

		String fileName = args[0].trim();

		if( fileName.isEmpty() )
		{
			printProblem( "The file name is blank." );
			System.out.println( USAGE );
			return;
		}

		File file = new File( fileName );
		String problem = checkFile( file );

		if( problem != null )
		{
			printProblem( problem );
			return;
		}

		try
		{
			ParseError[] errors = parseFile( file );
			printResults( file.getName(), errors );
		}
		catch( FileNotFoundException e )
		{
			printProblem( "The file \"" + file.getName() + "\" could not be opened." );
		}
		catch( IOException e )
		{
			printProblem( "The file \"" + file.getName() + "\" could not be read." );
		}
		catch( RuntimeException e )
		{
			printProblem( "Something went wrong while parsing \"" + file.getName() + "\"." );
		}
	}

	/**
	 * Checks that the file exists, is not a folder, can be read and is not empty.
	 *
	 * @param file the file to check
	 * @return a message saying what is wrong, or null if the file is ok
	 */
	private static String checkFile( File file )
	{
		if( !file.exists() )
		{
			return "The file \"" + file.getPath() + "\" was not found.";
		}

		if( file.isDirectory() )
		{
			return "\"" + file.getPath() + "\" is a folder, not a file.";
		}

		if( !file.isFile() || !file.canRead() )
		{
			return "The file \"" + file.getPath() + "\" cannot be read.";
		}

		if( file.length() == 0 )
		{
			return "The file \"" + file.getPath() + "\" is empty.";
		}

		return null;
	}

	/**
	 * Reads the file one line at a time and gives each line and its line number
	 * to the Parser. Line numbers start at 1.
	 *
	 * @param file the file to parse
	 * @return the errors from the parser, sorted by line number
	 * @throws FileNotFoundException if the file can't be opened
	 * @throws IOException if there is a problem reading the file
	 */
	private static ParseError[] parseFile( File file ) throws FileNotFoundException, IOException
	{
		Parser parser = new Parser();

		try( BufferedReader reader = new BufferedReader( new FileReader( file ) ) )
		{
			String line = reader.readLine();
			int lineNumber = 1;

			while( line != null )
			{
				parser.parseLine( line, lineNumber );
				line = reader.readLine();
				lineNumber++;
			}
		}

		return parser.finish();
	}

	/**
	 * Prints that the file has no errors, or prints each error with its line
	 * number and bracket.
	 *
	 * @param fileName the name of the file that was parsed
	 * @param errors the errors from the parser
	 */
	private static void printResults( String fileName, ParseError[] errors )
	{
		if( errors == null || errors.length == 0 )
		{
			System.out.println( "File: " + fileName + " has no errors." );
			return;
		}

		System.out.println( "Errors in file " + fileName + ":" );

		for( ParseError error : errors )
		{
			System.out.println( formatError( error ) );
		}
	}

	/**
	 * Turns one error into the line that gets printed, like
	 * Line 4: Syntax error on "]".
	 *
	 * @param error the error to format
	 * @return the error as a line of text
	 */
	private static String formatError( ParseError error )
	{
		return "Line " + error.getLineNumber() + ": Syntax error on \"" + error.getBracket() + "\".";
	}

	/**
	 * Prints an error message for bad input.
	 *
	 * @param message the message to print
	 */
	private static void printProblem( String message )
	{
		System.out.println( "Error: " + message );
	}
}