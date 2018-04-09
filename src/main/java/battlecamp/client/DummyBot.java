package battlecamp.client;

import battlecamp.client.model.*;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by martin on 11.05.17.
 */
@Component
public class DummyBot extends AbstractBot {

    private Map<Tile, Tile> tiles = new HashMap<>();
    private List<Direction> directions = null;

    @Override
    public void enterGame(Game game) {
        SimpleGraph<Tile, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        game.getBoard().getTiles().forEach(t -> {
            tiles.put(t, t);
            graph.addVertex(t);
        });
        Tile home = null;
        Player player = game.getPlayers().stream().filter(p -> p.getId().equals(getBotName())).findFirst().get();
        for(int x = 0; x < game.getBoard().getColumns(); x++) {
            for(int y = 0; y < game.getBoard().getRows(); y++) {
                Tile currentTile = tiles.get(tileEntity(x, y));
                if (currentTile.getType().equals(Tile.Type.HUIS)) {
                    home = currentTile;
                }
                if (x < game.getBoard().getColumns() - 1 || y < game.getBoard().getRows() - 1) {
                    if (x > 0) {
                        Tile leftNeighbor = tiles.get(tileEntity(x - 1, y));
                        addEdge(graph, currentTile, leftNeighbor);
                    }
                    if (y > 0) {
                        Tile topNeighbor = tiles.get(tileEntity(x, y - 1));
                        addEdge(graph, currentTile, topNeighbor);
                    }
                    if (x < game.getBoard().getColumns() - 1) {
                        Tile rightNeighbor = tiles.get(tileEntity(x + 1, y));
                        addEdge(graph, currentTile, rightNeighbor);
                    }
                    if (y < game.getBoard().getRows() - 1) {
                        Tile bottomNeighbor = tiles.get(tileEntity(x, y + 1));
                        addEdge(graph, currentTile, bottomNeighbor);
                    }
                }
            }
        }
        Tile currentPosition = tiles.get(tileEntity(player.getX(), player.getY()));
        DijkstraShortestPath<Tile, DefaultEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Tile, DefaultEdge> path = dijkstraShortestPath.getPath(currentPosition, home);
        directions = new LinkedList<>();
        for (DefaultEdge edge : path.getEdgeList()) {
            Tile source = graph.getEdgeSource(edge);
            Tile target = graph.getEdgeTarget(edge);
            if (!currentPosition.equals(source)) {
                Tile tmp = source;
                source = target;
                target = tmp;
            }
            if (target.getX() < source.getX()) {
                directions.add(Direction.W);
            } else if (target.getX() > source.getX()) {
                directions.add(Direction.E);
            } else if (target.getY() < source.getY()) {
                directions.add(Direction.N);
            } else {
                directions.add(Direction.S);
            }
            currentPosition = target;
        }
    }

    private void addEdge(SimpleGraph<Tile, DefaultEdge>  graph, Tile source, Tile target) {
        if (!source.getType().equals(Tile.Type.ROTS) && !target.getType().equals(Tile.Type.ROTS)) {
            DefaultEdge edge = graph.addEdge(source, target);
        }
    }

    @Override
    public void gameUpdate(Update update) {

    }

    @Override
    public void beurt(String gameId) {
        if (!directions.isEmpty()) {
            action(gameId, directions.remove(0));
        }
    }

    private Tile tileEntity(int x, int y) {
        Tile tile = new Tile();
        tile.setX(x);
        tile.setY(y);
        return tile;
    }
}
