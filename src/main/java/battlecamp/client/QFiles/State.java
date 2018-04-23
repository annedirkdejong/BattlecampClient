package battlecamp.client.QFiles;

import battlecamp.client.model.Player;

import java.awt.*;

import java.util.*;
import java.util.List;


public class State {

    private List<String> types;
    private List<Point> coords;


    public State(List<Player> players) {

        this.types = new LinkedList<>();
        this.coords = new LinkedList<>();
        players.forEach(player -> {
            this.types.add(player.getType());
            this.coords.add(new Point(player.getX(), player.getY()));
        });

    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<Point> getCoords() {
        return coords;
    }

    public void setCoords(List<Point> coords) {
        this.coords = coords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return types.equals(state.getTypes()) &&
                coords.equals(state.getCoords());
    }

    @Override
    public int hashCode() {

        return Objects.hash(types, coords);
    }

    @Override
    public String toString() {
        return "State{" +
                "types=" + types +
                ", coords=" + coords +
                '}';
    }
}
