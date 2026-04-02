# Subdivision

A small mesh subdivision project with both Java and Python implementations for experimenting with triangle-mesh subdivision schemes and PLY model processing.

## Repository Layout

- `Java_version/`: Java implementation built with Maven
- `Python_ver/`: Python version of the project

## Java Version

The Java project currently includes:

- Loop subdivision
- Modified Butterfly subdivision
- Peter Reif subdivision
- Square-3 subdivision
- Regional variants of several schemes
- PLY input/output utilities
- Simple mesh analysis and performance comparison helpers

### Quick Start

```bash
git clone https://github.com/BIGTANGBRO/Subdivision.git
cd Subdivision/Java_version
mvn clean compile
```

Run the main entry with a PLY file:

```bash
mvn exec:java "-Dexec.args=path/to/model.ply"
```

Or package a runnable jar:

```bash
mvn package
java -jar target/Java_version-1.0-SNAPSHOT.jar path/to/model.ply
```

## Notes

- Input models are expected to be in PLY format.
- Generated files such as build output and local test models are ignored by Git.
- The Java code has been verified to compile and run through the main workflow on a sample PLY model.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).
