package io.github.kawakaze430.finance;

import java.util.Set;
public record PolicyDocument(String id, String title, String text, Set<String> keywords) {}
