package sb.yoon.kiosk.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class OptionsAndMenuJoiner {
    @Id private Long id;
    private Long menuId;
    private Long optionId;
    @Generated(hash = 1631115094)
    public OptionsAndMenuJoiner(Long id, Long menuId, Long optionId) {
        this.id = id;
        this.menuId = menuId;
        this.optionId = optionId;
    }
    @Generated(hash = 1504334399)
    public OptionsAndMenuJoiner() {
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
    public Long getOptionId() {
        return this.optionId;
    }
    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }
}
