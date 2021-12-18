package sb.yoon.kiosk2.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class IngredientsAndMenuJoiner {
    @Id private Long id;
    private Long menuId;
    private Long ingredientId;
    @Generated(hash = 704970105)
    public IngredientsAndMenuJoiner(Long id, Long menuId, Long ingredientId) {
        this.id = id;
        this.menuId = menuId;
        this.ingredientId = ingredientId;
    }
    @Generated(hash = 766328806)
    public IngredientsAndMenuJoiner() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getMenuId() {
        return this.menuId;
    }
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }
    public Long getIngredientId() {
        return this.ingredientId;
    }
    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }
}
