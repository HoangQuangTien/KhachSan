package com.example.DuAnTotNghiepKs.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "chan")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma")
    @NotBlank(message = "Mã không được để trống")
    @Size(min = 1, max = 10, message = "Mã phải có từ 1 đến 10 ký tự")
    private String ma;

    @Column(name = "ten")
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 1, max = 50, message = "Tên phải có từ 1 đến 50 ký tự")
    private String ten;

    @Column(name = "so_luong")
    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng phải là số dương")
    private Integer soLuong;


    @Column(name = "anh", length = 255)
    @NotBlank(message = "Hình ảnh không được để trống")
    private String anh;

    @Column(name = "gia")
    @Positive(message = "gia phải là số dương")
    private BigDecimal gia;

}