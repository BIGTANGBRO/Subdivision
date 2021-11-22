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
    //neighbour polygons
    protected List<Integer> triangleIndices;
    protected int numNeighbours;

    Triangle() {
        this.vertices = new ArrayList<>();
    }

    /**
     * constructor
     *
     * @param index           index number
     * @param triangleIndices the index of the neighbour triangles
     */
    Triangle(final int index, final List<Integer> triangleIndices) {
        this.index = index;
        this.triangleIndices = triangleIndices;
        this.vertices = new ArrayList<>(3);
        this.numNeighbours = triangleIndices.size();
    }

    Triangle(final int index, final List<Vertex> vertices, final List<Integer> triangleIndices) {
        this.index = index;
        this.triangleIndices = triangleIndices;
        this.vertices = vertices;
        this.numNeighbours = triangleIndices.size();
    }

    public boolean contanVertex(Vertex vertex) {
        return this.vertices.contains(vertex);
    }

    public boolean containVertices(Vertex v1, Vertex v2) {
        return (this.vertices.contains(v1) && this.vertices.contains(v2));
    }

    public Vertex getRemain(Vertex v1, Vertex v2) {
        for (Vertex v : this.vertices) {
            if (v.getIndex() != v1.getIndex() && v.getIndex() != v2.getIndex()) {
                return v;
            }
        }
        return null;
    }
}
