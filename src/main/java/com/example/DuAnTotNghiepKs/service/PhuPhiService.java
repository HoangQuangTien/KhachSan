package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.PhuPhi;
import com.example.DuAnTotNghiepKs.exception.ResourceNotfound;
import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;

import com.example.DuAnTotNghiepKs.repository.PhuPhiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhuPhiService {

    @Autowired
    private PhuPhiRepo phuPhiRepo;

    @Autowired
    private DatPhongRepo datPhongRepo;

    public List<PhuPhi> getAll(){
        return phuPhiRepo.findAll();
    }

    public void save(PhuPhi phuPhi){
        if (phuPhi.getDatPhong() != null && phuPhi.getDatPhong().getIdDatPhong() != null){
            DatPhong datPhong = datPhongRepo.findById(phuPhi.getDatPhong().getIdDatPhong())
                    .orElseThrow(() ->  new ResourceNotfound("Không tìm thấy id đặt phòng: " + phuPhi.getDatPhong().getIdDatPhong()));
            phuPhi.setDatPhong(datPhong);
        }
        //lưu đối tượng vào cơ sở dữ liệu
        phuPhiRepo.save(phuPhi);
    }

    public PhuPhi findById(Integer id){
        return phuPhiRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phụ phí với ID: "+ id));
    }

    public void delete(Integer id){
        phuPhiRepo.deleteById(id);
    }

    public void update(PhuPhi phuPhi){
        phuPhiRepo.save(phuPhi);
    }


}