package battlecamp.client;

import battlecamp.client.model.*;
import battlecamp.client.QFiles.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Anne-dirk on 09.04.18.
 */
@Component
public class QBot extends AbstractBot {

    private Object lock = new Object();

    private final float Reward = -1.0f;
    private final float Alpha = 0.5f;
    private final float Gamma = 0.9f;
    private final float Epsilon = 0.09f;


    private Game currentGame;
    private Map<StateActionPair, Float> QFunction;

    private boolean running = false;
    private StateActionPair previousStateActionPair;



    @Override
    public void enterGame(Game game) {
        this.previousStateActionPair = null;

        if(this.QFunction == null) {
            this.QFunction = new HashMap<>();
        }
        this.currentGame = game;
        this.running = true;

    }

    @Override
    public void gameUpdate(Update update) {
        if(update.getPlayer() != null && this.previousStateActionPair != null) {

            synchronized (lock) {

                if (update.getPlayer().isWinner()) {
                    float q = this.QFunction.getOrDefault(this.previousStateActionPair, 0.0f);
                    System.out.println(this.previousStateActionPair);
                    System.out.print("Last q-value: " + q);
                    this.updateQ(q, 0, 1);
                    System.out.println(", new q-value: " + this.QFunction.get(this.previousStateActionPair));
                    this.running = false;
                } else if (update.getPlayer().isDead()) {
                    float q = this.QFunction.getOrDefault(this.previousStateActionPair, 0.0f);
                    int bestAction = getBestAction(this.currentGame.getState());
                    float maxQ = this.QFunction.getOrDefault(new StateActionPair(this.currentGame.getState(), bestAction), 0.0f);
                    this.updateQ(q, maxQ, -1);
                    this.running = false;
                }
            }
        }
    }

    @Override
    public void beurt(String gameId, Player player, List<Player> players) {

        for(Player p : players)
            this.currentGame.updatePlayer(p);
        synchronized (lock) {
            // Select best action
            int action = getBestAction(this.currentGame.getState());
            // Observe result from previous action & update QFunction
            if (this.previousStateActionPair != null) {
                float q = this.QFunction.getOrDefault(this.previousStateActionPair, 0.0f);
                float maxQ = this.QFunction.getOrDefault(new StateActionPair(this.currentGame.getState(), action), 0.0f);
                updateQ(q, maxQ, 0);
            }
            action = determineAction(action);
            // Save state and action
            this.previousStateActionPair = new StateActionPair(this.currentGame.getState(), action);
            // Take action e-greedy
            //action = determineAction(action);
            switch (action) {
                case 0:
                    action(gameId, Direction.N);
                    break;
                case 1:
                    action(gameId, Direction.E);
                    break;
                case 2:
                    action(gameId, Direction.S);
                    break;
                case 3:
                    action(gameId, Direction.W);
                    break;
            }
        }
    }

    private void updateQ(float newQ, float maxQ, float reward){
        this.QFunction.put(
                this.previousStateActionPair, newQ + this.Alpha * (reward + this.Gamma * maxQ - newQ)
        );
    }

    private int getBestAction(State s){
        float highest = -Float.MAX_VALUE;
        int bestAction = -1;
        for(int action = 0; action < 4; action++){
            float value = this.QFunction.getOrDefault(new StateActionPair(s,action),0.0f);
            //System.out.println("Action value: " + value);
            if(value > highest){
                highest = value;
                bestAction = action;
            }
        }
        return  bestAction;
    }

    private int determineAction(int bestAction){
        List<Integer> actions = new LinkedList<>(Arrays.asList(0,1,2,3));
        actions.remove(bestAction);

        // e-greedy action
        Random r = new Random();
        float e = r.nextInt(1000) / 1000.0f;
        if(e > this.Epsilon)
            return bestAction;
        if(e < this.Epsilon / 3)
            return actions.get(0);
        if(e < (this.Epsilon / 3) * 2)
            return actions.get(1);
        return actions.get(2);

    }
}
