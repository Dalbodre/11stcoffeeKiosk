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
import sb.yoon.kiosk.model.Option;
import sb.yoon.kiosk.model.OptionDao;
import sb.yoon.kiosk.model.OptionsAndMenuJoiner;
import sb.yoon.kiosk.model.OptionsAndMenuJoinerDao;

public class Init {
    private CategoryDao categoryDao;
    private MenuDao menuDao;
    private IngredientDao ingredientDao;
    private IngredientsAndMenuJoinerDao ingredientsAndMenuJoinerDao;
    private OptionDao optionDao;
    private OptionsAndMenuJoinerDao optionsAndMenuJoinerDao;

    // @todo 만약 세팅되있지 않은 경우 sqlite에 기본 카테고리, 메뉴, 재료 등 삽입 수행
    public Init(CategoryDao categoryDao, MenuDao menuDao, IngredientDao ingredientDao,
                IngredientsAndMenuJoinerDao ingredientsAndMenuJoinerDao, OptionDao optionDao, OptionsAndMenuJoinerDao optionsAndMenuJoinerDao) {

        this.categoryDao = categoryDao;
        this.menuDao = menuDao;
        this.ingredientDao = ingredientDao;
        this.ingredientsAndMenuJoinerDao = ingredientsAndMenuJoinerDao;
        this.optionDao = optionDao;
        this.optionsAndMenuJoinerDao = optionsAndMenuJoinerDao;

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

        ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(5L, 3L, 1L));
        ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(6L, 3L, 3L));
    }

    private void initOptions() {
        optionDao.insertOrReplace(new Option(1L, "사이즈 선택", false, "레귤러/라지/엑스라지"));
        optionDao.insertOrReplace(new Option(2L, "온도 선택", false, "HOT/COLD"));
    }

    private void initOptionJoiner() {
        optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(1L, 1L, 1L));
        optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(2L, 1L, 2L));

        optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(3L, 2L, 1L));
        optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(4L, 2L, 2L));

        optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(5L, 3L, 1L));
        optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(6L, 3L, 2L));
    }
}
