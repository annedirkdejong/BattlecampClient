package battlecamp.client;

import battlecamp.client.model.Beurt;
import battlecamp.client.model.Direction;
import battlecamp.client.model.Game;
import battlecamp.client.model.Update;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.TextMessage;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by martin on 10.05.17.
 */
public abstract class AbstractBot {

    private Set<Game> pendingJoins = new HashSet<>();
    private Set<Game> joinedGames = new HashSet<>();
    private Object lock = new Object();

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.type}")
    private String botType;

    @Value("${battlecamp.server}")
    private String baseAddress;

    private <T> T mapMessage(TextMessage message, Class<T> tClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(message.getText(), tClass);
        } catch (Exception e) {
            return null;
        }
    }

    @JmsListener(destination = "games", containerFactory = "myFactory")
    public final void receiveGameUpdates(TextMessage message) {
        Game game = mapMessage(message, Game.class);
        System.out.println("Game update: " + game);
        if (game != null) {
            synchronized (lock) {
                if (!pendingJoins.contains(game) && !joinedGames.contains(game) && joinGame(game)) {
                    System.out.println("Joining Game with id: " + game.getId());
                    pendingJoins.add(game);
                } else if (!joinedGames.contains(game) && pendingJoins.contains(game) && game.getPlayers().stream().anyMatch(p -> p.getId().equals(getBotName()))) {
                    joinedGames.add(game);
                    pendingJoins.remove(game);
                    System.out.println("Joined Game with id: " + game.getId());
                    enterGame(game);
                }
            }
        }
    }

    @JmsListener(destination = "updates", containerFactory = "myFactory")
    public final void receiveUpdates(TextMessage message) {
        Update update = mapMessage(message, Update.class);
        if (update != null && !joinedGames.isEmpty()) {
            gameUpdate(update);
        }
    }

    @JmsListener(destination = "beurt", containerFactory = "myFactory")
    public final void receiveBeurtUpdates(TextMessage message) {
        Beurt beurt = mapMessage(message, Beurt.class);
        if (beurt != null && !joinedGames.isEmpty() && beurt.getPlayer().getId().equals(getBotName())) {
            System.out.println("Aan de beurt: " + beurt.getGameId());
            beurt(beurt.getGameId());
        }
    }

    public final void action(String gameId, Direction direction) {
        List<NameValuePair> nvps = new LinkedList<>();
        nvps.add(new BasicNameValuePair("gameId", gameId));
        nvps.add(new BasicNameValuePair("playerId", getBotName()));
        nvps.add(new BasicNameValuePair("direction", direction.name()));
        performPost(baseAddress + "/action", nvps);
    }

    public final String getBotName() {
        return botName;
    }

    public final String getBotType() {
        return botType;
    }

    public abstract void enterGame(Game game);

    public abstract void gameUpdate(Update update);

    public abstract void beurt(String gameId);

    private boolean joinGame(Game game) {
        String botName = getBotName();
        if (!game.isStarted() && !game.isStopped() && game.getPlayers().stream().noneMatch(player -> player.getId().equals(botName))) {
            String url = baseAddress + "/join";
            List<NameValuePair> nameValuePairs = new LinkedList<>();
            nameValuePairs.add(new BasicNameValuePair("gameId", game.getId()));
            nameValuePairs.add(new BasicNameValuePair("playerId", botName));
            nameValuePairs.add(new BasicNameValuePair("playerColor", "Red"));
            nameValuePairs.add(new BasicNameValuePair("playerType", getBotType()));
            return performPost(url, nameValuePairs) == 200;
        } else {
            return false;
        }
    }

    private int performPost(String url, List<NameValuePair> nameValuePairs) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            return httpResponse.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
            return 500;
        }
    }

}
