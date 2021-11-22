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

    Edge(final Vertex a, final Vertex b, final int index) {
        this.a = a;
        this.b = b;
        this.index = index;
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
}
