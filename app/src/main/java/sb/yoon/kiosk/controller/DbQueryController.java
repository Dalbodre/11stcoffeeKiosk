package sb.yoon.kiosk.controller;

import android.app.Activity;

import java.util.List;

import sb.yoon.kiosk.KioskApplication;
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

public class DbQueryController {
    private DaoSession daoSession;
    private CategoryDao categoryDao;
    private MenuDao menuDao;
    private IngredientDao ingredientDao;
    private IngredientsAndMenuJoinerDao ingredientsAndMenuJoinerDao;
    private OptionDao optionDao;
    private OptionsAndMenuJoinerDao optionsAndMenuJoinerDao;

    public DbQueryController(Activity context) {
        this.daoSession = ((KioskApplication)context.getApplication()).getDaoSession();
        this.categoryDao = daoSession.getCategoryDao();
        this.menuDao = daoSession.getMenuDao();
        this.ingredientDao = daoSession.getIngredientDao();
        this.ingredientsAndMenuJoinerDao = daoSession.getIngredientsAndMenuJoinerDao();
        this.optionDao = daoSession.getOptionDao();
        this.optionsAndMenuJoinerDao = daoSession.getOptionsAndMenuJoinerDao();
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

    // DB 초기값 설정 역할
    public void initDB() {
        Init init = new Init();

        init.initCategories();

        init.initMenus();
        init.initIngredients();
        init.initIngredAndMenuJoiners();

        init.initOptions();
        init.initOptionJoiner();
    }

    public List<Category> getCategoriesList() {
        return this.categoryDao.queryBuilder().orderAsc(CategoryDao.Properties.Id).list();
    }

    public List<Menu> getMenuList(Category category) {
        return category.getMenuList();
    }

    public List<Ingredient> getIngredientList(Menu menu) {
        return menu.getIngredientList();
    }

    public List<Option> getOptionList(Menu menu) {
        return menu.getOptionList();
    }

    // 이름으로 메뉴 검색
    public List<Menu> searchMenusByName(String name) {
        return menuDao.queryBuilder().where(MenuDao.Properties.Name.like("%" + name + "%")).list();
    }

    // 이름으로 재료 검색
    public List<Ingredient> searchIngredientsByName(String name) {
        return ingredientDao.queryBuilder().where(IngredientDao.Properties.Name.like("%" + name + "%")).list();
    }

    // 초기화 역할 하는 클래스
    // 구조상 보기 편하라고 Init 내부 클래스 안에다가 해줬는데 성능상 별로면 클래스 없애고 그냥 메서드로 변경
    class Init {
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


}
