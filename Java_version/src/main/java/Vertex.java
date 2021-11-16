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
    private List<Polygon> polygons;
    //neighbour vertices
    private List<Vertex> vertices;
    private List<Integer> vertexIndices;

    //In triangular mesh, a vertex which is not connected 6 neightbours is extradinary
    Vertex() {

    }

    //Constructor for the vertex
    Vertex(final int index, final double[] coordinates, final int nNeighbours) {
        this.index = index;
        this.coords.setXVal(coordinates[0]);
        this.coords.setYVal(coordinates[1]);
        this.coords.setZVal(coordinates[2]);
        this.polygons = new ArrayList<>(nNeighbours);
        this.vertices = new ArrayList<>(nNeighbours);
        this.vertexIndices = new ArrayList<>(nNeighbours);
    }

    Vertex(final int index, final Vector3d coordinates, final int nNeighbours, final List<Integer> vertexIndices) {
        this.index = index;
        this.coords = coordinates;
        this.polygons = new ArrayList<>(nNeighbours);
        this.vertices = new ArrayList<>(nNeighbours);
        this.vertexIndices = vertexIndices;
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
        return this.polygons.size();
    }

    public boolean isExtraordinary() {
        return this.getNumNeighbours() != 6;
    }
}
