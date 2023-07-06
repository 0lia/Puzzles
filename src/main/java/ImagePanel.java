import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * class for storing images on the view
 */
class ImagePanel extends JPanel {

    private BufferedImage img;
    private File file;
    Random random = new Random();

    public ImagePanel(BufferedImage img, File file) {
        this.img = img;
        this.file = file;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
        setLocation(random.nextInt(600) + 100, random.nextInt(300) + 20);
    }

    public BufferedImage getImg() {
        return img;
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    @Override
    public void update(Graphics g) {
        img = Puzzle.rotate(img, Direction.LEFT);

        try {
            ImageIO.write(img, "JPEG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        paintComponent(g);

    }

}