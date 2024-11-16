package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.ChiTietDatPhong;
import com.example.DuAnTotNghiepKs.entity.KhachHang;
import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import com.example.DuAnTotNghiepKs.repository.KhuyenMaiRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private KhuyenMaiRepo khuyenMaiRepo;

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalDateTime setDefaultTimeTo7AM(LocalDate date) {
        return date.atTime(LocalTime.of(7, 0));
    }

    public void sendEmail(String to, String subject, String text, ChiTietDatPhong ctdp) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set email properties
        helper.setTo(to);
        helper.setSubject(subject);

        // Generate QR code as a byte array
        byte[] qrCodeBytes = qrCodeService.generateQRCode(
                String.format(
                        "Mã Chi Tiết Đặt Phòng: %s\nKhách Hàng: %s\nNgày Lập: %s\nTổng Tiền: %s",
                        ctdp.getMaChiTietDatPhong(),
                        ctdp.getKhachHang().getHoVaTen(),
                        ctdp.getNgayLap(),
                        ctdp.getTongTien().toString()
                ), 300, 300
        );

        // Convert the byte array to a DataSource for the attachment
        ByteArrayResource qrCodeResource = new ByteArrayResource(qrCodeBytes);
        helper.addAttachment("qrCode.png", qrCodeResource, "image/png");

        // Determine email content based on tinhTrang
        String htmlContent;
        if (ctdp.getDatPhong().getTinhTrang() != null && ctdp.getDatPhong().getTinhTrang().equals("Đã Checkin")) {
            // Lấy giá trị ngày nhận và ngày trả kiểu LocalDateTime trực tiếp
            LocalDateTime ngayNhan = ctdp.getDatPhong().getNgayNhan();
            LocalDateTime ngayTra = ctdp.getDatPhong().getNgayTra();

            // Định dạng ngày giờ (không cần chỉnh thời gian vì đã có sẵn trong LocalDateTime)
            String formattedNgayNhan = formatDateTime(ngayNhan);
            String formattedNgayTra = formatDateTime(ngayTra);

            // Sử dụng các giá trị đã định dạng trong nội dung email
            htmlContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: 'Arial', sans-serif; background-color: #fff3cd; margin: 0; padding: 20px; }" +
                    ".container { max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }" +
                    ".header { background-color: #ffc107; color: white; padding: 15px; text-align: center; border-radius: 10px 10px 0 0; }" +
                    ".header h1 { margin: 0; font-size: 24px; }" +
                    "p { margin: 20px 0; color: #856404; }" +
                    ".details { margin: 20px 0; padding: 10px; background-color: #fff3cd; border-radius: 5px; color: #856404; }" +
                    ".footer { text-align: center; color: #856404; margin-top: 20px; font-size: 14px; padding-top: 10px; border-top: 1px solid #ffeeba; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<h1>Kính gửi " + ctdp.getKhachHang().getHoVaTen() + ",</h1>" +
                    "</div>" +
                    "<p>Chúng tôi xác nhận rằng bạn đã trả phòng vào <strong>" + formattedNgayTra + "</strong>.</p>" +
                    "<p>Nếu bạn cần thêm bất kỳ thông tin gì, vui lòng liên hệ với chúng tôi.</p>" +
                    "<div class='footer'>Trân trọng, <br>DRAGONBALL HOTEL</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

        } else {

            htmlContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: 'Arial', sans-serif; background-color: #fff3cd; margin: 0; padding: 20px; }" +
                    ".container { max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }" +
                    ".header { background-color: #ffc107; color: white; padding: 15px; text-align: center; border-radius: 10px 10px 0 0; }" +
                    ".header h1 { margin: 0; font-size: 24px; }" +
                    "p { margin: 20px 0; color: #856404; }" +
                    ".details { margin: 20px 0; padding: 10px; background-color: #fff3cd; border-radius: 5px; color: #856404; }" +
                    ".footer { text-align: center; color: #856404; margin-top: 20px; font-size: 14px; padding-top: 10px; border-top: 1px solid #ffeeba; }" +
                    "a.button { display: inline-block; padding: 10px 20px; margin: 20px auto; font-size: 16px; color: white; background-color: #fd7e14; border-radius: 5px; text-decoration: none; }" +
                    "a.button:hover { background-color: #e8590c; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<h1>Kính gửi " + ctdp.getKhachHang().getHoVaTen() + ",</h1>" +
                    "</div>" +
                    "<p>Cảm ơn bạn đã chọn ở lại với chúng tôi tại DRAGONBALL HOTEL! Chúng tôi rất vui khi có bạn là khách hàng của chúng tôi từ <strong>" + ctdp.getNgayLap() + "</strong>.</p>" +
                    "<p>Xin lưu ý rằng loại phòng của bạn là <strong>" + ctdp.getDatPhong().getLoaiPhong().getTenLoaiPhong() + "</strong> và khoản thanh toán đã được nhận với số tiền là <strong>" + ctdp.getDatPhong().getTienCoc() + "</strong>.</p>" +
                    "<p>Nếu bạn yêu cầu bất kỳ tiện nghi hoặc dịch vụ bổ sung nào, chúng tôi sẽ sẵn lòng cung cấp những tiện nghi hoặc dịch vụ đó cho bạn trong thời gian lưu trú.</p>" +
                    "<p>Cảm ơn bạn một lần nữa vì đã chọn chúng tôi và chúng tôi mong được có bạn là khách của chúng tôi.</p>" +
                    "<div class='footer'>Trân trọng, <br>DRAGONBALL HOTEL</div>" +
                    "<div class='qr-code'>" +
//                    "<img src='cid:qrCode' alt='QR Code' style='display:block;margin:10px auto; max-width: 150px;'/>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
        }

        helper.setText(htmlContent, true);

        // Send the email
        emailSender.send(message);
    }




    public void guimail(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setFrom("conbotthoiok@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true);
        emailSender.send(mimeMessage);

    }

    public KhuyenMai layvoucher(Integer id){
        return  khuyenMaiRepo.findById(id).orElse(null);
    }



    @SneakyThrows
    public void sendEmail(String to, String subject, String body) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true để chỉ định nội dung là HTML
        emailSender.send(message);
    }



    public void sendEmail1(String to, String subject, String text, ChiTietDatPhong ctdp)
            throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set email properties
        helper.setTo(to);
        helper.setSubject(subject);

        // Generate QR code as a byte array
        byte[] qrCodeBytes = qrCodeService.generateQRCode(String.format(
                "Mã Chi Tiết Đặt Phòng: %s\nKhách Hàng: %s\nNgày Lập: %s\nTổng Tiền: %s", ctdp.getMaChiTietDatPhong(),
                ctdp.getKhachHang().getHoVaTen(), ctdp.getNgayLap(), ctdp.getTongTien().toString()), 300, 300);

        // Convert the byte array to a DataSource for the attachment
        ByteArrayResource qrCodeResource = new ByteArrayResource(qrCodeBytes);
        helper.addAttachment("qrCode.png", qrCodeResource, "image/png");

        // Determine email content based on tinhTrang
        String htmlContent;
        if (ctdp.getDatPhong().getTinhTrang() != null && ctdp.getDatPhong().getTinhTrang().equals("Đã Checkin")) {
            // Lấy giá trị ngày nhận và ngày trả kiểu LocalDateTime trực tiếp
            LocalDateTime ngayNhan = ctdp.getDatPhong().getNgayNhan();
            LocalDateTime ngayTra = ctdp.getDatPhong().getNgayTra();

            // Định dạng ngày giờ (không cần chỉnh thời gian vì đã có sẵn trong
            // LocalDateTime)
            String formattedNgayNhan = formatDateTime(ngayNhan);
            String formattedNgayTra = formatDateTime(ngayTra);

            // Sử dụng các giá trị đã định dạng trong nội dung email
            htmlContent = "<html>" + "<head>" + "<style>"
                    + "body { font-family: 'Arial', sans-serif; background-color: #fff3cd; margin: 0; padding: 20px; }"
                    + ".container { max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }"
                    + ".header { background-color: #ffc107; color: white; padding: 15px; text-align: center; border-radius: 10px 10px 0 0; }"
                    + ".header h1 { margin: 0; font-size: 24px; }" + "p { margin: 20px 0; color: #856404; }"
                    + ".details { margin: 20px 0; padding: 10px; background-color: #fff3cd; border-radius: 5px; color: #856404; }"
                    + ".footer { text-align: center; color: #856404; margin-top: 20px; font-size: 14px; padding-top: 10px; border-top: 1px solid #ffeeba; }"
                    + "</style>" + "</head>" + "<body>" + "<div class='container'>" + "<div class='header'>"
                    + "<h1>Kính gửi " + ctdp.getKhachHang().getHoVaTen() + ",</h1>" + "</div>"
                    + "<p>Chúng tôi xác nhận rằng bạn đã trả phòng vào <strong>" + formattedNgayTra + "</strong>.</p>"
                    + "<p>Nếu bạn cần thêm bất kỳ thông tin gì, vui lòng liên hệ với chúng tôi.</p>"
                    + "<div class='footer'>Trân trọng, <br>DRAGONBALL HOTEL</div>" + "</div>" + "</body>" + "</html>";

        } else {

            htmlContent = "<html>" + "<head>" + "<style>"
                    + "body { font-family: 'Arial', sans-serif; background-color: #fff3cd; margin: 0; padding: 20px; }"
                    + ".container { max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }"
                    + ".header { background-color: #ffc107; color: white; padding: 15px; text-align: center; border-radius: 10px 10px 0 0; }"
                    + ".header h1 { margin: 0; font-size: 24px; }" + "p { margin: 20px 0; color: #856404; }"
                    + ".details { margin: 20px 0; padding: 10px; background-color: #fff3cd; border-radius: 5px; color: #856404; }"
                    + ".footer { text-align: center; color: #856404; margin-top: 20px; font-size: 14px; padding-top: 10px; border-top: 1px solid #ffeeba; }"
                    + "a.button { display: inline-block; padding: 10px 20px; margin: 20px auto; font-size: 16px; color: white; background-color: #fd7e14; border-radius: 5px; text-decoration: none; }"
                    + "a.button:hover { background-color: #e8590c; }" + "</style>" + "</head>" + "<body>"
                    + "<div class='container'>" + "<div class='header'>" + "<h1>Kính gửi "
                    + ctdp.getKhachHang().getHoVaTen() + ",</h1>" + "</div>"
                    + "<p>Cảm ơn bạn đã chọn ở lại với chúng tôi tại DRAGONBALL HOTEL! Chúng tôi rất vui khi có bạn là khách hàng của chúng tôi từ <strong>"
                    + ctdp.getNgayLap() + "</strong>.</p>" + "<p>Xin lưu ý rằng loại phòng của bạn là <strong>"
                    + ctdp.getDatPhong().getLoaiPhong().getTenLoaiPhong()
                    + "</strong> và khoản thanh toán đã được nhận với số tiền là <strong>"
                    + ctdp.getDatPhong().getTienCoc() + "</strong>.</p>"
                    + "<p>Nếu bạn yêu cầu bất kỳ tiện nghi hoặc dịch vụ bổ sung nào, chúng tôi sẽ sẵn lòng cung cấp những tiện nghi hoặc dịch vụ đó cho bạn trong thời gian lưu trú.</p>"
                    + "<p>Cảm ơn bạn một lần nữa vì đã chọn chúng tôi và chúng tôi mong được có bạn là khách của chúng tôi.</p>"
                    + "<div class='footer'>Trân trọng, <br>DRAGONBALL HOTEL</div>" + "<div class='qr-code'>" +
//                    "<img src='cid:qrCode' alt='QR Code' style='display:block;margin:10px auto; max-width: 150px;'/>" +
                    "</div>" + "</div>" + "</body>" + "</html>";
        }

        helper.setText(htmlContent, true);

        // Send the email
        emailSender.send(message);
    }

    // Gửi mail đăng kí thành công
    public void sendEmailRegister(String to, String subject, KhachHang khachHang)
            throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);

        String htmlContent;
        htmlContent = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<style>\n"
                + "body { font-family: 'Arial', sans-serif; background-color: #fff3cd; margin: 0; padding: 20px; }\n"
                + ".container { max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }\n"
                + ".header { background-color: #ffc107; color: white; padding: 15px; text-align: center; border-radius: 10px 10px 0 0; }\n"
                + ".header h1 { margin: 0; font-size: 24px; }\n" + "p { margin: 20px 0; color: #856404; }\n"
                + ".details { margin: 20px 0; padding: 10px; background-color: #fff3cd; border-radius: 5px; color: #856404; }\n"
                + ".footer { text-align: center; color: #856404; margin-top: 20px; font-size: 14px; padding-top: 10px; border-top: 1px solid #ffeeba; }\n"
                + "</style>\n" + "</head>\n" + "<body>\n" + "<div class='container'>\n" + "<div class='header'>\n"
                + "<h1>Kính gửi tài khoản " + khachHang.getHoVaTen() + ",</h1>\n" + "</div>\n"
                 + "<p><strong>Tài khoản:</strong> " + khachHang.getTaiKhoan().getTenDangNhap() + "</p>" +
                "<p><strong>Mật khẩu:</strong> " + khachHang.getTaiKhoan().getMatKhau() + "</p>" +
                 "<p>Cảm ơn bạn đã đăng ký tài khoản</p>\n"
                + "<div class='footer'>Trân trọng, <br>DRAGONBALL HOTEL</div>\n" + "</div>\n" + "</body>\n"
                + "</html>\n" + "";

        helper.setText(htmlContent, true);

        // Send the email
        emailSender.send(message);
    }

}

