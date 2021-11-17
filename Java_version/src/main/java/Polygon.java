import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangshao
 */
@Getter
@Setter
public class Polygon {
    //data structure for the polygon
    protected int index;
    protected List<Vertex> vertices;
    protected List<Polygon> neighbours;
    protected List<Edge> edges;

    Polygon() {
        this.vertices = new ArrayList<>();
        this.neighbours = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    Polygon(final int index, final int numOfVertices, int numNeighbours) {
        this.index = index;
        this.vertices = new ArrayList<>(numOfVertices);
        this.edges = new ArrayList<>(numOfVertices);
        this.neighbours = new ArrayList<>(numNeighbours);
    }

    public void addVertices(final Vertex newVertex) {
        this.vertices.add(newVertex);
    }

    public void addEdges(final Edge edge) {
        this.edges.add(edge);
    }

    public void addNeighbours(final Polygon newPolygon) {
        this.neighbours.add(newPolygon);
    }
}
