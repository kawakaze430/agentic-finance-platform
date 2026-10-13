package io.github.kawakaze430.finance;

import com.fasterxml.jackson.databind.JsonNode;
public record ToolStep(int step, String tool, JsonNode arguments, JsonNode result) {}
