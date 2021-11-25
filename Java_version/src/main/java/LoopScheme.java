import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author tangshao
 */
@Getter
@Setter
public class LoopScheme {
    private List<Triangle> triangles;
    private List<Vertex> vertices;
    private List<Edge> edges;
    private Map<Integer, Integer> oddNodeMap;

    public LoopScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
        this.oddNodeMap = new HashMap<>();
    }

    /**
     * Compute the odd vertex
     *
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
     * compute the new odd vertex/edge point
     *
     * @param numNodes number of all the vertices in the previous model
     * @return Map with new vertex
     */
    public Map<Integer, Vector3d> computeOdd(int numNodes) {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = numNodes;
        //iteration about the edges
        for (Edge edge : edges) {
            //each odd node corresponds to an edge
            Vertex v1 = edge.getA();
            Vertex v2 = edge.getB();
            Vector3d coord = computeOdd(v1, v2);
            //the index starts from numCoords
            vertexMap.put(index, coord);
            //edge point index corresponds to the edge index
            oddNodeMap.put(edge.getIndex(), index);
            index += 1;
        }
        return vertexMap;
    }

    /**
     * Compute the even vertex
     *
     * @param vertex individual vertex
     * @return new coord of the vertex
     */
    public Vector3d computeEven(final Vertex vertex) {
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

    /**
     * compute the even node for the whole model
     *
     * @return map with index and coords
     */
    public Map<Integer, Vector3d> ComputeEven() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        for (int index = 0; index < vertices.size(); index++) {
            Vector3d coord = computeEven(vertices.get(index));
            vertexMap.put(index, coord);
        }
        return vertexMap;
    }

    /**
     * connect to form the new faces
     *
     * @return map with face index and vertex indices
     */
    public Map<Integer, List<Integer>> createTriangle() {
        //connect the vertices
        //vertexMap is from computeOdd
        int faceCount = 0;
        Map<Integer, List<Integer>> faceMap = new HashMap<>();
        for (Triangle triangle : this.triangles) {
            HashSet<Integer> oddVertexSet = new HashSet<>();
            for (Vertex vertex : triangle.getVertices()) {
                List<Edge> connectedEdges = triangle.getConnectedEdges(vertex);
                List<Integer> vertexIndices = new ArrayList<>(3);
                vertexIndices.add(vertex.getIndex());
                for (Edge edge : connectedEdges) {
                    int newVertexIndex = oddNodeMap.get(edge.getIndex());
                    oddVertexSet.add(newVertexIndex);
                    vertexIndices.add(newVertexIndex);
                }
                faceMap.put(faceCount, vertexIndices);
                faceCount += 1;
            }
            //connect the new created odd vertices to form a surface
            List<Integer> oddVertexArr = new ArrayList<>(oddVertexSet);
            faceMap.put(faceCount, oddVertexArr);
            faceCount += 1;
        }
        return faceMap;
    }
}
