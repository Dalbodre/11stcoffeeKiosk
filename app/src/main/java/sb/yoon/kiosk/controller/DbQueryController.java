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

    // DB ????????? ?????? ??????
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

    // ?????? ????????? ?????? ????????? ??????????????? ?????????
    // key: [????????????, multiSelectable] value: List<??????????????????>
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

    // ???????????? ?????? ??????
    public List<Menu> searchMenusByName(String name) {
        return menuDao.queryBuilder().where(MenuDao.Properties.Name.like("%" + name + "%")).list();
    }

    // ???????????? ?????? ??????
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
            System.out.println("???");
        }
    }

    /*public String getCategoryName(Long id){
        return this.categoryDao.queryRaw(CategoryDao.Properties.Id.eq(id), );
    }*/

    // ????????? ?????? ?????? ?????????
    // ????????? ?????? ???????????? Init ?????? ????????? ???????????? ???????????? ????????? ????????? ????????? ????????? ?????? ???????????? ??????
    class Init {
        private void destroyAll() {
            for (AbstractDao abstractDao : daoSession.getAllDaos()){
                abstractDao.deleteAll();
            }
        }
        private void initCategories() {
            // 1L = new Long(1)
            categoryDao.insertOrReplace(new Category(1L, "??????", true));
            categoryDao.insertOrReplace(new Category(2L, "??????", true));
            categoryDao.insertOrReplace(new Category(3L, "???",true));
            categoryDao.insertOrReplace(new Category(4L, "??????&?????????",true));
            categoryDao.insertOrReplace(new Category(5L, "?????????&?????????", true));
            categoryDao.insertOrReplace(new Category(6L, "????????????", false));
        }

        private void initMenus() {
            //?????? 1L
            menuDao.insertOrReplace(new Menu(1L, "???????????????", 1L, 2000, "espresso", true, false));
            menuDao.insertOrReplace(new Menu(2L, "???????????????", 1L, 2200, "americano", true, true));
            menuDao.insertOrReplace(new Menu(3L, "????????????", 1L, 2800, "caffe_latte", true, true));
            menuDao.insertOrReplace(new Menu(4L, "????????????", 1L, 2800, "capuccino", true, false));
            menuDao.insertOrReplace(new Menu(5L, "???????????????", 1L, 3200, "vanila", true, true));
            menuDao.insertOrReplace(new Menu(6L, "??????????????????", 1L, 3200, "hazel_latte", true, true));
            menuDao.insertOrReplace(new Menu(7L, "?????????????????????", 1L, 3200, "macchiato", true, true));
            menuDao.insertOrReplace(new Menu(8L, "????????????", 1L, 3200, "caffe_mocha", true, true));
            menuDao.insertOrReplace(new Menu(9L, "??????????????????", 1L, 3200, "chiyak", true, true));

            //?????? 2L
            menuDao.insertOrReplace(new Menu(16L, "???????????????", 2L,3200, "greentea", true, true));
            menuDao.insertOrReplace(new Menu(17L, "???????????????", 2L, 3200, "spotato", true, true));
            menuDao.insertOrReplace(new Menu(18L, "???????????????", 2L, 3200, "milktea", true, true));
            menuDao.insertOrReplace(new Menu(19L, "????????????", 2L, 3200, "choco_latte", true, true));

            //????????? 3L
            menuDao.insertOrReplace(new Menu(20L, "????????????", 3L, 2500, "camomile", true, true));
            menuDao.insertOrReplace(new Menu(21L, "????????????", 3L, 2500, "peppermint", true, true));
            menuDao.insertOrReplace(new Menu(22L, "????????????", 3L, 2500, "elgray_tea", true, true));

            //???????????? 3L
            menuDao.insertOrReplace(new Menu(23L, "?????????", 3L, 3500, "jamong_tea", true, true));
            menuDao.insertOrReplace(new Menu(24L, "?????????", 3L, 3500, "yuja_tea", true, true));
            menuDao.insertOrReplace(new Menu(25L, "?????????", 3L, 3500, "ginger", true, true));

            // ???????????? 3L
            menuDao.insertOrReplace(new Menu(43L, "????????? ????????????", 3L, 2200, "peach_ice", false, true));

            //?????? 4L
            menuDao.insertOrReplace(new Menu(14L, "????????????", 4L, 3500, "apple", false, true));
            menuDao.insertOrReplace(new Menu(15L, "???????????? ????????????", 4L, 3500, "apple_sparkle", false, true));

            //????????? 4L
            menuDao.insertOrReplace(new Menu(10L, "???????????????", 4L, 3000, "lemon_ade", false, true));
            menuDao.insertOrReplace(new Menu(11L, "??????????????????", 4L, 3000, "orange_ade", false, true));
            menuDao.insertOrReplace(new Menu(12L, "???????????????", 4L, 3500, "jamong_ade", false, true));
            menuDao.insertOrReplace(new Menu(13L, "??????????????????", 4L, 3500, "grape_ade", false, true));

            //????????? 5L
            menuDao.insertOrReplace(new Menu(26L, "??????????????????", 5L, 4000, "java_frappe", false, true));
            menuDao.insertOrReplace(new Menu(27L, "??????????????????", 5L, 4000, "cookie_frappe", false, true));

            //????????? 5L
            menuDao.insertOrReplace(new Menu(28L, "???????????????", 5L, 4000, "yuja_smoothe", false, true));
            menuDao.insertOrReplace(new Menu(29L, "??????????????????", 5L, 4000, "plain_smoothe", false, true));
            menuDao.insertOrReplace(new Menu(30L, "???????????????", 5L, 4000, "berry_smoothe", false, true));
            menuDao.insertOrReplace(new Menu(31L, "?????????????????????", 5L, 4000, "blueberry_smoothe", false, true));

            //??? 6L
            menuDao.insertOrReplace(new Menu(32L, "?????????(?????????)", 6L, 2800, "bagle", false, false));
            menuDao.insertOrReplace(new Menu(33L, "?????????", 6L, 2300, "mokabun", false, false));
            menuDao.insertOrReplace(new Menu(34L, "??? ??????????????????", 6L, 3500, "egg_muffin", false, false));
            menuDao.insertOrReplace(new Menu(35L, "???????????????", 6L, 3500, "crossmushu", false, false));
            menuDao.insertOrReplace(new Menu(36L, "???????????????", 6L, 4000, "plain_cake", false, false));
            menuDao.insertOrReplace(new Menu(37L, "????????????", 6L, 4000, "choco_cake", false, false));
            menuDao.insertOrReplace(new Menu(38L, "????????????", 6L, 4000, "cheese_cake", false, false));
            menuDao.insertOrReplace(new Menu(39L, "????????? ????????????", 6L, 4000, "muffin", false, false));
            menuDao.insertOrReplace(new Menu(40L, "?????????(??????)", 6L, 2000, "choco_macarron", false, false));
            menuDao.insertOrReplace(new Menu(41L, "?????????(?????????)", 6L, 2000, "banilla_macarron", false, false));
            menuDao.insertOrReplace(new Menu(42L, "?????????(??????)", 6L, 2000, "caramel_macarron", false, false));
        }

        private void initIngredients() {
            ingredientDao.insertOrReplace(new Ingredient(1L, "????????????",  "coffee"));
            ingredientDao.insertOrReplace(new Ingredient(2L, "???",  "water"));
            ingredientDao.insertOrReplace(new Ingredient(3L, "??????",  "milk"));
            ingredientDao.insertOrReplace(new Ingredient(4L, "???????????????",  "cynamon"));
            ingredientDao.insertOrReplace(new Ingredient(5L, "???????????????",  "vanila_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(6L, "??????????????????",  "hazelnut_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(7L, "???????????????",  "caramel_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(8L, "????????????", "choco_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(9L, "????????????", "chiyak_syrup"));

            ingredientDao.insertOrReplace(new Ingredient(10L, "?????????", "spakle"));
            ingredientDao.insertOrReplace(new Ingredient(11L, "?????????", "gak_ice"));
            ingredientDao.insertOrReplace(new Ingredient(12L, "????????????", "lemon_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(13L, "???????????????", "orange_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(14L, "????????????", "jamong_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(15L, "???????????????", "grape_syrup"));
            ingredientDao.insertOrReplace(new Ingredient(16L, "????????????", "grape"));
            ingredientDao.insertOrReplace(new Ingredient(17L, "???????????????", "peach_garu"));

            ingredientDao.insertOrReplace(new Ingredient(18L, "????????????", "tea_garu"));
            ingredientDao.insertOrReplace(new Ingredient(19L, "?????? ?????????", "goguma"));
            ingredientDao.insertOrReplace(new Ingredient(20L, "???????????????", "milktea_garu"));
            ingredientDao.insertOrReplace(new Ingredient(21L, "????????????", "choco_garu"));

            ingredientDao.insertOrReplace(new Ingredient(22L, "???????????? ??????", "camomile_bag"));
            ingredientDao.insertOrReplace(new Ingredient(23L, "???????????? ??????", "elgray_bag"));
            ingredientDao.insertOrReplace(new Ingredient(24L, "???????????? ??????", "peppermint_bag"));

            ingredientDao.insertOrReplace(new Ingredient(25L, "?????????", "jamong_chung"));
            ingredientDao.insertOrReplace(new Ingredient(26L, "?????????", "yuja_chung"));
            ingredientDao.insertOrReplace(new Ingredient(27L, "?????????", "saenggang"));

            ingredientDao.insertOrReplace(new Ingredient(28L, "???????????????", "javachip_garu"));
            ingredientDao.insertOrReplace(new Ingredient(29L, "????????????", "gal_ice"));
            ingredientDao.insertOrReplace(new Ingredient(30L, "???????????????", "cookie_grau"));

            ingredientDao.insertOrReplace(new Ingredient(31L, "??????", "yuja"));
            ingredientDao.insertOrReplace(new Ingredient(32L, "???????????????", "yogurt_garu"));
            ingredientDao.insertOrReplace(new Ingredient(33L, "??????", "straw"));
            ingredientDao.insertOrReplace(new Ingredient(34L, "????????????", "blueberry"));
        }

        private void initIngredAndMenuJoiners() {
            //???????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(1L, 1L, 1L));
            //???????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(2L, 2L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(3L, 2L, 2L));
            //????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(4L, 3L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(5L, 3L, 3L));
            //????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(6L, 4L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(7L, 4L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(8L, 4L, 4L));
            //???????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(9L, 5L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(10L, 5L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(11L, 5L, 5L));
            //??????????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(12L, 6L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(13L, 6L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(14L, 6L, 6L));
            //?????????????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(15L, 7L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(16L, 7L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(17L, 7L, 7L));
            //????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(18L, 8L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(19L, 8L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(20L, 8L, 8L));
            //??????????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(21L, 9L, 1L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(22L, 9L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(23L, 9L, 8L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(24L, 9L, 9L));

            //???????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(26L, 10L, 10L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(27L, 10L, 11L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(25L, 10L, 12L));
            //??????????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(29L, 11L, 10L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(30L, 11L, 11L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(28L, 11L, 13L));
            //???????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(32L, 12L, 10L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(33L, 12L, 11L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(31L, 12L, 14L));
            //??????????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(36L, 13L, 10L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(37L, 13L, 11L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(34L, 13L, 15L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(35L, 13L, 16L));
            //????????? ????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(38L, 43L, 17L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(39L, 43L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(40L, 43L, 11L));

            //???????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(41L, 16L, 18L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(42L, 16L, 3L));
            //???????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(43L, 17L, 19L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(44L, 17L, 3L));
            //???????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(45L, 18L, 20L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(46L, 18L, 3L));
            //????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(47L, 19L, 21L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(48L, 19L, 3L));

            //????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(49L, 20L, 22L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(50L, 20L, 2L));
            //????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(51L, 21L, 23L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(52L, 21L, 2L));
            //????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(53L, 22L, 24L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(54L, 22L, 2L));

            //?????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(55L, 23L, 25L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(56L, 23L, 2L));
            //?????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(57L, 24L, 26L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(58L, 24L, 2L));
            //?????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(59L, 25L, 27L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(60L, 25L, 2L));

            //??????????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(61L, 26L, 28L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(62L, 26L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(63L, 26L, 29L));
            //??????????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(64L, 27L, 30L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(65L, 27L, 3L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(66L, 27L, 29L));

            //???????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(67L, 28L, 31L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(68L, 28L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(69L, 28L, 29L));
            //????????? ??????????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(70L, 29L, 32L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(71L, 29L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(72L, 29L, 29L));
            //?????? ??????????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(73L, 30L, 33L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(74L, 30L, 32L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(75L, 30L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(76L, 30L, 29L));
            //???????????? ??????????????????
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(77L, 31L, 34L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(78L, 31L, 32L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(79L, 31L, 2L));
            ingredientsAndMenuJoinerDao.insertOrReplace(new IngredientsAndMenuJoiner(80L, 31L, 29L));
        }

        private void initOptions() {
            optionDao.insertOrReplace(new Option(1L, "??? ??????", 500, true));
            optionDao.insertOrReplace(new Option(2L, "????????????", 500, true));
            optionDao.insertOrReplace(new Option(3L, "??????????????????", 500, true));
            optionDao.insertOrReplace(new Option(4L, "?????????", 0, false));
        }

        private void initOptionJoiner() {
//            Long i = 1L;
//            for(Long j = 1L; j<=32L; j++){
//                for(Long k = 1L; k<=6L; k++){
//                    optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(i++, j, k));
//                }
//            }

            // ??????????????? ?????????, ?????????????????? ????????? ???????????? ?????? ????????? ?????? ????????? ??????
//            Long joinerId = 1L;
//            for (Long menuId = 1L; menuId<=32L; menuId++) {
//                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 5L));
//                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 6L));
//            }

            Long joinerId = 1L;
            // ?????? ??????????????? ???, ????????????, ???????????? ??????, ????????? ????????? ??????
            for (Long menuId = 1L; menuId<=9L; menuId++) {
                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 1L));
                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 2L));
                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 3L));
                optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(joinerId++, menuId, 4L));
            }

            // ????????? ??????????????? ??? ??????
            optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(37L, 43L, 1L));

//            //ex)
//            optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(1L, 1L, 1L));
        }
    }
}
