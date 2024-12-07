package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.CustomerDTO;
import com.example.DuAnTotNghiepKs.entity.*;
import com.example.DuAnTotNghiepKs.repository.*;
import com.example.DuAnTotNghiepKs.service.*;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class ChiTietDatPhongController {

    @Autowired
    private ChiTietDatPhongRepo chiTietDatPhongRepo;
    @Autowired
    private DatPhongRepo datPhongRepo;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private PhongRepo phongRepo;

    @Autowired
    private PhongService phongService;

    @Autowired
    private LoaiPhongService loaiPhongService;

    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private ChiTietDatPhongService chiTietDatPhongService;
    @Autowired
    private LichSuDatPhongRepo lichSuDatPhongRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private NhanVienRepo nhanVienRepo;

    @GetMapping("/danhsachdatphong")
    public String index(
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "size", defaultValue = "5") int pageSize,
            @RequestParam(name = "startDate", defaultValue = "") String startDateStr,
            @RequestParam(name = "endDate", defaultValue = "") String endDateStr,
            @RequestParam(defaultValue = "", name = "keyword") String keyword,
            @RequestParam(name = "tinhTrang", defaultValue = "") String tinhTrangStr,
            Model model
    ) {
        // Các biến xử lý ngày và thông báo lỗi
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        String errorMessage = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        // Xử lý ngày tháng
        try {
            if (!startDateStr.isEmpty()) {
                startDate = LocalDateTime.parse(startDateStr + "T00:00", formatter);
            }
            if (!endDateStr.isEmpty()) {
                endDate = LocalDateTime.parse(endDateStr + "T23:59", formatter);
            }
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                errorMessage = "Ngày bắt đầu không được lớn hơn ngày kết thúc.";
                startDate = null;
                endDate = null;
            }
        } catch (DateTimeParseException e) {
            errorMessage = "Ngày tháng không hợp lệ. Vui lòng nhập đúng định dạng yyyy-MM-dd.";
        }

        Page<DatPhong> datPhongPage;
        List<ChiTietDatPhong> chiTietDatPhongs = new ArrayList<>();

        if (errorMessage == null) {
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

            // Lọc theo từ khóa nếu có
            if (!keyword.isEmpty()) {
                datPhongPage = datPhongRepo.searchBy(keyword, pageable);
            } else if (startDate != null && endDate != null) {
                datPhongPage = datPhongRepo.findByNgayNhanBetween(startDate, endDate, pageable);
            } else {
                datPhongPage = datPhongRepo.findAll(pageable);
            }

            // Lọc theo tình trạng nếu có
            if (!tinhTrangStr.isEmpty()) {
                List<DatPhong> filteredDatPhongs = datPhongPage.stream()
                        .filter(datPhong -> datPhong.getTinhTrang().equals(tinhTrangStr))
                        .collect(Collectors.toList());
                datPhongPage = new PageImpl<>(filteredDatPhongs, pageable, filteredDatPhongs.size());
            }

            // Lấy chi tiết đặt phòng từ danh sách đặt phòng
            chiTietDatPhongs = chiTietDatPhongRepo.findByDatPhongIn(datPhongPage.getContent());

            List<KhachHang> khachHangs = khachHangRepository.findAll();
            List<Phong> phongs = phongRepo.findAll();

            // Thêm các thuộc tính vào model
            model.addAttribute("data", chiTietDatPhongs);
            model.addAttribute("datPhongs", datPhongPage.getContent());
            model.addAttribute("khachHangs", khachHangs);
            model.addAttribute("phongs", phongs);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", datPhongPage.getTotalPages());
            model.addAttribute("totalItems", datPhongPage.getTotalElements());
        } else {
            model.addAttribute("data", List.of());
            model.addAttribute("datPhongs", List.of());
            model.addAttribute("khachHangs", List.of());
            model.addAttribute("phongs", List.of());
        }

        // Thêm các thuộc tính cần thiết vào model
        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);
        model.addAttribute("tinhTrang", tinhTrangStr);
        model.addAttribute("keyword", keyword);
        model.addAttribute("errorMessage", errorMessage);

        return "list/QuanLyDatPhong/danhsachdatphong"; // Trả về view chính
    }




    @GetMapping("/qrcode/{id}")
    public ResponseEntity<byte[]> getQRCode(@PathVariable("id") Integer id) {
        ChiTietDatPhong chiTietDatPhong = chiTietDatPhongService.findById(id);
        String qrData = String.format(
                "Mã CTDP: %s\nTên Nhân Viên: %s\nTên Khách Hàng: %s\nTên Phòng: %s\nNgày Nhận Phòng: %s\nNgày Trả Phòng: %s\nTổng Tiền: %s",
                chiTietDatPhong.getMaChiTietDatPhong(),
//                chiTietDatPhong.getNhanVien().getHoTen(),
                chiTietDatPhong.getKhachHang().getHoVaTen(),
                chiTietDatPhong.getDatPhong().getPhong().getTenPhong(),
                chiTietDatPhong.getDatPhong().getNgayNhan(),
                chiTietDatPhong.getDatPhong().getNgayTra(),
                chiTietDatPhong.getTongTien()
        );

        try {
            byte[] qrCodeImage = qrCodeService.generateQRCode(qrData, 300, 300);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/danhsachdatphong/sua-khach-hang/{id}")
    public String editKhachHang(@PathVariable("id") Integer id, Model model) {
        KhachHang khachHang = khachHangRepository.findById(id).orElse(null);
        if (khachHang == null) {
            return "redirect:/danhsachdatphong";
        }
        model.addAttribute("khachHang", khachHang);
        return "list/QuanlyDatPhong/edit_khach_hang";
    }
    @PostMapping("/update-customer")
    @ResponseBody
    public ResponseEntity<?> updateCustomer(
            @ModelAttribute("customerDTO") @Valid CustomerDTO customerDTO,
            BindingResult result,
            @RequestParam("customerId") Integer customerId) {

        if (result.hasErrors()) {
            // Return error response
            String errorMessage = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", errorMessage));
        }

        try {
            KhachHang khachHang = khachHangRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

            khachHang.setHoVaTen(customerDTO.getTenKhachHang());
            khachHang.setSoDienThoai(customerDTO.getSoDienThoai());
            khachHang.setEmail(customerDTO.getEmail());

            khachHangRepository.save(khachHang);

            return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Đã xảy ra lỗi: " + e.getMessage()));
        }

    }

    @GetMapping("/get-customer/{id}")
    @ResponseBody
    public KhachHang getCustomer(@PathVariable Integer id) {
        return khachHangRepository.findById(id).orElse(null); // Trả về null nếu không tìm thấy khách hàng
    }

    @PostMapping("/gui-email")
    public String sendEmail(@RequestParam("chiTietDatPhongId") Integer chiTietDatPhongId, RedirectAttributes redirectAttributes) {
        System.out.println("ID chi tiết đặt phòng: " + chiTietDatPhongId); // Debug ID
        try {
            // Retrieve ChiTietDatPhong from the database
            ChiTietDatPhong ctdp = chiTietDatPhongService.findById(chiTietDatPhongId);
            System.out.println("ChiTietDatPhong retrieved: " + ctdp); // Debug object

            if (ctdp == null) {
                System.out.println("Chi tiết đặt phòng không được tìm thấy.");
                redirectAttributes.addFlashAttribute("message", "Chi tiết đặt phòng không được tìm thấy.");
                return "redirect:/danhsachdatphong";
            }

            // Get the customer's email from ChiTietDatPhong
            String email = ctdp.getKhachHang().getEmail();
            if (email == null || email.isEmpty()) {
                System.out.println("Không tìm thấy địa chỉ email của khách hàng.");
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy địa chỉ email của khách hàng.");
                return "redirect:/danhsachdatphong";
            }

            // Send email with booking details
            emailService.sendEmail(email, "Chi Tiết Đặt Phòng", "Nội dung email", ctdp);
            System.out.println("Gửi email thành công."); // Debug success message

            // Add success message to RedirectAttributes
            redirectAttributes.addFlashAttribute("message", "Gửi email thành công.");
        } catch (MessagingException | IOException e) {
            System.out.println("Gửi email thất bại: " + e.getMessage()); // Debug error message
            // Add error message to RedirectAttributes
            redirectAttributes.addFlashAttribute("message", "Gửi email thất bại: " + e.getMessage());
        }
        // Redirect to /danhsachdatphong and display the message
        return "redirect:/danhsachdatphong";
    }



    @GetMapping("/xuat-excel")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        List<ChiTietDatPhong> chiTietDatPhongs = chiTietDatPhongRepo.findAll(); // Retrieve all data

        // Create workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Chi Tiết Đặt Phòng");

        // Create column headers
        String[] columnNames = {"Mã CTDP", "Tên Nhân Viên", "Tên Khách Hàng", "Tên Phòng", "Ngày Nhận Phòng", "Ngày Trả Phòng", "Tổng Tiền"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnNames[i]);
        }

        int rowNum = 1;
        for (ChiTietDatPhong ctdp : chiTietDatPhongs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(ctdp.getMaChiTietDatPhong());
//            row.createCell(1).setCellValue(ctdp.getNhanVien().getHoTen());
            row.createCell(2).setCellValue(ctdp.getKhachHang().getHoVaTen());
            row.createCell(3).setCellValue(ctdp.getDatPhong().getPhong().getTenPhong());
            row.createCell(4).setCellValue(ctdp.getDatPhong().getNgayNhan().toString());
            row.createCell(5).setCellValue(ctdp.getDatPhong().getNgayTra().toString());
            row.createCell(6).setCellValue(ctdp.getTongTien().toString());
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=chitietdatphong.xlsx");

        return ResponseEntity.ok().headers(headers).body(out.toByteArray());
    }
    private Date setDefaultTimeTo7AM(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    @GetMapping("/edit-room")
    public String showEditRoomForm(@RequestParam("roomId") Integer id, Model model) {
        Optional<Phong> optionalPhong = phongService.getPhongById(id);

        if (optionalPhong.isPresent()) {
            Phong phong = optionalPhong.get();
            model.addAttribute("phong", phong);
            Optional<DatPhong> datPhongOpt = datPhongRepo.findTopByPhongOrderByNgayNhanDesc(phong);
            List<Phong> availableRooms = new ArrayList<>();

            if (datPhongOpt.isPresent()) {
                DatPhong datPhong = datPhongOpt.get();
                LocalDateTime startDate = datPhong.getNgayNhan();
                LocalDateTime endDate = datPhong.getNgayTra();
                availableRooms = phongService.getAvailableRoomsForEdit(phong.getIdPhong(), startDate, endDate);
            } else {
                availableRooms = phongService.getAvailableRoomsForEdit(phong.getIdPhong(), null, null);
            }
            if (!availableRooms.contains(phong)) {
                availableRooms.add(phong);
            }
            model.addAttribute("allRooms", availableRooms);
            List<LoaiPhong> loaiPhongs = loaiPhongService.getAllLoaiPhongs();
            model.addAttribute("loaiPhongs", loaiPhongs);
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy phòng với ID: " + id);
            return "error";
        }

        return "list/QuanLyDatPhong/edit-phong";
    }



    @PostMapping("/update-room")
    public ResponseEntity<?> updateRoom(
            @RequestParam("roomId") Integer roomId,
            @RequestParam("tenPhong") Integer tenPhongId,
            @RequestParam("lyDoThayDoi") String lyDoThayDoi) {

        try {
            Phong phongHienTai = phongService.getPhongById(roomId)
                    .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

            Optional<DatPhong> datPhongOpt = datPhongRepo.findTopByPhongOrderByNgayNhanDesc(phongHienTai);

            if (datPhongOpt.isPresent()) {
                DatPhong datPhong = datPhongOpt.get();
                if ("Đã Checkin".equals(datPhong.getTinhTrang())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success", false, "message", "Không thể đổi phòng vì phòng đã được check-in."));
                }
                if ("Đã Hủy".equals(datPhong.getTinhTrang())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success", false, "message", "Không thể đổi phòng vì phòng đã bị hủy."));
                }
            }

            Phong phongMoi = phongService.getPhongById(tenPhongId)
                    .orElseThrow(() -> new RuntimeException("Phòng không tồn tại với ID: " + tenPhongId));

            if (phongMoi.getGia() < phongHienTai.getGia()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Không thể đổi sang phòng có giá thấp hơn phòng hiện tại."));
            }

            LocalDateTime startDate = datPhongOpt.get().getNgayNhan();
            LocalDateTime endDate = datPhongOpt.get().getNgayTra();

            // Kiểm tra xem phòng mới có bị đặt trùng thời gian không
            boolean isPhongMoiBooked = !datPhongRepo.findByPhongAndThoiGian(phongMoi.getIdPhong(), startDate, endDate).isEmpty();

            if (isPhongMoiBooked) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Phòng mới đang được đặt. Không thể chuyển sang phòng này."));
            }

            if (datPhongOpt.isPresent()) {
                DatPhong datPhong = datPhongOpt.get();
                datPhong.setPhong(phongMoi); // Cập nhật phòng mới

                // Tính toán số ngày ở
                long numberOfNights = java.time.Duration.between(datPhong.getNgayNhan(), datPhong.getNgayTra()).toDays();

                // Tính giá phòng cho số ngày ở
                double roomPricePerNight = phongMoi.getLoaiPhong().getGia(); // Giá phòng mỗi đêm
                double totalRoomPrice = roomPricePerNight * numberOfNights; // Tổng tiền phòng cho số ngày ở

                // Cập nhật tổng tiền phòng
                datPhong.setTongTien((float) totalRoomPrice);

                // Tính tiền cọc (80% tổng tiền)
                float tienCoc = (float) (totalRoomPrice * 0.8); // Tiền cọc bằng 80% tổng tiền
                datPhong.setTienCoc(tienCoc);

                // Tính tiền còn lại
                float tienConLai = (float) (totalRoomPrice - tienCoc);

                // Cập nhật tiền còn lại
                datPhong.setTienConLai(tienConLai);

                // Lưu lại thông tin đã cập nhật
                datPhongRepo.save(datPhong);
            }

            LichSuDatPhong lichSuDatPhong = new LichSuDatPhong();
            lichSuDatPhong.setDatPhong(datPhongOpt.get());
            lichSuDatPhong.setChiTietThayDoi("Đổi từ " + phongHienTai.getTenPhong() + " sang phòng " + phongMoi.getTenPhong() + ". Lý do: " + lyDoThayDoi);
            lichSuDatPhong.setThoiGianThayDoi(new Date());
            lichSuDatPhongRepo.save(lichSuDatPhong);

            return ResponseEntity.ok(Map.of("success", true, "message", "Đã chuyển phòng thành công"));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Đã xảy ra lỗi: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Đã xảy ra lỗi không mong muốn: " + e.getMessage()));
        }
    }


}


