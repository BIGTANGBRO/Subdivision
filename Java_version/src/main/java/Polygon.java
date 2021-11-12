/*
 * Polygon.java
 * Copyright 2021 Qunhe Tech, all rights reserved.
 * Qunhe PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangshao
 */
@Getter
@Setter
public class Polygon {
    //data structure for the polygon
    protected int index;
    protected List<Vertex> vertices;
    protected List<Polygon> neighbours;
    protected List<Edge> edges;

    Polygon() {
        this.vertices = new ArrayList<>();
        this.neighbours = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    Polygon(final int index, final int numOfNeighbours, final int numOfVertices) {
        this.index = index;
        this.vertices = new ArrayList<>(numOfVertices);
        this.edges = new ArrayList<>(numOfNeighbours);
        this.neighbours = new ArrayList<>(numOfNeighbours);
    }

    public void addVertices(final Vertex newVertex) {
        vertices.add(newVertex);
    }

    public void addNeighbours(final Polygon newPolygon, final Edge edge) {
        this.neighbours.add(newPolygon);
        this.edges.add(edge);
    }
}
