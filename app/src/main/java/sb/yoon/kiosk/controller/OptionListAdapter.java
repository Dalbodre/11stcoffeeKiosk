package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.R;
import sb.yoon.kiosk.model.CartOption;

public class OptionListAdapter extends RecyclerView.Adapter<OptionListAdapter.CustomViewHolder> {
    protected List<CartOption> optionList = new ArrayList<>();

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
            TextView quantityTextView = new TextView(holder.context);
            quantityTextView.setText(Integer.toString(cartOption.getQuantity()));
            quantityTextView.setTextColor(Color.parseColor("#4b3621"));

            Button minusButton = new Button(holder.context);
            minusButton.setText("◀");
            minusButton.setTextColor(Color.parseColor("#4b3621"));
            minusButton.setTextSize(10f);
            minusButton.setOnClickListener(new OnClickQuantityButtons(cartOption, quantityTextView));

            Button plusButton = new Button(holder.context);
            plusButton.setText("▶");
            plusButton.setTextColor(Color.parseColor("#4b3621"));
            plusButton.setTextSize(10f);
            plusButton.setOnClickListener(new OnClickQuantityButtons(cartOption, quantityTextView));

            holder.optionButtonsWrapper.addView(minusButton);
            holder.optionButtonsWrapper.addView(quantityTextView);
            holder.optionButtonsWrapper.addView(plusButton);
        } else if (!cartOption.isInteger()) {
            ToggleButton button = new ToggleButton(holder.context);
            button.setText("적용 안됨");
            button.setOnClickListener(new OnClickBoolButtons(cartOption));

            holder.optionButtonsWrapper.addView(button);
        }
    }

    class OnClickQuantityButtons implements View.OnClickListener {
        private CartOption cartOption;
        private TextView quantityTextView;

        public OnClickQuantityButtons(CartOption cartOption, TextView quantityTextView) {
            this.cartOption = cartOption;
            this.quantityTextView = quantityTextView;
        }

        @Override
        public void onClick(View view) {
            Button button = (Button) view;
            Log.d("버튼텍스트", "::" + (String) button.getText());
            if (button.getText().equals("◀")) {
                int quantityNew = cartOption.getQuantity() - 1;
                if (quantityNew < 0) {
                    return;
                }
                cartOption.setQuantity(quantityNew);
                quantityTextView.setText(Integer.toString(quantityNew));
            } else if (button.getText().equals("▶")) {
                int quantityNew = cartOption.getQuantity() + 1;
                cartOption.setQuantity(quantityNew);
                quantityTextView.setText(Integer.toString(quantityNew));
            }
        }
    }

    class OnClickBoolButtons implements View.OnClickListener {
        private CartOption cartOption;

        public OnClickBoolButtons(CartOption cartOption) {
            this.cartOption = cartOption;
        }

        @Override
        public void onClick(View view) {
            ToggleButton button = (ToggleButton) view;
            if (button.isChecked()) {
                cartOption.setQuantity(0);
                button.setPressed(false);
                button.setText("적용됨");
            } else {
                cartOption.setQuantity(1);
                button.setPressed(true);
                button.setText("적용 안됨");
            };
        }
    }

    @Override
    public int getItemCount() {
        return (null != optionList ? optionList.size() : 0);
    }
}