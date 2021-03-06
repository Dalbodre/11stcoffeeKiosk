package sb.yoon.kiosk.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.DaoException;

@Entity(indexes = {
        @Index(value = "name DESC", unique = true)
})
public class Ingredient {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String iconPath;

    @ToMany
    @JoinEntity(
            entity = IngredientsAndMenuJoiner.class,
            sourceProperty = "ingredientId",
            targetProperty = "menuId"
    )
    private List<Menu> menuList;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

/** Used for active entity operations. */
@Generated(hash = 942581853)
private transient IngredientDao myDao;

@Generated(hash = 1936183127)
public Ingredient(Long id, @NotNull String name, @NotNull String iconPath) {
    this.id = id;
    this.name = name;
    this.iconPath = iconPath;
}

@Generated(hash = 1584798654)
public Ingredient() {
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

public String getIconPath() {
    return this.iconPath;
}

public void setIconPath(String iconPath) {
    this.iconPath = iconPath;
}

/**
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 754970061)
public List<Menu> getMenuList() {
    if (menuList == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        MenuDao targetDao = daoSession.getMenuDao();
        List<Menu> menuListNew = targetDao._queryIngredient_MenuList(id);
        synchronized (this) {
            if (menuList == null) {
                menuList = menuListNew;
            }
        }
    }
    return menuList;
}

/** Resets a to-many relationship, making the next get call to query for a fresh result. */
@Generated(hash = 1650202618)
public synchronized void resetMenuList() {
    menuList = null;
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

/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 1386056592)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getIngredientDao() : null;
}

}
