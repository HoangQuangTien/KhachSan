package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.LoaiPhong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoaiPhongRepo extends JpaRepository<LoaiPhong, Integer> {
//    Optional<LoaiPhong> findById(Integer id);

    boolean existsByMaLoaiPhong(String maLoaiPhong);
    @Query("SELECT lp FROM LoaiPhong lp WHERE lp.maLoaiPhong LIKE %:keyword% OR lp.tenLoaiPhong LIKE %:keyword%")
    List<LoaiPhong> searchByKeyword(@Param("keyword") String keyword);
    Page<LoaiPhong> findAll(Pageable pageable);
    Page<LoaiPhong> findByMaLoaiPhongContainingIgnoreCaseOrTenLoaiPhongContainingIgnoreCase(
            String maLoaiPhong, String tenLoaiPhong, Pageable pageable);

    boolean existsByTenLoaiPhong(String tenLoaiPhong);

    boolean existsByTang_IdTang(Integer idTang);

    Page<LoaiPhong> findByTang_IdTangAndGiaBetween(Integer idTang,Float giaMin, Float giaMax,Pageable pageable);
    Page<LoaiPhong> findByGiaBetween(Float giaMin, Float giaMax, Pageable pageable);
    Page<LoaiPhong> findByTang_IdTang(Integer idTang,Pageable pageable);


}

