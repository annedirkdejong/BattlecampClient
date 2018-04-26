package battlecamp.client;

import battlecamp.client.GUI.SettingsGUI;
import battlecamp.client.model.*;
import battlecamp.client.QFiles.*;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Anne-dirk on 09.04.18.
 */
@Component
public class QBot extends AbstractBot {

    private final boolean useGUI = true;

    private final float Reward = -1.0f;
    private final float Alpha = 0.5f;
    private final float Gamma = 0.9f;
    private final float Epsilon = 0.3f;


    private Game currentGame;
    private Map<StateActionPair, Float> QFunction;

    private boolean doneTraining = false;
    private boolean lock = false;
    private StateActionPair previousStateActionPair;

    SettingsGUI gui = new SettingsGUI();

    long totalMs = 0;
    int gameNr = 0;


    @Override
    public void enterGame(Game game) {

        if(!this.lock) {
            try {
                // Re-initialize variables for a new game
                this.lock = true;
                this.previousStateActionPair = null;
                this.QFunction = new HashMap<>();
                this.currentGame = game;

                // Create GUI according to the new board
                if(useGUI)
                    gui.setBoard(game.getBoard().copy());

                // Start training
                long start_time = System.currentTimeMillis();
                int epochs = 100;
                for (int epoch = 0; epoch < epochs; epoch++) {
                    this.simulateEpisode();
                }
                long timeElapsed = System.currentTimeMillis() - start_time;
                System.out.println("Done training in " + timeElapsed + "ms (" + epochs + " epochs)");

                totalMs += timeElapsed;
                System.out.println("New average " + ++gameNr + ": " + totalMs / gameNr + "ms");

            }
            catch(Exception e){
                System.out.println("ERROR: Exception was thrown while training the QFunction");
            }
            finally {
                this.lock = false;
                this.doneTraining = true;
            }
        }
    }

    @Override
    public void gameUpdate(Update update) {

    }

    @Override
    public void beurt(String gameId, Player player, List<Player> players) {

        // Update player locations
        for(Player p : players)
            this.currentGame.updatePlayer(p);


        if(this.doneTraining) {
            // Execute best action based on QFunction
            int action = getBestAction(this.currentGame.getState());
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

    private void simulateEpisode(){
        // Initialize
        Board board = this.currentGame.getBoard().copy();
        State currentState = this.currentGame.getState().copy();
        float reward = 0.0f;
        int step = 0;

        // Play the board until the iglo is found or if the maximum number of moves has been reached (penguin might be stuck)
        int maxMoves = board.getRows() * board.getColumns() * 4;
        while(reward == 0.0f && step < maxMoves) {
            // Select best action based on current QFunction
            int action = determineAction(getBestAction(this.currentGame.getState()));
            // Find next State when executing action
            State nextState = getNextState(board, currentState, action);
            // Determine reward for landing in that state
            reward = getNextReward(board, nextState);
            // Update Q-function accordingly
            updateQFunction(currentState, action, nextState, reward);
            currentState = nextState;
            step++;
        }

    }

    private void updateQFunction(State currentState, int action, State nextState, float reward){
        // Update QFunction
        StateActionPair currentPair = new StateActionPair(currentState, action);
        this.QFunction.put(
                currentPair,
                this.QFunction.getOrDefault(currentPair, 0.0f)
                        + this.Alpha * (reward + this.Gamma
                        * this.QFunction.getOrDefault(new StateActionPair(nextState, getBestAction(nextState)), 0.0f)
                        - this.QFunction.getOrDefault(currentPair, 0.0f))
        );
        // Update GUI
        if(useGUI)
            this.gui.updateState(   currentState.getCoords().get(0).x, currentState.getCoords().get(0).y, this.QFunction.getOrDefault(new StateActionPair(currentState, getBestAction(currentState)), 0.0f));
    }


    private int getBestAction(State s){
        List<Integer> actions = new LinkedList<>(Arrays.asList(0,1,2,3));
        Collections.shuffle(actions);

        float highest = -Float.MAX_VALUE;
        int bestAction = -1;
        for(int i = 0; i < 4; i++){
            float value = this.QFunction.getOrDefault(new StateActionPair(s,actions.get(i)),0.0f);
            if(value > highest){
                highest = value;
                bestAction = i;
            }
        }
        return  actions.get(bestAction);
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

    private float getNextReward(Board board, State state){
        int x = state.getCoords().get(0).x;
        int y = state.getCoords().get(0).y;
        Tile t = findTile(board, x, y);
        if(t.getType() == Tile.Type.HUIS) {
            return 1.0f;
        }
        else
            return 0;
    }

    private State getNextState(Board board, State currentState, int currentAction){
        int nextX = currentState.getCoords().get(0).x;
        int nextY = currentState.getCoords().get(0).y;
        switch (currentAction) {
            case 0: {
                nextY--;
                break;
            }
            case 1: {
                nextX++;
                break;
            }
            case 2: {
                nextY++;
                break;
            }
            case 3: {
                nextX--;
                break;
            }
        }
        Tile nextTile = findTile(board, nextX, nextY);
        if(nextTile == null || nextTile.getType() == Tile.Type.ROTS)
            return currentState;
        else{
            State s = currentState.copy();
            List<Point> coords = new ArrayList<>();
            coords.add(new Point(nextX, nextY));
            s.setCoords(coords);
            return s;
        }
    }

    private Tile findTile(Board board, int x, int y) {
        for (Tile tile : board.getTiles()) {
            if (tile.getX() == x && tile.getY() == y) {
                return tile;
            }
        }
        return null;
    }

}
