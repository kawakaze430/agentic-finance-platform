package io.github.kawakaze430.finance;

import java.util.UUID;
public record VendorView(UUID id, String name, String email, String currency) {}
