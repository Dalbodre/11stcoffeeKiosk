package sb.yoon.kiosk;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import sb.yoon.kiosk.controller.AdminButtonAdapter;
import sb.yoon.kiosk.controller.AdminFragmentAdapter;
import sb.yoon.kiosk.model.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;


public class admin_tab extends Fragment {
    private List<Menu> menuList;
    //private String categoryName;
    GridView gridView;
    AdminButtonAdapter adapter;

    public static admin_tab newInstance(int position){
        admin_tab at = new admin_tab();
        return at;
    }
    public void setMenuList(List<Menu> menuList){this.menuList = menuList;}
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(inflater.getContext()).inflate(R.layout.admin_fragment, container, false);
        gridView = (GridView)view.findViewById(R.id.grid);

        /*
        Todo
        멍청돋는 저는 gridView로 menuList를 들고 오지 못하겠습니다 ㅅㅂ..
         */
       /* Bundle bundle = getArguments();
        menuList = bundle.getParcelableArrayList("list");*/

        //adapter = new AdminButtonAdapter(menuList, (AdminActivity)getActivity());
       // Log.d("menuList", menuList.get(0).getName());
        gridView.setAdapter(adapter);
        return view;
    }
}
