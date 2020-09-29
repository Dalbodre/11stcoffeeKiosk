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

public class ItemList extends ListFragment {

    Menu[] INDEX_MENU;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 메뉴 인덱스 구성
        INDEX_MENU = new Menu[]{
                new Menu("아메리카노",
                        ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null),
                        new int[]{1000, 2000},
                        new Ingredient[]{
                                new Ingredient("커피", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                        }),
                new Menu("라떼",
                    ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null),
                    new int[]{1000, 2000},
                    new Ingredient[]{
                            new Ingredient("커피", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                            new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                            new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                            new Ingredient("sss", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                            new Ingredient("zzz", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                            new Ingredient("물", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                    }),
                new Menu("카푸치노",
                        ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null),
                        new int[]{1000, 2000},
                        new Ingredient[]{
                                new Ingredient("커피", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null)),
                                new Ingredient("우유", ResourcesCompat.getDrawable(getResources(), R.drawable.apollo1, null))
                        }),
        };

        // 인덱스 데이터를 리스트에 추가
        List<Menu> list = new ArrayList<>();
        Collections.addAll(list, INDEX_MENU);

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
