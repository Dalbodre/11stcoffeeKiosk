package sb.yoon.kiosk.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(indexes = {
        @Index(value = "name DESC", unique = true)
})
public class Menu implements Parcelable {
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
    @NotNull
    private int bgColor;

    @ToMany
    @JoinEntity(
            entity = IngredientsAndMenuJoiner.class,
            sourceProperty = "menuId",
            targetProperty = "ingredientId"
    )
    private List<Ingredient> ingredientList;

    @ToMany
    @JoinEntity(
            entity = OptionsAndMenuJoiner.class,
            sourceProperty = "menuId",
            targetProperty = "optionId"
    )
    private List<Option> optionList;

    private Boolean isHot;

    private Boolean isCold;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

/** Used for active entity operations. */
@Generated(hash = 1372547067)
private transient MenuDao myDao;

@Generated(hash = 1079563137)
public Menu(Long id, @NotNull String name, @NotNull Long categoryId, int price,
        @NotNull String iconPath, int bgColor, Boolean isHot, Boolean isCold) {
    this.id = id;
    this.name = name;
    this.categoryId = categoryId;
    this.price = price;
    this.iconPath = iconPath;
    this.bgColor = bgColor;
    this.isHot = isHot;
    this.isCold = isCold;
}

@Generated(hash = 1631206187)
public Menu() {
}

    protected Menu(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            categoryId = null;
        } else {
            categoryId = in.readLong();
        }
        price = in.readInt();
        iconPath = in.readString();
        bgColor = in.readInt();
        byte tmpIsHot = in.readByte();
        isHot = tmpIsHot == 0 ? null : tmpIsHot == 1;
        byte tmpIsCold = in.readByte();
        isCold = tmpIsCold == 0 ? null : tmpIsCold == 1;
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

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

public Long getCategoryId() {
    return this.categoryId;
}

public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
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

public int getBgColor() {
    return this.bgColor;
}

public void setBgColor(int bgColor) {
    this.bgColor = bgColor;
}

public Boolean getIsHot() {
    return this.isHot;
}

public void setIsHot(Boolean isHot) {
    this.isHot = isHot;
}

public Boolean getIsCold() {
    return this.isCold;
}

public void setIsCold(Boolean isCold) {
    this.isCold = isCold;
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
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 1446926842)
public List<Option> getOptionList() {
    if (optionList == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        OptionDao targetDao = daoSession.getOptionDao();
        List<Option> optionListNew = targetDao._queryMenu_OptionList(id);
        synchronized (this) {
            if (optionList == null) {
                optionList = optionListNew;
            }
        }
    }
    return optionList;
}

/** Resets a to-many relationship, making the next get call to query for a fresh result. */
@Generated(hash = 1801972530)
public synchronized void resetOptionList() {
    optionList = null;
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

@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(name);
        if (categoryId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(categoryId);
        }
        parcel.writeInt(price);
        parcel.writeString(iconPath);
        parcel.writeInt(bgColor);
        parcel.writeByte((byte) (isHot == null ? 0 : isHot ? 1 : 2));
        parcel.writeByte((byte) (isCold == null ? 0 : isCold ? 1 : 2));
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 557085301)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMenuDao() : null;
    }
}