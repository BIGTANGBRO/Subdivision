import lombok.Getter;
import lombok.Setter;

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
        Map<Integer, Vector3d> vertexMap = loopScheme.computeOdd();
        Map<Integer, Vector3d> vertexEvenMap = loopScheme.computeEven();
        vertexMap.putAll(vertexEvenMap);
        Map<Integer, List<Integer>> faceMap = loopScheme.createTriangle();
        this.vertexMap = vertexMap;
        this.faceMap = faceMap;
    }

    public static void writeFile() {

    }
}
