package sb.yoon.kiosk2.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(indexes = {
        @Index(value = "name ASC", unique = true)
})
public class Category {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String name;

    @ToMany(referencedJoinProperty = "categoryId")
    private List<Menu> menuList;

    @NotNull
    private Boolean tumblerFlag;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

/** Used for active entity operations. */
@Generated(hash = 40161530)
private transient CategoryDao myDao;

@Generated(hash = 1352811361)
public Category(Long id, @NotNull String name, @NotNull Boolean tumblerFlag) {
    this.id = id;
    this.name = name;
    this.tumblerFlag = tumblerFlag;
}

@Generated(hash = 1150634039)
public Category() {
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

public Boolean getTumblerFlag() {
    return this.tumblerFlag;
}

public void setTumblerFlag(Boolean tumblerFlag) {
    this.tumblerFlag = tumblerFlag;
}

/**
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 639885653)
public List<Menu> getMenuList() {
    if (menuList == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        MenuDao targetDao = daoSession.getMenuDao();
        List<Menu> menuListNew = targetDao._queryCategory_MenuList(id);
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
@Generated(hash = 503476761)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getCategoryDao() : null;
}
}
