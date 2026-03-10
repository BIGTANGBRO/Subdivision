import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;

import java.io.IOException;
import java.util.*;

/**
 * 主入口类
 * 
 * @author tangshao
 */
public class MainEntry {

    /**
     * 比较不同细分方案的性能
     * @param inputModel 输入模型
     */
    private static void compareSchemes(InputModel inputModel) {
        // 准备测试数据
        Map<Integer, Vector3d> baseVertices = new HashMap<>();
        Map<Integer, List<Integer>> baseFaces = new HashMap<>();

        // 创建性能比较实例
        ComparisonStepSeparate comparisonStep = new ComparisonStepSeparate(baseVertices, baseFaces);

        // 执行性能测试
        comparisonStep.performanceTest(inputModel);

        // 保存测试结果
        comparisonStep.saveTestResults("performance_comparison");
    }

    public static void main(String[] args) {
        System.out.println("开始处理PLY模型...");

        if (args.length < 1) {
            System.err.println("用法: java MainEntry <PLY文件路径>");
            return;
        }

        String filePath = args[0];
        System.out.println("正在读取文件: " + filePath);

        try {
            // 读取PLY文件
            PlyReader plyReader = new PlyReaderFile(filePath);
            Map<Integer, Vector3d> vertices = new HashMap<>();
            Map<Integer, List<Integer>> faces = new HashMap<>();
            
            ReadPLY.read(plyReader, vertices, faces);
            System.out.println("成功读取 " + vertices.size() + " 个顶点和 " + faces.size() + " 个面");

            // 创建输入模型
            InputModel inputModel = new InputModel(vertices, faces);
            System.out.println("输入模型创建完成");

            // 执行分析步骤 - 可选择不同的细分方案
            Map<Integer, Vector3d> outputVertices = new HashMap<>(vertices);
            Map<Integer, List<Integer>> outputFaces = new HashMap<>(faces);

            // 示例：使用Loop方案进行细分
            AnalysisStep analysisStep = new AnalysisStep(outputVertices, outputFaces);
            analysisStep.implementScheme1(inputModel);
            System.out.println("细分完成");

            // 创建输出模型
            OutputModel outputModel = new OutputModel(analysisStep.getVertexMap(), analysisStep.getFaceMap());
            
            // 尝试写入PLY文件
            try {
                outputModel.writePLY("output_model");
                System.out.println("输出文件已保存为 output_model.ply");
            } catch (IOException e) {
                System.err.println("写入PLY文件时发生错误: " + e.getMessage());
            }

            // 性能比较测试
            System.out.println("\n开始性能比较测试...");
            compareSchemes(inputModel);

            // 关闭PLY读取器
            plyReader.close();

        } catch (IOException e) {
            System.err.println("读取PLY文件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
