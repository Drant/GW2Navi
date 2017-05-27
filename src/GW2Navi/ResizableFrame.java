package GW2Navi;

/**
 * ResizableFrame.java from code by Iovcev Elena. Constructs a JFrame that is
 * undecorated without an OS' usual GUI elements outside the frame, and is
 * resizable on the edges and moveable from a custom title bar.
 * Source: http://www.coderanch.com/t/415944/GUI/java/user-ve-undecorated-window-resizable
 */
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.*;
 
public class ResizableFrame extends JFrame implements MouseMotionListener, MouseListener
{
	Dimension screenUnbounded = Toolkit.getDefaultToolkit().getScreenSize(); // Fullscreen
	Rectangle screenBounded = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds(); // Subtracts the taskbar
	int screenBoundedWidth = (int) screenBounded.getWidth();
	int screenBoundedHeight = (int) screenBounded.getHeight();
	int screenUnboundedWidth = (int) screenUnbounded.getWidth();
	int screenUnboundedHeight = (int) screenUnbounded.getHeight();
	final Point initialLocation;
	Point startingDragPoint;
	Point startingDragLocation;
	Point previousLocation = new Point(0, 0);
	int previousWidth = (int) (screenBoundedWidth / 2);
	int previousHeight = (int) (screenBoundedHeight / 2);
	int minWidth;
	int minHeight;
	
	int cursorAreaSides = 2; // Grabbable area width
	int cursorAreaCorners = 4;
	FrameCursor cursors = new FrameCursor();
	
	// Constructor
	public ResizableFrame(Dimension initialDimension, Dimension minimumDimension, Point initialLocation)
	{
		this.initialLocation = initialLocation;
		minWidth = (int) minimumDimension.getWidth();
		minHeight = (int) minimumDimension.getHeight();
		initializeFrame((int) initialDimension.getWidth(), (int) initialDimension.getHeight());
	}
	
	/**
	 * Initializes the frame.
	 * @param pIniWidth
	 * @param pIniHeight 
	 */
	private void initializeFrame(int pIniWidth, int pIniHeight)
	{
		addMouseMotionListener(this);
		addMouseListener(this);
		this.setSize(pIniWidth, pIniHeight);
		
		setLocation(initialLocation);
		setUndecorated(true);
	}
	
	/**
	 * Setter for minimum size property.
	 * @param pMinWidth
	 * @param pMinHeight 
	 */
	public void setMinimumSize(int pMinWidth, int pMinHeight)
	{
		minWidth = pMinWidth;
		minHeight = pMinHeight;
	}

	/**
	 * Gets the top left corner coordinate of the frame's position.
	 * @param e
	 * @param frame
	 * @return point.
	 */
	public static Point getScreenLocation(MouseEvent e, JFrame frame) 
	{ 
		Point cursor = e.getPoint();
		Point view_location = frame.getLocationOnScreen();
		return new Point(
			(int) (view_location.getX() + cursor.getX()), 
			(int) (view_location.getY() + cursor.getY())
		);
	}
	
	// Frame bindings for move and resize
	@Override
	public void mouseDragged(MouseEvent e)
	{
		moveOrFullResizeFrame(e);
	}
	@Override
	public void mouseMoved(MouseEvent e)
	{
		Point cursorLocation = e.getPoint();
		int xPos = cursorLocation.x;
		int yPos = cursorLocation.y;
				
		// Corner areas are in first order of conditionals so they have higher priority
		if (xPos <= cursorAreaCorners && yPos <= cursorAreaCorners)
		{
			setCursor(cursors.RESIZE_NW);
		}
		else if (xPos >= getWidth() - cursorAreaCorners && yPos <= cursorAreaCorners)
		{
			setCursor(cursors.RESIZE_NE);
		}
		else if (xPos >= getWidth() - cursorAreaCorners && yPos >= getHeight() - cursorAreaCorners)
		{
			setCursor(cursors.RESIZE_SE);
		}
		else if (xPos <= cursorAreaCorners && yPos >= getHeight() - cursorAreaCorners)
		{
			setCursor(cursors.RESIZE_SW);
		}
		// Side areas lower priority than corner areas
		else if (xPos >= cursorAreaSides && xPos <= getWidth() - cursorAreaSides && yPos >= getHeight() - cursorAreaSides)
		{
			setCursor(cursors.RESIZE_S);
		}
		else if (xPos >= getWidth() - cursorAreaSides && yPos >= cursorAreaSides && yPos <= getHeight() - cursorAreaSides)
		{
			setCursor(cursors.RESIZE_E);
		}
		else if (xPos <= cursorAreaSides && yPos >= cursorAreaSides && yPos <= getHeight() - cursorAreaSides)
		{
			setCursor(cursors.RESIZE_W);
		}
		else if (xPos >= cursorAreaSides && xPos <= getWidth() - cursorAreaSides && yPos <= cursorAreaSides)
		{
			setCursor(cursors.RESIZE_N);
		}
		else
		{
			setCursor(cursors.NORMAL);
		}
	}
	@Override
	public void mouseClicked(MouseEvent e)
	{
		Object sourceObject = e.getSource();
		if (sourceObject instanceof JPanel)
		{
			// Double click using the left mouse button
			if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
			{
				if (getCursor().equals(cursors.NORMAL))
				{
					headerDoubleClickResize();
				}
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e)
	{
		this.startingDragPoint = getScreenLocation(e, this);
		this.startingDragLocation = this.getLocation();
		
		if (getWidth() < screenBoundedWidth || getHeight() < screenBoundedHeight) 
		{
			previousLocation = this.getLocation();
			previousWidth = getWidth();
			previousHeight = getHeight();
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	/**
	 * Does appropriate actions depending on user's mouse interaction with frame.
	 * @param e 
	 */
	public void moveOrFullResizeFrame(MouseEvent e)
	{
		Object sourceObject = e.getSource();
		Point current = getScreenLocation(e, this);
		Point offset = null;
		try
		{
			offset = new Point((int) current.getX() - (int) startingDragPoint.getX(), (int) current.getY() - (int) startingDragPoint.getY());
		}
		catch (NullPointerException ex)
		{
			return;
		}
		
		if (sourceObject instanceof JPanel && getCursor().equals(cursors.NORMAL))
		{
			setLocation((int) (startingDragLocation.getX() + offset.getX()), (int) (startingDragLocation.getY() + offset.getY()));
		}
		else if (!getCursor().equals(cursors.NORMAL))
		{
			int oldLocationX = (int) getLocation().getX();
			int oldLocationY = (int) getLocation().getY();
			int newLocationX = (int) (this.startingDragLocation.getX() + offset.getX());
			int newLocationY = (int) (this.startingDragLocation.getY() + offset.getY());
			boolean N_Resize = getCursor().equals(cursors.RESIZE_N);
			boolean NE_Resize = getCursor().equals(cursors.RESIZE_NE);
			boolean NW_Resize = getCursor().equals(cursors.RESIZE_NW);
			boolean E_Resize = getCursor().equals(cursors.RESIZE_E);
			boolean W_Resize = getCursor().equals(cursors.RESIZE_W);
			boolean S_Resize = getCursor().equals(cursors.RESIZE_S);
			boolean SW_Resize = getCursor().equals(cursors.RESIZE_SW);
			boolean wantSetLocation = false;
			int newWidth = e.getX();
			int newHeight = e.getY();
					
			if (NE_Resize)
			{
				newHeight = getHeight() - (newLocationY - oldLocationY);
				newLocationX = (int) getLocation().getX();
				wantSetLocation = true;
			}
			else if (E_Resize)
			{
				newHeight = getHeight();
			}
			else if (S_Resize)
			{
				newWidth = getWidth();
			}	
			else if (N_Resize)
			{
				newLocationX = (int) getLocation().getX();
				newWidth = getWidth();
				newHeight = getHeight() - (newLocationY - oldLocationY);
				wantSetLocation = true;
			}
			else if (NW_Resize)
			{
				newWidth = getWidth() - (newLocationX - oldLocationX);
				newHeight = getHeight() - (newLocationY - oldLocationY);
				wantSetLocation = true;
			}
			else if (NE_Resize)
			{
				newHeight = getHeight() - (newLocationY - oldLocationY);
				newLocationX = (int) getLocation().getX();
			}
			else if (SW_Resize)
			{
				newWidth = getWidth() - (newLocationX - oldLocationX);
				newLocationY = (int) getLocation().getY();
				wantSetLocation = true;
			}
			else if (W_Resize)
			{
				newWidth = getWidth() - (newLocationX - oldLocationX);
				newLocationY = (int) getLocation().getY();
				newHeight = getHeight();
				wantSetLocation = true;
			}
			
			if (newWidth >= screenUnboundedWidth || newWidth <= minWidth)
			{
				newLocationX = oldLocationX;
				newWidth = getWidth();
			}
			
			if (newHeight >= screenUnboundedHeight || newHeight <= minHeight)
			{
				newLocationY = oldLocationY;
				newHeight = getHeight();
			}
			
			if (newWidth != getWidth() || newHeight != getHeight())
			{
				this.setSize(newWidth, newHeight);
							
				if (wantSetLocation)
				{
					this.setLocation(newLocationX, newLocationY);
				}
			}
		}
	}
	
	/**
	 * Tells if the frame is maximized checking its size.
	 * @return boolean.
	 */
	public boolean isMaximized()
	{
		return (getWidth() >= screenBoundedWidth && getHeight() >= screenBoundedHeight);
	}
	
	/**
	 * Sets the frame to be the same size as the screen (ignores taskbar).
	 */
	public void setFullscreen()
	{
		this.setLocation(0, 0);
		this.setSize(screenUnboundedWidth, screenUnboundedHeight);
	}
	
	/**
	 * Mimics the double click window bar behavior, which maximizes/restores the frame.
	 */
	public void headerDoubleClickResize()
	{
		if (isMaximized()) 
		{
			this.setSize(previousWidth, previousHeight);
			this.setLocation(previousLocation);
		}
		else
		{
			setFullscreen();
		}
	}
}
