package sb.yoon.kiosk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.Menu;
import sb.yoon.kiosk.model.Option;
import sb.yoon.kiosk.model.OptionsAndMenuJoiner;

public class AdminAddActivity extends AppCompatActivity {

    public static boolean isAdd;
    private DbQueryController controller;

    private ImageView menuImg;
    private TextView menuName;
    private CheckBox isCold;
    private CheckBox isHot;
    private EditText price;
    private CheckBox shot;
    private CheckBox sugar;
    private CheckBox hazelnut;
    private CheckBox mild;
    private CheckBox tumblerFlag;
    private Spinner spinner;

    private Long menuId;
    private Long categoryId;
    private Boolean categoryAddflag;
    private Boolean isTumbler;
    private HashMap<String, Long> categoryNames;
    private String categoryNameInDb;
    private List<Category> categories;

    private Long lastMenuIdx;
    private Long lastCategoryIdx;
    private Long lastOptionAndMenuJoinerIdx;
    private Long MOJoinerIdx;

    private View divider;
    private TextView textview;
    private TextView categoryText;

    private String iconPath;
    private final int GALLERY_CODE = 1112;

    private Uri selectedImageUri;
    ArrayList<String> getHashMapKeys;

    //???????????????
    private Long updateMenuId;
    private Menu updateMenu;
    private Long updateCategoryId;
    private Boolean changeImg;
    private Button ok;
    private List<OptionsAndMenuJoiner> optionList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.admin_add_activity);

        KioskApplication kioskApplication = (KioskApplication)getApplication();
        controller = kioskApplication.getDbQueryController();

        lastMenuIdx = controller.getLastMenuIdx() + 1;
        lastCategoryIdx = controller.getLastCategoryIdx() + 1;
        lastOptionAndMenuJoinerIdx = controller.getLastOptionAndMenuJoinerIdx() + 1;

        menuImg = (ImageView)findViewById(R.id.imageBox);
        menuName = findViewById(R.id.menuText);
        isCold = findViewById(R.id.isCold);
        isHot = findViewById(R.id.isHot);
        price = findViewById(R.id.price);

        shot = findViewById(R.id.shot);
        sugar = findViewById(R.id.sugar);
        hazelnut = findViewById(R.id.hazelnut);
        mild = findViewById(R.id.mild);

        spinner = findViewById(R.id.spinner);
        textview = findViewById(R.id.categoryAdd);
        divider = findViewById(R.id.view);
        categoryText = findViewById(R.id.categoryText);
        ok = findViewById(R.id.Ok);

        tumblerFlag = findViewById(R.id.tumblerFlag);
        isTumbler = true;
        categoryAddflag = false;
        changeImg = false;
        //????????? ???????????? null????????? ?????? ???.
        updateMenuId = null;
        updateCategoryId = null;
        updateMenu = null;
        MOJoinerIdx = lastOptionAndMenuJoinerIdx;
        //??????????????? ?????? ????????? ????????????
        // !!!! ????????? ????????? ????????????.
        Intent intent = getIntent();
        updateMenuId = intent.getLongExtra("menuID", 999L);
        if(updateMenuId != 999L){
            isAdd = false;
            menuId = updateMenuId;
            updateMenu = controller.getMenu(updateMenuId);
            selectedImageUri = Uri.parse(updateMenu.getIconPath());
            menuImg.setImageURI(selectedImageUri);
            menuName.setText(updateMenu.getName());
            price.setText(Integer.toString(updateMenu.getPrice()));
            List<Option> updateOption = updateMenu.getOptionList();
            for(int i=0; i<updateOption.size(); i++){
                switch(updateOption.get(i).getName()){
                    case "?????????":
                        mild.setChecked(true);
                        break;
                    case "??? ??????":
                        shot.setChecked(true);
                        break;
                    case "????????????":
                        sugar.setChecked(true);
                        break;
                    case "??????????????????":
                        hazelnut.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
            optionList = controller.getOptionListInDb(updateMenuId);
            categoryId = updateMenu.getCategoryId();
            isHot.setChecked(updateMenu.getIsHot());
            isCold.setChecked(updateMenu.getIsCold());
        }

        categories = controller.getCategoriesList();
        categoryNames = new LinkedHashMap<String, Long>();
        categoryNames.put("???????????? ??????", 0L);

        for(Category category : categories){
            categoryNames.put(category.getName(), category.getId());
        }

        ArrayAdapter<String> spinItems = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);
        spinItems.addAll(categoryNames.keySet());

        spinner.setAdapter(spinItems);
        if(categoryId != null) {
            int position = spinItems.getPosition(getKey(categoryNames, categoryId));
            spinner.setSelection(position);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //???????????? ????????? ??????????????? ???
                if(i == 0){
                    categoryAddflag = true;
                    textview.setVisibility(View.VISIBLE);
                    divider.setVisibility(View.VISIBLE);
                    categoryText.setVisibility(View.VISIBLE);
                    tumblerFlag.setVisibility(View.VISIBLE);
                    categoryId = lastCategoryIdx;
                    Log.d("lastIdx", String.valueOf(lastCategoryIdx));
                }
                else{
                    categoryAddflag = false;
                    textview.setVisibility(View.GONE);
                    divider.setVisibility(View.GONE);
                    categoryText.setVisibility(View.GONE);
                    categoryText.setText("");
                    //Todo DB ????????? ?????? ??????.
                    //categoryId = categories.get(i-1).getId();
                    categoryId = categoryNames.get(spinItems.getItem(i));
                    Log.d("spinner", spinItems.getItem(i));
                    tumblerFlag.setChecked(categories.get(i-1).getTumblerFlag());
                    Log.d("spinner Id", String.valueOf(l));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        menuImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                changeImg = true;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent, GALLERY_CODE);
            }
        });
        iconPath = "0"; //Todo ????????????. ?????? ????????? ???????????? ??????????????? ????????? ???????????? ????????? getIconPath?????? ??????.

        if(isAdd) {
            Log.d("flag", "????????????");
            menuId = lastMenuIdx;
            ok.setText("??????");
        }
        else{
            Log.d("flag", "????????????");
            ok.setText("??????");
        }

        //????????? ?????? ??????(Todo)

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data.getData() != null){
            selectedImageUri = data.getData();
            menuImg.setImageURI(selectedImageUri);
            //iconPath = selectedImageUri.toString();
            System.out.println(iconPath);

        }
    }
    public String getPath(Uri uri){
        String[] projection = {MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String sourcePath = cursor.getString(column_idx);
        //???????????????
        String filepath[] = sourcePath.split("/");
        String filename = filepath[filepath.length-1];
        System.out.println(filename);
        //????????? ???????????? ??????
        try {
            File sd = new File(sourcePath);
            String destPath = "/storage/emulated/0/DCIM/kiosk_images/"+filename;
            File data = new File(destPath);

            String state = Environment.getExternalStorageState();
            if(state.equals(Environment.MEDIA_MOUNTED)){
                FileChannel src = new FileInputStream(sd).getChannel();
                FileChannel dest = new FileOutputStream(data).getChannel();
                src.transferTo(0, src.size(), dest);
                src.close();
                dest.close();
            }
            else{
                throw new Exception("SD????????? ???????????? ??????.");
            }
            return destPath;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public <K, V> K getKey(Map<K,V> map, V value){
        for(K key : map.keySet()){
            if(value.equals(map.get(key))){
                return key;
            }
        }
        return null;
    }

    public void adminAction(View view) {
        switch (view.getId()) {
            case R.id.Ok:
                //Todo ?????? ?????? ?????? ????????????????????????? ???????????? ?????????. AlertDialog ??????.
                //???????????? ?????????
                //???????????? ????????? ??????(???????????? ?????????)
                if(changeImg) {
                    if((iconPath = getPath(selectedImageUri)) == null) {
                        iconPath="empty_img";
                    }
                    else{
                        iconPath = getPath(selectedImageUri);
                    }
                }
                else if(!changeImg && updateMenu != null){
                    iconPath = updateMenu.getIconPath();
                }
                else{
                    iconPath = "empty_img";
                }
                if(isAdd) {
                    if (!menuName.getText().toString().equals("") &&
                            !price.getText().toString().equals("") &&
                            categoryAddflag &&
                            !categoryText.getText().toString().equals("")) {
                        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(AdminAddActivity.this)
                                .setMessage("?????????????????????????")
                                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        controller.menuDao.insertOrReplace(new Menu(
                                                menuId,
                                                menuName.getText().toString(),
                                                categoryId,
                                                Integer.parseInt(price.getText().toString()),
                                                iconPath,
                                                isHot.isChecked(),
                                                isCold.isChecked()));
                                        controller.categoryDao.insertOrReplace(new Category(categoryId, categoryText.getText().toString(), tumblerFlag.isChecked()));

                                        //??????
                                        if (shot.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 1L));
                                            MOJoinerIdx++;
                                        }
                                        if (sugar.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 2L));
                                            MOJoinerIdx++;
                                        }
                                        if (hazelnut.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 3L));
                                            MOJoinerIdx++;
                                        }
                                        if (mild.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 4L));
                                            MOJoinerIdx++;
                                        }
                                        isAdd = false;
                                        Intent intent = new Intent(view.getContext(), AdminActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finishAffinity();
                                    }
                                })
                                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog msgDlg = msgBuilder.create();
                        msgDlg.show();
                    } else if (!menuName.getText().toString().equals("") && !price.getText().toString().equals("")
                            && categoryAddflag && categoryText.getText().toString().equals("")) {
                        Log.d("check", "???????????? ???.");
                        //???????????? ????????? ???????????? ???????????? ????????? ??????.
                        Toast.makeText(this, "????????? ???????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    } else if (!menuName.getText().toString().equals("") && !price.getText().toString().equals("") && !categoryAddflag) {
                        Log.d("check", "?????? ??????????????? ??????");
                        //???????????? ????????? ?????? ????????? ??????????????? ??????.
                        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(AdminAddActivity.this)
                                .setMessage("?????????????????????????")
                                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        controller.menuDao.insertOrReplace(new Menu(
                                                menuId,
                                                menuName.getText().toString(),
                                                categoryId,
                                                Integer.parseInt(price.getText().toString()),
                                                iconPath,
                                                isHot.isChecked(),
                                                isCold.isChecked()));
                                        //??????
                                        if (shot.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 1L));
                                            MOJoinerIdx++;
                                        }
                                        if (sugar.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 2L));
                                            MOJoinerIdx++;
                                        }
                                        if (hazelnut.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 3L));
                                            MOJoinerIdx++;
                                        }
                                        if (mild.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 4L));
                                            MOJoinerIdx++;
                                        }
                                        isAdd = false;
                                        //finish();
                                        Intent intent = new Intent(view.getContext(), AdminActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finishAffinity();
                                    }
                                })
                                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog msgDlg = msgBuilder.create();
                        msgDlg.show();


                    } else {
                        Log.d("check", "?????? ???");
                        Toast.makeText(this, "??? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                //????????? ??????
                else{
                    if (!menuName.getText().toString().equals("") &&
                            !price.getText().toString().equals("") &&
                            categoryAddflag &&
                            !categoryText.getText().toString().equals("")) {
                        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(AdminAddActivity.this)
                                .setMessage("?????????????????????????")
                                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        controller.menuDao.insertOrReplace(new Menu(
                                                menuId,
                                                menuName.getText().toString(),
                                                categoryId,
                                                Integer.parseInt(price.getText().toString()),
                                                iconPath,
                                                isHot.isChecked(),
                                                isCold.isChecked()));
                                        controller.categoryDao.insertOrReplace(new Category(categoryId, categoryText.getText().toString(), tumblerFlag.isChecked()));
                                        Log.d("categoryId", String.valueOf(categoryId));
                                        for (int j = 0; j < 4; j++) {
                                            if (j >= optionList.size()) {
                                                break;
                                            }
                                            controller.optionsAndMenuJoinerDao.deleteByKey(optionList.get(j).getId());
                                        }
                                        if (shot.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 1L));
                                            MOJoinerIdx++;
                                        } else {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, null, null));
                                            MOJoinerIdx++;
                                        }
                                        if (sugar.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 2L));
                                            MOJoinerIdx++;
                                        } else {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, null, null));
                                            MOJoinerIdx++;
                                        }
                                        if (hazelnut.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 3L));
                                            MOJoinerIdx++;
                                        } else {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, null, null));
                                            MOJoinerIdx++;
                                        }
                                        if (mild.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 4L));
                                            MOJoinerIdx++;
                                        } else {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, null, null));
                                            MOJoinerIdx++;
                                        }
                                        isAdd = false;
                                        Intent intent = new Intent(view.getContext(), AdminActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finishAffinity();
                                    }
                                })
                                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                        AlertDialog msgDlg = msgBuilder.create();

                        msgDlg.show();
                    }
                    else if (!menuName.getText().toString().equals("") && !price.getText().toString().equals("") && !categoryAddflag) {
                        //?????? 2
                        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(AdminAddActivity.this)
                                .setMessage("?????????????????????????")
                                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        controller.menuDao.insertOrReplace(new Menu(
                                                menuId,
                                                menuName.getText().toString(),
                                                categoryId,
                                                Integer.parseInt(price.getText().toString()),
                                                iconPath,
                                                isHot.isChecked(),
                                                isCold.isChecked()));
                                        Log.d("categoryId", String.valueOf(categoryId));
                                        for (int j = 0; j < 4; j++) {
                                            if (j >= optionList.size()) {
                                                break;
                                            }
                                            controller.optionsAndMenuJoinerDao.deleteByKey(optionList.get(j).getId());
                                        }
                                        if (shot.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 1L));
                                            MOJoinerIdx++;
                                        }

                                        if (sugar.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 2L));
                                            MOJoinerIdx++;
                                        }
                                        if (hazelnut.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 3L));
                                            MOJoinerIdx++;
                                        }
                                        if (mild.isChecked()) {
                                            controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 4L));
                                            MOJoinerIdx++;
                                        }

                                        isAdd = false;
                                        Intent intent = new Intent(view.getContext(), AdminActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finishAffinity();
                                    }
                                })
                                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog msgDlg = msgBuilder.create();
                        msgDlg.show();
                    }
                    else if (!menuName.getText().toString().equals("") && !price.getText().toString().equals("")
                            && categoryId == lastCategoryIdx && categoryText.getText().toString().equals("")) {
                        Log.d("check", "???????????? ???.");
                        //???????????? ????????? ???????????? ???????????? ????????? ??????.
                        Toast.makeText(this, "????????? ???????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Log.d("check", "?????? ???");
                        Toast.makeText(this, "??? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            case R.id.cancel:
                //Todo ?????? ?????? ?????? ?????? ???????????? ????????? ???????????????. ????????????????????????? ???????????? ?????????.
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(AdminAddActivity.this)
                        .setMessage("????????? ?????????????????????????\n?????? ???????????? ????????? ???????????????.")
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog msgDlg = msgBuilder.create();
                msgDlg.show();
                break;
        }
    }
}
