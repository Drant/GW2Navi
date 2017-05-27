package GW2Navi;

/**
 * Menu.java creates and binds menu bar, menu popup, and tray icon.
 */

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

public class Menu {
	
	Navi oNavi;
	boolean isProjection = false;
	CustomMenu TheMenu; // The menu and its submenus on the bar
	JDialog TrayPopupHelper;
	static Point mousePressedPoint;
	int ICON_HEIGHT = 16;
	
	JRadioButtonMenuItem tempradioitem;
	JMenuItem item_AlwaysOnTop;
	JMenuItem item_OpaqueOnFocus;
	JMenuItem item_LastVisited;
	JMenuItem item_EnableGPS;
	JMenuItem item_EnableVisibleCursor;
	JMenuItem item_EnableKnobMoveable;
	JMenuItem item_EnableKnobBig;
	JMenuItem item_EnableNative;
	JMenu menu_OpacityFocused;
	JMenu menu_Language;
	JMenu menu_Cursor;
	
	/**
	 * Creates menu for specific overlay modes.
	 * @param pNavi to access variables.
	 * @param pIsProjection whether to style the overlay as so.
	 */
	public Menu(Navi pNavi, boolean pIsProjection)
	{
		oNavi = pNavi;
		isProjection = pIsProjection;
		
		initializeOptionsItems();
		if (isProjection)
		{
			
		}
		else
		{
			createBarMenu();
			createBarPopup();
			oNavi.TheBar.add(TheMenu);
		}
		if (SystemTray.isSupported())
		{
			createTray();
		}
	}
	
	/**
	 * Initializes the shared menu items between the styles that affect options.
	 */
	private void initializeOptionsItems()
	{
		JMenuItem tempitem;
		
		item_AlwaysOnTop = new JCheckBoxMenuItem(oNavi.TheTranslations.get("AlwaysOnTop"));
		item_OpaqueOnFocus = new JCheckBoxMenuItem(oNavi.TheTranslations.get("Enable", "Focused"));
		item_EnableVisibleCursor = new JCheckBoxMenuItem(oNavi.TheTranslations.get("Enable", "Cursor"));
		item_LastVisited = new JCheckBoxMenuItem(oNavi.TheTranslations.get("Enable", "LastVisited"));
		item_EnableGPS = new JCheckBoxMenuItem(oNavi.TheTranslations.get("Enable") + " GPS");
		item_EnableNative = new JCheckBoxMenuItem(oNavi.TheTranslations.get("Enable", "Native") + " GUI");
		item_EnableKnobMoveable = new JCheckBoxMenuItem(oNavi.TheTranslations.get("Moveable", "Knob"));
		item_EnableKnobBig = new JCheckBoxMenuItem(oNavi.TheTranslations.get("Big", "Knob"));
		menu_OpacityFocused = new JMenu(oNavi.TheTranslations.get("Opacity", "Focused"));
		menu_Cursor = new JMenu(oNavi.TheTranslations.get("Cursor"));
		menu_Language = new JMenu(oNavi.TheTranslations.get("Language"));
		
		item_AlwaysOnTop.setIcon(Navi.getIcon("alwaysontop"));
		item_LastVisited.setIcon(Navi.getIcon("lastvisited"));
		item_EnableGPS.setIcon(Navi.getIcon("gps"));
		item_EnableNative.setIcon(Navi.getIcon("native"));
		item_EnableKnobMoveable.setIcon(Navi.getIcon("moveable"));
		item_EnableKnobBig.setIcon(Navi.getIcon("big"));
		menu_OpacityFocused.setIcon(Navi.getIcon("opacity"));
		menu_Language.setIcon(Navi.getIcon("language"));
		menu_Cursor.setIcon(Navi.getIcon("cursor"));
		
		item_AlwaysOnTop.setMnemonic(KeyEvent.VK_A);
		item_OpaqueOnFocus.setMnemonic(KeyEvent.VK_F);
		item_EnableVisibleCursor.setMnemonic(KeyEvent.VK_R);
		item_LastVisited.setMnemonic(KeyEvent.VK_V);
		item_EnableGPS.setMnemonic(KeyEvent.VK_G);
		item_EnableNative.setMnemonic(KeyEvent.VK_N);
		item_EnableKnobMoveable.setMnemonic(KeyEvent.VK_L);
		item_EnableKnobBig.setMnemonic(KeyEvent.VK_B);
		menu_OpacityFocused.setMnemonic(KeyEvent.VK_F);
		menu_Cursor.setMnemonic(KeyEvent.VK_R);
		menu_Language.setMnemonic(KeyEvent.VK_L);
		
		item_AlwaysOnTop.setSelected(oNavi.TheOptions.wantAlwaysOnTop);
		item_OpaqueOnFocus.setSelected(oNavi.TheOptions.wantOpacityOnFocus);
		item_EnableVisibleCursor.setSelected(oNavi.TheOptions.wantVisibleCursor);
		item_LastVisited.setSelected(oNavi.TheOptions.wantLastVisited);
		item_EnableGPS.setSelected(oNavi.TheOptions.wantGPS);
		item_EnableNative.setSelected(oNavi.TheOptions.wantNativeInterface);
		item_EnableKnobMoveable.setSelected(oNavi.TheOptions.wantKnobMoveable);
		item_EnableKnobBig.setSelected(oNavi.TheOptions.wantKnobBig);
		
		item_AlwaysOnTop.addItemListener((ItemEvent e) ->
		{
			boolean want = (e.getStateChange() == ItemEvent.SELECTED);
			oNavi.TheOptions.set_wantAlwaysOnTop(want);
			oNavi.TheFrame.setAlwaysOnTop(want);
		});
		
		item_EnableVisibleCursor.addItemListener((ItemEvent e) ->
		{
			boolean want = (e.getStateChange() == ItemEvent.SELECTED);
			oNavi.TheOptions.set_wantVisibleCursor(want);
			oNavi.toggleVisibleCursor(want);
		});
		
		item_LastVisited.addItemListener((ItemEvent e) ->
		{
			oNavi.TheOptions.set_wantLastVisited(e.getStateChange() == ItemEvent.SELECTED);
		});
		
		item_EnableGPS.addItemListener((ItemEvent e) ->
		{
			boolean want = (e.getStateChange() == ItemEvent.SELECTED);
			oNavi.TheOptions.set_wantGPS(want);
			oNavi.toggleGPS(want);
		});
		
		item_EnableNative.addItemListener((ItemEvent e) ->
		{
			boolean want = (e.getStateChange() == ItemEvent.SELECTED);
			oNavi.TheOptions.set_wantNativeInterface(want);
			oNavi.doOptionExit();
		});
		
		item_EnableKnobMoveable.addItemListener((ItemEvent e) ->
		{
			boolean want = (e.getStateChange() == ItemEvent.SELECTED);
			oNavi.TheOptions.set_wantKnobMoveable(want);
			oNavi.TheKnob.setMoveable(want);
		});
		
		item_EnableKnobBig.addItemListener((ItemEvent e) ->
		{
			boolean want = (e.getStateChange() == ItemEvent.SELECTED);
			oNavi.TheOptions.set_wantKnobBig(want);
			oNavi.TheKnob.resetSize();
		});
		
		// Opacity on Focus option
		item_OpaqueOnFocus.addItemListener((ItemEvent e) ->
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				oNavi.TheOptions.set_wantOpacityOnFocus(true);
				if (oNavi.TheFrame.isFocused())
				{
					oNavi.TheFrame.setOpacity(oNavi.TheOptions.OPACITY_FOCUSED);
				}
			}
			else
			{
				oNavi.TheOptions.set_wantOpacityOnFocus(false);
				oNavi.TheFrame.setOpacity(oNavi.TheOptions.OPACITY_UNFOCUSED);
			}
		});
		
		// Menu items for "Focused Opacity" menu
		//------------------------------------------------------------------
		menu_OpacityFocused.add(item_OpaqueOnFocus);
		createOpacityList(OpacityType.WindowFocused, menu_OpacityFocused);
		
		// Menu items for "Cursor" menu
		//------------------------------------------------------------------
		menu_Cursor.add(item_EnableVisibleCursor);
		for (int i = 0; i < oNavi.TheOptions.CURSOR_USER.length; i++)
		{
			tempitem = new JMenuItem("#" + (i+1) + " " + oNavi.TheOptions.CURSOR_USER[i]);
			tempitem.setIcon(Navi.getIcon("open"));
			tempitem.setMnemonic('0' + (i+1));
			menu_Cursor.add(tempitem);
			tempitem.addActionListener((ActionEvent e) ->
			{
				item_EnableVisibleCursor.setSelected(true);
				int index = getMenuItemIndex(menu_Cursor, e) - 1;
				oNavi.TheOptions.set_CURSOR_START(index);
				oNavi.toggleVisibleCursor();
			});
		}
		
		// Menu items for "Language" menu
		//------------------------------------------------------------------
		ButtonGroup group_Language = new ButtonGroup();
		final ArrayList<JRadioButtonMenuItem> item_LanguageArraylist = new ArrayList();
		
		for (int i = 0; i < oNavi.TheTranslations.lang.length; i++)
		{
			tempradioitem = new JRadioButtonMenuItem(oNavi.TheTranslations.lang[i]);
			tempradioitem.addActionListener((ActionEvent e) ->
			{
				for (int i1 = 0; i1 < oNavi.TheTranslations.lang.length; i1++)
				{
					if (item_LanguageArraylist.get(i1).isSelected())
					{
						// Save the selected language and ask user to restart
						oNavi.TheOptions.set_LANGUAGE(oNavi.TheTranslations.code[i1]);
						JOptionPane.showMessageDialog(
							oNavi.TheFrame,
							oNavi.TheTranslations.getFromLang("OptionSelected", oNavi.TheTranslations.code[i1]),
							oNavi.TheTranslations.getFromLang("Language", oNavi.TheTranslations.code[i1]),
							JOptionPane.INFORMATION_MESSAGE
						);
						oNavi.doExit();
						break;
					}
				}
			});
			
			group_Language.add(tempradioitem);
			item_LanguageArraylist.add(tempradioitem);
			menu_Language.add(tempradioitem);
			
			// Select the language written in the options
			if (oNavi.TheTranslations.code[i].equals(oNavi.TheOptions.LANGUAGE))
			{
				item_LanguageArraylist.get(i).setSelected(true);
			}
		}
	}
	
	/**
	 * Creates the menu bar, bar buttons, and its submenus.
	 */
	private void createBarMenu()
	{
		// Menu creation
		//==================================================================
		
		TheMenu = new CustomMenu();
		TheMenu.setCursor(new FrameCursor().NORMAL);
		JMenuItem tempitem;
		
		JMenuItem menu_Miniaturize = new JMenuItem(Navi.getIcon("m_miniaturize"));
		JMenu menu_Main = new JMenu("");
		menu_Main.setIcon(Navi.getIcon("m_menu"));
		JMenuItem menu_Minimize = new JMenu("");
		menu_Minimize.setIcon(Navi.getIcon("m_minimize"));
		JMenuItem menu_Quick_1 = new JMenuItem(Navi.getIcon("m_1"));
		JMenuItem menu_Quick_2 = new JMenuItem(Navi.getIcon("m_2"));
		JMenuItem menu_Quick_3 = new JMenuItem(Navi.getIcon("m_3"));
		JMenuItem menu_Quick_4 = new JMenuItem(Navi.getIcon("m_4"));
		JMenuItem menu_Quick_A = new JMenuItem(Navi.getIcon("m_A"));
		JMenuItem menu_Quick_B = new JMenuItem(Navi.getIcon("m_B"));
		JMenuItem menu_Quick_Toggle = new JMenuItem(Navi.getIcon("m_toggle"));
		TheMenu.add(Box.createHorizontalGlue());
		
		menu_Miniaturize.setOpaque(false);
		menu_Quick_1.setOpaque(false);
		menu_Quick_2.setOpaque(false);
		menu_Quick_3.setOpaque(false);
		menu_Quick_4.setOpaque(false);
		menu_Quick_A.setOpaque(false);
		menu_Quick_B.setOpaque(false);
		menu_Quick_Toggle.setOpaque(false);
		
		menu_Miniaturize.setPreferredSize(new Dimension((oNavi.TheOptions.wantNativeInterface ? 16 : 20), ICON_HEIGHT));
		menu_Quick_1.setPreferredSize(new Dimension(20, ICON_HEIGHT));
		menu_Quick_2.setPreferredSize(new Dimension(20, ICON_HEIGHT));
		menu_Quick_3.setPreferredSize(new Dimension(20, ICON_HEIGHT));
		menu_Quick_4.setPreferredSize(new Dimension(20, ICON_HEIGHT));
		menu_Quick_A.setPreferredSize(new Dimension(20, ICON_HEIGHT));
		menu_Quick_B.setPreferredSize(new Dimension(20, ICON_HEIGHT));
		menu_Quick_Toggle.setPreferredSize(new Dimension(24, ICON_HEIGHT));
		
		// Press and drag mouse to move window
		menu_Miniaturize.addMouseMotionListener(TheMenu);
		menu_Main.addMouseMotionListener(TheMenu);
		menu_Minimize.addMouseMotionListener(TheMenu);
		menu_Quick_1.addMouseMotionListener(TheMenu);
		menu_Quick_2.addMouseMotionListener(TheMenu);
		menu_Quick_3.addMouseMotionListener(TheMenu);
		menu_Quick_4.addMouseMotionListener(TheMenu);
		menu_Quick_A.addMouseMotionListener(TheMenu);
		menu_Quick_B.addMouseMotionListener(TheMenu);
		menu_Quick_Toggle.addMouseMotionListener(TheMenu);
		
		// Right click to open context menu
		menu_Quick_1.addMouseListener(new PopupListener());
		menu_Quick_2.addMouseListener(new PopupListener());
		menu_Quick_3.addMouseListener(new PopupListener());
		menu_Quick_4.addMouseListener(new PopupListener());
		menu_Quick_A.addMouseListener(new PopupListener());
		menu_Quick_B.addMouseListener(new PopupListener());
		menu_Quick_Toggle.addMouseListener(new PopupListener());
		
		TheMenu.add(menu_Miniaturize);
		TheMenu.add(menu_Main);
		TheMenu.add(menu_Minimize);
		TheMenu.add(menu_Quick_1);
		TheMenu.add(menu_Quick_2);
		TheMenu.add(menu_Quick_3);
		TheMenu.add(menu_Quick_4);
		TheMenu.add(menu_Quick_A);
		TheMenu.add(menu_Quick_B);
		TheMenu.add(menu_Quick_Toggle);
		
		
		// "G" miniaturize button top level
		//------------------------------------------------------------------
		menu_Miniaturize.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_miniaturize_h"));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_miniaturize"));
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressedPoint = oNavi.TheFrame.getMousePosition();
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					oNavi.miniaturizeFrame();
				}
				else
				{
					oNavi.setClickable(false);
				}
			}
		});
		
		// "_" minimize button top level
		//------------------------------------------------------------------
		menu_Minimize.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_minimize_h"));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_minimize"));
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressedPoint = oNavi.TheFrame.getMousePosition();
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					oNavi.TheFrame.setState(Frame.ICONIFIED);
				}
				else if (SwingUtilities.isRightMouseButton(e))
				{
					oNavi.toggleFrame(false);
				}
			}
		});
		
		// "≡" menu top level
		//------------------------------------------------------------------
		menu_Main.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				((JMenu) e.getSource()).setIcon(Navi.getIcon("m_menu_h"));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				((JMenu) e.getSource()).setIcon(Navi.getIcon("m_menu"));
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressedPoint = oNavi.TheFrame.getMousePosition();
			}
		});
		
		JMenu menu_Bookmarks = new JMenu(oNavi.TheTranslations.get("Bookmarks"));
		JMenu menu_Options = new JMenu(oNavi.TheTranslations.get("Options"));
		JMenuItem item_Navigation = new JCheckBoxMenuItem(oNavi.TheTranslations.get("Show", "Navigation"));
		JMenuItem item_NewWindow = new JMenuItem(oNavi.TheTranslations.get("New", "Window"));
		JMenuItem item_Reload = new JMenuItem(oNavi.TheTranslations.get("Reload"));
		JMenuItem item_Log = new JMenuItem(oNavi.TheTranslations.get("Log"));
		JMenuItem item_Home = new JMenuItem(oNavi.TheTranslations.get("Homepage"));
		JMenuItem item_Update = new JMenuItem(oNavi.TheTranslations.get("Update"));
		JMenuItem item_About = new JMenuItem(oNavi.TheTranslations.get("About"));
		JMenuItem item_Exit = new JMenuItem(oNavi.TheTranslations.get("Exit"));
		
		menu_Bookmarks.setIcon(Navi.getIcon("bookmark_folder"));
		menu_Options.setIcon(Navi.getIcon("options"));
		item_Navigation.setIcon(Navi.getIcon("navigation"));
		item_NewWindow.setIcon(Navi.getIcon("newwindow"));
		item_Reload.setIcon(Navi.getIcon("br_reload"));
		item_Log.setIcon(Navi.getIcon("log"));
		item_Home.setIcon(Navi.getIcon("home"));
		item_Update.setIcon(Navi.getIcon("update"));
		item_About.setIcon(Navi.getIcon("about"));
		item_Exit.setIcon(Navi.getIcon("exit"));

		menu_Bookmarks.setMnemonic(KeyEvent.VK_B);
		menu_Options.setMnemonic(KeyEvent.VK_S);
		item_Navigation.setMnemonic(KeyEvent.VK_V);
		item_NewWindow.setMnemonic(KeyEvent.VK_W);
		item_Reload.setMnemonic(KeyEvent.VK_R);
		item_Log.setMnemonic(KeyEvent.VK_L);
		item_Home.setMnemonic(KeyEvent.VK_H);
		item_Update.setMnemonic(KeyEvent.VK_U);
		item_About.setMnemonic(KeyEvent.VK_A);
		item_Exit.setMnemonic(KeyEvent.VK_X);
		
		menu_Main.add(menu_Bookmarks);
		menu_Main.add(item_Navigation);
		menu_Main.addSeparator();
		menu_Main.add(item_NewWindow);
		menu_Main.add(item_Reload);
		menu_Main.add(item_Log);
		menu_Main.addSeparator();
		menu_Main.add(item_Home);
		menu_Main.add(item_Update);
		menu_Main.add(item_About);
		menu_Main.addSeparator();
		menu_Main.add(menu_Options);
		menu_Main.addSeparator();
		menu_Main.add(item_Exit);
		
		item_Navigation.setSelected(oNavi.TheOptions.wantNavbar);

		item_Navigation.addItemListener((ItemEvent e) ->
		{
			oNavi.doShowNavbar(e.getStateChange() == ItemEvent.SELECTED, false);
		});
		item_NewWindow.addActionListener((ActionEvent e) ->
		{
			oNavi.openNewWindow();
		});
		item_Reload.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.reloadIgnoreCache();
		});
		item_Log.addActionListener((ActionEvent e) ->
		{
			oNavi.showLog();
		});
		item_Home.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.loadURL(oNavi.TheOptions.URL_HOMEPAGE);
		});
		item_Update.addActionListener((ActionEvent e) ->
		{
			try
			{
				Desktop.getDesktop().browse(new URI(oNavi.TheOptions.URL_UPDATE));
				oNavi.TheFrame.setState(Frame.ICONIFIED);
			}
			catch (IOException | URISyntaxException ex) {}
		});
		item_About.addActionListener((ActionEvent e) ->
		{
			JOptionPane.showMessageDialog(oNavi.TheFrame,
					new JLabel(oNavi.TEXT_ABOUT),
					oNavi.TheTranslations.get("About"),
					JOptionPane.INFORMATION_MESSAGE,
					Navi.getIcon("task_program")
			);
		});
		item_Exit.addActionListener((ActionEvent e) ->
		{
			oNavi.doExit();
		});

		// Menu items for "Bookmark" menu
		//------------------------------------------------------------------
		for (Map.Entry<String, String> entry : oNavi.TheBookmarks.Book.entrySet())
		{
			String key = entry.getKey();
			tempitem = new JMenuItem(key);
			tempitem.setIcon(Navi.getIcon("bookmark"));

			tempitem.addActionListener((ActionEvent e) ->
			{
				String key1 = e.getActionCommand();
				oNavi.TheBrowser.loadURL(oNavi.TheBookmarks.Book.get(key1));
			});
			menu_Bookmarks.add(tempitem);
		}
		
		// Menu items for "Options" menu
		//------------------------------------------------------------------
		// Options submenu
		menu_Options.add(item_AlwaysOnTop);
		menu_Options.add(menu_OpacityFocused);
		menu_Options.addSeparator();
		menu_Options.add(item_LastVisited);
		menu_Options.add(item_EnableGPS);
		menu_Options.add(item_EnableNative);
		menu_Options.addSeparator();
		menu_Options.add(menu_Language);
		
		// "1234AB" buttons top level
		//------------------------------------------------------------------
		
		/**
		 * Bar, Compact, and Tall sizes use the standard width so it's assumed
		 * that the website's sidebar must fit entirely in the window, so have
		 * to consider the border thickness too.
		 */
		menu_Quick_1.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_1_h"));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_1"));
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressedPoint = oNavi.TheFrame.getMousePosition();
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					oNavi.TheFrame.setSize(
						oNavi.TheOptions.FRAME_QUICK_1.width + oNavi.ADD_HORIZONTAL_PIXELS,
						oNavi.TheOptions.FRAME_QUICK_1.height + oNavi.ADD_VERTICAL_PIXELS + oNavi.NAVBAR_HEIGHT_CURRENT);

					oNavi.TheBrowserWrapper.verifiedExecute(oNavi.TheOptions.JS_QUICK_1);
				}
			}
		});
		menu_Quick_2.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_2_h"));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_2"));
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressedPoint = oNavi.TheFrame.getMousePosition();
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					oNavi.TheFrame.setSize(
						oNavi.TheOptions.FRAME_QUICK_2.width + oNavi.ADD_HORIZONTAL_PIXELS,
						oNavi.TheOptions.FRAME_QUICK_2.height + oNavi.ADD_VERTICAL_PIXELS + oNavi.NAVBAR_HEIGHT_CURRENT);

					oNavi.TheBrowserWrapper.verifiedExecute(oNavi.TheOptions.JS_QUICK_2);
				}
			}
		});
		menu_Quick_3.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_3_h"));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_3"));
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressedPoint = oNavi.TheFrame.getMousePosition();
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					oNavi.TheFrame.setSize(
						oNavi.TheOptions.FRAME_QUICK_3.width + oNavi.ADD_HORIZONTAL_PIXELS,
						oNavi.TheOptions.FRAME_QUICK_3.height + oNavi.ADD_VERTICAL_PIXELS + oNavi.NAVBAR_HEIGHT_CURRENT);

					oNavi.TheBrowserWrapper.verifiedExecute(oNavi.TheOptions.JS_QUICK_3);
				}
			}
		});
		menu_Quick_4.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_4_h"));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_4"));
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressedPoint = oNavi.TheFrame.getMousePosition();
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					oNavi.TheFrame.setSize(
						oNavi.TheOptions.FRAME_QUICK_4.width + oNavi.ADD_HORIZONTAL_PIXELS,
						oNavi.TheOptions.FRAME_QUICK_4.height + oNavi.ADD_VERTICAL_PIXELS + oNavi.NAVBAR_HEIGHT_CURRENT);

					oNavi.TheBrowserWrapper.verifiedExecute(oNavi.TheOptions.JS_QUICK_4);
				}
			}
		});
		menu_Quick_A.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_A_h"));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_A"));
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressedPoint = oNavi.TheFrame.getMousePosition();
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					oNavi.TheFrame.setSize(
						oNavi.TheOptions.FRAME_QUICK_A.width + oNavi.ADD_HORIZONTAL_PIXELS,
						oNavi.TheOptions.FRAME_QUICK_A.height + oNavi.ADD_VERTICAL_PIXELS + oNavi.NAVBAR_HEIGHT_CURRENT);
					
					oNavi.TheBrowserWrapper.verifiedExecute(oNavi.TheOptions.JS_QUICK_A);
				}
			}
		});
		menu_Quick_B.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_B_h"));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_B"));
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressedPoint = oNavi.TheFrame.getMousePosition();
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					oNavi.TheFrame.setSize(
						oNavi.TheOptions.FRAME_QUICK_B.width + oNavi.ADD_HORIZONTAL_PIXELS,
						oNavi.TheOptions.FRAME_QUICK_B.height + oNavi.ADD_VERTICAL_PIXELS + oNavi.NAVBAR_HEIGHT_CURRENT);
					
					oNavi.TheBrowserWrapper.verifiedExecute(oNavi.TheOptions.JS_QUICK_B);
				}
			}
		});
		menu_Quick_Toggle.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_toggle_h"));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				((JMenuItem) e.getSource()).setIcon(Navi.getIcon("m_toggle"));
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressedPoint = oNavi.TheFrame.getMousePosition();
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					oNavi.TheBrowserWrapper.verifiedExecute(oNavi.TheOptions.JS_QUICK_TOGGLE);
				}
			}
		});
	} // END OF menu bar creation

	
	
	
	
	
	
	
	
	/**
	 * Creates the menu bar's context menu that pops up after right clicking.
	 */
	private void createBarPopup()
	{
		oNavi.TheBarPopup = new JPopupMenu();
		JMenuItem tempitem;
		// Add listener to components that can bring up popup menus
		MouseListener popupListener = new PopupListener();
		oNavi.TheBar.addMouseListener(popupListener);
		
		final JMenu menu_Sizes = new JMenu("(S) " + oNavi.TheTranslations.get("Sizes"));
		final JMenu menu_Colors = new JMenu("(C) " + oNavi.TheTranslations.get("Colors"));
		final JMenu menu_Border = new JMenu("(B) " + oNavi.TheTranslations.get("Border"));
		
		menu_Sizes.setIcon(Navi.getIcon("presets"));
		menu_Colors.setIcon(Navi.getIcon("colors"));
		menu_Border.setIcon(Navi.getIcon("border"));
		
		menu_Sizes.setMnemonic(KeyEvent.VK_S);
		menu_Colors.setMnemonic(KeyEvent.VK_C);
		menu_Border.setMnemonic(KeyEvent.VK_B);
		
		oNavi.TheBarPopup.add(menu_Sizes);
		oNavi.TheBarPopup.add(menu_Colors);
		oNavi.TheBarPopup.add(menu_Border);
		oNavi.TheBarPopup.add(menu_Cursor);
		oNavi.TheBarPopup.addSeparator();
		
		// Menu items for "Sizes" menu
		//------------------------------------------------------------------
		for (int i = 0; i < oNavi.TheOptions.WINDOWPRESET_USER.length; i++)
		{
			tempitem = new JMenuItem(oNavi.TheTranslations.get("Load") + " #" + (i+1));
			tempitem.setIcon(Navi.getIcon("open"));
			tempitem.setMnemonic('0' + (i+1));
			menu_Sizes.add(tempitem);
			tempitem.addActionListener((ActionEvent e) ->
			{
				int index = getMenuItemIndex(menu_Sizes, e);
				oNavi.loadWindowPreset(oNavi.TheOptions.WINDOWPRESET_USER[index]);
				oNavi.TheBrowserWrapper.verifiedExecute(oNavi.TheOptions.JS_SIZE_USER[index]);
			});
		}
		
		menu_Sizes.addSeparator();
		
		for (int i = 0; i < oNavi.TheOptions.WINDOWPRESET_USER.length; i++)
		{
			tempitem = new JMenuItem(oNavi.TheTranslations.get("Save") + " #" + (i+1));
			tempitem.setIcon(Navi.getIcon("save"));
			menu_Sizes.add(tempitem);
			tempitem.addActionListener((ActionEvent e) ->
			{
				// index needs to subtract the upper "Load" items and the separator item
				int index = getMenuItemIndex(menu_Sizes, e) - oNavi.TheOptions.WINDOWPRESET_USER.length - 1;
				oNavi.saveWindowPreset(index);
			});
		}
		
		
		// Menu items for "Colors" menu
		//------------------------------------------------------------------
		// Load Theme items
		for (int i = 0; i < oNavi.TheOptions.COLORPRESET_USER.length; i++)
		{
			tempitem = new JMenuItem(oNavi.TheTranslations.get("Load", "Theme") + " #" + (i+1));
			tempitem.setIcon(Navi.getIcon("open"));
			tempitem.setMnemonic('0' + (i+1));
			menu_Colors.add(tempitem);
			tempitem.addActionListener((ActionEvent e) ->
			{
				int index = getMenuItemIndex(menu_Colors, e);
				oNavi.loadColorPreset(oNavi.TheOptions.COLORPRESET_USER[index]);
			});
		}
		
		// Set individual component color items
		JMenuItem item_ColorBarFocused = new JMenuItem(oNavi.TheTranslations.get("Set", "Bar", "Focused"));
		JMenuItem item_ColorBarUnfocused = new JMenuItem(oNavi.TheTranslations.get("Set", "Bar", "Not", "Focused"));
		JMenuItem item_ColorBorderFocused = new JMenuItem(oNavi.TheTranslations.get("Set", "Border", "Focused"));
		JMenuItem item_ColorBorderUnfocused = new JMenuItem(oNavi.TheTranslations.get("Set", "Border", "Not", "Focused"));
		
		item_ColorBarFocused.setIcon(Navi.getIcon("colorset"));
		item_ColorBarUnfocused.setIcon(Navi.getIcon("colorset"));
		item_ColorBorderFocused.setIcon(Navi.getIcon("colorset"));
		item_ColorBorderUnfocused.setIcon(Navi.getIcon("colorset"));
		
		menu_Colors.addSeparator();
		menu_Colors.add(item_ColorBarFocused);
		menu_Colors.add(item_ColorBarUnfocused);
		menu_Colors.add(item_ColorBorderFocused);
		menu_Colors.add(item_ColorBorderUnfocused);
		
		item_ColorBarFocused.addActionListener((ActionEvent e) ->
		{
			Color selectedcolor = JColorChooser.showDialog(oNavi.TheFrame, "", oNavi.TheOptions.COLORPRESET_START.BarFocused);
			if (selectedcolor != null)
			{
				oNavi.TheOptions.COLORPRESET_START.BarFocused = selectedcolor;
				oNavi.TheOptions.set_COLORPRESET_START();
			}
		});
		item_ColorBarUnfocused.addActionListener((ActionEvent e) ->
		{
			Color selectedcolor = JColorChooser.showDialog(oNavi.TheFrame, "", oNavi.TheOptions.COLORPRESET_START.BarUnfocused);
			if (selectedcolor != null)
			{
				oNavi.TheOptions.COLORPRESET_START.BarUnfocused = selectedcolor;
				oNavi.TheOptions.set_COLORPRESET_START();
			}
		});
		item_ColorBorderFocused.addActionListener((ActionEvent e) ->
		{
			Color selectedcolor = JColorChooser.showDialog(oNavi.TheFrame, "", oNavi.TheOptions.COLORPRESET_START.BorderFocused);
			if (selectedcolor != null)
			{
				oNavi.TheOptions.COLORPRESET_START.BorderFocused = selectedcolor;
				oNavi.TheOptions.set_COLORPRESET_START();
			}
		});
		item_ColorBorderUnfocused.addActionListener((ActionEvent e) ->
		{
			Color selectedcolor = JColorChooser.showDialog(oNavi.TheFrame, "", oNavi.TheOptions.COLORPRESET_START.BorderUnfocused);
			if (selectedcolor != null)
			{
				oNavi.TheOptions.COLORPRESET_START.BorderUnfocused = selectedcolor;
				oNavi.TheOptions.set_COLORPRESET_START();
			}
		});
		
		menu_Colors.addSeparator();
		
		// Save Theme items
		for (int i = 0; i < oNavi.TheOptions.COLORPRESET_USER.length; i++)
		{
			tempitem = new JMenuItem(oNavi.TheTranslations.get("Save", "Theme") + " #" + (i+1));
			tempitem.setIcon(Navi.getIcon("save"));
			menu_Colors.add(tempitem);
			tempitem.addActionListener((ActionEvent e) ->
			{
				// index needs to subtract the upper "Load" items, "Set" items, and the separator items
				int index = getMenuItemIndex(menu_Colors, e) - oNavi.TheOptions.COLORPRESET_USER.length - (4+2);
				oNavi.TheOptions.set_COLORPRESET_USER(index);
			});
		}
		
		// Menu items for "Border" menu
		//------------------------------------------------------------------
		int maxborderpixels = 2;
		for (int i = 0; i <= maxborderpixels; i++)
		{
			tempitem = new JMenuItem(i + " " + oNavi.TheTranslations.get("Pixel"));
			tempitem.setIcon(Navi.getIcon("pip"));
			tempitem.setMnemonic('0' + i);
			menu_Border.add(tempitem);
			tempitem.addActionListener((ActionEvent e) ->
			{
				int index = getMenuItemIndex(menu_Border, e);
				oNavi.resizeByThickness(index);
				oNavi.TheOptions.set_BORDER_THICKNESS(index);
				oNavi.sumDimensions();
				oNavi.doFrameFocus();
			});
		}
		// Unfocused Opacity list
		createOpacityList(OpacityType.WindowUnfocused, oNavi.TheBarPopup);
	}
	
	/**
	 * Creates the taskbar tray icon and its context menu.
	 */
	private void createTray()
	{
		// Tray icon menu, reuses menu items previously created
		//------------------------------------------------------------------
		// Tray icon setup
		URL trayiconurl = System.class.getResource("/img/" + (isProjection ? "tray_projection" : "tray_program") + ".png");
		Image icon = Toolkit.getDefaultToolkit().getImage(trayiconurl);
		oNavi.TheTrayIcon = new TrayIcon(icon, (isProjection ? Navi.PROGRAM_NAME_PROJECTION : Navi.PROGRAM_NAME));
		oNavi.TheTray = SystemTray.getSystemTray();

		// Custom tray popup menu
		JPopupMenu TrayPopup = new JPopupMenu();
		JMenu tmenu_Opacity = new JMenu(oNavi.TheTranslations.get("Opacity"));
		JMenu tmenu_OpacityFocused = new JMenu(oNavi.TheTranslations.get("Opacity", "Focused"));
		JMenu tmenu_Window = new JMenu(oNavi.TheTranslations.get("Window"));
		JMenu tmenu_Navigation = new JMenu(oNavi.TheTranslations.get("Navigation"));
		JMenuItem titem_NewWindow = new JMenuItem(oNavi.TheTranslations.get("New", "Window"));
		JMenuItem titem_Reload = new JMenuItem(oNavi.TheTranslations.get("Reload"));
		JMenuItem titem_Log = new JMenuItem(oNavi.TheTranslations.get("Log"));
		JMenuItem titem_Home = new JMenuItem(oNavi.TheTranslations.get("Homepage"));
		JMenuItem titem_Update = new JMenuItem(oNavi.TheTranslations.get("Update"));
		JMenuItem titem_About = new JMenuItem(oNavi.TheTranslations.get("About"));
		JMenuItem titem_ShowWindow = new JMenuItem(oNavi.TheTranslations.get("Show", "Window"));
		JMenuItem titem_Windowed = new JMenuItem(oNavi.TheTranslations.get("Windowed"));
		JMenuItem titem_Maximize = new JMenuItem(oNavi.TheTranslations.get("Maximize"));
		JMenuItem titem_AlwaysOnTop = new JMenuItem(oNavi.TheTranslations.get("AlwaysOnTop"));
		JMenuItem titem_OpaqueOnFocus = new JCheckBoxMenuItem(oNavi.TheTranslations.get("Enable", "Focused"));
		JMenuItem titem_Minimize = new JMenuItem(oNavi.TheTranslations.get("Minimize"));
		JMenuItem titem_MinimizeToTray = new JMenuItem(oNavi.TheTranslations.get("Minimize", "Tray"));
		JMenuItem titem_Miniaturize = new JMenuItem(oNavi.TheTranslations.get("Miniaturize"));
		JMenuItem titem_AlignKnob = new JMenuItem(oNavi.TheTranslations.get("Align", "Knob"));
		JMenuItem titem_ZoomIn = new JMenuItem(oNavi.TheTranslations.get("Zoom") + " +");
		JMenuItem titem_ZoomDefault = new JMenuItem(oNavi.TheTranslations.get("Zoom") + " 100%");
		JMenuItem titem_ZoomOut = new JMenuItem(oNavi.TheTranslations.get("Zoom") + " −");
		JMenuItem titem_Exit = new JMenuItem(oNavi.TheTranslations.get("Exit"));
		JMenuItem titem_ExitTray = new JMenuItem(oNavi.TheTranslations.get("Exit"));
		
		tmenu_Opacity.setIcon(Navi.getIcon("opacity"));
		tmenu_OpacityFocused.setIcon(Navi.getIcon("opacity"));
		titem_NewWindow.setIcon(Navi.getIcon("newwindow"));
		tmenu_Window.setIcon(Navi.getIcon("window"));
		tmenu_Navigation.setIcon(Navi.getIcon("navigation"));
		titem_Reload.setIcon(Navi.getIcon("br_reload"));
		titem_Log.setIcon(Navi.getIcon("log"));
		titem_Home.setIcon(Navi.getIcon("home"));
		titem_Update.setIcon(Navi.getIcon("update"));
		titem_About.setIcon(Navi.getIcon("about"));
		titem_ShowWindow.setIcon(Navi.getIcon("window"));
		titem_Windowed.setIcon(Navi.getIcon("presets"));
		titem_Maximize.setIcon(Navi.getIcon((isProjection) ? "projection" : "maximize"));
		titem_AlwaysOnTop.setIcon(Navi.getIcon("alwaysontop"));
		titem_Minimize.setIcon(Navi.getIcon("minimizetotray"));
		titem_MinimizeToTray.setIcon(Navi.getIcon("minimizetotray"));
		titem_Miniaturize.setIcon(Navi.getIcon("minimize"));
		titem_AlignKnob.setIcon(Navi.getIcon("align"));
		titem_ZoomIn.setIcon(Navi.getIcon("br_zoomin"));
		titem_ZoomDefault.setIcon(Navi.getIcon("br_zoomdefault"));
		titem_ZoomOut.setIcon(Navi.getIcon("br_zoomout"));
		titem_Exit.setIcon(Navi.getIcon("exit"));
		titem_ExitTray.setIcon(Navi.getIcon("exit"));
		
		tmenu_Opacity.setMnemonic(KeyEvent.VK_A);
		tmenu_OpacityFocused.setMnemonic(KeyEvent.VK_F);
		tmenu_Window.setMnemonic(KeyEvent.VK_I);
		tmenu_Navigation.setMnemonic(KeyEvent.VK_N);
		titem_NewWindow.setMnemonic(KeyEvent.VK_W);
		titem_Reload.setMnemonic(KeyEvent.VK_R);
		titem_Log.setMnemonic(KeyEvent.VK_L);
		titem_Home.setMnemonic(KeyEvent.VK_H);
		titem_Update.setMnemonic(KeyEvent.VK_U);
		titem_About.setMnemonic(KeyEvent.VK_B);
		titem_ShowWindow.setMnemonic(KeyEvent.VK_W);
		titem_Windowed.setMnemonic(KeyEvent.VK_D);
		titem_Maximize.setMnemonic(KeyEvent.VK_E);
		titem_AlwaysOnTop.setMnemonic(KeyEvent.VK_Y);
		titem_OpaqueOnFocus.setMnemonic(KeyEvent.VK_F);
		titem_Minimize.setMnemonic(KeyEvent.VK_M);
		titem_MinimizeToTray.setMnemonic(KeyEvent.VK_T);
		titem_Miniaturize.setMnemonic(KeyEvent.VK_Z);
		titem_AlignKnob.setMnemonic(KeyEvent.VK_G);
		titem_Exit.setMnemonic(KeyEvent.VK_X);
		titem_ExitTray.setMnemonic(KeyEvent.VK_X);

		if (isProjection)
		{
			JMenu tmenu_Options = new JMenu(oNavi.TheTranslations.get("Options"));
			tmenu_Options.setIcon(Navi.getIcon("options"));
			// Projection opacity focused submenu
			tmenu_Options.add(tmenu_OpacityFocused);
			tmenu_OpacityFocused.add(titem_OpaqueOnFocus);
			titem_OpaqueOnFocus.setSelected(oNavi.TheOptions.wantProjectionOpacityOnFocus);
			createOpacityList(OpacityType.ProjectionFocused, tmenu_OpacityFocused);
			tmenu_Options.addSeparator();
			// Options submenu exclusively for projection
			tmenu_Options.setMnemonic(KeyEvent.VK_S);
			tmenu_Options.add(item_EnableKnobMoveable);
			tmenu_Options.add(item_EnableKnobBig);
			tmenu_Options.addSeparator();
			tmenu_Options.add(item_EnableGPS);
			tmenu_Options.addSeparator();
			tmenu_Options.add(menu_Language);
			
			// The knob is the main way of interacting with the overlay frame
			oNavi.TheKnobPopup = new JPopupMenu();
			oNavi.TheKnobPopup.add(tmenu_Opacity);
			oNavi.TheKnobPopup.add(tmenu_Window);
			oNavi.TheKnobPopup.add(menu_Cursor);
			oNavi.TheKnobPopup.addSeparator();
			oNavi.TheKnobPopup.add(titem_NewWindow);
			oNavi.TheKnobPopup.add(titem_Reload);
			oNavi.TheKnobPopup.add(titem_Log);
			oNavi.TheKnobPopup.addSeparator();
			oNavi.TheKnobPopup.add(titem_Home);
			oNavi.TheKnobPopup.add(titem_Update);
			oNavi.TheKnobPopup.add(titem_About);
			oNavi.TheKnobPopup.addSeparator();
			oNavi.TheKnobPopup.add(tmenu_Options);
			oNavi.TheKnobPopup.addSeparator();
			oNavi.TheKnobPopup.add(titem_Exit);
			createOpacityList(OpacityType.ProjectionUnfocused, tmenu_Opacity);
			
			// Knob submenu
			tmenu_Window.add(titem_Windowed);
			tmenu_Window.add(titem_Maximize);
			tmenu_Window.addSeparator();
			tmenu_Window.add(titem_Minimize);
			tmenu_Window.add(titem_MinimizeToTray);
			tmenu_Window.add(titem_AlignKnob);
			tmenu_Window.addSeparator();
			tmenu_Window.add(titem_ZoomIn);
			tmenu_Window.add(titem_ZoomDefault);
			tmenu_Window.add(titem_ZoomOut);
			
			// Opacity on Focus option
			titem_OpaqueOnFocus.addItemListener((ItemEvent e) ->
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					oNavi.TheOptions.set_wantProjectionOpacityOnFocus(true);
					if (oNavi.TheProjection.isFocused())
					{
						oNavi.TheProjection.setOpacity(oNavi.TheOptions.PROJECTION_OPACITY_FOCUSED);
					}
				}
				else
				{
					oNavi.TheOptions.set_wantProjectionOpacityOnFocus(false);
					oNavi.TheProjection.setOpacity(oNavi.TheOptions.PROJECTION_OPACITY_UNFOCUSED);
				}
			});
			
			// Tray menu actions are limited because the knob contains those actions already
			TrayPopup.add(titem_ShowWindow);
			TrayPopup.addSeparator();
			TrayPopup.add(titem_ExitTray);
		}
		else
		{
			// For window, put essential window manipulation actions here
			TrayPopup.add(titem_ShowWindow);
			TrayPopup.add(titem_Maximize);
			TrayPopup.add(titem_AlwaysOnTop);
			TrayPopup.addSeparator();
			TrayPopup.add(titem_MinimizeToTray);
			TrayPopup.add(titem_Miniaturize);
			TrayPopup.addSeparator();
			TrayPopup.add(titem_ExitTray);
		}
		
		titem_NewWindow.addActionListener((ActionEvent e) ->
		{
			oNavi.openNewWindow();
		});
		titem_Reload.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.reloadIgnoreCache();
		});
		titem_Log.addActionListener((ActionEvent e) ->
		{
			oNavi.showLog();
		});
		titem_Home.addActionListener((ActionEvent e) ->
		{
			String homeurl = (isProjection) ? oNavi.TheOptions.URL_PROJECTION : oNavi.TheOptions.URL_HOMEPAGE;
			oNavi.TheBrowser.loadURL(homeurl);
		});
		titem_Update.addActionListener((ActionEvent e) ->
		{
			try
			{
				Desktop.getDesktop().browse(new URI(oNavi.TheOptions.URL_UPDATE));
			}
			catch (IOException | URISyntaxException ex) {}
		});
		titem_About.addActionListener((ActionEvent e) ->
		{
			// This about window is only accessed from projection
			JOptionPane.showMessageDialog(oNavi.TheProjection,
					new JLabel(oNavi.TEXT_ABOUT_PROJECTION),
					oNavi.TheTranslations.get("About"),
					JOptionPane.INFORMATION_MESSAGE,
					Navi.getIcon("task_program")
			);
		});
		titem_ShowWindow.addActionListener((ActionEvent e) ->
		{
			oNavi.toggleFrame(true);
		});
		titem_Windowed.addActionListener((ActionEvent e) ->
		{
			oNavi.toggleProjectionMaximize(false);
		});
		titem_Maximize.addActionListener((ActionEvent e) ->
		{
			if (isProjection)
			{
				oNavi.toggleProjectionMaximize(true);
			}
			else
			{
				oNavi.toggleFrameMaximize();
			}
		});
		titem_AlwaysOnTop.addActionListener((ActionEvent e) ->
		{
			item_AlwaysOnTop.doClick();
		});
		titem_Minimize.addActionListener((ActionEvent e) ->
		{
			if (isProjection)
			{
				oNavi.TheKnob.setVisible(false);
			}
			oNavi.toggleFrame(false);
		});
		titem_MinimizeToTray.addActionListener((ActionEvent e) ->
		{
			if (isProjection)
			{
				oNavi.TheKnob.setVisible(false);
				oNavi.TheProjection.setVisible(false);
			}
			oNavi.toggleFrame(false);
		});
		titem_Miniaturize.addActionListener((ActionEvent e) ->
		{
			oNavi.miniaturizeFrame();
		});
		titem_AlignKnob.addActionListener((ActionEvent e) ->
		{
			oNavi.toggleFrame(true);
			oNavi.TheKnob.alignKnob();
		});
		titem_ZoomIn.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.setZoomLevel(++oNavi.TheOptions.PROJECTION_ZOOM_LEVEL);
		});
		titem_ZoomDefault.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.setZoomLevel(oNavi.TheOptions.PROJECTION_ZOOM_LEVEL = oNavi.TheBrowserWrapper.ZOOM_LEVEL_DEFAULT);
		});
		titem_ZoomOut.addActionListener((ActionEvent e) ->
		{
			oNavi.TheBrowser.setZoomLevel(--oNavi.TheOptions.PROJECTION_ZOOM_LEVEL);
		});
		titem_Exit.addActionListener((ActionEvent e) ->
		{
			oNavi.doExit();
		});
		titem_ExitTray.addActionListener((ActionEvent e) ->
		{
			oNavi.doExit();
		});
		
		// Tray helper to remove the popup after user click outside of it
		// Source by Michael Plautz from: http://stackoverflow.com/questions/19868209/cannot-hide-systemtray-jpopupmenu-when-it-loses-focus/20079304#20079304
		TrayPopupHelper = new JDialog();
		TrayPopupHelper.setUndecorated(true);
		TrayPopupHelper.addWindowFocusListener(new WindowFocusListener()
		{
			@Override
			public void windowLostFocus(WindowEvent e)
			{
				TrayPopupHelper.setVisible(false);
			}
			@Override
			public void windowGainedFocus(WindowEvent e) {}
		});

		// Tray icon behavior
		oNavi.TheTrayIcon.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					oNavi.toggleFrame();
				}
			}
			@Override
            public void mouseReleased(MouseEvent e)
			{
                if (e.isPopupTrigger())
				{
                    TrayPopup.setLocation(e.getX(), e.getY());
                    TrayPopupHelper.setLocation(e.getX(), e.getY());
                    TrayPopup.setInvoker(TrayPopupHelper);
                    TrayPopupHelper.setVisible(true);
                    TrayPopup.setVisible(true);
                }
            }
		});

		// Create the tray icon
		try
		{
			oNavi.TheTray.add(oNavi.TheTrayIcon);
		}
		catch (AWTException e)
		{
			oNavi.addLog("TrayIcon could not be added.");
		}
	}
	
	/**
	 * Creates menu items representing each selectable opacity level.
	 * @param pIsFocused for the possible lists.
	 * @param pList menu to insert into.
	 */
	private void createOpacityList(OpacityType pType, JComponent pList)
	{
		JRadioButtonMenuItem tempradioitem;
		ButtonGroup group_Opacity = new ButtonGroup();
		final ArrayList<JRadioButtonMenuItem> item_OpacityArraylist = new ArrayList();

		for (int i = 0; i < oNavi.OPACITY_LEVELS_10; i++)
		{
			tempradioitem = new JRadioButtonMenuItem(Integer.toString(oNavi.OPACITY_LEVELS_10 * (oNavi.OPACITY_LEVELS_10 - i)) + "%");
			if (i == 0)
			{
				tempradioitem.setMnemonic('0');
			}
			else
			{
				tempradioitem.setMnemonic(('9' - i) + 1);
			}

			tempradioitem.addActionListener((ActionEvent e) ->
			{
				float selectedopacity;
				for (int i1 = 0; i1 < oNavi.OPACITY_LEVELS_10; i1++)
				{
					if (item_OpacityArraylist.get(i1).isSelected())
					{
						selectedopacity = (oNavi.OPACITY_LEVELS_10 - i1) * oNavi.OPACITY_STEP;
						switch (pType)
						{
							case WindowFocused:
							{
								oNavi.TheOptions.set_OPACITY_FOCUSED(selectedopacity);
								oNavi.TheFrame.setOpacity(oNavi.TheOptions.OPACITY_FOCUSED);
							} break;
							case WindowUnfocused:
							{
								oNavi.TheOptions.set_OPACITY_UNFOCUSED(selectedopacity);
								oNavi.TheFrame.setOpacity(oNavi.TheOptions.OPACITY_UNFOCUSED);
							} break;
							case ProjectionFocused:
							{
								oNavi.TheOptions.set_PROJECTION_OPACITY_FOCUSED(selectedopacity);
								oNavi.TheProjection.setOpacity(oNavi.TheOptions.PROJECTION_OPACITY_FOCUSED);
							} break;
							case ProjectionUnfocused:
							{
								oNavi.TheOptions.set_PROJECTION_OPACITY_UNFOCUSED(selectedopacity);
								oNavi.TheProjection.setOpacity(oNavi.TheOptions.PROJECTION_OPACITY_UNFOCUSED);
							} break;
						}
					}
				}
			});

			group_Opacity.add(tempradioitem);
			item_OpacityArraylist.add(tempradioitem);
			pList.add(tempradioitem);
		}
		float opacityselected = 1;
		switch (pType)
		{
			case WindowFocused:
			{
				opacityselected = oNavi.TheOptions.OPACITY_FOCUSED;
			} break;
			case WindowUnfocused:
			{
				opacityselected = oNavi.TheOptions.OPACITY_UNFOCUSED;
			} break;
			case ProjectionFocused:
			{
				opacityselected = oNavi.TheOptions.PROJECTION_OPACITY_FOCUSED;
			} break;
			case ProjectionUnfocused:
			{
				opacityselected = oNavi.TheOptions.PROJECTION_OPACITY_UNFOCUSED;
			} break;
		}
		item_OpacityArraylist.get(oNavi.OPACITY_LEVELS_10 - (int)(opacityselected * oNavi.OPACITY_LEVELS_10)).setSelected(true);
	}
	
	/**
	 * Opacity affect type for creating opacity list.
	 */
	public enum OpacityType
	{
		WindowFocused,
		WindowUnfocused,
		ProjectionFocused,
		ProjectionUnfocused
	}

	/**
	 * Extended class to recolor the menu bar, which itself is inside a JPanel.
	 */
	private class CustomMenu extends JMenuBar implements MouseMotionListener
	{
		public CustomMenu()
		{
			addMouseMotionListener(this);
		}

		/**
		 * Creates a rectangle of set color variable over the menu bar such that
		 * it becomes its "background". This method is automatically called when
		 * the menu bar is constructed or hovered on.
		 * @param g 
		 */
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Navi.COLOR_BAR_CURRENT);
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}
		@Override
		public void mouseDragged(MouseEvent e)
		{
			Point currCoords = e.getLocationOnScreen();
			oNavi.TheFrame.setLocation(currCoords.x - mousePressedPoint.x, currCoords.y - mousePressedPoint.y);
		}
		@Override
		public void mouseMoved(MouseEvent e) {}
	}
	
	/**
	 * Listener class for menu bar popup.
	 */
	class PopupListener extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				oNavi.TheBarPopup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	/**
	 * Gets the top to bottom index of menu item inside a menu.
	 * @param pMenu container.
	 * @param pEvent that the menu item triggered.
	 * @return index of the menu item.
	 */
	private int getMenuItemIndex(JMenu pMenu, ActionEvent pEvent)
	{
		JMenuItem tempitem;
		for (int i = 0; i < pMenu.getItemCount(); i++)
		{
			tempitem = pMenu.getItem(i);
			// Ignore separators, which returns null from getItem
			if (tempitem != null)
			{
				if (tempitem.equals(((JMenuItem) pEvent.getSource())))
				{
					return i;
				}
			}
		}
		return -1;
	}
}
