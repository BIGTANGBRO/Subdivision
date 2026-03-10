import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Peter Reif 细分方案
 * @author tangshao
 */
@Getter
@Setter
public class PeterReifScheme {
    protected List<Triangle> triangles;
    protected List<Vertex> vertices;
    protected List<Edge> edges;
    protected Map<Integer, Integer> oddNodeMap;
    protected Map<Integer, List<Integer>> trianglesTrackMap;

    public PeterReifScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.oddNodeMap = new HashMap<>();
        this.edges = edges;
        this.trianglesTrackMap = new HashMap<>();
    }

    /**
     * 计算奇数顶点的位置
     * @return 包含新顶点坐标的映射
     */
    public Map<Integer, Vector3d> computeOdd() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = this.vertices.size();
        for (Edge edge : edges) {
            Vertex v1 = edge.getA();
            Vertex v2 = edge.getB();
            Vector3d coord = computeOddPoint(v1, v2);
            vertexMap.put(index, coord);
            oddNodeMap.put(edge.getIndex(), index);
            index++;
        }
        return vertexMap;
    }

    /**
     * 计算单个奇数顶点位置
     * @param v1 第一个顶点
     * @param v2 第二个顶点
     * @return 新顶点坐标
     */
    private Vector3d computeOddPoint(Vertex v1, Vertex v2) {
        // Peter Reif 方案的简化实现
        // 这里是线性插值，实际方案可能更复杂
        Vector3d coord1 = v1.getCoords();
        Vector3d coord2 = v2.getCoords();
        
        return MathUtils.addVector(
            MathUtils.dotVal(Constant.ONE_HALF, coord1),
            MathUtils.dotVal(Constant.ONE_HALF, coord2)
        );
    }

    /**
     * 创建新三角形
     * @param vertexMap 包含顶点坐标的映射
     * @return 包含面索引的映射
     */
    public Map<Integer, List<Integer>> createTriangle(Map<Integer, Vector3d> vertexMap) {
        int faceCount = 0;
        Map<Integer, List<Integer>> faceMap = new HashMap<>();

        for (final Triangle triangle : this.triangles) {
            final Set<Integer> oddVertexSet = new HashSet<>();
            Vector3d faceNormal = triangle.getUnitNormal();
            List<Integer> triangleIndexTracking = new ArrayList<>(); // 每次循环开始时创建新的列表
            
            for (final Vertex vertex : triangle.getVertices()) {
                final List<Edge> connectedEdges = triangle.getConnectedEdges(vertex);
                final List<Integer> vertexIndices = new ArrayList<>(3);
                vertexIndices.add(vertex.getIndex());
                for (final Edge edge : connectedEdges) {
                    final int newVertexIndex = oddNodeMap.get(edge.getIndex());
                    oddVertexSet.add(newVertexIndex);
                    vertexIndices.add(newVertexIndex);
                }
                Vector3d subFaceNormal = MathUtils.getUnitNormal(
                    vertexMap.get(vertexIndices.get(0)),
                    vertexMap.get(vertexIndices.get(1)),
                    vertexMap.get(vertexIndices.get(2))
                );

                if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                    Collections.swap(vertexIndices, 1, 2);
                }
                triangleIndexTracking.add(faceCount);
                faceMap.put(faceCount, vertexIndices);
                faceCount += 1;
            }
            // 连接新创建的奇数顶点形成一个面
            final List<Integer> oddVertexArr = new ArrayList<>(oddVertexSet);
            Vector3d subFaceNormal = MathUtils.getUnitNormal(
                vertexMap.get(oddVertexArr.get(0)),
                vertexMap.get(oddVertexArr.get(1)),
                vertexMap.get(oddVertexArr.get(2))
            );
            if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                Collections.swap(oddVertexArr, 1, 2);
            }
            triangleIndexTracking.add(faceCount);
            faceMap.put(faceCount, oddVertexArr);
            trianglesTrackMap.put(triangle.getIndex(), triangleIndexTracking);
            faceCount += 1;
        }
        return faceMap;
    }
}
