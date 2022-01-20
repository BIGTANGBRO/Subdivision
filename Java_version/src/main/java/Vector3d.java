import lombok.Getter;
import lombok.Setter;

/**
 * @author tangshao
 */
@Getter
@Setter
public class Vector3d {
    //coordinates
    private double xVal;
    private double yVal;
    private double zVal;

    Vector3d(final double x, final double y, final double z) {
        this.xVal = x;
        this.yVal = y;
        this.zVal = z;
    }

    public double[] getArr() {
        return new double[]{xVal, yVal, zVal};
    }

    public double getMod() {
        return Math.pow(Math.pow(xVal, 2) + Math.pow(yVal, 2) + Math.pow(zVal, 2), 0.5);
    }
}
