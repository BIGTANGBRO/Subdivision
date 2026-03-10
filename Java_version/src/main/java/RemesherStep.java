import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 重网格化步骤类
 * @author tangshao
 */
@Getter
@Setter
public class RemesherStep {
    private Map<Integer, Vector3d> vertexMap;
    private Map<Integer, List<Integer>> faceMap;
    private double featureSize; // 特征尺寸
    private double targetEdgeLength; // 目标边长

    public RemesherStep(Map<Integer, Vector3d> vertexMap, Map<Integer, List<Integer>> faceMap) {
        this.vertexMap = vertexMap;
        this.faceMap = faceMap;
        this.featureSize = 0.1; // 默认特征尺寸
        this.targetEdgeLength = 0.05; // 默认目标边长
    }

    /**
     * 设置目标边长
     * @param targetEdgeLength 目标边长
     */
    public void setTargetEdgeLength(double targetEdgeLength) {
        this.targetEdgeLength = targetEdgeLength;
    }

    /**
     * 设置特征尺寸
     * @param featureSize 特征尺寸
     */
    public void setFeatureSize(double featureSize) {
        this.featureSize = featureSize;
    }

    /**
     * 执行重网格化
     */
    public void performRemeshing() {
        // 1. 边长检查和调整
        adjustEdgeLengths();
        
        // 2. 特征保持
        preserveFeatures();
        
        // 3. 网格平滑
        smoothMesh();
    }

    /**
     * 调整边长
     */
    private void adjustEdgeLengths() {
        // 这是一个简化的实现
        // 实际的重网格化会更复杂，包括分裂过长的边和折叠过短的边
        System.out.println("Adjusting edge lengths...");
        
        // 遍历所有面，检查边长
        for (Map.Entry<Integer, List<Integer>> faceEntry : new HashMap<>(faceMap).entrySet()) {
            List<Integer> faceVertices = faceEntry.getValue();
            
            if (faceVertices.size() == 3) {
                // 检查三角形的三条边
                for (int i = 0; i < 3; i++) {
                    int v1Idx = faceVertices.get(i);
                    int v2Idx = faceVertices.get((i + 1) % 3);
                    
                    if (v1Idx < vertexMap.size() && v2Idx < vertexMap.size()) {
                        Vector3d v1 = vertexMap.get(v1Idx);
                        Vector3d v2 = vertexMap.get(v2Idx);
                        
                        if (v1 != null && v2 != null) {
                            double edgeLength = MathUtils.getMod(MathUtils.minusVector(v1, v2));
                            
                            // 如果边太长，需要细分
                            if (edgeLength > targetEdgeLength * 1.5) {
                                splitEdge(v1Idx, v2Idx);
                            }
                            // 如果边太短，可能需要折叠（在此简化处理中跳过）
                        }
                    }
                }
            }
        }
    }

    /**
     * 分割边
     * @param v1Idx 第一个顶点索引
     * @param v2Idx 第二个顶点索引
     */
    private void splitEdge(int v1Idx, int v2Idx) {
        Vector3d v1 = vertexMap.get(v1Idx);
        Vector3d v2 = vertexMap.get(v2Idx);
        
        if (v1 != null && v2 != null) {
            // 计算中点
            Vector3d midPoint = MathUtils.addVector(
                MathUtils.dotVal(0.5, v1),
                MathUtils.dotVal(0.5, v2)
            );
            
            // 添加新顶点
            int newVertexIdx = vertexMap.size();
            vertexMap.put(newVertexIdx, midPoint);
            
            System.out.println("Split edge between vertices " + v1Idx + " and " + v2Idx + ", added vertex " + newVertexIdx);
        }
    }

    /**
     * 保持特征
     */
    private void preserveFeatures() {
        System.out.println("Preserving features...");
        // 在实际实现中，这将检测和保持网格的重要特征，如边缘和角落
        // 此处为简化实现
    }

    /**
     * 平滑网格
     */
    private void smoothMesh() {
        System.out.println("Smoothing mesh...");
        
        // 简单的拉普拉斯平滑
        Map<Integer, Vector3d> newVertices = new HashMap<>();
        
        for (Map.Entry<Integer, Vector3d> vertexEntry : vertexMap.entrySet()) {
            int vertexIdx = vertexEntry.getKey();
            Vector3d currentVertex = vertexEntry.getValue();
            
            // 找到相邻顶点
            Set<Integer> adjacentVertices = findAdjacentVertices(vertexIdx);
            
            if (!adjacentVertices.isEmpty()) {
                Vector3d sum = new Vector3d(0, 0, 0);
                for (int adjIdx : adjacentVertices) {
                    Vector3d adjVertex = vertexMap.get(adjIdx);
                    if (adjVertex != null) {
                        sum = MathUtils.addVector(sum, adjVertex);
                    }
                }
                
                // 计算平均值
                Vector3d avg = MathUtils.dotVal(1.0 / adjacentVertices.size(), sum);
                
                // 应用平滑（混合原始位置和平均位置）
                Vector3d smoothedVertex = MathUtils.addVector(
                    MathUtils.dotVal(0.1, currentVertex),
                    MathUtils.dotVal(0.9, avg)
                );
                
                newVertices.put(vertexIdx, smoothedVertex);
            } else {
                newVertices.put(vertexIdx, currentVertex); // 没有相邻顶点则保持不变
            }
        }
        
        // 更新顶点
        vertexMap.putAll(newVertices);
    }

    /**
     * 查找相邻顶点
     * @param vertexIdx 顶点索引
     * @return 相邻顶点集合
     */
    private Set<Integer> findAdjacentVertices(int vertexIdx) {
        Set<Integer> adjacentVertices = new HashSet<>();
        
        // 遍历所有面，找到包含给定顶点的面，然后获取该面的其他顶点
        for (List<Integer> face : faceMap.values()) {
            if (face.contains(vertexIdx)) {
                for (int vIdx : face) {
                    if (vIdx != vertexIdx) {
                        adjacentVertices.add(vIdx);
                    }
                }
            }
        }
        
        return adjacentVertices;
    }

    /**
     * 更新面的连接关系以适应新顶点
     */
    public void updateFaceConnections() {
        System.out.println("Updating face connections...");
        // 在实际实现中，这将更新面的连接关系以适应新添加的顶点
        // 此处为简化实现
    }
}