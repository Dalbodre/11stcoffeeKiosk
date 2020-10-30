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
    private String iconPath;
    private String name;
@Generated(hash = 1478783078)
public Ingredient(Long id, @NotNull String iconPath, String name) {
    this.id = id;
    this.iconPath = iconPath;
    this.name = name;
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
public String getIconPath() {
    return this.iconPath;
}
public void setIconPath(String iconPath) {
    this.iconPath = iconPath;
}
public String getName() {
    return this.name;
}
public void setName(String name) {
    this.name = name;
}
}
