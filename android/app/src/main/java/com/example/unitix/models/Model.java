package com.example.unitix.models;

public abstract class Model {

    String id;
    boolean isValid;




    public String getId() {
        return this.id;
    }

    public boolean isValid() {
        return this.isValid;
    }

}
