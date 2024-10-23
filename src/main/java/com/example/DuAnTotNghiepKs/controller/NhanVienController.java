package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.entity.DiaChiNhanVien;
import com.example.DuAnTotNghiepKs.entity.NhanVien;
import com.example.DuAnTotNghiepKs.entity.TaiKhoan;
import com.example.DuAnTotNghiepKs.repository.DiaChiNhanVienRepo;
import com.example.DuAnTotNghiepKs.repository.NhanVienRepo;
import com.example.DuAnTotNghiepKs.repository.TaiKhoanRepo;
import com.example.DuAnTotNghiepKs.service.NhanVienService;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/quan-ly-nhan-vien")
public class NhanVienController {


    private static final Logger log = LoggerFactory.getLogger(NhanVienController.class);
    @Autowired
    private NhanVienRepo nhanVienRepo;

    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private NhanVienService nhanVienSerVice;


    @GetMapping
    public String loadEmployee(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size
            , Model model) {

        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"idNhanVien"));
        Page<NhanVienDTO> nhanVienDTOS = nhanVienSerVice.getAll(pageable);
        model.addAttribute("nhanViens", nhanVienDTOS.getContent()); // Lấy danh sách nhân viên
        model.addAttribute("currentPage", page); // Truyền thêm thông tin trang hiện tại
        model.addAttribute("totalPages", nhanVienDTOS.getTotalPages()); // Tổng số trang
        model.addAttribute("size", size); // Số mục trên mỗi trang
        return "list/QuanLyNhanVien/home";
    }



    //tìm kiếm contrller
    @GetMapping("/search")
    public ResponseEntity<Page<NhanVienDTO>> searchEmployee(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "5") int size,
            @RequestParam(value = "keyword",required = false) String keyword) {

        Pageable pageable = PageRequest.of(page,size);
        Page<NhanVienDTO> nhanVienDTOS = nhanVienSerVice.searchEmployees(keyword,pageable);

        return ResponseEntity.ok(nhanVienDTOS); // Trả về danh sách nhân viên phân trang
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<NhanVienDTO>> filterTrangThai(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "5") int size,
            @RequestParam(value = "trangThai",defaultValue = "true") Boolean trangThai){

        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"idNhanVien"));
        Page<NhanVienDTO> nhanVienDTOS = nhanVienSerVice.filterTrangThai(trangThai,pageable);
        return ResponseEntity.ok(nhanVienDTOS);
    }




    @PostMapping("/add")
    public ResponseEntity<?> save(
            @RequestParam("tenDangNhap") String tenDangNhap,
            @RequestParam("matKhau") String matKhau,
            @RequestParam("maNhanVien") String maNhanVien,
            @RequestParam("hoTen") String hoTen,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("ngaySinh") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySinh,
            @RequestParam("email") String email,
            @RequestParam("thanhPho") String thanhPho,
            @RequestParam("quanHuyen") String quanHuyen,
            @RequestParam("phuongXa") String phuongXa,
            @RequestParam("img") MultipartFile img,
            @RequestParam("trangThai") String trangThai,
            @RequestParam("gioiTinh") String gioiTinh
    ){
        try {

            // Kiểm tra điều kiện
            if (!isValidPhoneNumber(soDienThoai)){
                return ResponseEntity.badRequest().body(Map.of("error","Số điện thoại phải là số và phải nhiều hơn 10 số."));
            }
            if (isPhoneNumberExists(soDienThoai)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số điện thoại đã tồn tại."));
            }
            if (isEmailExists(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email đã tồn tại."));
            }
            if (!isValidEmail(email)){
                return ResponseEntity.badRequest().body(Map.of("error","Email không đúng định dạng."));
            }
            if (!isMeaningfulEmail(email)){
                return ResponseEntity.badRequest().body(Map.of("error","email phải có ý nghĩa."));
            }
            if (!isOver18(ngaySinh)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Người dùng phải lớn hơn 18 tuổi."));
            }
            if (!isOver200(ngaySinh)){
                return ResponseEntity.badRequest().body(Map.of("error","Người dùng đã không được quá 200 tuổi"));
            }
            if (img.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tệp ảnh không được để trống."));
            }

            // Tạo đường dẫn thư mục static/images
            String uploadDir = "src/main/resources/static";
            String fileName = img.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // Lưu file ảnh vào hệ thống
            Files.copy(img.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Tách họ và tên
            String[] hoTenParts = hoTen.trim().split(" ");
            String ho = hoTenParts[0]; // Giả sử họ là phần đầu tiên
            String ten = hoTenParts[hoTenParts.length - 1]; // Giả sử tên là phần cuối cùng

            // Khởi tạo tên đăng nhập
            tenDangNhap = genTenDangNhap(ho, ten);

            // Giả sử bạn có một đối tượng Date

            // Chuyển đổi Date thành String năm sinh
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String namSinh = sdf.format(ngaySinh);
            // Tạo mật khẩu ngẫu nhiên
            matKhau = generatePasswordFromNameAndBirthYear(ten,namSinh);

            TaiKhoanDTO taiKhoanDTO = new TaiKhoanDTO(tenDangNhap,matKhau);

            // Khởi tạo maNhanVien
            String generateMaNhanVien = generateMaNhanVien();

            // Create NhanVienDTO
            NhanVienDTO nhanVienDTO = NhanVienDTO.builder()
                    .maNhanVien(generateMaNhanVien)
                    .hoTen(hoTen)
                    .ngaySinh(ngaySinh)
                    .soDienThoai(soDienThoai)
                    .email(email)
                    .thanhPho(thanhPho)
                    .quanHuyen(quanHuyen)
                    .phuongXa(phuongXa)
                    .img("/img/" + fileName)
                    .trangThai(Boolean.valueOf(trangThai))
                    .gioiTinh(Boolean.parseBoolean(gioiTinh))
                    .taiKhoanDTO(taiKhoanDTO)
                    .build();

            System.out.println("NhanVienDTO: "+ nhanVienDTO.toString());
            nhanVienSerVice.save(nhanVienDTO);

            // Gửi email khi thêm nhân viên thành công
            String to = email; // Email của nhân viên
            String subject = "Thêm nhân viên thành công";
            String text = "<html>" +
                    "<body>" +
                    "<h2>Chúc mừng!</h2>" +
                    "<p>Nhân viên <strong>" + hoTen + "</strong> đã được nhận vào khách sạn <strong>DRAGONBALL HOTEL</strong>.</p>" +
                    "<p><strong>Tài khoản:</strong> " + tenDangNhap + "</p>" +
                    "<p><strong>Mật khẩu:</strong> " + matKhau + "</p>" +
                    "<p>Chúc bạn có một trải nghiệm tuyệt vời!</p>" +
                    "<p>Trân trọng,<br>Đội ngũ quản lý khách sạn <strong>DRAGONBALL HOTEL</strong></p>" +
                    "</body>" +
                    "</html>";
            nhanVienSerVice.sendEmail(to, subject, text);

            return ResponseEntity.ok(Map.of("success","Thêm nhân viên thành công!"));

        }catch (Exception e){
            System.out.println("Lỗi:"+e.getMessage());
            log.error("Error adding employee",e);
            return ResponseEntity.badRequest().body(Map.of("error","Đã xảy ra lỗi: ")+e.getMessage());
        }
    }


    @PostMapping("/update")
    public ResponseEntity<?> update(
            @RequestParam("idNhanVien") Integer idNhanVien,
            @RequestParam("maNhanVien") String maNhanVien,
            @RequestParam("tenDangNhap") String tenDangNhap,
            @RequestParam("matKhau") String matKhau,
            @RequestParam("hoTen") String hoTen,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("ngaySinh") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySinh,
            @RequestParam("email") String email,
            @RequestParam("thanhPho") String thanhPho,
            @RequestParam("quanHuyen") String quanHuyen,
            @RequestParam("phuongXa") String phuongXa,
            @RequestParam("img") MultipartFile img,
            @RequestParam("trangThai") Boolean trangThai,
            @RequestParam("gioiTinh") String gioiTinh
    ) {
        try {
            // Kiểm tra các điều kiện hợp lệ
            if (!isValidPhoneNumber(soDienThoai)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số điện thoại phải là số và phải nhiều hơn 10 số."));
            }
            if (isEmailExistsUpDate(soDienThoai, idNhanVien)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số điện thoại đã tồn tại."));
            }
            if (isEmailExistsUpDate(email, idNhanVien)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email đã tồn tại."));
            }
            if (!isValidEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email không đúng định dạng."));
            }
            if (!isMeaningfulEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "email phải có ý nghĩa."));
            }
            if (!isOver18(ngaySinh)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Người dùng phải lớn hơn 18 tuổi."));
            }
            if (!isOver200(ngaySinh)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Người dùng đã không được quá 200 tuổi"));
            }

            // Lấy thông tin nhân viên hiện tại
            Optional<NhanVien> existingNhanVien = nhanVienSerVice.findById(idNhanVien);
            if (existingNhanVien == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nhân viên không tồn tại."));
            }

            TaiKhoanDTO taiKhoanDTO = new TaiKhoanDTO();
            taiKhoanDTO.setTenDangNhap(tenDangNhap);
            taiKhoanDTO.setMatKhau(matKhau);

            // Khởi tạo maNhanVien
            maNhanVien = generateMaNhanVien();

            NhanVienDTO nhanVienDTO = new NhanVienDTO();
            nhanVienDTO.setIdNhanVien(idNhanVien);
            nhanVienDTO.setMaNhanVien(maNhanVien);
            nhanVienDTO.setHoTen(hoTen);
            nhanVienDTO.setNgaySinh(ngaySinh);
            nhanVienDTO.setSoDienThoai(soDienThoai);
            nhanVienDTO.setEmail(email);
            nhanVienDTO.setThanhPho(thanhPho);
            nhanVienDTO.setQuanHuyen(quanHuyen);
            nhanVienDTO.setPhuongXa(phuongXa);
            nhanVienDTO.setTaiKhoanDTO(taiKhoanDTO);
            nhanVienDTO.setTrangThai(trangThai);
            nhanVienDTO.setGioiTinh(Boolean.parseBoolean(gioiTinh));

            // Xử lý ảnh
            if (img != null && !img.isEmpty()) {
                String uploadDir = "src/main/resources/static"; // Đường dẫn thư mục ảnh
                String fileName = img.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.copy(img.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                nhanVienDTO.setImg("/img/" + fileName); // Cập nhật đường dẫn hình ảnh
            } else {
                // Không có ảnh mới, giữ lại ảnh cũ
                nhanVienDTO.setImg(existingNhanVien.get().getImg());
            }

            nhanVienSerVice.save(nhanVienDTO);
            return ResponseEntity.ok(Map.of("success", "Sửa nhân viên thành công!"));

        } catch (Exception e) {
            System.out.println("Lỗi:" + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Đã xảy ra lỗi: " + e.getMessage()));
        }
    }


    //validate
    private boolean isOver18(Date ngaySinh) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);
        return ngaySinh.before(calendar.getTime());
    }

    private boolean isOver200(Date ngaySinh){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,-200);
        return ngaySinh.after(calendar.getTime());
    }

    //validate add
    private boolean isEmailExists(String email) {
        return nhanVienSerVice.isEmailExists(email); // Phương thức kiểm tra email tồn tại
    }
    // Khai báo biểu thức chính quy để kiểm tra định dạng email
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public boolean isValidEmail(String email) {
        // Kiểm tra định dạng email
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public boolean isMeaningfulEmail(String email) {
        // Kiểm tra xem email có đủ ý nghĩa hay không
        String[] parts = email.split("@");
        if (parts.length != 2 || parts[0].length() < 3) {
            return false; // Phần local phải dài hơn 2 ký tự
        }
        // Kiểm tra tên miền không hợp lệ
        String domain = parts[1];
        if (!domain.contains(".")) {
            return false; // Tên miền không hợp lệ
        }
        // Có thể thêm các kiểm tra khác như kiểm tra từ ngữ không phù hợp
        return true;
    }

    private boolean isPhoneNumberExists(String soDienThoai) {
        return nhanVienSerVice.isPhoneNumberExists(soDienThoai); // Phương thức kiểm tra số điện thoại tồn tại
    }
    // Phương thức kiểm tra số điện thoại
    public boolean isValidPhoneNumber(String phoneNumber) {
        String PHONE_REGEX = "^\\d+$";
        return Pattern.matches(PHONE_REGEX, phoneNumber) && phoneNumber.length() >= 10; // Kiểm tra số điện thoại phải có ít nhất 10 ký tự
    }

    //validate update
    private boolean isEmailExistsUpDate(String email, Integer idNhanVien) {
        // Lấy danh sách tất cả nhân viên từ cơ sở dữ liệu
        List<NhanVienDTO> existingEmployees = nhanVienSerVice.findAll(); // Giả sử bạn có phương thức này

        // Kiểm tra nếu email đã tồn tại, ngoại trừ email của nhân viên hiện tại
        return existingEmployees.stream()
                .anyMatch(employee -> employee.getEmail().equals(email) && !employee.getIdNhanVien().equals(idNhanVien));
    }
//
//    private boolean isPhoneExistsUpDate(String soDienThoai, Integer idNhanVien) {
//        // Lấy danh sách tất cả nhân viên từ cơ sở dữ liệu
//        List<NhanVienDTO> existingEmployees = nhanVienSerVice.findAll(); // Giả sử bạn có phương thức này
//
//        // Kiểm tra nếu email đã tồn tại, ngoại trừ email của nhân viên hiện tại
//        return existingEmployees.stream()
//                .anyMatch(employee -> employee.getSoDienThoai().equals(soDienThoai) && !employee.getIdNhanVien().equals(idNhanVien));
//    }
//



    //hàm mã tự gen
    private String generateMaNhanVien() {
        String prefix = "NV";
        NhanVien lastNhanVien = nhanVienRepo.findTopByOrderByIdNhanVienDesc();
        int nextId = lastNhanVien != null ? (int) (lastNhanVien.getIdNhanVien() + 1) : 1;
        return prefix + String.format("%03d", nextId);
    }

    private String genTenDangNhap(String ho, String ten) {
        // Xử lý để loại bỏ dấu
        String hoKhongDau = removeAccents(ho);
        String tenKhongDau = removeAccents(ten);
        NhanVien lastNhanVien = nhanVienRepo.findTopByOrderByIdNhanVienDesc();
        int nextId = lastNhanVien != null ? (int) (lastNhanVien.getIdNhanVien() + 1) : 1;
        // Tạo tên đăng nhập theo định dạng: "tên cuối cùng (không dấu) + họ (không dấu) + ID nhân viên"
        String tenDangNhap = tenKhongDau + hoKhongDau +  String.format("%03d", nextId);

        return tenDangNhap.toLowerCase(); // Chuyển thành chữ thường
    }


    // Hàm removeAccents để loại bỏ dấu
    private String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{ASCII}]", "");
    }
    //gen mk
    private String generatePasswordFromNameAndBirthYear(String ten, String namSinh) {
        // Loại bỏ dấu (nếu cần)
        String tenKhongDau = removeAccents(ten);

        // Tạo mật khẩu theo định dạng: "tên (không dấu) + năm sinh"
        String matKhau = tenKhongDau + namSinh;

        return matKhau.toLowerCase(); // Chuyển thành chữ thường
    }



}
