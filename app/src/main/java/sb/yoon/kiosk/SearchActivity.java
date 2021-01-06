package sb.yoon.kiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.greenrobot.greendao.Property;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
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

    private RecyclerView searchRecyclerView;

    private boolean isTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public void onClickSearchIngredientIcon(View view) {
        ImageView imageView = view.findViewById(R.id.element_image);
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