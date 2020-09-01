package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.PlanCreateDTO;
import com.fiveamazon.erp.dto.UploadSupplierDeliveryDTO;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.service.*;
import com.fiveamazon.erp.util.CommonPdfUtils;
import com.fiveamazon.erp.util.ExportPdfUtils;
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
@RequestMapping(value = "/sku")
@Slf4j
public class SkuController extends SimpleCommonController {
	@Autowired
	ProductService productService;
	@Autowired
	SkuService skuService;
	@Autowired
	ShipmentService shipmentService;
	@Autowired
	PurchaseService purchaseService;
	@Autowired
	PacketService packetService;
	@Autowired
	CommonPdfUtils commonPdfUtils;
	@Autowired
	PlanService planService;

	@Value("${simple.folder.image.product}")
	private String productImageFolder;

	@RequestMapping(value = "/test.pdf", method= RequestMethod.GET, produces = "application/pdf")
	public byte[] getPdfSampleOrder() {
		return getPdf();
	}


	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showView() {
		this.parameters.put("pageName", "sku");
		ModelAndView mv = new ModelAndView("sku", parameters);
		return mv;
	}

	@RequestMapping(value = "/findAll", method= RequestMethod.POST)
	public String findAll(){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<SkuInfoVO> skuInfoVOList = skuService.findAll();
		for(SkuInfoVO skuInfoVO: skuInfoVOList){
			array.put(skuInfoVO.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/getDetail", method= RequestMethod.POST)
	public String getDetail(@RequestParam("id")Integer id){
		log.warn("SkuController.getDetail: " + id);
		Integer skuId = id;
		SkuInfoPO skuInfoPO = skuService.getById(id);
		Integer productId = skuInfoPO.getProductId();
		JSONObject rs = new JSONObject();
		rs.put("skuId", skuId);
		rs.put("productId", productId);
		//
		rs.put("skuShipmentJson", skuService.getSkuShipmentObject(skuId));
		rs.put("skuElseShipmentJson", skuService.getSkuElseShipmentObject(productId, skuId));
		rs.put("productPurchaseJson", skuService.getProductPurchaseObject(productId));
		rs.put("productInventoryJson", skuService.getProductInventoryObjectBySkuId(skuId));
		//
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/createPlan", method= RequestMethod.POST)
	public String createPlan(@RequestBody PlanCreateDTO planCreateDTO){
		log.warn("SkuController.createPlan: " + new JSONObject(planCreateDTO).toString());
		Integer planId = planService.create(planCreateDTO);
		//
		JSONObject rs = new JSONObject();
		rs.put("planId", planId);
		rs.put("error", false);
		return rs.toString();
	}



	public byte[] getPdf() {
		net.sf.json.JSONObject pdfJson = new net.sf.json.JSONObject();
//		String xsltFileString = "fop/articleFO.xsl";
		String xsltFileString = "fop/planFO.xsl";
		InputStream xsltFile = this.getClass().getClassLoader().getResourceAsStream(xsltFileString);
		XMLSerializer serial = new XMLSerializer();
		serial.setArrayName("array");
		serial.setElementName("element");
		serial.setObjectName("article");
		pdfJson.put("userName", "testUserName");
		pdfJson.put("date", "testDate");

		pdfJson.put("element", 123123);
		pdfJson.put("array", new net.sf.json.JSONArray());
		pdfJson.put("object", new net.sf.json.JSONObject());
		String xml = serial.write(pdfJson);
		log.warn(xml);
		byte[] data = commonPdfUtils.generatePdf(xsltFile, xml, "", "PDF", "PDF", "Sample order");
		return data;
	}
}

