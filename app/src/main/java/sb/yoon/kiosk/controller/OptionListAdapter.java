package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.R;
import sb.yoon.kiosk.model.CartOption;

public class OptionListAdapter extends RecyclerView.Adapter<OptionListAdapter.CustomViewHolder> {
    List<CartOption> optionList = new ArrayList<>();

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView optionNameTextView;
        protected LinearLayout optionButtonsWrapper;
        protected Context context;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            optionNameTextView = itemView.findViewById(R.id.option_name_text);
            optionButtonsWrapper = itemView.findViewById(R.id.option_buttons_wrapper);
            context = itemView.getContext();
        }
    }

    public OptionListAdapter(List<CartOption> optionList) {
        this.optionList = optionList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.option_list_item, parent, false);

        return new OptionListAdapter.CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        CartOption cartOption = optionList.get(position);

        holder.optionNameTextView.setText(cartOption.getName());

        if (cartOption.isInteger()) {
            Button minusButton = new Button(holder.context);
            minusButton.setText("◀");
            minusButton.setTextColor(Color.parseColor("#4b3621"));
            minusButton.setTextSize(10f);

            TextView quantityTextView = new TextView(holder.context);
            quantityTextView.setText(Integer.toString(cartOption.getQuantity()));
            quantityTextView.setTextColor(Color.parseColor("#4b3621"));

            Button plusButton = new Button(holder.context);
            plusButton.setText("▶");
            plusButton.setTextColor(Color.parseColor("#4b3621"));
            plusButton.setTextSize(10f);

            holder.optionButtonsWrapper.addView(minusButton);
            holder.optionButtonsWrapper.addView(quantityTextView);
            holder.optionButtonsWrapper.addView(plusButton);
        } else if (!cartOption.isInteger()) {
            Button button = new Button(holder.context);
            button.setText("적용 안함");

            holder.optionButtonsWrapper.addView(button);
        }
    }

    @Override
    public int getItemCount() {
        return (null != optionList ? optionList.size() : 0);
    }
}
