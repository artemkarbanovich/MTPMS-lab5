package karbanovich.fit.bstu.myrecipes;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String name;
    private String category;
    private String cookingTime;
    private String recipeImage;
    private String ingredients;
    private String recipe;


    public Recipe(String name, String category, String cookingTime, String recipeImage, String ingredients, String recipe) {
        this.name = name;
        this.category = category;
        this.cookingTime = cookingTime;
        this.recipeImage = recipeImage;
        this.ingredients = ingredients;
        this.recipe = recipe;
    }

    public String getName() {return name;}
    public String getCategory() {return category;}
    public String getCookingTime() {return cookingTime;}
    public String getRecipeImage() {return recipeImage;}
    public String getIngredients() {return ingredients;}
    public String getRecipe() {return recipe;}

    public void setName(String name) {this.name = name;}
    public void setCategory(String category) {this.category = category;}
    public void setCookingTime(String cookingTime) {this.cookingTime = cookingTime;}
    public void setRecipeImage(String recipeImage) {this.recipeImage = recipeImage;}
    public void setIngredients(String ingredients) {this.ingredients = ingredients;}
    public void setRecipe(String recipe) {this.recipe = recipe;}

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Recipe recipe = (Recipe) o;
        return this.name.equals(recipe.name) &&
                this.recipeImage.equals(recipe.recipeImage) &&
                this.category.equals(recipe.category) &&
                this.cookingTime.equals(recipe.cookingTime) &&
                this.ingredients.equals(recipe.ingredients) &&
                this.recipe.equals(recipe.recipe);
    }
}
