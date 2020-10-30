package sb.yoon.kiosk.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

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

}
