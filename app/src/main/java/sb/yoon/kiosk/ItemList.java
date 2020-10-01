package sb.yoon.kiosk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.ListFragment;
import sb.yoon.kiosk.controller.KioskListAdapter;
import sb.yoon.kiosk.model.Menu;

import java.util.ArrayList;

public class ItemList extends ListFragment {

    ArrayList<Menu> menuList;

    public ItemList(ArrayList<Menu> menuList) {
        this.menuList = menuList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 인덱스 표시 어댑터 설정
        KioskListAdapter adapter = new KioskListAdapter(requireActivity().getApplicationContext(), 0, menuList);

        // 어댑터를 설정
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        return view;
    }
}
