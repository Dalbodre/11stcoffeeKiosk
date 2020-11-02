package sb.yoon.kiosk.controller;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.InputStream;

import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.CategoryDao;
import sb.yoon.kiosk.model.DaoSession;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.IngredientDao;
import sb.yoon.kiosk.model.IngredientsAndMenuJoiner;
import sb.yoon.kiosk.model.IngredientsAndMenuJoinerDao;
import sb.yoon.kiosk.model.Menu;
import sb.yoon.kiosk.model.MenuDao;

public class Init {
    private CategoryDao categoryDao;
    private MenuDao menuDao;
    private IngredientDao ingredientDao;
    private IngredientsAndMenuJoinerDao ingredientsAndMenuJoinerDao;

    // @todo 만약 세팅되있지 않은 경우 sqlite에 기본 카테고리, 메뉴, 재료 등 삽입 수행
    public Init(DaoSession daoSession) {

        categoryDao = daoSession.getCategoryDao();
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
        categoryDao.insertOrReplace(new Category(1L, "커피"));
        categoryDao.insertOrReplace(new Category(2L, "에이드"));
        categoryDao.insertOrReplace(new Category(3L, "주스"));
        categoryDao.insertOrReplace(new Category(4L, "디저트"));
    }

    private void initMenus() {
        menuDao.insertOrReplace(new Menu(1L, "아메리카노", 1L, 1100, "americano", true));
        menuDao.insertOrReplace(new Menu(2L, "카페라떼", 1L, 1800, "caffe_latte", true));
        menuDao.insertOrReplace(new Menu(3L, "카푸치노", 1L, 2100, "capuccino", true));

        menuDao.insertOrReplace(new Menu(4L, "T아메리카노", 1L, 1100, "americano", true));
        menuDao.insertOrReplace(new Menu(5L, "T카페라떼", 1L, 1800, "caffe_latte", true));
        menuDao.insertOrReplace(new Menu(6L, "T카푸치노", 1L, 2100, "capuccino", true));

        menuDao.insertOrReplace(new Menu(7L, "E아메리카노", 1L, 1100, "americano", true));
        menuDao.insertOrReplace(new Menu(8L, "E카페라떼", 1L, 1800, "caffe_latte", true));
        menuDao.insertOrReplace(new Menu(9L, "E카푸치노", 1L, 2100, "capuccino", true));
    }

    private void initIngredients() {
        ingredientDao.insertOrReplace(new Ingredient(1L, "커피콩",  "coffee"));
        ingredientDao.insertOrReplace(new Ingredient(2L, "물",  "water"));
        ingredientDao.insertOrReplace(new Ingredient(3L, "우유",  "milk"));

    }

    private void initIngredAndMenuJoiners() {
        ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(1L, 1L, 1L));
        ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(2L, 1L, 2L));

        ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(3L, 2L, 1L));
        ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(4L, 2L, 3L));
        ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(5L, 2L, 2L));
        ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(6L, 2L, 3L));

        ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(7L, 3L, 1L));
        ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(8L, 3L, 3L));
    }
}
