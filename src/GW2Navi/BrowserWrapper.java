package GW2Navi;

/**
 * Browser.java wrapper class for the actual browser. Constructs the browser and
 * its navigation bar. Also contains additional URL functions. Uses code from
 * MainFrame.java by Chromium Embedded Framework Authors
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.callback.CefMenuModel.MenuId;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefContextMenuHandler;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefLoadHandlerAdapter;

public class BrowserWrapper {
	
	// Browser
	protected CefApp JCEF_App;
	protected CefClient JCEF_Client;
	protected CefSettings JCEF_Settings;
	protected CefBrowser JCEF_Browser;
	
	// Dependence
	NavigationBar TheNavbar;
	Option TheOptions;
	
	// Constants and limits
	final String CACHE_FOLDER_NAME = "GW2Navi"; // Browser cache folder, absolute in %APPDATA%
	final String DIRECTORY_CACHE = "bin\\cache"; // Browser cache folder, relative to executable
	final String[] LEGAL_URL_SCHEMES = {"http://", "https://"}; // Last visited URL must start with these substrings
	final int URL_CHAR_LIMIT = 1024;
	
	// Constructor
	public BrowserWrapper(Navi pNavi, boolean pIsProjection)
	{
		TheOptions = pNavi.TheOptions;
		boolean wantOffscreenRendering = false;
		boolean wantTransparent = true;
		
		// Setup browser
		JPanel webBrowserPanel = new JPanel(new BorderLayout());
		webBrowserPanel.setBorder(BorderFactory.createEmptyBorder());
		webBrowserPanel.setOpaque(false);
		webBrowserPanel.setBackground(new Color(0, 0, 0, 0));
		List<String> argList = new ArrayList<>();
		argList.add("--enable-aggressive-domstorage-flushing"); // More frequent saves of browser storage
		if (pIsProjection)
		{
			argList.add("--enable-gpu");
			wantOffscreenRendering = true;
		}
		String[] args = argList.toArray(new String[argList.size()]);
		// Shutdown the app if the native CEF part is terminated
		CefApp.addAppHandler(new CefAppHandlerAdapter(args)
		{
			@Override
			public void stateHasChanged(org.cef.CefApp.CefAppState state)
			{
				if (state == CefApp.CefAppState.TERMINATED)
				{
					System.exit(0);
				}
			}
		});

		// Initialize browser settings
		JCEF_Settings = new CefSettings();
		JCEF_Settings.cache_path = (TheOptions.wantPortable) ? DIRECTORY_CACHE : new File(System.getenv("AppData"), CACHE_FOLDER_NAME).getAbsolutePath();
		JCEF_Settings.windowless_rendering_enabled = false; // If set to true then the setMnemonic menu key shortcuts will not work
		JCEF_App = CefApp.getInstance(args, JCEF_Settings);
		
		// Start browser
		JCEF_Client = JCEF_App.createClient();
		String loadUrl = sanitizeAddress((TheOptions.wantLastVisited) ? TheOptions.URL_LASTVISITED : TheOptions.URL_HOMEPAGE);
		if (pIsProjection)
		{
			loadUrl = TheOptions.URL_PROJECTION;
		}
		JCEF_Client.addContextMenuHandler(new ContextMenuHandler());
		JCEF_Browser = JCEF_Client.createBrowser(loadUrl, wantOffscreenRendering, wantTransparent);

		// Create navigation bar
		if (pIsProjection == false)
		{
			TheNavbar = new NavigationBar(JCEF_Browser, TheOptions);
			JCEF_Client.addLoadHandler(new CefLoadHandlerAdapter()
			{
				@Override
				public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward)
				{
					TheNavbar.update(browser, isLoading, canGoBack, canGoForward);
				}
			});
		}
		
		// Custom event handling and browser console
		JCEF_Client.addDisplayHandler(new CefDisplayHandlerAdapter()
		{
			@Override
			public void onAddressChange(CefBrowser browser, String url)
			{
				if (TheNavbar != null)
				{
					TheNavbar.setAddress(browser, url);
				}
			}
			@Override
			public boolean onConsoleMessage(CefBrowser browser, String message, String source, int line)
			{
				pNavi.addLog("\"" + message + "\", source: " + source + " (" + line + ")");
				return false;
			}
		});
		
		// Insert browser into frame
		webBrowserPanel.add(JCEF_Browser.getUIComponent(), BorderLayout.CENTER);
		if (pIsProjection)
		{
			pNavi.setBackground(new Color(0, 0, 0, 0));
			pNavi.setOpaque(false);
		}
		else
		{
			pNavi.add(TheNavbar, BorderLayout.NORTH);
			JCEF_Browser.getUIComponent().setFocusable(false); // Prevents the browser from taking focus from the frame
		}
		pNavi.add(webBrowserPanel, BorderLayout.CENTER);
		
		// Have to wait for the browser to load before zoom works
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				JCEF_Browser.setZoomLevel(TheOptions.ZOOM_DEFAULT_LEVEL);
			}
		}, TheOptions.ZOOM_STARTUP_DELAY);
	}
	
	/**
	 * Gets the actual browser.
	 * @return browser object.
	 */
	public CefBrowser getBrowser()
	{
		return JCEF_Browser;
	}
	
	/**
	 * Cleans an address bar location that is to be saved.
	 * @param pAddress string.
	 * @return sanitized URL.
	 */
	public String sanitizeAddress(String pAddress)
	{
		boolean isAddressLegal = false;
		// Address must begin with legal URL scheme
		for (String iScheme : LEGAL_URL_SCHEMES)
		{
			if (pAddress.indexOf(iScheme) == 0)
			{
				isAddressLegal = true;
				break;
			}
		}
		if (isAddressLegal)
		{
			// Truncate and sanitize
			if (pAddress.length() > URL_CHAR_LIMIT)
			{
				return pAddress.substring(0, URL_CHAR_LIMIT);
			}
			return pAddress; 
		}
		
		// If illegal address then use default URL
		return TheOptions.URL_HOMEPAGE;
	}
	
	/**
	 * Tells if current viewed site is GW2Timer or at least localhost so
	 * JavaScript is executed exclusively there.
	 * @return true if so.
	 */
	public boolean verifySite()
	{
		String currenturl = JCEF_Browser.getURL();
		String sitedomain = TheOptions.URL_SITE;
		String localdomain = TheOptions.URL_LOCAL;
		
		// Checks if the substring from the beginning of the URL contains the match
		if (currenturl.length() >= sitedomain.length())
		{
			if (currenturl.substring(0, sitedomain.length()).equals(sitedomain))
			{
				return true;
			}
		}
		if (currenturl.length() >= localdomain.length())
		{
			if (currenturl.substring(0, localdomain.length()).equals(localdomain))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Executes JavaScript after verifying the current URL location.
	 * Does nothing if failed verification.
	 * @param pJavaScript to execute.
	 */
	protected void verifiedExecute(String pJavaScript)
	{
		if (verifySite())
		{
			JCEF_Browser.executeJavaScript(pJavaScript, JCEF_Browser.getURL(), 0);
		}
	}
	protected void executeJavaScript(String pJavaScript)
	{
		JCEF_Browser.executeJavaScript(pJavaScript, JCEF_Browser.getURL(), 0);
	}
	
	/**
	 * The website has its own context menu, so this minimal browser context menu
	 * is kept only for the "Copy" and "Paste" functionality.
	 */
	public class ContextMenuHandler implements CefContextMenuHandler
	{
		@Override
		public void onBeforeContextMenu(CefBrowser pBrowser, CefContextMenuParams pParams, CefMenuModel pModel)
		{
			pModel.insertItemAt(0, MenuId.MENU_ID_RELOAD_NOCACHE, "Reload");
			pModel.insertSeparatorAt(0);
			// Does nothing, allowing the user to cancel the context menu without clicking outside
			pModel.insertItemAt(0, MenuId.MENU_ID_STOPLOAD, "Cancel");
		}
		@Override
		public boolean onContextMenuCommand(CefBrowser cb, CefContextMenuParams ccmp, int i, int i1)
		{
			return false;
		}
		@Override
		public void onContextMenuDismissed(CefBrowser cb)
		{
			
		}
	}
}
