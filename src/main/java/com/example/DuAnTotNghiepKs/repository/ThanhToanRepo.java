package com.example.DuAnTotNghiepKs.repository;



import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.ThanhToan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@RequestMapping
public interface ThanhToanRepo extends JpaRepository<ThanhToan, Integer> {

    @Query("SELECT dp FROM DatPhong dp WHERE dp.trangThai = true")
    Page<DatPhong> findAllByDaCoc(Pageable pageable);


    @Query("SELECT tt FROM ThanhToan tt WHERE tt.tinhTrang = true")
    Page<ThanhToan> findAllByTinhTrang(Pageable pageable);


        @Query("SELECT tt FROM ThanhToan tt " +
                "JOIN tt.datPhong dp " +
                "JOIN dp.khachHang kh " +
                "JOIN tt.nhanVien nv " +
                "WHERE (kh.hoVaTen LIKE %:query% OR nv.hoTen LIKE %:query%) " +
                "AND tt.ngayThanhToan = :ngayThanhToan")
        List<ThanhToan> searchByKhachHangOrNhanVienAndNgayThanhToan(
                @Param("query") String query,
                @Param("ngayThanhToan") Date ngayThanhToan);

}
