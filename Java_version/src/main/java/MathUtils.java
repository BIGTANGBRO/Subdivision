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
}
