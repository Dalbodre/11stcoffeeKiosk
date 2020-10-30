package sb.yoon.kiosk;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import sb.yoon.kiosk.controller.KioskListAdapter;
import sb.yoon.kiosk.model.Menu;

import java.util.ArrayList;
import java.util.List;

public class ItemListFragment extends ListFragment {

    List<Menu> menuList;

    public ItemListFragment(List<Menu> menuList) {
        this.menuList = menuList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 인덱스 표시 어댑터 설정
        KioskListAdapter adapter = new KioskListAdapter(menuList);
        // 어댑터를 설정
        setListAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        return view;
    }
}
