package com.example.DuAnTotNghiepKs.rest;


import com.example.DuAnTotNghiepKs.DTO.TangDTO;
import com.example.DuAnTotNghiepKs.entity.Tang;
import com.example.DuAnTotNghiepKs.service.TangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tang")
public class TangRest {

    @Autowired
    private TangService tangService;

    @GetMapping("/for-loai-phong/{idLoaiPhong}")
    @ResponseBody
    public List<TangDTO> getTangByLoaiPhong(@PathVariable Integer idLoaiPhong) {
        // Lấy danh sách tầng thuộc loại phòng
        List<Tang> tangList = tangService.findByLoaiPhongId(idLoaiPhong);

        // Chuyển đổi danh sách tầng thành DTO hoặc JSON
        return tangList.stream()
                .map(tang -> new TangDTO(tang.getIdTang(), tang.getTenTang()))
                .collect(Collectors.toList());
    }

}
