
package Model;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import javax.swing.ImageIcon;

/**
 *
 * @author macario
 */
public class Agent extends Thread implements BoardObject {
    private final String name;
    private final ImageIcon image;
    private final Square[][] board;
    public int[] pos = new int[2];
    private Sample sample;
    Random random = new Random(System.currentTimeMillis());
    public static int[] dirRow = {-1, 0, 1, 0};
    public static int[] dirCol = {0, -1, 0, 1};
    public int[] posMotherShip;
    public int speed;

    public Agent(String name, String imagePath, Square[][] board, int speed)
    {
        this.speed = speed;
        this.name=name;
        System.out.println(getClass().getResource(imagePath));
        image = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath))).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        this.board = board;
        pos[0] = random.nextInt(board.length);
        pos[1] = random.nextInt(board.length);
        board[pos[0]][pos[1]].setObject(this);
    }

    public ImageIcon getIcon() {
        return image;
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < board.length && y >= 0 && y < board[0].length;
    }


    @Override
    public void run()
    {

        while(true){


        }


    }

    public synchronized void updatePosition(int x, int y)
    {
        try
        {
            sleep(100+speed);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace(System.out);
        }
        board[pos[0]][pos[1]].eraseObject();
        pos[0]=x;
        pos[1]=y;
        board[pos[0]][pos[1]].setObject(this);
        System.out.println(name + " i -> Row: " + pos[0] + " Col:"+ pos[1]);
    }

}
