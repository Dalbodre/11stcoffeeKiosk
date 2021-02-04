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
import java.util.List;
import sb.yoon.kiosk.controller.DbQueryController;
import sb.yoon.kiosk.model.AdminMenu;
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

    private Spinner spinner;

    private Long menuId;
    private Long categoryId;


    private List<String> categoryNames;
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

    //업데이트용
    private Long updateMenuId;
    private Menu updateMenu;
    private Long updateCategoryId;
    private Boolean changeImg;

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

        changeImg = false;
        //반드시 초기화시 null이어야 하는 값.
        updateMenuId = null;
        updateCategoryId = null;
        updateMenu = null;
        //인텐트에서 메뉴 아이디 불러오기
        // !!!! 재료는 손대지 않습니다.
        Intent intent = getIntent();
        updateMenuId = intent.getLongExtra("menuID", 999L);
        if(updateMenuId != 999L){
            isAdd = false;
            updateMenu = controller.getMenu(updateMenuId);
            selectedImageUri = Uri.parse(updateMenu.getIconPath());
            menuImg.setImageURI(selectedImageUri);
            menuName.setText(updateMenu.getName());
            price.setText(Integer.toString(updateMenu.getPrice()));
            List<Option> updateOption = updateMenu.getOptionList();
            for(int i=0; i<updateOption.size(); i++){
                switch(updateOption.get(i).getName()){
                    case "연하게":
                        mild.setChecked(true);
                        break;
                    case "샷 추가":
                        shot.setChecked(true);
                        break;
                    case "설탕시럽":
                        sugar.setChecked(true);
                        break;
                    case "헤이즐넛시럽":
                        hazelnut.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
            updateCategoryId = updateMenu.getCategoryId();
            isHot.setChecked(updateMenu.getIsHot());
            isCold.setChecked(updateMenu.getIsCold());
        }

        categoryNames = new ArrayList<>();
        categoryNames.add("카테고리 추가");

        for(Category category : categories){
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> spinItems = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categoryNames);
        spinner.setAdapter(spinItems);
        if(updateCategoryId != null) {
            spinner.setSelection(updateCategoryId.intValue());
        }
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
                changeImg = true;
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
            MOJoinerIdx = lastOptionAndMenuJoinerIdx;
        }
        //이미지 추가 팝업(Todo)

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
        //정규표현식
        String filepath[] = sourcePath.split("/");
        String filename = filepath[filepath.length-1];
        System.out.println(filename);
        //내부에 저장하는 코드
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
                throw new Exception("SD카드가 삽입되지 않음.");
            }
            return destPath;
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
                //이미지를 변경한 경우(수정이든 추가든)
                if(changeImg) {
                    iconPath = getPath(selectedImageUri);
                }
                else if(!changeImg && updateMenu != null){
                    iconPath = updateMenu.getIconPath();
                }
                else{
                    iconPath = "empty_img";
                }
                if (!menuName.getText().toString().equals("") &&
                        !price.getText().toString().equals("") &&
                        categoryId == lastCategoryIdx &&
                        !categoryText.getText().toString().equals("")) {

                    Log.d("check", "카테고리 추가 정상적");
                    Log.d("check", menuName.getText().toString());
                    controller.menuDao.insertOrReplace(new Menu(
                            menuId,
                            menuName.getText().toString(),
                            categoryId,
                            Integer.parseInt(price.getText().toString()),
                            iconPath,
                            isHot.isChecked(),
                            isCold.isChecked()));

                    //todo 텀플러 플래그 마지막에 확인하고 변경
                    controller.categoryDao.insertOrReplace(new Category(categoryId, categoryText.getText().toString(), false));

                    //옵션
                    if(shot.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 1L));
                        MOJoinerIdx++;
                    }
                    if(sugar.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 2L));
                        MOJoinerIdx++;
                    }
                    if(hazelnut.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 3L));
                        MOJoinerIdx++;
                    }
                    if(mild.isChecked()){
                        controller.optionsAndMenuJoinerDao.insertOrReplace(new OptionsAndMenuJoiner(MOJoinerIdx, menuId, 4L));
                        MOJoinerIdx++;
                    }
                    finish();
                } else if (!menuName.getText().toString().equals("") && !price.getText().toString().equals("")
                        && categoryId == lastCategoryIdx && categoryText.getText().toString().equals("")) {
                    Log.d("check", "카테고리 빔.");
                    //카테고리 추가를 선택하고 텍스트가 비었을 경우.
                    Toast.makeText(this, "추가할 카테고리 이름을 입력해주세요.", Toast.LENGTH_LONG).show();
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

                    //finish();
                    Intent intent = new Intent(view.getContext(), AdminActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("check", "내용 빔");
                    Toast.makeText(this.getApplicationContext(), "빈 곳을 채워주세요.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.cancel:
                //Todo 원래 팝업 열고 현재 작성중인 내용이 사라집니다. 취소하시겠습니까? 출력하고 해야됨.
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(AdminAddActivity.this)
                        .setMessage("작성을 취소하시겠습니까?\n현재 작성중인 내용이 사라집니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
