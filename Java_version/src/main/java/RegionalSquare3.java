import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author: tangshao
 * @Date: 06/05/2022
 */
@Getter
@Setter
public class RegionalSquare3 extends Square3Scheme {
    private List<Triangle> trianglesSubdivide;
    private List<Triangle> trianglesNotSubdivide;
    private Map<Triangle, Integer> trianglesNearSubMap;
    private List<Triangle> trianglesConnect;
    private Set<Edge> edgesNotFlip;


    public RegionalSquare3(final List<Triangle> triangles, final List<Vertex> vertices, final List<Edge> edges) {
        super(triangles, vertices, edges);
        this.trianglesNotSubdivide = new ArrayList<>();
        this.trianglesSubdivide = new ArrayList<>();
        this.trianglesConnect = new ArrayList<>();
        this.trianglesNearSubMap = new HashMap<>();
        this.edgesNotFlip = new HashSet<>();
    }

    /**
     * Select which triangle to subdivide
     */
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
        //add the boundary case to the triangle categories
        this.getTrianglesNearSubdivide();
    }

    /**
     * Find the triangles which are near the subdivision triangles
     */
    private void getTrianglesNearSubdivide() {
        for (Triangle triangle : this.trianglesNotSubdivide) {
            List<Integer> triIndices = triangle.getTriangleIndices();
            int count = 0;
            for (Integer triIndex : triIndices) {
                if (this.trianglesSubdivide.contains(this.triangles.get(triIndex))) {
                    count += 1;
                }
            }

            if (count == 3) {
                this.trianglesSubdivide.add(triangle);
            } else if (count == 1 || count == 2) {
                this.trianglesNearSubMap.put(triangle, count);
                this.edgesNotFlip.addAll(triangle.getEdges());
            } else {
                this.trianglesConnect.add(triangle);
                this.edgesNotFlip.addAll(triangle.getEdges());
            }
        }
    }

    /**
     * Insert the odd points
     *
     * @return Map with vertex indices and coordinates
     */
    public Map<Integer, Vector3d> insertPoints() {
        int index = this.vertices.size();
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        for (Triangle triangleEach : this.trianglesSubdivide) {
            vertexMap.put(index, this.insertPointRegular(triangleEach));
            //Each new point corresponds to a triangle
            this.triangleVertexMap.put(triangleEach.getIndex(), index);
            index += 1;
        }
        return vertexMap;
    }

    public Map<Integer, Vector3d> computeEven() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        Set<Integer> vReplace = new HashSet<>();
        for (Triangle triangleEach : this.trianglesSubdivide) {
            for (Vertex v : triangleEach.getVertices()) {
                if (!vReplace.contains(v.getIndex())) {
                    vReplace.add(v.getIndex());
                    Vector3d coord = computeEven(v);
                    vertexMap.put(v.getIndex(), coord);
                }
            }
        }
        return vertexMap;
    }

    private Map<Integer, List<Integer>> createOriginalTriangles(int indexStart) {
        Map<Integer, List<Integer>> faceMapOld = new HashMap<>();
        //set the start index
        int index = indexStart;
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
            List<Integer> trianglesNear = triangle.getTriangleIndices();
            Vector3d faceNormal = triangle.getUnitNormal();
            //decide the case for the boundary triangle
            int count = this.trianglesNearSubMap.get(triangle);
            if (count == 1) {
                for (Integer triangleIndex : trianglesNear) {
                    Triangle triangleWithPoint = this.triangles.get(triangleIndex);
                    //find the one that will be subdivided
                    if (this.trianglesSubdivide.contains(triangleWithPoint)) {
                        int newVertexIndex = this.triangleVertexMap.get(triangleIndex);
                        int oppoVertexIndex = 0;
                        List<Vertex> verticesEachTri = triangle.getVertices();
                        List<Vertex> sideVertices = new ArrayList<>();
                        for (Vertex v : verticesEachTri) {
                            if (triangleWithPoint.containVertex(v)) {
                                sideVertices.add(v);
                            } else {
                                oppoVertexIndex = v.getIndex();
                            }
                        }

                        for (Vertex v : sideVertices) {
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
                //count == 2
                List<Integer> vertexIndices = new ArrayList<>();
                List<Edge> edgesEachTri = triangle.getEdges();
                List<Edge> edgesNear = new ArrayList<>(2);
                List<Triangle> trianglesWithPoint = new ArrayList<>(2);

                for (Integer triangleIndex : trianglesNear) {
                    Triangle triangleWithPoint = this.triangles.get(triangleIndex);
                    if (this.trianglesSubdivide.contains(triangleWithPoint)) {
                        trianglesWithPoint.add(triangleWithPoint);
                        for (Edge edge : edgesEachTri) {
                            if (triangleWithPoint.getEdges().contains(edge)) {
                                edgesNear.add(edge);
                            }
                        }
                    }
                }

                int commonVertexIndex = 0;
                for (Vertex v: edgesNear.get(0).getVertices()){
                    if (edgesNear.get(1).has(v)){
                        commonVertexIndex = v.getIndex();
                    }
                }

                int triPoint1 = this.triangleVertexMap.get(trianglesWithPoint.get(0).getIndex());
                int triPoint2 = this.triangleVertexMap.get(trianglesWithPoint.get(1).getIndex());
                int edgePoint1 = edgesNear.get(0).getOtherVertex(this.vertices.get(commonVertexIndex)).getIndex();
                int edgePoint2 = edgesNear.get(1).getOtherVertex(this.vertices.get(commonVertexIndex)).getIndex();

                //divided into 3 triangles in each scenario
                vertexIndices.add(triPoint1);
                vertexIndices.add(commonVertexIndex);
                vertexIndices.add(triPoint2);
                Vector3d subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(vertexIndices.get(0)), vertexMap.get(vertexIndices.get(1)), vertexMap.get(vertexIndices.get(2)));
                if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                    Collections.swap(vertexIndices, 1, 2);
                }
                faceMap.put(index, vertexIndices);
                index += 1;

                vertexIndices = new ArrayList<>();
                vertexIndices.add(triPoint1);
                vertexIndices.add(edgePoint2);
                vertexIndices.add(triPoint2);
                subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(vertexIndices.get(0)), vertexMap.get(vertexIndices.get(1)), vertexMap.get(vertexIndices.get(2)));
                if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                    Collections.swap(vertexIndices, 1, 2);
                }
                faceMap.put(index, vertexIndices);
                index += 1;

                vertexIndices = new ArrayList<>();
                vertexIndices.add(triPoint1);
                vertexIndices.add(edgePoint2);
                vertexIndices.add(edgePoint1);
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

    /**
     * The method that connects the vertices and create the triangles
     *
     * @param verticesMap Map with vertex index and coordinate
     * @return Triangle map with index and vertex index
     */
    public Map<Integer, List<Integer>> createTriangle(final Map<Integer, Vector3d> verticesMap) {
        //initialization
        final HashSet<Edge> edgeSet = new HashSet<>();
        final Map<Integer, List<Integer>> faceMap = new HashMap<>();
        int faceCount = 0;

        for (final Triangle triangle : this.trianglesSubdivide) {
            final List<Integer> triIndices = triangle.getTriangleIndices();
            final List<Edge> edgesEachTri = triangle.getEdges();

            //get the average facenormal here
            double frac = 2d / 3d;
            Vector3d faceNormal = MathUtils.dotVal(frac, triangle.getUnitNormal());
            for (final Integer triIndex : triIndices) {
                final Triangle triangleNear = this.triangles.get(triIndex);
                faceNormal = MathUtils.addVector(faceNormal, MathUtils.dotVal((1d - frac) / (double) triIndices.size(), triangleNear.getUnitNormal()));
            }

            //each edge, 2 triangles created.
            for (final Edge edgeEachTri : edgesEachTri) {
                if (this.edgesNotFlip.contains(edgeEachTri) || edgeSet.contains(edgeEachTri)) {
                    continue;
                }
                List<Integer> vertexIndices = new ArrayList<>();
                Triangle triangleThis = new Triangle();

                for (final Integer triIndex : triIndices) {
                    if (this.triangles.get(triIndex).containVertices(edgeEachTri.getA(), edgeEachTri.getB())) {
                        edgeSet.add(edgeEachTri);
                        triangleThis = this.triangles.get(triIndex);
                    }
                }
                //normal case
                for (final Vertex vertexEachEdge : edgeEachTri.getVertices()) {
                    //connect
                    final Integer vertex1 = this.triangleVertexMap.get(triangle.getIndex());
                    final Integer vertex2 = this.triangleVertexMap.get(triangleThis.getIndex());
                    vertexIndices.add(vertexEachEdge.getIndex());
                    vertexIndices.add(vertex1);
                    vertexIndices.add(vertex2);
                    final Vector3d subFaceNormal = MathUtils.getUnitNormal(verticesMap.get(vertexIndices.get(0)), verticesMap.get(vertexIndices.get(1)), verticesMap.get(vertexIndices.get(2)));
                    if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                        Collections.swap(vertexIndices, 1, 2);
                    }

                    faceMap.put(faceCount, vertexIndices);
                    vertexIndices = new ArrayList<>();
                    faceCount += 1;
                }
            }
        }
        int indexStart1 = faceMap.size();
        faceMap.putAll(createOriginalTriangles(indexStart1));
        int indexStart2 = faceMap.size();
        faceMap.putAll(this.createBoundaryTriangles(indexStart2, verticesMap));

        return faceMap;
    }
}
