package sb.yoon.kiosk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.model.AdminMenu;
import sb.yoon.kiosk.model.Category;
import sb.yoon.kiosk.model.Menu;
import sb.yoon.kiosk.model.OptionsAndMenuJoiner;

public class AdminAddActivity extends AppCompatActivity {
    public static AdminMenu adminMenu;
    public static boolean isAdd;
    private DbQueryController controller;

    private ImageView menuImg;
    private TextView menuName;
    private CheckBox isCold;
    private CheckBox isHot;
    private TextView price;
    private CheckBox shot;
    private CheckBox sugar;
    private CheckBox hazelnut;
    private CheckBox mild;

    private Spinner spinner;

    private Long menuId;
    private Long categoryId;
    private String categoryName;

    private List<String> categoryNames;
    private List<Category> categories;

    private Long lastMenuIdx;
    private Long lastCategoryIdx;
    private Long lastOptionAndMenuJoinerIdx;

    private View divider;
    private TextView textview;
    private TextView categoryText;

    private String iconPath;
    private final int GALLERY_CODE = 1112;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.admin_add_activity);

        KioskApplication kioskApplication = (KioskApplication)getApplication();
        controller = kioskApplication.getDbQueryController();

        categories = controller.getCategoriesList();
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

        categoryNames = new ArrayList<>();
        categoryNames.add("카테고리 추가");

        for(Category category : categories){
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> spinItems = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categoryNames);
        spinner.setAdapter(spinItems);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //카테고리 추가가 선택되었을 때
                if(i == 0){
                    textview.setVisibility(View.VISIBLE);
                    divider.setVisibility(View.VISIBLE);
                    categoryText.setVisibility(View.VISIBLE);
                    categoryId = lastCategoryIdx;
                }
                else{
                    textview.setVisibility(View.GONE);
                    divider.setVisibility(View.GONE);
                    categoryText.setVisibility(View.GONE);
                    categoryText.setText("");
                    //Todo DB 고치기 작업 필요.
                    categoryId = l-1;
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
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent, GALLERY_CODE);
            }
        });
        iconPath = "0"; //Todo 고쳐야됨. 아마 이미지 선택하는 인텐트에서 이미지 선택하면 거기서 getIconPath하면 될듯.

        if(isAdd) {
            Log.d("flag", "추가모드");
            menuId = lastMenuIdx;
        }
        //이미지 추가 팝업(Todo)

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data.getData() != null){
            System.out.println(data.getData());
            iconPath = getPath(data.getData());
            //Uri selectedImageUri = data.getData();
            //menuImg.setImageURI(selectedImageUri);
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
        //정규표현식
        String filepath[] = sourcePath.split("/");
        String filename = filepath[filepath.length-1];
        System.out.println(filename);
        //내부에 저장하는 코드
        try {
            System.out.println("시작합니다.");
            File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
            File data = Environment.getDataDirectory();
            System.out.println("패스 :: " + sd.toString() + " " + data.toString());
            System.out.println("패스2 :: " + this.getExternalFilesDir("absolute").toString() + " " + this.getExternalFilesDirs("absolute").toString());
            if(sd.canWrite()){
                String destPath = "/storage/emulated/0/DCIM/Camera/"+filename;
                File source = new File(sd, sourcePath);
                File destination = new File(data, destPath);
                System.out.println("source.exists() go");
                if(source.exists()){
                    System.out.println("source.exists() True");
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dest = new FileInputStream(destination).getChannel();
                    dest.transferFrom(src, 0, src.size());
                    src.close();
                    dest.close();
                }
                return destPath;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void adminAction(View view) {
        switch (view.getId()) {
            case R.id.Ok:
                //Todo 원래 팝업 열고 추가하시겠습니까? 출력하고 해야됨. AlertDialog 이용.
                //카테고리 추가시
                if (!menuName.getText().toString().equals("") &&
                        !price.getText().toString().equals("") &&
                        categoryId == lastCategoryIdx &&
                        !categoryText.getText().toString().equals("")) {

                    Log.d("check", "카테고리 추가 정상적");
                    Log.d("check", menuName.getText().toString());
                    /*controller.menuDao.insertOrReplace(new Menu(
                            menuId,
                            menuName.getText().toString(),
                            categoryId,
                            Integer.parseInt(price.getText().toString()),
                            iconPath,
                            isHot.isChecked(),
                            isCold.isChecked()));
                    controller.categoryDao.insertOrReplace(new Category(categoryId, categoryText.getText().toString()));

                    //옵션
                    if(shot.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(lastOptionAndMenuJoinerIdx, menuId, 1L));
                        lastOptionAndMenuJoinerIdx++;
                    }
                    if(sugar.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(lastOptionAndMenuJoinerIdx, menuId, 2L));
                        lastOptionAndMenuJoinerIdx++;
                    }
                    if(hazelnut.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(lastOptionAndMenuJoinerIdx, menuId, 3L));
                        lastOptionAndMenuJoinerIdx++;
                    }
                    if(mild.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(lastOptionAndMenuJoinerIdx, menuId, 4L));
                        lastOptionAndMenuJoinerIdx++;
                    }*/
                    finish();
                } else if (!menuName.getText().toString().equals("") && !price.getText().toString().equals("")
                        && categoryId == lastCategoryIdx && categoryText.getText().toString().equals("")) {
                    Log.d("check", "카테고리 빔.");
                    //카테고리 추가를 선택하고 텍스트가 비었을 경우.
                    Toast.makeText(this, "추가할 카테고리 이름을 입력해주세요.", Toast.LENGTH_LONG);
                } else if (!menuName.getText().toString().equals("") && !price.getText().toString().equals("") && categoryId != lastCategoryIdx) {
                    Log.d("check", "기존 카테고리내 추가");
                    /*//카테고리 추가가 아닌 기존의 카테고리일 경우.
                    controller.menuDao.insertOrReplace(new Menu(
                            menuId,
                            menuName.getText().toString(),
                            categoryId,
                            Integer.parseInt(price.getText().toString()),
                            iconPath,
                            isHot.isChecked(),
                            isCold.isChecked()));
                    //옵션
                    if(shot.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(lastOptionAndMenuJoinerIdx, menuId, 1L));
                        lastOptionAndMenuJoinerIdx++;
                    }
                    if(sugar.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(lastOptionAndMenuJoinerIdx, menuId, 2L));
                        lastOptionAndMenuJoinerIdx++;
                    }
                    if(hazelnut.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(lastOptionAndMenuJoinerIdx, menuId, 3L));
                        lastOptionAndMenuJoinerIdx++;
                    }
                    if(mild.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(lastOptionAndMenuJoinerIdx, menuId, 4L));
                        lastOptionAndMenuJoinerIdx++;
                    }*/
                    finish();
                } else {
                    Log.d("check", "내용 빔");
                    Toast.makeText(this.getApplicationContext(), "빈 곳을 채워주세요.", Toast.LENGTH_LONG);
                }
                break;
            case R.id.cancel:
                //Todo 원래 팝업 열고 현재 작성중인 내용이 사라집니다. 취소하시겠습니까? 출력하고 해야됨.
                finish();
                break;
        }
    }
}
