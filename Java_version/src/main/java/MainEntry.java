import org.smurn.jply.PlyReaderFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * @author tangshao
 */
public class MainEntry {
    public static void accessQuality(InputModel inputModel) throws IOException {
        //ComparisonStep.writeAngle(inputModel);
        ComparisonStep.writeCurvatureGaussian(inputModel);
        ComparisonStep.writeCurvatureMean(inputModel);
        ComparisonStep.writeCurvaturePrincipal(inputModel);
        System.out.println("--------Properties are written successfully-------");
    }

    public static void accessQualityOnExtra(InputModel inputModel) throws IOException {
        //ComparisonStepSeparate.writeAngle(inputModel);
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

    public static OutputModel getOutputModel(String filePath) throws IOException {
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

        OutputModel outputModel = new OutputModel(vertices, faces);
        return outputModel;
    }

    //subdivision workflow
    public static void workFlow() throws IOException {
        System.out.println("--------NORMAL PROCEDURE EXECUTING-------");
        //set the current timeMills
        long startTime = System.currentTimeMillis();

        //file location
        String modelName = "sphere";
        String fileName = "C:\\Users\\jt2418\\Downloads\\" + modelName + ".ply";

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

        analysisStep.implementScheme3(inputModel);
        //analysisStep.implementScheme3(analysisStep.createTheModel());
        //analysisStep.implementScheme3(analysisStep.createTheModel());

        System.out.println("-------Subdivision scheme implemented successfully-------");
        InputModel newModel = analysisStep.createTheModel();

        //normal calculation
        Map<Integer, Vector3d> normalMap = ComparisonStep.getNormalForVertices(newModel);
        OutputModel outputModel = new OutputModel(analysisStep.getVertexMap(), analysisStep.getFaceMap(), normalMap);
        System.out.println("Info of the new model:");
        System.out.println("Number of elements:" + outputModel.getFaceMap().size());
        System.out.println("Number of vertices:" + outputModel.getVertexMap().size());

        //write the file
        outputModel.writePLYNormal(modelName + "_refined");
        //outputModel.writePLYCurvature(modelName + "_refined", ComparisonStep.getGaussianCurvature(newModel), ComparisonStep.getMeanCurvature(newModel));
        //outputModel.writePLYCurvature2(modelName + "_refined2", ComparisonStep.getPrincipalCurvature(newModel));

        long endTime = System.currentTimeMillis();

        //ComparisonStep.writeSphereR(newModel);
        //accessQuality(newModel);
        //accessQualityOnExtra(newModel);

        System.out.println("-------Process finished-------");
        System.out.println("The program takes " + (endTime - startTime) / 1000d + "s");
    }

    public static void main(String[] args) throws IOException {
        workFlow();

        //InputModel modelInput = readTheModel("C:\\Users\\tangj\\Downloads\\Fyp_Quant_data\\Cow_data\\3\\cow_refined.ply");
        //ComparisonStep.writeCurvatureMean(model);
        //ComparisonStep.writeCurvaturePrincipal(model);
        //ComparisonStepSeparate.writeCurvature2(model);

        //InputModel modelInput = readTheModel("C:\\Users\\tangj\\Downloads\\Fyp_simulation_models\\STL_produced\\Design_Regular3.ply");
        //InputModel modelInput = readTheModel("C:\\Users\\tangj\\Downloads\\Fyp_Quant_data\\Sphere_Regular_data\\stl_sphere\\Design1.ply");
        //ComparisonStep.writeHausorffDistribution(modelInput.getVertices(), modelAnalytical.getVertices(), 1);
        //ComparisonStep.writeHausorffDistribution(modelAnalytical.getVertices(), modelInput.getVertices(), 2);
        //double averageH2 = ComparisonStep.getAverageH(modelInput);
        //System.out.println("Average h of the second model is " + averageH2);
    }
}
