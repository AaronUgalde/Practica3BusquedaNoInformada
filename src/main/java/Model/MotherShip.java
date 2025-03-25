package Model;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MotherShip implements BoardObject {

    public ImageIcon image;

    public MotherShip(String imagePath){
        image = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    }

    @Override
    public ImageIcon getIcon() {
        return image;
    }
}
