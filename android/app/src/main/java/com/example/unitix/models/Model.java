package com.example.unitix.models;

public abstract class Model {

    String id;
    boolean isValid;

    static final String[] MONTHS = {"January", "Febraury", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};


    public String getId() {
        return this.id;
    }

    public boolean isValid() {
        return this.isValid;
    }

    String getMonth(int month) {
        return MONTHS[month - 1];
    }

}
