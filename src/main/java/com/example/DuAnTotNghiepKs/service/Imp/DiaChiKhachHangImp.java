package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.DiaChiKhachHangDTO;
import com.example.DuAnTotNghiepKs.entity.DiaChiKhachHang;
import com.example.DuAnTotNghiepKs.exception.ResourceNotfound;
import com.example.DuAnTotNghiepKs.repository.DiaChiKhachHangRepository;
import com.example.DuAnTotNghiepKs.service.DiaChiKhachHangService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiaChiKhachHangImp implements DiaChiKhachHangService {
    @Autowired
    private DiaChiKhachHangRepository diaChiKhachHangRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<DiaChiKhachHangDTO> getAll() {
        List<DiaChiKhachHang> khachHangs = diaChiKhachHangRepository.findAll();
        return khachHangs.stream()
                .map(diaChiKhachHang -> modelMapper.map(diaChiKhachHang, DiaChiKhachHangDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public DiaChiKhachHangDTO getOne(Integer id) {
        DiaChiKhachHang diaChiKhachHang = diaChiKhachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfound("Không tìm thấy địa chỉ có id: " + id));
        return modelMapper.map(diaChiKhachHang, DiaChiKhachHangDTO.class);
    }

    @Override
    public void save(DiaChiKhachHangDTO diaChiKhachHangDTO) {
        DiaChiKhachHang diaChiKhachHang = modelMapper.map(diaChiKhachHangDTO, DiaChiKhachHang.class);
        diaChiKhachHangRepository.save(diaChiKhachHang);
    }

    @Override
    public void update(Integer id, DiaChiKhachHangDTO diaChiKhachHangDTO) {
        DiaChiKhachHang diaChi = diaChiKhachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfound("Không tìm thấy địa chỉ có id: " + id));

        // Cập nhật thông tin từ diaChiDTO vào diaChi
        diaChi.setDiaChiCuThe(diaChiKhachHangDTO.getDiaChiCuThe());
        diaChi.setThanhPho(diaChiKhachHangDTO.getThanhPho());
        diaChi.setQuanHuyen(diaChiKhachHangDTO.getQuanHuyen());
        diaChi.setPhuongXa(diaChiKhachHangDTO.getPhuongXa());
        diaChi.setGhiChu(diaChiKhachHangDTO.getGhiChu());

        // Lưu thông tin địa chỉ đã cập nhật vào cơ sở dữ liệu
        diaChiKhachHangRepository.save(diaChi);
    }


    @Override
    public void delete(Integer id) {

    }

    @Override
    public List<DiaChiKhachHangDTO> getAllJoinFetch() {
        return null;
    }

    public List<DiaChiKhachHangDTO> findById(Integer idKhachHang) {
        List<DiaChiKhachHang> diaChiKhachHangs = diaChiKhachHangRepository.findByIdKhachHang_Id(idKhachHang);
        return diaChiKhachHangs.stream()
                .map(diaChiKhachHang -> modelMapper.map(diaChiKhachHang, DiaChiKhachHangDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public Page<DiaChiKhachHangDTO> findByIdDC(Integer idKhachHang, Pageable pageable) {
        Page<DiaChiKhachHang> diaChiKhachHangPage = diaChiKhachHangRepository.findByIdKhachHang_Id(idKhachHang, pageable);
        return diaChiKhachHangPage.map(diaChiKhachHang -> modelMapper.map(diaChiKhachHang, DiaChiKhachHangDTO.class));
    }
}
