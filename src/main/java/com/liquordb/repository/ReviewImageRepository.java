package com.liquordb.repository;

import com.liquordb.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends JpaRepository<Image, Long> {
}