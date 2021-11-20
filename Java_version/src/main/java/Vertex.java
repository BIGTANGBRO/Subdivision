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
    private List<Triangle> triangles;
    private List<Integer> triangleIndices;
    //neighbour vertices
    private List<Vertex> neighbourVertices;
    private List<Integer> vertexIndices;

    Vertex() {

    }

    //In triangular mesh, a vertex which is not connected 6 neightbours is extradinary
    Vertex(final int index, final Vector3d coordinates, final int nPolygons, final List<Integer> vertexIndices) {
        this.index = index;
        this.coords = coordinates;
        this.triangles = new ArrayList<>(3);
        this.neighbourVertices = new ArrayList<>(3);
        this.vertexIndices = vertexIndices;
        this.triangleIndices = new ArrayList<>(nPolygons);
    }

    //In triangular mesh, a vertex which is not connected 6 neightbours is extradinary
    Vertex(final int index, final Vector3d coordinates, final int nPolygons, final List<Integer> vertexIndices, final List<Integer> triangleIndices) {
        this.index = index;
        this.coords = coordinates;
        this.triangles = new ArrayList<>(3);
        this.neighbourVertices = new ArrayList<>(3);
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
        return this.triangles.size();
    }

    public boolean isExtraordinary() {
        return this.getNumNeighbours() != 6;
    }
}
