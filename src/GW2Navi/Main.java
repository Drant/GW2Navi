package GW2Navi;

/**
 * Main.java executes the program.
 */

import javax.swing.SwingUtilities;

public class Main {
	
	public static void main(String args[])
	{
		// Run program, usage note: http://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					boolean want3D = args.length > 0 && args[0].equals("-3d");
					Navi navi = new Navi(want3D);
				}
				catch (InterruptedException ex) {}
			}
		});
	}
}
