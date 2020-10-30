package sb.yoon.kiosk.controller;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.InputStream;
import java.nio.file.Paths;

import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.CategoriesDao;
import sb.yoon.kiosk.model.DaoSession;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.IngredientDao;
import sb.yoon.kiosk.model.IngredientsAndMenuJoiner;
import sb.yoon.kiosk.model.IngredientsAndMenuJoinerDao;
import sb.yoon.kiosk.model.Menu;
import sb.yoon.kiosk.model.MenuDao;
import sb.yoon.kiosk.tools.Decompress;

public class Init {
    private CategoriesDao categoriesDao;
    private MenuDao menuDao;
    private IngredientDao ingredientDao;
    private IngredientsAndMenuJoinerDao ingredientsAndMenuJoinerDao;

    private String menusPath;
    private String ingredientsPath;

    // @todo 만약 세팅되있지 않은 경우 sqlite에 기본 카테고리, 메뉴, 재료 등 삽입 수행
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Init(DaoSession daoSession, InputStream zipFileStream, String unzipPath) {

        (new Decompress(zipFileStream, unzipPath)).unzip();
        menusPath = Paths.get(unzipPath, "menupan", "menus").toString();
        ingredientsPath = Paths.get(unzipPath, "menupan", "ingredients").toString();

        categoriesDao = daoSession.getCategoriesDao();
        menuDao = daoSession.getMenuDao();
        ingredientDao = daoSession.getIngredientDao();
        ingredientsAndMenuJoinerDao = daoSession.getIngredientsAndMenuJoinerDao();

        try {
            initCategories();
            initMenus();
            initIngredients();
            initIngredAndMenuJoiners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCategories() {
        // 1L = new Long(1)
        categoriesDao.insertOrReplace(new Category(1L, "커피"));
        categoriesDao.insertOrReplace(new Category(2L, "에이드"));
        categoriesDao.insertOrReplace(new Category(3L, "주스"));
        categoriesDao.insertOrReplace(new Category(4L, "디저트"));
    }

    private void initMenus() {
        menuDao.insertOrReplace(new Menu(1L, "아메리카노", 1L, 1100, menusPath + "/americano.png", true));
    }

    private void initIngredients() {
        ingredientDao.insertOrReplace(new Ingredient(1L, "커피콩", ingredientsPath + "/coffee.png"));
    }

    private void initIngredAndMenuJoiners() {
        ingredientsAndMenuJoinerDao.insert(new IngredientsAndMenuJoiner(null, 1L, 1L));
    }
}
