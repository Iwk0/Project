package com.tictactoe.model;

import java.util.List;

/**
 * Created by imishev on 25.9.2014 г..
 */
public class Image {

    private int id;
    private List<String> paths;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}