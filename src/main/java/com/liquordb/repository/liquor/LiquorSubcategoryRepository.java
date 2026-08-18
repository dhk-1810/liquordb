package com.liquordb.repository.liquor;

import com.liquordb.entity.LiquorSubcategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LiquorSubcategoryRepository extends JpaRepository<LiquorSubcategory, Long> {

    Optional<LiquorSubcategory> findByName(String name);

    Optional<LiquorSubcategory> findByNameKo(String nameKo);

    Optional<LiquorSubcategory> findByCategoryAndName(com.liquordb.enums.LiquorCategory category, String name);
}
