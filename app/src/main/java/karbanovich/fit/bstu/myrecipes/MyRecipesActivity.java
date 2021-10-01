package karbanovich.fit.bstu.myrecipes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MyRecipesActivity extends AppCompatActivity {

    ListView recipesList;
    ArrayList<Recipe> recipes;
    CustomListAdapter customListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);

        binding();
    }

    private void binding() {
        recipesList = (ListView) findViewById(R.id.recipesList);
        recipes = JSONHelper.getRecipesJSON(this);
        customListAdapter = new CustomListAdapter(this, recipes);
        recipesList.setAdapter(customListAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {return false;}

            @Override
            public boolean onQueryTextChange(String s) {
                customListAdapter.getFilter().filter(s);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.search_view)
            return true;

        return super.onOptionsItemSelected(item);
    }

    public class CustomListAdapter extends BaseAdapter implements Filterable {

        private ArrayList<Recipe> recipes;
        private ArrayList<Recipe> filteredRecipes;
        private Context context;


        public CustomListAdapter(Context context, ArrayList<Recipe> recipes) {
            this.context = context;
            this.recipes = recipes;
            this.filteredRecipes = recipes;
        }

        @Override
        public int getCount() {return filteredRecipes.size();}

        @Override
        public Object getItem(int i) {return null;}

        @Override
        public long getItemId(int i) {return 0;}

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.recipes_item, null);

            ImageView itemImage = (ImageView) view.findViewById(R.id.itemImage);
            TextView itemName = (TextView) view.findViewById(R.id.itemName);
            TextView itemCategory = (TextView) view.findViewById(R.id.itemCategory);
            TextView itemIngredients = (TextView) view.findViewById(R.id.itemIngredients);

            if(!filteredRecipes.get(position).getRecipeImage().equals(""))
                itemImage.setImageURI(Uri.parse(filteredRecipes.get(position).getRecipeImage()));
            itemName.setText(filteredRecipes.get(position).getName());
            itemCategory.setText(filteredRecipes.get(position).getCategory());
            itemIngredients.setText(filteredRecipes.get(position).getIngredients());

            view.setOnClickListener(v -> {
                Intent intent = new Intent(MyRecipesActivity.this, SelectedRecipeActivity.class);
                intent.putExtra("item", filteredRecipes.get(position));
                startActivity(intent);
            });

            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults filterResults = new FilterResults();

                    if(charSequence == null || charSequence.length() == 0) {
                        filterResults.count = recipes.size();
                        filterResults.values = recipes;
                    } else {
                        String searchStr = charSequence.toString().toLowerCase(Locale.ROOT);
                        ArrayList<Recipe> resultData = new ArrayList<>();

                        for(Recipe item : recipes){
                            if(item.getName().toLowerCase(Locale.ROOT).contains(searchStr)
                                    || item.getCategory().toLowerCase(Locale.ROOT).contains(searchStr))
                                resultData.add(item);

                            filterResults.count = resultData.size();
                            filterResults.values = resultData;
                        }
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filteredRecipes = (ArrayList<Recipe>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
            return filter;
        }
    }
}