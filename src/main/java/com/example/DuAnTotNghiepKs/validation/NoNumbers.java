package com.example.DuAnTotNghiepKs.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoNumbersValidator.class)  // Xác định lớp thực hiện validation
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })  // Áp dụng cho các thành phần này
@Retention(RetentionPolicy.RUNTIME)  // Annotation có thời gian sống ở runtime
public @interface NoNumbers {
    String message() default "Tên loại phòng không được chứa số";  // Thông báo lỗi mặc định
    Class<?>[] groups() default {};  // Nhóm các lớp kiểm tra (tùy chọn)
    Class<? extends Payload>[] payload() default {};  // Thông tin bổ sung (tùy chọn)
}

