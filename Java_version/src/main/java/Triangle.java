import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangshao
 */
@Getter
@Setter
public class Triangle {
    //data structure for the polygon
    protected int index;
    protected List<Vertex> vertices;
    protected List<Edge> edges;
    //neighbour polygons
    protected List<Integer> triangleIndices;
    protected int numNeighbours;

    Triangle() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    /**
     * constructor
     * @param index index number
     * @param triangleIndices the index of the neighbour triangles
     */
    Triangle(final int index, final List<Integer> triangleIndices) {
        this.index = index;
        this.triangleIndices = triangleIndices;
        this.vertices = new ArrayList<>(3);
        this.edges = new ArrayList<>(3);
        this.numNeighbours = triangleIndices.size();
    }

    Triangle(final int index, final List<Vertex> vertices, final List<Integer> triangleIndices) {
        this.index = index;
        this.triangleIndices = triangleIndices;
        this.vertices = vertices;
        this.edges = new ArrayList<>(3);
        this.numNeighbours = triangleIndices.size();
    }
}
