package com.microservice.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
        name = "Accounts",
        description = "Schema to hold Account information"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountsDto {

    @NotNull(message = "Account Number cannot be null")
    @NotEmpty(message = "Account Number cannot be empty")
    @Pattern(regexp = "^[0-9]{10}$", message = "Account Number must be 10 digits")
    @Schema(description = "Account Number", example = "1234567890")
    private long accountNumber;
    @NotNull(message = "Account Type cannot be null")
    @NotEmpty(message = "Account Type cannot be empty")
    @Schema(description = "Account Type", example = "Savings")
    private String accountType;
    @NotNull(message = "Branch Address cannot be null")
    @NotEmpty(message = "Branch Address cannot be empty")
    @Schema(description = "Branch Address", example = "Mumbai")
    private String branchAddress;
}
