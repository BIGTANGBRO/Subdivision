import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 边类
 * @author tangshao
 */
@Getter
@Setter
public class Edge {
    private int index;
    private Vertex a;
    private Vertex b;

    public Edge() {
    }

    public Edge(final Vertex a, final Vertex b, final int index) {
        this.a = a;
        this.b = b;
        this.index = index;
    }

    public boolean has(final Vertex v) {
        return v.getIndex() == a.getIndex() || v.getIndex() == b.getIndex();
    }

    public List<Vertex> getVertices() {
        final List<Vertex> vertices = new ArrayList<>();
        vertices.add(a);
        vertices.add(b);
        return vertices;
    }

    public Vertex getOtherVertex(final Vertex v) {
        if (!has(v)) {
            return null; // 如果v不是这条边的端点，则返回null
        }
        return a != v ? a : b;
    }

    public double getLength() {
        return MathUtils.getMod(MathUtils.minusVector(this.a.getCoords(), this.b.getCoords()));
    }

    @Override
    public int hashCode() {
        // 使用较小索引的顶点作为第一个元素来确保顺序无关性
        int minIndex = Math.min(a.getIndex(), b.getIndex());
        int maxIndex = Math.max(a.getIndex(), b.getIndex());
        return minIndex * 31 + maxIndex;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        final Edge edge = (Edge) obj;
        return (this.a.getIndex() == edge.a.getIndex() && this.b.getIndex() == edge.b.getIndex()) || 
               (this.b.getIndex() == edge.a.getIndex() && this.a.getIndex() == edge.b.getIndex());
    }
    
    @Override
    public String toString() {
        return String.format("Edge{index=%d, vertexA=%d, vertexB=%d}", index, a.getIndex(), b.getIndex());
    }
}