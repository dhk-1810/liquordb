package com.liquordb.mapper;

import com.liquordb.dto.report.ReportRequestDto;
import com.liquordb.dto.report.ReportResponseDto;
import com.liquordb.entity.Report;

import java.time.LocalDateTime;

public class ReportMapper {

    public static Report toEntity(ReportRequestDto dto) {
        return Report.builder()
                .userId(dto.getUserId())
                .targetId(dto.getTargetId())
                .targetType(dto.getTargetType())
                .reason(dto.getReason())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ReportResponseDto toDto(Report report) {
        return ReportResponseDto.builder()
                .userId(report.getUserId())
                .targetId(report.getTargetId())
                .targetType(report.getTargetType())
                .reason(report.getReason())
                .isApproved(report.isApproved())
                .createdAt(LocalDateTime.now())
                .build();
    }

}
