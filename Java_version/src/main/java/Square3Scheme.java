import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author: tangshao
 * @Date: 29/01/2022
 */
@Getter
@Setter
public class Square3Scheme {
    private List<Triangle> triangles;
    private List<Vertex> vertices;
    private List<Edge> edges;
    //store the odd vertex corresponding to each triangle
    private Map<Integer, Integer> triangleVertexMap;

    public Square3Scheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
        this.triangleVertexMap = new HashMap<Integer, Integer>();
    }

    //similar to that in modified butterfly
    public List<Vertex> getPtsInOrder(Triangle triangleInsert, Vertex v) {
        List<Edge> edgesConnect = triangleInsert.getConnectedEdges(v);
        List<Integer> trianglesIndexNear = v.getTriangleIndices();
        List<Triangle> trianglesNear = new ArrayList<>();
        for (Integer triIndex : trianglesIndexNear) {
            trianglesNear.add(this.triangles.get(triIndex));
        }
        List<Vertex> verticesNear = new ArrayList<>();

        //initial point insertion
        Vertex p0 = edgesConnect.get(0).getOtherVertex(v);
        Vertex p1 = edgesConnect.get(1).getOtherVertex(v);
        verticesNear.add(p0);
        verticesNear.add(p1);
        Vertex vOld = verticesNear.get(1);

        int iterN = 0;
        int maxN = trianglesNear.size() * v.getNumVertices() * 2;

        while (verticesNear.size() != v.getNumVertices()) {
            if (iterN > maxN) {
                break;
            }
            for (Triangle triangle : trianglesNear) {
                if (triangle.containVertices(vOld, v)) {
                    Vertex vRemain = triangle.getRemain(v, vOld);
                    if (!verticesNear.contains(vRemain)) {
                        verticesNear.add(vRemain);
                        vOld = vRemain;
                    }
                }
                iterN += 1;
            }
            iterN += 1;
        }
        return verticesNear;
    }

    private double getCoeff(int n, int j) {
        return (1d / 9d + 2d / 3d * Math.cos(2 * Math.PI * (double) j / (double) n) + 2d / 9d * Math.cos(4 * Math.PI * (double) j / (double) n)) / (double) n;
    }

    public Vector3d insertPointIrregular(Triangle triangleEach) {
        //Map<Integer, Vector3d> stencils = new HashMap<>();
        List<Vector3d> coords = new ArrayList<>();
        for (Vertex v : triangleEach.getVertices()) {
            if (!v.isRegular()) {
                int n = v.getNumVertices();
                List<Vertex> verticesNear = getPtsInOrder(triangleEach, v);
                Vector3d sum = MathUtils.dotVal(8d / 9d, v.getCoords());
                switch (n) {
                    case 3:
                        double[] coeffs = new double[]{7d / 27d, -2d / 27d, -2d / 27d};
                        for (int i = 0; i < verticesNear.size(); i++) {
                            Vertex vEach = verticesNear.get(i);
                            MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], vEach.getCoords()));
                        }
                    case 4:
                        coeffs = new double[]{7d / 36d, 1d / 27d, -5d / 36d, 1d / 27d};
                        for (int i = 0; i < verticesNear.size(); i++) {
                            Vertex vEach = verticesNear.get(i);
                            MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], vEach.getCoords()));
                        }
                    default:
                        for (int i = 0; i < verticesNear.size(); i++) {
                            Vertex vEach = verticesNear.get(i);
                            double coeff = getCoeff(n, i);
                            MathUtils.addVector(sum, MathUtils.dotVal(coeff, vEach.getCoords()));
                        }
                }
                //stencils.put(v.getIndex(), sum);
                coords.add(sum);
            }
        }
        int nExtra = coords.size();
        Vector3d coordNewVertex = new Vector3d(0, 0, 0);
        for (Vector3d coordEach : coords) {
            coordNewVertex = MathUtils.addVector(coordNewVertex, coordEach);
        }
        return MathUtils.dotVal(1d / (double) nExtra, coordNewVertex);
    }

    public Vector3d insertPointRegular(Triangle triangleEach) {
        Vector3d coord = new Vector3d(0, 0, 0);
        for (Vertex v : triangleEach.getVertices()) {
            coord = MathUtils.addVector(coord, v.getCoords());
        }
        return MathUtils.dotVal(1d / 3d, coord);
    }

    public Map<Integer, Vector3d> insertPoints() {
        int index = this.vertices.size();
        Map<Integer, Vector3d> vertexMap = new HashMap<Integer, Vector3d>();
        for (Triangle triangleEach : this.triangles) {
            if (triangleEach.isNearExtraordinary()) {
                vertexMap.put(index, this.insertPointIrregular(triangleEach));
            } else {
                vertexMap.put(index, this.insertPointRegular(triangleEach));
            }
            this.triangleVertexMap.put(triangleEach.getIndex(), index);
            index += 1;
        }
        return vertexMap;
    }

    public Map<Integer, List<Integer>> createTriangle(Map<Integer, Vector3d> verticesMap) {
        final HashSet<Edge> edgeSet = new HashSet<>();
        final Map<Integer, List<Integer>> faceMap = new HashMap<>();
        int faceCount = 0;
        for (Triangle triangle : this.triangles) {
            List<Integer> triIndices = triangle.getTriangleIndices();
            List<Edge> edgesEachTri = triangle.getEdges();
            Vector3d faceNormal = triangle.getUnitNormal();
            //each edge, 2 triangles created.
            for (Edge edgeEachTri : edgesEachTri) {
                List<Integer> vertexIndices = new ArrayList<>();
                Triangle triangleThis = new Triangle();
                if (edgeSet.contains(edgeEachTri)) {
                    continue;
                }
                for (Integer triIndex : triIndices) {
                    if (this.triangles.get(triIndex).containVertices(edgeEachTri.getA(), edgeEachTri.getB())) {
                        edgeSet.add(edgeEachTri);
                        triangleThis = this.triangles.get(triIndex);
                    }
                }
                if (triangleThis.getVertices().size() == 0) {
                    vertexIndices.add(this.triangleVertexMap.get(triangle.getIndex()));
                    vertexIndices.add(edgeEachTri.getA().getIndex());
                    vertexIndices.add(edgeEachTri.getB().getIndex());
                    Vector3d subFaceNormal = MathUtils.getUnitNormal(verticesMap.get(vertexIndices.get(0)), verticesMap.get(vertexIndices.get(0)), verticesMap.get(vertexIndices.get(0)));
                    if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90 || MathUtils.getAngle(faceNormal, subFaceNormal) < 0) {
                        Collections.swap(vertexIndices, 1, 2);
                    }
                    faceMap.put(faceCount, vertexIndices);
                    faceCount += 1;
                } else {
                    for (Vertex vertexEachEdge : edgeEachTri.getVertices()) {
                        vertexIndices.add(vertexEachEdge.getIndex());
                        Integer vertex1 = this.triangleVertexMap.get(triangle.getIndex());
                        Integer vertex2 = this.triangleVertexMap.get(triangleThis.getIndex());
                        vertexIndices.add(vertex1);
                        vertexIndices.add(vertex2);
                        Vector3d subFaceNormal = MathUtils.getUnitNormal(vertexEachEdge.getCoords(), verticesMap.get(vertex1), verticesMap.get(vertex2));
                        if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90 || MathUtils.getAngle(faceNormal, subFaceNormal) < 0) {
                            Collections.swap(vertexIndices, 1, 2);
                        }
                        faceMap.put(faceCount, vertexIndices);
                        vertexIndices = new ArrayList<>();
                        faceCount += 1;
                    }
                }
            }
        }
        return faceMap;
    }
}
