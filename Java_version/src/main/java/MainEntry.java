import org.smurn.jply.PlyReaderFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author tangshao
 */
public class MainEntry {
    public void getOriginal() {
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
    }

    public static void main(String[] args) throws IOException {
        //set the current timeMills
        long startTime = System.currentTimeMillis();

        //file reader
        String modelName = "turbine";
        String fileName = "C:\\Users\\tangj\\Downloads\\" + modelName + ".ply";
        InputStream in = new FileInputStream(fileName);
        PlyReaderFile reader = new PlyReaderFile(in);
        int numFaces = reader.getElementCount("face");
        int numVertices = reader.getElementCount("vertex");
        Map<Integer, Vector3d> vertices = new HashMap<>(numFaces);
        Map<Integer, List<Integer>> faces = new HashMap<>(numVertices);

        //read the detail
        ReadPLY.read(reader, vertices, faces);
        System.out.println("--------Input model read successfully-------");
        System.out.println("Number of elements:" + numFaces);
        System.out.println("Number of vertices:" + numVertices);

        //start implementing the algorithms on the data structure
        AnalysisStep analysisStep = new AnalysisStep(vertices, faces);
        InputModel inputModel = analysisStep.createTheModel();
        analysisStep.implementScheme1(inputModel);
        InputModel inputModel1 = analysisStep.createTheModel();
        analysisStep.implementScheme1(inputModel1);

        System.out.println("-------Subdivision scheme implemented successfully-------");
        System.out.println("Number of elements:" + analysisStep.getFaceMap().size());
        System.out.println("Number of vertices:" + analysisStep.getVertexMap().size());
        analysisStep.writePLY(modelName + "_refined");
        Long endTime = System.currentTimeMillis();
        System.out.println("-------File written successfully-------");
        System.out.println("The program takes " + (endTime - startTime) / 1000d + "s");
    }
}
