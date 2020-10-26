package com.fiveamazon.erp.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.entity.SkuInfoPO;
import com.fiveamazon.erp.entity.SkuInfoVO;
import com.fiveamazon.erp.service.*;
import com.fiveamazon.erp.util.CommonPdfUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.xml.XMLSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.websocket.server.PathParam;
import java.io.InputStream;
import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@RestController
@RequestMapping(value = "/fnsku")
@Slf4j
public class FnskuController extends SimpleCommonController {
	@Autowired
	CommonPdfUtils commonPdfUtils;

	@RequestMapping(value = "/{fnsku}.pdf", method= RequestMethod.GET, produces = "application/pdf")
	public byte[] getFnskuPdf(@PathVariable("fnsku") String fnsku) {
		byte[] pdfBytes = null;
		try{
			pdfBytes = FileUtil.readBytes("/Users/nan/s/barcode/" + fnsku + ".pdf");
		}catch (Exception e){
			pdfBytes = FileUtil.readBytes("/Users/nan/s/empty.pdf");
		}
		return pdfBytes;
	}

	@RequestMapping(value = "/{fnsku}", method= RequestMethod.GET, produces = "application/pdf")
	public void getFnsku(@PathVariable("fnsku") String fnsku) {
		log.warn("getFnsku");
		log.warn("fnsku: " + fnsku);
		log.warn("getFnsku");
	}
}

