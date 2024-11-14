package com.hiepnt.moviebooking.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class QRCodeGenerator {
    public void generateQRCode(String bookingCode) throws WriterException, IOException {
        String qrCodeDirectory = "./qrcodes/";

        // Kiểm tra và tạo thư mục nếu chưa tồn tại
        File directory = new File(qrCodeDirectory);
        if (!directory.exists()) {
            Files.createDirectories(Paths.get(qrCodeDirectory));
        }
        String filePath = "./qrcodes/" + bookingCode + ".png"; // Đường dẫn lưu QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(bookingCode, BarcodeFormat.QR_CODE, 300, 300);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
}
