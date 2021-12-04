import org.smurn.jply.PlyReaderFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author tangshao
 */
public class MainEntry {
    public static void main(String[] args) throws IOException {
        //file reader
        String modelName = "happy_vrip_res4";
        String fileName = "C:\\Users\\tangj\\Downloads\\happy_recon\\happy_recon\\" + modelName + ".ply";
        InputStream in = new FileInputStream(fileName);
        PlyReaderFile reader = new PlyReaderFile(in);
        int numFaces = reader.getElementCount("face");
        int numVertices = reader.getElementCount("vertex");
        Map<Integer, Vector3d> vertices = new HashMap<>(numFaces);
        Map<Integer, List<Integer>> faces = new HashMap<>(numVertices);

        //read the detail
        ReadPLY.read(reader, vertices, faces);
        System.out.println("------Input model read successfully-----");

        //start implementing the algorithms on the data structure
        AnalysisStep analysisStep = new AnalysisStep(vertices, faces);
        InputModel inputModel = analysisStep.createTheModel();
        analysisStep.implementScheme1(inputModel);
        InputModel inputModel1 = analysisStep.createTheModel();
        analysisStep.implementScheme1(inputModel1);

//        List<Vertex> verticesList = inputModel.getVertices();
//        List<Triangle> trianglesList = inputModel.getTriangles();
//        Map<Integer, Vector3d> newVertices = new HashMap<>();
//        Map<Integer, List<Integer>> newFaces = new HashMap<>();
//        for (Vertex vertex:verticesList){
//            newVertices.put(vertex.getIndex(), vertex.getCoords());
//        }
//
//        for (Triangle triangle : trianglesList){
//            List<Vertex> vertexEachTri = triangle.getVertices();
//            List<Integer> vertexIndices = new ArrayList<>();
//            for (int i = 0;i < 3;i++){
//                vertexIndices.add(vertexEachTri.get(i).getIndex());
//            }
//            newFaces.put(triangle.getIndex(), vertexIndices);
//        }

        System.out.println("-------Subdivision scheme implemented successfully-------");
        analysisStep.writePLY(modelName + "_res2");
        System.out.println("-------File written successfully-------");
    }
}
