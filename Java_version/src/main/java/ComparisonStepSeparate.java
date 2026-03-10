import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 单独的比较步骤类，用于比较不同的细分方案
 * @author tangshao
 */
@Getter
@Setter
public class ComparisonStepSeparate {
    private Map<Integer, Vector3d> vertexMap;
    private Map<Integer, List<Integer>> faceMap;

    public ComparisonStepSeparate(final Map<Integer, Vector3d> vertexMap, final Map<Integer, List<Integer>> faceMap) {
        this.faceMap = faceMap;
        this.vertexMap = vertexMap;
    }

    /**
     * 实现PeterReif方案
     */
    public void implementPeterReifScheme(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        PeterReifScheme pScheme = new PeterReifScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = pScheme.computeOdd();
        this.vertexMap.putAll(vertexOddMap);
        Map<Integer, List<Integer>> faceMap = pScheme.createTriangle(this.vertexMap);
        this.faceMap = faceMap;
    }

    /**
     * 实现Loop方案
     */
    public void implementLoopScheme(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        LoopScheme loopScheme = new LoopScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = loopScheme.computeOdd();
        Map<Integer, Vector3d> vertexEvenMap = loopScheme.computeEven();
        this.vertexMap.putAll(vertexEvenMap);
        this.vertexMap.putAll(vertexOddMap);
        Map<Integer, List<Integer>> faceMap = loopScheme.createTriangle(this.vertexMap);
        this.faceMap = faceMap;
    }

    /**
     * 实现区域Loop方案
     */
    public void implementRegionalLoopScheme(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        RegionalLoop regionalLoop = new RegionalLoop(triangles, vertices, edges);
        regionalLoop.applyThreshold();

        // 计算顶点
        Map<Integer, Vector3d> vertexOddMap = regionalLoop.computeOdd();
        Map<Integer, Vector3d> vertexEvenMap = regionalLoop.computeEven();
        this.vertexMap.putAll(vertexEvenMap);
        this.vertexMap.putAll(vertexOddMap);

        // 连接三角形
        this.faceMap = regionalLoop.createTriangle(this.vertexMap);
    }

    /**
     * 实现改进的蝴蝶方案
     */
    public void implementModifiedButterflyScheme(final InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        ModifiedButterflyScheme mScheme = new ModifiedButterflyScheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = mScheme.computeOdd();
        this.vertexMap.putAll(vertexOddMap);
        this.faceMap = mScheme.createTriangle(this.vertexMap);
    }

    /**
     * 实现区域蝴蝶方案
     */
    public void implementRegionalButterflyScheme(final InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        RegionalButterfly mRegionalScheme = new RegionalButterfly(triangles, vertices, edges);
        mRegionalScheme.applyThreshold();
        Map<Integer, Vector3d> vertexOddMap = mRegionalScheme.computeOdd();
        this.vertexMap.putAll(vertexOddMap);
        this.faceMap = mRegionalScheme.createTriangle(this.vertexMap);
    }

    /**
     * 实现Square3方案
     */
    public void implementSquare3Scheme(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        Square3Scheme sScheme = new Square3Scheme(triangles, vertices, edges);
        Map<Integer, Vector3d> vertexOddMap = sScheme.insertPoints();
        this.vertexMap = sScheme.computeEven();
        this.vertexMap.putAll(vertexOddMap);
        Map<Integer, List<Integer>> faceMap = sScheme.createTriangle(this.vertexMap);
        this.faceMap = faceMap;
    }

    /**
     * 实现区域Square3方案
     */
    public void implementRegionalSquare3Scheme(InputModel inputModel) {
        List<Triangle> triangles = inputModel.getTriangles();
        List<Edge> edges = inputModel.getEdges();
        List<Vertex> vertices = inputModel.getVertices();
        RegionalSquare3 regionalSquare = new RegionalSquare3(triangles, vertices, edges);
        regionalSquare.applyThreshold();

        // 计算顶点
        Map<Integer, Vector3d> vertexOddMap = regionalSquare.insertPoints();
        Map<Integer, Vector3d> vertexEvenMap = regionalSquare.computeEven();
        this.vertexMap.putAll(vertexEvenMap);
        this.vertexMap.putAll(vertexOddMap);

        // 连接三角形
        this.faceMap = regionalSquare.createTriangle(this.vertexMap);
    }

    /**
     * 创建模型
     * @return 输入模型
     */
    public InputModel createModel() {
        return new InputModel(this.vertexMap, this.faceMap);
    }

    /**
     * 测试所有方案的性能
     */
    public void performanceTest(InputModel inputModel) {
        System.out.println("Starting performance tests...");
        
        // 测试PeterReif方案
        testSchemePerformance(() -> implementPeterReifScheme(inputModel), "PeterReif");
        
        // 重置数据
        resetData();
        
        // 测试Loop方案
        testSchemePerformance(() -> implementLoopScheme(inputModel), "Loop");
        
        // 重置数据
        resetData();
        
        // 测试区域Loop方案
        testSchemePerformance(() -> implementRegionalLoopScheme(inputModel), "RegionalLoop");
        
        // 重置数据
        resetData();
        
        // 测试改进的蝴蝶方案
        testSchemePerformance(() -> implementModifiedButterflyScheme(inputModel), "ModifiedButterfly");
        
        // 重置数据
        resetData();
        
        // 测试区域蝴蝶方案
        testSchemePerformance(() -> implementRegionalButterflyScheme(inputModel), "RegionalButterfly");
        
        // 重置数据
        resetData();
        
        // 测试Square3方案
        testSchemePerformance(() -> implementSquare3Scheme(inputModel), "Square3");
        
        // 重置数据
        resetData();
        
        // 测试区域Square3方案
        testSchemePerformance(() -> implementRegionalSquare3Scheme(inputModel), "RegionalSquare3");
    }

    /**
     * 测试方案性能
     * @param schemeImplementation 方案实现
     * @param schemeName 方案名称
     */
    private void testSchemePerformance(Runnable schemeImplementation, String schemeName) {
        long startTime = System.currentTimeMillis();
        long startMemory = getUsedMemory();
        
        schemeImplementation.run();
        
        long endTime = System.currentTimeMillis();
        long endMemory = getUsedMemory();
        
        System.out.printf("%s方案: %d ms, 内存变化: %d KB%n", 
                         schemeName, endTime - startTime, (endMemory - startMemory) / 1024);
    }

    /**
     * 获取当前使用的内存量
     * @return 已使用的内存量（字节）
     */
    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * 重置数据
     */
    private void resetData() {
        this.vertexMap.clear();
        this.faceMap.clear();
    }

    /**
     * 保存测试结果到文件
     * @param fileName 文件名
     */
    public void saveTestResults(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + "_performance_test_results.txt"))) {
            writer.write("Performance Test Results:\n");
            writer.write("Number of Vertices: " + vertexMap.size() + "\n");
            writer.write("Number of Faces: " + faceMap.size() + "\n");
            writer.write("Test completed.\n");
        } catch (IOException e) {
            System.err.println("Error saving test results: " + e.getMessage());
        }
    }
}
