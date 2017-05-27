package GW2Navi;

/**
 * ProjectionKnob.java creates and styles the main menu button for projection
 * mode. See Menu class for added actions.
 */

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;

public class ProjectionKnob extends JWindow implements MouseMotionListener, MouseListener {
	
	Navi oNavi;
	
	JLabel IconContainer;
	ImageIcon Icon;
	int width;
	int height;
	int knobClickX;
	int knobClickY;
	int knobPosX;
	int knobPosY;
	int projectionWidth;
	int projectionHeight;
	int projectionMinWidth;
	int projectionMinHeight;
	int state = 0;
	boolean isMoveable;
	
	/**
	 * Constructor
	 * @param pNavi 
	 */
	public ProjectionKnob(Navi pNavi)
	{
		oNavi = pNavi;
		isMoveable = oNavi.TheOptions.wantKnobMoveable;
		projectionMinWidth = oNavi.TheOptions.PROJECTION_MINIMUM.width;
		projectionMinHeight = oNavi.TheOptions.PROJECTION_MINIMUM.height;
		initializeKnob();
	}
	
	/**
	 * Sets initial appearance.
	 */
	private void initializeKnob()
	{
		addMouseMotionListener(this);
		addMouseListener(this);
		this.setCursor(new FrameCursor().NORMAL);
		this.add(IconContainer = new JLabel());
		this.resetSize();
		/**
		 * The position should be initially set to 0,0 in the options file, so
		 * this function can know to reposition the knob on first run.
		 */
		if (oNavi.TheOptions.WINDOWPRESET_KNOB.PosX == 0 && oNavi.TheOptions.WINDOWPRESET_KNOB.PosY == 0)
		{
			// Move to default position
			alignKnob();
		}
		else
		{
			// Move to previously saved position
			this.setLocation(oNavi.TheOptions.WINDOWPRESET_KNOB.PosX, oNavi.TheOptions.WINDOWPRESET_KNOB.PosY);
		}
		this.setAlwaysOnTop(true);
		this.setBackground(new Color(0, 0, 0, 0));
		this.setVisible(true);
	}
	
	/**
	 * Sets knob Icon image to visualize the overlay's state.
	 * @param pState 
	 */
	protected void updateKnobAppearance(int pState)
	{
		state = pState;
		switch (pState)
		{
			case 0:
				Icon = Navi.getIcon("knob", width, height);
				this.setOpacity(1);
				break;
			case 1:
				Icon = Navi.getIcon("knob", width, height);
				this.setOpacity((float) 0.5);
				break;
			case 2:
				Icon = Navi.getIcon("knob_inactive", width, height);
				this.setOpacity((float) 0.5);
				break;
		}
		IconContainer.setIcon(Icon);
	}
	
	/**
	 * Sets whether the knob is moveable.
	 * @param pIsMoveable 
	 */
	public void setMoveable(boolean pIsMoveable)
	{
		isMoveable = pIsMoveable;
	}
	
	/**
	 * Moves the knob to the default side position of the screen.
	 */
	public void alignKnob()
	{
		int leftX = 0;
		int leftY = oNavi.RESOLUTION_CENTER_Y - height;
		int rightX = oNavi.RESOLUTION_WIDTH - width;
		int rightY = oNavi.RESOLUTION_CENTER_Y - height;
		
		// If already centered on the left side, then alternate to the right side
		if (this.getLocation().x == leftX && this.getLocation().y == leftY)
		{
			this.setLocation(rightX, rightY);	
		}
		else
		{
			this.setLocation(leftX, leftY);	
		}
	}
	
	/**
	 * Resizes the knob's image and interactive size.
	 */
	public void resetSize()
	{
		width = oNavi.TheOptions.wantKnobBig ? oNavi.TheOptions.WINDOWPRESET_KNOB_BIG.Width : oNavi.TheOptions.WINDOWPRESET_KNOB.Width;
		height = oNavi.TheOptions.wantKnobBig ? oNavi.TheOptions.WINDOWPRESET_KNOB_BIG.Height : oNavi.TheOptions.WINDOWPRESET_KNOB.Height;
		Icon = Navi.getIcon("knob", width, height);
		IconContainer.setIcon(Icon);
		this.setSize(width, height);
	}
	
	@Override
	public void mousePressed(MouseEvent pEvent)
	{
		Point point = getLocation();
		knobPosX = point.x;
		knobPosY = point.y;
		knobClickX = pEvent.getX();
		knobClickY = pEvent.getY();
		projectionWidth = oNavi.TheProjection.getWidth();
		projectionHeight = oNavi.TheProjection.getHeight();
	}
	@Override
	public void mouseDragged(MouseEvent pEvent)
	{
		if (isMoveable)
		{
			Point point = getLocation();
			int diffX = (pEvent.getX() - knobClickX);
			int diffY = (pEvent.getY() - knobClickY);
			int projectionNewWidth;
			int projectionNewHeight;
			int knobNewX = point.x + diffX;
			int knobNewY = point.y + diffY;
			boolean wantKnobMove = true;
			boolean wantProjectionMove = true;
			
			// Shift-Drag moves the projection
			if (pEvent.isShiftDown())
			{
				oNavi.TheProjection.setLocation(point);
				// Remember the projection as windowed
				if (oNavi.TheOptions.wantProjectionMaximized)
				{
					oNavi.TheOptions.set_wantProjectionMaximized(false);
				}
			}
			// Ctrl-Drag resizes the projection
			else if (pEvent.isControlDown())
			{
				projectionNewWidth = projectionWidth - (point.x - knobPosX);
				projectionNewHeight = projectionHeight - (point.y - knobPosY);
				
				if (projectionNewWidth >= projectionMinWidth && projectionNewHeight >= projectionMinHeight)
				{
					oNavi.TheProjection.setSize(projectionNewWidth, projectionNewHeight);
					wantProjectionMove = true;
				}
				else if (projectionNewWidth >= projectionMinWidth)
				{
					oNavi.TheProjection.setSize(projectionNewWidth, oNavi.TheProjection.getHeight());
					wantProjectionMove = true;
					knobNewY = point.y;
				}
				else if (projectionNewHeight >= projectionMinHeight)
				{
					oNavi.TheProjection.setSize(oNavi.TheProjection.getWidth(), projectionNewHeight);
					wantProjectionMove = true;
					knobNewX = point.x;
				}
				else
				{
					wantKnobMove = false;
				}
				
				if (wantProjectionMove)
				{
					oNavi.TheProjection.setLocation(point);
				}
				// Remember the projection as windowed
				if (oNavi.TheOptions.wantProjectionMaximized)
				{
					oNavi.TheOptions.set_wantProjectionMaximized(false);
				}
			}
			if (wantKnobMove)
			{
				setLocation(knobNewX, knobNewY);
			}
		}
	}
	@Override
	public void mouseMoved(MouseEvent pEvent) {}
	@Override
	public void mouseClicked(MouseEvent pEvent)
	{
		if (pEvent.getButton() == MouseEvent.BUTTON1)
		{
			switch (state)
			{
				case 0: // Shown
					updateKnobAppearance(1);
					oNavi.TheProjection.setVisible(true);
					oNavi.setClickable(false);
					break;
				case 1: // Shown but clickthrough
					updateKnobAppearance(2);
					oNavi.toggleFrame(false);
					break;
				default: // Hidden
					updateKnobAppearance(0);
					oNavi.setClickable(true);
					oNavi.toggleFrame(true);
					break;
			}
		}
		this.setAlwaysOnTop(true);
	}
	@Override
	public void mouseReleased(MouseEvent pEvent)
	{
		if (pEvent.isPopupTrigger())
		{
			oNavi.TheKnobPopup.show(pEvent.getComponent(), pEvent.getX(), pEvent.getY());
		}
	}
	@Override
	public void mouseEntered(MouseEvent pEvent)
	{
		IconContainer.setIcon(Navi.getIcon("knob_hover", width, height));
	}
	@Override
	public void mouseExited(MouseEvent pEvent)
	{
		IconContainer.setIcon(Icon);
	}
}
