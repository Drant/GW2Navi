package GW2Navi;

/**
 * GPS.java reads Guild Wars 2 memory-mapped file using the Mumble Link API and
 * updates the website's JavaScript variables. http://wiki.mumble.info/wiki/Link
 * Contains code from post by Lulan.8497
 * Source: https://forum-en.guildwars2.com/forum/community/api/Mumble-Link-for-Java-using-JNA
 */

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import java.util.Arrays;

public class GPS implements Runnable {
	
	// GPS data format
	private final int MEM_MAP_SIZE = 5460;
	private final String MEM_MAP_NAME = "MumbleLink";
	private final HANDLE sharedFile;
	private final Pointer sharedMemory;
	private int uiVersion = 0;
	private int uiTick = 0;
	private float[] fAvatarPosition = new float[0];
	private float[] fAvatarFront = new float[0];
	private float[] fAvatarTop = new float[0];
	private float[] fCameraPosition = new float[0];
	private float[] fCameraFront = new float[0];
	private float[] fCameraTop = new float[0];
	private char[] identity = new char[0];
	private char[] gameName = new char[0];
	private int context_len = 0;
	private byte[] context = new byte[0];
	
	// Overlay specific
	BrowserWrapper TheBrowserWrapper;
	Option TheOptions;
	public boolean wantLoop = true;
	
	// Constructor
	public GPS(Navi pNavi, BrowserWrapper pBrowser)
	{
		sharedFile = Kernel32.INSTANCE.CreateFileMapping(
			WinBase.INVALID_HANDLE_VALUE, null, WinNT.PAGE_EXECUTE_READWRITE, 0, MEM_MAP_SIZE, MEM_MAP_NAME);
		sharedMemory = Kernel32.INSTANCE.MapViewOfFile(
			sharedFile, WinNT.SECTION_MAP_READ, 0, 0, MEM_MAP_SIZE);
		
		TheBrowserWrapper = pBrowser;
		TheOptions = pNavi.TheOptions;
	}

	@Override
	public void run()
	{
		try
		{
			while (wantLoop && this.sharedMemory != null)
			{
				fAvatarPosition = this.sharedMemory.getFloatArray(8, 3);
				fAvatarFront = this.sharedMemory.getFloatArray(20, 3);
				fCameraPosition = this.sharedMemory.getFloatArray(556, 3);
				fCameraFront = this.sharedMemory.getFloatArray(568, 3);
				identity = this.sharedMemory.getCharArray(592, 256);
				final String indentitystr = sanitizeIdentity(new String(identity)).trim();
				
				if (TheBrowserWrapper.verifySite())
				{
					// Tell the website to update its global variables
					String js = TheOptions.JS_GPS_POSITION + Arrays.toString(fAvatarPosition) + ";"
						+ TheOptions.JS_GPS_DIRECTION + Arrays.toString(fAvatarFront) + ";"
						+ TheOptions.JS_GPS_PERSPECTIVE + Arrays.toString(fCameraPosition) + ";"
						+ TheOptions.JS_GPS_CAMERA + Arrays.toString(fCameraFront) + ";"
						+ TheOptions.JS_GPS_IDENTITY + indentitystr + ";";
					TheBrowserWrapper.executeJavaScript(js);
				}

				// Original code with all API variables
				/*
				uiVersion = this.sharedMemory.getInt(0);
				uiTick = this.sharedMemory.getInt(4);
				fAvatarPosition = this.sharedMemory.getFloatArray(8, 3);
				fAvatarFront = this.sharedMemory.getFloatArray(20, 3);
				fAvatarTop = this.sharedMemory.getFloatArray(32, 3);
				gameName = this.sharedMemory.getCharArray(44, 256);
				fCameraPosition = this.sharedMemory.getFloatArray(556, 3);
				fCameraFront = this.sharedMemory.getFloatArray(568, 3);
				fCameraTop = this.sharedMemory.getFloatArray(580, 3);
				identity = this.sharedMemory.getCharArray(592, 256);
				context_len = this.sharedMemory.getInt(1104);
				context = this.sharedMemory.getByteArray(1108, 256);
				System.out.println("uiVersion: " + uiVersion);
				System.out.println("uiTick: " + uiTick);
				System.out.println("fAvatarPosition: " + Arrays.toString(fAvatarPosition));
				System.out.println("fAvatarFront: " + Arrays.toString(fAvatarFront));
				System.out.println("fAvatarTop: " + Arrays.toString(fAvatarTop));
				System.out.println("gameName: " + (new String(gameName)).trim());
				System.out.println("fCameraPosition: " + Arrays.toString(fCameraPosition));
				System.out.println("fCameraFront: " + Arrays.toString(fCameraFront));
				System.out.println("fCameraTop: " + Arrays.toString(fCameraTop));
				System.out.println("identity: " + (new String(identity)).trim());
				System.out.println("context_len: " + context_len);
				System.out.println("context: " + Arrays.toString(context));
				System.out.println("#####################################################");
				*/

				Thread.sleep(TheOptions.GPS_REFRESH_RATE);
			}
		}
		catch (InterruptedException ex)
		{
			System.out.println("GPS thread sleep error.");
        }
	}
	
	/**
	 * Retrieved JSON may sometimes be untrimmed, extracts only the outermost {} part.
	 * @param pString JSON to sanitize.
	 * @return sanitized JSON string.
	 */
	public String sanitizeIdentity(String pString)
	{
		String s = pString;
		int bgn = s.indexOf("{");
		int end = s.indexOf("}");
		if (bgn == -1 || end == -1)
		{
			return "null";
		}
		return s.substring(bgn, end + 1);
	}
	
	/**
	 * Calling this before terminating the program will prevent an access violation.
	 */
	public void kill()
	{
		wantLoop = false;
	}
}
