package com.yydp.ai.client;

import com.yydp.ai.client.dto.*;

public interface AiRemoteClient {
    ChunkSummaryResponse summarizeChunk(ChunkSummaryRequest request);

    FinalSummaryResponse summarizeFinal(FinalSummaryRequest request);

    IntentParseResponse parseIntent(IntentParseRequest request);

    RecommendReasonResponse recommendReason(RecommendReasonRequest request);

    RecommendRerankResponse recommendRerank(RecommendRerankRequest request);

    ReviewRiskCheckResponse reviewRiskCheck(ReviewRiskCheckRequest request);
}
