import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Loop 细分方案
 * @author tangshao
 */
@Getter
@Setter
public class LoopScheme {
    protected List<Triangle> triangles;
    protected List<Vertex> vertices;
    protected List<Edge> edges;
    protected Map<Integer, Integer> oddNodeMap;
    protected Map<Integer, List<Integer>> trianglesTrackMap;
    protected Map<Integer, Vector3d> evenVertices;

    public LoopScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.oddNodeMap = new HashMap<>();
        this.edges = edges;
        this.trianglesTrackMap = new HashMap<>();
        this.evenVertices = new HashMap<>();
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
        Vector3d coord1 = v1.getCoords();
        Vector3d coord2 = v2.getCoords();
        
        // Loop方案中边的中点公式
        // 如果边的两端都是正则顶点（非边界），则使用标准权重
        // 否则使用边界规则
        
        if (v1.isBoundary() && v2.isBoundary()) {
            // 边界情况：1/2 * (v1 + v2)
            return MathUtils.addVector(
                MathUtils.dotVal(Constant.ONE_HALF, coord1),
                MathUtils.dotVal(Constant.ONE_HALF, coord2)
            );
        } else {
            // 内部边：3/8 * (v1 + v2) + 1/8 * (opposite1 + opposite2)
            // 但这里我们简化为：3/8 * (v1 + v2)
            return MathUtils.addVector(
                MathUtils.dotVal(Constant.THREE_EIGHTHS, coord1),
                MathUtils.dotVal(Constant.THREE_EIGHTHS, coord2)
            );
        }
    }

    /**
     * 计算偶数顶点的新位置
     * @return 包含偶数顶点新坐标的映射
     */
    public Map<Integer, Vector3d> computeEven() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        
        for (Vertex vertex : vertices) {
            Vector3d newCoord = computeEvenPoint(vertex);
            vertexMap.put(vertex.getIndex(), newCoord);
        }
        
        return vertexMap;
    }

    /**
     * 计算单个偶数顶点的新位置
     * @param vertex 要计算的顶点
     * @return 新的坐标
     */
    private Vector3d computeEvenPoint(Vertex vertex) {
        Vector3d oldCoord = vertex.getCoords();
        List<Integer> neighborIndices = vertex.getVertexIndices();
        int n = neighborIndices.size();
        
        if (vertex.isBoundary()) {
            // 边界顶点处理
            if (n == 2) { // 角点，只有两个邻接顶点
                Vector3d sumNeighbors = new Vector3d(0, 0, 0);
                for (Integer idx : neighborIndices) {
                    sumNeighbors = MathUtils.addVector(sumNeighbors, vertices.get(idx).getCoords());
                }
                // Slerp方案边界规则：新位置 = (6*v + (v_prev + v_next))/8
                return MathUtils.addVector(
                    MathUtils.dotVal(0.75, oldCoord),
                    MathUtils.dotVal(0.125, sumNeighbors)
                );
            } else { // 一般边界点
                // 查找边界上的前后顶点
                List<Vertex> neighbors = new ArrayList<>();
                for (Integer idx : neighborIndices) {
                    neighbors.add(vertices.get(idx));
                }
                
                // 简化的边界处理
                Vector3d prev = neighbors.get(0).getCoords();
                Vector3d next = neighbors.get(1).getCoords();
                
                return MathUtils.addVector(
                    MathUtils.dotVal(0.75, oldCoord),
                    MathUtils.dotVal(0.125, MathUtils.addVector(prev, next))
                );
            }
        } else {
            // 内部顶点处理
            if (n == 6) {
                // 正则顶点：使用标准权重
                Vector3d sumNeighbors = new Vector3d(0, 0, 0);
                for (Integer idx : neighborIndices) {
                    sumNeighbors = MathUtils.addVector(sumNeighbors, vertices.get(idx).getCoords());
                }
                
                // Loop权重：(1-n*beta)*oldCoord + beta*sumNeighbors
                // 其中beta = (5/8 - (3+2*cos(2π/n))^2/64) / n
                // 当n=6时，beta = 1/16
                double beta = 1.0 / 16.0;
                return MathUtils.addVector(
                    MathUtils.dotVal(1 - n * beta, oldCoord),
                    MathUtils.dotVal(beta, sumNeighbors)
                );
            } else {
                // 非正则顶点：计算权重
                Vector3d sumNeighbors = new Vector3d(0, 0, 0);
                for (Integer idx : neighborIndices) {
                    sumNeighbors = MathUtils.addVector(sumNeighbors, vertices.get(idx).getCoords());
                }
                
                // Loop方案的beta值计算
                double beta = (3.0 + 2.0 * Math.cos(2.0 * Math.PI / n)) / (4.0 * n);
                return MathUtils.addVector(
                    MathUtils.dotVal(1 - n * beta, oldCoord),
                    MathUtils.dotVal(beta, sumNeighbors)
                );
            }
        }
    }

    /**
     * 创建新三角形
     * @param vertexMap 包含顶点坐标的映射
     * @return 包含面索引的映射
     */
    public Map<Integer, List<Integer>> createTriangle(Map<Integer, Vector3d> vertexMap) {
        int faceCount = 0;
        Map<Integer, List<Integer>> faceMap = new HashMap<>();
        List<Integer> triangleIndexTracking = new ArrayList<>();

        for (final Triangle triangle : this.triangles) {
            final Set<Integer> oddVertexSet = new HashSet<>();
            Vector3d faceNormal = triangle.getUnitNormal();
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