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

    public void getRMSError(InputModel inputModel) {

    }

    public double getHausorffDistance(List<Vertex> vertices1, List<Vertex> vertices2) {
        double maxDA = 0d;
        for (Vertex vertex1 : vertices1) {
            double minDist = Double.POSITIVE_INFINITY;
            for (Vertex vertex2 : vertices2) {
                double distance = MathUtils.minusVector(vertex1.getCoords(), vertex2.getCoords()).getMod();
                if (distance <= minDist) {
                    minDist = distance;
                }
            }
            if (minDist > maxDA) {
                maxDA = minDist;
            }
        }

        double maxDB = 0d;
        for (Vertex vertex1 : vertices2) {
            double minDist = Double.POSITIVE_INFINITY;
            for (Vertex vertex2 : vertices1) {
                double distance = MathUtils.minusVector(vertex1.getCoords(), vertex2.getCoords()).getMod();
                if (distance <= minDist) {
                    minDist = distance;
                }
            }
            if (minDist > maxDB) {
                maxDB = minDist;
            }
        }

        return Math.max(maxDA, maxDB);
    }
}
