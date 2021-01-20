package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.R;
import sb.yoon.kiosk.model.Ingredient;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.CustomViewHolder> {
    private List<Ingredient> ingredients = new ArrayList<>();
    private Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.for_element_image);
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
        holder.imageView.setTag(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != ingredients ? ingredients.size() : 0);
    }

}
