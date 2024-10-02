package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.entity.LichSuDatPhong;
import com.example.DuAnTotNghiepKs.repository.LichSuDatPhongRepo;
import com.example.DuAnTotNghiepKs.service.LichSuDatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class LichSuDatPhongServiceImp implements LichSuDatPhongService {

    @Autowired
    private LichSuDatPhongRepo lichSuDatPhongRepo;

    @Override
    public LichSuDatPhong saveLichSuDatPhong(LichSuDatPhong lichSuDatPhong) {
        return lichSuDatPhongRepo.save(lichSuDatPhong);
    }
    @Override
    public Page<LichSuDatPhong> getLichSuDatPhong(int page, int size) {
        return lichSuDatPhongRepo.findAll(PageRequest.of(page, size));
    }
}
