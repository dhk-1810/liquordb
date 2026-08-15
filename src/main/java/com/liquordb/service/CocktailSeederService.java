package com.liquordb.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liquordb.entity.Liquor;
import com.liquordb.enums.Country;
import com.liquordb.enums.LiquorCategory;
import com.liquordb.repository.liquor.LiquorRepository;
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

        Liquor cocktail = Liquor.create(
                name,
                LiquorCategory.COCKTAIL,
                1L, // 기본 서브카테고리 ID
                Country.UNKNOWN,
                "TheCocktailDB",
                0.0, // 기본 ABV
                imageUrl
        );

        liquorRepository.save(cocktail);
        log.info("[CocktailSeeder] 칵테일 등록 완료: {} ({})", name, imageUrl);
        return true;
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
    }
}
