import java.util.Map;

import static java.lang.Math.pow;

/**
 * @author: tangshao
 * @Date: 2021/12/8
 */
public class ComparisonStep {
    ComparisonStep() {

    }

    //use x, y to define the coordinates
    public double getSphereZSqure(double x, double y) {
        return pow(127.0d, 2) - pow(x, 2) - pow(y, 2);
    }

    public double getError(Map<Integer, Vector3d> vertices) {
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

    //quantify the value of the mesh difference
}
