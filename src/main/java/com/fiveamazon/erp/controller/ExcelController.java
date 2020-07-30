package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.AnalysisEventListener;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.common.SimpleCommonException;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderDetailPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryPO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderDetailEO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderEO;
import com.fiveamazon.erp.service.ExcelService;
import com.fiveamazon.erp.service.ProductService;
import com.fiveamazon.erp.util.CommonExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@RestController
@RequestMapping(value = "/excel")
@Slf4j
public class ExcelController extends SimpleCommonController {
	@Autowired
	ProductService productService;
	@Autowired
	ExcelService excelService;

	@Value("${simple.folder.image.product}")
	private String productImageFolder;


	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showView() {
		this.parameters.put("pageName", "excel");
		ModelAndView mv = new ModelAndView("excel", parameters);
		return mv;
	}

    @RequestMapping(value = "/findByExcelId", method= RequestMethod.POST)
    public String findByExcelId(@RequestParam("excelId")Integer excelId){
        JSONObject rs = new JSONObject();
        JSONArray orderArray = new JSONArray();
        JSONArray orderDetailArray = new JSONArray();
        List<ExcelSupplierDeliveryOrderPO> excelSupplierDeliveryOrderPOList = excelService.findOrderByExcelId(excelId);
        List<ExcelSupplierDeliveryOrderDetailPO> excelSupplierDeliveryOrderDetailPOList = excelService.findOrderDetailByExcelId(excelId);
        for(ExcelSupplierDeliveryOrderPO excelSupplierDeliveryOrderPO: excelSupplierDeliveryOrderPOList){
            orderArray.put(excelSupplierDeliveryOrderPO.toJson());
        }
        for(ExcelSupplierDeliveryOrderDetailPO excelSupplierDeliveryOrderDetailPO: excelSupplierDeliveryOrderDetailPOList){
            orderDetailArray.put(excelSupplierDeliveryOrderDetailPO.toJson());
        }
        rs.put("orderArray", orderArray);
        rs.put("orderDetailArray", orderDetailArray);
        rs.put("error", false);
        return rs.toString();
    }

	@PostMapping("/uploadSupplierDelivery")
	public String uploadSupplierDelivery(@RequestParam(value="file",required=false) MultipartFile multipartFile) throws IOException{
		log.warn("ExcelController.uploadSupplierDelivery");
		JSONObject rs = new JSONObject();
		if(multipartFile == null){
			throw new SimpleCommonException("file is null o");
		}
		String originalFileName = multipartFile.getOriginalFilename();
		log.info("Original File Name:" + originalFileName);
		//
		ExcelSupplierDeliveryPO excelSupplierDeliveryPO = new ExcelSupplierDeliveryPO();
		excelSupplierDeliveryPO.setFileName(originalFileName);
		Integer excelId = excelService.saveExcelSupplierDelivery(excelSupplierDeliveryPO);
		//
		AnalysisEventListener<ExcelSupplierDeliveryOrderEO> userAnalysisEventListenerSheet1 = CommonExcelUtils.getListener(this.batchInsertExcelSupplierDeliveryOrder(excelId), 2);
		EasyExcel.read(multipartFile.getInputStream(), ExcelSupplierDeliveryOrderEO.class, userAnalysisEventListenerSheet1).sheet(0).doRead();
		//
		AnalysisEventListener<ExcelSupplierDeliveryOrderDetailEO> userAnalysisEventListenerSheet2 = CommonExcelUtils.getListener(this.batchInsertExcelSupplierDeliveryOrderDetail(excelId), 2);
		EasyExcel.read(multipartFile.getInputStream(), ExcelSupplierDeliveryOrderDetailEO.class, userAnalysisEventListenerSheet2).sheet(1).doRead();
		rs.put("excelId", excelId);
		return rs.toString();
	}

	private Consumer<List<ExcelSupplierDeliveryOrderEO>> batchInsertExcelSupplierDeliveryOrder(Integer excelId){
		return supplierDeliveryOrderEpoList -> excelService.insertExcelSupplierDeliveryOrder(excelId, supplierDeliveryOrderEpoList);
	}

	private Consumer<List<ExcelSupplierDeliveryOrderDetailEO>> batchInsertExcelSupplierDeliveryOrderDetail(Integer excelId){
		return supplierDeliveryOrderDetailEpoList -> excelService.insertExcelSupplierDeliveryOrderDetail(excelId, supplierDeliveryOrderDetailEpoList);
	}
}

