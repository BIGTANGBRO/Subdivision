/*
 * Vertex.java
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
public class Vertex {
    //this is a vertex class
    private int index;
    private Vector3d coords;
    private List<Polygon> polygons;
    //neighbour vertices
    private List<Vertex> vertices;

    //In triangular mesh, a vertex which is not connected 6 neightbours is extradinary
    Vertex() {

    }

    //Constructor for the vertex
    Vertex(final int index, final double[] coordinates, final int nNeighbours) {
        this.index = index;
        this.coords.setXVal(coordinates[0]);
        this.coords.setYVal(coordinates[1]);
        this.coords.setZVal(coordinates[2]);
        this.polygons = new ArrayList<>(nNeighbours);
        this.vertices = new ArrayList<>(nNeighbours);
    }

    public void setX(final double x) {
        this.coords.setXVal(x);
    }

    public void setY(final double y) {
        this.coords.setYVal(y);
    }

    public void setZ(final double z) {
        this.coords.setZVal(z);
    }

    public int getNumNeighbours() {
        return this.polygons.size();
    }

    public boolean isExtraordinary() {
        return this.getNumNeighbours() != 6;
    }
}
