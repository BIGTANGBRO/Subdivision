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

    public LoopScheme(final List<Triangle> triangles, final List<Vertex> vertices, final List<Edge> edges) {
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
    public Vector3d computeOdd(final Vertex v1, final Vertex v2) {
        final List<Vertex> vertices = new ArrayList<>(2);
        int triangleCount = 0;
        for (final Triangle triangle : triangles) {
            if (triangle.containVertices(v1, v2)) {
                //serach for vLeft and vRight
                final Vertex v = triangle.getRemain(v1, v2);
                vertices.add(v);
                triangleCount += 1;
            }
        }
        if (triangleCount == 1) {
            return MathUtils.dotVal(Constant.ONEOVERTWO, MathUtils.addVector(v1.getCoords(), v2.getCoords()));
        }
        final Vector3d coord1 = MathUtils.dotVal(Constant.THREEOVEREIGHT, MathUtils.addVector(v1.getCoords(), v2.getCoords()));
        final Vector3d coord2 = MathUtils.dotVal(Constant.ONEOVEREIGHT, MathUtils.addVector(vertices.get(0).getCoords(), vertices.get(1).getCoords()));
        return MathUtils.addVector(coord1, coord2);
    }

    /**
     * compute the new odd vertex/edge point
     *
     * @return Map with new vertex
     */
    public Map<Integer, Vector3d> computeOdd() {
        final Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = vertices.size();
        //iteration about the edges
        for (final Edge edge : edges) {
            //each odd node corresponds to an edge
            final Vertex v1 = edge.getA();
            final Vertex v2 = edge.getB();
            final Vector3d coord = computeOdd(v1, v2);
            //the index starts from numCoords
            vertexMap.put(index, coord);
            //edge point index corresponds to the edge index
            oddNodeMap.put(edge.getIndex(), index);
            index += 1;
        }
        return vertexMap;
    }

    private double getAlpha(final int n) {
        if (n == 3) {
            return 3.0d / 16.0d;
        }
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
        final double alpha = getAlpha(n);

        final List<Integer> neighbourVertices = vertex.getVertexIndices();
        final Vector3d coordV = vertex.getCoords();
        Vector3d vOther = new Vector3d(0d, 0d, 0d);
        for (int i = 0; i < n; i++) {
            final int neighbourIndex = neighbourVertices.get(i);
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
        final Map<Integer, Vector3d> vertexMap = new HashMap<>();
        for (int index = 0; index < vertices.size(); index++) {
            final Vector3d coord = computeEven(vertices.get(index));
            vertexMap.put(index, coord);
        }
        return vertexMap;
    }

    /**
     * connect to form the new faces
     *
     * @return map with face index and vertex indices
     */
    public Map<Integer, List<Integer>> createTriangle(Map<Integer, Vector3d> vertexMap) {
        //connect the vertices
        //vertexMap is from computeOdd
        int faceCount = 0;
        final Map<Integer, List<Integer>> faceMap = new HashMap<>();
        //iterate over the original triangles
        for (final Triangle triangle : this.triangles) {
            final HashSet<Integer> oddVertexSet = new HashSet<>();
            //set the face topology
            Vector3d faceNormal = triangle.getUnitNormal();
            for (final Vertex vertex : triangle.getVertices()) {
                final List<Edge> connectedEdges = triangle.getConnectedEdges(vertex);
                final List<Integer> vertexIndices = new ArrayList<>(3);
                vertexIndices.add(vertex.getIndex());
                for (final Edge edge : connectedEdges) {
                    final int newVertexIndex = oddNodeMap.get(edge.getIndex());
                    oddVertexSet.add(newVertexIndex);
                    vertexIndices.add(newVertexIndex);
                }

                faceMap.put(faceCount, vertexIndices);
                faceCount += 1;
            }
            //connect the new created odd vertices to form a surface
            final List<Integer> oddVertexArr = new ArrayList<>(oddVertexSet);
            faceMap.put(faceCount, oddVertexArr);
            faceCount += 1;
        }
        return faceMap;
    }
}
