package io.github.kawakaze430.finance;

import java.util.List;
public record BrainReply(List<ToolCall> calls, Recommendation recommendation) {}
