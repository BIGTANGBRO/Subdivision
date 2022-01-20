import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author: tangshao
 * @Date: 2022/1/18
 */
@Getter
@Setter
public class ModifiedButterflyScheme2 {
    private List<Triangle> triangles;
    private List<Vertex> vertices;
    private List<Edge> edges;
    private Map<Integer, Integer> oddNodeMap;
    private double w = 1.0d / 16.0d;

    public ModifiedButterflyScheme2(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
        this.oddNodeMap = new HashMap<>();
    }

    public ModifiedButterflyScheme2(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges, double w) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
        this.w = w;
    }

    public List<Integer> getNeighbourPtsInOrder(Vertex vMain, Vertex vNear) {
        Vector3d coordMain = vMain.getCoords();
        Vector3d coordNear = vNear.getCoords();
        Vector3d vDir1 = MathUtils.minusVector(coordNear, coordMain);
        final List<Double> degrees = new ArrayList<>();
        List<Integer> vertexIndices = vMain.getVertexIndices();
        for (Integer index : vertexIndices) {
            Vertex vertex = this.vertices.get(index);
            Vector3d coord = vertex.getCoords();
            Vector3d vDir2 = MathUtils.minusVector(coord, coordMain);
            double degreeAngle = MathUtils.getAngle(vDir1, vDir2);
            degrees.add(degreeAngle);
        }

        //sort the arrary from in order
        vertexIndices.sort(Comparator.comparingInt(degrees::indexOf));
        return vertexIndices;
    }

    //Both are not extraordinary
    public Map<String, List<Integer>> getStenil(Vertex v1, Vertex v2) {
        List<Integer> vertexIndices1 = getNeighbourPtsInOrder(v1, v2);
        List<Integer> vertexIndices2 = getNeighbourPtsInOrder(v2, v1);
        Map<String, List<Integer>> stencils = new HashMap<>();
        List<Integer> aList = new ArrayList<>();
        List<Integer> bList = new ArrayList<>();
        List<Integer> cList = new ArrayList<>();
        List<Integer> dList = new ArrayList<>();
        aList.add(v1.getIndex());
        aList.add(v2.getIndex());
        bList.add(vertexIndices1.get(1));
        bList.add(vertexIndices1.get(5));
        bList.add(vertexIndices2.get(1));
        bList.add(vertexIndices2.get(5));

        cList.add(vertexIndices1.get(2));
        cList.add(vertexIndices1.get(4));
        cList.add(vertexIndices2.get(2));
        cList.add(vertexIndices2.get(4));

        dList.add(vertexIndices2.get(3));
        dList.add(vertexIndices2.get(3));
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
    private double getCoeff(int j, int n) {
        return (0.25 + Math.cos(2.0d * Math.PI * (double) j / (double) n) + 0.5 * Math.cos(4.0d * Math.PI * (double) j / (double) n)) / (double) n;
    }

    public double[] getCoeff(double w) {
        return new double[]{0.5 - w, 0.125 + 2 * w, -1d / 16d - w, w};
    }

    public Vector3d calcualeExtraordinary(Vertex vMain, Vertex vNear) {
        //n is the number of neighbours
        List<Integer> vertexIndices = getNeighbourPtsInOrder(vMain, vNear);
        int n = vertexIndices.size();
        if (n == 3) {
            double[] coeffs = new double[]{5d / 12d, -1d / 12d, -1d / 12d};
            Vector3d sum = new Vector3d(0, 0, 0);
            for (int i = 0; i < 3; i++) {
                Vertex v = this.vertices.get((vertexIndices.get(i)));
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], v.getCoords()));
            }
            return MathUtils.addVector(sum, MathUtils.dotVal(3d / 4d, vMain.getCoords()));
        } else if (n == 4) {
            double[] coeffs = new double[]{3d / 8d, 0, -1d / 8d, 0};
            Vector3d sum = new Vector3d(0, 0, 0);
            for (int i = 0; i < 4; i++) {
                Vertex v = this.vertices.get((vertexIndices.get(i)));
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], v.getCoords()));
            }
            return MathUtils.addVector(sum, MathUtils.dotVal(3d / 4d, vMain.getCoords()));
        } else {
            Vector3d sum = new Vector3d(0, 0, 0);
            for (int i = 0; i < 3; i++) {
                double coeff = getCoeff(i, n);
                Vertex v = this.vertices.get((vertexIndices.get(i)));
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff, v.getCoords()));
            }
            return MathUtils.addVector(sum, MathUtils.dotVal(3d / 4d, vMain.getCoords()));
        }
    }

    public Vector3d computeOdd(Vertex v1, Vertex v2) {
        if (v1.getNumVertices() == 6 && v2.getNumVertices() == 6) {
            Map<String, List<Integer>> stencils = getStenil(v1, v2);
            List<Integer> alist = stencils.get("a");
            List<Integer> blist = stencils.get("b");
            List<Integer> clist = stencils.get("c");
            List<Integer> dlist = stencils.get("d");
            double[] coeff = getCoeff(-1d / 16d);
            Vector3d sum = new Vector3d(0, 0, 0);
            for (Integer vertexIndex : alist) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[0], vertices.get(vertexIndex).getCoords()));
            }

            for (Integer vertexIndex : blist) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[1], vertices.get(vertexIndex).getCoords()));
            }
            for (Integer vertexIndex : clist) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[2], vertices.get(vertexIndex).getCoords()));
            }

            for (Integer vertexIndex : dlist) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[3], vertices.get(vertexIndex).getCoords()));
            }
            return sum;
        } else if (v1.getNumVertices() == 6) {
            return calcualeExtraordinary(v2, v1);
        } else if (v2.getNumVertices() == 6) {
            return calcualeExtraordinary(v1, v2);
        } else {
            Vector3d vCoord1 = calcualeExtraordinary(v1, v2);
            Vector3d vCoord2 = calcualeExtraordinary(v2, v1);
            return MathUtils.dotVal(0.5, MathUtils.addVector(vCoord1, vCoord2));
        }
    }

    public Map<Integer, Vector3d> computeOdd() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = this.vertices.size();
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

    public Map<Integer, List<Integer>> createTriangle() {
        int faceCount = 0;
        Map<Integer, List<Integer>> faceMap = new HashMap<>();
        for (final Triangle triangle : this.triangles) {
            final HashSet<Integer> oddVertexSet = new HashSet<>();
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
