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
	
	Option TheOptions;
	JFrame TheProjection;
	Navi oNavi;
	
	JLabel IconContainer;
	ImageIcon Icon;
	int width;
	int height;
	int X;
	int Y;
	int state = 0;
	boolean isMoveable;
	
	/**
	 * Constructor
	 * @param pNavi 
	 */
	public ProjectionKnob(Navi pNavi)
	{
		oNavi = pNavi;
		TheOptions = pNavi.TheOptions;
		TheProjection = pNavi.TheProjection;
		isMoveable = TheOptions.wantKnobMoveable;
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
		if (TheOptions.WINDOWPRESET_KNOB.PosX == 0 && TheOptions.WINDOWPRESET_KNOB.PosY == 0)
		{
			// Move to default position
			alignKnob();
		}
		else
		{
			// Move to previously saved position
			this.setLocation(TheOptions.WINDOWPRESET_KNOB.PosX, TheOptions.WINDOWPRESET_KNOB.PosY);
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
		width = TheOptions.wantKnobBig ? TheOptions.WINDOWPRESET_KNOB_BIG.Width : TheOptions.WINDOWPRESET_KNOB.Width;
		height = TheOptions.wantKnobBig ? TheOptions.WINDOWPRESET_KNOB_BIG.Height : TheOptions.WINDOWPRESET_KNOB.Height;
		Icon = Navi.getIcon("knob", width, height);
		IconContainer.setIcon(Icon);
		this.setSize(width, height);
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		X = e.getX();
		Y = e.getY();
	}
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (isMoveable)
		{
			Point p = getLocation();
			setLocation(
				p.x + (e.getX() - X),
				p.y + (e.getY() - Y)
			);
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			switch (state)
			{
				case 0: // Shown
					updateKnobAppearance(1);
					TheProjection.setVisible(true);
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
	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			oNavi.TheKnobPopup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	@Override
	public void mouseEntered(MouseEvent e)
	{
		IconContainer.setIcon(Navi.getIcon("knob_hover", width, height));
	}
	@Override
	public void mouseExited(MouseEvent e)
	{
		IconContainer.setIcon(Icon);
	}
}
