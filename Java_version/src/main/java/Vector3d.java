import lombok.Getter;
import lombok.Setter;

/**
 * 三维向量类
 * @author tangshao
 */
@Getter
@Setter
public class Vector3d {
    //坐标
    private double xVal;
    private double yVal;
    private double zVal;

    public Vector3d(final double x, final double y, final double z) {
        this.xVal = x;
        this.yVal = y;
        this.zVal = z;
    }

    public double[] getArr() {
        return new double[]{xVal, yVal, zVal};
    }

    /**
     * 获取向量的模长
     * @return 向量的模长
     */
    public double getMod() {
        return Math.sqrt(xVal*xVal + yVal*yVal + zVal*zVal);
    }

    /**
     * 标准化向量
     * @return 标准化后的单位向量
     */
    public Vector3d normalize() {
        double mod = getMod();
        if (mod < Constant.EPSILON) {
            return new Vector3d(0, 0, 0); // 零向量无法标准化
        }
        return new Vector3d(xVal/mod, yVal/mod, zVal/mod);
    }

    @Override
    public String toString() {
        return String.format("Vector3d{x=%.3f, y=%.3f, z=%.3f}", xVal, yVal, zVal);
    }
}