package battlecamp.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by martin on 11.05.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beurt {

    private String gameId;
    private Player player;

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
}
