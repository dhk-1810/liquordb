package com.liquordb.entity.liquordetail;

import com.liquordb.entity.Liquor;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("WHISKY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WhiskyDetail extends LiquorDetail {

    private String caskType;
    private Integer age;

    private WhiskyDetail(Liquor liquor, String caskType, Integer age) {
        super(null, liquor);
        this.caskType = caskType;
        this.age = age;
    }

    public static WhiskyDetail create(Liquor liquor, String caskType, Integer age) {
        return new WhiskyDetail(liquor, caskType, age);
    }

    public void update(String caskType, Integer age) {
        this.caskType = caskType;
        this.age = age;
    }
}
