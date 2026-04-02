# Java Version

Java implementation of the subdivision project for triangle meshes in PLY format.

## Features

- Multiple subdivision schemes:
  Loop, Modified Butterfly, Peter Reif, Square-3, and regional variants
- PLY model reading and writing
- Mesh analysis helpers
- Simple performance comparison across schemes

## Requirements

- Java 8+
- Maven 3.6+

## Build

```bash
mvn clean compile
```

## Run

Using Maven:

```bash
mvn exec:java "-Dexec.args=path/to/model.ply"
```

Using the packaged jar:

```bash
mvn package
java -jar target/Java_version-1.0-SNAPSHOT.jar path/to/model.ply
```

## Project Structure

```text
src/main/java/
  AnalysisStep.java
  ComparisonStep.java
  ComparisonStepSeparate.java
  Constant.java
  Edge.java
  InputModel.java
  LoopScheme.java
  MainEntry.java
  MathUtils.java
  ModifiedButterflyScheme.java
  OutputModel.java
  PeterReifScheme.java
  ReadPLY.java
  RegionalButterfly.java
  RegionalLoop.java
  RegionalSquare3.java
  RemesherStep.java
  Square3Scheme.java
  Triangle.java
  Vector3d.java
  Vertex.java
```

## Main Workflow

`MainEntry` does the following:

1. Reads a PLY mesh
2. Builds an `InputModel`
3. Runs a subdivision scheme
4. Writes the subdivided mesh to a PLY file
5. Runs simple performance comparison code

## Notes

- Output files are generated locally and should not normally be committed.
- If you want to test quickly, pass any small triangular PLY model to `MainEntry`.
