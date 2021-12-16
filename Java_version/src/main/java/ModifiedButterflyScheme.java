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
public class ModifiedButterflyScheme {
    private List<Triangle> triangles;
    private List<Vertex> vertices;
    private List<Edge> edges;
    private double w = 1.0d / 16.0d;

    public ModifiedButterflyScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
    }

    public ModifiedButterflyScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges, double w) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
        this.w = w;
    }

    /**
     * return the stencil for 2 vertices
     *
     * @param v1 Vertex1
     * @param v2 Vertex2
     * @return Map with id and vertexIndex
     */
    public Map<String, List<Integer>> getStencil(Vertex v1, Vertex v2) {
        Map<String, List<Integer>> stencils = new HashMap<>();
        //get the neighbour of the two vertices
        List<Integer> vertices1 = v1.getVertexIndices();
        List<Integer> vertices2 = v2.getVertexIndices();
        int id1 = v1.getIndex();
        int id2 = v2.getIndex();
        List<Integer> aList = new ArrayList<>();
        aList.add(id1);
        aList.add(id2);
        stencils.put("a", aList);
        List<Integer> bList = new ArrayList<>();
        //get b
        for (Integer b1 : vertices1) {
            for (Integer b2 : vertices2) {
                if (b1.equals(b2)) {
                    bList.add(b1);
                }
            }
        }
        stencils.put("b", bList);

        List<Integer> cdList = new ArrayList<>(6);
        for (Integer cd : vertices1) {
            if (!aList.contains(cd) && !bList.contains(cd)) {
                cdList.add(cd);
            }
        }
        for (Integer cd : vertices2) {
            if (!aList.contains(cd) && !bList.contains(cd)) {
                cdList.add(cd);
            }
        }
        Vertex vertex11 = this.vertices.get(cdList.get(0));
        Vertex vertex12 = this.vertices.get(cdList.get(1));
        Vertex vertex13 = this.vertices.get(cdList.get(2));

        Vertex vertex21 = this.vertices.get(cdList.get(3));
        Vertex vertex22 = this.vertices.get(cdList.get(4));
        Vertex vertex23 = this.vertices.get(cdList.get(5));

        int dPoint = 0;
        List<Integer> cList = new ArrayList<>(2);
        List<Integer> dList = new ArrayList<>(1);
        //for the first point
        if (vertex11.getVertexIndices().contains(vertex12.getIndex()) && vertex11.getVertexIndices().contains(vertex13.getIndex())) {
            dPoint = vertex11.getIndex();
            cList.add(vertex12.getIndex());
            cList.add(vertex13.getIndex());
            dList.add(dPoint);
        } else if (vertex12.getVertexIndices().contains(vertex11.getIndex()) && vertex12.getVertexIndices().contains(vertex13.getIndex())) {
            dPoint = vertex12.getIndex();
            cList.add(vertex11.getIndex());
            cList.add(vertex13.getIndex());
            dList.add(dPoint);
        } else {
            dPoint = vertex13.getIndex();
            cList.add(vertex11.getIndex());
            cList.add(vertex12.getIndex());
            dList.add(dPoint);
        }

        //for the second point
        if (vertex11.getVertexIndices().contains(vertex22.getIndex()) && vertex21.getVertexIndices().contains(vertex23.getIndex())) {
            dPoint = vertex21.getIndex();
            cList.add(vertex22.getIndex());
            cList.add(vertex23.getIndex());
            dList.add(dPoint);
        } else if (vertex22.getVertexIndices().contains(vertex21.getIndex()) && vertex22.getVertexIndices().contains(vertex23.getIndex())) {
            dPoint = vertex22.getIndex();
            cList.add(vertex21.getIndex());
            cList.add(vertex23.getIndex());
            dList.add(dPoint);
        } else {
            dPoint = vertex23.getIndex();
            cList.add(vertex21.getIndex());
            cList.add(vertex22.getIndex());
            dList.add(dPoint);
        }
        stencils.put("c", cList);
        stencils.put("d", dList);
        return stencils;
    }

    public void computeOdd(Vertex v1, Vertex v2) {
        //one vertex is valence 6, the other is extraordinary.
    }

    public void computeEven() {

    }
}
