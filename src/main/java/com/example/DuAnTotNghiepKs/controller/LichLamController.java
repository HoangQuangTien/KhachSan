//package com.example.DuAnTotNghiepKs.controller;
//
//import com.example.DuAnTotNghiepKs.entity.LichLamViec;
//import com.example.DuAnTotNghiepKs.entity.NhanVien;
//import com.example.DuAnTotNghiepKs.repository.CaLamViecRepo;
//import com.example.DuAnTotNghiepKs.repository.LichLamViecRepo;
//import com.example.DuAnTotNghiepKs.repository.NhanVienRepo;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/admin")
//public class LichLamController {
//
//    @Autowired
//    CaLamViecRepo caLamViecRepo;
//
//    @Autowired
//    NhanVienRepo nhanVienRepo;
//    @Autowired
//    private LichLamViecRepo lichLamViecRepo;
//
//    @ModelAttribute
//    public void getModel(Model model){
//        model.addAttribute("listNhanVien",nhanVienRepo.findAll());
//        model.addAttribute("listCaLam",caLamViecRepo.findAll());
//    }
//
//    @GetMapping("/quan-ly-lich-lam-viec")
//    public String load(@ModelAttribute LichLamViec lichLamViec, Model model){
//        List<LichLamViec> list = lichLamViecRepo.findAll();
//        model.addAttribute("list",list);
//        getModel(model);
//        return "list/QuanLyNhanVien/home";
//    }
//
//    @PostMapping("/add-lich-lam")
//    public String save(@Valid @ModelAttribute LichLamViec lichLamViec, BindingResult result, Model model){
//        Optional<LichLamViec> exitingma = lichLamViecRepo.findBymaLichLamViec(lichLamViec.getMaLichLamViec());
//        if (exitingma.isPresent()){
//            result.rejectValue("maLichLamViec","errors.maLichLamViec","Mã lịch làm việc đã tồn tại");
//        }
//        if (result.hasErrors()){
//            model.addAttribute("lichLamViec",lichLamViec);
//            model.addAttribute("list",lichLamViecRepo.findAll());
//            getModel(model);
//            return "lichLam/home";
//        }
//        lichLamViecRepo.save(lichLamViec);
//        return "redirect:/admin/quan-ly-lich-lam-viec";
//    }
//
//    @GetMapping("/viewUpdate-lich-lam")
//    public String viewUpdate(@ModelAttribute LichLamViec lichLamViec,
//                             @RequestParam(value = "idLichLamViec",required = false)Integer id,Model model){
//        model.addAttribute("lichLamViec",lichLamViecRepo.findById(id));
//        getModel(model);
//        return "lichLam/update";
//    }
//
//    @PostMapping("/update-lich-lam")
//    public String updateLichLam(@Valid @ModelAttribute LichLamViec lichLamViec,BindingResult result,Model model){
//        if (result.hasErrors()){
//            model.addAttribute("lichLamViec",lichLamViec);
//            model.addAttribute("list",lichLamViecRepo.findAll());
//            getModel(model);
//            return "lichLam/home";
//        }
//        lichLamViecRepo.save(lichLamViec);
//        return "redirect:/admin/quan-ly-lich-lam-viec";
//    }
//
//    @GetMapping("/delete-lich-lam")
//    public String delete(@RequestParam(value = "idLichLamViec",required = false)Integer id){
//        lichLamViecRepo.deleteById(id);
//        return "redirect:/admin/quan-ly-lich-lam-viec";
//    }
//
//    @PostMapping("/save-lich-lam")
//    public String saveLichLam(@Valid @ModelAttribute LichLamViec lichLamViec, BindingResult errors, @RequestParam String action, Model model){
//        boolean isUpdate = "update".equals(action) && lichLamViec.getIdLichLamViec() != null && nhanVienRepo.existsById(lichLamViec.getIdLichLamViec());
//
//        if (isUpdate) {
//            // Update operation
//            Optional<LichLamViec> existingLichLam = lichLamViecRepo.findById(lichLamViec.getIdLichLamViec());
//
//            if (existingLichLam.isPresent()) {
//                LichLamViec existing = existingLichLam.get();
//
//                // Check if maLichLam is changing and if it already exists
//                if (!existing.getMaLichLamViec().equals(lichLamViec.getMaLichLamViec())) {
//                    Optional<LichLamViec> existingMa = lichLamViecRepo.findBymaLichLamViec(lichLamViec.getMaLichLamViec());
//                    if (existingMa.isPresent()) {
//                        errors.rejectValue("maLichLamViec", "error.lichLamViec", "Mã lich lam việc đã đã tồn tại");
//                    }
//                }
//
//
//                if (errors.hasErrors()) {
//                    model.addAttribute("lichLamViec", lichLamViec);
//                    return "lichLam/home";
//                }
//
//                // Update existing employee
//                existing.setMaLichLamViec(lichLamViec.getMaLichLamViec());
//                existing.setNgay(lichLamViec.getNgay());
//                existing.setNhanVien(lichLamViec.getNhanVien());
//                existing.setCaLamViec(lichLamViec.getCaLamViec());
//                // Update other fields if needed
//
//                lichLamViecRepo.save(existing);
//            } else {
//                errors.reject("error.lichLamViec", "Lịch làm không tồn tại");
//                model.addAttribute("lichLamViec", lichLamViec);
//                return "lichLam/home";
//            }
//        } else {
//            // Add operation
//            Optional<LichLamViec> existingMa = lichLamViecRepo.findBymaLichLamViec(lichLamViec.getMaLichLamViec());
//            if (existingMa.isPresent()) {
//                errors.rejectValue("maLichLamViec", "error.lichLamViec", "Mã lịch làm đã tồn tại");
//            }
//            if (errors.hasErrors()) {
//                return "lichLam/home";
//            }
//            lichLamViecRepo.save(lichLamViec);
//        }
//        return "redirect:/admin/quan-ly-lich-lam-viec";
//    }
//}
