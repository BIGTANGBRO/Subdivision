# 3D Mesh Subdivision Framework

A comprehensive Java-based framework for 3D mesh subdivision algorithms, implementing various subdivision schemes for processing PLY format 3D models.

## Features

- **Multiple Subdivision Schemes**: Implements Butterfly, Loop, Square3 and their regional variants
- **PLY Format Support**: Read and write 3D models in PLY format
- **Geometric Analysis**: Tools for mesh analysis and comparison
- **Remeshing Capabilities**: Tools for mesh quality improvement
- **Performance Testing**: Built-in performance comparison of different schemes

## Implemented Algorithms

- Modified Butterfly Scheme
- Loop Subdivision Scheme
- Peter Reif Scheme
- Square3 Scheme
- Regional Variants (RegionalButterfly, RegionalLoop, RegionalSquare3)

## Requirements

- Java 8 or higher
- Maven 3.6+

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd Java_version
```

2. Build the project:
```bash
mvn clean compile
```

3. Package the project:
```bash
mvn package
```

## Usage

### Running the Application

```bash
java -cp target/Java_version-1.0-SNAPSHOT.jar MainEntry <path_to_ply_file>
```

### Example

```bash
java -cp target/Java_version-1.0-SNAPSHOT.jar MainEntry model.ply
```

## Project Structure

```
src/
├── main/
│   └── java/
│       ├── AnalysisStep.java          # Implementation of subdivision schemes
│       ├── ComparisonStep.java        # Comparison tools for different schemes
│       ├── ComparisonStepSeparate.java # Separate comparison implementation
│       ├── Constant.java              # Mathematical constants
│       ├── Edge.java                  # Edge data structure
│       ├── InputModel.java            # Input model representation
│       ├── LoopScheme.java            # Loop subdivision algorithm
│       ├── MainEntry.java             # Main application entry point
│       ├── MathUtils.java             # Mathematical utilities
│       ├── ModifiedButterflyScheme.java # Modified butterfly algorithm
│       ├── OutputModel.java           # Output model representation
│       ├── PeterReifScheme.java       # Peter Reif algorithm
│       ├── ReadPLY.java               # PLY file reader
│       ├── RegionalButterfly.java     # Regional butterfly algorithm
│       ├── RegionalLoop.java          # Regional loop algorithm
│       ├── RegionalSquare3.java       # Regional square3 algorithm
│       ├── RemesherStep.java          # Mesh remeshing tools
│       ├── Square3Scheme.java         # Square3 algorithm
│       ├── Triangle.java              # Triangle data structure
│       ├── Vector3d.java              # 3D vector implementation
│       └── Vertex.java                # Vertex data structure
└── pom.xml                           # Maven configuration
```

## API Usage Example

```java
// Read a PLY file
PlyReader plyReader = new PlyReaderFile(filePath);
Map<Integer, Vector3d> vertices = new HashMap<>();
Map<Integer, List<Integer>> faces = new HashMap<>();

ReadPLY.read(plyReader, vertices, faces);

// Create input model
InputModel inputModel = new InputModel(vertices, faces);

// Apply subdivision scheme (e.g., Loop scheme)
Map<Integer, Vector3d> outputVertices = new HashMap<>(vertices);
Map<Integer, List<Integer>> outputFaces = new HashMap<>(faces);

AnalysisStep analysisStep = new AnalysisStep(outputVertices, outputFaces);
analysisStep.implementScheme1(inputModel); // Loop scheme

// Create and save output
OutputModel outputModel = new OutputModel(analysisStep.getVertexMap(), analysisStep.getFaceMap());
outputModel.writePLY("output_model");
```

## Extending the Framework

New subdivision schemes can be implemented by extending the base classes and implementing the required methods:

1. Implement `computeOdd()` for computing new edge vertices
2. Implement `computeEven()` for updating existing vertices (if applicable)
3. Implement `createTriangle()` for connecting new vertices into faces

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Based on classic subdivision surface algorithms from computer graphics literature
- Uses the JPLY library for PLY file parsing
- Inspired by geometric modeling research
