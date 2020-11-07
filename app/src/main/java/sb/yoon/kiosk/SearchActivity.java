package sb.yoon.kiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import org.greenrobot.greendao.Property;

import java.util.List;

import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.controller.SearchRecyclerViewAdapter;
import sb.yoon.kiosk.layout.SearchItemDecoration;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.IngredientDao;

public class SearchActivity extends AppCompatActivity {
    private List<Ingredient> ingredientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView searchRecyclerView = this.findViewById(R.id.search_ingredients_list);
        GridLayoutManager mLinearLayoutManager = new GridLayoutManager(this, 4);
        searchRecyclerView.setLayoutManager(mLinearLayoutManager);
        searchRecyclerView.addItemDecoration(new SearchItemDecoration(this, 4));

        KioskApplication kioskApplication = (KioskApplication) getApplication();
        DbQueryController dbQueryController = new DbQueryController(kioskApplication.getDaoSession());
        IngredientDao ingredientDao = dbQueryController.getIngredientDao();
        ingredientList = ingredientDao.queryBuilder().orderAsc(IngredientDao.Properties.Name).list();
        Log.d("재료 리스트:", ingredientList.toString());
        searchRecyclerView.setAdapter(new SearchRecyclerViewAdapter(ingredientList));
    }
}