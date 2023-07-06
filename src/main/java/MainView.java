import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * main window of the application
 */
public class MainView extends JFrame {

    public MainView(File path) throws IOException {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(ImageIO.read(new File("src/main/resources/static/puzzle.jpg")));
        setSize(900, 700);
        setLocationRelativeTo(this);
        JPanel back = new JPanel();

        back.setSize(getSize());
        back.setBackground (Color.WHITE);
        back.setLayout(null);
        back.setLocation(0,0);

        File[] files = path.listFiles();

        for (File f : files) {
            BufferedImage image = ImageIO.read(f);
            ImagePanel panel = new ImagePanel(image, f);
            back.add(panel);
        }

        Movement mv = new Movement(back.getComponents());

        JButton buttonCheck = new JButton("Check");
        buttonCheck.setVisible(true);
        buttonCheck.setBounds(1,1,95,30);
        buttonCheck.addActionListener(e -> JOptionPane.showMessageDialog(buttonCheck,  checkButton(back.getComponents())));
        add(buttonCheck);

        JButton buttonResult = new JButton("Result");
        buttonResult.setVisible(true);
        buttonResult.setBounds(1,40,95,30);
        buttonResult.addActionListener(e -> resultButton());
        add(buttonResult);

        add(back);

        setVisible(true);
    }

    /**
     * show user the result image
     */
    public void resultButton(){
        Puzzle.makePuzzlesAutomatically();

        try {
            ResultView view = new ResultView(new File("src/main/resources/static/result.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * check if user put the puzzles correctly
     * @param components - puzzles as ImagePanel
     * @return message for user
     */
    public String checkButton(Component... components){
        List<BufferedImage> actual = Arrays.stream(components).sorted((o1, o2) -> {
            if(Math.abs(o1.getY() - o2.getY()) > 75)
                return o1.getY() - o2.getY();
            return o1.getX() - o2.getX();
        }).map(component -> ((ImagePanel)component).getImg()).collect(Collectors.toList());

        Puzzle.makePuzzlesAutomatically();

        List<List<BufferedImage>> matrix = Puzzle.matrix;
        List<BufferedImage> expected = new ArrayList<>();

        for (List<BufferedImage> listImages : matrix){
            for (BufferedImage img : listImages)
                expected.add(img);
        }

        for (int i = 0; i < expected.size(); i++) {
           if(Math.abs(expected.get(i).getRGB(0, 0) - actual.get(i).getRGB(0, 0)) > 1005500
           || Math.abs(expected.get(i).getRGB(10, 10) - actual.get(i).getRGB(10, 10)) > 1005500
           )
               return "Wrong puzzle in " + (i/4 + 1) + " row, column "  + ((i % 4) + 1);
        }
        return "Nice job!!!";

    }
}
