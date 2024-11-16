package com.example.DuAnTotNghiepKs.entity;

import com.example.DuAnTotNghiepKs.entity.Phong;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


@Data
@Entity
@Table(name = "DanhGia")
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_danh_gia")
    private Integer idDanhGia; // ID đánh giá tự tăng

    @ManyToOne
    @JoinColumn(name = "id_phong", nullable = false)
    private Phong phong; // Liên kết với bảng Phòng

    @Column(name = "ten_khach_hang", nullable = false, length = 255)
    private String tenKhachHang; // Tên khách hàng đánh giá

    @Column(name = "so_sao", nullable = false)
    private Integer soSao; // Số sao đánh giá (từ 1 đến 5)

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung; // Nội dung đánh giá

    @Column(name = "ngay_danh_gia", nullable = false)
    private LocalDateTime ngayDanhGia = LocalDateTime.now(); // Ngày đánh giá
}
