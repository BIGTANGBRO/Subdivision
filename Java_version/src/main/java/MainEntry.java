import org.smurn.jply.PlyReaderFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author tangshao
 */
public class MainEntry {
    public static void accessQuality(InputModel inputModel) throws IOException {
        ComparisonStep.writeAngle(inputModel);
        ComparisonStep.writeCurvatureGaussian(inputModel);
        ComparisonStep.writeCurvatureMean(inputModel);
        System.out.println("--------Properties are written successfully-------");
    }

    public static void accessQualityOnExtra(InputModel inputModel) throws IOException {
        ComparisonStepSeparate.writeAngle(inputModel);
        ComparisonStepSeparate.writeCurvature1(inputModel);
        ComparisonStepSeparate.writeCurvature2(inputModel);
        System.out.println("--------Extraordinary properties are written successfully-------");
    }

    public static void compareSphere(InputModel inputModel) throws IOException {
        ComparisonStep.writeSphereDiff(inputModel);
    }

    public static void vertexCompareHausorff(InputModel inputModel1, InputModel inputModel2) {
        double distance = ComparisonStep.getHausorffDistance(inputModel1.getVertices(), inputModel2.getVertices());
        System.out.println("The maximum hausorff distance is" + distance);
    }

    public static InputModel readTheModel(String filePath) throws IOException {
        System.out.println("--------COMPARISON PROCEDURE EXECUTING-------");

        //Variables initializing
        InputStream in = new FileInputStream(filePath);
        PlyReaderFile reader = new PlyReaderFile(in);
        int numFaces = reader.getElementCount("face");
        int numVertices = reader.getElementCount("vertex");
        Map<Integer, Vector3d> vertices = new HashMap<>(numVertices);
        Map<Integer, List<Integer>> faces = new HashMap<>(numFaces);
        //read the detail
        ReadPLY.read(reader, vertices, faces);
        System.out.println("--------File read from computer successfully-------");
        System.out.println("Info of the model");
        System.out.println("Number of elements:" + numFaces);
        System.out.println("Number of vertices:" + numVertices);

        AnalysisStep analysisStep = new AnalysisStep(vertices, faces);
        return analysisStep.createTheModel();
    }

    //subdivision workflow
    public static void workFlow() throws IOException {
        System.out.println("--------NORMAL PROCEDURE EXECUTING-------");
        //set the current timeMills
        long startTime = System.currentTimeMillis();

        //file location
        String modelName = "cow";
        String fileName = "C:\\Users\\tangj\\Downloads\\" + modelName + ".ply";

        //Variables initializing
        InputStream in = new FileInputStream(fileName);
        PlyReaderFile reader = new PlyReaderFile(in);
        int numFaces = reader.getElementCount("face");
        int numVertices = reader.getElementCount("vertex");
        Map<Integer, Vector3d> vertices = new HashMap<>(numVertices);
        Map<Integer, List<Integer>> faces = new HashMap<>(numFaces);
        //read the detail and Creation
        ReadPLY.read(reader, vertices, faces);
        System.out.println("--------Input coarse model read successfully-------");
        System.out.println("Info of the old model");
        System.out.println("Number of elements:" + numFaces);
        System.out.println("Number of vertices:" + numVertices);
        AnalysisStep analysisStep = new AnalysisStep(vertices, faces);
        InputModel inputModel = analysisStep.createTheModel();

        analysisStep.implementScheme1(inputModel);
        analysisStep.implementScheme1(analysisStep.createTheModel());
        analysisStep.implementScheme1(analysisStep.createTheModel());

        System.out.println("-------Subdivision scheme implemented successfully-------");
        InputModel newModel = analysisStep.createTheModel();

        //normal calculation
        Map<Integer, Vector3d> normalMap = ComparisonStep.getNormalForVertices(newModel);
        OutputModel outputModel = new OutputModel(analysisStep.getVertexMap(), analysisStep.getFaceMap(), normalMap);
        System.out.println("Info of the new model:");
        System.out.println("Number of elements:" + outputModel.getFaceMap().size());
        System.out.println("Number of vertices:" + outputModel.getVertexMap().size());

        //write the file
        //outputModel.writePLY(modelName + "_refined");
        outputModel.writePLYNormal(modelName + "_refined");
        //outputModel.writePLYCurvature(modelName + "_refined", ComparisonStep.getGaussianCurvature(newModel), ComparisonStep.getMeanCurvature(newModel));
        long endTime = System.currentTimeMillis();

        //ComparisonStep.writeSphereDiff(newModel);
        //accessQuality(newModel);
        //accessQualityOnExtra(newModel);

        System.out.println("-------Process finished-------");
        System.out.println("The program takes " + (endTime - startTime) / 1000d + "s");
    }

    public static void main(String[] args) throws IOException {
        //workFlow();
        InputModel model = readTheModel("C:\\Users\\tangj\\Downloads\\Fyp_Quant_data\\Cow_data\\111\\cow_refined.ply");
        ComparisonStep.writeCurvatureMean(model);
        ComparisonStep.writeCurvatureGaussian(model);

    }
}
