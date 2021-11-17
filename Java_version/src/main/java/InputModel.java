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
    private List<Polygon> polygons;
    private List<Vertex> vertices;

    /**
     * create vertices for the input model
     *
     * @param vertices    vertices with index and coords
     * @param faces       faces with index and vertex indices
     * @param numFaces    number of faces in total
     * @param numVertices number of vertices in total
     */
    public InputModel(Map<Integer, Vector3d> vertices, Map<Integer, List<Integer>> faces, int numFaces, int numVertices) {
        //vertex index is from 0 to numFaces;
        //face index is from 0 to numVertices
        this.polygons = new ArrayList<>(numFaces);
        this.vertices = new ArrayList<>(numVertices);

        for (int i = 0; i < numVertices; i++) {
            //set the parameter for the vertex i;
            Vector3d coord = vertices.get(i);
            int numNeighbours = 0;
            List<Integer> pointIndices = new ArrayList<>();
            List<Integer> polygonIndices = new ArrayList<>();

            //iterate over the whole faces
            for (int j = 0; j < numFaces; j++) {
                List<Integer> vertexIndices = faces.get(j);
                for (int k = 0; k < 3; k++) {
                    if (vertexIndices.get(k).equals(i)) {
                        numNeighbours += 1;
                        polygonIndices.add(j);
                    }
                }
            }
            //iterate over the faces include this vertex
            for (Integer polygonIndex : polygonIndices) {
                for (int iVertex = 0; iVertex < 3; iVertex++) {
                    int vertexIndex = faces.get(polygonIndex).get(iVertex);
                    if (vertexIndex != i && !pointIndices.contains(vertexIndex)) {
                        pointIndices.add(vertexIndex);
                    }
                }
            }
            Vertex v = new Vertex(i, coord, pointIndices.size(), polygonIndices.size(), pointIndices);
            this.vertices.add(v);

            //get the data from the polygons
            for (int iFace = 0; iFace < numFaces; iFace ++ ){
                List<Integer> vertexIndices = faces.get(iFace);
                int nNeighbourPolygons = 0;
                List<Integer> condition1 = new ArrayList<>(2);
                List<Integer> condition2 = new ArrayList<>(2);
                List<Integer> condition3 = new ArrayList<>(2);
                //set for different edge
                condition1.add(vertexIndices.get(1));
                condition1.add(vertexIndices.get(2));
                condition2.add(vertexIndices.get(1));
                condition2.add(vertexIndices.get(3));
                condition3.add(vertexIndices.get(2));
                condition3.add(vertexIndices.get(3));

                for (int jFace = 0; jFace < numFaces; jFace ++){
                    if (iFace == jFace){
                        continue;
                    }
                    int vCount = 0;
                    for (int iVertex = 0; iVertex < 3; iVertex++){
                        if (condition1.contains(faces.get(jFace).get(iVertex))){
                            vCount += 1;
                            if (vCount == 2){
                                nNeighbourPolygons += 1;
                                break;
                            }
                        }
                    }
                }

                for (int jFace = 0; jFace < numFaces; jFace ++){
                    if (iFace == jFace){
                        continue;
                    }
                    int vCount = 0;
                    for (int iVertex = 0; iVertex < 3; iVertex++){
                        if (condition2.contains(faces.get(jFace).get(iVertex))){
                            vCount += 1;
                            if (vCount == 2){
                                nNeighbourPolygons += 1;
                                break;
                            }
                        }
                    }
                }

                for (int jFace = 0; jFace < numFaces; jFace ++){
                    if (iFace == jFace){
                        continue;
                    }
                    int vCount = 0;
                    for (int iVertex = 0; iVertex < 3; iVertex++){
                        if (condition3.contains(faces.get(jFace).get(iVertex))){
                            vCount += 1;
                            if (vCount == 2){

                                break;
                            }
                        }
                    }
                }
            }
            //end of the polygon data construction
        }
    }
}