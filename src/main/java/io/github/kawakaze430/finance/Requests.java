package io.github.kawakaze430.finance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
public final class Requests {
    private Requests() {}
    public record Vendor(@NotBlank @Size(max=120) String name, @NotBlank @Email @Size(max=200) String email, @NotBlank String currency) {}
    public record Invoice(@NotNull UUID vendorId, @NotBlank @Pattern(regexp="[A-Za-z0-9._/-]{1,80}") String reference,
        @NotNull @DecimalMin("0.01") @Digits(integer=14,fraction=2) BigDecimal amount, @NotBlank String currency, @NotNull LocalDate dueDate) {}
    public record Decision(@NotBlank @Size(min=8,max=500) String reason) {}
    public record BankTransaction(@NotBlank @Size(max=100) String externalId, @NotBlank @Size(max=120) String reference,
        @NotBlank @Size(max=120) String counterparty, @NotNull @DecimalMin("0.01") @Digits(integer=14,fraction=2) BigDecimal amount,
        @NotBlank String currency, @NotNull LocalDate bookedOn) {}
    public record Match(@NotNull UUID invoiceId, @NotNull UUID bankTransactionId) {}
    public record Line(@NotNull AccountCode account, @NotNull @DecimalMin("0") @Digits(integer=14,fraction=2) BigDecimal debit,
        @NotNull @DecimalMin("0") @Digits(integer=14,fraction=2) BigDecimal credit) {}
    public record Journal(@NotBlank @Size(max=120) String reference, @NotBlank String currency,
        @NotBlank @Size(max=300) String description, @NotNull @Size(min=2,max=20) List<@Valid Line> lines) {}
    public record Agent(@NotNull UUID invoiceId, @NotBlank @Size(max=1500) String question) {}
}
