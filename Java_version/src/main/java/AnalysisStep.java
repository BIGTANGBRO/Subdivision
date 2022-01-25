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
 * the class used to implement the subdivision scheme
 */
@Getter
@Setter
public class AnalysisStep {
    private Map<Integer, Vector3d> vertexMap;
    private Map<Integer, List<Integer>> faceMap;
    private Map<Integer, Vector3d> vertexNormals;

    public AnalysisStep(final Map<Integer, Vector3d> vertexMap, final Map<Integer, List<Integer>> faceMap, Map<Integer, Vector3d> vertexNormals) {
        this.faceMap = faceMap;
        this.vertexMap = vertexMap;
        this.vertexNormals = vertexNormals;
    }

    public void implementScheme1_2(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        LoopScheme loopScheme = new LoopScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = loopScheme.computeOdd();
        this.vertexMap.putAll(vertexOddMap);
        Map<Integer, List<Integer>> faceMap = loopScheme.createTriangle(this.vertexMap);
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

    public void implementScheme3(final InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        ModifiedButterflyScheme2 mScheme = new ModifiedButterflyScheme2(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = mScheme.computeOdd();
        Map<Integer, List<Integer>> faceMap = mScheme.createTriangle();
        this.vertexMap.putAll(vertexOddMap);
        this.faceMap = faceMap;
    }

    public InputModel createTheModel() {
        return new InputModel(this.vertexMap, this.faceMap, this.vertexNormals);
    }
}
