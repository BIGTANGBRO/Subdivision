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

    /**
     * create vertices for the input model
     *
     * @param vertices    vertices with index and coords
     * @param faces       faces with index and vertex indices
     * @param numFaces    number of faces in total
     * @param numVertices number of vertices in total
     */
    public InputModel(Map<Integer, Vector3d> vertices, Map<Integer, List<Integer>> faces, int numVertices, int numFaces) {
        //vertex index is from 0 to numFaces;
        //face index is from 0 to numVertices
        this.triangles = new ArrayList<>(numFaces);
        this.vertices = new ArrayList<>(numVertices);

        for (int i = 0; i < numVertices; i++) {
            //set the parameter for the vertex i;
            Vector3d coord = vertices.get(i);
            List<Integer> pointIndices = new ArrayList<>();
            List<Integer> triangleIndices = new ArrayList<>();

            //iterate over the whole faces
            for (int j = 0; j < numFaces; j++) {
                List<Integer> vertexIndices = faces.get(j);
                for (int k = 0; k < 3; k++) {
                    if (vertexIndices.get(k).equals(i)) {
                        triangleIndices.add(j);
                    }
                }
            }
            //iterate over the faces include this vertex
            for (Integer triangleIndex : triangleIndices) {
                for (int iVertex = 0; iVertex < 3; iVertex++) {
                    int vertexIndex = faces.get(triangleIndex).get(iVertex);
                    if (vertexIndex != i && !pointIndices.contains(vertexIndex)) {
                        pointIndices.add(vertexIndex);
                    }
                }
            }
            Vertex v = new Vertex(i, coord, pointIndices, triangleIndices);
            this.vertices.add(v);
        }
        //end of the vertex creation

        //get the data from the polygons
        for (int iFace = 0; iFace < numFaces; iFace++) {
            //get the vertex indices of one surface
            List<Integer> vertexIndices = faces.get(iFace);
            List<Integer> faceIndices = new ArrayList<>();
            List<Integer> condition1 = new ArrayList<>(2);
            List<Integer> condition2 = new ArrayList<>(2);
            List<Integer> condition3 = new ArrayList<>(2);
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
            Triangle triangle = new Triangle(iFace, faceIndices);
            this.triangles.add(triangle);
        }

        // after this constructor, still need to update triangles' properties: vertices, edges, neighbourTriangles.
        // vertices' property: triangles, neighbour vertices.
        //complete the operation for the property

    }
}
