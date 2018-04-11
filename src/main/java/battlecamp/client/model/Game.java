package battlecamp.client.model;

import battlecamp.client.QFiles.State;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by martin on 10.05.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    private String id;
    private Board board;
    private boolean started;
    private boolean stopped;
    private int moves;
    private List<Player> players;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public State getState(){
        return new State(this.players);
    }

    public void updatePlayerPos(String id, int x, int y){
        Player player = getPlayers().stream().filter(p -> p.getId().equals(id)).findFirst().get();
        player.setX(x);
        player.setY(y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;

        Game game = (Game) o;

        return id.equals(game.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", started=" + started +
                ", stopped=" + stopped +
                ", moves=" + moves +
                ", players=" + players +
                '}';
    }
}
