package GW2Navi;

/**
 * Bookmark.java simple file reader and parser of key-value pair bookmark lines
 * in a plain text bookmark file.
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap; // This type of hash map is ordered


public class Bookmark {
	
	public LinkedHashMap<String, String> Book;
	private final String SPECIALSTRING_COMMENT = "//";
	private final String SPECIALSTRING_DELIMITER = "=";
	protected final int NAME_CHAR_LIMIT = 64;
	protected final int ADDRESS_CHAR_LIMIT = 256;
	
	/**
	 * Reads a text file and convert each non-comment line to key value pairs.
	 * @param pFileName of the text file.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Bookmark(String pFileName) throws FileNotFoundException, IOException
	{
		Book = new LinkedHashMap<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(pFileName)))
		{
			String line;

			while ((line = br.readLine()) != null)
			{
				extractPair(line);
			}
			br.close();
		}
	}
	
	/**
	 * Takes a string line and extracts the key value pair storage object.
	 * @param pLine to extract.
	 */
	private void extractPair(String pLine)
	{
		// Don't extract if the line if it starts with a comment character
		if (pLine.contains(SPECIALSTRING_COMMENT)
			&& pLine.substring(0, SPECIALSTRING_COMMENT.length()).equals(SPECIALSTRING_COMMENT))
		{
			return;
		}
		
		String[] entry = halveString(pLine, SPECIALSTRING_DELIMITER);
		if (entry != null)
		{
			Book.put(entry[0], entry[1]);
		}
	}
	
	/**
	 * Splits a singly delimited string in half.
	 * @param pString to halve.
	 * @param pDelimiter to find index to split.
	 * @return two strings array.
	 */
	private String[] halveString(String pString, String pDelimiter)
	{
		int delimiterindex = pString.indexOf(pDelimiter);
		// Null if string is too short or the delimiter isn't in it
		if (pString.length() < 3 || delimiterindex == -1)
		{
			return null;
		}
		
		String[] ret = new String[2];
		ret[0] = pString.substring(0, delimiterindex).trim();
		ret[1] = pString.substring(delimiterindex + 1, pString.length()).trim();
		
		// Truncate the site name and address URL if they are over the character limit
		if (ret[0].length() > NAME_CHAR_LIMIT)
		{
			ret[0] = ret[0].substring(0, NAME_CHAR_LIMIT);
		}
		if (ret[1].length() > ADDRESS_CHAR_LIMIT)
		{
			ret[1] = ret[1].substring(0, ADDRESS_CHAR_LIMIT);
		}
		
		return ret;
	}
}
