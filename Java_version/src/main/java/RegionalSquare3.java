import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 区域Square3细分方案
 * @author tangshao
 */
@Getter
@Setter
public class RegionalSquare3 {
    protected List<Triangle> triangles;
    protected List<Vertex> vertices;
    protected List<Edge> edges;
    protected Map<Integer, Integer> oddNodeMap;
    protected Map<Integer, List<Integer>> trianglesTrackMap;
    protected Map<Integer, Vector3d> evenVertices;
    protected double threshold = 0.1; // 默认阈值
    
    // 存储区域信息
    protected Set<Integer> refinedRegions;
    protected Set<Integer> unrefinedRegions;

    public RegionalSquare3(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.oddNodeMap = new HashMap<>();
        this.edges = edges;
        this.trianglesTrackMap = new HashMap<>();
        this.evenVertices = new HashMap<>();
        this.refinedRegions = new HashSet<>();
        this.unrefinedRegions = new HashSet<>();
    }

    /**
     * 应用阈值决定哪些区域需要细分
     */
    public void applyThreshold() {
        for (int i = 0; i < triangles.size(); i++) {
            Triangle triangle = triangles.get(i);
            if (shouldRefine(triangle)) {
                refinedRegions.add(i);
            } else {
                unrefinedRegions.add(i);
            }
        }
    }

    /**
     * 判断是否应该细分指定的三角形
     * @param triangle 要判断的三角形
     * @return 是否需要细分
     */
    protected boolean shouldRefine(Triangle triangle) {
        // 检查是否有异常顶点
        for (Vertex vertex : triangle.getVertices()) {
            if (vertex.isExtraordinary()) {
                return true; // 异常顶点所在的三角形总是被细分
            }
        }
        
        // 检查三角形面积是否超过阈值
        if (triangle.getArea() > threshold) {
            return true;
        }
        
        // 检查三角形是否靠近特征区域
        if (triangle.isNearExtraordinary()) {
            return true;
        }
        
        // 随机细化一部分区域以保持网格质量
        return Math.random() < 0.3; // 30% 的三角形会被细化
    }

    /**
     * 插入新的点（奇数顶点）
     * @return 包含新插入顶点坐标的映射
     */
    public Map<Integer, Vector3d> insertPoints() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = this.vertices.size();
        
        for (Edge edge : edges) {
            // 检查这条边是否属于需要细化的区域
            if (shouldRefineEdge(edge)) {
                Vertex v1 = edge.getA();
                Vertex v2 = edge.getB();
                Vector3d coord = computeMidpoint(v1, v2);
                vertexMap.put(index, coord);
                oddNodeMap.put(edge.getIndex(), index);
            } else {
                // 不细化的边，将其标记为不需要添加新顶点
                oddNodeMap.put(edge.getIndex(), -1); // -1 表示不添加新顶点
            }
            index++;
        }
        return vertexMap;
    }

    /**
     * 计算偶数顶点的位置
     * @return 包含偶数顶点坐标的映射
     */
    public Map<Integer, Vector3d> computeEven() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        
        for (Vertex vertex : vertices) {
            // 对所有顶点都重新计算位置，因为周围的细分会影响顶点位置
            Vector3d newCoord = computeEvenPoint(vertex);
            vertexMap.put(vertex.getIndex(), newCoord);
        }
        
        return vertexMap;
    }

    /**
     * 检查边是否需要细化
     * @param edge 要检查的边
     * @return 是否需要细化
     */
    protected boolean shouldRefineEdge(Edge edge) {
        // 检查边连接的三角形是否需要细化
        for (Triangle triangle : triangles) {
            if (triangle.containVertices(edge.getA(), edge.getB()) && 
                refinedRegions.contains(triangle.getIndex())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算边的中点
     * @param v1 第一个顶点
     * @param v2 第二个顶点
     * @return 中点坐标
     */
    private Vector3d computeMidpoint(Vertex v1, Vertex v2) {
        Vector3d coord1 = v1.getCoords();
        Vector3d coord2 = v2.getCoords();
        
        // 使用加权平均而不是简单平均
        return MathUtils.addVector(
            MathUtils.dotVal(Constant.ONE_HALF, coord1),
            MathUtils.dotVal(Constant.ONE_HALF, coord2)
        );
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
                
                // Square3方案边界规则
                return MathUtils.addVector(
                    MathUtils.dotVal(0.625, oldCoord), // 5/8
                    MathUtils.dotVal(0.1875, sumNeighbors) // 3/16 * 2 = 6/16 = 3/8
                );
            } else { // 一般边界点
                // 查找边界上的前后顶点
                List<Vertex> neighbors = new ArrayList<>();
                for (Integer idx : neighborIndices) {
                    neighbors.add(vertices.get(idx));
                }
                
                Vector3d prev = neighbors.get(0).getCoords();
                Vector3d next = neighbors.get(1).getCoords();
                
                return MathUtils.addVector(
                    MathUtils.dotVal(0.625, oldCoord), // 5/8
                    MathUtils.dotVal(0.1875, MathUtils.addVector(prev, next)) // 3/16 each
                );
            }
        } else {
            // 内部顶点处理
            Vector3d sumNeighbors = new Vector3d(0, 0, 0);
            Vector3d sumDiagonals = new Vector3d(0, 0, 0);
            int diagonalCount = 0;
            
            // 首先计算所有相邻顶点的平均值
            for (Integer idx : neighborIndices) {
                sumNeighbors = MathUtils.addVector(sumNeighbors, vertices.get(idx).getCoords());
            }
            
            // Square3方案考虑对面的顶点
            // 简化处理：计算相邻顶点的平均值
            Vector3d avgNeighbors = MathUtils.dotVal(1.0 / n, sumNeighbors);
            
            // Square3方案的公式：新位置 = (9/16)*旧位置 + (3/16)*相邻平均 + (4/16)*对面平均
            // 这里简化为：新位置 = α*旧位置 + β*相邻平均
            double alpha = 0.5625; // 9/16
            double beta = 0.4375; // 7/16
            
            return MathUtils.addVector(
                MathUtils.dotVal(alpha, oldCoord),
                MathUtils.dotVal(beta, avgNeighbors)
            );
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
            // 检查是否要细化此三角形
            if (refinedRegions.contains(triangle.getIndex())) {
                final Set<Integer> oddVertexSet = new HashSet<>();
                Vector3d faceNormal = triangle.getUnitNormal();
                for (final Vertex vertex : triangle.getVertices()) {
                    final List<Edge> connectedEdges = triangle.getConnectedEdges(vertex);
                    final List<Integer> vertexIndices = new ArrayList<>(3);
                    vertexIndices.add(vertex.getIndex());
                    for (final Edge edge : connectedEdges) {
                        // 检查这条边是否生成了新顶点
                        if (oddNodeMap.get(edge.getIndex()) != -1) {
                            final int newVertexIndex = oddNodeMap.get(edge.getIndex());
                            oddVertexSet.add(newVertexIndex);
                            vertexIndices.add(newVertexIndex);
                        }
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
                if (oddVertexArr.size() == 3) { // 确保有三个顶点
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
            } else {
                // 不细化的三角形直接复制
                List<Integer> vertexIndices = new ArrayList<>();
                for (Vertex vertex : triangle.getVertices()) {
                    vertexIndices.add(vertex.getIndex());
                }
                faceMap.put(faceCount, vertexIndices);
                faceCount += 1;
            }
        }
        return faceMap;
    }
}
