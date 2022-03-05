import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author: tangshao
 * @Date: 05/03/2022
 */
@Getter
@Setter
public class RegionalLoop extends LoopScheme {
    public RegionalLoop(final List<Triangle> triangles, final List<Vertex> vertices, final List<Edge> edges) {
        super(triangles, vertices, edges);
    }

    public Map<Integer, Vector3d> computeOdd() {
        final Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = this.vertices.size();
        //iteration about the edges
        for (final Edge edge : edges) {
            //each odd node corresponds to an edge
            final Vertex v1 = edge.getA();
            final Vertex v2 = edge.getB();
            Vector3d coord = new Vector3d(0, 0, 0);
            if (v1.getNumTriangles() > 7 || v2.getNumTriangles() > 7) {
                coord = computeOdd2(v1, v2);
            } else {
                coord = computeOddNormal(v1, v2);
            }
            //the index starts from numCoords
            vertexMap.put(index, coord);
            //edge point index corresponds to the edge index
            oddNodeMap.put(edge.getIndex(), index);
            index += 1;
        }
        return vertexMap;
    }

    public Map<Integer, Vector3d> computeEven() {
        final Map<Integer, Vector3d> vertexMap = new HashMap<>();
        for (int index = 0; index < vertices.size(); index++) {
            final Vector3d coord = computeEven(vertices.get(index));
            vertexMap.put(index, coord);
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
