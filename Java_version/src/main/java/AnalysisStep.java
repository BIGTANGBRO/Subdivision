import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.LinkOption;
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

    public AnalysisStep(final Map<Integer, Vector3d> vertexMap, final Map<Integer, List<Integer>> faceMap) {
        this.faceMap = faceMap;
        this.vertexMap = vertexMap;

    }

    public InputModel createTheModel() {
        return new InputModel(this.vertexMap, this.faceMap);
    }

    public void implementScheme1(final InputModel inputModel) {
        //implement the scheme here
        final List<Triangle> triangles = inputModel.getTriangles();
        final List<Edge> edges = inputModel.getEdges();
        final List<Vertex> vertices = inputModel.getVertices();
        final LoopScheme loopScheme = new LoopScheme(triangles, vertices, edges);
        final Map<Integer, Vector3d> vertexOddMap = loopScheme.computeOdd();
        final Map<Integer, Vector3d> vertexEvenMap = loopScheme.computeEven();
        final Map<Integer, List<Integer>> faceMap = loopScheme.createTriangle();
        final Map<Integer, Vector3d> newVertexMap = new HashMap<>();
        newVertexMap.putAll(vertexOddMap);
        newVertexMap.putAll(vertexEvenMap);
        this.vertexMap = newVertexMap;
        this.faceMap = faceMap;
    }

    public void implementScheme1_2(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        LoopScheme loopScheme = new LoopScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = loopScheme.computeOdd();
        Map<Integer, List<Integer>> faceMap = loopScheme.createTriangle();
        this.vertexMap.putAll(vertexOddMap);
        this.faceMap = faceMap;
    }

    public void implementScheme2(final InputModel inputModel) {
        final List<Triangle> triangles = inputModel.getTriangles();
        final List<Edge> edges = inputModel.getEdges();
        final List<Vertex> vertices = inputModel.getVertices();
        final PeterReifScheme pScheme = new PeterReifScheme(triangles, vertices, edges);
        final Map<Integer, Vector3d> vertexOddMap = pScheme.computeOdd();
        final Map<Integer, List<Integer>> faceMap = pScheme.createTriangle();
        this.vertexMap.putAll(vertexOddMap);
        this.faceMap = faceMap;
    }

    public void implementScheme3(final InputModel inputModel){
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        ModifiedButterflyScheme mScheme = new ModifiedButterflyScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = mScheme.computeOdd();
        Map<Integer, List<Integer>> faceMap = mScheme.createTriangle();
        this.vertexMap.putAll(vertexOddMap);
        this.faceMap = faceMap;
    }

    public void writePLY(final String name) throws IOException {
        final String fileName = "C:\\Users\\tangj\\Downloads\\" + name + ".ply";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write("ply \nformat ascii 1.0\ncomment zipper output\n");
        bw.write("element vertex " + vertexMap.size() + "\n");
        bw.write("property float x\nproperty float y\nproperty float z\n");
        bw.write("element face " + faceMap.size() + "\n");
        bw.write("property list uchar int vertex_indices\n");
        bw.write("end_header\n");
        for (int i = 0; i < vertexMap.size(); i++) {
            final Vector3d coord = vertexMap.get(i);
            bw.write(Double.toString(coord.getXVal()) + " ");
            bw.write(Double.toString(coord.getYVal()) + " ");
            bw.write(Double.toString(coord.getZVal()) + " ");
            bw.write("\n");
        }

        for (int iFace = 0; iFace < faceMap.size(); iFace++) {
            final List<Integer> vertexIndices = faceMap.get(iFace);
            bw.write(3 + " ");
            for (int iVertex = 0; iVertex < 3; iVertex++) {
                bw.write(vertexIndices.get(iVertex) + " ");
            }
            bw.write("\n");
        }
        bw.close();
    }
}
