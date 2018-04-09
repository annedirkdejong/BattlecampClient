package battlecamp.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by martin on 10.05.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tile {

    public enum Type {WATER, ROTS, IJS, HUIS};

    private Object id;
    private int x;
    private int y;
    private Type type;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tile)) return false;

        Tile tile = (Tile) o;

        if (x != tile.x) return false;
        return y == tile.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
