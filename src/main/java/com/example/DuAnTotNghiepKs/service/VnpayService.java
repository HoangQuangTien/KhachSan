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

    public VnpayService(DatPhongRepo datPhongRepo) {
        this.datPhongRepo = datPhongRepo;
    }


    public String createPayment(DatPhong datPhong, HttpServletRequest req, Long tienCoc, String bankCode, String language) {
        // Tạo URL thanh toán VNPAY với thông tin đặt phòng và trả lại URL
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = Config.getRandomNumber(8);  // Mã giao dịch duy nhất
        String vnp_IpAddr = Config.getIpAddress(req);  // Lấy địa chỉ IP từ yêu cầu
        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf((int) (tienCoc * 100)));  // Chuyển tiền cọc sang đơn vị VND (đơn vị là cent)
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "billpayment");  // Thêm mã đặt phòng vào thông tin đơn hàng
        vnp_Params.put("vnp_OrderType", "booking");

        String locate = (language != null && !language.isEmpty()) ? language : "vn";
        vnp_Params.put("vnp_Locale", locate);
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);  // Thời gian hết hạn giao dịch là 15 phút
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        // Tạo chuỗi query và hash data
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        // Tạo chuỗi ký tự bảo mật
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        String paymentUrl = Config.vnp_PayUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;
        System.out.println("paymentUrl"+paymentUrl);
        return paymentUrl;  // Trả về URL thanh toán
    }

    public DatPhong ThanhToanThanhCong(Integer id){
        Optional<DatPhong> datPhongOpt = datPhongRepo.findById(id);
        // Throw an exception if the booking is not found
        if (!datPhongOpt.isPresent()) {
            throw new IllegalArgumentException("Id dat phong không tồn tại");
        }

        // Proceed with the booking update if it exists
        DatPhong booking = datPhongOpt.get();
        booking.setTinhTrang("Chưa Checkin");
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

