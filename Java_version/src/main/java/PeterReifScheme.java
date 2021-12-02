import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: tangshao
 * @Date: 2021/12/2
 */
@Getter
@Setter
public class PeterReifScheme {
    private List<Triangle> triangles;
    private List<Edge> edges;
    private List<Vertex> vertices;
    private Map<Integer, Integer> oddNodeMap;

    public PeterReifScheme(List<Triangle> triangles, List<Edge> edges, List<Vertex> vertices) {
        this.triangles = triangles;
        this.edges = edges;
        this.vertices = vertices;
        this.oddNodeMap = new HashMap<>();
    }

    public Vector3d computeOdd(Vertex v1, Vertex v2) {
        List<Vertex> vertices = new ArrayList<>(2);
        Vector3d coord = MathUtils.dotVal(0.5, MathUtils.addVector(v1.getCoords(), v2.getCoords()));
        return coord;
    }

    public Map<Integer, Vector3d> computeOdd() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = vertices.size();
        for (Edge edge : edges) {
            Vertex v1 = edge.getA();
            Vertex v2 = edge.getB();
            Vector3d coord = computeOdd(v1, v2);
            vertexMap.put(index, coord);
            oddNodeMap.put(edge.getIndex(), index);
            index += 1;
        }
        return vertexMap;
    }
}
