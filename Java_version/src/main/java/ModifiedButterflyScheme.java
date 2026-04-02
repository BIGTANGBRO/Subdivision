import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 改进的蝴蝶细分方案
 *
 * @author: tangshao
 * @Date: 26/01/2022
 */
@Getter
@Setter
public class ModifiedButterflyScheme {
    protected List<Triangle> triangles;
    protected List<Vertex> vertices;
    protected List<Edge> edges;
    protected Map<Integer, Integer> oddNodeMap;
    protected Map<Integer, List<Integer>> trianglesTrackMap;
    protected double w = 0d; // 或 -1/16

    public ModifiedButterflyScheme(List<Triangle> triangles, List<Vertex> vertices, List<Edge> edges) {
        this.triangles = triangles;
        this.vertices = vertices;
        this.oddNodeMap = new HashMap<>();
        this.edges = edges;
        this.trianglesTrackMap = new HashMap<>();
    }

    /**
     * 按顺序获取邻近点
     *
     * @param vMain  主顶点
     * @param vNear  邻近顶点
     * @return 按顺序排列的顶点列表
     */
    public List<Vertex> getNeighbourPtsInOrder(Vertex vMain, Vertex vNear) {
        List<Integer> trianglesIndexNear = vMain.getTriangleIndices();
        List<Triangle> trianglesNear = new ArrayList<>();
        for (Integer triIndex : trianglesIndexNear) {
            trianglesNear.add(this.triangles.get(triIndex));
        }
        int iterN = 0;
        int maxN = trianglesNear.size() * vMain.getNumVertices() * 2;

        List<Vertex> verticesNear = new ArrayList<>();
        verticesNear.add(vNear);
        
        // 找到起始的相邻顶点
        boolean foundStart = false;
        for (Triangle triangle : trianglesNear) {
            if (triangle.containVertices(vMain, vNear)) {
                List<Vertex> remaining = triangle.getRemainInDirection(vMain);
                if (remaining.size() >= 2 && remaining.get(0).getIndex() == vNear.getIndex()) {
                    verticesNear.add(remaining.get(1));
                    foundStart = true;
                    break;
                }
            }
        }

        // 对于拓扑有问题的模型，增加鲁棒性
        if (!foundStart && verticesNear.size() <= 1) {
            for (Triangle triangle : trianglesNear) {
                if (triangle.containVertices(vMain, vNear)) {
                    Vertex remaining = triangle.getRemain(vMain, vNear);
                    if (remaining != null && !verticesNear.contains(remaining)) {
                        verticesNear.add(remaining);
                    }
                    break;
                }
            }
        }

        if (verticesNear.size() < 2) {
            return verticesNear; // 无法构建完整的邻域
        }
        
        Vertex vOld = verticesNear.get(1);
        while (verticesNear.size() != vMain.getNumVertices()) {
            if (iterN > maxN) {
                break;
            }
            boolean foundNext = false;
            for (Triangle triangle : trianglesNear) {
                if (triangle.containVertices(vMain, vOld)) {
                    Vertex vRemain = triangle.getRemain(vMain, vOld);
                    if (vRemain != null && !verticesNear.contains(vRemain)) {
                        verticesNear.add(vRemain);
                        vOld = vRemain;
                        foundNext = true;
                        break;
                    }
                }
            }
            if (!foundNext) {
                // 如果没有找到下一个顶点，尝试其他三角形
                iterN++;
            } else {
                iterN = 0; // 重置计数器，因为我们找到了一个新顶点
            }
        }
        return verticesNear;
    }

    // 获取模板
    public Map<String, List<Vertex>> getStencil(Vertex v1, Vertex v2) {
        List<Vertex> vertex1 = getNeighbourPtsInOrder(v1, v2);
        List<Vertex> vertex2 = getNeighbourPtsInOrder(v2, v1);
        Map<String, List<Vertex>> stencil = new HashMap<>();
        List<Vertex> aList = new ArrayList<>(2);
        List<Vertex> bList = new ArrayList<>(2);
        List<Vertex> cList = new ArrayList<>(4);
        List<Vertex> dList = new ArrayList<>(2);
        
        aList.add(v1);
        aList.add(v2);
        bList.add(vertex1.get(1));
        bList.add(vertex1.get(5));

        cList.add(vertex1.get(2));
        cList.add(vertex1.get(4));
        cList.add(vertex2.get(2));
        cList.add(vertex2.get(4));

        dList.add(vertex1.get(3));
        dList.add(vertex2.get(3));
        
        stencil.put("a", aList);
        stencil.put("b", bList);
        stencil.put("c", cList);
        stencil.put("d", dList);
        return stencil;
    }

    /**
     * 获取 n >= 5 时的系数
     *
     * @param j 索引
     * @param n 点的数量
     * @return 系数
     */
    protected double getCoeff(int j, int n) {
        double cosTerm1 = Math.cos(2.0d * Math.PI * j / n);
        double cosTerm2 = Math.cos(4.0d * Math.PI * j / n);
        return (0.25d + cosTerm1 + 0.5 * cosTerm2) / n;
    }

    public double[] getCoefficients(double w) {
        return new double[]{0.5d - w, 0.125d + 2d * w, -1d / 16d - w, w};
    }

    private Vector3d interpolateMidpoint(final Vertex v1, final Vertex v2) {
        return MathUtils.addVector(
            MathUtils.dotVal(Constant.ONE_HALF, v1.getCoords()),
            MathUtils.dotVal(Constant.ONE_HALF, v2.getCoords())
        );
    }

    private boolean isValidVector(final Vector3d coord) {
        return coord != null
            && Double.isFinite(coord.getXVal())
            && Double.isFinite(coord.getYVal())
            && Double.isFinite(coord.getZVal());
    }

    /**
     * 计算异常情况的坐标
     *
     * @param vMain 主顶点
     * @param vNear 邻近顶点
     * @return 计算出的顶点坐标
     */
    public Vector3d calculateExtraordinary(Vertex vMain, Vertex vNear) {
        // n 是邻居数量
        List<Vertex> vertexIndices = getNeighbourPtsInOrder(vMain, vNear);
        int n = vertexIndices.size();
        
        if (n == 3) {
            double[] coeffs = new double[]{5d / 12d, -1d / 12d, -1d / 12d};
            Vector3d sum = new Vector3d(0, 0, 0);
            for (int i = 0; i < 3 && i < vertexIndices.size(); i++) {
                Vertex v = vertexIndices.get(i);
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], v.getCoords()));
            }
            return MathUtils.addVector(sum, MathUtils.dotVal(3d / 4d, vMain.getCoords()));
        } else if (n == 4) {
            double[] coeffs = new double[]{3d / 8d, 0, -1d / 8d, 0};
            Vector3d sum = new Vector3d(0, 0, 0);
            for (int i = 0; i < 4 && i < vertexIndices.size(); i++) {
                Vertex v = vertexIndices.get(i);
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeffs[i], v.getCoords()));
            }
            return MathUtils.addVector(sum, MathUtils.dotVal(3d / 4d, vMain.getCoords()));
        } else {
            Vector3d sum = new Vector3d(0, 0, 0);
            for (int i = 0; i < n && i < vertexIndices.size(); i++) {
                double coeff = getCoeff(i, n);
                Vertex v = vertexIndices.get(i);
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff, v.getCoords()));
            }
            return MathUtils.addVector(sum, MathUtils.dotVal(3d / 4d, vMain.getCoords()));
        }
    }

    public Vector3d computeOdd(Vertex v1, Vertex v2) {
        final Vector3d coord;
        if (v1.isRegular() && v2.isRegular()) {
            Map<String, List<Vertex>> stencil = getStencil(v1, v2);
            List<Vertex> aList = stencil.get("a");
            List<Vertex> bList = stencil.get("b");
            List<Vertex> cList = stencil.get("c");
            List<Vertex> dList = stencil.get("d");
            double[] coeff = getCoefficients(this.w);
            Vector3d sum = new Vector3d(0, 0, 0);
            
            for (Vertex vertexNear : aList) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[0], vertexNear.getCoords()));
            }
            for (Vertex vertexNear : bList) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[1], vertexNear.getCoords()));
            }
            for (Vertex vertexNear : cList) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[2], vertexNear.getCoords()));
            }
            for (Vertex vertexNear : dList) {
                sum = MathUtils.addVector(sum, MathUtils.dotVal(coeff[3], vertexNear.getCoords()));
            }
            coord = sum;
        } else if (v1.isRegular()) {
            coord = calculateExtraordinary(v2, v1);
        } else if (v2.isRegular()) {
            coord = calculateExtraordinary(v1, v2);
        } else {
            Vector3d vCoord1 = calculateExtraordinary(v1, v2);
            Vector3d vCoord2 = calculateExtraordinary(v2, v1);
            coord = MathUtils.dotVal(Constant.HALF, MathUtils.addVector(vCoord1, vCoord2));
        }
        return isValidVector(coord) ? coord : interpolateMidpoint(v1, v2);
    }

    public Map<Integer, Vector3d> computeOdd() {
        Map<Integer, Vector3d> vertexMap = new HashMap<>();
        int index = this.vertices.size();
        for (Edge edge : edges) {
            Vertex v1 = edge.getA();
            Vertex v2 = edge.getB();
            Vector3d coord = computeOdd(v1, v2);
            vertexMap.put(index, coord);
            oddNodeMap.put(edge.getIndex(), index);
            index += 1;
        }
        return vertexMap;
    }

    public Map<Integer, List<Integer>> createTriangle(Map<Integer, Vector3d> vertexMap) {
        int faceCount = 0;
        Map<Integer, List<Integer>> faceMap = new HashMap<>();

        for (final Triangle triangle : this.triangles) {
            List<Integer> triangleIndexTracking = new ArrayList<>();
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
                if (vertexIndices.size() != 3) {
                    continue;
                }

                final Vector3d coord0 = vertexMap.get(vertexIndices.get(0));
                final Vector3d coord1 = vertexMap.get(vertexIndices.get(1));
                final Vector3d coord2 = vertexMap.get(vertexIndices.get(2));
                if (!isValidVector(coord0) || !isValidVector(coord1) || !isValidVector(coord2)) {
                    continue;
                }

                Vector3d subFaceNormal = MathUtils.getUnitNormal(coord0, coord1, coord2);
                
                // 检查面的朝向一致性
                if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                    Collections.swap(vertexIndices, 1, 2);
                }
                triangleIndexTracking.add(faceCount);
                faceMap.put(faceCount, vertexIndices);
                faceCount += 1;
            }
            // 连接新创建的奇数顶点形成一个面
            final List<Integer> oddVertexArr = new ArrayList<>(oddVertexSet);
            if (oddVertexArr.size() == 3) {
                final Vector3d coord0 = vertexMap.get(oddVertexArr.get(0));
                final Vector3d coord1 = vertexMap.get(oddVertexArr.get(1));
                final Vector3d coord2 = vertexMap.get(oddVertexArr.get(2));
                if (isValidVector(coord0) && isValidVector(coord1) && isValidVector(coord2)) {
                    Vector3d subFaceNormal = MathUtils.getUnitNormal(coord0, coord1, coord2);
                    if (MathUtils.getAngle(faceNormal, subFaceNormal) >= 90) {
                        Collections.swap(oddVertexArr, 1, 2);
                    }
                    triangleIndexTracking.add(faceCount);
                    faceMap.put(faceCount, oddVertexArr);
                    faceCount += 1;
                }
            }
            trianglesTrackMap.put(triangle.getIndex(), triangleIndexTracking);
        }
        return faceMap;
    }
}
