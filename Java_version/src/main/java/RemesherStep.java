import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: tangshao
 * @Date: 2021/12/16
 */
@Setter
@Getter
public class RemesherStep {
    private List<Triangle> triangles;
    private List<Vertex> vertices;
    private List<Edge> edges;

    public RemesherStep(InputModel inputModel) {
        this.triangles = inputModel.getTriangles();
        this.vertices = inputModel.getVertices();
        this.edges = inputModel.getEdges();
    }

    public void formTriangle() {

    }

    public void placePoints() {

    }

    public void remesh() {

    }
}
