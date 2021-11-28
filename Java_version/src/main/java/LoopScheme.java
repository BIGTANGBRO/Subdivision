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
        int triangleCount = 0;
        for (Triangle triangle : triangles) {
            if (triangle.containVertices(v1, v2)) {
                //serach for vLeft and vRight
                Vertex v = triangle.getRemain(v1, v2);
                vertices.add(v);
                triangleCount += 1;
            }
        }
        if (triangleCount == 1) {
            return MathUtils.dotVal(Constant.ONEOVERTWO, MathUtils.addVector(v1.getCoords(), v2.getCoords()));
        }
        Vector3d coord1 = MathUtils.dotVal(Constant.THREEOVEREIGHT, MathUtils.addVector(v1.getCoords(), v2.getCoords()));
        Vector3d coord2 = MathUtils.dotVal(Constant.ONEOVEREIGHT, MathUtils.addVector(vertices.get(0).getCoords(), vertices.get(1).getCoords()));
        return MathUtils.addVector(coord1, coord2);
    }

    /**
     * compute the new odd vertex/edge point
     *
     * @return Map with new vertex
     */
    public Map<Integer, Vector3d> computeOdd() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = vertices.size();
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

    private double getAlpha(int n) {
        return 1.0d / n * (5.0d / 8.0d - Math.pow((Constant.THREEOVEREIGHT + Constant.ONEOVERFOUR * Math.cos(2 * Math.PI / n)), 2));
    }

    /**
     * Compute the even vertex
     *
     * @param vertex individual vertex
     * @return new coord of the vertex
     */
    public Vector3d computeEven(final Vertex vertex) {
        //create the even vertices
        final int n = vertex.getNumVertices();
        double alpha = getAlpha(n);

        final List<Integer> neighbourVertices = vertex.getVertexIndices();
        final Vector3d coordV = vertex.getCoords();
        Vector3d vOther = new Vector3d(0d, 0d, 0d);
        for (int i = 0; i < n; i++) {
            int neighbourIndex = neighbourVertices.get(i);
            final Vertex v = this.vertices.get(neighbourIndex);
            final Vector3d coordNeighbour = v.getCoords();
            //get the sum of the neighbour points
            vOther = MathUtils.addVector(vOther, coordNeighbour);
        }
        final double coeff2 = 1 - n * alpha;
        final Vector3d newVertex = MathUtils.addVector(MathUtils.dotVal(coeff2, coordV), MathUtils.dotVal(alpha, vOther));
        return newVertex;
    }

    /**
     * compute the even node for the whole model
     *
     * @return map with index and coords
     */
    public Map<Integer, Vector3d> computeEven() {
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
