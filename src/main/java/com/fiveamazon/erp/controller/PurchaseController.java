package com.fiveamazon.erp.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.PurchaseDTO;
import com.fiveamazon.erp.dto.PurchaseDetailDTO;
import com.fiveamazon.erp.dto.PurchaseProductSearchDTO;
import com.fiveamazon.erp.dto.PurchaseSearchDTO;
import com.fiveamazon.erp.dto.download.PurchaseDetailDownloadDTO;
import com.fiveamazon.erp.dto.download.PurchaseDownloadDTO;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.service.ExcelService;
import com.fiveamazon.erp.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@RestController
@RequestMapping(value = "/purchase")
@Slf4j
public class PurchaseController extends SimpleCommonController {
	@Autowired
	PurchaseService purchaseService;
	@Autowired
	ExcelService excelService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showView() {
		this.parameters.put("pageName", "purchase");
		ModelAndView mv = new ModelAndView("purchase", parameters);
		return mv;
	}

	@RequestMapping(value = "/findAll", method= RequestMethod.POST)
	public String findAll(PurchaseSearchDTO purchaseSearchDTO){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<PurchaseViewPO> purchaseViewPOS = purchaseService.findAll(purchaseSearchDTO);
		for(PurchaseViewPO purchaseViewPO: purchaseViewPOS){
			array.put(purchaseViewPO.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/findAutoCompleteSupplier", method= RequestMethod.POST)
	public String findAutoCompleteSupplier(){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<String> supplierList = purchaseService.findSupplierList();
		for(String supplier: supplierList){
			if(StringUtils.isNotEmpty(supplier)){
				array.put(supplier);
			}
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/findAllDetail", method= RequestMethod.POST)
	public String findAllDetail(Integer purchaseId){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<PurchaseDetailPO> purchaseDetailPOS = purchaseService.findAllDetail(purchaseId);
		for(PurchaseDetailPO purchaseDetailPO: purchaseDetailPOS){
			array.put(purchaseDetailPO.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/getDetail", method= RequestMethod.POST)
	public String getDetail(@RequestParam("id")Integer id){
		JSONObject rs = new JSONObject();
		PurchasePO purchasePO = purchaseService.getById(id);
		Integer excelId = purchasePO.getExcelId();
		String excelDingdan = purchasePO.getExcelDingdan();
		ExcelSupplierDeliveryOrderPO excelSupplierDeliveryOrderPO = excelService.getExcelSupplierDeliveryOrderByExcelIdAndDingdanhao(excelId, excelDingdan);
		JSONObject dataJson = new JSONObject(excelSupplierDeliveryOrderPO);
		dataJson.putAll(purchasePO.toJson());
		rs.put("data", dataJson);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/saveDetail", method= RequestMethod.POST)
	public String saveDetail(PurchaseDTO purchaseDTO){
		purchaseDTO.setUsername(getUsername());
		JSONObject rs = new JSONObject();
		PurchasePO purchasePO = purchaseService.save(purchaseDTO);
		rs.put("data", purchasePO.toJson());
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/getDetailListDetail", method= RequestMethod.POST)
	public String getDetailListDetail(@RequestParam("id")Integer id){
		JSONObject rs = new JSONObject();
		PurchaseDetailPO purchaseDetailPO = purchaseService.getDetailById(id);
		rs.put("data", purchaseDetailPO.toJson());
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/saveDetailListDetail", method= RequestMethod.POST)
	public String saveDetailListDetail(PurchaseDetailDTO purchaseDetailDTO){
		purchaseDetailDTO.setUsername(getUsername());
		JSONObject rs = new JSONObject();
		PurchaseDetailPO purchaseDetailPO = purchaseService.saveDetail(purchaseDetailDTO);
		rs.put("data", purchaseDetailPO.toJson());
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/findAllProducts", method= RequestMethod.POST)
	public String findAllProducts(PurchaseProductSearchDTO purchaseProductSearchDTO){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<PurchaseProductViewPO> list = purchaseService.findAllProducts(purchaseProductSearchDTO);
		for(PurchaseProductViewPO item: list){
			array.put(item.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/downloadList", method= RequestMethod.POST)
	public String downloadList(HttpServletResponse response, @RequestParam String formData) throws IOException {
		log.info("PurchaseController.downloadList: " + formData);
		JSONObject rs = new JSONObject();
		JSONObject formDataJson = new JSONObject(formData);
		PurchaseSearchDTO searchDTO = JSONUtil.toBean(formDataJson, PurchaseSearchDTO.class);
		//sheet1 data

		List<PurchaseDownloadDTO> purchaseDownloadDTOList = new ArrayList<>();
		List<PurchaseViewPO> purchaseViewPOS = purchaseService.findAll(searchDTO);
		for(PurchaseViewPO purchaseViewPO: purchaseViewPOS){
			PurchaseDownloadDTO purchaseDownloadDTO = new PurchaseDownloadDTO();
			BeanUtils.copyProperties(purchaseViewPO, purchaseDownloadDTO);
			purchaseDownloadDTOList.add(purchaseDownloadDTO);
		}


		//sheet2 data
		List<PurchaseDetailDownloadDTO> detailDownloadDTOList = purchaseService.downloadDetail(searchDTO);

//		if(searchResultJson.getLong("total") > maxDownload){
//			throw new CommonException("查询结果超过 " + maxDownload + " 条，请缩小下载范围");
//		}
		String fileName = URLEncoder.encode("Purchase" + DateUtil.format(new Date(), "yyyyMMddHHmmss"), "UTF-8");

		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

		// excel头策略
		WriteCellStyle headWriteCellStyle = new WriteCellStyle();
		WriteFont headWriteFont = new WriteFont();
		headWriteFont.setFontHeightInPoints((short) 15);
		headWriteFont.setBold(false);
		headWriteCellStyle.setWriteFont(headWriteFont);

		// excel内容策略
		WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
		WriteFont contentWriteFont = new WriteFont();
		contentWriteFont.setFontHeightInPoints((short)15);
		contentWriteCellStyle.setWriteFont(contentWriteFont);

		// 设置handler
		HorizontalCellStyleStrategy styleStrategy =
				new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

//		EasyExcel.write(response.getOutputStream(), PurchaseDownloadDTO.class)
//				.sheet("Order List1")
//				.registerWriteHandler(styleStrategy)
//				.doWrite(purchaseDownloadDTOList)
//		;

//		ExcelWriterBuilder excelWriterBuilder =  EasyExcel.write(response.getOutputStream());
//		excelWriterBuilder.registerWriteHandler(styleStrategy);
//		ExcelWriter excelWriter = excelWriterBuilder.build();
		ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).registerWriteHandler(styleStrategy).build();
		WriteSheet writeSheet1 = EasyExcel.writerSheet("采购批次").head(PurchaseDownloadDTO.class).build();
		WriteSheet writeSheet2 = EasyExcel.writerSheet("采购详情").head(PurchaseDetailDownloadDTO.class).build();
		excelWriter.write(purchaseDownloadDTOList, writeSheet1);
		excelWriter.write(detailDownloadDTOList, writeSheet2);
		excelWriter.finish();
		rs.putOpt("error", false);
		return rs.toString();
	}
}



