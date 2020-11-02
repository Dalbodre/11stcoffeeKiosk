package sb.yoon.kiosk.controller;

import android.app.Activity;

import java.util.List;

import sb.yoon.kiosk.KioskApplication;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.CategoryDao;
import sb.yoon.kiosk.model.DaoSession;
import sb.yoon.kiosk.model.IngredientDao;
import sb.yoon.kiosk.model.IngredientsAndMenuJoinerDao;
import sb.yoon.kiosk.model.MenuDao;

public class DbQueryController {
    private DaoSession daoSession;
    private CategoryDao categoryDao;
    private MenuDao menuDao;
    private IngredientDao ingredientDao;
    private IngredientsAndMenuJoinerDao ingredientsAndMenuJoinerDao;

    public DbQueryController(Activity context) {
        this.daoSession = ((KioskApplication)context.getApplication()).getDaoSession();
        this.categoryDao = daoSession.getCategoryDao();
        this.menuDao = daoSession.getMenuDao();
        this.ingredientDao = daoSession.getIngredientDao();
        this.ingredientsAndMenuJoinerDao = daoSession.getIngredientsAndMenuJoinerDao();
    }

    public CategoryDao getCategoryDao() {
        return this.categoryDao;
    }

    public MenuDao getMenuDao() {
        return this.menuDao;
    }

    public IngredientDao getIngredientDao() {
        return this.ingredientDao;
    }

    public IngredientsAndMenuJoinerDao getIngredientsAndMenuJoinerDao() {
        return this.ingredientsAndMenuJoinerDao;
    }

    public void initDB() {
        try {
            // 키오스크 초기 설정값들 넣어주기
            new Init(this.categoryDao, this.menuDao, this.ingredientDao, this.ingredientsAndMenuJoinerDao);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Category> getCategoriesList() {
        return this.categoryDao.queryBuilder().orderAsc(CategoryDao.Properties.Id).list();
    }

}
