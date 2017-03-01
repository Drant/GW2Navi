/**
 * VisibleCursor.java creates a transparent frame with an image of a cursor;
 * the Always On Top setting and constant movement of the frame on the real
 * cursor's position allows it to mimic the appearance of a cursor.
 */
package GW2Navi;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import javax.swing.JWindow;
import javax.swing.JLabel;

public class VisibleCursor implements Runnable {
	
	JWindow PseudoCursor;
	public boolean wantLoop = true;
	WindowPreset CursorPreset;
	
	// Dependence
	Option TheOptions;
	
	// Constructor
	public VisibleCursor(Option pOptions)
	{
		// Load customization
		TheOptions = pOptions;
		CursorPreset = TheOptions.CURSORPRESET_USER[TheOptions.CURSOR_START];
		
		// Create pseudo cursor
		PseudoCursor = new JWindow();
		PseudoCursor.add(new JLabel(Navi.getCustomIcon(TheOptions.CURSOR_USER[TheOptions.CURSOR_START]))); // The cursor image
		PseudoCursor.setSize(CursorPreset.Width, CursorPreset.Height);
		PseudoCursor.setAlwaysOnTop(true);
		PseudoCursor.setBackground(new Color(0, 0, 0, 0));
		PseudoCursor.setVisible(true);
		
		// Set clickthrough
		WinDef.HWND hwnd = Navi.getHWnd(PseudoCursor);
		int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
		wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
		User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
	}

	// Move the pseudo cursor over the real cursor.
	@Override
	public void run()
	{
		try
		{
			while (wantLoop)
			{
				PseudoCursor.setVisible(true);
				Point location = MouseInfo.getPointerInfo().getLocation();
				Point locationProper = new Point(location.x - CursorPreset.PosX, location.y - CursorPreset.PosY);
				PseudoCursor.setLocation(locationProper);
				// Want the pseudo cursor to refresh its location as fast as possible
				Thread.sleep(TheOptions.CURSOR_REFRESH_RATE);
			}
		}
		catch (InterruptedException ex)
		{
			System.out.println("Cursor thread sleep error.");
        }
	}
	
	/**
	 * Stops the thread and destroys the pseudo cursor.
	 */
	public void kill()
	{
		wantLoop = false;
		PseudoCursor.setVisible(false);
		PseudoCursor.dispose();
	}
}
