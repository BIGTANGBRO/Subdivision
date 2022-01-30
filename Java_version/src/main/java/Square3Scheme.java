import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Square3Scheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
    }

    public List<Vertex> getPtsInOrder(Triangle triangleEach, Vertex v) {
        List<Edge> edgesConnect = triangleEach.getConnectedEdges(v);
        List<Integer> trianglesIndexNear = v.getTriangleIndices();
        List<Triangle> trianglesNear = new ArrayList<>();
        for (Integer triIndex : trianglesIndexNear) {
            trianglesNear.add(this.triangles.get(triIndex));
        }
        List<Vertex> verticesNear = new ArrayList<>();

        for (Edge edge : edgesConnect) {
            if (edge.getA().equals(v)) {
                verticesNear.add(edge.getB());
            } else {
                verticesNear.add(edge.getA());
            }
        }

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
        return (1d / 9d + 2d / 3d * Math.cos(2 * Math.PI * j / n) + 2d / 9d * Math.cos(4 * Math.PI * j / n)) / n;
    }

    public Vector3d insertPointIrregular(Triangle triangleEach) {
        //Map<Integer, Vector3d> stencils = new HashMap<>();
        List<Vector3d> coords = new ArrayList<>();
        for (Vertex v : triangleEach.getVertices()) {
            if (!v.isRegular()) {
                int n = v.getNumVertices();
                List<Vertex> verticesNear = getPtsInOrder(triangleEach, v);
                Vector3d sum = new Vector3d(0, 0, 0);
                switch (n) {
                    case 3:
                        double[] coeffs = new double[]{7d / 27d, -2d / 27d, -2d / 27d};
                        for (int i = 0; i < verticesNear.size(); i++) {
                            Vertex vEach = verticesNear.get(i);
                            MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], vEach.getCoords()));
                        }
                    case 4:
                        coeffs = new double[]{7d / 36d, 1d / 27d, 1d / 27d, -5d / 36d};
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
            MathUtils.addVector(coordNewVertex, coordEach);
        }
        return MathUtils.dotVal(1d / (double) nExtra, coordNewVertex);
    }

    public Vector3d insertPointRegular1(Triangle triangleEach) {
        Vector3d coord = new Vector3d(0, 0, 0);
        for (Vertex v : triangleEach.vertices) {
            coord = MathUtils.addVector(coord, v.getCoords());
        }
        return MathUtils.dotVal(1d / 3d, coord);
    }

    public Map<Integer, Vector3d> insertPoints() {
        int index = vertices.size();
        Map<Integer, Vector3d> vertexMap = new HashMap<Integer, Vector3d>();
        for (Triangle triangleEach : this.triangles) {
            if (triangleEach.isNearExtraordinary()) {
                Vector3d coord = this.insertPointIrregular(triangleEach);
                vertexMap.put(index, coord);
                index += 1;
            } else {
                vertexMap.put(index, this.insertPointRegular1(triangleEach));
                index += 1;
            }
        }
        return vertexMap;
    }
}
