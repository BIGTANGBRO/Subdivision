import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: tangshao
 * @Date: 29/01/2022
 */
@Getter
@Setter
public class Square3Scheme {
    private List<Triangle> triangles;
    private List<Vertex> vertices;
    private List<Edge> edges;

    public Square3Scheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
    }

    public Vector3d insertPointRegular1(Triangle triangleEach) {
        Vector3d coord = new Vector3d(0, 0, 0);
        for (Vertex v : triangleEach.vertices) {
            coord = MathUtils.addVector(coord, v.getCoords());
        }
        return MathUtils.dotVal(1d / 3d, coord);
    }

    public void insertPointIrregular(Triangle triangleEach) {
        for (Vertex v : triangleEach.getVertices()) {
            if (!v.isRegular()) {

            }
        }
    }

    public void getPtsInOrder(Triangle triangleEach, Vertex v) {
        List<Edge> edgesConnect = triangleEach.getConnectedEdges(v);
        List<Vertex> verticesNear = new ArrayList<>();
        for (Edge edge : edgesConnect) {
            if (edge.getA().equals(v)) {
                verticesNear.add(edge.getB());
            } else {
                verticesNear.add(edge.getA());
            }
        }

        //todo: same as the one in modified butterfly

    }

    public Map<Integer, Vector3d> insertPointRegular1() {
        int index = vertices.size();
        Map<Integer, Vector3d> vertexMap = new HashMap<Integer, Vector3d>();
        for (Triangle triangleEach : this.triangles) {
            if (triangleEach.isNearExtraordinary()) {

            } else {
                vertexMap.put(index, this.insertPointRegular1(triangleEach));
                index += 1;
            }
        }
        return vertexMap;
    }
}
