import java.util.ArrayList;
import java.util.List;

/**
 * @author tangshao
 */
public class LoopScheme {
    public List<Vector3d> computeOdd(final Polygon polygon) {
        //create the odd vertices
        final List<Edge> edges = polygon.getEdges();
        final List<Vector3d> newVertices = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            final Edge edge = edges.get(i);
            //get the edge vertex
            final Vertex v1 = edge.getA();
            final Vertex v2 = edge.getB();
            final Polygon p1 = edge.getPolygons().get(0);
            final Polygon p2 = edge.getPolygons().get(1);
            final List<Vertex> vertices1 = p1.getVertices();
            final List<Vertex> vertices2 = p2.getVertices();
            //get the neighbour vertex
            Vertex v3 = new Vertex();
            Vertex v4 = new Vertex();
            for (int i1 = 0; i1 < 3; i1++) {
                if (!vertices1.get(i1).equals(v1) && !vertices1.get(i1).equals(v2)) {
                    v3 = vertices1.get(i1);
                }
            }
            for (int i2 = 0; i2 < 3; i2++) {
                if (!vertices2.get(i2).equals(v1) && !vertices2.get(i2).equals(v2)) {
                    v4 = vertices2.get(i2);
                }
            }
            //compute the new coordinates for each edge of the triangle.
            final Vector3d coord1 = v1.getCoords();
            final Vector3d coord2 = v2.getCoords();
            final Vector3d coord3 = v3.getCoords();
            final Vector3d coord4 = v4.getCoords();
            final Vector3d newV1 = MathUtils.dotVal(Constant.THREEOVEREIGHT, MathUtils.addVector(coord1, coord2));
            final Vector3d newV2 = MathUtils.dotVal(Constant.THREEOVEREIGHT, MathUtils.addVector(coord3, coord4));
            newVertices.add(MathUtils.addVector(newV1, newV2));
        }
        return newVertices;
    }

    public Vector3d computeEven(final Vertex vertex) {
        //create the even vertices
        final int n = vertex.getNumNeighbours();
        double alpha = Constant.THREEOVERSIXTEEN;
        if (n > 3) {
            alpha = 1 / n * (5 / 8 - Math.pow((Constant.THREEOVEREIGHT + Constant.ONEOVERFOUR * Math.cos(2 * Constant.PI / n)), 2));
        }
        final List<Vertex> neighbourVertices = vertex.getVertices();
        final Vector3d coordV = vertex.getCoords();
        Vector3d v2 = new Vector3d(0d, 0d, 0d);
        for (int i = 0; i < n; i++) {
            final Vector3d coordNeighbour = neighbourVertices.get(i).getCoords();
            //get the sum of the neighbour points
            final Vector3d vProduct = MathUtils.dotVal(alpha, coordNeighbour);
            v2 = MathUtils.addVector(v2, vProduct);
        }
        final double coeff2 = 1 - n * alpha;
        final Vector3d newVertex = MathUtils.addVector(MathUtils.dotVal(coeff2, coordV), v2);
        return newVertex;
    }

    public void connectVertices() {
        //connect the new vertices to form the new mash
    }
}
