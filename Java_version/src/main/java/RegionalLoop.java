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
    private List<Triangle> trianglesConnect;
    private Map<Triangle, Integer> trianglesNearSubMap;
    private Set<Edge> edgesCount;

    public RegionalLoop(final List<Triangle> triangles, final List<Vertex> vertices, final List<Edge> edges) {
        super(triangles, vertices, edges);
        this.trianglesSubdivide = new ArrayList<>();
        this.edgesCount = new HashSet<>();
        this.trianglesNotSubdivide = new ArrayList<>();
        this.trianglesConnect = new ArrayList<>();
        this.trianglesNearSubMap = new HashMap<>();
    }

    public void applyThreshold() {
        for (Triangle triangle : this.triangles) {
            boolean isSubdivide = true;
            List<Vertex> verticesTri = triangle.getVertices();
            for (Vertex vEach : verticesTri) {
                List<Vertex> verticesRemain = triangle.getRemain(vEach);
                double angle = MathUtils.getAngle(vEach.getCoords(), verticesRemain.get(0).getCoords(), verticesRemain.get(1).getCoords());
                if (angle < 15d) {
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

    private void getTrianglesNearSubdivide() {
        for (Triangle triangle : this.trianglesNotSubdivide) {
            List<Edge> edges = triangle.getEdges();
            int count = 0;
            for (Edge edge : edges) {
                if (edgesCount.contains(edge)) {
                    count += 1;
                }
            }
            if (count == 3) {
                this.trianglesSubdivide.add(triangle);
            } else if (count == 1 || count == 2) {
                this.trianglesNearSubMap.put(triangle, count);
            } else {
                this.trianglesConnect.add(triangle);
            }
        }
    }


    private Map<Integer, List<Integer>> createOriginalTriangles() {
        Map<Integer, List<Integer>> faceMapOld = new HashMap<>();
        //set the start index
        int index = this.trianglesSubdivide.size() * 4;

        for (Triangle triangle : this.trianglesConnect) {
            List<Integer> vertexIndices = new ArrayList<>();
            for (Vertex vEach : triangle.getVertices()) {
                vertexIndices.add(vEach.getIndex());
            }
            faceMapOld.put(index, vertexIndices);
            index += 1;
        }
        return faceMapOld;
    }

    private Map<Integer, List<Integer>> createBoundaryTriangles(int index, Map<Integer, Vector3d> vertexMap) {
        final Map<Integer, List<Integer>> faceMap = new HashMap<>();

        for (Triangle triangle : this.trianglesNearSubMap.keySet()) {
            int count = this.trianglesNearSubMap.get(triangle);
            List<Edge> edges = triangle.getEdges();
            Vector3d faceNormal = triangle.getUnitNormal();
            if (count == 1) {
                for (Edge edge : edges) {
                    if (oddNodeMap.containsKey(edge.getIndex())) {
                        int newVertexIndex = oddNodeMap.get(edge.getIndex());
                        int oppoVertexIndex = triangle.getRemain(edge.getA(), edge.getB()).getIndex();
                        for (Vertex v : edge.getVertices()) {
                            List<Integer> vertexIndices = new ArrayList<>();
                            vertexIndices.add(newVertexIndex);
                            vertexIndices.add(oppoVertexIndex);
                            vertexIndices.add(v.getIndex());
                            Vector3d subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(vertexIndices.get(0)), vertexMap.get(vertexIndices.get(1)), vertexMap.get(vertexIndices.get(2)));
                            if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                                Collections.swap(vertexIndices, 1, 2);
                            }
                            faceMap.put(index, vertexIndices);
                            index += 1;
                        }
                        break;
                    }
                }
            } else {
                //count == 2;
                List<Integer> vertexIndices = new ArrayList<>();
                List<Edge> edgesHasPoint = new ArrayList<>(2);
                for (Edge edge : edges) {
                    if (oddNodeMap.containsKey(edge.getIndex())) {
                        edgesHasPoint.add(edge);
                    }
                }
                List<Vertex> verticesRemain = new ArrayList<>();
                //first point creation
                for (Vertex v : triangle.getVertices()) {
                    if (edgesHasPoint.get(0).has(v) && edgesHasPoint.get(1).has(v)) {
                        vertexIndices.add(v.getIndex());
                    } else {
                        verticesRemain.add(v);
                    }
                }
                vertexIndices.add(oddNodeMap.get(edgesHasPoint.get(0).getIndex()));
                vertexIndices.add(oddNodeMap.get(edgesHasPoint.get(1).getIndex()));
                Vector3d subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(vertexIndices.get(0)), vertexMap.get(vertexIndices.get(1)), vertexMap.get(vertexIndices.get(2)));
                if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                    Collections.swap(vertexIndices, 1, 2);
                }
                faceMap.put(index, vertexIndices);
                index += 1;

                //second triangle
                vertexIndices = new ArrayList<>();
                vertexIndices.add(verticesRemain.get(0).getIndex());
                vertexIndices.add(oddNodeMap.get(edgesHasPoint.get(0).getIndex()));
                vertexIndices.add(oddNodeMap.get(edgesHasPoint.get(1).getIndex()));
                subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(vertexIndices.get(0)), vertexMap.get(vertexIndices.get(1)), vertexMap.get(vertexIndices.get(2)));
                if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                    Collections.swap(vertexIndices, 1, 2);
                }
                faceMap.put(index, vertexIndices);
                index += 1;

                //third point
                vertexIndices = new ArrayList<>();
                vertexIndices.add(verticesRemain.get(1).getIndex());
                vertexIndices.add(verticesRemain.get(0).getIndex());
                for (Edge edge : edgesHasPoint) {
                    if (edge.has(verticesRemain.get(1))) {
                        vertexIndices.add(oddNodeMap.get(edge.getIndex()));
                        break;
                    }
                }
                subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(vertexIndices.get(0)), vertexMap.get(vertexIndices.get(1)), vertexMap.get(vertexIndices.get(2)));
                if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                    Collections.swap(vertexIndices, 1, 2);
                }
                faceMap.put(index, vertexIndices);
                index += 1;
            }
        }
        return faceMap;
    }

    public Map<Integer, List<Integer>> createTriangle(Map<Integer, Vector3d> vertexMap) {
        //connect the vertices
        //vertexMap is from computeOdd
        this.getTrianglesNearSubdivide();
        int faceCount = 0;
        final Map<Integer, List<Integer>> faceMap = new HashMap<>();

        //iterate over the original triangles
        for (final Triangle triangle : this.trianglesSubdivide) {
            //for track map
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
                faceCount += 1;
            }
            //connect the new created odd vertices to form a surface
            final List<Integer> oddVertexArr = new ArrayList<>(oddVertexSet);
            Vector3d subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(oddVertexArr.get(0)), vertexMap.get(oddVertexArr.get(1)), vertexMap.get(oddVertexArr.get(2)));
            if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                Collections.swap(oddVertexArr, 1, 2);
            }
            faceMap.put(faceCount, oddVertexArr);
            faceCount += 1;
        }
        faceMap.putAll(createOriginalTriangles());
        int indexStart = faceMap.size();
        faceMap.putAll(this.createBoundaryTriangles(indexStart, vertexMap));

        return faceMap;
    }
}
