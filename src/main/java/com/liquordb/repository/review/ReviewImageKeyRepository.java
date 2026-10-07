package com.liquordb.repository.review;

import com.liquordb.entity.ReviewImageKey;
import com.liquordb.entity.id.ReviewImageKeyId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageKeyRepository extends JpaRepository<ReviewImageKey, ReviewImageKeyId> {

}
