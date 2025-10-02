import java.awt.*;
import javax.swing.*;        

/**
 *  A simple Swing application that creates and displays a
 *  CardTable element.
 *
 *  @author Nicholas R. Howe
 *  @version CSC 112, 22 February 2006
 */
public class CardGame extends JApplet {
    /** card table object */
    public static final CardTable table = new CardTable();

    /**
     *  This method is called by the application version.
     */
    public void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("Sample GUI Application");
        try { frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	} catch (Exception e) {}

	// Add components
	createComponents(frame.getContentPane());

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     *  Both types of app call this to set up the GUI contents.
     *
     *  @param pane  The pane of the JFrame of JApplet
     */
    public void createComponents(Container pane) {
        // set up layout
	pane.add(table);
    }

    /** 
     *  This is the entry point for the applet version
     */
    public void init() {
        // Load card images
	Card.loadImages(table);

	//Execute a job on the event-dispatching thread:
	//creating this applet's GUI.
	try {
	    javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
		    public void run() {
                        // line below would create separate window
			//gui.createAndShowGUI();

                        // this line creates applet in browser window
                        createComponents(getContentPane());
		    }
		});
	} catch (Exception e) {
	    System.err.println("createGUI didn't successfully complete");
	}
    }

    /** 
     *  This is the entry point for the application version
     */
    public static void main(String[] args) {
        final CardGame GUI = new CardGame();

        // Load card images
	Card.loadImages(GUI.table);

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    GUI.createAndShowGUI();
		}
	    });
    }
}
