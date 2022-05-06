import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author: tangshao
 * @Date: 26/01/2022
 */
@Getter
@Setter
public class ModifiedButterflyScheme {
    protected List<Triangle> triangles;
    protected List<Vertex> vertices;
    protected List<Edge> edges;
    protected Map<Integer, Integer> oddNodeMap;
    protected Map<Integer, List<Integer>> trianglesTrackMap;
    protected double w = 0d;//or -1/16

    public ModifiedButterflyScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.oddNodeMap = new HashMap<>();
        this.edges = edges;
        this.trianglesTrackMap = new HashMap<>();
    }

    /**
     * Get the neighbour points in order
     *
     * @param vMain The main vertex
     * @param vNear The neighbour vertex
     * @return The list of vertices
     */
    public List<Vertex> getNeighbourPtsInOrder(Vertex vMain, Vertex vNear) {
        List<Integer> trianglesIndexNear = vMain.getTriangleIndices();
        List<Triangle> trianglesNear = new ArrayList<>();
        for (Integer triIndex : trianglesIndexNear) {
            trianglesNear.add(this.triangles.get(triIndex));
        }
        int iterN = 0;
        int maxN = trianglesNear.size() * vMain.getNumVertices() * 2;

        List<Vertex> verticesNear = new ArrayList<>();
        verticesNear.add(vNear);
        for (Triangle triangle : trianglesNear) {
            if (triangle.containVertices(vMain, vNear) && triangle.getRemainInDirection(vMain).get(0).getIndex() == vNear.getIndex()) {
                Vertex vRemain = triangle.getRemainInDirection(vMain).get(1);
                verticesNear.add(vRemain);
                break;
            }
        }

        //for some model with wrong topology, increase the robustness
        if (verticesNear.size() <= 1) {
            for (Triangle triangle : trianglesNear) {
                if (triangle.containVertices(vMain, vNear)) {
                    verticesNear.add(triangle.getRemain(vMain, vNear));
                    break;
                }
            }
        }

        Vertex vOld = verticesNear.get(1);
        while (verticesNear.size() != vMain.getNumVertices()) {
            if (iterN > maxN) {
                break;
            }
            for (Triangle triangle : trianglesNear) {
                if (triangle.containVertices(vMain, vOld)) {
                    Vertex vRemain = triangle.getRemain(vMain, vOld);
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

    //Both are not extraordinary
    public Map<String, List<Vertex>> getStenil(Vertex v1, Vertex v2) {
        List<Vertex> vertex1 = getNeighbourPtsInOrder(v1, v2);
        List<Vertex> vertex2 = getNeighbourPtsInOrder(v2, v1);
        Map<String, List<Vertex>> stencils = new HashMap<>();
        List<Vertex> aList = new ArrayList<>(2);
        List<Vertex> bList = new ArrayList<>(2);
        List<Vertex> cList = new ArrayList<>(4);
        List<Vertex> dList = new ArrayList<>(2);
        aList.add(v1);
        aList.add(v2);
        bList.add(vertex1.get(1));
        bList.add(vertex1.get(5));

        cList.add(vertex1.get(2));
        cList.add(vertex1.get(4));
        cList.add(vertex2.get(2));
        cList.add(vertex2.get(4));

        dList.add(vertex1.get(3));
        dList.add(vertex2.get(3));
        stencils.put("a", aList);
        stencils.put("b", bList);
        stencils.put("c", cList);
        stencils.put("d", dList);
        return stencils;
    }

    /**
     * Get the coefficients for n >= 5
     *
     * @param j index
     * @param n number of points
     * @return Coefficients
     */
    protected double getCoeff(int j, int n) {
        return (0.25d + Math.cos(2.0d * Math.PI * (double) j / (double) n) + 0.5 * Math.cos(4.0d * Math.PI * (double) j / (double) n)) / (double) n;
    }

    public double[] getCoeff(double w) {
        return new double[]{0.5d - w, 0.125d + 2d * w, -1d / 16d - w, w};
    }

    /**
     * Calculate the extraordinary coordinates
     *
     * @param vMain Main vertex
     * @param vNear Near vertex
     * @return The coordinate of the calculated vertex
     */
    public Vector3d calcualeExtraordinary(Vertex vMain, Vertex vNear) {
        //n is the number of neighbours
        List<Vertex> vertexIndices = getNeighbourPtsInOrder(vMain, vNear);
        int n = vertexIndices.size();
        if (n == 3) {
            double[] coeffs = new double[]{5d / 12d, -1d / 12d, -1d / 12d};
            Vector3d sum = new Vector3d(0, 0, 0);
            for (int i = 0; i < 3; i++) {
                Vertex v = vertexIndices.get(i);
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], v.getCoords()));
            }
            return MathUtils.addVector(sum, MathUtils.dotVal(3d / 4d, vMain.getCoords()));
        } else if (n == 4) {
            double[] coeffs = new double[]{3d / 8d, 0, -1d / 8d, 0};
            Vector3d sum = new Vector3d(0, 0, 0);
            for (int i = 0; i < 4; i++) {
                Vertex v = vertexIndices.get(i);
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], v.getCoords()));
            }
            return MathUtils.addVector(sum, MathUtils.dotVal(3d / 4d, vMain.getCoords()));
        } else {
            Vector3d sum = new Vector3d(0, 0, 0);
            for (int i = 0; i < n; i++) {
                double coeff = getCoeff(i, n);
                Vertex v = vertexIndices.get(i);
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff, v.getCoords()));
            }
            return MathUtils.addVector(sum, MathUtils.dotVal(3d / 4d, vMain.getCoords()));
        }
    }

    public Vector3d computeOdd(Vertex v1, Vertex v2) {
        if (v1.isRegular() && v2.isRegular()) {
            Map<String, List<Vertex>> stencils = getStenil(v1, v2);
            List<Vertex> alist = stencils.get("a");
            List<Vertex> blist = stencils.get("b");
            List<Vertex> clist = stencils.get("c");
            List<Vertex> dlist = stencils.get("d");
            double[] coeff = getCoeff(this.w);
            Vector3d sum = new Vector3d(0, 0, 0);
            for (Vertex vertexNear : alist) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[0], vertexNear.getCoords()));
            }
            for (Vertex vertexNear : blist) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[1], vertexNear.getCoords()));
            }
            for (Vertex vertexNear : clist) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[2], vertexNear.getCoords()));
            }
            for (Vertex vertexNear : dlist) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[3], vertexNear.getCoords()));
            }
            return sum;
        } else if (v1.isRegular()) {
            return calcualeExtraordinary(v2, v1);
        } else if (v2.isRegular()) {
            return calcualeExtraordinary(v1, v2);
        } else {
            Vector3d vCoord1 = calcualeExtraordinary(v1, v2);
            Vector3d vCoord2 = calcualeExtraordinary(v2, v1);
            return MathUtils.dotVal(0.5d, MathUtils.addVector(vCoord1, vCoord2));
        }
    }

    public Map<Integer, Vector3d> computeOdd() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = this.vertices.size();
        for (Edge edge : edges) {
            Vertex v1 = edge.getA();
            Vertex v2 = edge.getB();
//            if (v1.getIndex() == 273 || v1.getIndex() == 681){
//                System.out.println("here");
//            }
            Vector3d coord = computeOdd(v1, v2);
            vertexMap.put(index, coord);
            oddNodeMap.put(edge.getIndex(), index);
            index += 1;
        }
        return vertexMap;
    }

    public Map<Integer, List<Integer>> createTriangle(Map<Integer, Vector3d> vertexMap) {
        int faceCount = 0;
        Map<Integer, List<Integer>> faceMap = new HashMap<>();
        List<Integer> triangleIndexTracking = new ArrayList<>();

        for (final Triangle triangle : this.triangles) {
            final HashSet<Integer> oddVertexSet = new HashSet<>();
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
                triangleIndexTracking.add(faceCount);
                faceMap.put(faceCount, vertexIndices);
                faceCount += 1;
            }
            //connect the new created odd vertices to form a surface
            final List<Integer> oddVertexArr = new ArrayList<>(oddVertexSet);
            Vector3d subFaceNormal = MathUtils.getUnitNormal(vertexMap.get(oddVertexArr.get(0)), vertexMap.get(oddVertexArr.get(1)), vertexMap.get(oddVertexArr.get(2)));
            if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                Collections.swap(oddVertexArr, 1, 2);
            }
            triangleIndexTracking.add(faceCount);
            faceMap.put(faceCount, oddVertexArr);
            trianglesTrackMap.put(triangle.getIndex(), triangleIndexTracking);
            faceCount += 1;
        }
        return faceMap;
    }
}
