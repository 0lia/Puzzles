import util.ImageDifference;
import util.PositionDifference;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * main class of the application
 */
public class Puzzle {

    static File pieces = new File("src/main/resources/static/pieces");
    static List<List<BufferedImage>> matrix;

    public static void main(String[] args) {
        Random rnd = new Random();
        try {
            BufferedImage image = ImageIO.read(new File("src/main/resources/static/picture.jpg"));
            int xLength = image.getWidth();
            int yLength = image.getHeight();
            int dx = xLength/4;
            int dy = yLength/4;
            for (int x = 0; x < xLength; x+=dx) {
                for (int y = 0; y < yLength; y+=dy) {
                    BufferedImage crop = image.getSubimage(x,y, xLength/4, yLength/4);
                    crop = rotate(crop, Direction.values()[rnd.nextInt(4)]);
                    ImageIO.write(crop, "JPEG", new File(pieces.getPath() + "/picture" + x + "-" + y + ".jpeg"));
                }
            }
            new MainView(pieces);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Rotating image
     * @param image to rotate
     * @param direction of rotation
     * @return
     */
    public static BufferedImage rotate(BufferedImage image, Direction direction){
        if(direction.equals(Direction.NONE))
            return image;

        BufferedImage result;
        if(direction.equals(Direction.UPSIDE_DOWN)){
            result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        } else {
            result = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                switch (direction) {
                    case LEFT:
                        result.setRGB(y, (image.getWidth() - 1) - x, image.getRGB(x, y));
                        break;
                    case RIGHT:
                        result.setRGB((image.getHeight() - 1 - y), x, image.getRGB(x, y));
                        break;
                    case UPSIDE_DOWN:
                        result.setRGB((image.getWidth() - 1) - x, (image.getHeight() - 1) - y, image.getRGB(x, y));
                }
            }
        }
        return result;
    }

    /**
     * make puzzles automatically
     * saves result image as a file
     */
    public static void makePuzzlesAutomatically() {
        if(matrix == null) {
            matrix = makeMatrix();
        }
        BufferedImage result = joinPuzzles(matrix);
        try {
            ImageIO.write(result, "JPEG", new File("src/main/resources/static/result.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * reads puzzles from directory
     * @return list of puzzles
     */
    public static List<BufferedImage> getPieces(){
        File[] files = pieces.listFiles();
        List<BufferedImage> puzzles = new ArrayList<>();
        try {
            for(File f : files) {
                BufferedImage image = ImageIO.read(f);
                puzzles.add(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return puzzles;
    }

    /**
     * makes ordered matrix from list of puzzles
     * @return matrix of puzzles
     */
    public static List<List<BufferedImage>> makeMatrix(){
        List<BufferedImage> puzzles = getPieces();
        // puzzles = puzzles.stream().sorted(Comparator.comparingInt(o -> o.getRGB(7, 49))).collect(Collectors.toList());
        List<List<BufferedImage>> matrix = new ArrayList<>();
        List<BufferedImage> row1 = makeRow(puzzles);
        matrix.add(row1);

        for (int i = 0; i < 3; i++) {
            matrix.add(findNextRow(matrix, i, puzzles));
        }

        return matrix;
    }

    /**
     * makes result picture from given matrix
     * @param matrix - ordered matrix of puzzles
     * @return result picture
     */
    public static BufferedImage joinPuzzles(List<List<BufferedImage>> matrix){
        List<BufferedImage> rows = new ArrayList<>();
        //join rows
        for (List<BufferedImage> row : matrix){
            BufferedImage resultRow = row.get(0);
            for (int i = 1; i < row.size(); i++) {
                resultRow = joinBufferedImage(resultRow, row.get(i), Direction.RIGHT);
            }
            rows.add(resultRow);
        }
        //join whole picture
        BufferedImage result = rows.get(0);
        for (int i = 1; i < rows.size(); i++) {
            result = joinBufferedImage(result, rows.get(i), Direction.UPSIDE_DOWN);
        }
        return result;
    }

    /**
     * calculates row of puzzles that are the most suitable for previous row.
     * can turn matrix upside down if necessary
     * @param matrix - matrix filled with puzzles
     * @param lastRow - number of last filled row in matrix
     * @param puzzles - remaining puzzles
     * @return new row
     */
    public static List<BufferedImage> findNextRow(List<List<BufferedImage>> matrix, int lastRow, List<BufferedImage> puzzles){
        List<BufferedImage> newRow = new ArrayList<>();
        PositionDifference pos1 = compareToFirstPuzzle(rotate(matrix.get(lastRow).get(0), Direction.LEFT), puzzles);
        PositionDifference pos2 = compareToFirstPuzzle(rotate(matrix.get(lastRow).get(matrix.get(0).size() - 1), Direction.RIGHT), puzzles);

        if(pos1.getDifference() < pos2.getDifference()) {
            newRow.add(rotate(findBestRotation(rotate(matrix.get(lastRow).get(0), Direction.LEFT), puzzles.get(pos1.getPosition())).getImage(), Direction.RIGHT));
            puzzles.remove(pos1.getPosition());
        }else{
            for (int i = 0; i < matrix.size()/ 2; i++) {
                List<BufferedImage> buf = matrix.get(i);
                matrix.set(i, matrix.get(matrix.size() - i - 1));
                matrix.set(matrix.size() - i - 1, buf);
            }

            for (int i = 0; i < matrix.size(); i++) {
                turnOverImagesInRow(matrix.get(i));
            }
            newRow.add(rotate(puzzles.get(pos2.getPosition()), Direction.RIGHT));
            puzzles.remove(pos2.getPosition());
        }

        addPuzzleToRow(newRow, puzzles);
        addPuzzleToRow(newRow, puzzles);
        addPuzzleToRow(newRow, puzzles);

        return newRow;
    }

    /**
     * turns images in the row upside down, exchanging places of them (from 1st to last and so on)
     * @param row - row of images
     */
    public static void turnOverImagesInRow(List<BufferedImage> row){
        for (int i = 0; i < (row.size() + 1)/2; i++) {
            BufferedImage buf = row.get(i);
            row.set(i, rotate(row.get(row.size() - i - 1), Direction.UPSIDE_DOWN));
            row.set(row.size() - i - 1, rotate(buf, Direction.UPSIDE_DOWN));
        }

    }

    /**
     * calculates the first row of the matrix.
     * @param puzzles - list of all the puzzles
     * @return first row
     */
    public static List<BufferedImage> makeRow(List<BufferedImage> puzzles){
        long minDiff = Long.MAX_VALUE;
        int position = -1;
        Direction bestDirection = Direction.NONE;

        BufferedImage first = puzzles.get(0);
        puzzles.remove(0);

        for (int i = 0; i < 4; i++) {
            PositionDifference difference = compareToFirstPuzzle(first, puzzles);
            if(difference.getDifference() < minDiff){
                minDiff = difference.getDifference();
                position = difference.getPosition();
                bestDirection = Direction.values()[i];
            }
            first = rotate(first, Direction.LEFT);
        }
        List<BufferedImage> row = new ArrayList<>();

        row.add(rotate(first, bestDirection));
        row.add(puzzles.get(position));

        puzzles.remove(puzzles.get(position));

        addPuzzleToRow(row, puzzles);
        addPuzzleToRow(row, puzzles);

        return row;
    }

    /**
     * adds puzzles to row
     * @param row - beginning of the row
     * @param puzzles - remaining puzzles
     */
    public static void addPuzzleToRow(List<BufferedImage> row, List<BufferedImage> puzzles){
        PositionDifference dif1 = compareToFirstPuzzle(row.get(row.size() - 1), puzzles);
        PositionDifference dif2 = compareToFirstPuzzle(rotate(row.get(0), Direction.UPSIDE_DOWN), puzzles);

        if(dif1.getDifference() < dif2.getDifference()){
            row.add(findBestRotation(row.get(row.size() - 1), puzzles.get(dif1.getPosition())).getImage());
            puzzles.remove(dif1.getPosition());
        }else {
            row.add(0, rotate(puzzles.get(dif2.getPosition()), Direction.UPSIDE_DOWN));
            puzzles.remove(dif2.getPosition());
        }
    }

    /**
     * compares and finds the best suitable puzzle from list of puzzles
     * @param first - puzzle to compare with
     * @param puzzles - remaining puzzles
     * @return PositionDifference (position of the best suitable puzzle from list with difference between first and this puzzles)
     */
    public static PositionDifference compareToFirstPuzzle(BufferedImage first, List<BufferedImage> puzzles){
        long min = Long.MAX_VALUE;
        int bestImgNumber = -1;
        for (int i = 0; i < puzzles.size(); i++) {
            ImageDifference difference = findBestRotation(first, puzzles.get(i));
            if(difference.getDifference() < min){
                puzzles.set(i, difference.getImage());
                min = difference.getDifference();
                bestImgNumber = i;
            }
        }

        return new PositionDifference(bestImgNumber, min);
    }

    /** find the best rotation for 2nd image
     * @param img1 first image
     * @param img2 second image
     * @return best rotated img2 for img1 with difference between them
     */
    public static ImageDifference findBestRotation(BufferedImage img1, BufferedImage img2){
        long minDiff = Long.MAX_VALUE;
        Direction bestDir = Direction.NONE;

        for (int i = 0; i < 4; i++) {
            long dif = comparePuzzles(img1, img2);
            if(dif < minDiff){
                minDiff = dif;
                bestDir = Direction.values()[i];
            }
            img2 = rotate(img2, Direction.LEFT);
        }

        return new ImageDifference(rotate(img2, bestDir), minDiff);
    }

    /**
     * compares right side of img1 with left side of img2
     * @param img1 - first image
     * @param img2 - second image
     * @return the difference
     */
    public static long comparePuzzles(BufferedImage img1, BufferedImage img2){
        long diff = 0;
        for (int y = 0; y < img1.getHeight(); y++) {
             diff += Math.abs(img1.getRGB(img1.getWidth() - 1, y) - img2.getRGB(0, y));
        }
        return diff;
    }

    /**
     * join two buffered images
     * @param img1 - first image
     * @param img2 - second image
     * @param direction - direction of joining
     * @return joined image
     */
    public static BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2, Direction direction) {
        int offset = 2;
        int width, height;
        if(direction.equals(Direction.RIGHT)) {
            width = img1.getWidth() + img2.getWidth() + offset;
            height = Math.max(img1.getHeight(), img2.getHeight());
        }else{
            width = img1.getWidth();
            height = img1.getHeight() + img2.getHeight() + offset;
        }
        BufferedImage newImage = new BufferedImage(width, height,
                img1.getType());
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.BLACK);
        g2.fillRect(0, 0, width, height);
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        if(direction.equals(Direction.RIGHT))
            g2.drawImage(img2, null, img1.getWidth() + offset, 0);
        else
            g2.drawImage(img2, null, 0, img1.getHeight() + offset);
        g2.dispose();
        return newImage;
    }

}
