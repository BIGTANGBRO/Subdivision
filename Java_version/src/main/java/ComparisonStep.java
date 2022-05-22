import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.abs;
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

    public static List<Double> getSphereError1(List<Vertex> vertices) {
        //get the data from vertices;
        List<Double> error = new ArrayList<>();
        for (Vertex v : vertices) {
            final Vector3d coord = v.getCoords();
            final double x = coord.getXVal();
            final double y = coord.getYVal();
            final double zRef = Math.sqrt(Math.abs(getSphereZSqure(x, y)));
            final double z = coord.getZVal();
            final double diff = Math.abs(Math.abs(z) - Math.abs(zRef));
            error.add(diff);
        }
        return error;
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
        final String fileName = "C:\\Users\\tangj\\Downloads\\distribution_hausorff.dat";
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
     * Get the dihedral angle for each edge
     *
     * @param inputModel InputModel class
     * @return The list of dihedral angles in this model
     */
    private static List<Double> computeDihedralAngleEdge(final InputModel inputModel) {
        final List<Triangle> triangles = inputModel.getTriangles();
        final List<Vertex> vertices = inputModel.getVertices();
        List<Edge> edges = inputModel.getEdges();
        final List<Double> dihedralAngles = new ArrayList<>();
        List<Edge> edgesCalculated = new ArrayList<>();

        for (Triangle triangle : triangles) {
            List<Edge> edgesEachTri = triangle.getEdges();
            List<Integer> trianglesEachTri = triangle.getTriangleIndices();
            for (Edge edge : edgesEachTri) {
                if (edgesCalculated.contains(edge)) {
                    continue;
                } else {
                    edgesCalculated.add(edge);
                    for (Integer triIndex : trianglesEachTri) {
                        Triangle triangleEachTri = triangles.get(triIndex);
                        if (triangleEachTri.containVertices(edge.getA(), edge.getB())) {
                            double angle = 180 - MathUtils.getAngle(triangle.getUnitNormal(), triangleEachTri.getUnitNormal());
                            dihedralAngles.add(angle);
                            break;
                        }
                    }
                }
            }
        }
        return dihedralAngles;
    }

    /**
     * Get the dihedral angle for each triangle face in degrees
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
        return dihedralAngles;
    }

    public static void writeAngleEdge(final InputModel inputModel) throws IOException {
        final List<Double> dAngles = computeDihedralAngleEdge(inputModel);
        final String fileName = "C:\\Users\\tangj\\Downloads\\distributionAngleEdge.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (final Double angle : dAngles) {
            bw.write(Double.toString(angle) + "\n");
        }
        bw.close();
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

    /**
     * Get the guassian curvature for each vertex
     *
     * @param inputModel Completed inputModel
     * @return List of gaussian curvature
     */
    public static Map<Integer, Double> getGaussianCurvature(final InputModel inputModel) {
        final List<Vertex> vertices = inputModel.getVertices();
        final List<Triangle> triangles = inputModel.getTriangles();
        Map<Integer, Double> ks = new HashMap<>();
        for (final Vertex v : vertices) {
            final List<Integer> triangleIndices = v.getTriangleIndices();
            final List<Triangle> trianglesNear = new ArrayList<>();
            for (final Integer i : triangleIndices) {
                trianglesNear.add(triangles.get(i));
            }

            double area = 0d;
            for (Triangle triangle : trianglesNear) {
                area += triangle.getArea();
            }

            double angleSum = 0d;
            for (final Triangle triangleNear : trianglesNear) {
                final List<Vertex> verticesRemain = triangleNear.getRemain(v);
                //from degree to radians
                final double angle = Math.toRadians(MathUtils.getAngle(MathUtils.minusVector(verticesRemain.get(0).getCoords(), v.getCoords()), MathUtils.minusVector(verticesRemain.get(1).getCoords(), v.getCoords())));
                angleSum += angle;
            }
            double k = 2 * Math.PI - angleSum;
            k = 3 * k / area;
            ks.put(v.getIndex(), k);
        }
        return ks;
    }

    public static Map<Integer, Double> getMeanCurvature(final InputModel inputModel) {
        final List<Vertex> vertices = inputModel.getVertices();
        final List<Triangle> triangles = inputModel.getTriangles();
        final Map<Integer, Double> hs = new HashMap<>();
        for (final Vertex v : vertices) {
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

            double area = 0d;
            for (Triangle triangle : trianglesNear) {
                area += triangle.getArea();
            }

            //curvature calculation
            double h = 0;
            final List<Integer> verticesNear = v.getVertexIndices();
            for (final Map.Entry<Integer, List<Triangle>> entry : diTriangles.entrySet()) {
                //from degrees to radians, dihedral angle calculation
                if (entry.getValue().size() < 2) {
                    continue;
                }
                final double angle = Math.toRadians(MathUtils.getAngle(entry.getValue().get(0).getUnitNormal(), entry.getValue().get(1).getUnitNormal()));
                final double length = MathUtils.getMod(MathUtils.minusVector(vertices.get(verticesNear.get(entry.getKey())).getCoords(), v.getCoords()));
                h += 1d / 4d * length * angle;
            }
            h = 3d * h / area;
            hs.put(v.getIndex(), h);
        }
        return hs;
    }

    public static Map<Integer, List<Double>> getPrincipalCurvature(InputModel inputModel) {
        final Map<Integer, Double> distribution1 = getGaussianCurvature(inputModel);
        final Map<Integer, Double> distribution2 = getMeanCurvature(inputModel);
        Map<Integer, List<Double>> principalCurvatures = new HashMap<>();
        for (int i = 0; i < inputModel.getVertices().size(); i++) {
            List<Double> pCurvetures = new ArrayList<>();
            double k1 = distribution2.get(i) + Math.sqrt(abs(distribution2.get(i) * distribution2.get(i) - distribution1.get(i)));
            double k2 = distribution2.get(i) - Math.sqrt(abs(distribution2.get(i) * distribution2.get(i) - distribution1.get(i)));
            pCurvetures.add(k1);
            pCurvetures.add(k2);
            principalCurvatures.put(i, pCurvetures);
        }
        return principalCurvatures;
    }

    public static void writeSphereDiff(final InputModel inputModel) throws IOException {
        final List<Double> distributionError1 = getSphereError1(inputModel.getVertices());
        final String fileName = "C:\\Users\\tangj\\Downloads\\distribution_sphere_error.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (final Double distance : distributionError1) {
            bw.write(Double.toString(distance) + "\n");
        }
        bw.close();
    }

    public static void writeCurvatureGaussian(final InputModel inputModel) throws IOException {
        final Map<Integer, Double> distribution1 = getGaussianCurvature(inputModel);
        final String fileName = "C:\\Users\\tangj\\Downloads\\distribution_curvature_gaussian.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (int i = 0; i < inputModel.getVertices().size(); i++) {
            bw.write(Double.toString(distribution1.get(i)) + "\n");
        }
        bw.close();
    }

    public static void writeCurvatureMean(final InputModel inputModel) throws IOException {
        final Map<Integer, Double> distribution1 = getMeanCurvature(inputModel);
        final String fileName = "C:\\Users\\tangj\\Downloads\\distribution_curvature_mean.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        //for (final Double distance : distribution1.values()) {
        //    bw.write(Double.toString(distance) + "\n");
        //}
        for (int i = 0; i < inputModel.getVertices().size(); i++) {
            bw.write(Double.toString(distribution1.get(i)) + "\n");
        }
        bw.close();
    }

    public static void writeCurvaturePrincipal(InputModel inputModel) throws IOException {
        final Map<Integer, List<Double>> distribution = getPrincipalCurvature(inputModel);
        final String fileName = "C:\\Users\\tangj\\Downloads\\distribution_curvature_principal.dat";
        final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (int i = 0; i < inputModel.getVertices().size(); i++) {
            double k1 = distribution.get(i).get(0);
            double k2 = distribution.get(i).get(1);
            bw.write(Double.toString(k1) + " " + Double.toString(k2) + "\n");
        }
        bw.close();
    }
}
