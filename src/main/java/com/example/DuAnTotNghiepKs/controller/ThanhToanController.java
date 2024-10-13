package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.DTO.ThanhToanDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.ThanhToan;
import com.example.DuAnTotNghiepKs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/thanhToan")
public class ThanhToanController {

    @Autowired
    private ThanhToanService thanhToanService;

    @Autowired
    private DatPhongService datPhongService;

    @Autowired
    private PhuPhiService phuPhiService;

    @Autowired
    private PhongService phongService;

    @Autowired
    private TaiKhoanService taiKhoanService;


    @GetMapping
    public String loadAll(
            @ModelAttribute ThanhToanDTO thanhToanDTO,
            @RequestParam(value = "page", defaultValue = "0") int currentPage, // Trang hiện tại
            @RequestParam(value = "size", defaultValue = "2") int pageSize, // Kích thước trang
            Model model) {

        // Lấy danh sách đặt phòng đã cọc
        Page<DatPhong> datPhongPage = datPhongService.getDatPhongsDaCoc(currentPage, pageSize);

        // Thêm dữ liệu vào model
        model.addAttribute("datPhongs", datPhongPage.getContent());
        model.addAttribute("phuPhis", phuPhiService.getAll());

        // Thêm thông tin phân trang
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("currentPage", pageSize);
        model.addAttribute("totalPagesDatPhong", datPhongPage.getTotalPages());

        //lấy id nhân viên
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg()); // Đảm bảo rằng bạn có trường img trong NhanVienDTO
        }


        return "list/QuanLyThanhToan/thanhToan"; // Trả về view


    }







    @PostMapping("/add")
    public String save(@ModelAttribute ThanhToanDTO thanhToanDTO, RedirectAttributes redirectAttributes) {
        try {
            // Lưu thông tin thanh toán
            thanhToanService.save(thanhToanDTO);

            // Lấy thời gian cho phép chuyển trạng thái
            long thoiGianChoPhep = thanhToanService.getThoiGianChoPhepChuyenTrangThai();

            // Tạo một Thread để thay đổi trạng thái phòng sau thời gian đã chỉ định
            new Thread(() -> {
                try {
                    // Đợi thời gian cho phép chuyển trạng thái
                    Thread.sleep(thoiGianChoPhep);

                    // Cập nhật trạng thái phòng
                    phongService.updateRoomStatusToAvailable(thanhToanDTO.getIdPhong());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Khôi phục trạng thái ngắt
                }
            }).start();

            // Nếu người dùng chọn in hóa đơn
            if (thanhToanDTO.isInHoaDon()) {
                byte[] pdfBytes = thanhToanService.createInvoicePDF(thanhToanDTO);

                // Đường dẫn tới thư mục lưu hóa đơn
                String directoryPath = "D:/invoices";
                File directory = new File(directoryPath);

                // Kiểm tra xem thư mục đã tồn tại chưa, nếu chưa thì tạo thư mục
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        System.out.println("Thư mục đã được tạo: " + directoryPath);
                    } else {
                        System.out.println("Không thể tạo thư mục: " + directoryPath);
                        // Có thể thêm logic xử lý lỗi ở đây, ví dụ: thông báo lỗi cho người dùng
                    }
                }

                // Đường dẫn đầy đủ đến file hóa đơn
                String filePath = directoryPath + "/invoice.pdf";

                // Lưu file PDF
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    fos.write(pdfBytes);
                    System.out.println("Hóa đơn đã được lưu tại: " + filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Xử lý lỗi nếu không thể ghi file
                }
            }


            // Thêm thông báo thành công vào redirect
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công!");
        } catch (Exception e) {
            // Thêm thông báo lỗi vào redirect nếu có lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra, vui lòng thử lại!");
        }

        // Redirect về trang thanh toán
        return "redirect:/thanhToan";
    }




}
