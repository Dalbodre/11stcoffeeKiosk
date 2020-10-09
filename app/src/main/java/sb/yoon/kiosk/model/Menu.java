package sb.yoon.kiosk.model;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

// 데이터 클래스
public class Menu {
    private StorageReference imageRef = null;
    private String name = "";
    private List<Ingredient> ingredients = null;
    private List<String> prices = null;

    public Menu(String name, StorageReference imageRef, List<String> prices, List<String> ingredients) {
        this.imageRef = imageRef;
        this.name = name;
        this.prices = prices;

        for (String ingredientName :
                ingredients) {
            getIngredients()
        }
        this.ingredients = ingredients;
    }

    public Menu() {

    }

    public void setImage(StorageReference image) {
        this.imageRef = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrices(List<String> prices) {
        this.prices = prices;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public StorageReference getImage() {
        return this.imageRef;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<String> getPrices() {
        return prices;
    }
}
