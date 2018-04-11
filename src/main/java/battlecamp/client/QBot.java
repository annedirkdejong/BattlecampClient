package battlecamp.client;

import battlecamp.client.model.*;
import battlecamp.client.QFiles.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by martin on 11.05.17.
 */
@Component
public class QBot extends AbstractBot {

    private final float Reward = -1.0f;
    private final float Alpha = 0.5f;
    private final float Gamma = 0.9f;
    private final float Epsilon = 0.09f;


    private Game currentGame;
    private Map<StateActionPair, Float> QFunction;

    private StateActionPair previousStateActionPair;



    @Override
    public void enterGame(Game game) {
        this.previousStateActionPair = null;

        if(this.QFunction == null) {
            System.out.println("new hashmap created");
            this.QFunction = new HashMap<>();
        }
        this.currentGame = game;

    }

    @Override
    public void gameUpdate(Update update) {
        System.out.println("Update: " + update);
        //if(update.getPlayer() != null)
        //    this.currentGame.updatePlayerPos(update.getPlayer().getId(), update.getX(), update.getY());
    }

    @Override
    public void beurt(String gameId, Player player) {
        this.currentGame.updatePlayerPos(player.getId(), player.getX(), player.getY());
        if(!player.getType().toLowerCase().contains("pinguin"))
            return;
        // Select best action
        int action = getBestAction(this.currentGame.getState());
        // Observe result from previous action & update QFunction
        if (this.previousStateActionPair != null) {
            float q = this.QFunction.getOrDefault(this.previousStateActionPair, 0.0f);
            float maxQsp = this.QFunction.getOrDefault(new StateActionPair(this.currentGame.getState(), action), 0.0f);

            this.QFunction.put(this.previousStateActionPair,
                    q + this.Alpha * (this.Reward + this.Gamma * maxQsp - q));
        }
        // Save state and action
        this.previousStateActionPair = new StateActionPair(this.currentGame.getState(), action);
        // Take action e-greedy
        action = determineAction(action);
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
