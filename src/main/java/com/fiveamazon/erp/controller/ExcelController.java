package com.fiveamazon.erp.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.AnalysisEventListener;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.common.SimpleCommonException;
import com.fiveamazon.erp.epo.SupplierDeliveryOrderEpo;
import com.fiveamazon.erp.epo.TestEpo;
import com.fiveamazon.erp.epo.TestEpo2;
import com.fiveamazon.erp.service.ExcelService;
import com.fiveamazon.erp.service.ProductService;
import com.fiveamazon.erp.service.SkuService;
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

	@PostMapping("/uploadSupplierDelivery")
	public String uploadSupplierDelivery(@RequestParam(value="file",required=false) MultipartFile multipartFile) throws IOException{
		log.warn("ExcelController.uploadSupplierDelivery");
		if(multipartFile == null){
			throw new SimpleCommonException("file is null o");
		}
		String originalFileName = multipartFile.getOriginalFilename();
		log.info("Original File Name:" + originalFileName);
		AnalysisEventListener<SupplierDeliveryOrderEpo> userAnalysisEventListener = CommonExcelUtils.getListener(this.batchInsert(), 2);
		EasyExcel.read(multipartFile.getInputStream(), SupplierDeliveryOrderEpo.class, userAnalysisEventListener).sheet(0).doRead();
		//

		AnalysisEventListener<TestEpo2> userAnalysisEventListener2 = CommonExcelUtils.getListener(this.batchInsert2(), 2);
		EasyExcel.read(multipartFile.getInputStream(), TestEpo2.class, userAnalysisEventListener2).sheet(1).doRead();
		return "success";
	}

	private Consumer<List<SupplierDeliveryOrderEpo>> batchInsert(){
		return supplierDeliveryOrderEpoList -> excelService.insert(supplierDeliveryOrderEpoList);
	}

	private Consumer<List<TestEpo2>> batchInsert2(){
		return users -> excelService.test2(users);
	}
}

