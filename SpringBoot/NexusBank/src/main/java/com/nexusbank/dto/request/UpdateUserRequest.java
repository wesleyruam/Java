
package com.nexusbank.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {

    @Size(min = 2, max = 150, message = "Nome deve ter entre 2 e 150 caracteres")
    private String name;

    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve conter 10 ou 11 dígitos")
    private String phone;
}