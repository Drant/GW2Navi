package GW2Navi;

/**
 * NavigationBar.java browser navigation buttons and address bar, modified from
 * ControlPanel.java by Chromium Embedded Framework Authors.
 */

import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.cef.OS;
import org.cef.browser.CefBrowser;

@SuppressWarnings("serial")
public class NavigationBar extends JPanel {
	
	Navi oNavi;
	private final JButton btnBack;
	private final JButton btnForward;
	private final JButton btnReload;
	private final JTextField fldAddress;
	private final String SEARCH_SUBSTITUTE = "%s";
	private final int ADDRESS_BAR_COLUMNS = 100;
	private final JButton btnZoomIn;
	private final JButton btnZoomDefault;
	private final JButton btnZoomOut;
	private final Dimension dimenButton = new Dimension(32, 16);
	private final Dimension dimenButtonWide = new Dimension(48, 16);
	private final Dimension dimenButtonZoomDefault = new Dimension(52, 16);
	
	public NavigationBar(Navi pNavi)
	{
		oNavi = pNavi;
		setEnabled(oNavi.TheBrowser != null);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		// BACK BUTTON
		btnBack = createNavbutton("br_back", dimenButtonWide);
		btnBack.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.goBack();
		});
		add(btnBack);
		add(Box.createHorizontalStrut(4)); // Padding

		// FORWARD BUTTON
		btnForward = createNavbutton("br_forward", dimenButtonWide);
		btnForward.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.goForward();
		});
		add(btnForward);
		add(Box.createHorizontalStrut(4));

		// RELOAD BUTTON
		btnReload = createNavbutton("br_reload");
		btnReload.addActionListener((ActionEvent e) ->
		{
			// Ctrl clicking the button will be hard refresh
			int mask = OS.isMacintosh() ? ActionEvent.META_MASK : ActionEvent.CTRL_MASK;
			if ((e.getModifiers() & mask) != 0)
			{
				oNavi.TheBrowser.reloadIgnoreCache();
			}
			else
			{
				oNavi.TheBrowser.reload();
			}
		});
		add(btnReload);
		add(Box.createHorizontalStrut(4));
		
		// GO BUTTON
		JButton goButton = createNavbutton("br_go");
		goButton.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.loadURL(getAddress());
		});
		add(goButton);
		add(Box.createHorizontalStrut(4));
		
		// ADDRESS BAR
		fldAddress = new JTextField(ADDRESS_BAR_COLUMNS);
		fldAddress.setAlignmentX(LEFT_ALIGNMENT);
		fldAddress.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.loadURL(getAddress());
		});
		fldAddress.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				KeyboardFocusManager.getCurrentKeyboardFocusManager()
						.clearGlobalFocusOwner();
				fldAddress.requestFocus();
				if (e.getClickCount() == 2)
				{
					fldAddress.selectAll();
				}
			}
		});
		add(fldAddress);
		add(Box.createHorizontalStrut(4));
		
		// ZOOM DEFAULT BUTTON
		btnZoomDefault = new JButton(Double.toString(oNavi.TheOptions.ZOOM_LEVEL)); // Also acts as zoom label
		btnZoomDefault.setFocusable(false);
		btnZoomDefault.setPreferredSize(dimenButtonZoomDefault);
		btnZoomDefault.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.setZoomLevel(oNavi.TheOptions.ZOOM_LEVEL = oNavi.TheBrowserWrapper.ZOOM_LEVEL_DEFAULT);
			btnZoomDefault.setText(Double.toString(oNavi.TheOptions.ZOOM_LEVEL));
		});
		// ZOOM OUT BUTTON
		btnZoomOut = createNavbutton("br_zoomout");
		btnZoomOut.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.setZoomLevel(--oNavi.TheOptions.ZOOM_LEVEL);
			btnZoomDefault.setText(Double.toString(oNavi.TheOptions.ZOOM_LEVEL));
		});
		// ZOOM IN BUTTON
		btnZoomIn = createNavbutton("br_zoomin");
		btnZoomIn.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.setZoomLevel(++oNavi.TheOptions.ZOOM_LEVEL);
			btnZoomDefault.setText(Double.toString(oNavi.TheOptions.ZOOM_LEVEL));
		});
		// Add zoom buttons
		add(btnZoomOut);
		add(btnZoomDefault);
		add(btnZoomIn);
	}
	
	/**
	 * Constructs a standardized navigation button.
	 * @param pIcon name.
	 * @param pSize dimension.
	 * @return JButton.
	 */
	private JButton createNavbutton(String pIcon)
	{
		return createNavbutton(pIcon, dimenButton);
	}
	private JButton createNavbutton(String pIcon, Dimension pSize)
	{
		JButton button = new JButton(Navi.getIcon(pIcon));
		/*button.setContentAreaFilled(false);
		button.setBorderPainted(false);*/
		button.setFocusable(false);
		button.setFocusPainted(false);
		button.setPreferredSize(pSize);
		button.setAlignmentX(LEFT_ALIGNMENT);
		return button;
	}

	/**
	 * Updates navigation buttons depending on browser state.
	 * @param browser
	 * @param isLoading
	 * @param canGoBack
	 * @param canGoForward 
	 */
	public void update(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward)
	{
		if (browser == oNavi.TheBrowser)
		{
			btnBack.setEnabled(canGoBack);
			btnForward.setEnabled(canGoForward);
		}
	}

	/**
	 * Formats an address string from the address bar for loading URL.
	 * @return address.
	 */
	public String getAddress()
	{
		String address = fldAddress.getText();
		// If the URI format is unknown "new URI" will throw an
		// exception. In this case we interpret the value of the
		// address field as search request. Therefore we simply add
		// the "search" scheme.
		try
		{
			address = address.replaceAll(" ", "%20");
			URI test = new URI(address);
			if (test.getScheme() != null)
			{
				return address;
			}
			if (test.getHost() != null && test.getPath() != null)
			{
				return address;
			}
			String specific = test.getSchemeSpecificPart();
			if (specific.indexOf('.') == -1)
			{
				throw new URISyntaxException(specific, "No dot inside domain");
			}
		}
		catch (URISyntaxException e1)
		{
			address = oNavi.TheOptions.URL_SEARCH.replaceAll(SEARCH_SUBSTITUTE, address);
		}
		return address;
	}
	
	/**
	 * Sets the content of the address bar element.
	 * @param browser
	 * @param address 
	 */
	public void setAddress(CefBrowser browser, String address)
	{
		if (browser == oNavi.TheBrowser)
		{
			fldAddress.setText(address);
		}
	}
}
