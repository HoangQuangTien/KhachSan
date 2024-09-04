package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.LoaiPhongDTO;
import com.example.DuAnTotNghiepKs.entity.LoaiPhong;
import com.example.DuAnTotNghiepKs.service.LoaiPhongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/loaiphongs")
public class LoaiPhongController {
    @Autowired
    private LoaiPhongService loaiPhongService;

    @GetMapping
    public String listLoaiPhong(@RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "5") int size,
                                Model model) {
        Page<LoaiPhong> loaiPhongPage=loaiPhongService.getLoaiPhongPage(page,size);
        model.addAttribute("loaiPhongPage", loaiPhongPage);
        return "list/QuanLyLoaiPhong/loaiphongs";
    }

    @GetMapping("/create")
    public String createLoaiPhongForm(Model model) {
        model.addAttribute("loaiPhong", new LoaiPhong());
        return "list/QuanLyLoaiPhong/Create";
    }

    @PostMapping("/save")
    public String saveLoaiPhong(@ModelAttribute("loaiPhong") @Valid LoaiPhong loaiPhong, BindingResult result, RedirectAttributes redirectAttributes) {
        // Kiểm tra lỗi xác thực
        if (result.hasErrors()) {
            return "list/QuanLyLoaiPhong/Create";
        }

        // Kiểm tra mã loại phòng đã tồn tại
        if (loaiPhongService.isMaLoaiPhongExists(loaiPhong.getMaLoaiPhong())) {
            result.rejectValue("maLoaiPhong", "error.loaiPhong", "Mã loại phòng đã tồn tại");
            return "list/QuanLyLoaiPhong/Create";
        }

        // Lưu loại phòng mới
        loaiPhongService.saveLoaiPhong(loaiPhong);
        redirectAttributes.addFlashAttribute("message", "Loại phòng đã được lưu thành công");
        return "redirect:/loaiphongs";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        LoaiPhong loaiPhong = loaiPhongService.getLoaiPhongById1(id);
        if (loaiPhong != null) {
            model.addAttribute("loaiPhong", loaiPhong);
            return "list/QuanLyLoaiPhong/edit";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy loại phòng với ID: " + id);
            return "redirect:/loaiphongs";
        }
    }
    @PostMapping("/edit/{id}")
    public String updateLoaiPhong(@PathVariable("id") Integer id,
                                  @ModelAttribute("loaiPhongs") @Valid LoaiPhong loaiPhong,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            // Nếu có lỗi xác thực, trả lại trang chỉnh sửa với thông báo lỗi
            return "list/QuanLyLoaiPhong/edit";
        }

        LoaiPhong existingLoaiPhong = loaiPhongService.getLoaiPhongById1(id);

        if (existingLoaiPhong != null) {
            existingLoaiPhong.setMaLoaiPhong(loaiPhong.getMaLoaiPhong());
            existingLoaiPhong.setTenLoaiPhong(loaiPhong.getTenLoaiPhong());
            existingLoaiPhong.setMoTa(loaiPhong.getMoTa());
            existingLoaiPhong.setSoLuongGiuong(loaiPhong.getSoLuongGiuong());
            existingLoaiPhong.setSoNguoiToiDa(loaiPhong.getSoNguoiToiDa());
            existingLoaiPhong.setGia(loaiPhong.getGia());
            loaiPhongService.saveLoaiPhong(existingLoaiPhong);
            return "redirect:/loaiphongs";
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy loại phòng với ID: " + id);
            return "redirect:/loaiphongs";
        }
    }
    @GetMapping("/search")
    @ResponseBody
    public Page<LoaiPhongDTO> search(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        Page<LoaiPhong> loaiPhongPage = loaiPhongService.searchLoaiPhong(keyword, page, size);

        // Chuyển đổi từ LoaiPhong thành LoaiPhongDTO nếu cần
        Page<LoaiPhongDTO> loaiPhongDTOPage = loaiPhongPage.map(this::convertToLoaiPhongDTO);

        return loaiPhongDTOPage;
    }

    private LoaiPhongDTO convertToLoaiPhongDTO(LoaiPhong loaiPhong) {
        return new LoaiPhongDTO(
                loaiPhong.getIdLoaiPhong(),
                loaiPhong.getMaLoaiPhong(),
                loaiPhong.getTenLoaiPhong(),
                loaiPhong.getMoTa(),
                loaiPhong.getSoLuongGiuong(),
                loaiPhong.getSoNguoiToiDa(),
                loaiPhong.getGia()
        );
    }
//
//    @GetMapping("/create")
//    public String createLoaiPhongForm(Model model) {
//        model.addAttribute("loaiPhong", new LoaiPhong());
//        return "loaiphong/Create";
//    }
//
//    @PostMapping("/save")
//    public String saveLoaiPhong(@ModelAttribute("loaiPhong") LoaiPhong loaiPhong) {
//        loaiPhongService.saveLoaiPhong(loaiPhong);
//        return "redirect:/loai-phong";
//    }

//
//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable("id") int id, Model model) {
//        LoaiPhong loaiPhong = loaiPhongService.getLoaiPhongById(id);
//        if (loaiPhong != null) {
//            model.addAttribute("loaiPhong", loaiPhong);
//            return "loaiphong/edit";
//        } else {
//            // Xử lý khi không tìm thấy LoaiPhong với ID tương ứng
//            return "redirect:/loai-phong";
//        }
//    }
//
//    @PostMapping("/edit/{id}")
//    public String updateLoaiPhong(@PathVariable("id") int id, @ModelAttribute("loaiPhong") LoaiPhong loaiPhong) {
//        LoaiPhong existingLoaiPhong = loaiPhongService.getLoaiPhongById(id);
//        if (existingLoaiPhong != null) {
//            existingLoaiPhong.setMaLoaiPhong(loaiPhong.getMaLoaiPhong());
//            existingLoaiPhong.setTenLoaiPhong(loaiPhong.getTenLoaiPhong());
//            existingLoaiPhong.setMoTa(loaiPhong.getMoTa());
//            existingLoaiPhong.setSoLuongGiuong(loaiPhong.getSoLuongGiuong());
//            existingLoaiPhong.setSoNguoiToiDa(loaiPhong.getSoNguoiToiDa());
//            existingLoaiPhong.setGia(loaiPhong.getGia());
//            loaiPhongService.saveLoaiPhong(existingLoaiPhong);
//        }
//        return "redirect:/loai-phong";
//    }

//    @GetMapping("/edit/{id}")
//    public String editLoaiPhongForm(@PathVariable int id, Model model) {
//        model.addAttribute("loaiPhong", loaiPhongService.getLoaiPhongById(id));
//        return "loaiphong/edit";
//    }
//
//    @PostMapping("/update/{id}")
//    public String updateLoaiPhong(@PathVariable int id, @ModelAttribute("loaiPhong") LoaiPhong loaiPhong) {
//        LoaiPhong existingLoaiPhong = loaiPhongService.getLoaiPhongById(id);
//        existingLoaiPhong.setMaLoaiPhong(loaiPhong.getMaLoaiPhong());
//        existingLoaiPhong.setTenLoaiPhong(loaiPhong.getTenLoaiPhong());
//        existingLoaiPhong.setMoTa(loaiPhong.getMoTa());
//        existingLoaiPhong.setSoLuongGiuong(loaiPhong.getSoLuongGiuong());
//        existingLoaiPhong.setSoNguoiToiDa(loaiPhong.getSoNguoiToiDa());
//        existingLoaiPhong.setGia(loaiPhong.getGia());
//
//        loaiPhongService.saveLoaiPhong(existingLoaiPhong);
//        return "redirect:/loai-phong";
//    }

    @GetMapping("/delete/{id}")
    public String deleteLoaiPhong(@PathVariable int id) {
        loaiPhongService.deleteLoaiPhong(id);
        return "redirect:/loaiphongs";
    }



}
