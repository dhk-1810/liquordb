package com.liquordb.exception.redis;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.util.Map;

public class RedisLockAcquisitionException extends RedisException {
    public RedisLockAcquisitionException(String lockKey) {
        super(
                ErrorCode.LOCK_ACQUISITION_FAILED,
                "Redis Lock 획득 실패",
                Map.of("lockKey", lockKey)
        );
    }
}
