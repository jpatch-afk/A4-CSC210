import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 *  This class implements a graphical canvas in which card 
 *  piles are placed.  It will also contain a nested listener class
 *  to respond to and handle mouse events.
 *
 *  The canvas is large enough to contain five rows of cards.
 *  Each row has its associated fixed CardPile.  When initialized,
 *  all the cards are in the top pile and the others are empty.
 *
 *  CardTable should implement the following behavior:
 *  - When the user doubleclicks on a card, that card and all those
 *    on top of it on the pile should be flipped over
 *  - When the user drags a card, that card and all those on top of it
 *    on the pile should be removed from the pile they are on and
 *    follow the mouse around.
 *  - When the user releases the mouse while dragging a pile of cards,
 *    the pile should be inserted into some fixed pile according to
 *    where the mouse was released. 
 *  
 *  @author Nicholas R. Howe
 *  @version CSC 112, 8 February 2006
 */
public class CardTable extends JComponent {
    /** Gives the number of piles available */
    public static final int NPILE = 5;

    /** gives the width of the canvas */
    public static final int WIDTH = 800;

    /** gives the height of the canvas */
    public static final int HEIGHT = 500;

    /** Storage for each of the piles available */
    CardPile pile[] = new CardPile[NPILE];

    /** Storage for pile that is in motion, if any
     *  This will be null if we are not dragging a pile */
    CardPile movingPile;

    /** Records card under last mouse press 
     *  If we detect the start of a drag, we will have to split off the pile 
     *  starting from this card */
    Card cardUnderMouse;

    /** Records index of pile under last mouse press 
     *  If we detect the start of a drag, we will have to split off the pile 
     *  starting from cardUnderMouse */
    CardPile pileUnderMouse;

    /** Initialize a table with a deck of cards in the first slot */
    public CardTable() {
	pile[0] = new CardPile(Card.newDeck(),2,2);
	pile[1] = new CardPile(2,102);
	pile[2] = new CardPile(2,202);
	pile[3] = new CardPile(2,302);
	pile[4] = new CardPile(2,402);

        //for (Card c: pile[0]) {
        //c.flipCard();
        //System.out.println(c);
        //}
	
        //Card c = pile[0].removeFirst();
        //System.out.println(c);
        //c.flipCard();
        //pile[1].add(c);

	Responder responder = new Responder();
	addMouseListener(responder);
	addMouseMotionListener(responder);
    }

    /**
     *  Returns the requested card pile
     *
     *  @param i  The index of the pile requested
     *  @return   The requested pile, or null if the pile is empty
     */
    public CardPile getPile(int i) {
	CardPile pile;
	if ((i >= 0)&&(i < NPILE)) {
	    pile = this.pile[i];
	} else {
	    pile = null;
	}
	return pile;
    }

    /**
     *  Attaches the specified cards to the specified pile.
     *  The location of the pile is set to a fixed location.
     *
     *  @param i  ID of the pile to use
     *  @param pile  Cards to put there
     */
    public void setPile(int i, CardPile pile) {
	if ((i >= 0)&&(i < NPILE)) {
            pile.setX(2);
            pile.setY(2+100*i);
	    this.pile[i] = pile;
	}
    }

    /**
     *  Draws the table and the cards upon it
     *
     *  @param g The graphics object to draw into
     */
    public void paintComponent(Graphics g) {
	g.setColor(Color.green.darker().darker());
	g.fillRect(0,0,WIDTH,HEIGHT);
	g.setColor(Color.black);
	for (int i = 0; i < pile.length; i++) {
	    g.drawRect(2,2+100*i,72,96);
	    pile[i].draw(g);
	}
	if (movingPile != null) {
	    movingPile.draw(g);
	}
    }

    /**
     *  The component will look bad if it is sized smaller than this
     *
     *  @return The minimum dimension
     */
    public Dimension getMinimumSize() {
	return new Dimension(WIDTH,HEIGHT);
    }

    /**
     *  The component will look best at this size
     *
     *  @return The preferred dimension
     */
    public Dimension getPreferredSize() {
	return new Dimension(WIDTH,HEIGHT);
    }

    /**
     *  For debugging.  Runs validation tests on all piles.
     */
    public void validatePiles() {
	for (int i = 0; i < NPILE; i++) {
	    System.out.print("Pile "+i+":  ");
            System.out.print("Location:  ("+pile[i].getX()+","+
                             pile[i].getY()+");  Length:  ");
            System.out.print(pile[i].size()+";  Status:  ");
            System.out.println("Valid.");
	}
	System.out.print("Moving pile:  ");
        System.out.print("Location:  ("+movingPile.getX()+","+
                         movingPile.getY()+");  Length:  ");
        System.out.print(movingPile.size()+";  Status:  ");
        System.out.println("Valid.");
    }

    /**
     *  Locates the pile clicked on, if any.
     *
     *  @param x,y  Coordinates of mouse click
     *  @return  CardPile  holding clicked card
     */
    private CardPile locatePile(int x, int y) {
        int index = y/100;
        if (index < 0) {
            index = 0;
        } else if (index>=NPILE) {
            index = NPILE-1;
        }
	return pile[index];
    }

    /**
     *  Locates the card clicked on, if any.
     *
     *  @param x,y  Coordinates of mouse click
     *  @return  Card  holding clicked card
     */
    private Card locateCard(int x, int y) {
	return locatePile(x,y).locateCard(x,y);  // ask the CardPile
    }

    /** Listener for relevant mouse events */
    private class Responder implements MouseListener, MouseMotionListener {
	/** Click event handler */
        public void mouseClicked(MouseEvent e) {
	    if (e.getClickCount() == 2) {
                Card mark = locateCard(e.getX(),e.getY());
                CardPile pile = locatePile(e.getX(),e.getY());
                ListIterator<Card> position = pile.iteratorBefore(mark);
                while (position.hasNext()) {
                    position.next().flipCard();
                }
		repaint();
	    }
	}

	/** 
	 * Press event handler stores card currently under mouse,
	 * but doesn't move any data until we have a drag event
	 */
        public void mousePressed(MouseEvent e) {
	    cardUnderMouse = locateCard(e.getX(),e.getY());
	    pileUnderMouse = locatePile(e.getX(),e.getY());
	    if (cardUnderMouse == null) {
		cardUnderMouse = pileUnderMouse.peekFirst();
	    }
        }

	/** Release event handler */
        public void mouseReleased(MouseEvent e) {
	    if (movingPile != null) {
		// we have a pile coming to rest -- where?
		CardPile targetPile = locatePile(e.getX(),e.getY());
		if (targetPile == null) {
		} 

                // now insert the movingPile
                Card targetCard = locateCard(e.getX(),e.getY());
                if (targetCard == null) {
                    targetPile.append(movingPile);
                } else {
                    targetPile.insertAfter(movingPile,targetCard);
                }
                movingPile = null;
	    }
	    repaint();
        }

	/** Enter event handler */
        public void mouseEntered(MouseEvent e) {
        }

	/** Exit event handler */
        public void mouseExited(MouseEvent e) {
        }

	/** Drag event handler moves piles around */
        public void mouseDragged(MouseEvent e) {
	    if (movingPile == null) {
		// this is the first drag event for this pile
		movingPile = pileUnderMouse.split(cardUnderMouse);
		cardUnderMouse = null;
	    } 
            movingPile.setX(e.getX());
            movingPile.setY(e.getY());
            repaint();
        }

	/** Move event handler */
        public void mouseMoved(MouseEvent e) {
        }
    }
}  // end of CardTable
