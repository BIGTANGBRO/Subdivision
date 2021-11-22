import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangshao
 */
@Getter
@Setter
public class LoopScheme {
    private List<Triangle> triangles;
    private List<Vertex> vertices;
    private List<Edge> edges;

    public LoopScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
    }

    /**
     * Compute the odd vertex
     * @param v1 first vertex
     * @param v2 second vertex
     * @return The coordinate of the vertex
     */
    public Vector3d computeOdd(Vertex v1, Vertex v2) {
        List<Vertex> vertices = new ArrayList<>(2);
        for (Triangle triangle : triangles) {
            if (triangle.containVertices(v1, v2)) {
                Vertex v = triangle.getRemain(v1, v2);
                vertices.add(v);
            }
        }
        Vector3d coord1 = MathUtils.dotVal(Constant.THREEOVEREIGHT, MathUtils.addVector(v1.getCoords(), v2.getCoords()));
        Vector3d coord2 = MathUtils.dotVal(Constant.ONEOVEREIGHT, MathUtils.addVector(vertices.get(0).getCoords(), vertices.get(1).getCoords()));
        return MathUtils.addVector(coord1, coord2);
    }

    /**
     * get all the edge points
     * @return a list of edge point vertex
     */
    public List<Vertex> computeOdd() {
        //Iterate all the edges
        return new ArrayList<>();
    }

    /**
     * Compute the even vertex
     * @param vertex individual vertex
     * @param vertices all the vertices
     * @return new coord of the vertex
     */
    public static Vector3d computeEven(final Vertex vertex, final List<Vertex> vertices) {
        //create the even vertices
        final int n = vertex.getNumNeighbours();
        double alpha = Constant.THREEOVERSIXTEEN;
        if (n > 3) {
            alpha = 1 / n * (5 / 8 - Math.pow((Constant.THREEOVEREIGHT + Constant.ONEOVERFOUR * Math.cos(2 * Constant.PI / n)), 2));
        }
        final List<Integer> neighbourVertices = vertex.getVertexIndices();
        final Vector3d coordV = vertex.getCoords();
        Vector3d v2 = new Vector3d(0d, 0d, 0d);
        for (int i = 0; i < n; i++) {
            final Vertex v = vertices.get(neighbourVertices.get(i));
            final Vector3d coordNeighbour = v.getCoords();
            //get the sum of the neighbour points
            final Vector3d vProduct = MathUtils.dotVal(alpha, coordNeighbour);
            v2 = MathUtils.addVector(v2, vProduct);
        }
        final double coeff2 = 1 - n * alpha;
        final Vector3d newVertex = MathUtils.addVector(MathUtils.dotVal(coeff2, coordV), v2);
        return newVertex;
    }

    public void connectVertices() {
        //connect the new vertices to form the new mash
    }
}
