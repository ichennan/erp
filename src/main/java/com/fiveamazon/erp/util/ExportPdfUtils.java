package com.fiveamazon.erp.util;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

@Slf4j
public class ExportPdfUtils {

    @Autowired
    CommonPdfUtils commonPdfUtils;

    public byte[] getPdf() {
        JSONObject pdfJson = new JSONObject();
        String xsltFileString = "fop/articleFO.xsl";
        InputStream xsltFile = this.getClass().getClassLoader().getResourceAsStream(xsltFileString);
        XMLSerializer serial = new XMLSerializer();
        serial.setArrayName("array");
        serial.setElementName("element");
        serial.setObjectName("article");
        pdfJson.put("userName", "testUserName");
        pdfJson.put("date", "testDate");
        String xml = serial.write(pdfJson);
        log.warn(xml);
        byte[] data = commonPdfUtils.generatePdf(xsltFile, xml, "", "PDF", "PDF", "Sample order");
        return data;
    }

}
