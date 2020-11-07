package sb.yoon.kiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.greenrobot.greendao.Property;

import java.util.List;

import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.controller.SearchRecyclerViewAdapter;
import sb.yoon.kiosk.layout.SearchItemDecoration;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.IngredientDao;
import sb.yoon.kiosk.model.Menu;

public class SearchActivity extends AppCompatActivity {
    private List<Ingredient> ingredientList;
    private List<Menu> searchedMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView searchRecyclerView = this.findViewById(R.id.search_ingredients_list);
        GridLayoutManager mLinearLayoutManager = new GridLayoutManager(this, 4);
        searchRecyclerView.setLayoutManager(mLinearLayoutManager);
        searchRecyclerView.addItemDecoration(new SearchItemDecoration(this, 4));

        KioskApplication kioskApplication = (KioskApplication) getApplication();
        DbQueryController dbQueryController = kioskApplication.getDbQueryController();
        IngredientDao ingredientDao = dbQueryController.getIngredientDao();
        ingredientList = ingredientDao.queryBuilder().orderAsc(IngredientDao.Properties.Name).list();
        searchRecyclerView.setAdapter(new SearchRecyclerViewAdapter(ingredientList));
    }

    public static class OnClickSearchIngredients implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Ingredient ingredient = (Ingredient) view.getTag();
            ingredient.getMenuList();
        }
    }
}