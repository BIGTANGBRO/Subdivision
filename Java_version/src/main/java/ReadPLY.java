import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReaderFile;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangshao
 */
public class ReadPLY {
    //read the vertex data and face data from the ply data
    public static void read(PlyReaderFile reader, Map<Integer, Vector3d> vertices, Map<Integer, List<Integer>> faces) throws IOException {
        ElementReader elementReader = reader.nextElementReader();
        Integer vertexIndex = 0;
        Integer faceIndex = 0;
        while (elementReader != null) {

            Element element = elementReader.readElement();
            while (element != null) {
                if ("vertex".equals(element.getType().getName())) {
                    double xCoord = element.getDouble("x");
                    double yCoord = element.getDouble("y");
                    double zCoord = element.getDouble("z");
                    Vector3d coord = new Vector3d(xCoord, yCoord, zCoord);
                    vertices.put(vertexIndex, coord);
                    vertexIndex += 1;
                } else if ("face".equals(element.getType().getName())) {
                    double[] vertexArr = element.getDoubleList("vertex_indices");
                    List<Integer> vertexList = new ArrayList<Integer>(3);
                    for (double v : vertexArr) {
                        vertexList.add((int) v);
                    }
                    faces.put(faceIndex, vertexList);
                    faceIndex += 1;
                }
                // next element
                element = elementReader.readElement();
            }
            elementReader.close();
            elementReader = reader.nextElementReader();
        }
    }

    public static void updateNeighbours(){
        //update the neighbour of the vertex

    }
}
