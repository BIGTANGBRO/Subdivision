import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Region mesh for loop subdivision
 *
 * @author: tangshao
 * @Date: 05/03/2022
 */
@Getter
@Setter
public class RegionalLoop extends LoopScheme {

    private List<Triangle> trianglesSubdivide;
    private List<Triangle> trianglesNotSubdivide;
    private Set<Edge> edgesCount;
    private List<Edge> edgesNeedConnect;
    private Map<Edge, Triangle> trianglesNearSubMap;

    public RegionalLoop(final List<Triangle> triangles, final List<Vertex> vertices, final List<Edge> edges) {
        super(triangles, vertices, edges);
        this.trianglesSubdivide = new ArrayList<>();
        this.edgesCount = new HashSet<>();
        this.edgesNeedConnect = new ArrayList();
        this.trianglesNearSubMap = new HashMap<>();
        this.trianglesNotSubdivide = new ArrayList<>();
    }

    public void applyThreshold() {
        for (Triangle triangle : this.triangles) {
            boolean isSubdivide = true;
            List<Vertex> verticesTri = triangle.getVertices();
            for (Vertex vEach : verticesTri) {
                List<Vertex> verticesRemain = triangle.getRemain(vEach);
                double angle = MathUtils.getAngle(vEach.getCoords(), verticesRemain.get(0).getCoords(), verticesRemain.get(1).getCoords());
                if (angle < 20d) {
                    isSubdivide = false;
                }
            }
            if (isSubdivide) {
                this.trianglesSubdivide.add(triangle);
            } else {
                this.trianglesNotSubdivide.add(triangle);
            }
        }
    }

    /**
     * Compute the odd points based on triangle
     *
     * @return Map with index and coordinates
     */
    public Map<Integer, Vector3d> computeOdd() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = this.vertices.size();
        //iterate over the triangle;
        for (Triangle triangle : this.trianglesSubdivide) {
            //get the edges for each triangle
            List<Edge> edgesTri = triangle.getEdges();
            for (Edge edge : edgesTri) {
                if (this.edgesCount.contains(edge)) {
                    continue;
                } else {
                    this.edgesCount.add(edge);
                }
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
                this.oddNodeMap.put(edge.getIndex(), index);
                index += 1;

            }
        }
        return vertexMap;
    }


    public Map<Integer, Vector3d> computeEven() {
        final Map<Integer, Vector3d> vertexMap = new HashMap<>();
        Set<Vertex> verticesSet = new HashSet<>();
        for (Triangle triangle : this.trianglesSubdivide) {
            List<Vertex> verticesTri = triangle.getVertices();
            for (Vertex vEach : verticesTri) {
                if (verticesSet.contains(vEach)) {
                    continue;
                } else {
                    verticesSet.add(vEach);
                }
                final Vector3d coord = computeEven(vEach);
                vertexMap.put(vEach.getIndex(), coord);
            }
        }
        for (int index = 0; index < vertices.size(); index++) {
            final Vector3d coord = computeEven(vertices.get(index));
            vertexMap.put(index, coord);
        }
        return vertexMap;
    }

    private void getSlitEdges() {
        for (Triangle triangle : this.trianglesNotSubdivide) {
            List<Edge> edgesTri = triangle.getEdges();
            for (Edge edge : edgesTri) {
                if (this.edgesCount.contains(edge)) {
                    edgesNeedConnect.add(edge);
                    this.trianglesNearSubMap.put(edge, triangle);
                }
            }
        }
    }

    private Map<Integer, List<Integer>> createConnectTriangles(int index, Map<Integer, Vector3d> vertexMap) {
        Map<Integer, List<Integer>> faceMapConnect = new HashMap<>();
        for (Edge edge : this.edgesNeedConnect) {
            List<Integer> vertexIndices = new ArrayList<>();
            vertexIndices.add(edge.getA().getIndex());
            vertexIndices.add(edge.getB().getIndex());
            vertexIndices.add(this.oddNodeMap.get(edge.getIndex()));
            Vector3d subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(vertexIndices.get(0)), vertexMap.get(vertexIndices.get(1)), vertexMap.get(vertexIndices.get(2)));
            Vector3d faceNormal = this.trianglesNearSubMap.get(edge).getUnitNormal();
            if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                Collections.swap(vertexIndices, 1, 2);
            }

            faceMapConnect.put(index, vertexIndices);
            index += 1;
        }
        return faceMapConnect;
    }

    private Map<Integer, List<Integer>> createOriginalTriangles() {
        Map<Integer, List<Integer>> faceMapOld = new HashMap<>();
        //set the start index
        int index = this.trianglesSubdivide.size() * 4;

        for (Triangle triangle : this.trianglesNotSubdivide) {
            List<Integer> vertexIndices = new ArrayList<>();
            for (Vertex vEach : triangle.getVertices()) {
                vertexIndices.add(vEach.getIndex());
            }
            faceMapOld.put(index, vertexIndices);
            index += 1;
        }
        return faceMapOld;
    }

    public Map<Integer, List<Integer>> createTriangle(Map<Integer, Vector3d> vertexMap) {
        //connect the vertices
        //vertexMap is from computeOdd
        int faceCount = 0;
        final Map<Integer, List<Integer>> faceMap = new HashMap<>();
        //iterate over the original triangles
        for (final Triangle triangle : this.trianglesSubdivide) {
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
        faceMap.putAll(createOriginalTriangles());

        getSlitEdges();
        Map<Integer, List<Integer>> connectTriMap = createConnectTriangles(faceMap.size(), vertexMap);
        faceMap.putAll(connectTriMap);
        return faceMap;
    }
}
