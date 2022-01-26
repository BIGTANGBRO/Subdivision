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
        this.edges = new ArrayList<>();
    }

    Triangle(final int index, final List<Vertex> vertices, final List<Integer> triangleIndices) {
        this.index = index;
        this.triangleIndices = triangleIndices;
        this.vertices = vertices;
        this.edges = new ArrayList<>();
    }

    public boolean containVertex(final Vertex vertex) {
        return this.vertices.contains(vertex);
    }

    public boolean containVertices(final Vertex v1, final Vertex v2) {
        return (this.vertices.contains(v1) && this.vertices.contains(v2));
    }

    public Vertex getRemain(final Vertex v1, final Vertex v2) {
        for (final Vertex v : this.vertices) {
            if (v.getIndex() != v1.getIndex() && v.getIndex() != v2.getIndex()) {
                return v;
            }
        }
        return null;
    }

    public List<Vertex> getRemain(Vertex v1) {
        List<Vertex> verticesRemain = new ArrayList<>(2);
        for (Vertex v : this.vertices) {
            if (v.getIndex() != v1.getIndex()) {
                verticesRemain.add(v);
            }
        }
        return verticesRemain;
    }

    public void addEdge(final Edge edge) {
        this.edges.add(edge);
    }

    public List<Edge> getConnectedEdges(final Vertex v) {
        final List<Edge> edges = new ArrayList<>(2);
        for (final Edge edge : this.edges) {
            if (edge.has(v)) {
                edges.add(edge);
            }
        }
        return edges;
    }

    public int getNumNeighbours() {
        return this.triangleIndices.size();
    }

    public Vector3d getUnitNormal() {
        Vector3d coord1 = this.vertices.get(0).getCoords();
        Vector3d coord2 = this.vertices.get(1).getCoords();
        Vector3d coord3 = this.vertices.get(2).getCoords();

        Vector3d vec1 = MathUtils.minusVector(coord1, coord2);
        Vector3d vec2 = MathUtils.minusVector(coord1, coord3);

        double x = vec1.getYVal() * vec2.getZVal() - vec1.getZVal() * vec2.getYVal();
        double y = vec1.getZVal() * vec2.getXVal() - vec1.getXVal() * vec2.getZVal();
        double z = vec1.getXVal() * vec2.getYVal() - vec1.getYVal() * vec2.getXVal();

        double mod = MathUtils.getMod(x, y, z);
        return new Vector3d(x / mod, y / mod, z / mod);
    }
}
