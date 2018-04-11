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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        if(Objects.equals(types, state.types) &&
                Objects.equals(coords, state.coords)) {
            //System.out.println("FOUND A COUPLE");
            //System.out.println("States: " + this.types + " - " + state.types);
            //System.out.println("States: " + this.coords + " - " + state.coords);
        }
        return Objects.equals(types, state.types) &&
                Objects.equals(coords, state.coords);
    }

    @Override
    public int hashCode() {

        return Objects.hash(types, coords);
    }
}
