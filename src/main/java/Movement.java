import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * class for making puzzles move
 */
public class Movement implements MouseListener, MouseMotionListener {

    private int x, y;

    public Movement(Component... components){
        for(Component c : components){
            c.addMouseListener(this);
            c.addMouseMotionListener(this);
        }
    }

    /**
     * rotate image when clicked
     * @param e - MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        e.getComponent().update(e.getComponent().getGraphics());
    }

    /**
     * get coordinates of image
     * @param e - MouseEvent
     */
    @Override
    public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * drag image
     * @param e - MouseEvent
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        e.getComponent().setLocation((e.getX() + e.getComponent().getX()) - x, (e.getY() + e.getComponent().getY()) - y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
