import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author tangshao
 */
@Getter
@Setter
public class InputModel {
    //this is the input model data structure
    private List<Triangle> triangles;
    private List<Vertex> vertices;
    private List<Edge> edges;

    public InputModel() {

    }

    /**
     * create vertices for the input model
     *
     * @param vertices    vertices with index and coords
     * @param faces       faces with index and vertex indices
     * @param numFaces    number of faces in total
     * @param numVertices number of vertices in total
     */
    public InputModel(final Map<Integer, Vector3d> vertices, final Map<Integer, List<Integer>> faces, final int numVertices, final int numFaces) {
        //vertex index is from 0 to numFaces;
        //face index is from 0 to numVertices
        this.triangles = new ArrayList<>(numFaces);
        this.vertices = new ArrayList<>(numVertices);

        //Start of vertex creation
        for (int i = 0; i < numVertices; i++) {
            //set the parameter for the vertex i;
            final Vector3d coord = vertices.get(i);
            final List<Integer> pointIndices = new ArrayList<>();
            final List<Integer> triangleIndices = new ArrayList<>();
            //iterate over the whole faces
            for (int j = 0; j < numFaces; j++) {
                final List<Integer> vertexIndices = faces.get(j);
                for (int k = 0; k < 3; k++) {
                    if (vertexIndices.get(k).equals(i)) {
                        triangleIndices.add(j);
                    }
                }
            }
            //iterate over the faces include this vertex
            for (final Integer triangleIndex : triangleIndices) {
                for (int iVertex = 0; iVertex < 3; iVertex++) {
                    final int vertexIndex = faces.get(triangleIndex).get(iVertex);
                    if (vertexIndex != i && !pointIndices.contains(vertexIndex)) {
                        pointIndices.add(vertexIndex);
                    }
                }
            }
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
            condition1.add(vertexIndices.get(0));
            condition1.add(vertexIndices.get(1));
            condition2.add(vertexIndices.get(0));
            condition2.add(vertexIndices.get(2));
            condition3.add(vertexIndices.get(1));
            condition3.add(vertexIndices.get(2));

            for (int jFace = 0; jFace < numFaces; jFace++) {
                if (iFace == jFace) {
                    continue;
                }
                int vCount = 0;
                for (int iVertex = 0; iVertex < 3; iVertex++) {
                    if (condition1.contains(faces.get(jFace).get(iVertex))) {
                        vCount += 1;
                        if (vCount == 2) {
                            faceIndices.add(jFace);
                            break;
                        }
                    }
                }
            }

            for (int jFace = 0; jFace < numFaces; jFace++) {
                if (iFace == jFace) {
                    continue;
                }
                int vCount = 0;
                for (int iVertex = 0; iVertex < 3; iVertex++) {
                    if (condition2.contains(faces.get(jFace).get(iVertex))) {
                        vCount += 1;
                        if (vCount == 2) {
                            faceIndices.add(jFace);
                            break;
                        }
                    }
                }
            }

            for (int jFace = 0; jFace < numFaces; jFace++) {
                if (iFace == jFace) {
                    continue;
                }
                int vCount = 0;
                for (int iVertex = 0; iVertex < 3; iVertex++) {
                    if (condition3.contains(faces.get(jFace).get(iVertex))) {
                        vCount += 1;
                        if (vCount == 2) {
                            faceIndices.add(jFace);
                            break;
                        }
                    }
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

        //end of edge creation
    }
}
