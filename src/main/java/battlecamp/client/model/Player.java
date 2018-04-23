package battlecamp.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by martin on 10.05.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    private String id;
    private String type;
    private boolean winner;
    private boolean dead;
    private boolean heeftBeurt;
    private int x;
    private int y;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isWinner() { return winner; }

    public void setWinner(boolean val) {
        this.winner = val;
    }

    public boolean isDead() { return dead; }

    public void setDead(boolean val) { this.dead = val; }

    public boolean isHeeftBeurt() {
        return heeftBeurt;
    }

    public void setHeeftBeurt(boolean heeftBeurt) {
        this.heeftBeurt = heeftBeurt;
    }

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

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", beurt=" + heeftBeurt +
                '}';
    }
}
