package sb.yoon.kiosk;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemList extends ListFragment {

    Data[] INDEX_DATA;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INDEX_DATA = new Data[]{
                new Data("list1", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                new Data("list2", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                new Data("list3", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
        };

        // 인덱스 데이터를 리스트에 추가
        List<Data> list = new ArrayList<>();
        Collections.addAll(list, INDEX_DATA);

        // 인덱스 표시 어댑터 설정
        CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(), 0, list);

        // 어댑터를 설정
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        return view;
    }
}
