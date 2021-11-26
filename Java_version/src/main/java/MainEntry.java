import org.smurn.jply.PlyReaderFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author tangshao
 */
public class MainEntry {
    public static void main(String[] args) throws IOException {
        //file reader
        String fileName = "C:\\Users\\tangj\\Downloads\\model lib\\bunny\\reconstruction\\bun_zipper_res4.ply";
        InputStream in = new FileInputStream(fileName);
        PlyReaderFile reader = new PlyReaderFile(in);
        int numFaces = reader.getElementCount("face");
        int numVertices = reader.getElementCount("vertex");

        //data initialization
        Map<Integer, Vector3d> vertices = new HashMap<>(numFaces);
        Map<Integer, List<Integer>> faces = new HashMap<>(numVertices);

        //read the detail
        ReadPLY.read(reader, vertices, faces);

        //generate the data structure
        System.out.println("------Input model read successfully-----");

        //start implementing the algorithms on the data structure
        AnalysisStep analysisStep = new AnalysisStep(vertices, faces);
        InputModel inputModel = analysisStep.createTheModel();
        analysisStep.implementSubdivision(inputModel);

        //todo:write the file
    }
}
