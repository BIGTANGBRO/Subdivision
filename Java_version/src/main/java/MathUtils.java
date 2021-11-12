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
}
