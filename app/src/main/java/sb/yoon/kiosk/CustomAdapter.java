package sb.yoon.kiosk;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

// 데이터 클래스
class Data {
    private Drawable icon;
    private String text;

    Data(String text, Drawable icon){
        this.icon = icon;
        this.text = text;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public void setText(String text) {
        this.text = text;
    }

    public Drawable getIcon() {
        return this.icon;
    }
    public String getText() {
        return this.text;
    }
}

// 어뎁터 클래스
public class CustomAdapter extends ArrayAdapter<Data> {
    private LayoutInflater layoutInflater;

    public CustomAdapter(Context context, int textViewResourceId, List<Data> objects){
        super(context, textViewResourceId, objects);
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // 특정 행의 데이터 구함
        Data data = (Data)getItem(position);

        // View는 재사용되기 때문에 처음에만 리스트 아이템 표시용 레이아웃을 읽어와서 생성함
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        // View의 각 Widget에 데이터 저장
        ImageView imageView;
        imageView = (ImageView)convertView.findViewById(R.id.img);
        imageView.setImageDrawable(data.getIcon());

        TextView textView;
        textView = (TextView)convertView.findViewById(R.id.text);
        textView.setText(data.getText());

        return convertView;
    }
}
