package sb.yoon.kiosk;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import sb.yoon.kiosk.controller.AdminGridLayoutAdapter;
import sb.yoon.kiosk.model.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

// 탭레이아웃에 표시될 플래그먼트
public class AdminTabFragment extends Fragment {
    private List<Menu> menuList;
    //private String categoryName;
    private GridView gridView;
    private AdminGridLayoutAdapter adapter;
    private Context context;

    public AdminTabFragment(List<Menu> menuList, Context context) {
        this.menuList = menuList;
        adapter = new AdminGridLayoutAdapter(this.menuList, context);
        this.context = context;
    }

    public void setMenuList(List<Menu> menuList){
        this.menuList = menuList;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(inflater.getContext()).inflate(R.layout.admin_fragment, container, false);
        gridView = (GridView)view.findViewById(R.id.grid);
        gridView.setAdapter(adapter);
        /*gridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context.getApplicationContext(), Integer.toString(position), Toast.LENGTH_LONG).show();
                System.out.println(position + " " + id);
            }
        });*/
        return view;
    }
}
