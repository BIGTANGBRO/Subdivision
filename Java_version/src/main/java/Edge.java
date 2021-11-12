import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangshao
 */
@Getter
@Setter
public class Edge {
    private int index;
    private List<Polygon> polygons;
    private Vertex a;
    private Vertex b;

    Edge(final Vertex a, final Vertex b, final int index) {
        this.a = a;
        this.b = b;
        this.index = index;
        this.polygons = new ArrayList<>(2);
    }

    public boolean has(final Vertex v) {
        return v == a || v == b;
    }

    public Vertex getOtherVertex(final Vertex v) {
        if (a != b) {
            return a;
        } else {
            return b;
        }
    }

    public void updatePolygon(final int index, final Polygon polygon) {
        this.polygons.set(index, polygon);
    }
}
