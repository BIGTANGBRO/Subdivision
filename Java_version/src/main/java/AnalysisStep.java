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

    public AnalysisStep(final Map<Integer, Vector3d> vertexMap, final Map<Integer, List<Integer>> faceMap) {
        this.faceMap = faceMap;
        this.vertexMap = vertexMap;
    }

    public void implementScheme1(InputModel inputModel) {
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
        return new InputModel(this.vertexMap, this.faceMap);
    }
}
