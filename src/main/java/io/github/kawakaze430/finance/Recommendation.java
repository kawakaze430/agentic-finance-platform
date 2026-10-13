package io.github.kawakaze430.finance;

import java.util.List;
public record Recommendation(RiskLevel risk, String summary, List<RecommendationAction> actions, List<String> policyCitations) {}
