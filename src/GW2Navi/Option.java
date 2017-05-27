package GW2Navi;

/**
 * Option.java serves as a mediator for the options text file and the program's
 * actual variables; it contains the exact string keys as in the text file and
 * is responsible for converting the values to the proper type.
 */

import java.awt.Dimension;
import org.ini4j.Ini;


public class Option {
	
	public Ini File;
	Ini.Section urls;
	Ini.Section javascript;
	Ini.Section preferences;
	Ini.Section componentconstants;
	Ini.Section standarddimensions;
	Ini.Section windowpresets;
	Ini.Section colorpresets;
	Ini.Section cursorsettings;
	
	String URL_HOMEPAGE;
	String URL_PROJECTION;
	String URL_LASTVISITED;
	String URL_UPDATE;
	String URL_SEARCH;
	String URL_SITE;
	String URL_LOCAL;
	
	String JS_QUICK_1;
	String JS_QUICK_2;
	String JS_QUICK_3;
	String JS_QUICK_4;
	String JS_QUICK_A;
	String JS_QUICK_B;
	String JS_QUICK_TOGGLE;
	String[] JS_SIZE_USER = new String[8];
	
	String JS_GPS_POSITION;
	String JS_GPS_DIRECTION;
	String JS_GPS_PERSPECTIVE;
	String JS_GPS_CAMERA;
	String JS_GPS_IDENTITY;
	
	String LANGUAGE;
	int BORDER_THICKNESS;
	int ZOOM_STARTUP_DELAY;
	float ZOOM_LEVEL;
	float PROJECTION_ZOOM_LEVEL;
	float OPACITY_FOCUSED;
	float OPACITY_UNFOCUSED;
	float PROJECTION_OPACITY_FOCUSED;
	float PROJECTION_OPACITY_UNFOCUSED;
	boolean wantSingleInstance;
	boolean wantNativeInterface;
	boolean wantOpacityOnFocus;
	boolean wantProjectionOpacityOnFocus;
	boolean wantProjectionMaximized;
	boolean wantAlwaysOnTop;
	boolean wantNavbar;
	boolean wantLastVisited;
	boolean wantKnobMoveable;
	boolean wantKnobBig;
	boolean wantPortable;
	boolean wantGPS;
	int GPS_REFRESH_RATE;
	boolean wantVisibleCursor;
	
	int MENUBAR_HEIGHT;
	int NAVBAR_THICKNESS;
	int NAVBAR_HEIGHT;
	
	Dimension FRAME_MINIMUM;
	Dimension FRAME_QUICK_1;
	Dimension FRAME_QUICK_2;
	Dimension FRAME_QUICK_3;
	Dimension FRAME_QUICK_4;
	Dimension FRAME_QUICK_A;
	Dimension FRAME_QUICK_B;
	Dimension PROJECTION_MINIMUM;
	
	WindowPreset WINDOWPRESET_START;
	WindowPreset[] WINDOWPRESET_USER = new WindowPreset[8];
	WindowPreset WINDOWPRESET_PROJECTION;
	WindowPreset WINDOWPRESET_KNOB;
	WindowPreset WINDOWPRESET_KNOB_BIG;
	ColorPreset COLORPRESET_START;
	ColorPreset[] COLORPRESET_USER = new ColorPreset[4];
	
	int CURSOR_REFRESH_RATE;
	int CURSOR_START;
	String[] CURSOR_USER = new String[8];
	WindowPreset[] CURSORPRESET_USER = new WindowPreset[8];
	
	// Constructor
	public Option(Ini pIni)
	{
		this.File = pIni;
		// INI sections
		urls = File.get("URLs");
		javascript = File.get("JavaScript");
		preferences = File.get("Preferences");
		componentconstants = File.get("ComponentConstants");
		standarddimensions = File.get("StandardDimensions");
		windowpresets = File.get("WindowPresets");
		colorpresets = File.get("ColorPresets");
		cursorsettings = File.get("CursorSettings");
		
		// INI properties, same order as in text file
		URL_HOMEPAGE = urls.get("URL_HOMEPAGE");
		URL_PROJECTION = urls.get("URL_PROJECTION");
		URL_LASTVISITED = urls.get("URL_LASTVISITED");
		URL_UPDATE = urls.get("URL_UPDATE");
		URL_SEARCH = urls.get("URL_SEARCH");
		URL_SITE = urls.get("URL_SITE");
		URL_LOCAL = urls.get("URL_LOCAL");
		
		JS_QUICK_1 = javascript.get("JS_QUICK_1");
		JS_QUICK_2 = javascript.get("JS_QUICK_2");
		JS_QUICK_3 = javascript.get("JS_QUICK_3");
		JS_QUICK_4 = javascript.get("JS_QUICK_4");
		JS_QUICK_A = javascript.get("JS_QUICK_A");
		JS_QUICK_B = javascript.get("JS_QUICK_B");
		JS_QUICK_TOGGLE = javascript.get("JS_QUICK_TOGGLE");
		JS_SIZE_USER[0] = javascript.get("JS_SIZE_USER0");
		JS_SIZE_USER[1] = javascript.get("JS_SIZE_USER1");
		JS_SIZE_USER[2] = javascript.get("JS_SIZE_USER2");
		JS_SIZE_USER[3] = javascript.get("JS_SIZE_USER3");
		JS_SIZE_USER[4] = javascript.get("JS_SIZE_USER4");
		JS_SIZE_USER[5] = javascript.get("JS_SIZE_USER5");
		JS_SIZE_USER[6] = javascript.get("JS_SIZE_USER6");
		JS_SIZE_USER[7] = javascript.get("JS_SIZE_USER7");
		
		JS_GPS_POSITION = javascript.get("JS_GPS_POSITION");
		JS_GPS_DIRECTION = javascript.get("JS_GPS_DIRECTION");
		JS_GPS_PERSPECTIVE = javascript.get("JS_GPS_PERSPECTIVE");
		JS_GPS_CAMERA = javascript.get("JS_GPS_CAMERA");
		JS_GPS_IDENTITY = javascript.get("JS_GPS_IDENTITY");
		
		LANGUAGE = preferences.get("LANGUAGE");
		BORDER_THICKNESS = Integer.parseInt(preferences.get("BORDER_THICKNESS"));
		ZOOM_STARTUP_DELAY = Integer.parseInt(preferences.get("ZOOM_STARTUP_DELAY"));
		ZOOM_LEVEL = Float.parseFloat(preferences.get("ZOOM_LEVEL"));
		PROJECTION_ZOOM_LEVEL = Float.parseFloat(preferences.get("PROJECTION_ZOOM_LEVEL"));
		OPACITY_FOCUSED = Float.parseFloat(preferences.get("OPACITY_FOCUSED"));
		OPACITY_UNFOCUSED = Float.parseFloat(preferences.get("OPACITY_UNFOCUSED"));
		PROJECTION_OPACITY_FOCUSED = Float.parseFloat(preferences.get("PROJECTION_OPACITY_FOCUSED"));
		PROJECTION_OPACITY_UNFOCUSED = Float.parseFloat(preferences.get("PROJECTION_OPACITY_UNFOCUSED"));
		wantSingleInstance = Boolean.parseBoolean(preferences.get("wantSingleInstance"));
		wantNativeInterface = Boolean.parseBoolean(preferences.get("wantNativeInterface"));
		wantOpacityOnFocus = Boolean.parseBoolean(preferences.get("wantOpacityOnFocus"));
		wantProjectionOpacityOnFocus = Boolean.parseBoolean(preferences.get("wantProjectionOpacityOnFocus"));
		wantProjectionMaximized = Boolean.parseBoolean(preferences.get("wantProjectionMaximized"));
		wantAlwaysOnTop = Boolean.parseBoolean(preferences.get("wantAlwaysOnTop"));
		wantNavbar = Boolean.parseBoolean(preferences.get("wantNavbar"));
		wantLastVisited = Boolean.parseBoolean(preferences.get("wantLastVisited"));
		wantKnobMoveable = Boolean.parseBoolean(preferences.get("wantKnobMoveable"));
		wantKnobBig = Boolean.parseBoolean(preferences.get("wantKnobBig"));
		wantPortable = Boolean.parseBoolean(preferences.get("wantPortable"));
		wantGPS = Boolean.parseBoolean(preferences.get("wantGPS"));
		GPS_REFRESH_RATE = Integer.parseInt(preferences.get("GPS_REFRESH_RATE"));
		wantVisibleCursor = Boolean.parseBoolean(preferences.get("wantVisibleCursor"));
		
		MENUBAR_HEIGHT = Integer.parseInt(componentconstants.get("MENUBAR_HEIGHT"));
		NAVBAR_THICKNESS = Integer.parseInt(componentconstants.get("NAVBAR_THICKNESS"));
		NAVBAR_HEIGHT = Integer.parseInt(componentconstants.get("NAVBAR_HEIGHT"));
		
		FRAME_MINIMUM = WindowPreset.parseDimension(standarddimensions.get("FRAME_MINIMUM"));
		FRAME_QUICK_1 = WindowPreset.parseDimension(standarddimensions.get("FRAME_QUICK_1"));
		FRAME_QUICK_2 = WindowPreset.parseDimension(standarddimensions.get("FRAME_QUICK_2"));
		FRAME_QUICK_3 = WindowPreset.parseDimension(standarddimensions.get("FRAME_QUICK_3"));
		FRAME_QUICK_4 = WindowPreset.parseDimension(standarddimensions.get("FRAME_QUICK_4"));
		FRAME_QUICK_A = WindowPreset.parseDimension(standarddimensions.get("FRAME_QUICK_A"));
		FRAME_QUICK_B = WindowPreset.parseDimension(standarddimensions.get("FRAME_QUICK_B"));
		PROJECTION_MINIMUM = WindowPreset.parseDimension(standarddimensions.get("PROJECTION_MINIMUM"));
		
		WINDOWPRESET_START = new WindowPreset(windowpresets.get("WINDOWPRESET_START"));
		WINDOWPRESET_USER[0] = new WindowPreset(windowpresets.get("WINDOWPRESET_USER0"));
		WINDOWPRESET_USER[1] = new WindowPreset(windowpresets.get("WINDOWPRESET_USER1"));
		WINDOWPRESET_USER[2] = new WindowPreset(windowpresets.get("WINDOWPRESET_USER2"));
		WINDOWPRESET_USER[3] = new WindowPreset(windowpresets.get("WINDOWPRESET_USER3"));
		WINDOWPRESET_USER[4] = new WindowPreset(windowpresets.get("WINDOWPRESET_USER4"));
		WINDOWPRESET_USER[5] = new WindowPreset(windowpresets.get("WINDOWPRESET_USER5"));
		WINDOWPRESET_USER[6] = new WindowPreset(windowpresets.get("WINDOWPRESET_USER6"));
		WINDOWPRESET_USER[7] = new WindowPreset(windowpresets.get("WINDOWPRESET_USER7"));
		WINDOWPRESET_PROJECTION = new WindowPreset(windowpresets.get("WINDOWPRESET_PROJECTION"));
		WINDOWPRESET_KNOB = new WindowPreset(windowpresets.get("WINDOWPRESET_KNOB"));
		WINDOWPRESET_KNOB_BIG = new WindowPreset(windowpresets.get("WINDOWPRESET_KNOB_BIG"));
		
		COLORPRESET_START = new ColorPreset(colorpresets.get("COLORPRESET_START"));
		COLORPRESET_USER[0] = new ColorPreset(colorpresets.get("COLORPRESET_USER0"));
		COLORPRESET_USER[1] = new ColorPreset(colorpresets.get("COLORPRESET_USER1"));
		COLORPRESET_USER[2] = new ColorPreset(colorpresets.get("COLORPRESET_USER2"));
		COLORPRESET_USER[3] = new ColorPreset(colorpresets.get("COLORPRESET_USER3"));
		
		CURSOR_REFRESH_RATE = Integer.parseInt(cursorsettings.get("CURSOR_REFRESH_RATE"));
		CURSOR_START = Integer.parseInt(cursorsettings.get("CURSOR_START"));
		CURSOR_USER[0] = cursorsettings.get("CURSOR_USER0");
		CURSOR_USER[1] = cursorsettings.get("CURSOR_USER1");
		CURSOR_USER[2] = cursorsettings.get("CURSOR_USER2");
		CURSOR_USER[3] = cursorsettings.get("CURSOR_USER3");
		CURSOR_USER[4] = cursorsettings.get("CURSOR_USER4");
		CURSOR_USER[5] = cursorsettings.get("CURSOR_USER5");
		CURSOR_USER[6] = cursorsettings.get("CURSOR_USER6");
		CURSOR_USER[7] = cursorsettings.get("CURSOR_USER7");
		CURSORPRESET_USER[0] = new WindowPreset(cursorsettings.get("CURSORPRESET_USER0"));
		CURSORPRESET_USER[1] = new WindowPreset(cursorsettings.get("CURSORPRESET_USER1"));
		CURSORPRESET_USER[2] = new WindowPreset(cursorsettings.get("CURSORPRESET_USER2"));
		CURSORPRESET_USER[3] = new WindowPreset(cursorsettings.get("CURSORPRESET_USER3"));
		CURSORPRESET_USER[4] = new WindowPreset(cursorsettings.get("CURSORPRESET_USER4"));
		CURSORPRESET_USER[5] = new WindowPreset(cursorsettings.get("CURSORPRESET_USER5"));
		CURSORPRESET_USER[6] = new WindowPreset(cursorsettings.get("CURSORPRESET_USER6"));
		CURSORPRESET_USER[7] = new WindowPreset(cursorsettings.get("CURSORPRESET_USER7"));
	}
	
	
	// Methods to update both this object's and the text file's variables.
	// Note that only a few variables are changeable from the program's UI.
	// =========================================================================
	
	public void set_URL_LASTVISITED(String pValue)
	{
		URL_LASTVISITED = pValue;
		urls.put("URL_LASTVISITED", pValue);
	}
	
	public void set_LANGUAGE(String pValue)
	{
		LANGUAGE = pValue;
		preferences.put("LANGUAGE", pValue);
	}
	
	public void set_BORDER_THICKNESS(int pValue)
	{
		BORDER_THICKNESS = pValue;
		preferences.put("BORDER_THICKNESS", pValue);
	}
	
	public void set_ZOOM_LEVEL(float pValue)
	{
		ZOOM_LEVEL = pValue;
		preferences.put("ZOOM_LEVEL", pValue);
	}
	
	public void set_PROJECTION_ZOOM_LEVEL(float pValue)
	{
		PROJECTION_ZOOM_LEVEL = pValue;
		preferences.put("PROJECTION_ZOOM_LEVEL", pValue);
	}
	
	public void set_wantSingleInstance(boolean pValue)
	{
		wantSingleInstance = pValue;
		preferences.put("wantSingleInstance", pValue);
	}
	
	public void set_wantNativeInterface(boolean pValue)
	{
		wantNativeInterface = pValue;
		preferences.put("wantNativeInterface", pValue);
	}
	
	public void set_wantOpacityOnFocus(boolean pValue)
	{
		wantOpacityOnFocus = pValue;
		preferences.put("wantOpacityOnFocus", pValue);
	}
	
	public void set_wantProjectionOpacityOnFocus(boolean pValue)
	{
		wantProjectionOpacityOnFocus = pValue;
		preferences.put("wantProjectionOpacityOnFocus", pValue);
	}
	
	public void set_wantProjectionMaximized(boolean pValue)
	{
		wantProjectionMaximized = pValue;
		preferences.put("wantProjectionMaximized", pValue);
	}
	
	public void set_wantAlwaysOnTop(boolean pValue)
	{
		wantAlwaysOnTop = pValue;
		preferences.put("wantAlwaysOnTop", pValue);
	}
	
	public void set_wantNavbar(boolean pValue)
	{
		wantNavbar = pValue;
		preferences.put("wantNavbar", pValue);
	}
	
	public void set_wantLastVisited(boolean pValue)
	{
		wantLastVisited = pValue;
		preferences.put("wantLastVisited", pValue);
	}
	
	public void set_wantKnobMoveable(boolean pValue)
	{
		wantKnobMoveable = pValue;
		preferences.put("wantKnobMoveable", pValue);
	}
	
	public void set_wantKnobBig(boolean pValue)
	{
		wantKnobBig = pValue;
		preferences.put("wantKnobBig", pValue);
	}
	
	public void set_OPACITY_FOCUSED(float pValue)
	{
		OPACITY_FOCUSED = pValue;
		preferences.put("OPACITY_FOCUSED", pValue);
	}
	
	public void set_OPACITY_UNFOCUSED(float pValue)
	{
		OPACITY_UNFOCUSED = pValue;
		preferences.put("OPACITY_UNFOCUSED", pValue);
	}
	
	public void set_PROJECTION_OPACITY_FOCUSED(float pValue)
	{
		PROJECTION_OPACITY_FOCUSED = pValue;
		preferences.put("PROJECTION_OPACITY_FOCUSED", pValue);
	}
	
	public void set_PROJECTION_OPACITY_UNFOCUSED(float pValue)
	{
		PROJECTION_OPACITY_UNFOCUSED = pValue;
		preferences.put("PROJECTION_OPACITY_UNFOCUSED", pValue);
	}
	
	public void set_WINDOWPRESET_START(int pWidth, int pHeight, int pPosX, int pPosY)
	{
		windowpresets.put("WINDOWPRESET_START", WindowPreset.getString(pWidth, pHeight, pPosX, pPosY));
	}
	
	public void set_WINDOWPRESET_USER(WindowPreset pPreset, int pNumber)
	{
		WINDOWPRESET_USER[pNumber] = pPreset;
		windowpresets.put("WINDOWPRESET_USER" + Integer.toString(pNumber), pPreset.toString());
	}
	
	public void set_WINDOWPRESET_PROJECTION(int pWidth, int pHeight, int pPosX, int pPosY)
	{
		windowpresets.put("WINDOWPRESET_PROJECTION", WindowPreset.getString(pWidth, pHeight, pPosX, pPosY));
	}
	
	public void set_WINDOWPRESET_KNOB(int pWidth, int pHeight, int pPosX, int pPosY)
	{
		windowpresets.put("WINDOWPRESET_KNOB", WindowPreset.getString(pWidth, pHeight, pPosX, pPosY));
	}
	
	public void set_COLORPRESET_START()
	{
		colorpresets.put("COLORPRESET_START", COLORPRESET_START.toString());
	}
	
	public void set_COLORPRESET_USER(int pNumber)
	{
		String preset = COLORPRESET_START.toString();
		COLORPRESET_USER[pNumber] = new ColorPreset(preset);
		colorpresets.put("COLORPRESET_USER" + Integer.toString(pNumber), preset);
	}
	
	public void set_wantGPS(boolean pValue)
	{
		wantGPS = pValue;
		preferences.put("wantGPS", pValue);
	}
	
	public void set_wantVisibleCursor(boolean pValue)
	{
		wantVisibleCursor = pValue;
		preferences.put("wantVisibleCursor", pValue);
	}
	
	public void set_CURSOR_START(int pValue)
	{
		CURSOR_START = pValue;
		cursorsettings.put("CURSOR_START", pValue);
	}
}
