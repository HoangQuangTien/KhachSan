package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.LoaiPhongDTO;
import com.example.DuAnTotNghiepKs.DTO.PhongDTO;
import com.example.DuAnTotNghiepKs.entity.LoaiPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import com.example.DuAnTotNghiepKs.service.LoaiPhongService;
import com.example.DuAnTotNghiepKs.service.PhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loaiPhong")
public class LoaiPhongRest {

    @Autowired
    private LoaiPhongService loaiPhongService;

    @Autowired
    private DatPhongService datPhongService;

    @Autowired
    private PhongService phongService;

    @GetMapping("/{id}")
    public ResponseEntity<LoaiPhongDTO> getLoaiPhong(@PathVariable int id) {
        LoaiPhong loaiPhong = loaiPhongService.findById(id);
        if (loaiPhong == null) {
            return ResponseEntity.notFound().build();
        }
        LoaiPhongDTO dto = new LoaiPhongDTO();
        dto.setTenTang(loaiPhong.getTang().getTenTang());
        dto.setTenDienTich(loaiPhong.getDienTich().getTenDienTich());
        dto.setGia(loaiPhong.getGia());
        dto.setSoNguoiToiDa(loaiPhong.getSoNguoiToiDa());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all-loai-phongs")
    @ResponseBody
    public List<LoaiPhongDTO> getAllLoaiPhongsWithAvailableRooms() {
        // Lấy tất cả các loại phòng
        List<LoaiPhong> loaiPhongs = loaiPhongService.getAllLoaiPhongs();

        // Chuyển đổi từng loại phòng sang DTO và lấy phòng chưa được đặt cho mỗi loại phòng
        return loaiPhongs.stream().map(loaiPhong -> {
            LoaiPhongDTO loaiPhongDTO = new LoaiPhongDTO();
            loaiPhongDTO.setIdLoaiPhong(loaiPhong.getIdLoaiPhong());
            loaiPhongDTO.setTenLoaiPhong(loaiPhong.getTenLoaiPhong());
            loaiPhongDTO.setSoNguoiToiDa(loaiPhong.getSoNguoiToiDa());
            loaiPhongDTO.setGia(loaiPhong.getGia());

            // Lấy danh sách phòng chưa được đặt cho loại phòng này
            List<Phong> availableRooms = datPhongService.findAvailablePhongByLoaiPhong(loaiPhong.getIdLoaiPhong());
            List<PhongDTO> phongDTOS = availableRooms.stream()
                    .map(phong -> phongService.convertToPhongDTO(phong))
                    .collect(Collectors.toList());

            loaiPhongDTO.setAvailableRooms(phongDTOS);
            return loaiPhongDTO;
        }).collect(Collectors.toList());
    }

}

