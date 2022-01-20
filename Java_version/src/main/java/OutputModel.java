import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: tangshao
 * @Date: 20/01/2022
 */
@Setter
@Getter
public class OutputModel {
    private Map<Integer, Vector3d> vertexMap;
    private Map<Integer, List<Integer>> faceMap;
    private Map<Integer, Double> vals;

    public OutputModel(Map<Integer, Vector3d> vertexMap, Map<Integer, List<Integer>> faceMap){
        this.faceMap = faceMap;
        this.vertexMap = vertexMap;
    }

    public OutputModel(Map<Integer, Vector3d> vertexMap, Map<Integer, List<Integer>> faceMap, Map<Integer, Double> vals){
        this.faceMap = faceMap;
        this.vertexMap = vertexMap;
        this.vals = vals;
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
