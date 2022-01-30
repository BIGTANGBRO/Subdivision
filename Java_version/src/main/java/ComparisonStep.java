import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.pow;

/**
 * @author: tangshao
 * @Date: 2021/12/8
 * The class used to assess the quality of the mesh model.
 */
public class ComparisonStep {
    ComparisonStep() {

    }

    //use x, y to define the coordinates
    public double getSphereZSqure(double x, double y) {
        return pow(127.0d, 2) - pow(x, 2) - pow(y, 2);
    }

    public double getSphereError(Map<Integer, Vector3d> vertices) {
        //get the data from vertices;
        double error = 0.0d;
        for (Map.Entry<Integer, Vector3d> entry : vertices.entrySet()) {
            Vector3d coord = entry.getValue();
            double x = coord.getXVal();
            double y = coord.getYVal();
            double zRef = Math.sqrt(getSphereZSqure(x, y));
            double z = coord.getZVal();
            double diff = Math.abs(Math.abs(z) - Math.abs(zRef));
            error += diff;
        }
        return error / (double) vertices.size();
    }

    public double getMinDistance(List<Vertex> vertices1, List<Vertex> vertices2) {
        double maxDistance = 0d;
        for (Vertex vertex1 : vertices1) {
            double minDist = Double.POSITIVE_INFINITY;
            for (Vertex vertex2 : vertices2) {
                double distance = MathUtils.minusVector(vertex1.getCoords(), vertex2.getCoords()).getMod();
                if (distance <= minDist) {
                    minDist = distance;
                }
            }
            if (minDist > maxDistance) {
                maxDistance = minDist;
            }
        }
        return maxDistance;
    }

    public List<Double> getMinDistanceDistribution(List<Vertex> vertices1, List<Vertex> vertices2) {
        List<Double> vertexDistances = new ArrayList<>(vertices1.size());
        for (Vertex vertex1 : vertices1) {
            double minDist = Double.POSITIVE_INFINITY;
            for (Vertex vertex2 : vertices2) {
                double distance = MathUtils.minusVector(vertex1.getCoords(), vertex2.getCoords()).getMod();
                if (distance <= minDist) {
                    minDist = distance;
                }
            }
            vertexDistances.add(minDist);
        }
        return vertexDistances;
    }

    public double getHausorffDistance(List<Vertex> vertices1, List<Vertex> vertices2) {
        double maxDA = getMinDistance(vertices1, vertices2);
        double maxDB = getMinDistance(vertices2, vertices1);

        return Math.max(maxDA, maxDB);
    }

    public void writeDistribution(List<Vertex> vertices1, List<Vertex> vertices2) throws IOException {
        List<Double> distribution1 = getMinDistanceDistribution(vertices1, vertices2);
        String fileName = "C:\\Users\\tangj\\Downloads\\distribution.csv";
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (Double distance : distribution1) {
            bw.write(Double.toString(distance) + "\n");
        }

        bw.close();
    }

    private static Vector3d getNormalForVertex(Vertex vertex, List<Triangle> triangles) {
        List<Integer> neighbours = vertex.getTriangleIndices();
        Vector3d vNorm = new Vector3d(0, 0, 0);
        for (Integer index : neighbours) {
            Triangle triangle = triangles.get(index);
            Vector3d normFace = triangle.getUnitNormal();
            vNorm = MathUtils.addVector(vNorm, normFace);
        }
        return MathUtils.dotVal(1d / (double) neighbours.size(), vNorm);
    }

    //static method to get the normal vector for vertices
    public static Map<Integer, Vector3d> getNormalForVertices(InputModel inputModel) {
        List<Vertex> vertices = inputModel.getVertices();
        List<Triangle> triangles = inputModel.getTriangles();
        Map<Integer, Vector3d> normMap = new HashMap<>();
        for (Vertex vertex : vertices) {
            Vector3d normV = getNormalForVertex(vertex, triangles);
            normMap.put(vertex.getIndex(), normV);
        }
        return normMap;
    }
}
