import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangshao
 */
@Getter
@Setter
public class Vertex {
    //this is a vertex class
    private int index;
    private Vector3d coords;
    //neighbour polygons
    private List<Integer> triangleIndices;
    //neighbour vertices
    private List<Integer> vertexIndices;

    Vertex() {

    }

    Vertex(final int index, final Vector3d coordinates, final int nPolygons, final List<Integer> vertexIndices) {
        this.index = index;
        this.coords = coordinates;
        this.vertexIndices = vertexIndices;
        this.triangleIndices = new ArrayList<>(nPolygons);
    }

    Vertex(final int index, final Vector3d coordinates,final List<Integer> vertexIndices, final List<Integer> triangleIndices) {
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

    public int getNumNeighbours() {
        return this.triangleIndices.size();
    }

    //In triangular mesh, a vertex which is not connected 6 neightbours is extradinary
    public boolean isExtraordinary() {
        return this.getNumNeighbours() != 6;
    }
}
