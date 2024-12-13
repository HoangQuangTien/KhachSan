package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.LoaiPhongDTO;
import com.example.DuAnTotNghiepKs.entity.LoaiPhong;
import com.example.DuAnTotNghiepKs.repository.LoaiPhongRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoaiPhongService {
    @Autowired
    private LoaiPhongRepo loaiPhongRepository;



    public List<LoaiPhong> getAllLoaiPhongs() {
        return loaiPhongRepository.findAll();
    }

    public Optional<LoaiPhong> getLoaiPhongById(Integer id) {
        return loaiPhongRepository.findById(id);
    }

    public LoaiPhong getLoaiPhongById1(int id) {
        return loaiPhongRepository.findById(id).orElse(null);
    }

    public LoaiPhong findById(Integer id) {
        return loaiPhongRepository.findById(id).orElse(null);
    }

    public boolean isMaLoaiPhongExists(String maLoaiPhong) {
        return loaiPhongRepository.existsByMaLoaiPhong(maLoaiPhong);
    }
    public LoaiPhong saveLoaiPhong(LoaiPhong loaiPhong) {
        return loaiPhongRepository.save(loaiPhong);
    }

    public void deleteLoaiPhong(int id) {
        loaiPhongRepository.deleteById(id);
    }

    public List<LoaiPhong> searchKhuyenMai(String keyword) {
        if (keyword != null) {
            return loaiPhongRepository.searchByKeyword(keyword);
        }
        return loaiPhongRepository.findAll();
    }

    public Page<LoaiPhong> getLoaiPhongPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return loaiPhongRepository.findAll(pageable);
    }
    public Page<LoaiPhong> searchLoaiPhong(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return loaiPhongRepository.findByMaLoaiPhongContainingIgnoreCaseOrTenLoaiPhongContainingIgnoreCase(keyword, keyword, pageable);
    }


//    public LoaiPhongDTO getLoaiPhongByIdk(Integer id) {
//        LoaiPhong loaiPhong = loaiPhongRepository.findById(id).orElse(null);
//        if (loaiPhong != null) {
//            LoaiPhongDTO dto = new LoaiPhongDTO();
//            dto.setIdLoaiPhong(loaiPhong.getIdLoaiPhong());
//            dto.setTenTang(String.valueOf(loaiPhong.getTang()));
//            dto.setTenDienTich(loaiPhong.getDienTich().getTenDienTich());
//            dto.setGia(loaiPhong.getGia());
//            return dto;
//        }
//        return null;
//    }


    public long countLoaiPhong() {
        return loaiPhongRepository.count(); // Giả sử bạn có repository `loaiPhongRepo` để truy vấn
    }

    public boolean isTenLoaiPhongTrung(String tenLoaiPhong) {
        return loaiPhongRepository.existsByTenLoaiPhong(tenLoaiPhong);
    }

    public boolean existsByTangId(Integer idTang) {
        return loaiPhongRepository.existsByTang_IdTang(idTang);
    }


    public Page<LoaiPhong> findByTangAndKhoangGia(Integer idTang,Float gia1, Float gia2,Pageable pageable){
        return loaiPhongRepository.findByTang_IdTangAndGiaBetween(idTang,gia1,gia2,pageable);
    }
    public Page<LoaiPhong> fingByKhoangGia(Float gia1,Float gia2,Pageable pageable){
        return loaiPhongRepository.findByGiaBetween(gia1,gia2,pageable);
    }
    public Page<LoaiPhong> findByTang(Integer idTang,Pageable pageable){
        return loaiPhongRepository.findByTang_IdTang(idTang,pageable);
    }
}
