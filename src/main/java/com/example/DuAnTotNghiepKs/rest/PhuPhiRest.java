package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.PhuPhi;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import com.example.DuAnTotNghiepKs.service.PhuPhiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class PhuPhiRest {

    @Autowired
    private PhuPhiService phuPhiService;

    @Autowired
    private DatPhongService datPhongService;

    @PostMapping("/phuPhi/add")
    public ResponseEntity<?> addPhuPhi(
            @RequestParam(value = "idDatPhong", required = true) Integer idDatPhong,
            @RequestParam(value = "tenPhuPhi", required = true) String tenPhuPhi,
            @RequestParam(value = "giaPhuPhi", required = true) Float giaPhuPhi) {
        try {
            if (idDatPhong == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Bạn chưa chọn hóa đơn để thanh toán"));
            }

            DatPhong datPhong = datPhongService.getDatPhongById(idDatPhong);
            if (datPhong == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy hóa đơn với ID: " + idDatPhong));
            }

            if (tenPhuPhi == null || tenPhuPhi.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin tên phụ phí."));
            }
            if (giaPhuPhi == null || giaPhuPhi <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin hoặc giá phụ phí không hợp lệ."));
            }

            PhuPhi phuPhi = new PhuPhi();
            phuPhi.setDatPhong(datPhong);
            phuPhi.setTenPhuPhi(tenPhuPhi);
            phuPhi.setGiaPhuPhi(giaPhuPhi);

            phuPhiService.save(phuPhi);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", "Phụ phí đã được thêm thành công."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Đã xảy ra lỗi khi thêm phụ phí: " + e.getMessage()));
        }
    }


    @DeleteMapping("/phuPhi/delete/{idPhuPhi}")
    public ResponseEntity<?> deletePhuPhi(@PathVariable Integer idPhuPhi) {
        try {
            PhuPhi phuPhi = phuPhiService.findById(idPhuPhi);
            if (phuPhi == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy phụ phí với ID: " + idPhuPhi));
            }
            phuPhiService.delete(idPhuPhi);
            return ResponseEntity.ok(Map.of("success", "Xóa thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Đã xảy ra lỗi khi xóa phụ phí: " + e.getMessage()));
        }
    }

    @PutMapping("/phuPhi/update")
    public ResponseEntity<?> updatePhuPhi(@RequestParam(value = "idDatPhong")Integer idDatPhong,
                                          @RequestParam(value = "idPhuPhi")Integer idPhuPhi,
                                          @RequestParam(value = "tenPhuPhi")String tenPhuphi,
                                          @RequestParam(value = "giaPhuPhi")Float giaPhuphi){
        try {
            System.out.println("idDatPhong: " + idDatPhong);
            System.out.println("idPhuPhi: " + idPhuPhi);
            System.out.println("tenPhuPhi: " + tenPhuphi);
            System.out.println("giaPhuPhi: " + giaPhuphi);

            DatPhong datPhong = datPhongService.getDatPhongById(idDatPhong);
            PhuPhi phuPhi = new PhuPhi();
            phuPhi.setDatPhong(datPhong);
            phuPhi.setIdPhuPhi(idPhuPhi);
            phuPhi.setTenPhuPhi(tenPhuphi);
            phuPhi.setGiaPhuPhi(giaPhuphi);
            phuPhiService.update(phuPhi);
            return ResponseEntity.ok(Map.of("success","Update phụ phí thành công"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("error","Xảy ra lỗi khi update phụ phí"+e.getMessage()));
        }
    }

}