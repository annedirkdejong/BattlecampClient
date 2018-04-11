package battlecamp.client.QFiles;

import battlecamp.client.model.Direction;

import java.util.Objects;

public class StateActionPair {

    private State state;
    private int action;

    public StateActionPair(State state, int action){
        this.state = state;
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateActionPair that = (StateActionPair) o;
        return action == that.action &&
                state.equals(that.state);//Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {

        return Objects.hash(state, action);
    }
}
