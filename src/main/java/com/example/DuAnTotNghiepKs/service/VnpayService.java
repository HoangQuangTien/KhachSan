package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.SecurityConfig.Config;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnpayService {

    private final DatPhongRepo datPhongRepo;

    // Khai báo Map tĩnh để lưu trữ dữ liệu ánh xạ vnp_TxnRef -> danh sách ID phòng
    private static final Map<String, List<Integer>> transactionMap = new HashMap<>();

    // Phương thức lưu ánh xạ vnp_TxnRef với danh sách ID phòng
    public static void saveTransaction(String vnp_TxnRef, List<Integer> idList) {
        transactionMap.put(vnp_TxnRef, idList);
    }

    // Phương thức lấy danh sách ID phòng dựa trên mã giao dịch vnp_TxnRef
    public static List<Integer> getTransaction(String vnp_TxnRef) {
        return transactionMap.get(vnp_TxnRef);
    }

    public VnpayService(DatPhongRepo datPhongRepo) {
        this.datPhongRepo = datPhongRepo;
    }



    public DatPhong ThanhToanThanhCong(Integer id){
        Optional<DatPhong> datPhongOpt = datPhongRepo.findById(id);
        // Throw an exception if the booking is not found
        if (!datPhongOpt.isPresent()) {
            throw new IllegalArgumentException("Id dat phong không tồn tại");
        }

        // Proceed with the booking update if it exists
        DatPhong booking = datPhongOpt.get();
        booking.setTinhTrang("Chưa xác nhận");
        datPhongRepo.save(booking);

        return booking;
    }

    public DatPhong ThanhToanThatBai(Integer id){
        Optional<DatPhong> datPhongOpt = datPhongRepo.findById(id);
        // Throw an exception if the booking is not found
        if (!datPhongOpt.isPresent()) {
            throw new IllegalArgumentException("Id dat phong không tồn tại");
        }

        // Proceed with the booking update if it exists
        DatPhong booking = datPhongOpt.get();
        booking.setTinhTrang("Đã Hủy");
        datPhongRepo.save(booking);

        return booking;
    }

}

