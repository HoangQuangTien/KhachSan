package com.example.DuAnTotNghiepKs.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResponseService {

    private final Map<String, String> responses;

    public ResponseService() {
        responses = new HashMap<>();
        responses.put("phòng giá rẻ", "Giá phòng rẻ nhất thuộc loại phòng Standar có giá 300.000 dành cho 1 nguời ở");
        responses.put("phòng rẻ", "Giá phòng rẻ nhất thuộc loại phòng Standar có giá 300.000 dành cho 1 nguời ở");
        responses.put(" rẻ", "Giá phòng rẻ nhất thuộc loại phòng Standardành cho 1 nguời ở");
        responses.put("phòng giá cao", "Giá phòng cao nhất thuộc loại phòng Deluxe có giá 1.400.000 dành cho 2 nguời ở");
        responses.put("phòng giá đắt", "Giá phòng cao nhất thuộc loại phòng Deluxe có giá 1.400.000 dành cho 2 nguời ở");
        responses.put("phòng đắt", "Giá phòng cao nhất thuộc loại phòng Deluxe có giá 1.400.000 dành cho 2 nguời ở");
        responses.put("phòng 1 người", "Chúng tôi có loại phòng Standar có giá 300.000 dành cho 1 người ở");
        responses.put("1 người", "Chúng tôi có loại phòng Standar có giá 300.000 dành cho 1 người ở");
        responses.put("phòng 2 người", "Chúng tôi có loại phòng Superior có giá 500.000 và Deluxe có giá 1.400.000 dành cho 2 người ở");
        responses.put("2 người", "Chúng tôi có loại phòng Superior có giá 500.000 và Deluxe có giá 1.400.000 dành cho 2 người ở");
        responses.put("phòng gia đình", "Chúng tôi có loại phòng Family suite có giá 700.000 dành cho 6 người ở");
        responses.put("gia đình", "Chúng tôi có loại phòng Family suite có giá 700.000 dành cho 6 người ở");
        responses.put(" đắt", "Giá phòng đắt nhất thuộc loại phòng Deluxe có giá 1.400.000 tùy thuộc vào thời gian đặt.");
        responses.put("giá phòng", "Giá phòng dao động từ 300.000 đến 1.400.000, tùy thuộc vào loại phòng và thời gian đặt.");
        responses.put("đặt phòng", "Bạn có thể đặt phòng trực tuyến qua website hoặc gọi điện cho chúng tôi.");
        responses.put("dịch vụ", "Chúng tôi cung cấp dịch vụ ăn uống, spa, và các tour du lịch khám phá.");
        responses.put("giờ nhận phòng", "Giờ nhận phòng là 14:00 và giờ trả phòng là 12:00.");
        responses.put("địa chỉ", "Khách sạn của chúng tôi nằm tại  P. Kiều Mai, Phúc Diễn, Từ Liêm, Hà Nội");
        responses.put("khuyến mãi", "Chúng tôi hiện có các chương trình khuyến mãi đặc biệt cho khách đặt phòng trước.");
        responses.put("thanh toán", "Chúng tôi chấp nhận thanh toán bằng thẻ tín dụng, thẻ ghi nợ và tiền mặt.");
        responses.put("chính sách hủy", "Bạn có thể hủy đặt phòng nhưng sẽ mất toàn bộ tiền cọc");
        responses.put("giờ mở cửa", "Khách sạn mở cửa 24/7 để phục vụ bạn.");
        responses.put("liên hệ", "Bạn có thể liên hệ với chúng tôi qua số điện thoại 0397156204.");
        responses.put("chính sách trẻ em", "Trẻ em dưới 5 tuổi được miễn phí, từ 5 đến 12 tuổi tính 50% giá phòng.");
        responses.put("wifi", "Khách sạn cung cấp wifi miễn phí trong toàn bộ khuôn viên.");
        responses.put("đặt bữa ăn", "Bạn có thể đặt bữa ăn qua dịch vụ phòng hoặc tại nhà hàng.");
        responses.put("khách sạn có cho phép thú cưng không", "Chúng tôi không cho phép thú cưng trong khách sạn.");
        responses.put("hủy dịch vụ", "Bạn có thể hủy dịch vụ trong vòng 1 giờ trước khi sử dụng.");
        responses.put("hello", "Chào Bạn cần minh giúp vấn đề gì ạ");
        responses.put("xin chào", "Chào Bạn cần minh giúp vấn đề gì ạ");
        responses.put("chào", "Chào Bạn cần minh giúp vấn đề gì ạ");
        responses.put("quy tắc", "QUY TẮC CỦA KHÁCH SẠN\n" +
                "1. Vui lòng xuất trình giấy tờ có giá trị hoặc hộ chiếu cùng thị thực xuất nhập cảnh còn giá trị tại quầy lễ tân khi nhận phòng.\n" +
                "2. Nghiêm cấm mang vật nuôi, vũ khí, vật liệu độc hại, chất nổ hoặc hóa chất dễ cháy vào khách sạn.\n" +
                "3. Vui lòng tiếp khách tại sảnh Lễ tân, không tiếp khách tại phòng ngủ.\n" +
                "4. Nghiêm cấm nấu ăn hoặc giặt giũ trong phòng.\n" +
                "5. Nghiêm cấm các hành vi đánh bạc, mại dâm, sử dụng các chất kích thích trong khách sạn.\n" +
                "6. Quý khách vui lòng tự bảo quản tài sản hoặc gửi tại quầy Lễ tân. Khách sạn không chịu trách nhiệm cho bất kỳ tổn thất nào.\n" +
                "7. Vui lòng tham khảo hướng dẫn cách phòng cháy chữa cháy và các trường hợp thoát hiểm khẩn cấp được đặt trong phòng.\n" +
                "8. Quý khách không được mang đồ ăn, thức uống, trái cây nặng mùi vào khách sạn.\n" +
                "9. Mọi hư hỏng, mất mát các trang thiết bị của khách sạn do quý khách gây ra, vui lòng đền bù đầy đủ trước khi trả phòng.\n" +
                "10. Quý khách không thay đổi, di chuyển các trang thiết bị trong phòng nếu không có sự đồng ý của Ban Quản lý khách sạn.\n" +
                "11. Quý khách sử dụng dịch vụ, trang thiết bị theo quy định của khách sạn.\n" +
                "12. Giá hiển thị trên trang web chỉ mang tính biểu thị, không bao gồm VAT và có thể thay đổi bất kỳ lúc nào có hoặc không có thông báo trước. Tất cả giá bao gồm bữa sáng. Chi phí giao dịch qua ngân hàng sẽ do khách hàng chi trả.\n" +
                "13. Miễn phí tiền phòng cho trẻ em từ 0-4 tuổi.\n" +
                "14. Khi ra khỏi phòng, xin Quý khách khoá cửa cẩn thận và gửi chìa khoá tại quầy Lễ tân. Khi nhận lại chìa khoá, xin vui lòng cho biết số phòng để tránh trường hợp người khác giả mạo.\n" +
                "15. Xin vui lòng không tiếp khách trong phòng ngủ khi cần thiết phải có sự đồng ý của Lễ tân và phải gửi giấy tờ tuỳ thân của khách tại quầy Lễ tân nhưng không ở lại sau 22 giờ.\n" +
                "16. Quý khách không được tự ý đổi phòng cho nhau, không được nhường phòng ngủ cho người khác sử dụng, không dẫn thêm người vào ngủ cùng phòng.\n" +
                "17. Trong trường hợp hủy phòng, khách sẽ mất toàn bộ tiền đã cọc.\n" +
                "18. Rác hoặc các chất khác có khả năng gây cản trở hoặc tắc nghẽn không được đưa vào, đặt hoặc vứt vào bồn rửa mặt, bồn tắm, phòng vệ sinh hoặc bất kỳ đường ống nào của khách sạn.\n" +
                "19. Tất cả rác thải nên được bỏ vào túi đặt trong thùng đựng rác, không xả rác và vứt các vật khác qua ban công hay cửa sổ.\n" +
                "20. Không được hút thuốc trong khách sạn.\n");

    }

    // Phương thức để xử lý câu hỏi từ người dùng
    public String getResponse(String userInput) {
        userInput = userInput.toLowerCase();  // Chuyển input thành chữ thường
        for (String key : responses.keySet()) {
            if (userInput.contains(key)) {
                return responses.get(key);  // Trả về phản hồi nếu từ khóa tồn tại trong câu hỏi
            }
        }
        return "Xin lỗi, tôi không hiểu câu hỏi của bạn.";  // Mặc định nếu không tìm thấy từ khóa
    }
}