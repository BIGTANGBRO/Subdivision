import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * OutputModel类用于将信息写入特定文件
 *
 * @author: tangshao
 * @Date: 20/01/2022
 */
@Setter
@Getter
public class OutputModel {
    private Map<Integer, Vector3d> vertexMap;
    private Map<Integer, List<Integer>> faceMap;
    private Map<Integer, Vector3d> normalMap;

    public OutputModel(Map<Integer, Vector3d> vertexMap, Map<Integer, List<Integer>> faceMap) {
        this.faceMap = faceMap;
        this.vertexMap = vertexMap;
    }

    public OutputModel(Map<Integer, Vector3d> vertexMap, Map<Integer, List<Integer>> faceMap, Map<Integer, Vector3d> normalMap) {
        this.faceMap = faceMap;
        this.vertexMap = vertexMap;
        this.normalMap = normalMap;
    }

    public void writePLY(final String name) throws IOException {
        // 使用相对路径替代硬编码的绝对路径
        final String fileName = name + ".ply";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write("ply\nformat ascii 1.0\ncomment zipper output\n");
        bw.write("element vertex " + vertexMap.size() + "\n");
        bw.write("property float x\nproperty float y\nproperty float z\n");
        bw.write("element face " + faceMap.size() + "\n");
        bw.write("property list uchar int vertex_indices\n");
        bw.write("end_header\n");
        for (int i = 0; i < vertexMap.size(); i++) {
            final Vector3d coord = vertexMap.get(i);
            bw.write(coord.getXVal() + " ");
            bw.write(coord.getYVal() + " ");
            bw.write(coord.getZVal() + " ");
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

    public void writePLYNormal(final String name) throws IOException {
        // 使用相对路径替代硬编码的绝对路径
        final String fileName = name + "_with_normals.ply";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write("ply\nformat ascii 1.0\ncomment zipper output\n");
        bw.write("element vertex " + vertexMap.size() + "\n");
        bw.write("property float x\nproperty float y\nproperty float z\n");
        bw.write("property float nx\nproperty float ny\nproperty float nz\n");
        bw.write("element face " + faceMap.size() + "\n");
        bw.write("property list uchar int vertex_indices\n");
        bw.write("end_header\n");
        for (int i = 0; i < vertexMap.size(); i++) {
            final Vector3d coord = vertexMap.get(i);
            final Vector3d norm = normalMap != null ? normalMap.get(i) : new Vector3d(0, 0, 0);
            bw.write(coord.getXVal() + " ");
            bw.write(coord.getYVal() + " ");
            bw.write(coord.getZVal() + " ");
            // 写入法向量值
            bw.write(norm.getXVal() + " ");
            bw.write(norm.getYVal() + " ");
            bw.write(norm.getZVal() + " ");
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

    public void writePLYErrorSphere(final String name, Map<Integer, Double> error) throws IOException {
        // 使用相对路径替代硬编码的绝对路径
        final String fileName = name + "_error.ply";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write("ply\nformat ascii 1.0\ncomment zipper output\n");
        bw.write("element vertex " + vertexMap.size() + "\n");
        bw.write("property float x\nproperty float y\nproperty float z\n");
        bw.write("property float red\nproperty float green\nproperty float blue\n");
        bw.write("element face " + faceMap.size() + "\n");
        bw.write("property list uchar int vertex_indices\n");
        bw.write("end_header\n");

        double maxError = Collections.max(error.values());

        for (int i = 0; i < vertexMap.size(); i++) {
            final Vector3d coord = vertexMap.get(i);

            double errorVal = error.get(i) / maxError * 255d;
            if (errorVal <= 1) {
                errorVal = 1;
            }

            bw.write(coord.getXVal() + " ");
            bw.write(coord.getYVal() + " ");
            bw.write(coord.getZVal() + " ");
            // 属性
            bw.write(errorVal + " ");
            bw.write(0 + " ");
            bw.write(0 + " ");
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

    public void writePLYCurvature(final String name, Map<Integer, Double> gaussian, Map<Integer, Double> mean) throws IOException {
        // 使用相对路径替代硬编码的绝对路径
        final String fileName = name + "_curvature.ply";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write("ply\nformat ascii 1.0\ncomment zipper output\n");
        bw.write("element vertex " + vertexMap.size() + "\n");
        bw.write("property float x\nproperty float y\nproperty float z\n");
        bw.write("property float red\nproperty float green\nproperty float blue\n");
        bw.write("element face " + faceMap.size() + "\n");
        bw.write("property list uchar int vertex_indices\n");
        bw.write("end_header\n");

        double maxMean = Collections.max(mean.values());
        double maxGaussian = Collections.max(gaussian.values());

        for (int i = 0; i < vertexMap.size(); i++) {
            final Vector3d coord = vertexMap.get(i);

            double curvatureMean = 0d;
            double curvatureGaussian = 0d;

            if (abs(mean.get(i)) >= maxMean) {
                curvatureMean = 255d;
            } else {
                curvatureMean = abs(mean.get(i)) / maxMean * 255d;
            }
            if (abs(gaussian.get(i)) >= maxGaussian) {
                curvatureGaussian = 255d;
            } else {
                curvatureGaussian = abs(gaussian.get(i)) / maxGaussian * 255d;
            }

            if (curvatureGaussian <= 1d) {
                curvatureGaussian = 1d;
            }
            if (curvatureMean <= 1d) {
                curvatureMean = 1d;
            }

            bw.write(coord.getXVal() + " ");
            bw.write(coord.getYVal() + " ");
            bw.write(coord.getZVal() + " ");
            // 属性
            bw.write(curvatureGaussian + " ");
            bw.write(curvatureMean + " ");
            bw.write(0 + " ");
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

    public void writePLYCurvature2(final String name, Map<Integer, List<Double>> principalCurvature) throws IOException {
        // 使用相对路径替代硬编码的绝对路径
        final String fileName = name + "_principal_curvature.ply";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write("ply\nformat ascii 1.0\ncomment zipper output\n");
        bw.write("element vertex " + vertexMap.size() + "\n");
        bw.write("property float x\nproperty float y\nproperty float z\n");
        bw.write("property uchar red\nproperty float green\nproperty float blue\n");
        bw.write("element face " + faceMap.size() + "\n");
        bw.write("property list uchar int vertex_indices\n");
        bw.write("end_header\n");

        // 获取主曲率的最大值
        double maxk1 = Collections.max(principalCurvature.values(), (a, b) -> 
            Double.compare(abs(a.get(0)), abs(b.get(0)))) 
            .stream().mapToDouble(Double::doubleValue).max().orElse(150.0);
        double maxk2 = Collections.max(principalCurvature.values(), (a, b) -> 
            Double.compare(abs(a.get(1)), abs(b.get(1)))) 
            .stream().mapToDouble(Double::doubleValue).max().orElse(20.0);

        for (int i = 0; i < vertexMap.size(); i++) {
            final Vector3d coord = vertexMap.get(i);

            double k1 = 0d;
            double k2 = 0d;
            
            List<Double> curvatures = principalCurvature.get(i);
            if (curvatures != null && curvatures.size() >= 2) {
                if (abs(curvatures.get(0)) >= maxk1) {
                    k1 = 255d;
                } else {
                    k1 = abs(curvatures.get(0)) / maxk1 * 255d;
                }
                if (abs(curvatures.get(1)) >= maxk2) {
                    k2 = 255d;
                } else {
                    k2 = abs(curvatures.get(1)) / maxk2 * 255d;
                }

                if (k1 <= 1d) {
                    k1 = 1d;
                }
                if (k2 <= 1d) {
                    k2 = 1d;
                }

                bw.write(coord.getXVal() + " ");
                bw.write(coord.getYVal() + " ");
                bw.write(coord.getZVal() + " ");
                // 属性
                bw.write(k1 + " ");
                bw.write(k2 + " ");
                bw.write(0 + " ");
                bw.write("\n");
            }
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