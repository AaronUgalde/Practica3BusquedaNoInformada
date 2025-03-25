package Model;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Migaja implements BoardObject{

    public ImageIcon dosMigajas = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/migajas2.png"))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    public ImageIcon unaMigaja = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/migajas1.png"))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    public ImageIcon currentImage;
    public int numMigajas;

    public Migaja(int numMigajas) {
        this.numMigajas = numMigajas;
        if(numMigajas == 1){
            currentImage = unaMigaja;
        } else if (numMigajas == 2) {
            currentImage = dosMigajas;
        }
    }

    public int getNumMigajas() {
        return numMigajas;
    }

    @Override
    public ImageIcon getIcon() {
        return currentImage;
    }
}
