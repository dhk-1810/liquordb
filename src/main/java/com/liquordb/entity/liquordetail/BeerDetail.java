package com.liquordb.entity.liquordetail;

import com.liquordb.entity.Liquor;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("BEER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BeerDetail extends LiquorDetail {
    private String malts;
    private String hops;
    private Double ibu;

    private BeerDetail(Liquor liquor, String malts, String hops, Double ibu) {
        super(null, liquor);
        this.malts = malts;
        this.hops = hops;
        this.ibu = ibu;
    }

    public static BeerDetail create(Liquor liquor, String malts, String hops, Double ibu) {
        return new BeerDetail(liquor, malts, hops, ibu);
    }

    public void update(String malts, String hops, Double ibu) {
        this.malts = malts;
        this.hops = hops;
        this.ibu = ibu;
    }
}
