package com.example.Albert.pestormix_apk.backend;

/**
 * Created by Albert on 29/05/2016.
 */
public class ValveBean {
    private String id;
    private String drinkName;
    private boolean drinkAlcohol;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public boolean isDrinkAlcohol() {
        return drinkAlcohol;
    }

    public void setDrinkAlcohol(boolean drinkAlcohol) {
        this.drinkAlcohol = drinkAlcohol;
    }
}
