package com.liquordb.entity;

import java.time.LocalDateTime;

public interface ReportableEntity {
    void hide(LocalDateTime now);
    void unhide();
    User getUser(); // 작성자를 알아야 제재가 가능함
}