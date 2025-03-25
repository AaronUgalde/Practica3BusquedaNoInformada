package Model;

import GUI.Scenario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Square extends JLabel {
    public int[] pos;
    public BoardObject object;

    public Square(int x, int y) {
        pos = new int[]{x, y};
        setBounds(y*50+10,x*50+10,50,50);
        setBorder(BorderFactory.createDashedBorder(Color.white));
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setEnviroment(e);
            }
        });
    }

    public void eraseObject(){
        this.setIcon(null);
        this.object = null;
    }

    public void setObject(BoardObject object) {
        if(object == null){
            this.object = null;
            this.setIcon(null);
        }else {
            this.object = object;
            this.setIcon(object.getIcon());
        }
        revalidate();
        repaint();
    }

    public void minusOneFlower(){
        Sample flower = (Sample) object;
        if(flower.getNumberOfFlowers() > 1) {
            setObject(new Sample(flower.getNumberOfFlowers() - 1));
        }else{
            eraseObject();
        }
    }

    public void minusOneMigajas(){
        Migaja migaja = (Migaja) object;
        if(migaja.getNumMigajas() > 1) {
            setObject(new Migaja(migaja.getNumMigajas() - 1));
        }else{
            eraseObject();
        }
    }

    private void setEnviroment(MouseEvent e) {
        Scenario scenario = (Scenario) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        setObject(scenario.getSelectedEnviroment());
        if(scenario.getSelectedEnviroment() instanceof MotherShip){
            scenario.setMotherShip(pos);
        }
    }

    public BoardObject getObject() {
        return object;
    }


    public int[] getPos() {
        return pos;
    }
}
