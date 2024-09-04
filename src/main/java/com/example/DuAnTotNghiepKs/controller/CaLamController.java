package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.entity.CaLamViec;
import com.example.DuAnTotNghiepKs.repository.CaLamViecRepo;
import com.example.DuAnTotNghiepKs.repository.LichLamViecRepo;
import com.example.DuAnTotNghiepKs.repository.NhanVienRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class CaLamController {

    @Autowired
    NhanVienRepo nhanVienRepo;

    @Autowired
    CaLamViecRepo caLamViecRepo;

    @Autowired
    LichLamViecRepo lichLamViecRepo;
    @Autowired
    private View error;


    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("list",caLamViecRepo.findAll());
    }

    @GetMapping("quan-ly-ca-lam-viec")
    public String giaoCa(@ModelAttribute CaLamViec caLamViec, Model model){
        model.addAttribute("list", caLamViecRepo.findAll());
        model.addAttribute("lichLamViec", lichLamViecRepo.findAll());
        return "caLam/home";
    }

    @PostMapping("/add-ca-lam")
    public String addNhanVien(@Valid @ModelAttribute CaLamViec caLamViec, BindingResult result,Model model){
        Optional<CaLamViec> exitingMa = caLamViecRepo.findBymaCaLamViec(caLamViec.getMaCaLamViec());
        if (exitingMa.isPresent()){
            result.rejectValue("maCaLamViec","errors.maCaLamViec","Mã ca làm việc không được để trùng");
        }
        if (result.hasErrors()){
            addAttributes(model);
            return "list/QuanLyNhanVien/home";
        }
        caLamViecRepo.save(caLamViec);
        return "redirect:/admin/quan-ly-ca-lam-viec";
    }

    @GetMapping("/delete-ca-lam")
    public String delString(@RequestParam(value = "maCaLamViec",required = false)String id, Model model){
        CaLamViec caLamViec = caLamViecRepo.findById(Integer.valueOf(id)).get();
        model.addAttribute("caLamViec", caLamViec);
        if (caLamViecRepo.existsById(Integer.valueOf(id))){
            caLamViecRepo.deleteById(Integer.valueOf(id));
        }
        return "redirect:/admin/quan-ly-ca-lam-viec";
    }

    @GetMapping("/viewUpdate-ca-lam")
    public String viewUpdate(@RequestParam(value = "maCaLamViec", required = false) String id, Model model) {
        if (id == null || id.trim().isEmpty()) {
            // Xử lý trường hợp ID null hoặc rỗng
            // Bạn có thể chuyển hướng hoặc trả về trang lỗi
            return "error/invalidId"; // Đảm bảo bạn có trang lỗi phù hợp
        }

        Optional<CaLamViec> optionalCaLamViec = caLamViecRepo.findById(Integer.valueOf(id));
        if (optionalCaLamViec.isPresent()) {
            model.addAttribute("caLamViec", optionalCaLamViec.get());
            return "caLam/update";
        } else {
            // Xử lý trường hợp không tìm thấy thực thể
            // Bạn có thể chuyển hướng hoặc trả về trang lỗi
            return "error/notFound"; // Đảm bảo bạn có trang lỗi phù hợp
        }
    }


    @PostMapping("/update-ca-lam")
    public String update(@Valid @ModelAttribute CaLamViec caLamViec, BindingResult result, Model model) {

        // Tìm kiếm thực thể theo ID
        Optional<CaLamViec> existingCaLam = caLamViecRepo.findBymaCaLamViec(caLamViec.getMaCaLamViec());

        if (existingCaLam.isPresent()) {
            CaLamViec existing = existingCaLam.get();

            // Kiểm tra nếu mã ca làm thay đổi
            if (!existing.getMaCaLamViec().equals(caLamViec.getMaCaLamViec())) {
                Optional<CaLamViec> existingMa = caLamViecRepo.findById(Integer.valueOf(caLamViec.getMaCaLamViec()));
                if (existingMa.isPresent()) {
                    result.rejectValue("maCaLamViec", "errors.maCaLamViec", "Mã ca làm việc đã tồn tại");
                    model.addAttribute("caLamViec", caLamViec);
                    return "caLam/update";
                }
            }

            // Kiểm tra lỗi ràng buộc (validation)
            if (result.hasErrors()) {
                model.addAttribute("caLamViec", caLamViec);
                return "caLam/update";
            }

            // Cập nhật các trường
            existing.setMaCaLamViec(caLamViec.getMaCaLamViec());
            existing.setTenCaLamViec(caLamViec.getTenCaLamViec());
            existing.setGioBatDau(caLamViec.getGioBatDau());
            existing.setGioKetThuc(caLamViec.getGioKetThuc());

            // Lưu thực thể cập nhật vào cơ sở dữ liệu
            caLamViecRepo.save(existing);

            return "redirect:/admin/quan-ly-ca-lam-viec";
        } else {
            // Xử lý trường hợp không tìm thấy thực thể
            result.reject("errors.caLamViec", "Ca làm việc không tồn tại");
            model.addAttribute("caLamViec", caLamViec);
            return "caLam/update";
        }
    }





    @PostMapping("/save-ca-lam")
    public String saveCaLamViec(@Valid @ModelAttribute CaLamViec caLamViec, BindingResult result, Model model) {
        boolean isUpdate = caLamViecRepo.existsById(Integer.valueOf(caLamViec.getMaCaLamViec()));

        // Check if the shift code already exists when adding a new shift
        if (!isUpdate) {
            Optional<CaLamViec> existingMa = caLamViecRepo.findBymaCaLamViec(caLamViec.getMaCaLamViec());
            if (existingMa.isPresent()) {
                result.rejectValue("maCaLamViec", "errors.maCaLamViec", "Mã ca làm việc không được để trùng");
            }
        }

        // Check for validation errors
        if (result.hasErrors()) {
            addAttributes(model); // Load necessary attributes for the view
            return "caLam/home";
        }

        // If it's an update, handle it separately
        if (isUpdate) {
            Optional<CaLamViec> existingCaLam = caLamViecRepo.findById(Integer.valueOf(caLamViec.getMaCaLamViec()));
            if (existingCaLam.isPresent()) {
                CaLamViec existing = existingCaLam.get();

                // Check if the shift code has changed and if the new code already exists
                if (!existing.getMaCaLamViec().equals(caLamViec.getMaCaLamViec())) {
                    Optional<CaLamViec> existingMa = caLamViecRepo.findBymaCaLamViec(caLamViec.getMaCaLamViec());
                    if (existingMa.isPresent()) {
                        result.rejectValue("maCaLamViec", "errors.maCaLamViec", "Mã ca làm việc đã tồn tại");
                    }
                }

                if (result.hasErrors()) {
                    model.addAttribute("caLamViec", caLamViec);
                    return "caLam/home";
                }

                // Update the fields
                existing.setMaCaLamViec(caLamViec.getMaCaLamViec());
                existing.setTenCaLamViec(caLamViec.getTenCaLamViec());
                existing.setGioBatDau(caLamViec.getGioBatDau());
                existing.setGioKetThuc(caLamViec.getGioKetThuc());
                caLamViecRepo.save(existing);
            } else {
                result.reject("error.caLamViec", "Ca làm không tồn tại");
                model.addAttribute("caLamViec", caLamViec);
                return "caLam/home";
            }
        } else {
            // If it's a new shift, save it directly
            caLamViecRepo.save(caLamViec);
        }

        return "redirect:/admin/quan-ly-ca-lam-viec";
    }

}
