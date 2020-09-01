package com.fiveamazon.erp.util;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

@Service
@Component
public class CommonPdfUtils {
    private FopFactory fopFactory;
//    private static final String FOP_CONF_FILE = "conf/fop.xml";
//    private static final String FOP_FONT_BASE = "/cn/fonts";

    public CommonPdfUtils() {
    }

    @PostConstruct
    public void init() {
        try {
            this.fopFactory = FopFactory.newInstance();
            DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
            Configuration cfg = cfgBuilder.buildFromFile((new File("fop.xml")));
            this.fopFactory.setFontBaseURL("/Users/nan/s/fonts");
            this.fopFactory.setUserConfig(cfg);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void clearImageCache() {
        System.out.println("Clearing image cache!");
        this.fopFactory.getImageManager().getCache().clearCache();
    }

    public byte[] generatePdf(InputStream xsltFile, File xml, String producer, String creator, String user, String title) {
        return this.generatePDF(xsltFile, new StreamSource(xml), producer, creator, user, title);
    }

    public byte[] generatePdf(InputStream xsltFile, String xml, String producer, String creator, String user, String title) {
        return this.generatePDF(xsltFile, new StreamSource(new StringReader(xml)), producer, creator, user, title);
    }

    private byte[] generatePDF(InputStream xsltFile, StreamSource src, String producer, String creator, String user, String title) {
        try {
            this.clearImageCache();
            FOUserAgent foUserAgent = this.fopFactory.newFOUserAgent();
            foUserAgent.setProducer(producer);
            foUserAgent.setCreator(creator);
            foUserAgent.setAuthor(user);
            foUserAgent.setTitle(title);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] var13;
            try {
                Fop fop = this.fopFactory.newFop("application/pdf", foUserAgent, out);
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));
                transformer.setParameter("versionParam", "2.0");
                Result res = new SAXResult(fop.getDefaultHandler());
                transformer.transform(src, res);
                var13 = out.toByteArray();
            } finally {
                out.close();
            }

            return var13;
        } catch (Exception var18) {
            System.out.println("PDF UTIL EXCEPTION YO: " + var18.getClass().getSimpleName() + "! Message: " + var18.getMessage());
            var18.printStackTrace();
            return null;
        }
    }

    public byte[] generateArticlePdf(String xsltFileString, cn.hutool.json.JSONObject contentJson, String producer, String creator, String title, String userName) {
        InputStream xsltFile = this.getClass().getClassLoader().getResourceAsStream(xsltFileString);
        XMLSerializer serial = new XMLSerializer();
        serial.setArrayName("array");
        serial.setElementName("element");
        serial.setObjectName("object");
        contentJson.put("userName", "testUserName");
        contentJson.put("date", "testDate");
        contentJson.put("element", 123123);
        contentJson.put("array", new JSONArray());
        contentJson.put("object", new JSONObject());
        String xml = serial.write(JSONObject.fromObject(contentJson.toString()));
        return this.generatePdf(xsltFile, xml, producer, creator, userName, title);
    }
}
