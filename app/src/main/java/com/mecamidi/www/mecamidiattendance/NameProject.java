package com.mecamidi.www.mecamidiattendance;

public class NameProject {

    private String name;
    private int id;

    public NameProject(String name,int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}