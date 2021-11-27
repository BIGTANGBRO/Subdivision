import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author: tangshao
 * @Date: 2021/11/25
 */
public class WriteFile {
    public void vtkFileWriter() {

    }

    public static void plyFileWriter(Map<Integer, Vector3d> vertices, Map<Integer, List<Integer>> faces) throws IOException {
        String fileName = "C:\\Users\\tangj\\Downloads\\sub.ply";
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write("ply \nformat ascii 1.0\ncomment zipper output\n");
        bw.write("element vertex " + vertices.size() + "\n");
        bw.write("property float x\nproperty float y\nproperty float z\n");
        bw.write("element face " + faces.size() + "\n");
        bw.write("property list uchar int vertex_indices\n");
        bw.write("end_header\n");
        for (int i = 0; i < vertices.size();i++){
            Vector3d coord = vertices.get(i);
            bw.write(Double.toString(coord.getXVal()) + " ");
            bw.write(Double.toString(coord.getYVal()) + " ");
            bw.write(Double.toString(coord.getZVal()) + " ");
            bw.write("\n");
        }

        for (int iFace = 0;iFace < faces.size();iFace++){
            List<Integer> vertexIndices = faces.get(iFace);
            bw.write(3 + " ");
            for (int iVertex = 0;iVertex < 3; iVertex ++){
                bw.write(vertexIndices.get(iVertex) + " ");
            }
            bw.write("\n");
        }
        bw.close();
    }
}
