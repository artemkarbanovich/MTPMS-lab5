package karbanovich.fit.bstu.myrecipes;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;


public class JSONHelper {
    private static final String FILE_NAME = "recipes.json";


    public static ArrayList<Recipe> getRecipesJSON(Context context) {
        if(!isExist(context))
            return new ArrayList<>();

        Gson gson = new Gson();
        File file = new File(context.getFilesDir(), FILE_NAME);

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);

            DataItems recipes = gson.fromJson(isr, DataItems.class);

            fis.close();
            isr.close();
            return recipes.getRecipes();
        } catch (Exception e) { }

        return new ArrayList<>();
    }

    public static void saveRecipesJSON(Context context, ArrayList<Recipe> recipes) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        Gson gson = new Gson();
        DataItems dataItems = new DataItems();

        dataItems.setRecipes(recipes);
        String jsonStr = gson.toJson(dataItems);

        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(jsonStr.getBytes());
            fos.close();
        } catch (Exception e) { }
    }

    private static boolean isExist(Context context) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        return file.exists();
    }

    private static class DataItems {
        private ArrayList<Recipe> recipes;

        public ArrayList<Recipe> getRecipes() {return recipes;}
        public void setRecipes(ArrayList<Recipe> recipes) {this.recipes = recipes;}
    }
}
