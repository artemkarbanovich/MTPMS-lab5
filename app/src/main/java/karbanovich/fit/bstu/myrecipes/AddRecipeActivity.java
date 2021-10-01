package karbanovich.fit.bstu.myrecipes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity implements DialogContexter {

    private ImageView recipeImage;
    private Uri recipeImageUri = Uri.parse("");
    private EditText name;
    private Spinner category;
    private EditText cookingTime;
    private EditText ingredients;
    private EditText recipe;
    private Toast notification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        binding();
    }

    public void addDishImage(View v) {
        Intent imagePickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        imagePickerIntent.setType("image/*");
        resultRecipeImage.launch(imagePickerIntent);
    }

    public void addRecipe(View v) {
        String name = this.name.getText().toString();
        String category = this.category.getSelectedItem().toString();
        String cookingTime = this.cookingTime.getText().toString();
        String recipeImage = this.recipeImageUri.toString();
        String ingredients = this.ingredients.getText().toString();
        String recipe = this.recipe.getText().toString();

        try {
            Recipe recipeObj = new Recipe(name, category, cookingTime, recipeImage, ingredients, recipe);
            ArrayList<Recipe> recipes = JSONHelper.getRecipesJSON(this);

            recipes.add(recipeObj);
            JSONHelper.saveRecipesJSON(this, recipes);

            notification = Toast.makeText(this, "Рецепт успешно добавлен", Toast.LENGTH_LONG);
        } catch (Exception e ) {
            notification = Toast.makeText(this, "Ошибка добавления",Toast.LENGTH_LONG);
        } finally {
            notification.show();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        String name = this.name.getText().toString();
        String category = this.category.getSelectedItem().toString();
        String cookingTime = this.cookingTime.getText().toString();
        String recipeImage = this.recipeImageUri.toString();
        String ingredients = this.ingredients.getText().toString();
        String recipe = this.recipe.getText().toString();

        if(!name.equals("") || !category.equals("Категория...") || !cookingTime.equals("")
                || !recipeImage.equals("") || !ingredients.equals("") || !recipe.equals("")) {
            CustomDialog dialog = new CustomDialog(DialogStatus.DIALOG_BACK_BUTTON);
            Bundle args = new Bundle();
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "custom");
        } else
            dialogAction();
    }

    @Override
    public void dialogAction() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void binding() {
        recipeImage = (ImageView) findViewById(R.id.recipeImage);
        name = (EditText) findViewById(R.id.name);
        category = (Spinner) findViewById(R.id.category);

        String[] dishCategories = {"Категория...", "Завтраки", "Десерты", "Закуски", "Супы", "Салаты", "Выпечка", "Мясное", "Гарнир", "Другое"};
        ArrayAdapter<String>  adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dishCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

        cookingTime = (EditText) findViewById(R.id.cookingTime);
        ingredients = (EditText) findViewById(R.id.ingredients);
        recipe = (EditText) findViewById(R.id.recipe);
    }

    private ActivityResultLauncher<Intent> resultRecipeImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                recipeImageUri = result.getData().getData();
                recipeImage.setImageURI(recipeImageUri);
            }
        }
    });
}