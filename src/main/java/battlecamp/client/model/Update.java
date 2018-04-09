package battlecamp.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by martin on 11.05.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Update {

    private int x;
    private int y;
    private Tile.Type type;
    private Player player;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Tile.Type getType() {
        return type;
    }

    public void setType(Tile.Type type) {
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
