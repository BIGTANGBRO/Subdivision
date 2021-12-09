import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: tangshao
 * @Date: 2021/12/2
 */
@Getter
@Setter
public class ModifiedButterflyScheme {
    private List<Triangle> triangles;
    private List<Vertex> vertices;
    private List<Edge> edges;
    private static final double w = 1.0d / 16.0d;

    public ModifiedButterflyScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
    }

    //using 10-point stencil
    public Vector3d computeOdd(Vertex v1, Vertex v2) {
        //assume both are not extraordinary
        List<Integer> vertices1 = v1.getVertexIndices();
        List<Integer> vertices2 = v2.getVertexIndices();
        int id1 = v1.getIndex();
        int id2 = v2.getIndex();
        List<Integer> aList = new ArrayList<>();
        aList.add(id1);
        aList.add(id2);
        List<Integer> bList = new ArrayList<>();
        //get b
        for (Integer b1 : vertices1) {
            for (Integer b2 : vertices2) {
                if (b1.equals(b2)) {
                    bList.add(b1);
                }
            }
        }

        List<Integer> cdList = new ArrayList<>();
        for (Integer cd : vertices1) {
            if (!aList.contains(cd) && !bList.contains(cd)) {
                cdList.add(cd);
            }
        }

        Vertex vertex1 = this.vertices.get(cdList.get(0));
        Vertex vertex2 = this.vertices.get(cdList.get(1));
        Vertex vertex3 = this.vertices.get(cdList.get(2));
        int dPoint = 0;
        List<Integer> cList = new ArrayList<>(2);
        if (vertex1.getVertexIndices().contains(vertex2.getIndex()) && vertex1.getVertexIndices().contains(vertex3.getIndex())) {
            dPoint = vertex1.getIndex();
            cList.add(vertex2.getIndex());
            cList.add(vertex3.getIndex());
        } else if (vertex2.getVertexIndices().contains(vertex1.getIndex()) && vertex2.getVertexIndices().contains(vertex3.getIndex())) {
            dPoint = vertex2.getIndex();
            cList.add(vertex1.getIndex());
            cList.add(vertex3.getIndex());
        } else {
            dPoint = vertex3.getIndex();
            cList.add(vertex1.getIndex());
            cList.add(vertex2.getIndex());
        }
        return new Vector3d(0, 0, 0);
    }

    public void computeOdd() {

    }

    public void computeEven() {

    }
}
