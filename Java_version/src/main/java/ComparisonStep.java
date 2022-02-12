import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.pow;

/**
 * @author: tangshao
 * @Date: 2021/12/8
 * The class used to assess the quality of the mesh model.
 */
public class ComparisonStep {

    //Theoretical difference between the mesh and the model
    private static double getSphereZSqure(final double x, final double y) {
        return pow(127.0d, 2) - pow(x, 2) - pow(y, 2);
    }

    public static double getSphereError(final Map<Integer, Vector3d> vertices) {
        //get the data from vertices;
        double error = 0.0d;
        for (final Map.Entry<Integer, Vector3d> entry : vertices.entrySet()) {
            final Vector3d coord = entry.getValue();
            final double x = coord.getXVal();
            final double y = coord.getYVal();
            final double zRef = Math.sqrt(getSphereZSqure(x, y));
            final double z = coord.getZVal();
            final double diff = Math.abs(Math.abs(z) - Math.abs(zRef));
            error += diff;
        }
        return error / (double) vertices.size();
    }

    //function to calculate the hausorff error
    private static double getMinDistance(final List<Vertex> vertices1, final List<Vertex> vertices2) {
        double maxDistance = 0d;
        for (final Vertex vertex1 : vertices1) {
            double minDist = Double.POSITIVE_INFINITY;
            for (final Vertex vertex2 : vertices2) {
                final double distance = MathUtils.minusVector(vertex1.getCoords(), vertex2.getCoords()).getMod();
                if (distance <= minDist) {
                    minDist = distance;
                }
            }
            if (minDist > maxDistance) {
                maxDistance = minDist;
            }
        }
        return maxDistance;
    }

    private static List<Double> getMinDistanceDistribution(final List<Vertex> vertices1, final List<Vertex> vertices2) {
        final List<Double> vertexDistances = new ArrayList<>(vertices1.size());
        for (final Vertex vertex1 : vertices1) {
            double minDist = Double.POSITIVE_INFINITY;
            for (final Vertex vertex2 : vertices2) {
                final double distance = MathUtils.minusVector(vertex1.getCoords(), vertex2.getCoords()).getMod();
                if (distance <= minDist) {
                    minDist = distance;
                }
            }
            vertexDistances.add(minDist);
        }
        return vertexDistances;
    }

    /**
     * Calculate the global hausorffDistance
     *
     * @param vertices1 Vertices list for model1
     * @param vertices2 Vertices list for model2
     * @return the error
     */
    public static double getHausorffDistance(final List<Vertex> vertices1, final List<Vertex> vertices2) {
        final double maxDA = getMinDistance(vertices1, vertices2);
        final double maxDB = getMinDistance(vertices2, vertices1);
        return Math.max(maxDA, maxDB);
    }

    /**
     * Output the data to plot the histogram
     *
     * @param vertices1 vertices from model1
     * @param vertices2 vertices from model2
     * @throws IOException Ioexception
     */
    public static void writeHausorffDistribution(final List<Vertex> vertices1, final List<Vertex> vertices2) throws IOException {
        final List<Double> distribution1 = getMinDistanceDistribution(vertices1, vertices2);
        final String fileName = "C:\\Users\\tangj\\Downloads\\distribution.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (final Double distance : distribution1) {
            bw.write(Double.toString(distance) + "\n");
        }
        bw.close();
    }

    //laplacian of vertex for roughness
    public static List<Vector3d> getRoughness(final List<Vertex> vertices) {
        final List<Vector3d> gls = new ArrayList<>();
        for (final Vertex vertex : vertices) {
            final List<Integer> indices = vertex.getVertexIndices();
            Vector3d numerator = new Vector3d(0, 0, 0);
            double denominator = 0d;
            for (final Integer index : indices) {
                final Vertex vNear = vertices.get(index);
                final Vector3d distanceVec = MathUtils.minusVector(vertex.getCoords(), vNear.getCoords());
                final double lapDistance = MathUtils.getSum(distanceVec) / MathUtils.getMod(distanceVec);
                denominator = denominator + Math.pow(lapDistance, -1);
                numerator = MathUtils.addVector(numerator, MathUtils.dotVal(Math.pow(lapDistance, -1), vNear.getCoords()));
            }
            final Vector3d gl = MathUtils.minusVector(vertex.getCoords(), MathUtils.dotVal(Math.pow(denominator, -1), numerator));
            gls.add(gl);
        }
        return gls;
    }

    /**
     * Get the dihedral angle for each triangle face
     *
     * @param inputModel InputModel class
     * @return The list of dihedral angles in this model
     */
    private static List<Double> computeDihedralAngle(final InputModel inputModel) {
        final List<Triangle> triangles = inputModel.getTriangles();
        final List<Vertex> vertices = inputModel.getVertices();
        final List<Double> dihedralAngles = new ArrayList<>();
        for (final Triangle triangle : triangles) {
            final List<Vertex> verticesTri = triangle.getVertices();

            double numerator = 0d;
            double denominator = 0d;

            //In a single triangle, get the val for each vertex
            for (final Vertex v : verticesTri) {
                final List<Integer> triangleIndices = v.getTriangleIndices();
                final List<Triangle> trianglesNear = new ArrayList<>();
                final List<Double> angles = new ArrayList<>();
                for (final Integer triangleIndex : triangleIndices) {
                    trianglesNear.add(triangles.get(triangleIndex));
                }

                final List<Integer> vertexIndices = v.getVertexIndices();
                //find the triangles share an edge
                for (final Integer vertexIndex : vertexIndices) {
                    final List<Vector3d> trianglesAnEdge = new ArrayList<>();
                    for (final Triangle triangleNear : trianglesNear) {
                        if (triangleNear.containVertices(v, vertices.get(vertexIndex))) {
                            trianglesAnEdge.add(triangleNear.getUnitNormal());
                        }
                    }
                    final double angle = MathUtils.getAngle(trianglesAnEdge.get(0), trianglesAnEdge.get(1));
                    angles.add(angle);
                }
                //calculate the average and variance
                final double average = MathUtils.getAverage(angles);
                final double variance = MathUtils.getVariance(angles, average);
                numerator += (average * variance);
                denominator += variance;
            }
            dihedralAngles.add(numerator / denominator);
        }
        return dihedralAngles;
    }

    public static void writeAngle(final InputModel inputModel) throws IOException {
        final List<Double> dAngles = computeDihedralAngle(inputModel);
        final String fileName = "C:\\Users\\tangj\\Downloads\\distributionAngle.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (final Double angle : dAngles) {
            bw.write(Double.toString(angle) + "\n");
        }
        bw.close();
    }

    //For normal calculation
    private static Vector3d getNormalForVertex(final Vertex vertex, final List<Triangle> triangles) {
        final List<Integer> neighbours = vertex.getTriangleIndices();
        Vector3d vNorm = new Vector3d(0, 0, 0);
        for (final Integer index : neighbours) {
            final Triangle triangle = triangles.get(index);
            final Vector3d normFace = triangle.getUnitNormal();
            vNorm = MathUtils.addVector(vNorm, normFace);
        }
        return MathUtils.dotVal(1d / (double) neighbours.size(), vNorm);
    }

    /**
     * get the normal for each vertex
     *
     * @param inputModel inputmodel
     * @return map contain the vertex index and normal values
     */
    public static Map<Integer, Vector3d> getNormalForVertices(final InputModel inputModel) {
        final List<Vertex> vertices = inputModel.getVertices();
        final List<Triangle> triangles = inputModel.getTriangles();
        final Map<Integer, Vector3d> normMap = new HashMap<>();
        for (final Vertex vertex : vertices) {
            final Vector3d normV = getNormalForVertex(vertex, triangles);
            normMap.put(vertex.getIndex(), normV);
        }
        return normMap;
    }

    private static List<Triangle> getNeighbourTriangleInOrder(Vertex v, List<Triangle> triangles, List<Vertex> vertices) {
        List<Vertex> verticesNear = getNeighbourPtsInOrder(v, triangles, vertices);
        List<Triangle> trianglesNear = new ArrayList<>();
        List<Integer> triangleIndices = v.getTriangleIndices();

        for (int i = 0; i < triangleIndices.size(); i++) {
            int index = triangleIndices.get(i);
            trianglesNear.add(triangles.get(index));
        }

        List<Triangle> trianglesInOrder = new ArrayList<>();
        for (int i = 0; i < verticesNear.size() - 1; i++) {
            Vertex v1 = verticesNear.get(i);
            Vertex v2 = verticesNear.get(i + 1);
            for (Triangle triangle : trianglesNear) {
                if (triangle.containVertices(v1, v2)) {
                    trianglesInOrder.add(triangle);
                    break;
                }
            }
        }
        for (Triangle triangle : trianglesNear) {
            if (triangle.containVertices(verticesNear.get(verticesNear.size() - 1), verticesNear.get(0))) {
                trianglesInOrder.add(triangle);
                break;
            }
        }
        return trianglesInOrder;
    }

    private static List<Vertex> getNeighbourPtsInOrder(Vertex vMain, List<Triangle> triangles, List<Vertex> vertices) {
        List<Integer> trianglesIndexNear = vMain.getTriangleIndices();
        List<Triangle> trianglesNear = new ArrayList<>();
        for (Integer triIndex : trianglesIndexNear) {
            trianglesNear.add(triangles.get(triIndex));
        }
        int iterN = 0;
        int maxN = trianglesNear.size() * vMain.getNumVertices() * 2;
        Vertex vNear = vertices.get(vMain.getVertexIndices().get(0));

        List<Vertex> verticesNear = new ArrayList<>();
        verticesNear.add(vNear);
        for (Triangle triangle : trianglesNear) {
            if (triangle.containVertices(vMain, vNear) && triangle.getRemainInDirection(vMain).get(0).getIndex() == vNear.getIndex()) {
                Vertex vRemain = triangle.getRemainInDirection(vMain).get(1);
                verticesNear.add(vRemain);
                break;
            }
        }

        //for some model with wrong topology, increase the robustness
        if (verticesNear.size() <= 1) {
            for (Triangle triangle : trianglesNear) {
                if (triangle.containVertices(vMain, vNear)) {
                    verticesNear.add(triangle.getRemain(vMain, vNear));
                    break;
                }
            }
        }

        Vertex vOld = verticesNear.get(1);
        while (verticesNear.size() != vMain.getNumVertices()) {
            if (iterN > maxN) {
                break;
            }
            for (Triangle triangle : trianglesNear) {
                if (triangle.containVertices(vMain, vOld)) {
                    Vertex vRemain = triangle.getRemain(vMain, vOld);
                    if (!verticesNear.contains(vRemain)) {
                        verticesNear.add(vRemain);
                        vOld = vRemain;
                    }
                }
                iterN += 1;
            }
            iterN += 1;
        }
        return verticesNear;
    }

    /**
     * Get the guassian curvature for each vertex
     *
     * @param inputModel Completed inputModel
     * @return List of gaussian curvature
     */
    public static List<Double> getGaussianCurvature(InputModel inputModel) {
        List<Vertex> vertices = inputModel.getVertices();
        List<Triangle> triangles = inputModel.getTriangles();
        List<Double> ks = new ArrayList<>();
        for (Vertex v : vertices) {
            //get the neighbouring triangles in order
            List<Triangle> trianglesNear = getNeighbourTriangleInOrder(v, triangles, vertices);

            double angleSum = 0d;
            for (Triangle triangleNear : trianglesNear) {
                List<Vertex> verticesRemain = triangleNear.getRemain(v);
                double angle = Math.abs(MathUtils.getAngle(MathUtils.minusVector(verticesRemain.get(0).getCoords(), v.getCoords()), MathUtils.minusVector(verticesRemain.get(0).getCoords(), v.getCoords())));
                angleSum += Math.toRadians(angle);
            }
            double k = 2 * Math.PI - angleSum;
            ks.add(k);
        }
        return ks;
    }

    /**
     * Get the mean curvature of the model
     *
     * @param inputModel Input model1
     * @return List of the mean curvature
     */
    public static List<Double> getMeanCurvature(InputModel inputModel) {
        List<Vertex> vertices = inputModel.getVertices();
        List<Triangle> triangles = inputModel.getTriangles();
        List<Double> hs = new ArrayList<>();
        for (Vertex v : vertices) {
            //data initialization, both data structures are in the order
            List<Triangle> trianglesNear = getNeighbourTriangleInOrder(v, triangles, vertices);
            //get the pair of the triangles with same edge
            Map<Vertex, List<Triangle>> diTriangles = new HashMap<>();

            for (int i = 0; i < trianglesNear.size() - 1; i++) {
                List<Triangle> trianglePair = new ArrayList<>();
                trianglePair.add(trianglesNear.get(i));
                trianglePair.add(trianglesNear.get(i + 1));
                Vertex vEdge = trianglesNear.get(0).findCommon(trianglesNear.get(i + 1), v);
                diTriangles.put(vEdge, trianglePair);
            }
            List<Triangle> trianglePair = new ArrayList<>();
            trianglePair.add(trianglesNear.get(trianglesNear.size() - 1));
            trianglePair.add(trianglesNear.get(0));
            Vertex vEdge = trianglesNear.get(trianglesNear.size() - 1).findCommon(trianglesNear.get(0), v);
            diTriangles.put(vEdge, trianglePair);

            //start the calculation
            double h = 0;
            for (Vertex vNode : diTriangles.keySet()) {
                List<Triangle> trianglesDi = diTriangles.get(vNode);
                double distance = MathUtils.getMod(MathUtils.minusVector(vNode.getCoords(), v.getCoords()));
                double angle = Math.toRadians(MathUtils.getAngle(trianglesDi.get(0).getUnitNormal(), trianglesDi.get(1).getUnitNormal()));
                h += 1d / 4d * angle * distance;
            }
            hs.add(h);
        }
        return hs;
    }
}
