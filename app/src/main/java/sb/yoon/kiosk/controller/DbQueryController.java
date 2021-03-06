package sb.yoon.kiosk.controller;

import android.util.Log;

import org.greenrobot.greendao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

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
    public CategoryDao categoryDao;
    public MenuDao menuDao;
    private IngredientDao ingredientDao;
    public IngredientsAndMenuJoinerDao ingredientsAndMenuJoinerDao;
    public OptionDao optionDao;
    public OptionsAndMenuJoinerDao optionsAndMenuJoinerDao;

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

    public Long getLastMenuIdx() {
        if( this.menuDao.queryBuilder().orderDesc(MenuDao.Properties.Id).limit(1).unique() == null){
            return 0L;
        }
        else{
            return (Long)this.menuDao.queryBuilder().orderDesc(MenuDao.Properties.Id).limit(1).unique().getId();
        }
    }
    public Long getLastCategoryIdx(){
        if(this.categoryDao.queryBuilder().orderDesc(CategoryDao.Properties.Id).limit(1).unique() == null){
            return 0L;
        }
        else {
            return (Long) this.categoryDao.queryBuilder().orderDesc(CategoryDao.Properties.Id).limit(1).unique().getId();
        }
    }
    public Long getLastOptionAndMenuJoinerIdx(){
        if(this.optionsAndMenuJoinerDao.queryBuilder().orderDesc(OptionsAndMenuJoinerDao.Properties.Id).limit(1).unique() == null){
            return 0L;
        }
        else{
            return (Long)this.optionsAndMenuJoinerDao.queryBuilder().orderDesc(OptionsAndMenuJoinerDao.Properties.Id).limit(1).unique().getId();
        }
    }

    public IngredientsAndMenuJoinerDao getIngredientsAndMenuJoinerDao() {
        return this.ingredientsAndMenuJoinerDao;
    }
    public OptionsAndMenuJoinerDao getOptionsAndMenuJoinerDao(){
        return this.optionsAndMenuJoinerDao;
    }

    // DB 초기값 설정 역할
    public void initDB() {
        Init init = new Init();

        init.destroyAll();
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
    public Menu getMenu(Long Id){
        return this.menuDao.queryBuilder().where(MenuDao.Properties.Id.eq(Id)).unique();
    }

    public List<Ingredient> getIngredientList(Menu menu) {
        return menu.getIngredientList();
    }

    public List<Option> getOptionList(Menu menu) {
        return menu.getOptionList();
    }

    public List<OptionsAndMenuJoiner> getOptionListInDb(Long menuID){
        return this.optionsAndMenuJoinerDao.queryBuilder().where(OptionsAndMenuJoinerDao.Properties.MenuId.eq(menuID)).list();
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
    public void refreshCategory(Long id){
        Log.d("status", "refresh");
        if (menuDao.queryBuilder().where(MenuDao.Properties.CategoryId.eq(id)).count() == 0) {
            categoryDao.deleteByKey(id);
            System.out.println("야");
        }
    }

    /*public String getCategoryName(Long id){
        return this.categoryDao.queryRaw(CategoryDao.Properties.Id.eq(id), );
    }*/

    // 초기화 역할 하는 클래스
    // 구조상 보기 편하라고 Init 내부 클래스 안에다가 해줬는데 성능상 별로면 클래스 없애고 그냥 메서드로 변경
    class Init {
        private void destroyAll() {
            for (AbstractDao abstractDao : daoSession.getAllDaos()){
                abstractDao.deleteAll();
            }
        }
        private void initCategories() {
            // 1L = new Long(1)
            categoryDao.insertOrReplace(new Category(1L, "커피", true));
            categoryDao.insertOrReplace(new Category(2L, "라떼", true));
            categoryDao.insertOrReplace(new Category(3L, "차",true));
            categoryDao.insertOrReplace(new Category(4L, "주스&에이드",true));
            categoryDao.insertOrReplace(new Category(5L, "스무디&파르페", true));
            categoryDao.insertOrReplace(new Category(6L, "베이커리", false));
        }

        private void initMenus() {
            //커피 1L
            menuDao.insertOrReplace(new Menu(1L, "에소프레소", 1L, 2000, "espresso", true, false));
            menuDao.insertOrReplace(new Menu(2L, "아메리카노", 1L, 2200, "americano", true, true));
            menuDao.insertOrReplace(new Menu(3L, "카페라떼", 1L, 2800, "caffe_latte", true, true));
            menuDao.insertOrReplace(new Menu(4L, "카푸치노", 1L, 2800, "capuccino", true, false));
            menuDao.insertOrReplace(new Menu(5L, "바닐라라떼", 1L, 3200, "vanila", true, true));
            menuDao.insertOrReplace(new Menu(6L, "헤이즐넛라떼", 1L, 3200, "hazel_latte", true, true));
            menuDao.insertOrReplace(new Menu(7L, "카라멜마끼아또", 1L, 3200, "macchiato", true, true));
            menuDao.insertOrReplace(new Menu(8L, "카페모카", 1L, 3200, "caffe_mocha", true, true));
            menuDao.insertOrReplace(new Menu(9L, "민트그린모카", 1L, 3200, "chiyak", true, true));

            //라떼 2L
            menuDao.insertOrReplace(new Menu(16L, "그린티라떼", 2L,3200, "greentea", true, true));
            menuDao.insertOrReplace(new Menu(17L, "고구마라떼", 2L, 3200, "spotato", true, true));
            menuDao.insertOrReplace(new Menu(18L, "밀크티라떼", 2L, 3200, "milktea", true, true));
            menuDao.insertOrReplace(new Menu(19L, "초코라떼", 2L, 3200, "choco_latte", true, true));

            //티백차 3L
            menuDao.insertOrReplace(new Menu(20L, "캐모마일", 3L, 2500, "camomile", true, true));
            menuDao.insertOrReplace(new Menu(21L, "페퍼민트", 3L, 2500, "peppermint", true, true));
            menuDao.insertOrReplace(new Menu(22L, "얼그레이", 3L, 2500, "elgray_tea", true, true));

            //과일청차 3L
            menuDao.insertOrReplace(new Menu(23L, "자몽차", 3L, 3500, "jamong_tea", true, true));
            menuDao.insertOrReplace(new Menu(24L, "유자청", 3L, 3500, "yuja_tea", true, true));
            menuDao.insertOrReplace(new Menu(25L, "생강차", 3L, 3500, "ginger", true, true));

            // 아이스티 3L
            menuDao.insertOrReplace(new Menu(43L, "복숭아 아이스티", 3L, 2200, "peach_ice", false, true));

            //주스 4L
            menuDao.insertOrReplace(new Menu(14L, "애플주스", 4L, 3500, "apple", false, true));
            menuDao.insertOrReplace(new Menu(15L, "애플주스 스파클링", 4L, 3500, "apple_sparkle", false, true));

            //에이드 4L
            menuDao.insertOrReplace(new Menu(10L, "레몬에이드", 4L, 3000, "lemon_ade", false, true));
            menuDao.insertOrReplace(new Menu(11L, "오렌지에이드", 4L, 3000, "orange_ade", false, true));
            menuDao.insertOrReplace(new Menu(12L, "자몽에이드", 4L, 3500, "jamong_ade", false, true));
            menuDao.insertOrReplace(new Menu(13L, "청포도에이드", 4L, 3500, "grape_ade", false, true));

            //프라페 5L
            menuDao.insertOrReplace(new Menu(26L, "자바칩프라페", 5L, 4000, "java_frappe", false, true));
            menuDao.insertOrReplace(new Menu(27L, "쿠앤크프라페", 5L, 4000, "cookie_frappe", false, true));

            //스무디 5L
            menuDao.insertOrReplace(new Menu(28L, "유자스무디", 5L, 4000, "yuja_smoothe", false, true));
            menuDao.insertOrReplace(new Menu(29L, "플레인스무디", 5L, 4000, "plain_smoothe", false, true));
            menuDao.insertOrReplace(new Menu(30L, "딸기스무디", 5L, 4000, "berry_smoothe", false, true));
            menuDao.insertOrReplace(new Menu(31L, "블루베리스무디", 5L, 4000, "blueberry_smoothe", false, true));

            //빵 6L
            menuDao.insertOrReplace(new Menu(32L, "베이글(플레인)", 6L, 2800, "bagle", false, false));
            menuDao.insertOrReplace(new Menu(33L, "모카번", 6L, 2300, "mokabun", false, false));
            menuDao.insertOrReplace(new Menu(34L, "햄 에그모닝머핀", 6L, 3500, "egg_muffin", false, false));
            menuDao.insertOrReplace(new Menu(35L, "크로크무슈", 6L, 3500, "crossmushu", false, false));
            menuDao.insertOrReplace(new Menu(36L, "고구마무스", 6L, 4000, "plain_cake", false, false));
            menuDao.insertOrReplace(new Menu(37L, "초코무스", 6L, 4000, "choco_cake", false, false));
            menuDao.insertOrReplace(new Menu(38L, "치즈케익", 6L, 4000, "cheese_cake", false, false));
            menuDao.insertOrReplace(new Menu(39L, "생크림 카스테라", 6L, 4000, "muffin", false, false));
            menuDao.insertOrReplace(new Menu(40L, "마카롱(초코)", 6L, 2000, "choco_macarron", false, false));
            menuDao.insertOrReplace(new Menu(41L, "마카롱(바닐라)", 6L, 2000, "banilla_macarron", false, false));
            menuDao.insertOrReplace(new Menu(42L, "마카롱(딸기)", 6L, 2000, "caramel_macarron", false, false));
        }

        private void initIngredients() {
            ingredientDao.insertOrReplace(new Ingredient(1L, "커피원두",  "coffee"));
            ingredientDao.insertOrReplace(new Ingredient(2L, "물",  "water"));
            ingredientDao.insertOrReplace(new Ingredient(3L, "우유",  "milk"));
            ingredientDao.insertOrReplace(new Ingredient(4L, "시나몬가루",  "cynamon"));
            ingredientDao.insertOrReplace(new Ingredient(5L, "바닐라시럽",  "vanila_syrup"));
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
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(38L, 43L, 17L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(39L, 43L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(40L, 43L, 11L));

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
            optionDao.insertOrReplace(new Option(1L, "샷 추가", 500, true));
            optionDao.insertOrReplace(new Option(2L, "설탕시럽", 500, true));
            optionDao.insertOrReplace(new Option(3L, "헤이즐넛시럽", 500, true));
            optionDao.insertOrReplace(new Option(4L, "연하게", 0, false));
        }

        private void initOptionJoiner() {
//            Long i = 1L;
//            for(Long j = 1L; j<=32L; j++){
//                for(Long k = 1L; k<=6L; k++){
//                    optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(i++, j, k));
//                }
//            }

            // 기본적으로 텀블러, 테이크아웃은 가지고 있으므로 모든 메뉴에 해당 옵션들 추가
//            Long joinerId = 1L;
//            for (Long menuId = 1L; menuId<=32L; menuId++) {
//                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 5L));
//                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 6L));
//            }

            Long joinerId = 1L;
            // 커피 카테고리에 샷, 설탕시럽, 헤이즐넛 시럽, 연하게 옵션들 추가
            for (Long menuId = 1L; menuId<=9L; menuId++) {
                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 1L));
                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 2L));
                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 3L));
                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 4L));
            }

            // 복숭아 아이스티에 샷 추가
            optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(37L, 43L, 1L));

//            //ex)
//            optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(1L, 1L, 1L));
        }
    }
}
