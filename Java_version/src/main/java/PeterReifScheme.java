import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author: tangshao
 * @Date: 2021/12/2
 */
@Getter
@Setter
public class PeterReifScheme {
    private List<Triangle> triangles;
    private List<Edge> edges;
    private List<Vertex> vertices;
    private Map<Integer, Integer> oddNodeMap;
    private Map<Integer, List<Integer>> trianglesTrackMap;


    public PeterReifScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.edges = edges;
        this.vertices = vertices;
        this.oddNodeMap = new HashMap<>();
        this.trianglesTrackMap = new HashMap<>();
    }

    public Vector3d computeOdd(Vertex v1, Vertex v2) {
        List<Vertex> vertices = new ArrayList<>(2);
        Vector3d coord = MathUtils.dotVal(0.5, MathUtils.addVector(v1.getCoords(), v2.getCoords()));
        return coord;
    }

    public Map<Integer, Vector3d> computeOdd() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = vertices.size();
        for (Edge edge : edges) {
            Vertex v1 = edge.getA();
            Vertex v2 = edge.getB();
            Vector3d coord = computeOdd(v1, v2);
            vertexMap.put(index, coord);
            oddNodeMap.put(edge.getIndex(), index);
            index += 1;
        }
        return vertexMap;
    }

    public Map<Integer, List<Integer>> createTriangle(Map<Integer, Vector3d> vertexMap) {
        //connect the vertices
        //vertexMap is from computeOdd
        int faceCount = 0;
        final Map<Integer, List<Integer>> faceMap = new HashMap<>();
        //iterate over the original triangles
        for (final Triangle triangle : this.triangles) {
            //for track map
            List<Integer> triangleIndexTracking = new ArrayList<>();

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
                Vector3d subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(vertexIndices.get(0)), vertexMap.get(vertexIndices.get(1)), vertexMap.get(vertexIndices.get(2)));
                if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                    Collections.swap(vertexIndices, 1, 2);
                }
                faceMap.put(faceCount, vertexIndices);
                triangleIndexTracking.add(faceCount);
                faceCount += 1;
            }
            //connect the new created odd vertices to form a surface
            final List<Integer> oddVertexArr = new ArrayList<>(oddVertexSet);
            Vector3d subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(oddVertexArr.get(0)), vertexMap.get(oddVertexArr.get(1)), vertexMap.get(oddVertexArr.get(2)));
            if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                Collections.swap(oddVertexArr, 1, 2);
            }
            faceMap.put(faceCount, oddVertexArr);
            triangleIndexTracking.add(faceCount);
            faceCount += 1;
            trianglesTrackMap.put(triangle.getIndex(), triangleIndexTracking);
        }
        return faceMap;
    }
}
