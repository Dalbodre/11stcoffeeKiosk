package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.R;
import sb.yoon.kiosk.SearchActivity;
import sb.yoon.kiosk.layout.ItemElement;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.Menu;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.CustomViewHolder> {
    private List<Ingredient> ingredients = new ArrayList<>();
    private Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.element_image);
            textView = itemView.findViewById(R.id.element_text);
            context = itemView.getContext();
        }
    }

    public SearchRecyclerViewAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_ingredients_list_element, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Drawable drawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier(ingredients.get(position).getIconPath(), "drawable", context.getPackageName()));
        holder.imageView.setImageDrawable(drawable);
        holder.textView.setText(ingredients.get(position).getName());
        holder.imageView.setTag(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != ingredients ? ingredients.size() : 0);
    }

}
