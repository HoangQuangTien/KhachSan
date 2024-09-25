package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.ThanhToanDTO;
import com.example.DuAnTotNghiepKs.entity.LoaiPhong;
import com.example.DuAnTotNghiepKs.entity.ThanhToan;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import com.example.DuAnTotNghiepKs.service.PhuPhiService;
import com.example.DuAnTotNghiepKs.service.ThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/thanhToan")
public class ThanhToanController {

    @Autowired
    private ThanhToanService thanhToanService;

    @Autowired
    private DatPhongService datPhongService;

    @Autowired
    private PhuPhiService phuPhiService;

    @GetMapping
    public String loadAll(@ModelAttribute ThanhToanDTO thanhToanDTO, Model model,@RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "5") int size){

        Page<ThanhToan> thanhToanPage= thanhToanService.getLoaiPhongPage(page,size);
        model.addAttribute("loaiPhongPage", thanhToanPage);
        model.addAttribute("phuPhis",phuPhiService.getAll());
        model.addAttribute("datPhongs", datPhongService.getDatPhongsDaCoc());  // Lấy danh sách đặt phòng đã cọc
//        model.addAttribute("datPhong",datPhong);
        return "list/QuanLyThanhToan/thanhToan";
    }


    @PostMapping("/add")
    public String save(@ModelAttribute ThanhToanDTO thanhToanDTO){
        thanhToanService.save(thanhToanDTO);
        return "redirect:/thanhToan";
    }




}

