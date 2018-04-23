package battlecamp.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by martin on 11.05.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beurt {

    private String gameId;
    private Player player;
    private List<Player> players;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Player> getPlayers() { return players; }

    public void setPlayers(List<Player> players) { this.players = players; }
}
