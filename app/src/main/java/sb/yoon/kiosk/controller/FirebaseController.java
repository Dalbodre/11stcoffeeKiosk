package sb.yoon.kiosk.controller;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import sb.yoon.kiosk.model.Ingredient;
import sb.yoon.kiosk.model.Menu;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseController {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private List<DocumentSnapshot> categories;
    private List<DocumentSnapshot> menuList;
    private HashMap<String, String> categoryNames = new HashMap<>();

    public FirebaseController() {
    }

    public HashMap<String, String> fetchCategoryNames() {
        db.collection("cafe").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                categories = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot category : categories) {
                    categoryNames.put(category.getId(), category.getString("name"));
                }
            }
        });

        return categoryNames;
    }

    public ArrayList<Menu> getMenuList(String category) {
        final ArrayList<Menu> menus = new ArrayList<>();

        db.collection("cafe").document(category).collection("menus").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                menuList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot menu : menuList) {
                    String name = menu.getString("name");

                    ArrayList<String> prices = (ArrayList<String>) menu.get("prices");
                    ArrayList<DocumentReference> ingredients = (ArrayList<DocumentReference>) menu.get("ingredients");
                    //menus.add(new Menu(menu.getString("name"), null, prices.toArray(new String[2]), ingredients));
                }
            }
        });
        return menus;
    }
}
