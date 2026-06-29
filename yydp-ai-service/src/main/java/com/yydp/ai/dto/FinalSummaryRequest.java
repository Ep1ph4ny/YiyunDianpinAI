package com.yydp.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class FinalSummaryRequest {
    private Long shopId;
    private String shopName;
    private Integer reviewCount;
    private List<ChunkSummaryResponse> chunkSummaries;
}

