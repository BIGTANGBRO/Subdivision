import java.util.List;

/**
 * 数学工具类
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

    /**
     * 获取两个向量之间的夹角（以度为单位）
     *
     * @param v1 向量1
     * @param v2 向量2
     * @return 两向量之间的角度（度）
     */
    public static double getAngle(Vector3d v1, Vector3d v2) {
        double dotProduct = v1.getXVal() * v2.getXVal() + v1.getYVal() * v2.getYVal() + v1.getZVal() * v2.getZVal();
        double magnitudeA = v1.getMod();
        double magnitudeB = v2.getMod();
        
        if (magnitudeA < Constant.EPSILON || magnitudeB < Constant.EPSILON) {
            return 0.0; // 零向量的情况
        }
        
        double cosTheta = dotProduct / (magnitudeA * magnitudeB);
        // 限制cosTheta范围在[-1, 1]之间，避免浮点误差导致的acos无效输入
        cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));
        
        return Math.toDegrees(Math.acos(cosTheta));
    }

    public static double getAngle(Vector3d v1, Vector3d v2, Vector3d v3) {
        Vector3d edge1 = MathUtils.minusVector(v1, v2); // 从v2到v1的向量
        Vector3d edge2 = MathUtils.minusVector(v1, v3); // 从v3到v1的向量

        return getAngle(edge1, edge2);
    }


    public static double getMod(double x, double y, double z) {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public static double getMod(Vector3d vec) {
        double x = vec.getXVal();
        double y = vec.getYVal();
        double z = vec.getZVal();
        return Math.sqrt(x*x + y*y + z*z);
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
        if (mod < Constant.EPSILON) {
            return new Vector3d(0, 0, 0); // 退化三角形
        }
        return new Vector3d(x / mod, y / mod, z / mod);
    }

    public static double getAverage(List<Double> list) {
        if (list.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Double val : list) {
            sum += val;
        }
        return sum / list.size();
    }

    public static double getVariance(List<Double> list, double average) {
        if (list.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Double val : list) {
            double diff = val - average;
            sum += diff * diff;
        }
        return sum / list.size();
    }
}