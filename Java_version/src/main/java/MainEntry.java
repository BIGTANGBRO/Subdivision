import org.smurn.jply.PlyReaderFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author tangshao
 */
public class MainEntry {
    public static void compare(InputModel inputModel1, InputModel inputModel2) {
        ComparisonStep compareStep = new ComparisonStep();
        double error = compareStep.getHausorffDistance(inputModel1.getVertices(), inputModel2.getVertices());
        System.out.println("The HausorffDistance Error is :" + error);
    }

    public static void main(String[] args) throws IOException {
        //set the current timeMills
        long startTime = System.currentTimeMillis();

        //file location
        String modelName = "sphere";
        String fileName = "C:\\Users\\tangj\\Downloads\\" + modelName + ".ply";

        //file reader
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
        OutputModel outputModel = new OutputModel(analysisStep.getVertexMap(), analysisStep.getFaceMap());

        System.out.println("-------Subdivision scheme implemented successfully-------");
        System.out.println("Number of elements:" + outputModel.getFaceMap().size());
        System.out.println("Number of vertices:" + outputModel.getVertexMap().size());

        //write the file
        outputModel.writePLY(modelName + "_refined");
        long endTime = System.currentTimeMillis();

        //print out the running time
        System.out.println("-------File written successfully-------");
        System.out.println("The program takes " + (endTime - startTime) / 1000d + "s");

        //comparison
        compare(inputModel, analysisStep.createTheModel());
    }
}
