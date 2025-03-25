package Model;


import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class Sample implements BoardObject {

    public ImageIcon fourFlowers = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/flower4.png"))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    public ImageIcon threeFlowers = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/flower3.png"))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    public ImageIcon twoFlowers = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/flower2.png"))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    public ImageIcon oneFlower = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/flower1.png"))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    public ImageIcon currentImage;
    public int numberOfFlowers;

    public Sample(int numberOfFlowers) {
        this.numberOfFlowers = numberOfFlowers;
        if(numberOfFlowers == 1){
            currentImage = oneFlower;
        }else if(numberOfFlowers == 2){
            currentImage = twoFlowers;
        }else if(numberOfFlowers == 3){
            currentImage = threeFlowers;
        }else if(numberOfFlowers == 4){
            currentImage = fourFlowers;
        }
    }

    public int getNumberOfFlowers() {
        return numberOfFlowers;
    }

    @Override
    public ImageIcon getIcon() {
        return currentImage;
    }
}
