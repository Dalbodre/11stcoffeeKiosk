package sb.yoon.kiosk.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {
        @Index(value = "name DESC", unique = true)
})
public class Option {
    @Id(autoincrement = true)
    private Long id;

    // 예) 사이즈
    @NotNull
    private String name;

    // 예) true
    // 해당 옵션이 다중 선택 가능한지 지정
    @NotNull
    private Boolean multiSelectable;

    // 예) 레귤러/라지/엑스라지
    // 옵션을 나열. '/' 기호로 구분.
    @NotNull
    private String value;

@Generated(hash = 190502910)
public Option(Long id, @NotNull String name, @NotNull Boolean multiSelectable,
        @NotNull String value) {
    this.id = id;
    this.name = name;
    this.multiSelectable = multiSelectable;
    this.value = value;
}

@Generated(hash = 104107376)
public Option() {
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

public Boolean getMultiSelectable() {
    return this.multiSelectable;
}

public void setMultiSelectable(Boolean multiSelectable) {
    this.multiSelectable = multiSelectable;
}

public String getValue() {
    return this.value;
}

public void setValue(String value) {
    this.value = value;
}
}
