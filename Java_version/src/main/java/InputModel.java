import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author tangshao
 */
@Getter
@Setter
public class InputModel {
    //this is the input model data structure
    //triangles and vertices are in the same order
    private List<Triangle> triangles;
    private List<Vertex> vertices;
    private List<Edge> edges;

    public InputModel() {

    }

    /**
     * construcotr
     *
     * @param triangles trianle list
     * @param vertices  vertex list
     * @param edges     edge list
     */
    public InputModel(final List<Triangle> triangles, final List<Vertex> vertices, final List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.edges = edges;
    }

    /**
     * create vertices for the input model
     *
     * @param vertices vertices with index and coords
     * @param faces    faces with index and vertex indices
     */
    public InputModel(final Map<Integer, Vector3d> vertices, final Map<Integer, List<Integer>> faces) {
        //vertex index is from 0 to numFaces;
        //face index is from 0 to numVertices
        final int numVertices = vertices.size();
        final int numFaces = faces.size();
        this.triangles = new ArrayList<>(numFaces);
        this.vertices = new ArrayList<>(numVertices);

        //Start of vertex creation
        for (int i = 0; i < numVertices; i++) {
            //set the parameter for the vertex i;
            final Vector3d coord = vertices.get(i);
            final List<Integer> triangleIndices = new ArrayList<>();
            final HashSet<Integer> pointSet = new HashSet<>();

            //iterate over the whole faces
            for (int j = 0; j < numFaces; j++) {
                final List<Integer> vertexIndices = faces.get(j);
                if (vertexIndices.contains(i)) {
                    triangleIndices.add(j);
                    for (final Integer vertexIndex : vertexIndices) {
                        if (vertexIndex != i) {
                            pointSet.add(vertexIndex);
                        }
                    }
                }
            }

            final List<Integer> pointIndices = new ArrayList<>(pointSet);
            final Vertex v = new Vertex(i, coord, pointIndices, triangleIndices);
            this.vertices.add(v);
        }
        //end of the vertex creation

        //Start of polygon creation
        for (int iFace = 0; iFace < numFaces; iFace++) {
            //get the vertex indices of one surface
            final List<Integer> vertexIndices = faces.get(iFace);
            final List<Integer> faceIndices = new ArrayList<>();
            final List<Integer> condition1 = new ArrayList<>(2);
            final List<Integer> condition2 = new ArrayList<>(2);
            final List<Integer> condition3 = new ArrayList<>(2);
            //set for different edge
            if (vertexIndices.size() == 2) {
                System.out.println("Exception:");
                System.out.println(iFace);
            }
            condition1.add(vertexIndices.get(0));
            condition1.add(vertexIndices.get(1));
            condition2.add(vertexIndices.get(0));
            condition2.add(vertexIndices.get(2));
            condition3.add(vertexIndices.get(1));
            condition3.add(vertexIndices.get(2));

            int faceCount = 0;
            for (int jFace = 0; jFace < numFaces; jFace++) {
                if (iFace == jFace) {
                    continue;
                }
                if (faceCount == 3) {
                    break;
                }
                final List<Integer> verticesEach = faces.get(jFace);
                if (verticesEach.contains(condition1.get(0)) && verticesEach.contains(condition1.get(1))) {
                    faceIndices.add(jFace);
                    faceCount += 1;
                }
                if (verticesEach.contains(condition2.get(0)) && verticesEach.contains(condition2.get(1))) {
                    faceIndices.add(jFace);
                    faceCount += 1;
                }
                if (verticesEach.contains(condition3.get(0)) && verticesEach.contains(condition3.get(1))) {
                    faceIndices.add(jFace);
                    faceCount += 1;
                }
            }

            final List<Vertex> verticesEachTri = new ArrayList<>(3);
            for (int iVertex = 0; iVertex < 3; iVertex++) {
                final Integer vertexIndex = vertexIndices.get(iVertex);
                final Vertex v = this.vertices.get(vertexIndex);
                verticesEachTri.add(v);
            }
            final Triangle triangle = new Triangle(iFace, verticesEachTri, faceIndices);
            this.triangles.add(triangle);
        }
        //end of polygon creation

        //start of edge creation
        final HashSet<Edge> edgeSet = new HashSet<>();
        final Map<Integer, Edge> edgeMap = new HashMap<>();

        int edgeCount = 0;
        for (final Triangle triangle : this.triangles) {
            final List<Vertex> vs = triangle.getVertices();
            final Vertex v1 = vs.get(0);
            final Vertex v2 = vs.get(1);
            final Vertex v3 = vs.get(2);
            final Edge edge1 = new Edge(v1, v2, edgeCount);
            if (!edgeSet.contains(edge1)) {
                edgeMap.put(v1.hashCode() + v2.hashCode(), edge1);
                edgeSet.add(edge1);
                triangle.addEdge(edge1);
                edgeCount += 1;
            } else {
                triangle.addEdge(edgeMap.get(v1.hashCode() + v2.hashCode()));
            }

            final Edge edge2 = new Edge(v1, v3, edgeCount);
            if (!edgeSet.contains(edge2)) {
                edgeMap.put(v1.hashCode() + v3.hashCode(), edge2);
                edgeSet.add(edge2);
                triangle.addEdge(edge2);
                edgeCount += 1;
            } else {
                triangle.addEdge(edgeMap.get(v1.hashCode() + v3.hashCode()));
            }

            final Edge edge3 = new Edge(v2, v3, edgeCount);
            if (!edgeSet.contains(edge3)) {
                edgeMap.put(v3.hashCode() + v2.hashCode(), edge3);
                edgeSet.add(edge3);
                triangle.addEdge(edge3);
                edgeCount += 1;
            } else {
                triangle.addEdge(edgeMap.get(v2.hashCode() + v3.hashCode()));
            }
        }
        this.edges = new ArrayList<>(edgeSet);
        //end of edge creation
    }
}
