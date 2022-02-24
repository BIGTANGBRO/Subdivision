import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: tangshao
 * @Date: 24/02/2022
 */
public class ComparisonStepSeparate {
    private static List<Double> getMinDistanceDistributionExtraordinary(final List<Vertex> vertices1, final List<Vertex> vertices2) {
        final List<Double> vertexDistances = new ArrayList<>(vertices1.size());
        for (final Vertex vertex1 : vertices1) {
            if (!vertex1.isRegular()) {
                double minDist = Double.POSITIVE_INFINITY;
                for (final Vertex vertex2 : vertices2) {
                    final double distance = MathUtils.minusVector(vertex1.getCoords(), vertex2.getCoords()).getMod();
                    if (distance <= minDist) {
                        minDist = distance;
                    }
                }
                vertexDistances.add(minDist);
            }
        }
        return vertexDistances;
    }

    /**
     * Output the data to plot the histogram
     *
     * @param vertices1 vertices from model1
     * @param vertices2 vertices from model2
     * @throws IOException Ioexception
     */
    public static void writeHausorffDistribution(final List<Vertex> vertices1, final List<Vertex> vertices2) throws IOException {
        final List<Double> distribution1 = getMinDistanceDistributionExtraordinary(vertices1, vertices2);
        final String fileName = "C:\\Users\\tangj\\Downloads\\distribution_hausorff_extraordinary.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (final Double distance : distribution1) {
            bw.write(Double.toString(distance) + "\n");
        }
        bw.close();
    }

    /**
     * Get the dihedral angle for each triangle face in degrees
     *
     * @param inputModel InputModel class
     * @return The list of dihedral angles in this model
     */
    private static List<Double> computeDihedralAngleExtraordinary(final InputModel inputModel) {
        final List<Triangle> triangles = inputModel.getTriangles();
        final List<Vertex> vertices = inputModel.getVertices();
        final List<Double> dihedralAngles = new ArrayList<>();
        for (final Triangle triangle : triangles) {
            if (triangle.isNearExtraordinary()) {
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
                        final double angle = 180d - MathUtils.getAngle(trianglesAnEdge.get(0), trianglesAnEdge.get(1));
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
        }
        return dihedralAngles;
    }

    public static void writeAngle(final InputModel inputModel) throws IOException {
        final List<Double> dAngles = computeDihedralAngleExtraordinary(inputModel);
        final String fileName = "C:\\Users\\tangj\\Downloads\\distributionAngleExtraordinary.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (final Double angle : dAngles) {
            bw.write(Double.toString(angle) + "\n");
        }
        bw.close();
    }

    /**
     * Get the guassian curvature for each vertex
     *
     * @param inputModel Completed inputModel
     * @return List of gaussian curvature
     */
    public static List<Double> getGaussianCurvatureExtraordinary(final InputModel inputModel) {
        final List<Vertex> vertices = inputModel.getVertices();
        final List<Triangle> triangles = inputModel.getTriangles();
        final List<Double> ks = new ArrayList<>();
        for (final Vertex v : vertices) {
            if (!v.isRegular()) {
                final List<Integer> triangleIndices = v.getTriangleIndices();
                final List<Triangle> trianglesNear = new ArrayList<>();
                for (final Integer i : triangleIndices) {
                    trianglesNear.add(triangles.get(i));
                }

                double angleSum = 0d;
                for (final Triangle triangleNear : trianglesNear) {
                    final List<Vertex> verticesRemain = triangleNear.getRemain(v);
                    //from degree to radians
                    final double angle = Math.toRadians(MathUtils.getAngle(MathUtils.minusVector(verticesRemain.get(0).getCoords(), v.getCoords()), MathUtils.minusVector(verticesRemain.get(1).getCoords(), v.getCoords())));
                    angleSum += angle;
                }
                final double k = 2 * Math.PI - angleSum;
                ks.add(k);
            }
        }
        return ks;
    }

    public static List<Double> getMeanCurvatureExtraordinary(final InputModel inputModel) {
        final List<Vertex> vertices = inputModel.getVertices();
        final List<Triangle> triangles = inputModel.getTriangles();
        final List<Double> hs = new ArrayList<>();
        for (final Vertex v : vertices) {
            if (!v.isRegular()) {
                //data initialization
                final List<Integer> triangleIndices = v.getTriangleIndices();
                final List<Triangle> trianglesNear = new ArrayList<>();
                for (final Integer i : triangleIndices) {
                    trianglesNear.add(triangles.get(i));
                }

                //get the pair of the triangles with same edge
                final Map<Integer, List<Triangle>> diTriangles = new HashMap<>();
                int index = 0;
                for (final Integer vertexNear : v.getVertexIndices()) {
                    final List<Triangle> trianglePair = new ArrayList<>();
                    final Vertex vNear = vertices.get(vertexNear);
                    for (final Triangle triangleNear : trianglesNear) {
                        if (triangleNear.containVertices(v, vNear)) {
                            trianglePair.add(triangleNear);
                        }
                    }
                    diTriangles.put(index, trianglePair);
                    index += 1;
                }

                //curvature calculation
                double h = 0;
                final List<Integer> verticesNear = v.getVertexIndices();
                for (final Map.Entry<Integer, List<Triangle>> entry : diTriangles.entrySet()) {
                    //from degrees to radians, dihedral angle calculation
                    final double angle = Math.PI - Math.toRadians(MathUtils.getAngle(entry.getValue().get(0).getUnitNormal(), entry.getValue().get(1).getUnitNormal()));
                    final double length = MathUtils.getMod(MathUtils.minusVector(vertices.get(verticesNear.get(entry.getKey())).getCoords(), v.getCoords()));
                    h += 1d / 4d * length * angle;
                }
                hs.add(h);
            }
        }
        return hs;
    }

    public static void writeCurvature1(final InputModel inputModel) throws IOException {
        final List<Double> distribution1 = getGaussianCurvatureExtraordinary(inputModel);
        final String fileName = "C:\\Users\\tangj\\Downloads\\distribution_curvature_gaussian_extraordinary.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (final Double distance : distribution1) {
            bw.write(Double.toString(distance) + "\n");
        }
        bw.close();
    }

    public static void writeCurvature2(final InputModel inputModel) throws IOException {
        final List<Double> distribution1 = getMeanCurvatureExtraordinary(inputModel);
        final String fileName = "C:\\Users\\tangj\\Downloads\\distribution_curvature_mean_extraordinary.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (final Double distance : distribution1) {
            bw.write(Double.toString(distance) + "\n");
        }
        bw.close();
    }
}
