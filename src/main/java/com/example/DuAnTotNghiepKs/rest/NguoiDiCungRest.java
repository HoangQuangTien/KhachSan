package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.DatPhongDTO;
import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.DTO.NguoiDiCungDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.NguoiDiCung;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import com.example.DuAnTotNghiepKs.service.KhachHangService;
import com.example.DuAnTotNghiepKs.service.NguoiDiCungService;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public class NguoiDiCungRest {

    @Autowired
    private DatPhongService datPhongService;

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private NguoiDiCungService nguoiDiCungService;

    @Autowired
    private TaiKhoanService taiKhoanService;


   


    @GetMapping("/nguoidicung")
    public String showDatPhongList(Model model) {
        // Lấy tất cả thông tin đặt phòng chưa check-in
        List<DatPhong> datPhongs = datPhongService.getDatPhongChuaVaDaCheckIn();
        model.addAttribute("datPhongs", datPhongs);

        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg());
        }

        return "list/QuanLyDatPhong/nguoidicung"; // Đường dẫn tới trang HTML hiển thị danh sách đặt phòng
    }

    @GetMapping("/hien-thi-khach-di-cung")
    public String hienThiKhachDiCung(@RequestParam Integer id, Model model) {
        DatPhong datPhong = datPhongService.findById(id);
        if (datPhong == null) {
            return "redirect:/error"; // Hoặc xử lý lỗi theo cách khác
        }

        DatPhongDTO datPhongDTO = new DatPhongDTO();
        datPhongDTO.setIdDatPhong(datPhong.getIdDatPhong());
        datPhongDTO.setIdKhachHang(datPhong.getKhachHang().getId());
        datPhongDTO.setMaKhachHang(datPhong.getKhachHang().getMaKhachHang());
        datPhongDTO.setHoVaTen(datPhong.getKhachHang().getHoVaTen());
        datPhongDTO.setEmail(datPhong.getKhachHang().getEmail());
        datPhongDTO.setSoDienThoai(datPhong.getKhachHang().getSoDienThoai());
        datPhongDTO.setGioiTinh(datPhong.getKhachHang().isGioiTinh());

        model.addAttribute("datPhongDTO", datPhongDTO);
        model.addAttribute("idDatPhong", datPhongDTO.getIdDatPhong());

        // Lấy danh sách người đi cùng từ cơ sở dữ liệu
        List<NguoiDiCungDTO> nguoiDiCungList = nguoiDiCungService.findByDatPhongId(id);
        model.addAttribute("nguoiDiCungList", nguoiDiCungList); // Truyền danh sách vào model

        return "list/QuanLyDatPhong/nguoidicungdetail"; // Đường dẫn tới trang hiển thị danh sách người đi cùng
    }


    @PutMapping("/nguoidicung/{id}")
    @Transactional
    public ResponseEntity<String> capNhatKhachHang(@PathVariable Integer id, @RequestBody KhachHangDTO khachHangDTO) {
        // Kiểm tra sự tồn tại của khách hàng
        if (!khachHangService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khách hàng với ID: " + id);
        }

        // Kiểm tra thông tin khách hàng không được để trống
        if (khachHangDTO.getHoVaTen() == null ||
                khachHangDTO.getEmail() == null ||
                khachHangDTO.getSoDienThoai() == null) {
            return ResponseEntity.badRequest().body("Thông tin không được để trống.");
        }

        // Cập nhật thông tin khách hàng
        boolean updated = khachHangService.update1(id, khachHangDTO);

        // Trả về phản hồi dựa trên kết quả cập nhật
        if (updated) {
            return ResponseEntity.ok("Cập nhật thành công!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra trong quá trình cập nhật.");
        }
    }

    @GetMapping("/check-dat-phong/{id}")
    public ResponseEntity<String> checkDatPhong(@PathVariable Integer id) {
        DatPhong datPhong = datPhongService.findById(id);
        if (datPhong == null) {
            return ResponseEntity.badRequest().body("Mã đặt phòng không hợp lệ!");
        }
        if (datPhong.getNguoiDiCungList() == null || datPhong.getNguoiDiCungList().isEmpty()) {
            return ResponseEntity.ok("Mã đặt phòng hợp lệ nhưng không có người đi cùng.");
        }
        return ResponseEntity.ok("Mã đặt phòng hợp lệ.");
    }

    @PostMapping("/them-nguoi-di-cung")
    public ResponseEntity<?> themNguoiDiCung(@RequestBody NguoiDiCungDTO nguoiDiCungDTO) {
        Integer datPhongId = nguoiDiCungDTO.getDatPhongId();

        // Kiểm tra mã đặt phòng
        if (datPhongId == null || datPhongId <= 0) {
            return ResponseEntity.badRequest().body("Mã đặt phòng không hợp lệ!");
        }

        // Tìm đặt phòng theo ID
        DatPhong datPhong = datPhongService.findById(datPhongId);
        if (datPhong == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy mã đặt phòng!");
        }

        // Kiểm tra số lượng người đi cùng so với số người tối đa của loại phòng
        int soNguoiToiDa = datPhong.getLoaiPhong().getSoNguoiToiDa();
        int soNguoiHienTai = nguoiDiCungService.countByDatPhongId(datPhongId);

        if (soNguoiHienTai >= (soNguoiToiDa - 1)) { // Giảm 1 vì đã có 1 khách hàng chính
            return ResponseEntity.badRequest().body("Số người đi cùng đã đạt giới hạn cho loại phòng này!");
        }

        // Tạo đối tượng người đi cùng và lưu vào cơ sở dữ liệu
        NguoiDiCung nguoiDiCung = new NguoiDiCung();
        nguoiDiCung.setTenNguoiDiCung(nguoiDiCungDTO.getTenNguoiDiCung());
        nguoiDiCung.setSoCmnd(nguoiDiCungDTO.getSoCmnd());
        nguoiDiCung.setDatPhong(datPhong);

        try {
            nguoiDiCungService.save(nguoiDiCung); // Lưu vào CSDL

            // Trả về NguoiDiCungDTO để cập nhật vào form
            NguoiDiCungDTO responseDTO = new NguoiDiCungDTO();
            responseDTO.setTenNguoiDiCung(nguoiDiCung.getTenNguoiDiCung());
            responseDTO.setSoCmnd(nguoiDiCung.getSoCmnd());
            responseDTO.setDatPhongId(datPhongId);

            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi thêm người đi cùng: " + e.getMessage());
        }
    }
    @GetMapping("/check-cccd/{cmnd}")
    public ResponseEntity<Map<String, Boolean>> checkCccd(@PathVariable String cmnd) {
        boolean exists = nguoiDiCungService.isCccdExists(cmnd);  // Kiểm tra CCCD trong cơ sở dữ liệu
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/xoaTatCaCCCD/{idDatPhong}")
    public ResponseEntity<?> xoaTatCaCCCD(@PathVariable Long idDatPhong) {
        try {
            nguoiDiCungService.xoaTatCaCCCDTheoIdDatPhong(idDatPhong);
            return ResponseEntity.ok().body("Xóa tất cả CCCD thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa CCCD: " + e.getMessage());
        }
    }
}
