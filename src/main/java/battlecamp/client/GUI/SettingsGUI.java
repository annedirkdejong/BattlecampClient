package battlecamp.client.GUI;

import battlecamp.client.QFiles.StateActionPair;
import battlecamp.client.model.Board;
import battlecamp.client.model.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SettingsGUI extends JFrame {

    Board board;
    private List<JButton> buttons;

    JPanel mainPanel;

    public SettingsGUI(){
        this.buttons = new LinkedList<>();
        this.setSize(1600,800);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();

        int xPos = (dim.width / 2) - (this.getWidth() / 2);
        int yPos = (dim.height / 2) - (this.getHeight() / 2);
        this.setLocation(xPos, yPos);


        mainPanel = new JPanel();
        this.add(mainPanel);



        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setResizable(false);
        this.setTitle("Qbot GUI");


    }

    public void updateState(int x, int y, float maxValue){
        JButton b = this.buttons.get(y * (int) Math.sqrt((double) this.buttons.size()) + x);
        b.setText(maxValue + "");
        if(maxValue == 0.0f)
            return;
        b.setBackground(new Color((int) ((1-maxValue)*240), 255, (int) ((1-maxValue)*240)));
        validate();
    }

    public void setBoard(Board board){
        this.setVisible(true);
        this.board = board;
        this.buttons = new LinkedList<>();
        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new GridLayout(board.getRows(), board.getColumns()));
        List<Tile> tiles = board.getTiles();
        for(int row = 0; row < board.getRows(); row++){
            for(int col = 0; col < board.getColumns(); col++) {
                JButton btn = new JButton();
                btn.setEnabled(false);
                if (tiles.get(col * board.getColumns() + row).getType() == Tile.Type.ROTS) {
                    btn.setBackground(Color.gray);
                } else if (tiles.get(col * board.getColumns() + row).getType() == Tile.Type.HUIS) {
                    btn.setBackground(Color.CYAN);
                } else {
                    btn.setBackground(Color.WHITE);
                    //btn.setText(0 + "");
                }
                this.buttons.add(btn);
                this.mainPanel.add(btn);
            }
        }
        this.add(mainPanel);
        validate();
    }

}
