import java.util.List;

/**
 * @author tangshao
 */
public class MathUtils {
    public static Vector3d addVal(final double val, final Vector3d vector3d) {
        final double xVal = vector3d.getXVal() + val;
        final double yVal = vector3d.getYVal() + val;
        final double zVal = vector3d.getZVal() + val;
        return new Vector3d(xVal, yVal, zVal);
    }

    public static Vector3d addVector(final Vector3d v1, final Vector3d v2) {
        final double xVal = v1.getXVal() + v2.getXVal();
        final double yVal = v1.getYVal() + v2.getYVal();
        final double zVal = v1.getZVal() + v2.getZVal();
        return new Vector3d(xVal, yVal, zVal);
    }

    public static Vector3d minusVector(final Vector3d v1, final Vector3d v2) {
        final double xVal = v1.getXVal() - v2.getXVal();
        final double yVal = v1.getYVal() - v2.getYVal();
        final double zVal = v1.getZVal() - v2.getZVal();
        return new Vector3d(xVal, yVal, zVal);
    }

    public static Vector3d addVector(final Vector3d v1, final Vector3d v2, final Vector3d v3) {
        final double xVal = v1.getXVal() + v2.getXVal() + v3.getXVal();
        final double yVal = v1.getYVal() + v2.getYVal() + v3.getYVal();
        final double zVal = v1.getZVal() + v2.getZVal() + v3.getZVal();
        return new Vector3d(xVal, yVal, zVal);
    }

    public static Vector3d addVector(final Vector3d v1, final Vector3d v2, final Vector3d v3, final Vector3d v4) {
        final double xVal = v1.getXVal() + v2.getXVal() + v3.getXVal() + v4.getXVal();
        final double yVal = v1.getYVal() + v2.getYVal() + v3.getYVal() + v4.getYVal();
        final double zVal = v1.getZVal() + v2.getZVal() + v3.getZVal() + v4.getZVal();
        return new Vector3d(xVal, yVal, zVal);
    }

    public static Vector3d dotVal(final double val, final Vector3d vector3d) {
        final double xVal = vector3d.getXVal() * val;
        final double yVal = vector3d.getYVal() * val;
        final double zVal = vector3d.getZVal() * val;
        return new Vector3d(xVal, yVal, zVal);
    }

    public static Vector3d dotVector(final Vector3d v1, final Vector3d v2) {
        final double xVal = v1.getXVal() * v2.getXVal();
        final double yVal = v1.getYVal() * v2.getYVal();
        final double zVal = v1.getZVal() * v2.getZVal();
        return new Vector3d(xVal, yVal, zVal);
    }

    public static double getAngle(Vector3d v1, Vector3d v2) {
        double aDotb = v1.getXVal() * v2.getXVal() + v1.getYVal() * v2.getYVal() + v1.getZVal() * v2.getZVal();
        double modA = Math.pow((Math.pow(v1.getXVal(), 2) + Math.pow(v1.getYVal(), 2) + Math.pow(v1.getZVal(), 2)), 0.5);
        double modB = Math.pow((Math.pow(v2.getXVal(), 2) + Math.pow(v2.getYVal(), 2) + Math.pow(v2.getZVal(), 2)), 0.5);
        return Math.toDegrees(Math.acos(aDotb / (modA * modB)));
    }

    public static double getMod(double x, double y, double z) {
        return Math.pow(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2), 0.5);
    }

    public static double getMod(Vector3d vec) {
        double x = vec.getXVal();
        double y = vec.getYVal();
        double z = vec.getZVal();
        return Math.pow(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2), 0.5);
    }

    public static double getSum(Vector3d vec) {
        return vec.getXVal() + vec.getYVal() + vec.getZVal();
    }

    public static Vector3d getUnitNormal(Vector3d coord1, Vector3d coord2, Vector3d coord3) {
        Vector3d vec1 = MathUtils.minusVector(coord1, coord2);
        Vector3d vec2 = MathUtils.minusVector(coord1, coord3);

        double x = vec1.getYVal() * vec2.getZVal() - vec1.getZVal() * vec2.getYVal();
        double y = vec1.getZVal() * vec2.getXVal() - vec1.getXVal() * vec2.getZVal();
        double z = vec1.getXVal() * vec2.getYVal() - vec1.getYVal() * vec2.getXVal();

        double mod = MathUtils.getMod(x, y, z);
        return new Vector3d(x / mod, y / mod, z / mod);
    }

    public static double getAverage(List<Double> list) {
        double sum = 0;
        for (Double val : list) {
            sum += val;
        }
        return sum / list.size();
    }

    public static double getVariance(List<Double> list, double average){
        double sum = 0;
        for (Double val : list) {
            double diff = Math.pow((val - average),2);
            sum += diff;
        }
        return sum / list.size();
    }
}
