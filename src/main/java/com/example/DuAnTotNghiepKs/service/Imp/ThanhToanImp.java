package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.ThanhToan;
import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;
import com.example.DuAnTotNghiepKs.repository.ThanhToanRepo;
import com.example.DuAnTotNghiepKs.service.ThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ThanhToanImp implements ThanhToanService {

    @Autowired
    private ThanhToanRepo thanhToanRepository;

    @Autowired
    private DatPhongRepo datPhongRepository;

    @Override
    public boolean xacNhanThanhToan(Integer idDatPhong, Double paymentAmount) {
        // Tìm đơn đặt phòng
        DatPhong datPhong = datPhongRepository.findById(idDatPhong).orElse(null);
        if (datPhong == null) {
            return false;
        }

        // Tạo đối tượng ThanhToan
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setDatPhong(datPhong);
        thanhToan.setNgayThanhToan(new Date()); // Ngày thanh toán hiện tại
        thanhToan.setPhuongThuc(paymentAmount >= (datPhong.getTienCoc())); // Giả sử phương thức thanh toán thành công nếu thanh toán >= tiền cọc
        thanhToan.setTinhTrang(true); // Đánh dấu là đã thanh toán

        // Lưu thanh toán vào cơ sở dữ liệu
        thanhToanRepository.save(thanhToan);

        // Cập nhật trạng thái đơn đặt phòng
        datPhong.setTinhTrang(Boolean.valueOf("Đã cọc")); // Cập nhật trạng thái
        datPhongRepository.save(datPhong);

        return thanhToan.isPhuongThuc();
    }

}
