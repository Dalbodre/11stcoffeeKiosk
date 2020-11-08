package sb.yoon.kiosk.controller;

import android.app.Activity;

import java.util.ArrayList;
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

    public List<Menu> getMenuListByIdArray(ArrayList<Integer> idArray) {
        return this.menuDao.queryBuilder().where(MenuDao.Properties.Id.in(idArray)).list();
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

    public List<Menu> searchMenuByIngredients(Ingredient ingredient) {
        return ingredient.getMenuList();
    }

    // 초기화 역할 하는 클래스
    // 구조상 보기 편하라고 Init 내부 클래스 안에다가 해줬는데 성능상 별로면 클래스 없애고 그냥 메서드로 변경
    class Init {
        private void initCategories() {
            // 1L = new Long(1)
            categoryDao.insertOrReplace(new Category(1L, "커피"));
            categoryDao.insertOrReplace(new Category(2L, "에이드&아이스티"));
            categoryDao.insertOrReplace(new Category(3L, "주스"));
            categoryDao.insertOrReplace(new Category(4L, "라떼"));
            categoryDao.insertOrReplace(new Category(5L, "티백차"));
            categoryDao.insertOrReplace(new Category(6L, "과일청차"));
            categoryDao.insertOrReplace(new Category(7L, "프라페"));
            categoryDao.insertOrReplace(new Category(8L, "스무디"));
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

            //에이드&아이스티
            menuDao.insertOrReplace(new Menu(10L, "레몬에이드", 2L, 3000, "lemon_ade", false));
            menuDao.insertOrReplace(new Menu(11L, "오렌지에이드", 2L, 3000, "orange_ade", false));
            menuDao.insertOrReplace(new Menu(12L, "자몽에이드", 2L, 3500, "jamong_ade", false));
            menuDao.insertOrReplace(new Menu(13L, "청포도에이드", 2L, 3500, "grape_ade", false));
            menuDao.insertOrReplace(new Menu(32L, "복숭아 아이스티", 2L, 2200, "peach_ice", false));

            //주스
            menuDao.insertOrReplace(new Menu(14L, "애플주스", 3L, 3500, "apple", false));
            menuDao.insertOrReplace(new Menu(15L, "애플주스 스파클링", 3L, 3500, "apple_sparkle", false));

            //라떼
            menuDao.insertOrReplace(new Menu(16L, "그린티라떼", 4L,3200, "greentea", true));
            menuDao.insertOrReplace(new Menu(17L, "고구마라떼", 4L, 3200, "spotato", true));
            menuDao.insertOrReplace(new Menu(18L, "밀크티라떼", 4L, 3200, "milktea", true));
            menuDao.insertOrReplace(new Menu(19L, "초코라떼", 4L, 3200, "choco_latte", true));

            //티백차
            menuDao.insertOrReplace(new Menu(20L, "캐모마일", 5L, 2500, "camomile", true));
            menuDao.insertOrReplace(new Menu(21L, "페퍼민트", 5L, 2500, "peppermint", true));
            menuDao.insertOrReplace(new Menu(22L, "얼그레이", 5L, 2500, "elgray_tea", true));

            //과일청차
            menuDao.insertOrReplace(new Menu(23L, "자몽차", 6L, 3500, "jamong_tea", true));
            menuDao.insertOrReplace(new Menu(24L, "유자청", 6L, 3500, "yuja_tea", true));
            menuDao.insertOrReplace(new Menu(25L, "생강차", 6L, 3500, "ginger", true));

            //프라페
            menuDao.insertOrReplace(new Menu(26L, "자바칩프라페", 7L, 4000, "java_frappe", false));
            menuDao.insertOrReplace(new Menu(27L, "쿠앤크프라페", 7L, 4000, "cookie_frappe", false));

            //스무디
            menuDao.insertOrReplace(new Menu(28L, "유자스무디", 8L, 4000, "yuja_smoothe", false));
            menuDao.insertOrReplace(new Menu(29L, "플레인요거트스무디", 8L, 4000, "plain_smoothe", false));
            menuDao.insertOrReplace(new Menu(30L, "딸기요거트스무디", 8L, 4000, "berry_smoothe", false));
            menuDao.insertOrReplace(new Menu(31L, "블루베리요거트스무디", 8L, 4000, "blueberry_smoothe", false));
        }

        private void initIngredients() {
            ingredientDao.insertOrReplace(new Ingredient(1L, "커피원두",  "coffee"));
            ingredientDao.insertOrReplace(new Ingredient(2L, "물",  "water"));
            ingredientDao.insertOrReplace(new Ingredient(3L, "우유",  "milk"));
            ingredientDao.insertOrReplace(new Ingredient(4L, "시나몬가루",  "cynamon"));
            ingredientDao.insertOrReplace(new Ingredient(5L, "바닐라시럽",  "vanila"));
            ingredientDao.insertOrReplace(new Ingredient(6L, "헤이즐넛시럽",  "hazelnut_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(7L, "카라멜소스",  "caramel_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(8L, "초코소스", "choco_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(9L, "민트시럽", "chiyak_syrup"));

            ingredientDao.insertOrReplace(new Ingredient(10L, "탄산수", "spakle"));
            ingredientDao.insertOrReplace(new Ingredient(11L, "각얼음", "gak_ice"));
            ingredientDao.insertOrReplace(new Ingredient(12L, "레몬시럽", "lemon_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(13L, "오렌지시럽", "orange_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(14L, "자몽시럽", "jamong_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(15L, "청포도시럽", "grape_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(16L, "청포도알", "grape"));
            ingredientDao.insertOrReplace(new Ingredient(17L, "복숭아가루", "peach_garu"));

            ingredientDao.insertOrReplace(new Ingredient(18L, "녹차가루", "tea_garu"));
            ingredientDao.insertOrReplace(new Ingredient(19L, "으깬 고구마", "goguma"));
            ingredientDao.insertOrReplace(new Ingredient(20L, "밀크티가루", "milktea_garu"));
            ingredientDao.insertOrReplace(new Ingredient(21L, "초코가루", "choco_garu"));

            ingredientDao.insertOrReplace(new Ingredient(22L, "캐모마일 티백", "camomile_bag"));
            ingredientDao.insertOrReplace(new Ingredient(23L, "페퍼민트 티백", "elgray_bag"));
            ingredientDao.insertOrReplace(new Ingredient(24L, "얼그레이 티백", "peppermint_bag"));

            ingredientDao.insertOrReplace(new Ingredient(25L, "자몽청", "jamong_chung"));
            ingredientDao.insertOrReplace(new Ingredient(26L, "유자청", "yuja_chung"));
            ingredientDao.insertOrReplace(new Ingredient(27L, "생강청", "saenggang"));

            ingredientDao.insertOrReplace(new Ingredient(28L, "자바칩가루", "javachip_garu"));
            ingredientDao.insertOrReplace(new Ingredient(29L, "갈은얼음", "gal_ice"));
            ingredientDao.insertOrReplace(new Ingredient(30L, "쿠앤크가루", "cookie_grau"));

            ingredientDao.insertOrReplace(new Ingredient(31L, "유자", "yuja"));
            ingredientDao.insertOrReplace(new Ingredient(32L, "요거트가루", "yogurt_garu"));
            ingredientDao.insertOrReplace(new Ingredient(33L, "딸기", "straw"));
            ingredientDao.insertOrReplace(new Ingredient(34L, "블루베리", "blueberry"));
        }

        private void initIngredAndMenuJoiners() {
            //에소프레소
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(1L, 1L, 1L));
            //아메리카노
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(2L, 2L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(3L, 2L, 2L));
            //카페라떼
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(4L, 3L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(5L, 3L, 3L));
            //카푸치노
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(6L, 4L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(7L, 4L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(8L, 4L, 4L));
            //바닐라라떼
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(9L, 5L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(10L, 5L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(11L, 5L, 5L));
            //헤이즐넛라떼
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(12L, 6L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(13L, 6L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(14L, 6L, 6L));
            //카라멜마끼아또
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(15L, 7L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(16L, 7L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(17L, 7L, 7L));
            //카페모카
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(18L, 8L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(19L, 8L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(20L, 8L, 8L));
            //민트그린모카
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(21L, 9L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(22L, 9L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(23L, 9L, 8L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(24L, 9L, 9L));

            //레몬에이드
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(26L, 10L, 10L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(27L, 10L, 11L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(25L, 10L, 12L));
            //오렌지에이드
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(29L, 11L, 10L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(30L, 11L, 11L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(28L, 11L, 13L));
            //자몽에이드
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(32L, 12L, 10L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(33L, 12L, 11L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(31L, 12L, 14L));
            //청포도에이드
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(36L, 13L, 10L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(37L, 13L, 11L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(34L, 13L, 15L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(35L, 13L, 16L));
            //복숭아 아이스티
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(38L, 32L, 17L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(39L, 32L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(40L, 32L, 11L));

            //그린티라떼
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(41L, 16L, 18L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(42L, 16L, 3L));
            //고구마라떼
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(43L, 17L, 19L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(44L, 17L, 3L));
            //밀크티라떼
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(45L, 18L, 20L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(46L, 18L, 3L));
            //초코라떼
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(47L, 19L, 21L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(48L, 19L, 3L));

            //캐모마일
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(49L, 20L, 22L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(50L, 20L, 2L));
            //페퍼민트
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(51L, 21L, 23L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(52L, 21L, 2L));
            //얼그레이
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(53L, 22L, 24L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(54L, 22L, 2L));

            //자몽차
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(55L, 23L, 25L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(56L, 23L, 2L));
            //유자차
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(57L, 24L, 26L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(58L, 24L, 2L));
            //생강차
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(59L, 25L, 27L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(60L, 25L, 2L));

            //자바칩프라페
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(61L, 26L, 28L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(62L, 26L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(63L, 26L, 29L));
            //쿠앤크프라페
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(64L, 27L, 30L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(65L, 27L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(66L, 27L, 29L));

            //유자스무디
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(67L, 28L, 31L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(68L, 28L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(69L, 28L, 29L));
            //플레인 요거트스무디
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(70L, 29L, 32L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(71L, 29L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(72L, 29L, 29L));
            //딸기 요거트스무디
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(73L, 30L, 33L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(74L, 30L, 32L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(75L, 30L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(76L, 30L, 29L));
            //블루베리 요거트스무디
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(77L, 31L, 34L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(78L, 31L, 32L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(79L, 31L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(80L, 31L, 29L));
        }

        private void initOptions() {
            optionDao.insertOrReplace(new Option(1L, "테이크아웃", 0, false));
            optionDao.insertOrReplace(new Option(2L, "텀블러", -200, false));
            optionDao.insertOrReplace(new Option(3L, "샷 추가", 500, true));
            optionDao.insertOrReplace(new Option(4L, "설탕시럽", 500, true));
            optionDao.insertOrReplace(new Option(5L, "헤이즐넛시럽", 500, true));
            optionDao.insertOrReplace(new Option(6L, "연하게", 0, false));
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
