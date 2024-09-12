package com.example.DuAnTotNghiepKs.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;



@Service
public class QrCodeService {

    // Phương thức để tạo mã QR
    public byte[] generateQRCode(String text, int width, int height) throws IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException e) {
            throw new RuntimeException("QR Code generation failed", e);
        }

        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(qrCodeImage, "png", baos);
        } catch (IOException e) {
            throw new IOException("Failed to write QR code image to output stream", e);
        }
        return baos.toByteArray();
    }

    // Phương thức để giải mã mã QR
    public Map<String, String> decodeQRCode(byte[] qrCodeImageBytes) {
        Map<String, String> decodedData = new HashMap<>();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(qrCodeImageBytes)) {
            BufferedImage bufferedImage = ImageIO.read(bais);

            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
            MultiFormatReader multiFormatReader = new MultiFormatReader();

            Result result;
            try {
                result = multiFormatReader.decode(binaryBitmap);
                decodedData.put("text", result.getText());
            } catch (ReaderException e) {
                decodedData.put("error", "Failed to decode QR code: " + e.getMessage());
            }
        } catch (IOException e) {
            decodedData.put("error", "Failed to read image: " + e.getMessage());
        }
        return decodedData;
    }
}

