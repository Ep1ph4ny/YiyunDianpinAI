package com.yydp.service;

import com.yydp.dto.Result;
import com.yydp.dto.ai.AiAssistantRequestDTO;
import com.yydp.dto.ai.AiReviewRiskCheckRequestDTO;

public interface IAiService {
    Result getShopSummary(Long shopId, Boolean refresh);

    Result warmupShopSummary(Long shopId);

    Result assistantRecommend(AiAssistantRequestDTO requestDTO);

    Result checkReviewRisk(AiReviewRiskCheckRequestDTO requestDTO);
}
