package GW2Navi;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JWindow;

/**
 * Struct-like object for holding spatial variables of a frame.
 */
public class WindowPreset {
	
	protected static final String DELIMITER = ",";
	public static int WidthDefault = 360;
	public static int HeightDefault = 580;
	public static int PosXDefault = 32;
	public static int PosYDefault = 32;
	public int Width;
	public int Height;
	public int PosX;
	public int PosY;
	
	/**
	 * Converts a preset string of values into this object for use in loading
	 * a window's spatial configuration.
	 * @param pPreset to parse.
	 */
	public WindowPreset(String pPreset)
	{
		String[] preset = pPreset.split(DELIMITER);
		int width;
		int height;
		int posx;
		int posy;
		
		try { width = Integer.parseInt(preset[0]); }
		catch (NumberFormatException|ArrayIndexOutOfBoundsException ex) { width = WidthDefault; }
		
		try { height = Integer.parseInt(preset[1]); }
		catch (NumberFormatException|ArrayIndexOutOfBoundsException ex) { height = HeightDefault; }
		
		try { posx = Integer.parseInt(preset[2]); }
		catch (NumberFormatException|ArrayIndexOutOfBoundsException ex) { posx = PosXDefault; }
		
		try { posy = Integer.parseInt(preset[3]); }
		catch (NumberFormatException|ArrayIndexOutOfBoundsException ex) { posy = PosYDefault; }

		Width = width;
		Height = height;
		PosX = posx;
		PosY = posy;
	}
	
	/**
	 * Constructs a Dimension object from a "width,height" string.
	 * @param pDimension to parse.
	 * @return Dimension
	 */
	public static Dimension parseDimension(String pDimension)
	{
		String[] dimension = pDimension.split(DELIMITER);
		int width;
		int height;
		
		try { width = Integer.parseInt(dimension[0]); }
		catch (NumberFormatException|ArrayIndexOutOfBoundsException ex) { width = WidthDefault; }
		
		try { height = Integer.parseInt(dimension[1]); }
		catch (NumberFormatException|ArrayIndexOutOfBoundsException ex) { height = HeightDefault; }
		
		return new Dimension(width, height);
	}
	
	/**
	 * Loads a WindowPreset into a frame.
	 * @param pFrame overlay frame or projection.
	 * @param pPreset to use.
	 */
	public static void loadWindowPreset(JFrame pFrame, WindowPreset pPreset)
	{
		pFrame.setSize(pPreset.Width, pPreset.Height);
		pFrame.setLocation(pPreset.PosX, pPreset.PosY);
	}
	
	/**
	 * Constructs this object directly from a JFrame.
	 * @param pFrame to extract information.
	 */
	public WindowPreset(JFrame pFrame)
	{
		Width = pFrame.getWidth();
		Height = pFrame.getHeight();
		PosX = pFrame.getX();
		PosY = pFrame.getY();
	}
	public WindowPreset(JWindow pWindow)
	{
		Width = pWindow.getWidth();
		Height = pWindow.getHeight();
		PosX = pWindow.getX();
		PosY = pWindow.getY();
	}
	
	/**
	 * Converts integer values of a preset to a delimited string.
	 * @param pWidth
	 * @param pHeight
	 * @param pPosX
	 * @param pPosY 
	 * @return String to write in options.
	 */
	public static String getString(int pWidth, int pHeight, int pPosX, int pPosY)
	{
		return Integer.toString(pWidth)
			+ DELIMITER + Integer.toString(pHeight)
			+ DELIMITER + Integer.toString(pPosX)
			+ DELIMITER + Integer.toString(pPosY);
	}
	
	/**
	 * Gets the option string representation of the WindowPreset object.
	 * @return string.
	 */
	@Override
	public String toString()
	{
		return Integer.toString(Width)
			+ DELIMITER + Integer.toString(Height)
			+ DELIMITER + Integer.toString(PosX)
			+ DELIMITER + Integer.toString(PosY);
	}
}
