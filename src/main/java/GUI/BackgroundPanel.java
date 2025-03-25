
package GUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BackgroundPanel extends JPanel
{

    ImageIcon background;

    public BackgroundPanel(String path)
    {
        super();
        this.background = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Dimension dim = this.getSize();
        int width = (int) dim.getWidth();
        int height = (int) dim.getHeight();
        g.drawImage(background.getImage(), 0, 0, width, height, null);
        setOpaque(false);
        super.paintComponent(g);
    }

}
