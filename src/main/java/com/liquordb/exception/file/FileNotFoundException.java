package com.liquordb.exception.file;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class FileNotFoundException extends FIleException {
    public FileNotFoundException(Long fileId) {
        super(
                ErrorCode.FILE_NOT_FOUND,
                "파일을 찾을 수 없습니다.",
                Map.of("fileId", fileId)
        );
    }
}
