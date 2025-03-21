package com.fiveamazon.erp.util;

import cn.hutool.core.io.FileUtil;
import com.spire.barcode.BarCodeType;
import com.spire.barcode.BarcodeScanner;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.io.File;

@Slf4j
public class BarcodePdfRenameUtils {
    public static void main(String[] args) {
        String fileFolderStr = "/Users/nan/s/barcode/";
        File fileFolder = new File(fileFolderStr);
        File[] fileList = fileFolder.listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                String filePath = file.getPath();
                if (!filePath.contains(".pdf")) {
                    continue;
                }
                String barcodeString = getBarcodeString(filePath);
                if (StringUtils.isBlank(barcodeString)) {
                    log.error("barcodeString is null");
                }
                try {
                    FileUtil.copy(filePath, fileFolderStr + barcodeString + ".pdf", false);
                } catch (Exception e) {
                    log.error("copy error: duplicate ?");
                }
            }
        }
    }

    private static String getBarcodeString(String filePath) {
        log.info("filePath: " + filePath);
        String barcodeString = null;
        PdfDocument pdf = new PdfDocument(filePath);
        PdfPageBase page;
        for (int i = 0; i < pdf.getPages().getCount(); i++) {
            page = pdf.getPages().get(i);
            for (BufferedImage image : page.extractImages()) {
                try {
                    String[] datas = BarcodeScanner.scan(image, BarCodeType.Code_128);
                    if (datas != null && datas.length > 0) {
                        barcodeString = datas[0];
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("barcode: error");
                }
            }
        }
        pdf.close();
        log.info("barcode: " + barcodeString);
        return barcodeString;
    }
}
