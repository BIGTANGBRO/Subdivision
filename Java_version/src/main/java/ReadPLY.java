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
    public static void read(final PlyReaderFile reader, final Map<Integer, Vector3d> vertices, final Map<Integer, List<Integer>> faces) throws IOException {
        ElementReader elementReader = reader.nextElementReader();
        int vertexIndex = 0;
        int faceIndex = 0;
        while (elementReader != null) {

            Element element = elementReader.readElement();
            while (element != null) {
                if ("vertex".equals(element.getType().getName())) {
                    final double xCoord = element.getDouble("x");
                    final double yCoord = element.getDouble("y");
                    final double zCoord = element.getDouble("z");
                    final Vector3d coord = new Vector3d(xCoord, yCoord, zCoord);
                    vertices.put(vertexIndex, coord);
                    vertexIndex += 1;
                } else if ("face".equals(element.getType().getName())) {
                    //vertex_indices
                    final double[] vertexArr = element.getDoubleList("vertex_indices");
                    final List<Integer> vertexList = new ArrayList<Integer>(3);
                    for (final double v : vertexArr) {
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
}
