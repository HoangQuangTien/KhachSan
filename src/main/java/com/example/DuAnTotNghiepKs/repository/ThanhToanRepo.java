package com.example.DuAnTotNghiepKs.repository;



import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.ThanhToan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface ThanhToanRepo extends JpaRepository<ThanhToan, Integer> {

    @Query("SELECT dp FROM DatPhong dp WHERE dp.trangThai = true")
    Page<DatPhong> findAllByDaCoc(Pageable pageable);


    @Query("SELECT tt FROM ThanhToan tt WHERE tt.tinhTrang = true")
    Page<ThanhToan> findAllByTinhTrang(Pageable pageable);


}
