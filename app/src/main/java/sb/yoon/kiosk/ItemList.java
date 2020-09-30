package sb.yoon.kiosk;

import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ItemList extends ListFragment {

    Menu[] INDEX_MENU;

    public ItemList(Menu[] INDEX_MENU) {
        this.INDEX_MENU = INDEX_MENU;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 인덱스 데이터를 리스트에 추가
        List<Menu> list = new ArrayList<>();
        Collections.addAll(list, INDEX_MENU);

        // 인덱스 표시 어댑터 설정
        CustomAdapter adapter = new CustomAdapter(requireActivity().getApplicationContext(), 0, list);

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
