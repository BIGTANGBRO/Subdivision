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
    private Map<Integer, List<Integer>> trianglesTrackMap;

    //constructor
    public Square3Scheme(final List<Triangle> triangles, final List<Vertex> vertices, final List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
        this.triangleVertexMap = new HashMap<Integer, Integer>();
        this.trianglesTrackMap = new HashMap<Integer, List<Integer>>();
    }

    //similar to that in modified butterfly
    public List<Vertex> getPtsInOrder(final Triangle triangleInsert, final Vertex v) {
        final List<Edge> edgesConnect = triangleInsert.getConnectedEdges(v);
        final List<Integer> trianglesIndexNear = v.getTriangleIndices();
        final List<Triangle> trianglesNear = new ArrayList<>();
        for (final Integer triIndex : trianglesIndexNear) {
            trianglesNear.add(this.triangles.get(triIndex));
        }
        final List<Vertex> verticesNear = new ArrayList<>();

        //initial point insertion
        final Vertex p0 = edgesConnect.get(0).getOtherVertex(v);
        final Vertex p1 = edgesConnect.get(1).getOtherVertex(v);
        verticesNear.add(p0);
        verticesNear.add(p1);
        Vertex vOld = verticesNear.get(1);

        int iterN = 0;
        final int maxN = trianglesNear.size() * v.getNumVertices() * 2;

        while (verticesNear.size() != v.getNumVertices()) {
            if (iterN > maxN) {
                break;
            }
            for (final Triangle triangle : trianglesNear) {
                if (triangle.containVertices(vOld, v)) {
                    final Vertex vRemain = triangle.getRemain(v, vOld);
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

    private double getCoeff(final int n, final int j) {
        return (1d / 9d + 2d / 3d * Math.cos(2 * Math.PI * (double) j / (double) n) + 2d / 9d * Math.cos(4 * Math.PI * (double) j / (double) n)) / (double) n;
    }

    /**
     * The method for a triangle near an extraordinary vertex
     *
     * @param triangleEach The triangle
     * @return new vertex's coordinate
     */
    private Vector3d insertPointIrregular(final Triangle triangleEach) {
        //Map<Integer, Vector3d> stencils = new HashMap<>();
        final List<Vector3d> coords = new ArrayList<>();
        for (final Vertex v : triangleEach.getVertices()) {
            if (!v.isRegular()) {
                final int n = v.getNumVertices();
                final List<Vertex> verticesNear = getPtsInOrder(triangleEach, v);
                Vector3d sum = MathUtils.dotVal(8d / 9d, v.getCoords());
                if (n == 3) {
                    final double[] coeffs = new double[]{7d / 27d, -2d / 27d, -2d / 27d};
                    for (int i = 0; i < verticesNear.size(); i++) {
                        final Vertex vEach = verticesNear.get(i);
                        sum = MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], vEach.getCoords()));
                    }
                } else if (n == 4) {
                    final double[] coeffs = new double[]{7d / 36d, 1d / 27d, -5d / 36d, 1d / 27d};
                    for (int i = 0; i < verticesNear.size(); i++) {
                        final Vertex vEach = verticesNear.get(i);
                        sum = MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], vEach.getCoords()));
                    }
                } else {
                    for (int i = 0; i < verticesNear.size(); i++) {
                        final Vertex vEach = verticesNear.get(i);
                        final double coeff = getCoeff(n, i);
                        sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff, vEach.getCoords()));
                    }
                }
                //stencils.put(v.getIndex(), sum);
                coords.add(sum);
            }
        }
        final int nExtra = coords.size();
        Vector3d coordNewVertex = new Vector3d(0, 0, 0);
        for (final Vector3d coordEach : coords) {
            coordNewVertex = MathUtils.addVector(coordNewVertex, coordEach);
        }
        return MathUtils.dotVal(1d / (double) nExtra, coordNewVertex);
    }

    private Vector3d insertPointRegular(final Triangle triangleEach) {
        Vector3d coord = new Vector3d(0, 0, 0);
        for (final Vertex v : triangleEach.getVertices()) {
            coord = MathUtils.addVector(coord, v.getCoords());
        }
        return MathUtils.dotVal(1d / 3d, coord);
    }

    /*
    Create the new points
     */
    public Map<Integer, Vector3d> insertPoints() {
        int index = this.vertices.size();
        final Map<Integer, Vector3d> vertexMap = new HashMap<Integer, Vector3d>();
        for (final Triangle triangleEach : this.triangles) {
//            if (triangleEach.isNearExtraordinary()) {
//                vertexMap.put(index, this.insertPointIrregular(triangleEach));
//            } else {
//                vertexMap.put(index, this.insertPointRegular(triangleEach));
//            }
            //same for regular and extraordinary
            vertexMap.put(index, this.insertPointRegular(triangleEach));
            this.triangleVertexMap.put(triangleEach.getIndex(), index);
            index += 1;
        }
        return vertexMap;
    }

    private double getCoeffEven(final int n) {
        return (4d - 2d * Math.cos(Math.PI * 2d / (double) n)) / (9d * (double) n);
    }

    /**
     * Compute the even vertex
     *
     * @param vertex the original vertex
     * @return the new vertex's coordinate
     */
    private Vector3d computeEven(final Vertex vertex) {
        final int n = vertex.getNumVertices();
        final double beta = getCoeffEven(n);
        final List<Integer> neighbourVertices = vertex.getVertexIndices();
        final Vector3d coordV = vertex.getCoords();
        Vector3d vOther = new Vector3d(0, 0, 0);
        for (int i = 0; i < n; i++) {
            final int neighbourIndex = neighbourVertices.get(i);
            final Vertex v = this.vertices.get(neighbourIndex);
            final Vector3d coordNeighbour = v.getCoords();
            vOther = MathUtils.addVector(vOther, coordNeighbour);
        }
        final double coeff2 = 1 - n * beta;
        final Vector3d newVertex = MathUtils.addVector(MathUtils.dotVal(coeff2, coordV), MathUtils.dotVal(beta, vOther));
        return newVertex;
    }

    /*
    Compute the even vertex in the model
     */
    public Map<Integer, Vector3d> computeEven() {
        final Map<Integer, Vector3d> vertexMap = new HashMap<>();
        for (int index = 0; index < this.vertices.size(); index++) {
            final Vector3d coord = computeEven(this.vertices.get(index));
            vertexMap.put(index, coord);
        }
        return vertexMap;
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

        for (final Triangle triangle : this.triangles) {
            final List<Integer> triIndices = triangle.getTriangleIndices();
            final List<Edge> edgesEachTri = triangle.getEdges();
            List<Integer> triangleIndexTracking = new ArrayList<>();

            //get the average facenormal here
            double frac = 2d / 3d;
            Vector3d faceNormal = MathUtils.dotVal(frac, triangle.getUnitNormal());
            for (final Integer triIndex : triIndices) {
                final Triangle triangleNear = this.triangles.get(triIndex);
                faceNormal = MathUtils.addVector(faceNormal, MathUtils.dotVal((1d - frac) / (double) triIndices.size(), triangleNear.getUnitNormal()));
            }

            //each edge, 2 triangles created.
            for (final Edge edgeEachTri : edgesEachTri) {
                List<Integer> vertexIndices = new ArrayList<>();
                Triangle triangleThis = new Triangle();
                if (edgeSet.contains(edgeEachTri)) {
                    continue;
                }
                for (final Integer triIndex : triIndices) {
                    if (this.triangles.get(triIndex).containVertices(edgeEachTri.getA(), edgeEachTri.getB())) {
                        edgeSet.add(edgeEachTri);
                        triangleThis = this.triangles.get(triIndex);
                    }
                }
                //if no triangle near is founded, it is the boundary case
                if (triangleThis.getVertices().size() == 0) {
                    //connect
                    vertexIndices.add(this.triangleVertexMap.get(triangle.getIndex()));
                    vertexIndices.add(edgeEachTri.getA().getIndex());
                    vertexIndices.add(edgeEachTri.getB().getIndex());

                    final Vector3d subFaceNormal = MathUtils.getUnitNormal(verticesMap.get(vertexIndices.get(0)), verticesMap.get(vertexIndices.get(1)), verticesMap.get(vertexIndices.get(2)));
                    if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                        Collections.swap(vertexIndices, 1, 2);
                    }
                    faceMap.put(faceCount, vertexIndices);
                    triangleIndexTracking.add(faceCount);
                    faceCount += 1;
                } else {
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
                        triangleIndexTracking.add(faceCount);
                        vertexIndices = new ArrayList<>();
                        faceCount += 1;
                    }
                }
            }
            this.trianglesTrackMap.put(triangle.getIndex(), triangleIndexTracking);
        }
        return faceMap;
    }
}
