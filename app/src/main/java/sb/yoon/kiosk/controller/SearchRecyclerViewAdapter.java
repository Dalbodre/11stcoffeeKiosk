package sb.yoon.kiosk.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sb.yoon.kiosk.layout.ItemElement;
import sb.yoon.kiosk.model.Ingredient;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.CustomViewHolder> {
    private List<Ingredient> ingredients = new ArrayList<>();
    private Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ItemElement itemElement;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            RecyclerView recyclerView = (RecyclerView) itemView;
            GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            gridLayoutManager.
            itemElement = new ItemElement(itemView.getContext(), null);
            gridLayout.addView(itemElement);
            context = itemView.getContext();
        }
    }

    public SearchRecyclerViewAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Drawable drawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier(ingredients.get(position).getIconPath(), "drawable", context.getPackageName()));
        holder.itemElement.setImageDrawable(drawable);
        holder.itemElement.setText(ingredients.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return (null != ingredients ? ingredients.size() : 0);
    }


}
