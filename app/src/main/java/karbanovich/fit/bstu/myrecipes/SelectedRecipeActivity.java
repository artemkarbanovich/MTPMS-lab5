package karbanovich.fit.bstu.myrecipes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.TypedArrayUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

public class SelectedRecipeActivity extends AppCompatActivity implements DialogContexter {
    //view
    private ImageView recipeImage;
    private EditText name;
    private Spinner category;
    private EditText cookingTime;
    private EditText ingredients;
    private EditText recipe;
    private Toast notification;
    private Button saveChanges;
    private Button changeDishImage;

    //data
    private Recipe recipeObj;
    private Uri recipeImageUri = Uri.parse("");
    private String[] dishCategories = {"Категория...", "Завтраки", "Десерты", "Закуски", "Супы", "Салаты", "Выпечка", "Мясное", "Гарнир", "Другое"};
    private Hashtable<String, KeyListener> keyListeners;
    private boolean editingStatus;
    private int dialogActionStatus = 0; //1 - удаление элемента, 2 - отмена изменений


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_recipe);

        binding();
        setData();
        disableInput();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.editItem:
                enableInput();
                return true;
            case R.id.deleteItem:
                dialogActionStatus = 1;
                CustomDialog dialog = new CustomDialog(DialogStatus.DIALOG_DELETE_ITEM);
                Bundle args = new Bundle();
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "custom");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addDishImage(View v) {
        Intent imagePickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        imagePickerIntent.setType("image/*");
        resultRecipeImage.launch(imagePickerIntent);
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

    public void saveRecipe(View v) {
        disableInput();

        ArrayList<Recipe> recipes = JSONHelper.getRecipesJSON(this);
        String name = this.name.getText().toString();
        String category = this.category.getSelectedItem().toString();
        String cookingTime = this.cookingTime.getText().toString();
        String recipeImage = this.recipeImageUri.toString();
        String ingredients = this.ingredients.getText().toString();
        String recipe = this.recipe.getText().toString();

        for(Recipe r: recipes)
            if(r.equals(recipeObj)) {
                r.setName(name);
                r.setCategory(category);
                r.setCookingTime(cookingTime);
                r.setRecipeImage(recipeImage);
                r.setIngredients(ingredients);
                r.setRecipe(recipe);
                break;
            }
        JSONHelper.saveRecipesJSON(this, recipes);

        recipeObj.setName(name);
        recipeObj.setCategory(category);
        recipeObj.setCookingTime(this.cookingTime.getText().toString());
        recipeObj.setRecipeImage(this.recipeImageUri.toString());
        recipeObj.setIngredients(this.ingredients.getText().toString());
        recipeObj.setRecipe(this.recipe.getText().toString());

        notification = Toast.makeText(this, "Рецепт успешно изменен", Toast.LENGTH_LONG);
        notification.show();
    }

    public void fullscreenImage(View v) {
        if(editingStatus || recipeImageUri.toString().equals(""))
            return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(recipeImageUri, "image/*");
        startActivity(intent);
    }

    private void binding() {
        Intent intent = getIntent();
        if(intent.getExtras() != null)
            recipeObj = (Recipe) intent.getSerializableExtra("item");

        recipeImage = (ImageView) findViewById(R.id.recipeImage);
        name = (EditText) findViewById(R.id.name);
        category = (Spinner) findViewById(R.id.category);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dishCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

        cookingTime = (EditText) findViewById(R.id.cookingTime);
        ingredients = (EditText) findViewById(R.id.ingredients);
        recipe = (EditText) findViewById(R.id.recipe);
        saveChanges = (Button) findViewById(R.id.btnSaveChanges);
        changeDishImage = (Button) findViewById(R.id.btnChangeDishImage);

        keyListeners = new Hashtable<>();
        keyListeners.put("name", name.getKeyListener());
        keyListeners.put("cookingTime", cookingTime.getKeyListener());
        keyListeners.put("ingredients", ingredients.getKeyListener());
        keyListeners.put("recipe", recipe.getKeyListener());
    }

    private void setData() {
        if(!recipeObj.getRecipeImage().equals(""))
            recipeImage.setImageURI(Uri.parse(recipeObj.getRecipeImage()));
        else
            recipeImage.setImageResource(R.drawable.dish_icon);

        recipeImageUri = Uri.parse(recipeObj.getRecipeImage());
        name.setText(recipeObj.getName());
        category.setSelection(Arrays.asList(dishCategories).indexOf(recipeObj.getCategory()));
        cookingTime.setText(recipeObj.getCookingTime());
        ingredients.setText(recipeObj.getIngredients());
        recipe.setText(recipeObj.getRecipe());
    }

    private void disableInput() {
        name.setKeyListener(null);
        category.setEnabled(false);
        cookingTime.setKeyListener(null);
        ingredients.setKeyListener(null);
        recipe.setKeyListener(null);
        saveChanges.setVisibility(View.INVISIBLE);
        changeDishImage.setVisibility(View.INVISIBLE);

        editingStatus = false;
    }

    private void enableInput() {
        name.setKeyListener(keyListeners.get("name"));
        category.setEnabled(true);
        cookingTime.setKeyListener(keyListeners.get("cookingTime"));
        ingredients.setKeyListener(keyListeners.get("ingredients"));
        recipe.setKeyListener(keyListeners.get("recipe"));
        saveChanges.setVisibility(View.VISIBLE);
        changeDishImage.setVisibility(View.VISIBLE);

        editingStatus = true;
    }

    @Override
    public void dialogAction() {
        if(dialogActionStatus == 1) {
            ArrayList<Recipe> recipes = JSONHelper.getRecipesJSON(this);

            for (Recipe recipe : recipes)
                if (recipe.equals(recipeObj)) {
                    recipes.remove(recipe);
                    break;
                }
            JSONHelper.saveRecipesJSON(this, recipes);

            notification = Toast.makeText(this, "Рецепт успешно удален", Toast.LENGTH_LONG);
            notification.show();

            Intent intent = new Intent(this, MyRecipesActivity.class);
            startActivity(intent);
            dialogActionStatus = 0;
        }
        if(dialogActionStatus == 2) {
            setData();
            disableInput();
            dialogActionStatus = 0;
        }
    }

    @Override
    public void onBackPressed() {
        if(!editingStatus) {
            Intent intent = new Intent(this, MyRecipesActivity.class);
            startActivity(intent);
            return;
        }

        String name = this.name.getText().toString();
        String category = this.category.getSelectedItem().toString();
        String cookingTime = this.cookingTime.getText().toString();
        String recipeImage = this.recipeImageUri.toString();
        String ingredients = this.ingredients.getText().toString();
        String recipe = this.recipe.getText().toString();

        if(!name.equals(recipeObj.getName()) || !category.equals(recipeObj.getCategory()) ||
                !cookingTime.equals(recipeObj.getCookingTime()) || !recipeImage.equals(recipeObj.getRecipeImage())||
                !ingredients.equals(recipeObj.getIngredients()) || !recipe.equals(recipeObj.getRecipe())) {
            dialogActionStatus = 2;
            CustomDialog dialog = new CustomDialog(DialogStatus.DIALOG_CANCEL_CHANGES);
            Bundle args = new Bundle();
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "custom");
        } else
            disableInput();
    }
}