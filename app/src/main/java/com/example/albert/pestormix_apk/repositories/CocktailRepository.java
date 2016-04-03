package com.example.albert.pestormix_apk.repositories;

import android.content.Intent;
import android.util.Log;

import com.example.albert.pestormix_apk.R;
import com.example.albert.pestormix_apk.application.PestormixApplication;
import com.example.albert.pestormix_apk.controllers.DataController;
import com.example.albert.pestormix_apk.exceptions.CocktailFormatException;
import com.example.albert.pestormix_apk.models.Cocktail;
import com.example.albert.pestormix_apk.models.Drink;
import com.example.albert.pestormix_apk.utils.Constants;
import com.example.albert.pestormix_apk.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Albert on 27/01/2016.
 */
public abstract class CocktailRepository {

    private static final String TAG = "CocktailRepository";

    public static List<Cocktail> init(Realm realm) {
        List<Cocktail> cocktails = new ArrayList<>();
        generateCocktail(realm, cocktails, Utils.getStringResource(R.string.cocktail_water),
                Utils.getStringResource(R.string.cocktail_water_description), Utils.getStringResource(R.string.cocktail_water));
        generateCocktail(realm, cocktails, Utils.getStringResource(R.string.cocktail_coca_cola),
                Utils.getStringResource(R.string.cocktail_coca_cola_description),
                Utils.getStringResource(R.string.drink_coca_cola));
        generateCocktail(realm, cocktails, Utils.getStringResource(R.string.cocktail_lemonade),
                Utils.getStringResource(R.string.cocktail_lemonade_description),
                Utils.getStringResource(R.string.drink_lemonade));
        generateCocktail(realm, cocktails, Utils.getStringResource(R.string.cocktail_orangeade),
                Utils.getStringResource(R.string.cocktail_orangeade_description),
                Utils.getStringResource(R.string.drink_orangeade));
        generateCocktail(realm, cocktails, Utils.getStringResource(R.string.cocktail_cuba_libre),
                Utils.getStringResource(R.string.cocktail_cuba_libre_description),
                Utils.getStringResource(R.string.drink_coca_cola) + "," + Utils.getStringResource(R.string.drink_ron));
        return cocktails;
    }

    private static void generateCocktail(Realm realm, List<Cocktail> cocktails, String name, String description, String drinks) {
        Cocktail cocktail = new Cocktail();
        cocktail.setName(name);
        cocktail.setDescription(description);
        List<Drink> drinksFromString = DrinkRepository.getDrinksFromString(realm, drinks);
        for (Drink drink : drinksFromString) {
            addDrink(cocktail, drink);
        }
        cocktails.add(cocktail);
    }

    public static void generateCocktails(Realm realm) {
        DataController.generateCocktails(realm);
    }

    public static void addDrink(Cocktail cocktail, Drink drink) {
        cocktail.getDrinks().add(drink);
    }

    public static String getDrinksAsString(Cocktail cocktail) {
        return getDrinksAsString(cocktail, ",");
    }

    public static Cocktail processData(String data) {
        if (data != null && !data.equals("")) {
            Cocktail cocktail;
            try {
                Log.d(TAG, data);
                cocktail = CocktailRepository.getCocktailFromString(Utils.getRealm(), data);
            } catch (CocktailFormatException e) {
                Utils.showToast(Utils.getStringResource(R.string.cocktail_format_error));
                return null;
            }
            if (CocktailRepository.cocktailExist(Utils.getRealm(), cocktail)) {
                Utils.showToast(Utils.getStringResource(R.string.cocktail_name_already_exist));
            } else {
                return cocktail;
            }
        }
        return null;
    }

    public static Cocktail getCocktailFromString(Realm realm, String data) throws CocktailFormatException {
        String[] split = data.split(",", 4);
        if (split.length < 4 || !split[0].equals("Pestormix")) {
            throw new CocktailFormatException();
        }
        Cocktail cocktail = new Cocktail();
        cocktail.setName(split[1]);
        cocktail.setDescription(split[2]);
        setDrinksFromString(realm, cocktail, split[3]);
        return cocktail;
    }

    public static String getDrinksAsString(Cocktail cocktail, String separator) {
        String drinks = "";
        for (Drink drink : cocktail.getDrinks()) drinks += drink.getName() + separator;
        drinks = drinks.substring(0, drinks.length() - separator.length()); //Delete the last ","
        return drinks;
    }

    public static List<Drink> getDrinks(Cocktail cocktail) {
        RealmList<Drink> results = cocktail.getDrinks();
        List<Drink> drinks = new ArrayList<>();
        for (Drink drink : results) drinks.add(drink);
        return drinks;
    }

    public static void setDrinksFromString(Realm realm, Cocktail cocktail, String drinksString) {
        List<Drink> drinks = DrinkRepository.getDrinksFromString(realm, drinksString);
        for (Drink drink : drinks) {
            if (!cocktail.isAlcohol() && drink.isAlcohol()) cocktail.setAlcohol(true);
            addDrink(cocktail, drink);
        }
    }

    public static void addCocktailToDB(Realm realm, Cocktail cocktail) {
        addCocktailToDB(realm, cocktail, true);
    }

    public static void addCocktailToDB(Realm realm, Cocktail cocktail, boolean push) {
        DataController.addCocktail(realm, cocktail);
        if (push) pushCocktailsToRemote();
    }

    public static Cocktail getCocktailByName(Realm realm, String cocktailName) {
        return DataController.getCocktailByName(realm, cocktailName);
    }

    public static List<String> getCocktailsNames(Realm realm) {
        return DataController.getCocktailsNames(realm);
    }

    public static List<Cocktail> getCocktails(Realm realm) {
        return DataController.getCocktails(realm);
    }

    public static boolean cocktailExist(Realm realm, Cocktail cocktail) {
        return DataController.cocktailExist(realm, cocktail);
    }

    public static void updateCocktail(Realm realm, Cocktail cocktail, String oldName) {

        if (oldName != null) {
            DataController.updateCocktail(realm, cocktail, oldName);
            pushCocktailsToRemote();
        } else {
            DataController.updateCocktail(realm, cocktail);
        }
    }

    public static void updateCocktails(Realm realm, List<Cocktail> cocktails) {
        for (Cocktail cocktail : cocktails) {
            updateCocktail(realm, cocktail, null);
        }
    }

    public static void removeCocktailByName(Realm realm, String cocktailName) {
        removeCocktailByName(realm, cocktailName, true);
    }

    public static void removeCocktailByName(Realm realm, String cocktailName, boolean push) {
        DataController.removeCocktailByName(realm, cocktailName);
        if (push) pushCocktailsToRemote();
    }

    public static void removeAllCocktails(Realm realm) {
        DataController.removeAllCocktails(realm);
    }

    public static void updateCocktails() {
        Intent intent = new Intent(Constants.ACTION_START_SYNC_WITH_REMOTE);
        PestormixApplication.getContext().sendBroadcast(intent);
    }

    public static void pushCocktailsToRemote() {
        Intent intent = new Intent(Constants.ACTION_START_SYNC_TO_REMOTE);
        PestormixApplication.getContext().sendBroadcast(intent);
    }


}
