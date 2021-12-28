import lombok.Getter;
import lombok.Setter;

import java.util.*;

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
    private Map<Integer, Integer> oddNodeMap;
    private double w = 1.0d / 16.0d;

    public ModifiedButterflyScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
        this.oddNodeMap = new HashMap<>();
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
        //index of the vertex
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

        List<Integer> cList = new ArrayList<>(2);
        List<Integer> dList = new ArrayList<>(1);
        //for the first point
        if (vertex11.getVertexIndices().contains(vertex12.getIndex()) && vertex11.getVertexIndices().contains(vertex13.getIndex())) {
            cList.add(vertex12.getIndex());
            cList.add(vertex13.getIndex());
            dList.add(vertex11.getIndex());
        } else if (vertex12.getVertexIndices().contains(vertex11.getIndex()) && vertex12.getVertexIndices().contains(vertex13.getIndex())) {
            cList.add(vertex11.getIndex());
            cList.add(vertex13.getIndex());
            dList.add(vertex12.getIndex());
        } else {
            cList.add(vertex11.getIndex());
            cList.add(vertex12.getIndex());
            dList.add(vertex13.getIndex());
        }

        //for the second point
        if (vertex21.getVertexIndices().contains(vertex22.getIndex()) && vertex21.getVertexIndices().contains(vertex23.getIndex())) {
            cList.add(vertex22.getIndex());
            cList.add(vertex23.getIndex());
            dList.add(vertex21.getIndex());
        } else if (vertex22.getVertexIndices().contains(vertex21.getIndex()) && vertex22.getVertexIndices().contains(vertex23.getIndex())) {
            cList.add(vertex21.getIndex());
            cList.add(vertex23.getIndex());
            dList.add(vertex22.getIndex());
        } else {
            cList.add(vertex21.getIndex());
            cList.add(vertex22.getIndex());
            dList.add(vertex23.getIndex());
        }
        stencils.put("c", cList);
        stencils.put("d", dList);
        return stencils;
    }

    /**
     * Get the points near a vertex in one direction
     *
     * @param vMain Center point
     * @param v2    point on the end of edge 0
     * @return List of Vertex indices
     */
    private List<Integer> getNeighBourPts(Vertex vMain, Vertex v2) {
        //vMain is the extradinory and v2 is another point on e0;
        List<Integer> indices = new ArrayList<>();
        //start from the first point
        indices.add(v2.getIndex());
        Vertex vNew = new Vertex();
        for (Triangle triangle : this.triangles) {
            if (triangle.containVertices(vMain, v2)) {
                Vertex v3 = triangle.getRemain(vMain, v2);
                indices.add(v3.getIndex());
                vNew = v3;
                break;
            }
        }

        Vertex vOld = v2;
        while (vNew != v2) {
            for (Triangle triangle : this.triangles) {
                if (triangle.containVertices(vMain, vNew) && !triangle.containVertex(vOld)) {
                    vOld = vNew;
                    vNew = triangle.getRemain(vMain, vNew);
                    if (vNew != v2) {
                        indices.add(vNew.getIndex());
                    }
                    break;
                }
            }
        }
        return indices;
    }

    /**
     * Get the coefficients for n >= 5
     *
     * @param j index
     * @param n number of points
     * @return Coefficients
     */
    private double getCoeff(int j, int n) {
        return (0.25 + Math.cos(2.0d * Math.PI * (double) j / (double) n) + 0.5 * Math.cos(4.0d * Math.PI * (double) j / (double) n)) / (double) n;
    }

    /**
     * Compute the coord of odd point
     *
     * @param v1 first point
     * @param v2 second point
     * @return point coordinates
     */
    public Vector3d computeOdd(Vertex v1, Vertex v2) {
        //set for different scenriosx
        if (v1.getNumVertices() == 6 && v2.getNumVertices() == 6) {
            //both are regular
            double a = 0.5d - w;
            double b = 1.0d / 8.0d + 2 * w;
            double c = -1.0d / 16.0d - w;
            double d = w;
            Map<String, List<Integer>> stencils = getStencil(v1, v2);
            List<Integer> aList = stencils.get("a");
            List<Integer> bList = stencils.get("b");
            List<Integer> cList = stencils.get("c");
            List<Integer> dList = stencils.get("d");

            Vector3d aSum = new Vector3d(0, 0, 0);
            Vector3d bSum = new Vector3d(0, 0, 0);
            Vector3d cSum = new Vector3d(0, 0, 0);
            Vector3d dSum = new Vector3d(0, 0, 0);
            for (Integer aIndex : aList) {
                Vertex v = this.vertices.get(aIndex);
                aSum = MathUtils.addVector(aSum, MathUtils.dotVal(a, v.getCoords()));
            }
            for (Integer bIndex : bList) {
                Vertex v = this.vertices.get(bIndex);
                bSum = MathUtils.addVector(bSum, MathUtils.dotVal(b, v.getCoords()));
            }
            for (Integer cIndex : cList) {
                Vertex v = this.vertices.get(cIndex);
                cSum = MathUtils.addVector(cSum, MathUtils.dotVal(c, v.getCoords()));
            }
            for (Integer dIndex : dList) {
                Vertex v = this.vertices.get(dIndex);
                dSum = MathUtils.addVector(dSum, MathUtils.dotVal(d, v.getCoords()));
            }
            return MathUtils.addVector(aSum, bSum, cSum, dSum);

        } else if (v1.getNumVertices() != 6 && v2.getNumVertices() == 6) {
            //first case
            List<Integer> verticesNear = getNeighBourPts(v1, v2);
            List<Double> mask = new ArrayList<>();
            if (v1.getNumVertices() == 3) {
                mask.add(5.0d / 12.0d);
                mask.add(-1.0d / 12.0d);
                mask.add(-1.0d / 12.0d);
            } else if (v1.getNumVertices() == 4) {
                mask.add(3.0d / 8.0d);
                mask.add(0d);
                mask.add(-1.0d / 8.0d);
                mask.add(0d);
            } else {
                for (int i = 0; i < verticesNear.size(); i++) {
                    mask.add(getCoeff(i, verticesNear.size()));
                }
            }
            Vector3d newVertex = new Vector3d(0, 0, 0);
            for (int i = 0; i < mask.size(); i++) {
                Vector3d vertexMulti = MathUtils.dotVal(mask.get(i), this.vertices.get(verticesNear.get(i)).getCoords());
                newVertex = MathUtils.addVector(newVertex, vertexMulti);
            }
            Vector3d newPointMain = MathUtils.dotVal(3.0d / 4.0d, v1.getCoords());
            Vector3d oddVertex = MathUtils.addVector(newPointMain, newVertex);
            return oddVertex;
        } else if (v2.getNumVertices() != 6 && v1.getNumVertices() == 6) {
            //second case
            List<Integer> verticesNear = getNeighBourPts(v2, v1);
            List<Double> mask = new ArrayList<>();
            if (v2.getNumVertices() == 3) {
                mask.add(5.0d / 12.0d);
                mask.add(-1.0d / 12.0d);
                mask.add(-1.0d / 12.0d);
            } else if (v2.getNumVertices() == 4) {
                mask.add(3.0d / 8.0d);
                mask.add(0d);
                mask.add(-1.0d / 8.0d);
                mask.add(0d);
            } else {
                for (int i = 0; i < verticesNear.size(); i++) {
                    mask.add(getCoeff(i, verticesNear.size()));
                }
            }
            Vector3d newVertex = new Vector3d(0, 0, 0);
            for (int i = 0; i < mask.size(); i++) {
                Vector3d vertexMulti = MathUtils.dotVal(mask.get(i), this.vertices.get(verticesNear.get(i)).getCoords());
                newVertex = MathUtils.addVector(newVertex, vertexMulti);
            }
            Vector3d newPointMain = MathUtils.dotVal(3.0d / 4.0d, v2.getCoords());
            return MathUtils.addVector(newPointMain, newVertex);
        } else {
            //both are irregular
            List<Integer> verticesNear1 = getNeighBourPts(v2, v1);
            List<Integer> verticesNear2 = getNeighBourPts(v1, v2);
            List<Double> mask1 = new ArrayList<>();
            List<Double> mask2 = new ArrayList<>();

            if (v1.getNumVertices() == 3) {
                mask1.add(5.0d / 12.0d);
                mask1.add(-1.0d / 12.0d);
                mask1.add(-1.0d / 12.0d);
            } else if (v1.getNumVertices() == 4) {
                mask1.add(3.0d / 8.0d);
                mask1.add(0d);
                mask1.add(-1.0d / 8.0d);
                mask1.add(0d);
            } else {
                for (int i = 0; i < verticesNear1.size(); i++) {
                    mask1.add(getCoeff(i, verticesNear1.size()));
                }
            }

            if (v2.getNumVertices() == 3) {
                mask2.add(5.0d / 12.0d);
                mask2.add(-1.0d / 12.0d);
                mask2.add(-1.0d / 12.0d);
            } else if (v2.getNumVertices() == 4) {
                mask2.add(3.0d / 8.0d);
                mask2.add(0d);
                mask2.add(-1.0d / 8.0d);
                mask2.add(0d);
            } else {
                for (int i = 0; i < verticesNear2.size(); i++) {
                    mask2.add(getCoeff(i, verticesNear2.size()));
                }
            }
            Vector3d newVertex1 = new Vector3d(0, 0, 0);
            Vector3d newVertex2 = new Vector3d(0, 0, 0);

            for (int i = 0; i < mask1.size(); i++) {
                Vector3d vertexMulti1 = MathUtils.dotVal(mask1.get(i), this.vertices.get(verticesNear1.get(i)).getCoords());
                newVertex1 = MathUtils.addVector(newVertex1, vertexMulti1);
            }
            for (int i = 0; i < mask2.size(); i++) {
                Vector3d vertexMulti2 = MathUtils.dotVal(mask2.get(i), this.vertices.get(verticesNear2.get(i)).getCoords());
                newVertex2 = MathUtils.addVector(newVertex2, vertexMulti2);
            }

            Vector3d newPointMain1 = MathUtils.dotVal(3.0d / 4.0d, v1.getCoords());
            Vector3d newPointMain2 = MathUtils.dotVal(3.0d / 4.0d, v2.getCoords());

            Vector3d oddVertex1 = MathUtils.addVector(newPointMain1, newVertex1);
            Vector3d oddVertex2 = MathUtils.addVector(newPointMain2, newVertex2);
            return MathUtils.dotVal(0.5d, MathUtils.addVector(oddVertex1, oddVertex2));
        }
    }

    /**
     * Iterate to compute the edge point
     *
     * @return Map with vertex index and coordinate
     */
    public Map<Integer, Vector3d> computeOdd() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = this.vertices.size();
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

    public Map<Integer, List<Integer>> createTriangle() {
        int faceCount = 0;
        Map<Integer, List<Integer>> faceMap = new HashMap<>();
        for (final Triangle triangle : this.triangles) {
            final HashSet<Integer> oddVertexSet = new HashSet<>();
            for (final Vertex vertex : triangle.getVertices()) {
                final List<Edge> connectedEdges = triangle.getConnectedEdges(vertex);
                final List<Integer> vertexIndices = new ArrayList<>(3);
                vertexIndices.add(vertex.getIndex());
                for (final Edge edge : connectedEdges) {
                    final int newVertexIndex = oddNodeMap.get(edge.getIndex());
                    oddVertexSet.add(newVertexIndex);
                    vertexIndices.add(newVertexIndex);
                }
                faceMap.put(faceCount, vertexIndices);
                faceCount += 1;
            }
            //connect the new created odd vertices to form a surface
            final List<Integer> oddVertexArr = new ArrayList<>(oddVertexSet);
            faceMap.put(faceCount, oddVertexArr);
            faceCount += 1;
        }
        return faceMap;
    }
}
