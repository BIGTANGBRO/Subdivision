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
    public static void compare(InputModel inputModel1, InputModel inputModel2) throws IOException {
        //write the haus
        ComparisonStep.writeHausorffDistribution(inputModel2.getVertices(), inputModel1.getVertices());
        ComparisonStep.writeAngle(inputModel2);
        ComparisonStep.writeCurvature1(inputModel2);
        ComparisonStep.writeCurvature2(inputModel2);

        ComparisonStepSeparate.writeHausorffDistribution(inputModel2.getVertices(), inputModel1.getVertices());
        ComparisonStepSeparate.writeAngle(inputModel2);
        ComparisonStepSeparate.writeCurvature1(inputModel2);
        ComparisonStepSeparate.writeCurvature2(inputModel2);

        double distance = ComparisonStep.getHausorffDistance(inputModel1.getVertices(), inputModel2.getVertices());
        System.out.println("The maximum hausorff distance is" + distance);
    }

    public static void compareExistedModel() throws IOException {
        System.out.println("--------COMPARISON PROCEDURE EXECUTING-------");
        String modelName = "sphere";
        String fileName = "C:\\Users\\tangj\\Downloads\\CowData\\333\\" + modelName + ".ply";

        //Variables initializing
        InputStream in = new FileInputStream(fileName);
        PlyReaderFile reader = new PlyReaderFile(in);
        int numFaces = reader.getElementCount("face");
        int numVertices = reader.getElementCount("vertex");
        Map<Integer, Vector3d> vertices = new HashMap<>(numVertices);
        Map<Integer, List<Integer>> faces = new HashMap<>(numFaces);
        //read the detail
        ReadPLY.read(reader, vertices, faces);
        System.out.println("--------Input model read successfully-------");
        System.out.println("Info of the model");
        System.out.println("Number of elements:" + numFaces);
        System.out.println("Number of vertices:" + numVertices);

        AnalysisStep analysisStep = new AnalysisStep(vertices, faces);
        InputModel inputModel = analysisStep.createTheModel();

        //No operation on this model, evaluate the features itself.
        System.out.println("--------Writing out the features-------");
        ComparisonStep.writeAngle(inputModel);
        ComparisonStep.writeCurvature1(inputModel);
        ComparisonStep.writeCurvature2(inputModel);
        System.out.println("-------The whole process finished-------");
    }

    public static void workFlow() throws IOException {
        System.out.println("--------NORMAL PROCEDURE EXECUTING-------");
        //set the current timeMills
        long startTime = System.currentTimeMillis();

        //file location
        String modelName = "sphere";
        String fileName = "C:\\Users\\tangj\\Downloads\\" + modelName + ".ply";

        //Variables initializing
        InputStream in = new FileInputStream(fileName);
        PlyReaderFile reader = new PlyReaderFile(in);
        int numFaces = reader.getElementCount("face");
        int numVertices = reader.getElementCount("vertex");
        Map<Integer, Vector3d> vertices = new HashMap<>(numVertices);
        Map<Integer, List<Integer>> faces = new HashMap<>(numFaces);
        //read the detail
        ReadPLY.read(reader, vertices, faces);
        System.out.println("--------Input model read successfully-------");
        System.out.println("Info of the old model");
        System.out.println("Number of elements:" + numFaces);
        System.out.println("Number of vertices:" + numVertices);

        //start implementing the algorithms on the data structure
        AnalysisStep analysisStep = new AnalysisStep(vertices, faces);
        InputModel inputModel = analysisStep.createTheModel();
        analysisStep.implementScheme3(inputModel);
        analysisStep.implementScheme3(analysisStep.createTheModel());
        //analysisStep.implementScheme2(analysisStep.createTheModel());

        System.out.println("-------Subdivision scheme implemented successfully-------");
        System.out.println("--------Calculate the normal for the vertex-------");
        Map<Integer, Vector3d> normalMap = ComparisonStep.getNormalForVertices(analysisStep.createTheModel());
        OutputModel outputModel = new OutputModel(analysisStep.getVertexMap(), analysisStep.getFaceMap(), normalMap);
        System.out.println("Info of the new model:");
        System.out.println("Number of elements:" + outputModel.getFaceMap().size());
        System.out.println("Number of vertices:" + outputModel.getVertexMap().size());

        //write the file
        outputModel.writePLYNormal(modelName + "_refined");
        //outputModel.writePLY(modelName + "_refined");
        long endTime = System.currentTimeMillis();

        //print out the running time
        System.out.println("-------File written successfully-------");
        System.out.println("The program takes " + (endTime - startTime) / 1000d + "s");

        //comparison
        System.out.println("-------Start doing the comparison-------");
        compare(inputModel, analysisStep.createTheModel());
        System.out.println("-------The whole process finished-------");
    }

    public static void main(String[] args) throws IOException {
        workFlow();
        //compareExistedModel();
    }
}
