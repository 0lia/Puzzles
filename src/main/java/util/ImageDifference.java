package util;

import java.awt.image.BufferedImage;

public class ImageDifference {

    private BufferedImage image;
    private Long difference;

    public ImageDifference(BufferedImage image, Long difference) {
        this.image = image;
        this.difference = difference;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Long getDifference() {
        return difference;
    }

    public void setDifference(Long difference) {
        this.difference = difference;
    }
}
