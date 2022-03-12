import lombok.Getter;
import lombok.Setter;

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

    public void implementSchemeGeo(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        PeterReifScheme pScheme = new PeterReifScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = pScheme.computeOdd();
        this.vertexMap.putAll(vertexOddMap);
        Map<Integer, List<Integer>> faceMap = pScheme.createTriangle(this.vertexMap);
        this.faceMap = faceMap;
    }

    public void implementScheme1(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        LoopScheme loopScheme = new LoopScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = loopScheme.computeOdd();
        this.vertexMap = loopScheme.computeEven();
        this.vertexMap.putAll(vertexOddMap);
        Map<Integer, List<Integer>> faceMap = loopScheme.createTriangle(this.vertexMap);
        this.faceMap = faceMap;
    }

    public void implementScheme1Regional(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        RegionalLoop regionalLoop = new RegionalLoop(triangles, vertices, edges);
        regionalLoop.applyThreshold();
        Map<Integer, Vector3d> vertexOddMap = regionalLoop.computeOdd();
        Map<Integer, Vector3d> vertexEvenMap = regionalLoop.computeEven();
        this.vertexMap.putAll(vertexEvenMap);
        this.vertexMap.putAll(vertexOddMap);
        Map<Integer, Vector3d> extraMap = regionalLoop.specialCase(this.vertexMap.size());
        this.vertexMap.putAll(extraMap);

        faceMap = regionalLoop.createTriangle(this.vertexMap);
        faceMap.putAll(regionalLoop.createOriginalTriangles());
    }

    public void implementScheme2(final InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        ModifiedButterflyScheme mScheme = new ModifiedButterflyScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = mScheme.computeOdd();
        this.vertexMap.putAll(vertexOddMap);
        Map<Integer, List<Integer>> faceMap = mScheme.createTriangle(this.vertexMap);
        this.faceMap = faceMap;
    }

    public void implementScheme3(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        Square3Scheme sScheme = new Square3Scheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = sScheme.insertPoints();
        this.vertexMap = sScheme.computeEven();
        this.vertexMap.putAll(vertexOddMap);
        Map<Integer, List<Integer>> faceMap = sScheme.createTriangle(this.vertexMap);
        this.faceMap = faceMap;
    }

    public InputModel createTheModel() {
        return new InputModel(this.vertexMap, this.faceMap);
    }
}
