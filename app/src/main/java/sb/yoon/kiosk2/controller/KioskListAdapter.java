package sb.yoon.kiosk2.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.content.ContextCompat;

import sb.yoon.kiosk2.KioskApplication;
import sb.yoon.kiosk2.KioskListActivity;
import sb.yoon.kiosk2.PopupActivity;
import sb.yoon.kiosk2.R;
import sb.yoon.kiosk2.layout.ItemElement;
import sb.yoon.kiosk2.layout.IngredientItem;
import sb.yoon.kiosk2.model.CartMenu;
import sb.yoon.kiosk2.model.CartOption;
import sb.yoon.kiosk2.model.Category;
import sb.yoon.kiosk2.model.Ingredient;
import sb.yoon.kiosk2.model.Menu;
import sb.yoon.kiosk2.model.Option;

import java.util.ArrayList;
import java.util.List;

// 어뎁터 클래스
public class KioskListAdapter extends BaseAdapter implements View.OnClickListener {
    // 메뉴 리스트
    private List<Menu> menuList;
    // private Context context;
    private KioskListActivity context;

    public KioskListAdapter(List<Menu> menuList, KioskListActivity mainActivity){
        this.menuList = menuList;
        this.context = mainActivity;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int i) {
        return menuList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        //return getCount();

        if(getCount() > 0) {
            return getCount();
        }
        else{
            return super.getViewTypeCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // 메뉴 한줄한줄 뷰 표현 (디스플레이)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        context = (KioskListActivity) parent.getContext();

        // 특정 행의 데이터 구함
        Menu menu = (Menu)getItem(position);

        // View는 재사용되기 때문에 처음에만 리스트 아이템 표시용 레이아웃을 읽어와서 생성함
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);
        }

        // View의 각 Widget에 데이터 저장
        // 메뉴 사진, 이름 추가
        ItemElement menuItem = convertView.findViewById(R.id.menu_element);
        Drawable drawable = ContextCompat.getDrawable(context, context.getResources()
                .getIdentifier(menu.getIconPath(), "drawable", context.getPackageName()));
        menuItem.setImageDrawable(drawable);
        menuItem.setName(menu.getName());
        menuItem.setPrice(menu.getPrice());
        menuItem.setTag(position);

        // ItemElement에 OnClickListener 달아주기
        menuItem.setOnClickListener(this);

        // 재료들 추가
        List<Ingredient> ingredients = menu.getIngredientList();
        LinearLayout holder = null;
        int index = 0;
        for (Ingredient ingredient : ingredients) {
            holder = (LinearLayout) convertView.findViewById(R.id.ingredientsHolder);

            Drawable ingredientDrawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier(ingredient.getIconPath(), "drawable", context.getPackageName()));

            IngredientItem ingredientElement = null;
            switch(index) {
                case 0: ingredientElement = holder.findViewById(R.id.ingredient1); break;
                case 1: ingredientElement = holder.findViewById(R.id.ingredient2); break;
                case 2: ingredientElement = holder.findViewById(R.id.ingredient3); break;
                case 3: ingredientElement = holder.findViewById(R.id.ingredient4); break;
                default:
                    throw new IllegalStateException("재료가 너무 많습니다: " + index);
            }
            if (ingredientElement != null) {
                ingredientElement.setImageDrawable(ingredientDrawable);
                ingredientElement.setName(ingredient.getName());

                ingredientElement.setVisibility(View.VISIBLE);

                //ㄱㅊ? 이거 필요하면 넣는걸로 합시다
                ingredientElement.setTag(position);
                ingredientElement.setOnClickListener(this);
            }

            index += 1;
        }

        return convertView;
    }

    // 메뉴 아이콘 눌렸을 때
    @Override
    public void onClick(View view) {
        if (context.menuOptionPopupButtonClicked)
            return;

        context.menuOptionPopupButtonClicked = true;

        // 리사이클뷰 (쇼핑카트)에 아이템 넣는거
        int position = (int) view.getTag();
        //context.addCartMenuList(menuList.get(position));

        // 팝업 띄우는거
        Intent intent = new Intent(context, PopupActivity.class);
        Menu menu = menuList.get(position);
        Drawable drawable = ContextCompat.getDrawable(context, context.getResources()
                .getIdentifier(menu.getIconPath(), "drawable", context.getPackageName()));
        List<Option> options = menu.getOptionList();
        List<CartOption> cartOptions = new ArrayList<>();
        for (Option option: options) {
            cartOptions.add(new CartOption(option.getName(), 0, option.getPrice(), option.getIsInteger()));
        }


        PopupActivity.cartMenu = new CartMenu(drawable, menu.getName(), menu.getPrice(), 0, cartOptions, menu.getId(), menu.getCategoryId(), menu.getIsHot(), menu.getIsCold(), null);

        // 카테고리 텀블러 가능한지 구하기
        KioskApplication kapp = (KioskApplication) context.getApplication();
        Category category = kapp.getDbQueryController().getCategoryDao().load(menu.getCategoryId());
        PopupActivity.tumblerFlag = category.getTumblerFlag();

        context.startActivityForResult(intent, 1);
    }
}