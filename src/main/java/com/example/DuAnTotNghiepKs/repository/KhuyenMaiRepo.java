package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface KhuyenMaiRepo extends JpaRepository<KhuyenMai,Integer> {
    KhuyenMai findByMaKhuyenMai(String maKhuyenMai);
    @Query("SELECT km FROM KhuyenMai km WHERE km.maKhuyenMai LIKE %:keyword% OR km.tenKhuyenMai LIKE %:keyword%")
    List<KhuyenMai> searchByKeyword(@Param("keyword") String keyword);

    // Các phương thức khác...
//    Page<KhuyenMai> findByTenKhuyenMaiContaining(String keyword, Pageable pageable);

    Page<KhuyenMai> getAllBy(Pageable pageable);

    // Tìm kiếm theo MaKhuyenMai hoặc TenKhuyenMai và trạng thái
    // Truy vấn tìm kiếm kết hợp
    @Query("SELECT k FROM KhuyenMai k WHERE (LOWER(k.maKhuyenMai) LIKE LOWER(CONCAT('%', :maKhuyenMai, '%')) " +
            "OR LOWER(k.tenKhuyenMai) LIKE LOWER(CONCAT('%', :tenKhuyenMai, '%'))) " +
            "AND k.trangThai = :trangThai")
    Page<KhuyenMai> searchKhuyenMai(@Param("maKhuyenMai") String maKhuyenMai,
                                    @Param("tenKhuyenMai") String tenKhuyenMai,
                                    @Param("trangThai") String trangThai,
                                    Pageable pageable);

    boolean existsByMaKhuyenMai(String maKhuyenMai);

    //Chuyển trạng thái khi ngày hết hạn

    List<KhuyenMai> findByTrangThai(String trangThai);

    @Query("SELECT k FROM KhuyenMai k WHERE k.ngayKetThuc >= :today")
    List<KhuyenMai> findAllActiveVouchers(@Param("today") Date today);
}
