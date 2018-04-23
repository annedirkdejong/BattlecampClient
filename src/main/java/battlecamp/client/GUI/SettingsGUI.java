package battlecamp.client.GUI;

import javax.swing.*;
import java.awt.*;

public class SettingsGUI extends JFrame {

    public SettingsGUI(){
        this.setSize(600,400);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();

        int xPos = (dim.width / 2) - (this.getWidth() / 2);
        int yPos = (dim.height / 2) - (this.getHeight() / 2);
        this.setLocation(xPos, yPos);


        JPanel mainPanel = new JPanel();



        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Qbot Settings");
        this.setVisible(true);

    }

}
