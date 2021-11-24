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
    private Vertex a;
    private Vertex b;
    private List<Integer> triangleIndices;

    Edge(final Vertex a, final Vertex b, final int index) {
        this.a = a;
        this.b = b;
        this.index = index;
        this.triangleIndices = new ArrayList<>();
    }

    public boolean has(final Vertex v) {
        return v == a || v == b;
    }

    public Vertex getOtherVertex(final Vertex v) {
        if (a != v) {
            return a;
        } else {
            return b;
        }
    }

    public void addTrianleIndex(int trianleIndex){
        this.triangleIndices.add(trianleIndex);
    }

    @Override
    public int hashCode() {
        return this.a.hashCode() + this.b.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Edge) {
            Edge edge = (Edge) obj;
            if ((this.a == edge.a && this.b == edge.b) || (this.b == edge.a && this.a == edge.b)) {
                return true;
            }
        }
        return false;
    }
}
