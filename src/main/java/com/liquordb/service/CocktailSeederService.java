package com.liquordb.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.enums.Country;
import com.liquordb.enums.LiquorCategory;
import com.liquordb.repository.liquor.LiquorRepository;
import com.liquordb.repository.liquor.LiquorSubcategoryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CocktailSeederService {

    private final LiquorRepository liquorRepository;
    private final LiquorSubcategoryRepository liquorSubcategoryRepository;
    private final RestClient restClient = RestClient.create();

    private static final String BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/search.php?f=";

    @Async
    public void seedCocktailsAsync() {
        log.info("[CocktailSeeder] TheCocktailDB 수동 데이터 수집 및 DB 추가 작업을 시작합니다.");
        int totalAdded = 0;
        int totalSkipped = 0;

        for (char c = 'a'; c <= 'z'; c++) {
            String url = BASE_URL + c;
            log.info("[CocktailSeeder] '{}' 글자로 시작하는 칵테일을 조회 중... ({})", c, url);

            try {
                CocktailApiResponse response = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(CocktailApiResponse.class);

                if (response != null && response.getDrinks() != null) {
                    for (DrinkDto drink : response.getDrinks()) {
                        boolean added = saveCocktailIfNotExists(drink);
                        if (added) {
                            totalAdded++;
                        } else {
                            totalSkipped++;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("[CocktailSeeder] '{}' 글자 데이터 수집 중 오류 발생", c, e);
            }

            try {
                // 5초 간격 조절
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("[CocktailSeeder] 수집 작업이 중단되었습니다.");
                break;
            }
        }

        log.info("[CocktailSeeder] 데이터 수집 및 추가 완료! 신규 추가: {}건, 중복 건너뜀: {}건", totalAdded, totalSkipped);
    }

    @Transactional
    public boolean saveCocktailIfNotExists(DrinkDto drink) {
        if (drink.getStrDrink() == null || drink.getStrDrink().isBlank()) {
            return false;
        }

        String name = drink.getStrDrink().trim();

        if (liquorRepository.existsByName(name)) {
            return false;
        }

        // 이미지 경로: 외부 URL을 그대로 imageKey에 수집
        String imageUrl = drink.getStrDrinkThumb();

        // 베이스 주류 소분류 매핑 (COCKTAIL 카테고리 소분류 검색, 없으면 'Other' 소분류로 매핑)
        String baseSpiritName = parseBaseSpirit(drink);
        Long subcategoryId = liquorSubcategoryRepository.findByCategoryAndName(LiquorCategory.COCKTAIL, baseSpiritName)
                .map(LiquorSubcategory::getId)
                .orElseGet(() -> liquorSubcategoryRepository.findByCategoryAndName(LiquorCategory.COCKTAIL, "Other")
                        .map(LiquorSubcategory::getId)
                        .orElse(null));

        Liquor cocktail = Liquor.create(
                name,
                null, // 칵테일의 한글명(nameKo)은 추후 번역 시 업데이트
                LiquorCategory.COCKTAIL,
                subcategoryId,
                Country.UNKNOWN,
                "", // 칵테일은 특정 제조사가 없음
                0.0,
                imageUrl
        );

        // CocktailDetail 생성 및 양방향 연동 (Liquor cascade로 함께 DB 저장)
        com.liquordb.entity.liquordetail.CocktailDetail detail = com.liquordb.entity.liquordetail.CocktailDetail.create(
                cocktail,
                drink.getStrGlass(),
                drink.getStrInstructions(),
                extractIngredients(drink)
        );
        cocktail.setDetail(detail);

        liquorRepository.save(cocktail);
        
        log.info("[CocktailSeeder] 칵테일 등록 완료: {} ({})", name, imageUrl);
        return true;
    }

    private String parseBaseSpirit(DrinkDto drink) {
        String[] ingredients = {
                drink.getStrIngredient1(), drink.getStrIngredient2(), drink.getStrIngredient3(),
                drink.getStrIngredient4(), drink.getStrIngredient5(), drink.getStrIngredient6(),
                drink.getStrIngredient7(), drink.getStrIngredient8(), drink.getStrIngredient9(),
                drink.getStrIngredient10(), drink.getStrIngredient11(), drink.getStrIngredient12(),
                drink.getStrIngredient13(), drink.getStrIngredient14(), drink.getStrIngredient15()
        };

        for (String ing : ingredients) {
            if (ing == null || ing.isBlank()) continue;
            String lower = ing.toLowerCase();

            if (lower.contains("vodka")) return "Vodka based";
            if (lower.contains("rum")) return "Rum based";
            if (lower.contains("gin")) return "Gin based";
            if (lower.contains("tequila") || lower.contains("mezcal")) return "Tequila based";
            if (lower.contains("whisky") || lower.contains("whiskey") || lower.contains("bourbon") || lower.contains("scotch")) return "Whisky based";
            if (lower.contains("brandy") || lower.contains("cognac")) return "Brandy based";
            if (lower.contains("liqueur") || lower.contains("triple sec") || lower.contains("amaretto") || lower.contains("baileys")) return "Liqueur based";
        }

        return "Other";
    }

    private String extractIngredients(DrinkDto drink) {
        StringBuilder sb = new StringBuilder();
        String[] ingredients = {
                drink.getStrIngredient1(), drink.getStrIngredient2(), drink.getStrIngredient3(),
                drink.getStrIngredient4(), drink.getStrIngredient5(), drink.getStrIngredient6(),
                drink.getStrIngredient7(), drink.getStrIngredient8(), drink.getStrIngredient9(),
                drink.getStrIngredient10(), drink.getStrIngredient11(), drink.getStrIngredient12(),
                drink.getStrIngredient13(), drink.getStrIngredient14(), drink.getStrIngredient15()
        };
        String[] measures = {
                drink.getStrMeasure1(), drink.getStrMeasure2(), drink.getStrMeasure3(),
                drink.getStrMeasure4(), drink.getStrMeasure5(), drink.getStrMeasure6(),
                drink.getStrMeasure7(), drink.getStrMeasure8(), drink.getStrMeasure9(),
                drink.getStrMeasure10(), drink.getStrMeasure11(), drink.getStrMeasure12(),
                drink.getStrMeasure13(), drink.getStrMeasure14(), drink.getStrMeasure15()
        };

        for (int i = 0; i < ingredients.length; i++) {
            String ing = ingredients[i];
            if (ing != null && !ing.isBlank()) {
                String measure = (measures[i] != null && !measures[i].isBlank()) ? measures[i].trim() : "";
                if (sb.length() > 0) sb.append("\n");
                if (!measure.isEmpty()) {
                    sb.append("• ").append(measure).append(" ").append(ing.trim());
                } else {
                    sb.append("• ").append(ing.trim());
                }
            }
        }
        return sb.toString();
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CocktailApiResponse {
        private List<DrinkDto> drinks;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DrinkDto {
        private String idDrink;
        private String strDrink;
        private String strDrinkThumb;
        private String strCategory;
        private String strAlcoholic;
        private String strGlass;
        private String strInstructions;

        private String strIngredient1;
        private String strIngredient2;
        private String strIngredient3;
        private String strIngredient4;
        private String strIngredient5;
        private String strIngredient6;
        private String strIngredient7;
        private String strIngredient8;
        private String strIngredient9;
        private String strIngredient10;
        private String strIngredient11;
        private String strIngredient12;
        private String strIngredient13;
        private String strIngredient14;
        private String strIngredient15;

        private String strMeasure1;
        private String strMeasure2;
        private String strMeasure3;
        private String strMeasure4;
        private String strMeasure5;
        private String strMeasure6;
        private String strMeasure7;
        private String strMeasure8;
        private String strMeasure9;
        private String strMeasure10;
        private String strMeasure11;
        private String strMeasure12;
        private String strMeasure13;
        private String strMeasure14;
        private String strMeasure15;
    }
}
