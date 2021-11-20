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
    protected List<Triangle> neighbourTriangles;
    protected List<Integer> triangleIndices;

    Triangle() {
        this.vertices = new ArrayList<>();
        this.neighbourTriangles = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    /**
     * constructor
     * @param index index number
     * @param numNeighbours number of neighbour triangle
     * @param triangleIndices the index of the neighbour triangles
     */
    Triangle(final int index, int numNeighbours, List<Integer> triangleIndices) {
        this.index = index;
        this.triangleIndices = triangleIndices;
        this.vertices = new ArrayList<>(3);
        this.edges = new ArrayList<>(3);
        this.neighbourTriangles = new ArrayList<>(numNeighbours);
    }

    Triangle(final int index, List<Vertex> vertices, int numNeighbours, List<Integer> triangleIndices) {
        this.index = index;
        this.triangleIndices = triangleIndices;
        this.vertices = vertices;
        this.edges = new ArrayList<>(3);
        this.neighbourTriangles = new ArrayList<>(numNeighbours);
    }
}
