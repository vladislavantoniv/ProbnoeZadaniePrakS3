package com.lab.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TestTypeDTO {
    private Long id;

    @NotBlank(message = "Название обязательно")
    @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
    private String name;

    @NotBlank(message = "Код обязателен")
    @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "Код должен содержать только заглавные буквы, цифры и дефисы")
    @Size(min = 2, max = 20, message = "Код должен быть от 2 до 20 символов")
    private String code;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    @DecimalMax(value = "1000000.0", message = "Цена не должна превышать 1 000 000")
    @Digits(integer = 10, fraction = 2, message = "Некорректный формат цены")
    private BigDecimal price;

    public TestTypeDTO() {}
    @SuppressWarnings("unused")
    public TestTypeDTO(String name, String code, String description, BigDecimal price) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}