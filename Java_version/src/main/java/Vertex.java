import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 顶点类
 * @author tangshao
 */
@Getter
@Setter
public class Vertex {
    //这是一个顶点类
    private int index;
    private Vector3d coords;
    //邻接多边形
    private List<Integer> triangleIndices;
    //邻接顶点
    private List<Integer> vertexIndices;

    public Vertex() {
        this.triangleIndices = new ArrayList<>();
        this.vertexIndices = new ArrayList<>();
    }

    public Vertex(int index) {
        this.index = index;
        coords = new Vector3d(0, 0, 0);
        this.triangleIndices = new ArrayList<>();
        this.vertexIndices = new ArrayList<>();
    }

    public Vertex(final int index, final Vector3d coordinates, final int nPolygons, final List<Integer> vertexIndices) {
        this.index = index;
        this.coords = coordinates;
        this.vertexIndices = vertexIndices;
        this.triangleIndices = new ArrayList<>(nPolygons);
    }

    public Vertex(final int index, final Vector3d coordinates, final List<Integer> vertexIndices, final List<Integer> triangleIndices) {
        this.index = index;
        this.coords = coordinates;
        this.vertexIndices = vertexIndices;
        this.triangleIndices = triangleIndices;
    }

    public void setX(final double x) {
        this.coords.setXVal(x);
    }

    public void setY(final double y) {
        this.coords.setYVal(y);
    }

    public void setZ(final double z) {
        this.coords.setZVal(z);
    }

    public int getNumTriangles() {
        return this.triangleIndices.size();
    }

    public int getNumVertices() {
        return this.vertexIndices.size();
    }

    public boolean isBoundary() {
        return this.getNumVertices() > this.getNumTriangles();
    }

    //在三角网格中，不连接6个邻居的顶点是异常的
    public boolean isRegular() {
        return this.getNumTriangles() == 6;
    }
    
    /**
     * 检查是否为异常顶点（非六邻域）
     * @return 如果是异常顶点返回true
     */
    public boolean isExtraordinary() {
        return !isRegular();
    }
    
    @Override
    public String toString() {
        return String.format("Vertex{index=%d, coords=%s}", index, coords);
    }
}