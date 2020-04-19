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

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof Model) {
            return this.id.equals(((Model) o).id);
        }
        return false;
    }

}
