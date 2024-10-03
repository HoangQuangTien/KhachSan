package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.ThamSoDTO;
import com.example.DuAnTotNghiepKs.entity.ThamSo;
import com.example.DuAnTotNghiepKs.repository.ThamSoRepo;
import com.example.DuAnTotNghiepKs.service.ThamSoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThamSoServiceImp implements ThamSoService {
    @Autowired
    private ThamSoRepo thamSoRepo;

    @Override
    public List<ThamSoDTO> getAllThamSo() {
        return thamSoRepo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public ThamSoDTO save(ThamSoDTO thamSoDTO) {
        ThamSo thamSo = convertToEntity(thamSoDTO);
        thamSoRepo.save(thamSo);
        return convertToDTO(thamSo);
    }

    @Override
    public ThamSoDTO findById(Long id) {
        ThamSo thamSo = thamSoRepo.findById(id).orElse(null);
        return convertToDTO(thamSo);
    }



    @Override
    public void deleteById(Long id) {
        thamSoRepo.deleteById(id);
    }

    @Override
    public String getThamSoValue(String tenThamSo) {
        ThamSo thamSo = thamSoRepo.findByTenThamSo(tenThamSo);
        return (thamSo != null) ? thamSo.getGiaTri() : null; // Trả về giá trị hoặc null nếu không tìm thấy
    }
    // Chuyển đổi từ Entity sang DTO
    private ThamSoDTO convertToDTO(ThamSo thamSo) {
        ThamSoDTO thamSoDTO = new ThamSoDTO();
        thamSoDTO.setId(thamSo.getId());
        thamSoDTO.setTenThamSo(thamSo.getTenThamSo());
        thamSoDTO.setGiaTri(thamSo.getGiaTri());
        thamSoDTO.setMoTa(thamSo.getMoTa());
        return thamSoDTO;
    }

    // Chuyển đổi từ DTO sang Entity
    private ThamSo convertToEntity(ThamSoDTO thamSoDTO) {
        ThamSo thamSo = new ThamSo();
        thamSo.setId(thamSoDTO.getId());
        thamSo.setTenThamSo(thamSoDTO.getTenThamSo());
        thamSo.setGiaTri(thamSoDTO.getGiaTri());
        thamSo.setMoTa(thamSoDTO.getMoTa());
        return thamSo;
    }


}
