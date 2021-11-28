import lombok.Getter;
import lombok.Setter;

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

    public void implementSubdivision(InputModel inputModel) {
        //implement the subdivision scheme here
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

    public static void writeFile() {

    }
}
