package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.entity.DiaChiNhanVien;
import com.example.DuAnTotNghiepKs.entity.NhanVien;
import com.example.DuAnTotNghiepKs.repository.DiaChiNhanVienRepo;
import com.example.DuAnTotNghiepKs.repository.NhanVienRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class NhanVienController {


    @Autowired
    private NhanVienRepo nhanVienRepo;

    @Autowired
    private DiaChiNhanVienRepo diaChiNhanVienRepo;

    @ModelAttribute()
    public void getModel(Model model) {
        model.addAttribute("list", nhanVienRepo.findAll());
    }

    @GetMapping("quan-ly-nhan-vien")
    public String loadEmployee(
            @RequestParam(defaultValue = "", name = "keyword") String keyword,
            @RequestParam(defaultValue = "0", name = "page") int number,
            @RequestParam(defaultValue = "true", name = "trangThai") String trangThai,
            @ModelAttribute NhanVien nhanVien, Model model) {

        // Tạo Pageable để phân trang, 5 là số lượng nhân viên trên mỗi trang
        Pageable pageable = PageRequest.of(number, 5, Sort.by(Sort.Direction.DESC, "idNhanVien"));
        Page<NhanVien> page;

        // Kiểm tra xem trạng thái có được cung cấp hay không
        if (trangThai.isEmpty()) {
            // Nếu trạng thái không được cung cấp, chỉ lọc theo từ khóa
            page = nhanVienRepo.findByKeyword(keyword, pageable);
        } else {
            // Nếu trạng thái được cung cấp, lọc theo từ khóa và trạng thái
            boolean status = Boolean.parseBoolean(trangThai);
            page = nhanVienRepo.findByKeywordAndTrangThai(keyword, status, pageable);
        }

        // Thêm các thuộc tính vào model để gửi về view
        model.addAttribute("list", page.getContent()); // Danh sách nhân viên trên trang hiện tại
        model.addAttribute("currentPage", number); // Trang hiện tại
        model.addAttribute("totalPages", page.getTotalPages()); // Tổng số trang
        model.addAttribute("keyword", keyword); // Từ khóa hiện tại
        model.addAttribute("trangThai", trangThai); // Trạng thái hiện tại

        return "list/QuanLyNhanVien/home"; // Trả về fragment HTML để cập nhật bảng
    }

    @GetMapping("/delete")
    public String delee(@RequestParam(value = "idNhanVien")Integer id){
        DiaChiNhanVien vien = new DiaChiNhanVien();
        if (vien.getIdDiaChi() != null){
            diaChiNhanVienRepo.deleteById(vien.getIdDiaChi());
        }
        nhanVienRepo.deleteById(id);
        return "redirect:/admin/quan-ly-nhan-vien";
    }

    @GetMapping("/add")
    public String viewErrors(Model model){
        model.addAttribute("nhanVien",new NhanVien());
        return "nhanVien/home";
    }

    @PostMapping("/add-employee")
    public String addEmployee(@Valid @ModelAttribute NhanVien nhanVien, BindingResult errors, RedirectAttributes redirectAttributes) {

        Optional<NhanVien> existingSdt = nhanVienRepo.findBysoDienThoai(nhanVien.getSoDienThoai());
        if (existingSdt.isPresent()) {
            errors.rejectValue("soDienThoai", "error.nhanVien", "Số điện thoại đã tồn tại");
        }
        Optional<NhanVien> extingEmail = nhanVienRepo.findByemail(nhanVien.getEmail());
        if (extingEmail.isPresent()){
            errors.rejectValue("email","error.nhanVien","Email đã tồn tại");
        }

        Date ngaySinhDate = nhanVien.getNgaySinh();

        if (ngaySinhDate != null) {
            // Chuyển đổi Date thành LocalDate
            LocalDate ngaySinh = ngaySinhDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();
            Period age = Period.between(ngaySinh, today);

            // Kiểm tra độ tuổi
            if (age.getYears() < 18) {
                errors.rejectValue("ngaySinh", "error.nhanVien", "Nhân viên phải đủ 18 tuổi");
            }
        } else {
            // Xử lý khi ngaySinhDate là null
            errors.rejectValue("ngaySinh", "error.nhanVien", "Ngày sinh không được để trống");
        }

        if (errors.hasErrors()) {
            List<String> errorMessages = errors.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/admin/add"; // Đảm bảo URL này đúng với trang thêm nhân viên của bạn
        }
        //ma gen tự động
        String newNhanVien = generateMaNhanVien();
        nhanVien.setMaNhanVien(newNhanVien);
        nhanVienRepo.save(nhanVien);
        return "redirect:/admin/quan-ly-nhan-vien";
    }




    @GetMapping("/viewUpdate-employee")
    public String viewUpdateEmployee(
            @ModelAttribute NhanVien nhanVien,
            @RequestParam(value = "idNhanVien") Integer id, Model model) {
        nhanVien = nhanVienRepo.findById(id).get();
        model.addAttribute("nhanVien",nhanVien);
        return "list/QuanLyNhanVien/update";

    }




    @PostMapping("/update-employee")
    public String update(@Valid @ModelAttribute("nhanVien") NhanVien nhanVien, BindingResult errors, Model model) {

        Optional<NhanVien> existingNhanVien = nhanVienRepo.findById(nhanVien.getIdNhanVien());

        Date ngaySinhDate = nhanVien.getNgaySinh();

        if (ngaySinhDate != null) {
            // Chuyển đổi Date thành LocalDate
            LocalDate ngaySinh = ngaySinhDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();
            Period age = Period.between(ngaySinh, today);

            // Kiểm tra độ tuổi
            if (age.getYears() < 18) {
                errors.rejectValue("ngaySinh", "error.nhanVien", "Nhân viên phải đủ 18 tuổi");
            }
        } else {
            // Xử lý khi ngaySinhDate là null
            errors.rejectValue("ngaySinh", "error.nhanVien", "Ngày sinh không được để trống");
        }
        if (existingNhanVien.isPresent()) {
            NhanVien existing = existingNhanVien.get();

            // Kiểm tra nếu maNhanVien thay đổi
            if (!existing.getMaNhanVien().equals(nhanVien.getMaNhanVien())) {
                Optional<NhanVien> existingMa = nhanVienRepo.findBymaNhanVien(nhanVien.getMaNhanVien());
                if (existingMa.isPresent()) {
                    errors.rejectValue("maNhanVien", "error.nhanVien", "Mã nhân viên đã tồn tại");
                }
            }

            // Kiểm tra nếu soDienThoai thay đổi
            if (!existing.getSoDienThoai().equals(nhanVien.getSoDienThoai())) {
                Optional<NhanVien> existingSdt = nhanVienRepo.findBysoDienThoai(nhanVien.getSoDienThoai());
                if (existingSdt.isPresent()) {
                    errors.rejectValue("soDienThoai", "error.nhanVien", "Số điện thoại đã tồn tại");
                }
            }


            if (errors.hasErrors()) {
                model.addAttribute("nhanVien", nhanVien);
                return "list/QuanLyNhanVien/update";
            }

            // Cập nhật thông tin nhân viên
            existing.setMaNhanVien(nhanVien.getMaNhanVien());
            existing.setHoTen(nhanVien.getHoTen());
            existing.setSoDienThoai(nhanVien.getSoDienThoai());
            existing.setNgaySinh(nhanVien.getNgaySinh());
            existing.setLiDo(nhanVien.getLiDo());
            existing.setTrangThai(nhanVien.getTrangThai());
            existing.setGioiTinh(nhanVien.getGioiTinh());
            existing.setThanhPho(nhanVien.getThanhPho());
            existing.setPhuongXa(nhanVien.getPhuongXa());
            existing.setQuanHuyen(nhanVien.getQuanHuyen());
            // Thêm các trường khác nếu cần

            nhanVienRepo.save(existing);
            return "redirect:/admin/quan-ly-nhan-vien";
        } else {
            errors.reject("error.nhanVien", "Nhân viên không tồn tại");
            model.addAttribute("nhanVien", nhanVien);
            return "list/QuanLyNhanVien/home";
        }
    }


    @PostMapping("/save-employee")
    public String saveEmployee(@Valid @ModelAttribute NhanVien nhanVien, BindingResult errors,
                               @RequestParam String action,
                               @RequestParam(defaultValue = "0", name = "page") int number, Model model) {

        //phân trang
        Pageable pageable = PageRequest.of(number, 5, Sort.by(Sort.Direction.DESC, "idNhanVien"));
        Page<NhanVien> page = nhanVienRepo.findAll(pageable);

        //validate ngày sinh phải lớn hơn 18 tuổi
        Date ngaySinhDate = nhanVien.getNgaySinh();

        if (ngaySinhDate != null) {
            // Chuyển đổi Date thành LocalDate
            LocalDate ngaySinh = ngaySinhDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();
            Period age = Period.between(ngaySinh, today);

            // Kiểm tra độ tuổi
            if (age.getYears() < 18) {
                errors.rejectValue("ngaySinh", "error.nhanVien", "Nhân viên phải đủ 18 tuổi");
            }
        } else {
            // Xử lý khi ngaySinhDate là null
            errors.rejectValue("ngaySinh", "error.nhanVien", "Ngày sinh không được để trống");
        }


        // Các kiểm tra và xử lý khác...

        boolean isUpdate = "update".equals(action) && nhanVien.getIdNhanVien() != null
                && nhanVienRepo.existsById(nhanVien.getIdNhanVien());

        if (isUpdate) {
            // Update operation
            Optional<NhanVien> existingNhanVien = nhanVienRepo.findById(nhanVien.getIdNhanVien());

            if (existingNhanVien.isPresent()) {
                NhanVien existing = existingNhanVien.get();

                // Check if maNhanVien is changing and if it already exists
                if (!existing.getMaNhanVien().equals(nhanVien.getMaNhanVien())) {
                    Optional<NhanVien> existingMa = nhanVienRepo.findBymaNhanVien(nhanVien.getMaNhanVien());
                    if (existingMa.isPresent()) {
                        errors.rejectValue("maNhanVien", "error.nhanVien", "Mã nhân viên đã tồn tại");
                    }
                }

                // Check if soDienThoai is changing and if it already exists
                if (!existing.getSoDienThoai().equals(nhanVien.getSoDienThoai())) {
                    Optional<NhanVien> existingSdt = nhanVienRepo.findBysoDienThoai(nhanVien.getSoDienThoai());
                    if (existingSdt.isPresent()) {
                        errors.rejectValue("soDienThoai", "error.nhanVien", "Số điện thoại đã tồn tại");
                    }
                }

                if (errors.hasErrors()) {
                    model.addAttribute("nhanVien", nhanVien);
                    model.addAttribute("listDiaChi", diaChiNhanVienRepo.findAll()); // Add this to populate dropdown
                    return "list/QuanLyNhanVien/home";
                }

                // Update existing employee
                existing.setMaNhanVien(nhanVien.getMaNhanVien());
                existing.setHoTen(nhanVien.getHoTen());
                existing.setSoDienThoai(nhanVien.getSoDienThoai());
                existing.setNgaySinh(nhanVien.getNgaySinh());
                existing.setEmail(nhanVien.getEmail());

                existing.setTrangThai(nhanVien.getTrangThai());
                existing.setGioiTinh(nhanVien.getGioiTinh());

                nhanVienRepo.save(existing);
            } else {
                errors.reject("error.nhanVien", "Nhân viên không tồn tại");
                model.addAttribute("nhanVien", nhanVien);
                model.addAttribute("list", page.getContent()); // Danh sách nhân viên trên trang hiện tại
                model.addAttribute("listDiaChi", diaChiNhanVienRepo.findAll());
                model.addAttribute("currentPage", number); // Trang hiện tại
                model.addAttribute("totalPages", page.getTotalPages()); // Tổng số trang
                return "list/QuanLyNhanVien/home";
            }
        } else {
            // Add operation
            //Gen tự động mà nhân viên
            if (nhanVien.getIdNhanVien() == null) {
                String newMaNhanVien = generateMaNhanVien();
                nhanVien.setMaNhanVien(newMaNhanVien);
            }
            Optional<NhanVien> existingMa = nhanVienRepo.findBymaNhanVien(nhanVien.getMaNhanVien());
            if (existingMa.isPresent()) {
                errors.rejectValue("maNhanVien", "error.nhanVien", "Mã nhân viên đã tồn tại");
            }
            Optional<NhanVien> existingSdt = nhanVienRepo.findBysoDienThoai(nhanVien.getSoDienThoai());
            if (existingSdt.isPresent()) {
                errors.rejectValue("soDienThoai", "error.nhanVien", "Số điện thoại đã tồn tại");
            }
            if (errors.hasErrors()) {
                model.addAttribute("nhanVien", nhanVien);
                model.addAttribute("list", page.getContent()); // Danh sách nhân viên trên trang hiện tại
                model.addAttribute("listDiaChi", diaChiNhanVienRepo.findAll());
                model.addAttribute("currentPage", number); // Trang hiện tại
                model.addAttribute("totalPages", page.getTotalPages()); // Tổng số trang
                return "list/QuanLyNhanVien/home";
            }
            nhanVienRepo.save(nhanVien);
        }
        return "redirect:/admin/quan-ly-nhan-vien";
    }

    //hàm mã tự gen
    private String generateMaNhanVien() {
        String prefix = "NV";
        NhanVien lastNhanVien = nhanVienRepo.findTopByOrderByIdNhanVienDesc();
        int nextId = lastNhanVien != null ? (int) (lastNhanVien.getIdNhanVien() + 1) : 1;
        return prefix + String.format("%03d", nextId);
    }

}
