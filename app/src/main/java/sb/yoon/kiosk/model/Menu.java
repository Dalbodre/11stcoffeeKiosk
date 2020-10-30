package sb.yoon.kiosk.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(indexes = {
        @Index(value = "name DESC", unique = true)
})
public class Menu {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Long categoryId;

    @NotNull
    private int price;
    @NotNull
    private String iconPath;

    @ToMany
    @JoinEntity(
            entity = IngredientsAndMenuJoiner.class,
            sourceProperty = "menuId",
            targetProperty = "ingredientId"
    )
    private List<Ingredient> ingredientList;

    private Boolean isHot;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

/** Used for active entity operations. */
@Generated(hash = 1372547067)
private transient MenuDao myDao;

@Generated(hash = 2114746110)
public Menu(Long id, @NotNull String name, @NotNull Long categoryId, int price,
        @NotNull String iconPath, Boolean isHot) {
    this.id = id;
    this.name = name;
    this.categoryId = categoryId;
    this.price = price;
    this.iconPath = iconPath;
    this.isHot = isHot;
}

@Generated(hash = 1631206187)
public Menu() {
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public String getName() {
    return this.name;
}

public void setName(String name) {
    this.name = name;
}

public int getPrice() {
    return this.price;
}

public void setPrice(int price) {
    this.price = price;
}

public String getIconPath() {
    return this.iconPath;
}

public void setIconPath(String iconPath) {
    this.iconPath = iconPath;
}

public Boolean getIsHot() {
    return this.isHot;
}

public void setIsHot(Boolean isHot) {
    this.isHot = isHot;
}

/**
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 1704633988)
public List<Ingredient> getIngredientList() {
    if (ingredientList == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        IngredientDao targetDao = daoSession.getIngredientDao();
        List<Ingredient> ingredientListNew = targetDao
                ._queryMenu_IngredientList(id);
        synchronized (this) {
            if (ingredientList == null) {
                ingredientList = ingredientListNew;
            }
        }
    }
    return ingredientList;
}

/** Resets a to-many relationship, making the next get call to query for a fresh result. */
@Generated(hash = 29217069)
public synchronized void resetIngredientList() {
    ingredientList = null;
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 128553479)
public void delete() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.delete(this);
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 1942392019)
public void refresh() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.refresh(this);
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 713229351)
public void update() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.update(this);
}

public Long getCategoryId() {
    return this.categoryId;
}

public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
}

/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 557085301)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getMenuDao() : null;
}

}
