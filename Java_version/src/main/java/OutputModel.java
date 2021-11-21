import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: tangshao
 * @Date: 2021/11/21
 */

@Getter
@Setter
public class OutputModel {
    private List<Triangle> triangles;
    private List<Vertex> vertices;

    public OutputModel(){

    }

    public OutputModel(List<Triangle> triangles, List<Vertex> vertices){
        this.triangles = triangles;
        this.vertices = vertices;
    }

    public void writeVTKFile(){
        //write the data into the .vtk file
    }
}
