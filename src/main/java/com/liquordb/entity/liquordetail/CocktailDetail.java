package com.liquordb.entity.liquordetail;

import com.liquordb.entity.Liquor;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("COCKTAIL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CocktailDetail extends LiquorDetail {

    private String glass;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(columnDefinition = "TEXT")
    private String ingredients;

    private CocktailDetail(Liquor liquor, String glass, String instructions, String ingredients) {
        super(null, liquor);
        this.glass = glass;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public static CocktailDetail create(Liquor liquor, String glass, String instructions, String ingredients) {
        return new CocktailDetail(liquor, glass, instructions, ingredients);
    }

    public void update(String glass, String instructions, String ingredients) {
        this.glass = glass;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }
}
