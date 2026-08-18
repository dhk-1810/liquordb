package com.liquordb.entity.liquordetail;

import com.liquordb.entity.Liquor;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("WINE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WineDetail extends LiquorDetail {

    private String grapeVariety;
    private Integer vintage;
    private String region;

    private WineDetail(Liquor liquor, String grapeVariety, Integer vintage, String region) {
        super(null, liquor);
        this.grapeVariety = grapeVariety;
        this.vintage = vintage;
        this.region = region;
    }

    public static WineDetail create(Liquor liquor, String grapeVariety, Integer vintage, String region) {
        return new WineDetail(liquor, grapeVariety, vintage, region);
    }

    public void update(String grapeVariety, Integer vintage, String region) {
        this.grapeVariety = grapeVariety;
        this.vintage = vintage;
        this.region = region;
    }
}
