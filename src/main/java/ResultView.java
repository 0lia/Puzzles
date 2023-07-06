import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * shows result of automatic making puzzles
 */
public class ResultView extends JFrame{

    public ResultView(File file) throws IOException {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(ImageIO.read(new File("src/main/resources/static/puzzle.jpg")));

        setSize(626, 649);
        setLocationRelativeTo(this);

        JPanel back = new JPanel();
        back.setSize(getSize());
        back.setBackground (Color.WHITE);
        back.setLayout(null);
        back.setLocation(0,0);

        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImagePanel panel = new ImagePanel(image, file);
        panel.setLocation(2, 2);
        back.add(panel);

        add(back);
        setVisible(true);
    }
}
