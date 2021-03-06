package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import sb.yoon.kiosk.PopupActivity;
import sb.yoon.kiosk.R;
import sb.yoon.kiosk.model.CartOption;

public class OptionListAdapter extends RecyclerView.Adapter<OptionListAdapter.CustomViewHolder> {
    protected List<CartOption> optionList = new ArrayList<>();

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView optionNameTextView;
        protected LinearLayout optionButtonsWrapper;
        protected Context context;
        protected Button minusButton;
        protected TextView quanityView;
        protected ToggleButton toggleButton;
        protected Button plusButton;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            optionNameTextView = itemView.findViewById(R.id.option_name_text);
            optionButtonsWrapper = itemView.findViewById(R.id.option_buttons_wrapper);
            minusButton = itemView.findViewById(R.id.button1);
            quanityView = itemView.findViewById(R.id.textView2);
            plusButton = itemView.findViewById(R.id.button2);
            toggleButton = itemView.findViewById(R.id.toggleButton);
            context = itemView.getContext();

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/gmarket_medium.ttf");
            optionNameTextView.setTypeface(typeface);
            minusButton.setTypeface(typeface);
            quanityView.setTypeface(typeface);
            plusButton.setTypeface(typeface);
            toggleButton.setTypeface(typeface);
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

        holder.optionNameTextView.setText(cartOption.getName() + " (??????: " + cartOption.getPrice() + ")");

        if (cartOption.isInteger()) {
            TextView quantityTextView = holder.quanityView;
            quantityTextView.setText(Integer.toString(cartOption.getQuantity()));
            //quantityTextView.setTextSize(60f);
            quantityTextView.setPadding(10, 0,10,0);
            quantityTextView.setTextColor(Color.parseColor("#081832"));

            Button minusButton = holder.minusButton;
            minusButton.setText("<");
            minusButton.setTextColor(Color.parseColor("#081832"));
            //minusButton.setTextSize(60f);
            minusButton.setBackground(ContextCompat.getDrawable(holder.context, R.drawable.for_option_background));
            minusButton.setOnClickListener(new OnClickQuantityButtons(cartOption, quantityTextView, minusButton));

            /*if(quantityTextView.getText().equals("0")){
                minusButton.setVisibility(Button.GONE);
            }
            else{
                minusButton.setVisibility(Button.VISIBLE);
            }*/

            Button plusButton = holder.plusButton;
            plusButton.setText(">");
            plusButton.setTextColor(Color.parseColor("#081832"));
            //plusButton.setTextSize(60f);
            plusButton.setBackground(ContextCompat.getDrawable(holder.context, R.drawable.for_option_background));
            plusButton.setOnClickListener(new OnClickQuantityButtons(cartOption, quantityTextView, minusButton));

//            holder.optionButtonsWrapper.addView(minusButton);
//            holder.optionButtonsWrapper.addView(quantityTextView);
//            holder.optionButtonsWrapper.addView(plusButton);
            //minusButton.setVisibility(View.VISIBLE);
            quantityTextView.setVisibility(View.VISIBLE);
            plusButton.setVisibility(View.VISIBLE);
        } else if (!cartOption.isInteger()) {
            ToggleButton button = holder.toggleButton;
            button.setChecked(false);
            button.setBackgroundDrawable(ContextCompat.getDrawable(holder.context, R.drawable.togglebutton_off));
            button.setTextColor(ContextCompat.getColor(holder.context, R.color.color11thBlue));
            button.setText("????????????");
            button.setPadding(10,10,10,10);
            //button.setTextSize(60f);
            button.setOnClickListener(new OnClickBoolButtons(cartOption, holder.context));

            button.setVisibility(View.VISIBLE);
        }
    }

    class OnClickQuantityButtons implements View.OnClickListener {
        private final Button minusButton;
        private CartOption cartOption;
        private TextView quantityTextView;

        public OnClickQuantityButtons(CartOption cartOption, TextView quantityTextView, Button minusButton) {
            this.cartOption = cartOption;
            this.quantityTextView = quantityTextView;
            this.minusButton = minusButton;
        }

        @Override
        public void onClick(View view) {
            Button button = (Button) view;
            Log.d("???????????????", "::" + (String) button.getText());
            if (button.getText().equals("<")) {
                int quantityNew = cartOption.getQuantity() - 1;
                if (quantityNew <= 0) {
                    minusButton.setVisibility(View.GONE);
                }
                cartOption.setQuantity(quantityNew);
                quantityTextView.setText(Integer.toString(quantityNew));
            } else if (button.getText().equals(">")) {
                int quantityNew = cartOption.getQuantity() + 1;
                if (quantityNew >= 1) {
                    minusButton.setVisibility(View.VISIBLE);
                }
                cartOption.setQuantity(quantityNew);
                quantityTextView.setText(Integer.toString(quantityNew));
            }
        }
    }

    class OnClickBoolButtons implements View.OnClickListener {
        private CartOption cartOption;
        private Context context;
        private int quantity;

        public OnClickBoolButtons(CartOption cartOption, Context context) {
            this.cartOption = cartOption;
            this.context = context;
            this.quantity = cartOption.getQuantity();
        }

        @Override
        public void onClick(View view) {
            ToggleButton button = (ToggleButton) view;
            button.setTextColor(Color.parseColor("#081832"));
            if (!button.isChecked()) {
                cartOption.setQuantity(0);
                int quantity = this.quantity;
                Log.d("shit", Integer.toString(quantity));
                button.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.togglebutton_off));
                button.setChecked(false);
                button.setText("????????????");
                //button.setTextSize(60f);
                button.setTextColor(Color.parseColor("#081832"));
                button.setPadding(10,10,10,10);
            } else {
                cartOption.setQuantity(1);
                button.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.togglebutton_on));
                button.setPressed(true);
                button.setText("?????????");
                //button.setTextSize(60f);
                button.setTextColor(Color.parseColor("#ffffff"));
                button.setPadding(10,10,10,10);
            };
        }
    }

    @Override
    public int getItemCount() {
        return (null != optionList ? optionList.size() : 0);
    }
}
