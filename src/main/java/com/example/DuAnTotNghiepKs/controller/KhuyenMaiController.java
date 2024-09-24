package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import com.example.DuAnTotNghiepKs.service.KhuyenMaiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@Controller
@RequestMapping("/admin/quan-ly-khuyen-mai")
public class KhuyenMaiController {
    @Autowired
    private KhuyenMaiService khuyenMaiService;

//    @Autowired
//    private KhuyenMaiRepo khuyenMaiRepo;

    @GetMapping()
    public String showKhuyenMaiPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        Sort sort = Sort.by(Sort.Direction.DESC, "ngayKetThuc");
        Page<KhuyenMai> khuyenMaiPage = khuyenMaiService.getKhuyenMaiPage(page, size,sort);
        model.addAttribute("khuyenMaiPage", khuyenMaiPage);
        return "list/QuanLyKhuyenMai/KhuyenMai";
    }

//    @GetMapping()
//    public String showKhuyenMaiPage(Model model) {
//        List<KhuyenMai> khuyenMais = khuyenMaiService.getAllKhuyenMai();
//        model.addAttribute("khuyenMais",khuyenMais);
//        return "khuyenmai";
//    }

//    @GetMapping
//    public List<KhuyenMai> getAllKhuyenMai() {
//        return khuyenMaiService.getAllKhuyenMai();
//
//    }

    @GetMapping("/view-add")
    public String  showAdd(Model model) {
        model.addAttribute("khuyenMai", new KhuyenMai());
        return "list/QuanLyKhuyenMai/add";

    }

    @PostMapping("/add")
    public String addKhuyenMai(@ModelAttribute @Valid KhuyenMai khuyenMai, BindingResult result) {

        // Ngày hiện tại
        LocalDate today = LocalDate.now();
        LocalDate ngayBatDau = khuyenMai.getNgayBatDau() != null ?
                khuyenMai.getNgayBatDau().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate ngayKetThuc = khuyenMai.getNgayKetThuc() != null ?
                khuyenMai.getNgayKetThuc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        // Kiểm tra ngày bắt đầu phải lớn hơn ngày hiện tại
        if (ngayBatDau != null && ngayBatDau.isBefore(today)) {
            result.rejectValue("ngayBatDau", "error.khuyenMai", "Ngày bắt đầu phải lớn hơn ngày hiện tại");
        }

        // Kiểm tra ngày kết thúc phải lớn hơn ngày bắt đầu
        if (ngayKetThuc != null && ngayBatDau != null && ngayKetThuc.isBefore(ngayBatDau)) {
            result.rejectValue("ngayKetThuc", "error.khuyenMai", "Ngày kết thúc phải lớn hơn ngày bắt đầu");
        }

        if (result.hasErrors()) {
            return "list/QuanLyKhuyenMai/add";
        }
        if (khuyenMaiService.existsByMaKhuyenMai(khuyenMai.getMaKhuyenMai())) {
            result.rejectValue("maKhuyenMai", "error.khuyenMai", "Mã khuyến mãi đã tồn tại");
            return "list/QuanLyKhuyenMai/add";
        }
        khuyenMai.getMaKhuyenMai();
        khuyenMaiService.saveKhuyenMai(khuyenMai);
        return "redirect:/admin/quan-ly-khuyen-mai";
    }

//    @PostMapping("/add")
//    public String addKhuyenMai(@ModelAttribute KhuyenMai khuyenMai,Model model){
//        khuyenMaiService.saveKhuyenMai(khuyenMai);
//        return"redirect:/khuyenmaii";
//    }
//    @PostMapping("/add")
//    public String addKhuyenMai(@Valid @ModelAttribute KhuyenMai khuyenMai, BindingResult result, Model model) {
//        // Kiểm tra lỗi xác thực
//        if (result.hasErrors()) {
//            // Nếu có lỗi, trả về lại trang với thông báo lỗi và dữ liệu đã nhập
//            model.addAttribute("khuyenMai", khuyenMai);
//            return "addKM"; // Thay đổi tên trang HTML tương ứng với trang hiện tại của bạn
//        }
//
//        // Nếu không có lỗi, lưu khuyến mãi vào cơ sở dữ liệu
//        khuyenMaiService.saveKhuyenMai(khuyenMai);
//
//        // Redirect đến trang danh sách khuyến mãi sau khi thêm thành công
//        return "redirect:/khuyenmaii";
//    }



    @GetMapping("/edit/{id}")
    public String showEditKhuyenMai(@PathVariable("id")  Integer id , Model model) {
        KhuyenMai khuyenMai=khuyenMaiService.getKhuyenMaiById(id);
//        System.out.println("Ngày Bắt Đầu: " + khuyenMai.getNgayBatDau());
//        System.out.println("Ngày Kết Thúc: " + khuyenMai.getNgayKetThuc());
        model.addAttribute("khuyenMai",khuyenMai);
        return "list/QuanLyKhuyenMai/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateKhuyenMai(@Valid @ModelAttribute KhuyenMai khuyenMai, BindingResult result, @PathVariable Integer id) {

        // Ngày hiện tại
        LocalDate today = LocalDate.now();
        LocalDate ngayBatDau = khuyenMai.getNgayBatDau() != null ?
                khuyenMai.getNgayBatDau().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate ngayKetThuc = khuyenMai.getNgayKetThuc() != null ?
                khuyenMai.getNgayKetThuc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        // Kiểm tra ngày bắt đầu phải lớn hơn ngày hiện tại
        if (ngayBatDau != null && ngayBatDau.isBefore(today)) {
            result.rejectValue("ngayBatDau", "error.khuyenMai", "Ngày bắt đầu phải lớn hơn ngày hiện tại");
        }

        // Kiểm tra ngày kết thúc phải lớn hơn ngày bắt đầu
        if (ngayKetThuc != null && ngayBatDau != null && ngayKetThuc.isBefore(ngayBatDau)) {
            result.rejectValue("ngayKetThuc", "error.khuyenMai", "Ngày kết thúc phải lớn hơn ngày bắt đầu");
        }

        // Kiểm tra lỗi và trả về trang chỉnh sửa nếu có lỗi
        if (result.hasErrors()) {
            return "list/QuanLyKhuyenMai/edit";
        }

        // Lưu thông tin khuyến mãi nếu không có lỗi
        khuyenMaiService.updateKhuyenMai(id,khuyenMai);
        return "redirect:/admin/quan-ly-khuyen-mai";
    }



    @PostMapping("/delete/{id}")
    public String deleteKhuyenMai(@PathVariable Integer id) {
        khuyenMaiService.deleteKhuyenMaiById(id);
        return "redirect:/admin/quan-ly-khuyen-mai" ;
    }

//    @GetMapping("/search")
//    public String searchKhuyenMai(@RequestParam("keyword") String keyword, Model model) {
//        List<KhuyenMai> khuyenMais = khuyenMaiService.searchKhuyenMai(keyword);
//        model.addAttribute("khuyenMais", khuyenMais);
//        return "khuyenmai";
//    }

    @GetMapping("/search")
    public String searchKhuyenMai(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "trangThai") String trangThai,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayKetThuc"));
        Page<KhuyenMai> khuyenMaiPage = khuyenMaiService.searchKhuyenMai(keyword, trangThai, page, size);

        model.addAttribute("khuyenMaiPage", khuyenMaiPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThai", trangThai);

        return "list/QuanLyKhuyenMai/KhuyenMai";
    }


}
