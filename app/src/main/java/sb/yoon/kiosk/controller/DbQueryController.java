package sb.yoon.kiosk.controller;

import android.app.Activity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public DbQueryController(DaoSession daoSession) {
        this.daoSession = daoSession;
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

    // 해당 메뉴에 대한 옵션을 해시맵으로 돌려줌
    // key: [옵션이름, multiSelectable] value: List<옵션아이템들>
   /* public HashMap<String[], List<String>> getParsedOptionList(Menu menu) {
        List<Option> optionList = this.getOptionList(menu);
        HashMap<String[], List<String>> parsedOptionMap = new HashMap<>();

        for (Option option : optionList) {
            String name = option.getName();
            String multiSelectable = option.getMultiSelectable().toString();
            String[] key = {name, multiSelectable};

            List<String> value = Arrays.asList(option.getValue().split("/"));
            parsedOptionMap.put(key, value);
        }

        return parsedOptionMap;
    }*/

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
            //커피
            menuDao.insertOrReplace(new Menu(1L, "에소프레소", 1L, 2000, "espresso", true));
            menuDao.insertOrReplace(new Menu(2L, "아메리카노", 1L, 2200, "americano", true));
            menuDao.insertOrReplace(new Menu(3L, "카페라떼", 1L, 2800, "caffe_latte", true));
            menuDao.insertOrReplace(new Menu(4L, "카푸치노", 1L, 2800, "capuccino", true));
            menuDao.insertOrReplace(new Menu(5L, "바닐라라떼", 1L, 3200, "vanila", true));
            menuDao.insertOrReplace(new Menu(6L, "헤어즐넛라떼", 1L, 3200, "hazel_latte", true));
            menuDao.insertOrReplace(new Menu(7L, "카라멜마끼아또", 1L, 3200, "macchiato", true));
            menuDao.insertOrReplace(new Menu(8L, "카페모카", 1L, 3200, "caffe_mocha", true));
            menuDao.insertOrReplace(new Menu(9L, "민트그린모카", 1L, 3200, "chiyak", true));

            /*//에이드
            menuDao.insertOrReplace(new Menu(10L, "레몬에이드"));
            menuDao.insertOrReplace(new Menu(11L, "오렌지에이드"));
            menuDao.insertOrReplace(new Menu(12L, "자몽에이드"));
            menuDao.insertOrReplace(new Menu(13L, "청포도에이드"));

            //주스
            menuDao.insertOrReplace(new Menu(14L, "애플주스"));
            menuDao.insertOrReplace(new Menu(15L, "애플주스 스파클링"));

            //라떼
            menuDao.insertOrReplace(new Menu(16L, "그린티라떼"));
            menuDao.insertOrReplace(new Menu(17L, "고구마라떼"));
            menuDao.insertOrReplace(new Menu(18L, "밀크티라떼"));
            menuDao.insertOrReplace(new Menu(19L, "초코라떼"));

            //티백차
            menuDao.insertOrReplace(new Menu(20L, "캐모마일"));
            menuDao.insertOrReplace(new Menu(21L, "페퍼민트"));
            menuDao.insertOrReplace(new Menu(22L, "얼그레이"));

            //과일청차
            menuDao.insertOrReplace(new Menu(23L, "자몽차"));
            menuDao.insertOrReplace(new Menu(24L, "유자청"));
            menuDao.insertOrReplace(new Menu(25L, "생강차"));

            //프라페
            menuDao.insertOrReplace(new Menu(26L, "자바칩프라페"));
            menuDao.insertOrReplace(new Menu(27L, "쿠앤크프라페"));

            //스무디
            menuDao.insertOrReplace(new Menu(28L, "유자스무디"));
            menuDao.insertOrReplace(new Menu(29L, "플레인요거트스무디"));
            menuDao.insertOrReplace(new Menu(30L, "딸기요거트스무디"));
            menuDao.insertOrReplace(new Menu(31L, "블루베리요거트스무디"));
*/
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
            optionDao.insertOrReplace(new Option(1L, "사이즈", 500));
            optionDao.insertOrReplace(new Option(2L, "온도", 0));
            optionDao.insertOrReplace(new Option(3L, "테이크아웃", 0));
            optionDao.insertOrReplace(new Option(4L, "텀블러", -200));
            optionDao.insertOrReplace(new Option(5L,"샷", 500));
            optionDao.insertOrReplace(new Option(6L, "설탕시럽", 0));
            optionDao.insertOrReplace(new Option(7L, "헤이즐넛시럽", 500));
            optionDao.insertOrReplace(new Option(8L, "연하게", 0));
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
