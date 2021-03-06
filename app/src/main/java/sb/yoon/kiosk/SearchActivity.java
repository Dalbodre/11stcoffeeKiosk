package sb.yoon.kiosk;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.controller.SearchRecyclerViewAdapter;
import sb.yoon.kiosk.layout.SearchItemDecoration;
import sb.yoon.kiosk.libs.IdleTimer;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.IngredientDao;
import sb.yoon.kiosk.model.Menu;

public class SearchActivity extends Activity {
    private List<Ingredient> ingredientList;
    private List<Menu> searchedMenuList;

    private RecyclerView searchRecyclerView;

    private IdleTimer idleTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);

        searchRecyclerView = this.findViewById(R.id.search_ingredients_list);
        GridLayoutManager mLinearLayoutManager = new GridLayoutManager(this, 5);
        searchRecyclerView.setLayoutManager(mLinearLayoutManager);
        searchRecyclerView.addItemDecoration(new SearchItemDecoration(this, 5));

        KioskApplication kioskApplication = (KioskApplication) getApplication();
        DbQueryController dbQueryController = kioskApplication.getDbQueryController();
        IngredientDao ingredientDao = dbQueryController.getIngredientDao();
        ingredientList = ingredientDao.queryBuilder().orderAsc(IngredientDao.Properties.Name).list();
        searchRecyclerView.setAdapter(new SearchRecyclerViewAdapter(ingredientList));

        Button button = findViewById(R.id.search_return_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        idleTimer = new IdleTimer(this, 150000, 1000);
        idleTimer.start();
    }

    @Override
    public void onUserInteraction() {
        idleTimer.cancel();
        idleTimer.start();
        super.onUserInteraction();
    }

    @Override
    protected void onPause() {
        super.onPause();
        idleTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        idleTimer.start();
    }

    public void onClickSearchIngredientIcon(View view) {
        ImageView imageView = view.findViewById(R.id.search_image);
        Ingredient ingredient = (Ingredient) imageView.getTag();
        searchedMenuList = ingredient.getMenuList();
        ArrayList<Integer> searchedMenuArrayList = new ArrayList<>();
        for (Menu menu : searchedMenuList) {
            long id = menu.getId();
            searchedMenuArrayList.add((int) id);
        }

        Intent intent = new Intent();
        intent.putIntegerArrayListExtra("searchedMenuIdList", searchedMenuArrayList);
        setResult(RESULT_OK, intent);
        finish();
    }
}