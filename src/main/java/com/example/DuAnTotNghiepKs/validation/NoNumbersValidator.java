package com.example.DuAnTotNghiepKs.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class NoNumbersValidator implements ConstraintValidator<NoNumbers, String> {

    @Override
    public void initialize(NoNumbers constraintAnnotation) {
        // Phương thức khởi tạo không cần thiết trong trường hợp này
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Kiểm tra nếu giá trị là null hoặc không chứa số
        return value != null && !value.matches(".*\\d.*");
    }
}

