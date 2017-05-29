package GW2Navi;

/**
 * Navi.java GUI controller and overlay frame for displaying gw2timer.com.
 * Originally used DJ Native Swing by Christopher Deckers, and replaced with
 * Chromium Embedded Framework by Marshall Greenblatt. Original JCEF port thanks
 * to Farly Fitrian Dwiputra. Resizable frame code from post by Iovcev Elena.
 * Inline citations provided for copy and pasted snippets.
 * 
 * Libraries/APIs/Assets used:
 * java-cef for embedding browser in program.
 * ini4j for reading and writing user option text files.
 * Java Native Access for reading MumbleLink memory data used by GPS.
 * Crystal Clear icon set by Everaldo Coelho.
 * 
 * See variable declarations for program version.
 */

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.ini4j.Ini;

public final class Navi extends JPanel {
	
	// Meta
	boolean isDebug = false;
	final static String PROGRAM_VERSION = "2017.05.29";
	final static String PROGRAM_NAME = "GW2Navi";
	final static String PROGRAM_NAME_PROJECTION = "GW2Navi 3D";
	final static String PROGRAM_NAME_CURSOR = "GW2Navi Cursor";
	static String DIRECTORY_ICONS = "img/"; // Image folder inside package
	static String DIRECTORY_ICONS_CUSTOM = "bin/cursors/";
	static String FILENAME_OPTIONS = "options.ini";
	static String FILENAME_OPTIONS_DEBUG = "options-debug.ini";
	static String FILENAME_TRANSLATIONS = "translations.ini";
	static String FILENAME_BOOKMARKS = "bookmarks.txt";
	static String EXTENSION_IMAGES = ".png";
	static String DIRECTORY_CURRENT = "";
	static String DIRECTORY_CUSTOM = "";
	
	// GUI measurements and initial states
	WindowPreset currentWindowPreset;
	Point PROJECTION_DEFAULT_LOCATION = new Point(0, 0);
	int ADD_VERTICAL_PIXELS;
	int ADD_HORIZONTAL_PIXELS;
	int BORDER_THICKNESS_TOTAL;
	int RESOLUTION_WIDTH;
	int RESOLUTION_HEIGHT;
	int RESOLUTION_CENTER_X;
	int RESOLUTION_CENTER_Y;
	int NAVBAR_HEIGHT_CURRENT = 0; // Will be assigned 0 if not shown
	static Color COLOR_BAR_CURRENT;
	int OPACITY_LEVELS_10 = 10;
	float OPACITY_STEP = 0.10f;
	float TRANSPARENCY_MIN = 0.90f; // Highest level of opacity that is still transparent
	boolean isProjection = false;
	boolean isBarVisible = true;
	boolean isClickable = true;
	boolean isMiniaturized = false;
	boolean isGPSStarted = false;
	boolean isCursorStarted = false;
	boolean isExited = false;
	
	// Files
	protected Translation TheTranslations;
	protected Option TheOptions;
	protected Bookmark TheBookmarks;

	// Components
	protected ResizableFrame TheFrame; // Framed window with bar and resizable borders for 2D
	protected JFrame TheProjection; // Fullscreen see-through projection window for 3D
	protected ProjectionKnob TheKnob; // Draggable menu button for projection
	protected JPanel TheContainer; // Container for framed window
	protected JPanel TheBar; // The draggable top bar of the framed window that contains the menu
	protected JPopupMenu TheBarPopup; // The context menu that pops up from the bar after right clicking
	protected JPopupMenu TheKnobPopup; // The context menu that pops up from the knob after right clicking
	protected SystemTray TheTray; // Taskbar tray object
	protected TrayIcon TheTrayIcon; // Icon for tray
	protected static ClassLoader TheClassLoader; // Helper class to load images
	protected GPS TheGPS; // GPS memory reading class
	protected VisibleCursor TheVisibleCursor; // High visibility cursor class
	
	// Browser
	BrowserWrapper TheBrowserWrapper;
	CefBrowser TheBrowser;
	StringBuilder TheConsoleLog;
	
	// Messages
	final String TEXT_FILELOADWARNING = " was unable to be loaded!";
	final String TEXT_FILESAVEWARNING = " was unable to be saved!";
	final String PROGRAM_BRIEF = PROGRAM_NAME + " browser-overlay for Guild Wars 2 by GW2Timer.com<br />"
		+ "Version: <b>" + PROGRAM_VERSION + "</b> - Released: 2014.07.16 - Created: 2014.06.01<br />";
	final String TEXT_ABOUT = "<html>"
		+ PROGRAM_BRIEF
		+ "<br />"
		+ "<h2>Shortcuts:</h2>"
		+ "Left click the &quot;_&quot; icon to <b>minimize</b>, right click it to <b>minimize to tray</b>.<br />"
		+ "Left click the &quot;G&quot; icon to <b>miniaturize</b>, right click it to enable <b>clickthrough</b>.<br />"
		+ "Alt+Tab/Alt+Shift+Tab to " + PROGRAM_NAME + " or click on " + PROGRAM_NAME + " on the taskbar to disable clickthrough.<br />"
		+ "Double click the overlay window bar to <b>maximize</b>. Move cursor over edges to <b>resize</b>.<br />"
		+ "Press F5 in the browser to <b>reload</b> the website.<br />"
		+ "<br />"
		+ "<h2>Appearance:</h2>"
		+ "Right click the overlay window bar to open the popup context menu.<br />"
		+ "With the context menu visible, keypress (a letter then a number):<br />"
		+ "S + # = load a Size preset.<br />"
		+ "C + # = load a Color preset.<br />"
		+ "# = load an Opacity value.<br />"
	+ "</html>";
	final String TEXT_ABOUT_PROJECTION = "<html>"
		+ PROGRAM_BRIEF
		+ "<br />"
		+ "<h2>Shortcuts:</h2>"
		+ "Left click the &quot;G&quot; icon (called &quot;knob&quot;) to <b>toggle</b> the overlay...<br />"
		+ "1st click: enable <b>clickthrough</b> so clicks are on the game rather than the overlay.<br />"
		+ "2nd click: <b>hide</b> the overlay.<br />"
		+ "3nd click: <b>show</b> the overlay and disable clickthrough (reset).<br />"
		+ "<br />"
		+ "Right click the knob for the <b>overlay menu</b>.<br />"
		+ "Drag the knob to move it to an accessible and unintrusive place on screen.<br />"
		+ "Hold Shift + Drag the knob to <b>move</b> the projection.<br />"
		+ "Hold Ctrl + Drag the knob to <b>resize</b> the projection.<br />"
		+ "Hold Alt + LeftClick the knob to toggle <b>maximized/windowed</b>.<br />"
		+ "Hold Alt + RightClick the knob to <b>minimize</b>.<br />"
		+ "<br />"
		+ "<h2>Projection Usage:</h2>"
		+ "The projection is the website layer over the game layer.<br />"
		+ "Clicking on any website element will focus the website and unfocus the game.<br />"
		+ "To zoom the map, click on any map element then use the scroll wheel.<br />"
		+ "Dragging a map element will drag the whole map also.<br />"
		+ "Use the display filter on the website to hide unwanted map elements.<br />"
	+ "</html>";

	// Constructor
	public Navi(boolean pIsProjection) throws InterruptedException
	{
		super(new BorderLayout());
		isProjection = pIsProjection;
		
		// Changes for debugging
		if (isDebug)
		{
			FILENAME_OPTIONS = FILENAME_OPTIONS_DEBUG;
		}
		
		// Load options and data first before doing anything
		loadStorage();
		TheConsoleLog = new StringBuilder();
		addLog("Below are browser script errors and other console messages, if available.");
		
		// Set to Window's native appearance instead of standard Java GUI
		if (TheOptions.wantNativeInterface)
		{
			try
			{
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
		}
		
		// Get the top level directory of the program for finding files
		String path = new File("X").getAbsolutePath();
		if (path.length() > 1)
		{
			DIRECTORY_CURRENT = path.substring(0, path.length() - 1); // Remove the dummy "X" at the end
			DIRECTORY_CUSTOM = DIRECTORY_CURRENT.replace("\\", "/");
		}
		TheClassLoader = this.getClass().getClassLoader();
		
		// Initialize bounds
		Dimension screenUnbounded = Toolkit.getDefaultToolkit().getScreenSize(); // Fullscreen
		RESOLUTION_WIDTH = screenUnbounded.width;
		RESOLUTION_HEIGHT = screenUnbounded.height;
		RESOLUTION_CENTER_X = RESOLUTION_WIDTH / 2;
		RESOLUTION_CENTER_Y = RESOLUTION_HEIGHT / 2;
		
		// Initialize browser
		TheBrowserWrapper = new BrowserWrapper(this, isProjection);
		TheBrowser = TheBrowserWrapper.getBrowser();
		
		// The overlay may be run as two styles: window or projection
		if (isProjection)
		{
			/**
			 * 3D: Will be a fullscreen browser that shows the website opaquely
			 * while allowing whichever part that has the CSS background
			 * transparent to be seethrough and clickthrough as if the website
			 * is not there.
			 */
			createProjection();
		}
		else
		{
			/**
			 * 2D: Will be a regular browser window that stays on top of other
			 * windows such as the game window.
			 */
			createFrame();
		}
		// Create menu elements
		Menu menu = new Menu(this, isProjection);
		
		// Start supplementary functions
		toggleGPS(TheOptions.wantGPS);
		
		/**
		 * This variable is a way to tell if multiple instances of the overlay is
		 * running. It is false when any instances run, and true when any instance
		 * exits. A potential problem is if the user starts two instances, closes
		 * one, and runs another: that instance would think it is alone.
		 */
		if (TheOptions.wantSingleInstance)
		{
			TheOptions.set_wantSingleInstance(false);
			saveOptionsFile();
			
			// Start features that should exist singly, regardless of how many overlays are running
			toggleVisibleCursor(TheOptions.wantVisibleCursor);
		}
	}
	

	/**
	 * Reads the options and other text files and initializes them.
	 * Must be in this order.
	 */
	protected void loadStorage()
	{
		try
		{
			TheOptions = new Option(new Ini(new File(FILENAME_OPTIONS)));
		}
		catch (IOException ex)
		{
			displayErrorLoad(FILENAME_OPTIONS);
		}
		
		try
		{
			TheTranslations = new Translation(
				new Ini(new File(FILENAME_TRANSLATIONS)),
				TheOptions.LANGUAGE);
		}
		catch (IOException ex)
		{
			displayErrorLoad(FILENAME_TRANSLATIONS);
		}
		
		try
		{
			TheBookmarks = new Bookmark(FILENAME_BOOKMARKS);
		}
		catch (IOException ex)
		{
			displayErrorLoad(FILENAME_BOOKMARKS);
		}
	}
	
	/**
	 * Enables or disables the GPS class.
	 * @param pEnable or not.
	 */
	protected void toggleGPS(boolean pEnable)
	{
		if (pEnable)
		{
			if (isGPSStarted == false)
			{
				TheGPS = new GPS(this);
				Thread gpsThread = new Thread(TheGPS);
				gpsThread.start();
				isGPSStarted = true;
			}
		}
		else
		{
			if (isGPSStarted)
			{
				TheGPS.kill();
				TheGPS = null;
				isGPSStarted = false;
				TheBrowser.reload();
			}
		}
	}
	
	/**
	 * Enables or disables the VisibleCursor class.
	 * @param pEnable or not.
	 */
	protected void toggleVisibleCursor(boolean pEnable)
	{
		if (pEnable)
		{
			if (isCursorStarted == false)
			{
				TheVisibleCursor = new VisibleCursor(TheOptions);
				Thread cursorThread = new Thread(TheVisibleCursor);
				cursorThread.start();
				isCursorStarted = true;
			}
		}
		else
		{
			if (isCursorStarted)
			{
				TheVisibleCursor.kill();
				TheVisibleCursor = null;
				isCursorStarted = false;
			}
		}
	}
	protected void toggleVisibleCursor()
	{
		toggleVisibleCursor(false);
		toggleVisibleCursor(true);
	}
	
	/**
	 * Initializes a standalone button element that acts as the overlay controller
	 * for projection mode.
	 */
	protected void createKnob()
	{
		TheKnob = new ProjectionKnob(this);
	}
	
	/**
	 * Initializes a borderless see-through browser window.
	 */
	protected void createProjection()
	{
		// Container
		TheProjection = new JFrame();
		TheProjection.setTitle(PROGRAM_NAME_PROJECTION);
		TheProjection.setIconImage(getIcon("task_projection").getImage());
		TheProjection.add(this, BorderLayout.CENTER);
		
		// Appearance
		TheProjection.setAlwaysOnTop(true);
		TheProjection.setResizable(false);
		TheProjection.setUndecorated(true);
		TheProjection.setBackground(new Color(0, 0, 0, 0)); 
		TheProjection.setOpacity(TheOptions.PROJECTION_OPACITY_FOCUSED);
		TheProjection.setVisible(true);
		maximizeWindow(TheOptions.wantProjectionMaximized);
		
		// Controller for projection
		createKnob();
		
		// Bind projection behavior
		bindWindowFocus();
		bindWindowClose();
	}

	/**
	 * Initializes a bordered semi-transparent browser window.
	 */
	protected void createFrame()
	{
		// Combine some measurements
		sumFrameDimensions();
		
		// Create frame (the window)
		Point initialLocation = new Point(
			TheOptions.WINDOWPRESET_START.PosX,
			TheOptions.WINDOWPRESET_START.PosY);
		Dimension initialDimension = new Dimension(
			TheOptions.WINDOWPRESET_START.Width,
			TheOptions.WINDOWPRESET_START.Height);
		Dimension minimumDimension = new Dimension(
			TheOptions.FRAME_MINIMUM.width + ADD_HORIZONTAL_PIXELS,
			TheOptions.FRAME_MINIMUM.height + ADD_VERTICAL_PIXELS);
		TheFrame = new ResizableFrame(initialDimension, minimumDimension, initialLocation);

		// Additional frame settings
		TheFrame.setTitle(PROGRAM_NAME);
		TheFrame.getContentPane().add(this, BorderLayout.CENTER);
		TheFrame.setAlwaysOnTop(TheOptions.wantAlwaysOnTop);
		TheFrame.setOpacity(TheOptions.OPACITY_UNFOCUSED);
		TheFrame.setVisible(true);

		// Container inside the frame
		TheContainer = (JPanel) TheFrame.getContentPane();
		TheContainer.setBackground(new Color(0, 0, 0, 0));

		// Top bar of the frame, acts as a menu and a place to drag-move the frame
		TheBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
		TheBar.setPreferredSize(new Dimension(
			TheOptions.WINDOWPRESET_START.Width,
			TheOptions.MENUBAR_HEIGHT));
		TheBar.setBackground(TheOptions.COLORPRESET_START.BarUnfocused);
		TheBar.addMouseListener(TheFrame);
		TheBar.addMouseMotionListener(TheFrame);
		TheContainer.add(TheBar, BorderLayout.NORTH);

		// Set program taskbar icon
		TheFrame.setIconImage(getIcon("task_program").getImage());
		
		// Show navbar if chosen before
		toggleFrameNavbar(TheOptions.wantNavbar, true);
		
		// Bind frame behavior
		bindWindowFocus();
		bindWindowClose();
	}
	
	/**
	 * Gets a icon object from its filename.
	 * @param pName filename without the extension.
	 * @return icon.
	 */
	protected static ImageIcon getIcon(String pName)
	{
		return new ImageIcon(TheClassLoader.getResource(DIRECTORY_ICONS + pName + EXTENSION_IMAGES));
	}
	protected static ImageIcon getIcon(String pName, int pWidth, int pHeight)
	{
		ImageIcon imageIcon = new ImageIcon(TheClassLoader.getResource(DIRECTORY_ICONS + pName + EXTENSION_IMAGES));
		ImageIcon resizedIcon = new ImageIcon(imageIcon.getImage().getScaledInstance(pWidth, pHeight, Image.SCALE_SMOOTH));
		return resizedIcon;
	}
	protected static ImageIcon getCustomIcon(String pName)
	{
		return new ImageIcon(DIRECTORY_CUSTOM + DIRECTORY_ICONS_CUSTOM + pName); // Extension must be manually specified
	}
	
	/**
	 * Reassigns reuseable dimension sizes and summands.
	 */
	protected void sumFrameDimensions()
	{
		BORDER_THICKNESS_TOTAL = TheOptions.BORDER_THICKNESS * 2;
		ADD_HORIZONTAL_PIXELS = BORDER_THICKNESS_TOTAL;
		ADD_VERTICAL_PIXELS = TheOptions.MENUBAR_HEIGHT + BORDER_THICKNESS_TOTAL;
	}
	
	/**
	 * Resizes the frame based on current selected border thickness.
	 * @param pNewThickness thickness size in pixels.
	 */
	protected void resizeFrameByThickness(int pNewThickness)
	{
		int adjust = pNewThickness - TheOptions.BORDER_THICKNESS;
		TheFrame.setSize(new Dimension(
			TheFrame.getWidth() + (adjust * 2),
			TheFrame.getHeight() + (adjust * 2))
		);
	}
	
	/**
	 * Moves the frame x and y additional pixels.
	 * @param posX to the right, negative values to the left.
	 * @param posY to the bottom, negative values to the top.
	 */
	protected void nudgeFrame(int posX, int posY)
	{
		TheFrame.setLocation(
			(int) TheFrame.getLocation().getX() + posX,
			(int) TheFrame.getLocation().getY() + posY);
	}
	
	/**
	 * Saves the current frame size and resizes the frame to fit only the bar
	 * icon, or resizes it back to old size if already miniaturized.
	 */
	protected void miniaturizeFrame()
	{
		if (isMiniaturized)
		{
			TheFrame.setSize(currentWindowPreset.Width, currentWindowPreset.Height);
		}
		else
		{
			currentWindowPreset = new WindowPreset(TheFrame);
			TheFrame.setSize(
				TheOptions.MENUBAR_HEIGHT,
				TheOptions.MENUBAR_HEIGHT + BORDER_THICKNESS_TOTAL);
		}
		isMiniaturized = !isMiniaturized;
	}
	
	/**
	 * Shows or hides the program. If hide then its title will not be on the taskbar.
	 * "Window" refers to either the Framed Window or the Projection Window.
	 * @param pWantShow
	 */
	protected void toggleWindow(boolean pWantShow)
	{
		if (isProjection)
		{
			if (pWantShow)
			{
				TheProjection.setVisible(true);
				TheProjection.setState(Frame.NORMAL);
				TheProjection.requestFocus();
				TheKnob.updateKnobAppearance(0);
			}
			else
			{
				TheProjection.setState(Frame.ICONIFIED);
				TheKnob.updateKnobAppearance(2);
			}
		}
		else
		{
			if (pWantShow)
			{
				TheFrame.setVisible(true);
				TheFrame.setState(Frame.NORMAL);
				TheFrame.requestFocus();
			}
			else
			{
				TheFrame.setState(Frame.ICONIFIED);
				if (SystemTray.isSupported())
				{
					TheFrame.setVisible(false);
				}
			}
		}
	}
	protected void toggleWindow()
	{
		if (isProjection)
		{
			if (TheProjection.getState() != Frame.ICONIFIED)
			{
				toggleWindow(false);
			}
			else
			{
				toggleWindow(true);
			}
		}
		else
		{
			if (TheFrame.isVisible())
			{
				toggleWindow(false);
			}
			else
			{
				toggleWindow(true);
			}
		}
	}
	
	/**
	 * Maximizes the program or restores if already maximized.
	 * @param pBoolean true for maximized.
	 */
	public void maximizeWindow(boolean pBoolean)
	{
		if (isProjection)
		{
			if (pBoolean)
			{
				// If switching from windowed to maximized, then save the current windowed dimensions
				if (TheOptions.wantProjectionMaximized == false)
				{
					saveProjectionWindowPreset();
				}
				TheProjection.setLocation(PROJECTION_DEFAULT_LOCATION);
				TheProjection.setSize(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
				TheProjection.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximized will make it overlap the taskbar, which is desired for fullscreen
			}
			else
			{
				WindowPreset.loadWindowPreset(TheProjection, TheOptions.WINDOWPRESET_PROJECTION_START);
			}
			TheOptions.set_wantProjectionMaximized(pBoolean);
		}
		else
		{
			TheFrame.headerDoubleClickResize();
		}
	}
	
	/**
	 * Minimizes the program to the taskbar (not tray).
	 */
	public void minimizeWindow()
	{
		if (isProjection)
		{
			TheKnob.setVisible(false);
		}
		toggleWindow(false);
	}
	
	/**
	 * Shows or hides the frame menu bar.
	 * @param pWantVisible 
	 */
	protected void toggleFrameBar(boolean pWantVisible)
	{
		if (isBarVisible == pWantVisible)
		{
			return;
		}
		isBarVisible = pWantVisible;
		
		TheBar.setVisible(pWantVisible);
		if (pWantVisible)
		{
			// Compensate size for the missing menu bar
			TheFrame.setSize(new Dimension(
				TheFrame.getWidth(),
				TheFrame.getHeight() + TheOptions.MENUBAR_HEIGHT)
			);
			// Compensate position
			nudgeFrame(0, -TheOptions.MENUBAR_HEIGHT);
		}
		else
		{
			TheFrame.setSize(new Dimension(
				TheFrame.getWidth(),
				TheFrame.getHeight() - TheOptions.MENUBAR_HEIGHT)
			);
			nudgeFrame(0, TheOptions.MENUBAR_HEIGHT);
		}
	}
	
	/**
	 * Shows the navigation bar depending on boolean.
	 * @param pWantNavbar to show or hide.
	 * @param pIsInitial whether this function is being run at the start of the program.
	 */
	protected void toggleFrameNavbar(boolean pWantNavbar, boolean pIsInitial)
	{
		TheOptions.set_wantNavbar(pWantNavbar);
		TheBrowserWrapper.TheNavbar.setVisible(TheOptions.wantNavbar);
		NAVBAR_HEIGHT_CURRENT = (TheOptions.wantNavbar) ? TheOptions.NAVBAR_HEIGHT : 0;

		if (pIsInitial == false)
		{
			if (TheOptions.wantNavbar)
			{
				TheFrame.setSize(new Dimension(
					TheFrame.getWidth() + TheOptions.NAVBAR_THICKNESS * 2,
					TheFrame.getHeight() + TheOptions.NAVBAR_HEIGHT)
				);
			}
			else
			{
				TheFrame.setSize(new Dimension(
					TheFrame.getWidth() - TheOptions.NAVBAR_THICKNESS * 2,
					TheFrame.getHeight() - TheOptions.NAVBAR_HEIGHT)
				);
			}
		}
	}
	
	/**
	 * Makes the frame clickthrough, such that the overlay is visible but any
	 * mouse interactions with the window are not possible.
	 * Source by Joe C from: http://stackoverflow.com/questions/11217660/java-making-a-window-click-through-including-text-images
	 * @param pWantClickable to turn on/off.
	 */
	protected void setWindowClickable(boolean pWantClickable)
	{
		// Don't do anything if already in wanted state
		if (isClickable == pWantClickable)
		{
			return;
		}
		isClickable = pWantClickable;
		WinDef.HWND hwnd;
		// Also hides the menu bar if want clickthrough, and resizes the frame to compensate for the menu bar loss/gain
		if (isProjection)
		{
			hwnd = getHWnd(TheProjection);
		}
		else
		{
			boolean isMaximized = TheFrame.isMaximized();
			toggleFrameBar(isClickable);
			if (isMaximized)
			{
				TheFrame.setFullscreen();
			}
			hwnd = getHWnd(TheFrame);
		}
		
		// Documentation for the GetWindowLong function is at https://msdn.microsoft.com/en-us/library/windows/desktop/ms633591.aspx
		int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
		if (pWantClickable)
		{
			// Remove the clickthrough flags
			if (isProjection)
			{
				wl = wl & WinUser.WS_EX_LAYERED;
			}
			else
			{
				wl = wl & WinUser.WS_EX_LAYERED & WinUser.WS_EX_TRANSPARENT;
			}
		}
		else
		{
			if (isProjection == false && TheOptions.OPACITY_UNFOCUSED == 1)
			{
				// Clickthrough only works if the window is transparent (not 100% opaque)
				TheOptions.set_OPACITY_UNFOCUSED(TRANSPARENCY_MIN);
				TheFrame.setOpacity(TheOptions.OPACITY_UNFOCUSED);
			}
			// The flags to make the window clickthrough
			wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
		}
		// Documentation for the GWL_EXSTYLE value is at https://msdn.microsoft.com/en-us/library/windows/desktop/ff700543.aspx
		User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
	}
	protected void toggleClickable()
	{
		setWindowClickable(!isClickable);
	}
	
	/**
	 * Get the window handle from the OS.
	 * @param pComponent
	 * @return hwnd
	 */
	protected static HWND getHWnd(Component pComponent)
	{
		HWND hwnd = new HWND();
		hwnd.setPointer(Native.getComponentPointer(pComponent));
		return hwnd;
	}

	/**
	 * Detects frame's focus state to do the visual changes.
	 */
	protected void bindWindowFocus()
	{
		if (isProjection)
		{
			TheProjection.addWindowFocusListener(new WindowFocusListener()
			{
				@Override
				public void windowGainedFocus(WindowEvent e)
				{
					doProjectionFocus(true);
				}
				@Override
				public void windowLostFocus(WindowEvent e)
				{
					doProjectionFocus(false);
				}
			});
		}
		else
		{
			TheFrame.addWindowFocusListener(new WindowFocusListener()
			{
				@Override
				public void windowGainedFocus(WindowEvent e)
				{
					doFrameFocus();
				}
				@Override
				public void windowLostFocus(WindowEvent e)
				{
					doFrameFocus();
				}
			});
		}
	}
	
	/**
	 * Changes the visuals of the frame depending focus state.
	 */
	protected void doProjectionFocus(boolean pState)
	{
		if (TheProjection.isFocused())
		{
			// Make the window clickable if focused (by using the taskbar)
			setWindowClickable(true);
			TheKnob.updateKnobAppearance(0);
			// Reapply opacity
			if (TheOptions.wantProjectionOpacityOnFocus)
			{
				TheProjection.setOpacity(TheOptions.PROJECTION_OPACITY_FOCUSED);
			}
			else
			{
				TheProjection.setOpacity(TheOptions.PROJECTION_OPACITY_UNFOCUSED);
			}
		}
		else
		{
			if (TheProjection.getState() == Frame.ICONIFIED)
			{
				TheKnob.updateKnobAppearance(2);
			}
			// Reapply opacity
			if (TheOptions.wantProjectionOpacityOnFocus)
			{
				TheProjection.setOpacity(TheOptions.PROJECTION_OPACITY_UNFOCUSED);
			}
			else
			{
				TheProjection.setOpacity(TheOptions.PROJECTION_OPACITY_UNFOCUSED);
			}
		}
		if (pState)
		{
			TheKnob.setVisible(true);
			TheKnob.setAlwaysOnTop(true);
		}
	}
	
	/**
	 * Changes the visuals of the frame depending focus state.
	 */
	protected void doFrameFocus()
	{
		if (TheFrame.isFocused())
		{
			// Make the window clickable if focused (by using the taskbar)
			setWindowClickable(true);
			// Restyle the bar
			COLOR_BAR_CURRENT = TheOptions.COLORPRESET_START.BarFocused;
			TheBar.setBackground(TheOptions.COLORPRESET_START.BarFocused);
			// Reapply opacity
			if (TheOptions.wantOpacityOnFocus)
			{
				TheFrame.setOpacity(TheOptions.OPACITY_FOCUSED);
			}
			else
			{
				TheFrame.setOpacity(TheOptions.OPACITY_UNFOCUSED);
			}

			// Restyle the border
			LineBorder panelBorder = new LineBorder(
				TheOptions.COLORPRESET_START.BorderFocused, TheOptions.BORDER_THICKNESS);
			TheContainer.setBorder(panelBorder);
		}
		else
		{
			// Restyle the bar
			COLOR_BAR_CURRENT = TheOptions.COLORPRESET_START.BarUnfocused;
			TheBar.setBackground(TheOptions.COLORPRESET_START.BarUnfocused);
			// Reapply opacity
			if (TheOptions.wantOpacityOnFocus)
			{
				TheFrame.setOpacity(TheOptions.OPACITY_UNFOCUSED);
			}
			else
			{
				TheFrame.setOpacity(TheOptions.OPACITY_UNFOCUSED);
			}

			// Restyle the border
			LineBorder panelBorder = new LineBorder(
				TheOptions.COLORPRESET_START.BorderUnfocused, TheOptions.BORDER_THICKNESS);
			TheContainer.setBorder(panelBorder);
		}
	}

	/**
	 * Does options saving before exiting when user exits the program.
	 */
	protected void bindWindowClose()
	{
		if (isProjection)
		{
			TheProjection.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			TheProjection.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					doExit();
				}
			});
		}
		else
		{
			TheFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			TheFrame.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					doExit();
				}
			});
		}
	}
	
	/**
	 * Sanitizes and saves the WindowPreset for elements that use it.
	 */
	protected void saveKnobWindowPreset()
	{
		// Make sure knob is not outside of screen
		Point knobPoint = TheKnob.getLocation();
		int knobPosX = (knobPoint.x > 0 && knobPoint.x < RESOLUTION_WIDTH) ? knobPoint.x : 0;
		int knobPosY = (knobPoint.y > 0 && knobPoint.y < RESOLUTION_HEIGHT) ? knobPoint.y : 0;
		TheOptions.set_WINDOWPRESET_KNOB(TheOptions.WINDOWPRESET_KNOB.Width, TheOptions.WINDOWPRESET_KNOB.Height, knobPosX, knobPosY);
	}
	protected void saveProjectionWindowPreset()
	{
		// Make sure dimension is not zero or negative
		int width = (TheProjection.getWidth() > TheOptions.PROJECTION_MINIMUM.width) ? TheProjection.getWidth() : TheOptions.PROJECTION_MINIMUM.width;
		int height = (TheProjection.getHeight() > TheOptions.PROJECTION_MINIMUM.height) ? TheProjection.getHeight() : TheOptions.PROJECTION_MINIMUM.height;
		// Make sure position is not outside of screen
		Point point = TheProjection.getLocation();
		int posX = (point.x > 0 && point.x < RESOLUTION_WIDTH) ? point.x : 0;
		int posY = (point.y > 0 && point.y < RESOLUTION_HEIGHT) ? point.y : 0;
		TheOptions.set_WINDOWPRESET_PROJECTION_START(width, height, posX, posY);
	}
	protected void saveFrameWindowPreset()
	{
		// Make sure dimension is not zero or negative
		int width = (TheFrame.getWidth() > TheOptions.FRAME_MINIMUM.width) ? TheFrame.getWidth() : TheOptions.FRAME_MINIMUM.width;
		int height = (TheFrame.getHeight() > TheOptions.FRAME_MINIMUM.height) ? TheFrame.getHeight() : TheOptions.FRAME_MINIMUM.height;
		// Make sure position is not outside of screen
		Point point = TheFrame.getLocation();
		int posX = (point.x > 0 && point.x < RESOLUTION_WIDTH) ? point.x : 0;
		int posY = (point.y > 0 && point.y < RESOLUTION_HEIGHT) ? point.y : 0;
		TheOptions.set_WINDOWPRESET_START(width, height, posX, posY);
	}
	
	/**
	 * Adjusts the frame's size and position from a preset.
	 * @param pPreset to read.
	 */
	protected void loadWindowPreset(WindowPreset pPreset)
	{
		if (isProjection)
		{
			WindowPreset.loadWindowPreset(TheProjection, pPreset);
			TheOptions.set_wantProjectionMaximized(false);
			saveProjectionWindowPreset();
		}
		else
		{
			WindowPreset.loadWindowPreset(TheFrame, pPreset);
		}
	}
	
	/**
	 * Saves the frame's size and position to the associated variable and the
	 * options text file.
	 * @param pNumber to select preset.
	 */
	protected void saveWindowPreset(int pNumber)
	{
		if (isProjection)
		{
			WindowPreset preset = new WindowPreset(TheProjection);
			TheOptions.set_WINDOWPRESET_PROJECTION_USER(preset, pNumber);
			TheOptions.set_wantProjectionMaximized(false);
		}
		else
		{
			WindowPreset preset = new WindowPreset(TheFrame);
			TheOptions.set_WINDOWPRESET_USER(preset, pNumber);
		}
	}
	
	/**
	 * Reassigns the color objects from a preset.
	 * @param pPreset to read.
	 */
	protected void loadColorPreset(ColorPreset pPreset)
	{
		TheOptions.COLORPRESET_START = new ColorPreset(pPreset.toString());
		TheOptions.set_COLORPRESET_START();
		
		TheFrame.setFocusable(false);
		TheFrame.setFocusable(true);
		
		// Refresh frame visuals
		doFrameFocus();
	}
	
	/**
	 * Shows standard message for when an option needs the program to be reloaded.
	 */
	protected void doOptionExit()
	{
		JOptionPane.showMessageDialog(TheFrame,
			TheTranslations.get("OptionSelected"),
			TheTranslations.get("Options"),
			JOptionPane.INFORMATION_MESSAGE);
		doExit();
	}
	
	/**
	 * Does actions needed to be done before closing the program.
	 */
	protected void doExit()
	{
		if (isExited == false)
		{
			isExited = true;
			// Stop GPS loop thread
			toggleGPS(false);
			toggleVisibleCursor(false);
			// Save all options
			saveOptions(true);
			// Close the browser and frame
			CefApp.getInstance().dispose();
			if (isProjection)
			{
				TheProjection.dispose();
			}
			else
			{
				TheFrame.dispose();
			}
		}
	}

	/**
	 * Saves all options that could have been changed while using the program
	 * into the options text file.
	 * @param pIsExit if called when program is about to exit.
	 */
	protected void saveOptions(boolean pIsExit)
	{
		if (isProjection)
		{
			// Save knob's location
			saveKnobWindowPreset();
			
			// Save projection's dimensions if windowed
			if (TheOptions.wantProjectionMaximized == false)
			{
				saveProjectionWindowPreset();
			}
			
			// Save zoom level
			TheOptions.set_PROJECTION_ZOOM_LEVEL((float) TheBrowser.getZoomLevel());
		}
		else
		{
			// Current frame size and position
			if (isMiniaturized == false)
			{
				saveFrameWindowPreset();
			}
			
			// Save zoom level
			TheOptions.set_ZOOM_LEVEL((float) TheBrowser.getZoomLevel());
			
			// Save last visited URL
			TheOptions.set_URL_LASTVISITED(TheBrowserWrapper.sanitizeAddress(TheBrowser.getURL()));
		}
		
		// Other options to save if exiting the program
		if (pIsExit)
		{
			// Reset multiple window check
			TheOptions.set_wantSingleInstance(true);
		}
		
		// Save
		saveOptionsFile();
	}
	protected void saveOptionsFile()
	{
		// Save the options file
		try
		{
			TheOptions.File.store();
		}
		catch (IOException ex)
		{
			displayErrorSave(FILENAME_OPTIONS);
		}
	}
	
	/**
	 * Shows an error message for a save/load error.
	 * @param pItemName erroneous item.
	 */
	protected void displayErrorLoad(String pItemName)
	{
		JOptionPane.showMessageDialog(TheFrame,
			pItemName + TEXT_FILELOADWARNING,
			"Warning", JOptionPane.ERROR_MESSAGE);
	}
	protected void displayErrorSave(String pItemName)
	{
		JOptionPane.showMessageDialog(TheFrame,
			pItemName + TEXT_FILESAVEWARNING,
			"Warning", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Adds a log entry line to the string representation of the browser console log.
	 * @param pMessage to add.
	 */
	public void addLog(String pMessage)
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String outputline = "[" + timestamp + "] " + pMessage + "\n";
		TheConsoleLog.append(outputline);
	}
	
	/**
	 * Shows the browser console log.
	 */
	public void showLog()
	{
		JTextArea jta = new JTextArea(TheConsoleLog.toString());
		JScrollPane jsp = new JScrollPane(jta)
		{
			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension(1024, 512);
			}
		};
		JOptionPane.showMessageDialog(TheFrame, jsp, "Browser Console Log", JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Opens another overlay process by executing the known filename of this program.
	 */
	protected void openNewWindow()
	{
		String filepath = Navi.DIRECTORY_CURRENT + ((isProjection) ? TheOptions.URL_PROJECTION_NEW_WINDOW : TheOptions.URL_NEW_WINDOW);
		try
		{
			Process process = Runtime.getRuntime().exec(filepath);
		}
		catch (IOException ex)
		{
			displayErrorLoad(filepath);
		}
	}
}
