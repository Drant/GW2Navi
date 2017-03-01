package GW2Navi;

import static GW2Navi.WindowPreset.DELIMITER;
import java.awt.Color;

/**
 * Struct-like object for holding color variables of a frame.
 */
public class ColorPreset {
	
	protected static final String StringDelimiter = ",";
	public static String BarFocusedDefault = "#777777";
	public static String BarUnfocusedDefault = "#555555";
	public static String BorderFocusedDefault = "#666666";
	public static String BorderUnfocusedDefault = "#444444";
	public Color BarFocused;
	public Color BarUnfocused;
	public Color BorderFocused;
	public Color BorderUnfocused;
	
	/**
	 * Converts a preset string of values into this object for use in loading
	 * a window's spatial configuration.
	 * @param pPreset to parse. Must be four values separated by commas.
	 */
	public ColorPreset(String pPreset)
	{
		String[] preset = pPreset.split(StringDelimiter);
		String barfocused;
		String barunfocused;
		String borderfocused;
		String borderunfocused;
		
		try { barfocused = preset[0]; }
		catch (ArrayIndexOutOfBoundsException ex) { barfocused = BarFocusedDefault; }
		
		try { barunfocused = preset[1]; }
		catch (ArrayIndexOutOfBoundsException ex) { barunfocused = BarFocusedDefault; }
		
		try { borderfocused = preset[2]; }
		catch (ArrayIndexOutOfBoundsException ex) { borderfocused = BarFocusedDefault; }
		
		try { borderunfocused = preset[3]; }
		catch (ArrayIndexOutOfBoundsException ex) { borderunfocused = BarFocusedDefault; }
		
		BarFocused = getColorFromHex(barfocused);
		BarUnfocused = getColorFromHex(barunfocused);
		BorderFocused = getColorFromHex(borderfocused);
		BorderUnfocused = getColorFromHex(borderunfocused);
	}
	
	/**
	 * Converts a #000000 string to a Color object.
	 * @param pString to decode.
	 * @return color.
	 */
	public static Color getColorFromHex(String pString)
	{
		return new Color(Integer.decode(pString));
	}
	
	/**
	 * Converts a Color object to a #000000 hexadecimal color code.
	 * @param pColor to convert.
	 * @return color hexcode.
	 */
	public static String getHexFromColor(Color pColor)
	{
		return "#" + Integer.toHexString(pColor.getRGB()).substring(2);
	}
	
	/**
	 * Gets the option string representation of the ColorPreset object.
	 * @return string.
	 */
	@Override
	public String toString()
	{
		return getHexFromColor(BarFocused)
			+ DELIMITER + getHexFromColor(BarUnfocused)
			+ DELIMITER + getHexFromColor(BorderFocused)
			+ DELIMITER + getHexFromColor(BorderUnfocused);
	}
}
