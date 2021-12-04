import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangshao
 */
@Getter
@Setter
public class AnalysisStep {
    private Map<Integer, Vector3d> vertexMap;
    private Map<Integer, List<Integer>> faceMap;

    public AnalysisStep(Map<Integer, Vector3d> vertexMap, Map<Integer, List<Integer>> faceMap) {
        this.faceMap = faceMap;
        this.vertexMap = vertexMap;

    }

    public InputModel createTheModel() {
        return new InputModel(this.vertexMap, this.faceMap);
    }

    public void implementScheme1(InputModel inputModel) {
        //implement the scheme here
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        LoopScheme loopScheme = new LoopScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = loopScheme.computeOdd();
        Map<Integer, Vector3d> vertexEvenMap = loopScheme.computeEven();
        Map<Integer, List<Integer>> faceMap = loopScheme.createTriangle();
        Map<Integer, Vector3d> newVertexMap = new HashMap<>();
        newVertexMap.putAll(vertexOddMap);
        newVertexMap.putAll(vertexEvenMap);
        this.vertexMap = newVertexMap;
        this.faceMap = faceMap;
    }

    public void implementScheme2(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        PeterReifScheme pScheme = new PeterReifScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = pScheme.computeOdd();
        Map<Integer, List<Integer>> faceMap = pScheme.createTriangle();
        this.vertexMap.putAll(vertexOddMap);
        this.faceMap = faceMap;
    }

    public void writePLY(String name) throws IOException {
        String fileName = "C:\\Users\\tangj\\Downloads\\" + name + ".ply";
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write("ply \nformat ascii 1.0\ncomment zipper output\n");
        bw.write("element vertex " + vertexMap.size() + "\n");
        bw.write("property float x\nproperty float y\nproperty float z\n");
        bw.write("element face " + faceMap.size() + "\n");
        bw.write("property list uchar int vertex_indices\n");
        bw.write("end_header\n");
        for (int i = 0; i < vertexMap.size(); i++) {
            Vector3d coord = vertexMap.get(i);
            bw.write(Double.toString(coord.getXVal()) + " ");
            bw.write(Double.toString(coord.getYVal()) + " ");
            bw.write(Double.toString(coord.getZVal()) + " ");
            bw.write("\n");
        }

        for (int iFace = 0; iFace < faceMap.size(); iFace++) {
            List<Integer> vertexIndices = faceMap.get(iFace);
            bw.write(3 + " ");
            for (int iVertex = 0; iVertex < 3; iVertex++) {
                bw.write(vertexIndices.get(iVertex) + " ");
            }
            bw.write("\n");
        }
        bw.close();
    }
}
