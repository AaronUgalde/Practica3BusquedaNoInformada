package GUI;

import Model.*;
import Model.BoardObject;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.Random;

public class Scenario extends JFrame {

    public int dim;
    private BoardObject selectedEnviroment;
    public Square[][] board;
    private Agent agent1;
    private Agent agent2;
    Random random = new Random(System.currentTimeMillis());


    public Scenario(int dim) {
        this.dim = dim;
        this.setContentPane(new BackgroundPanel("/forest_background.png"));
        this.setTitle("Agents");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setBounds(50,50,dim*50+35,dim*50+85);
        initComponents();
    }

    private void initComponents() {

        //Agregamos los botones y sus funciones
        JMenu settingsMenu = new JMenu("Settings");
        ButtonGroup settingsOptions = new ButtonGroup();
        JRadioButtonMenuItem sample = new JRadioButtonMenuItem("Sample");
        sample.addItemListener(evt -> manageSample(evt));
        JRadioButtonMenuItem obstacle = new JRadioButtonMenuItem("Obstacle");
        obstacle.addItemListener(evt -> manageObstacle(evt));
        JRadioButtonMenuItem motherShip = new JRadioButtonMenuItem("MotherShip");
        motherShip.addItemListener(evt -> manageMotherShip(evt));
        settingsOptions.add(sample);
        settingsOptions.add(obstacle);
        settingsOptions.add(motherShip);
        settingsMenu.add(sample);
        settingsMenu.add(obstacle);
        settingsMenu.add(motherShip);

        JMenu file = new JMenu("File");
        JMenuItem run  = new JMenuItem("Run");
        run.addActionListener(evt -> manageRun(evt));
        JMenuItem exit   = new JMenuItem("Exit");
        exit.addActionListener(evt -> manageQuit(evt));
        file.add(run);
        file.add(exit);

        JMenuBar barraMenus = new JMenuBar();
        barraMenus.add(file);
        barraMenus.add(settingsMenu);
        this.setJMenuBar(barraMenus);

        this.setLayout(null);
        initPlane();

        class MyWindowAdapter extends WindowAdapter
        {
            @Override
            public void windowClosing(WindowEvent eventObject)
            {
                goodBye();
            }
        }
        addWindowListener(new MyWindowAdapter());

        // Crea 2 agentes
        agent1 = new Agent("bee1", "/bee1.png", board, 50);
        agent2 = new Agent("bee2", "/bee1.png", board, 50);
    }

    public void setMotherShip(int[] pos) {
        agent1.posMotherShip = pos;
        agent2.posMotherShip = pos;
    }

    private void manageSample(ItemEvent evt) {
        JRadioButtonMenuItem opt = (JRadioButtonMenuItem) evt.getSource();
        if(opt.isSelected())
            selectedEnviroment = new Sample(4);
        else selectedEnviroment = null;
    }

    private void manageObstacle(ItemEvent evt) {
        JRadioButtonMenuItem opt = (JRadioButtonMenuItem) evt.getSource();
        if(opt.isSelected())
            selectedEnviroment = new Obstacle("/tronco.png");
        else selectedEnviroment = null;
    }

    private void manageMotherShip(ItemEvent evt) {
        JRadioButtonMenuItem opt = (JRadioButtonMenuItem) evt.getSource();
        if(opt.isSelected())
            selectedEnviroment = new MotherShip("/hive.png");
        else selectedEnviroment = null;
    }

    private void manageRun(ActionEvent eventObject)
    {
        if(!agent1.isAlive()) agent1.start();
        if(!agent2.isAlive()) agent2.start();
    }

    public void manageQuit(ActionEvent eventObject){
        goodBye();
    }

    private void goodBye() {
        int answer = JOptionPane.showConfirmDialog(rootPane, "Desea salir?","Aviso",JOptionPane.YES_NO_OPTION);
        if(answer==JOptionPane.YES_OPTION) System.exit(0);
    }

    private void initPlane() {
        board = new Square[dim][dim];
        for(int i=0;i<dim;i++) {
            for (int j = 0; j < dim; j++) {
                board[i][j] = new Square(i, j);
                this.add(board[i][j]);
            }
        }
    }

    public BoardObject getSelectedEnviroment() {
        return selectedEnviroment;
    }

}
