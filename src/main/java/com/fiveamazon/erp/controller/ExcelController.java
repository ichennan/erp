package com.fiveamazon.erp.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.AnalysisEventListener;
import com.aspose.cells.LoadFormat;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.common.SimpleCommonException;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.UploadFbaDTO;
import com.fiveamazon.erp.dto.UploadSupplierDeliveryDTO;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.entity.excel.ExcelCarrierBillDetailPO;
import com.fiveamazon.erp.entity.excel.ExcelCarrierBillPO;
import com.fiveamazon.erp.entity.excel.ExcelTransactionDetailPO;
import com.fiveamazon.erp.entity.excel.ExcelTransactionPO;
import com.fiveamazon.erp.epo.*;
import com.fiveamazon.erp.service.*;
import com.fiveamazon.erp.util.CommonExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
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
	@Autowired
	TransactionService transactionService;
	@Autowired
	PurchaseService purchaseService;
	@Autowired
	ShipmentService shipmentService;
	@Autowired
	OverseaService overseaService;

	@Value("${simple.folder.file.upload}")
	private String uploadFileFolder;


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

	@RequestMapping(value = "/findFbaByExcelId", method= RequestMethod.POST)
	public String findFbaByExcelId(@RequestParam("excelId")Integer excelId){
		JSONObject rs = new JSONObject();
		ExcelFbaPO excelFbaPO = excelService.getFbaByExcelId(excelId);
		List<ExcelFbaPackListPO> excelFbaPackListPOList = excelService.findFbaPackListByExcelId(excelId);
		JSONArray array = new JSONArray();
		for(ExcelFbaPackListPO excelFbaPackListPO: excelFbaPackListPOList){
			array.put(excelFbaPackListPO.toJson());
		}
		rs.put("array", array);
		rs.put("data", new JSONObject(excelFbaPO));
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/findCarrierBillByExcelId", method= RequestMethod.POST)
	public String findCarrierBillByExcelId(@RequestParam("excelId")Integer excelId){
		JSONObject rs = new JSONObject();
		ExcelCarrierBillPO excelPO = excelService.getCarrierBillByExcelId(excelId);
		List<ExcelCarrierBillDetailPO> detailPOList = excelService.findCarrierBillDetailByExcelId(excelId);
		JSONArray array = new JSONArray();
		for(ExcelCarrierBillDetailPO detailPO: detailPOList){
			array.put(detailPO.toJson());
		}
		rs.put("array", array);
		rs.put("data", new JSONObject(excelPO));
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/findTransactionByExcelId", method= RequestMethod.POST)
	public String findTransactionByExcelId(@RequestParam("excelId")Integer excelId){
		JSONObject rs = new JSONObject();
		ExcelTransactionPO excelTransactionPO = excelService.getTransactionByExcelId(excelId);
		Integer storeId = excelTransactionPO.getStoreId();
		StorePO storePO = productService.getStoreById(storeId);
		String storeName = storePO.getName();
		List<ExcelTransactionDetailPO> list = excelService.findTransactionDetailByExcelId(excelId);
		JSONArray array = new JSONArray();
		JSONObject typeJson = new JSONObject();
		Date dateFrom = excelTransactionPO.getDateFrom();
		Date dateTo = excelTransactionPO.getDateTo();
		for(ExcelTransactionDetailPO item: list){
			String type = item.getType();
			BigDecimal total = item.getTotal();
			if(!StringUtils.hasText(type)){
				type = "Others";
			}
			if(typeJson.containsKey(type)){
				JSONObject typeContentJson = typeJson.getJSONObject(type);
				typeContentJson.put("total", typeContentJson.getBigDecimal("total").add(total));
				typeContentJson.put("count", typeContentJson.getInt("count") + 1);
			}else{
				JSONObject typeContentJson = new JSONObject();
				typeContentJson.put("type", type);
				typeContentJson.put("total", total);
				typeContentJson.put("count", 1);
				typeJson.put(type, typeContentJson);
			}
		}

		JSONArray arraySum = new JSONArray();
		JSONObject jsonStore = new JSONObject();
		jsonStore.put("type", "Store");
		jsonStore.put("count", storeName);
		jsonStore.put("total", "");
		arraySum.add(jsonStore);
		//
		JSONObject jsonDate = new JSONObject();
		jsonDate.put("type", "Date");
		jsonDate.put("count", DateUtil.format(dateFrom, "yyyy-MM-dd HH:mm:ss"));
		jsonDate.put("total", DateUtil.format(dateTo, "yyyy-MM-dd HH:mm:ss"));
		arraySum.add(jsonDate);
		//
		JSONObject jsonSum = new JSONObject();
		arraySum.add(jsonSum);
		//
		Integer countCount = 0;
		BigDecimal totalTotal = new BigDecimal(0);
		for(String key : typeJson.keySet()){
			if("Transfer".equalsIgnoreCase(key)){
				arraySum.add(typeJson.get(key));
			}else{
				countCount += typeJson.getJSONObject(key).getInt("count");
				totalTotal = totalTotal.add(typeJson.getJSONObject(key).getBigDecimal("total"));
				array.add(typeJson.get(key));
			}
		}
		//
		jsonSum.put("type", "Sum");
		jsonSum.put("count", countCount);
		jsonSum.put("total", totalTotal);

		rs.put("array", array);
		rs.put("arraySum", arraySum);
		rs.put("error", false);
		return rs.toString();
	}

	@PostMapping("/upload")
	public String upload(@RequestParam(value="file",required=false) MultipartFile multipartFile, String uploadFileData) throws IOException{
		log.warn("ExcelController.upload uploadFileData: " + uploadFileData);
		String uploadFileName = saveFile(multipartFile);
		JSONObject uploadFileJson = new JSONObject(uploadFileData);
		String fileCategory = uploadFileJson.getStr("fileCategory");
		JSONObject rs = new JSONObject();
		rs.put("fileCategory", fileCategory);
		if(multipartFile == null){
			throw new SimpleCommonException("file is null o");
		}
		String originalFileName = multipartFile.getOriginalFilename();
		log.info("Original File Name:" + originalFileName);
		Integer excelId;
		String tempExcelName;
		//
		switch (fileCategory){
			case "fba":
				ExcelFbaPO excelFbaPO = new ExcelFbaPO();
				excelFbaPO.setFileName(originalFileName);
				excelId = excelService.saveExcelFba(excelFbaPO);
				//
				AnalysisEventListener<ExcelFbaRowEO> listenerExcelFbaRowEO = CommonExcelUtils.getListener(this.batchInsertExcelFbaPackList(excelId), 100);
				EasyExcel.read(uploadFileFolder + uploadFileName, ExcelFbaRowEO.class, listenerExcelFbaRowEO).sheet(0).headRowNumber(0).doRead();
				rs.put("excelId", excelId);
				break;
			case "fbatsv":
				ExcelFbaPO excelFbaPO4tsv = new ExcelFbaPO();
				excelFbaPO4tsv.setFileName(originalFileName);
				excelId = excelService.saveExcelFba(excelFbaPO4tsv);
				tempExcelName = convertTsvToXlsx(uploadFileName);
				//
				AnalysisEventListener<ExcelFbatsvRowEO> listenerExcelFbatsvEO = CommonExcelUtils.getListener(this.batchInsertExcelFbatsvPackList(excelId), 100);
				EasyExcel.read(uploadFileFolder + tempExcelName, ExcelFbatsvRowEO.class, listenerExcelFbatsvEO).sheet(0).headRowNumber(0).doRead();
				rs.put("excelId", excelId);
				break;
			case SimpleConstant.FILE_CATEGORY_MonthlyUnifiedTransaction:
				ExcelTransactionPO excelTransactionPO = new ExcelTransactionPO();
				excelTransactionPO.setFileName(originalFileName);
				excelId = excelService.saveExcelTransaction(excelTransactionPO);
				tempExcelName = convertCsvToXlsx(uploadFileName);
				//
				AnalysisEventListener<ExcelTransactionRowEO> listenerExcelTransactionEO = CommonExcelUtils.getListener(this.batchInsertTransactionRow(excelId), 100);
				EasyExcel.read(uploadFileFolder + tempExcelName, ExcelTransactionRowEO.class, listenerExcelTransactionEO).sheet(0).headRowNumber(8).doRead();
				rs.put("excelId", excelId);
				break;
			case SimpleConstant.FILE_CATEGORY_CarrierBillCainiao:
				ExcelCarrierBillPO excelCarrierBillPO = new ExcelCarrierBillPO();
				excelCarrierBillPO.setFileName(originalFileName);
				excelCarrierBillPO.setCarrier(SimpleConstant.CARRIER_Cainiao);
				excelId = excelService.saveExcelCarrierBill(excelCarrierBillPO);
				//
				AnalysisEventListener<ExcelCarrierBillCainiaoRowEO> listenerExcelCarrierBillEO = CommonExcelUtils.getListener(this.batchInsertCarrierBillRow(excelId), 1000);
				EasyExcel.read(uploadFileFolder + uploadFileName, ExcelCarrierBillCainiaoRowEO.class, listenerExcelCarrierBillEO).sheet(0).headRowNumber(3).doRead();
				rs.put("excelId", excelId);
				break;
			case "supplierDelivery":
				ExcelSupplierDeliveryPO excelSupplierDeliveryPO = new ExcelSupplierDeliveryPO();
				excelSupplierDeliveryPO.setFileName(originalFileName);
				excelId = excelService.saveExcelSupplierDelivery(excelSupplierDeliveryPO);
				convertTsvToXlsx(uploadFileName);
				//
				AnalysisEventListener<ExcelSupplierDeliveryOrderEO> listenerExcelSupplierDeliveryOrderEO = CommonExcelUtils.getListener(this.batchInsertExcelSupplierDeliveryOrder(excelId), 100);
				EasyExcel.read(uploadFileFolder + uploadFileName, ExcelSupplierDeliveryOrderEO.class, listenerExcelSupplierDeliveryOrderEO).sheet(0).doRead();
				//
				AnalysisEventListener<ExcelSupplierDeliveryOrderDetailEO> listenerExcelSupplierDeliveryOrderDetailEO = CommonExcelUtils.getListener(this.batchInsertExcelSupplierDeliveryOrderDetail(excelId), 100);
				EasyExcel.read(uploadFileFolder + uploadFileName, ExcelSupplierDeliveryOrderDetailEO.class, listenerExcelSupplierDeliveryOrderDetailEO).sheet(1).doRead();
				rs.put("excelId", excelId);
				break;
		}
		return rs.toString();
	}

	@RequestMapping(value = "/uploadToPurchase", method= RequestMethod.POST)
	public String uploadToPurchase(@RequestBody UploadSupplierDeliveryDTO uploadSupplierDeliveryDTO){
		log.warn("ExcelController.uploadToPurchase");
		log.warn(new JSONObject(uploadSupplierDeliveryDTO).toString());
		purchaseService.createByExcel(uploadSupplierDeliveryDTO);
		JSONObject rs = new JSONObject();
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/uploadToTransaction", method= RequestMethod.POST)
	public String uploadToTransaction(@RequestParam("excelId")Integer excelId){
		log.warn("ExcelController.uploadToTransaction: " + excelId);
		transactionService.createByExcel(excelId);
		JSONObject rs = new JSONObject();
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/uploadToCarrierBill", method= RequestMethod.POST)
	public String uploadToCarrierBill(@RequestParam("excelId")Integer excelId){
		log.warn("ExcelController.uploadToCarrierBill: " + excelId);
		shipmentService.updateCarrierBillByExcel(excelId);
		overseaService.updateCarrierBillByExcel(excelId);
		JSONObject rs = new JSONObject();
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/uploadToShipment", method= RequestMethod.POST)
	public String uploadToShipment(@RequestBody UploadFbaDTO uploadFbaDTO){
		log.warn("ExcelController.uploadToShipment");
		log.warn(new JSONObject(uploadFbaDTO).toString());
		shipmentService.createByExcel(uploadFbaDTO);
		JSONObject rs = new JSONObject();
		rs.put("error", false);
		return rs.toString();
	}

	private Consumer<List<ExcelSupplierDeliveryOrderEO>> batchInsertExcelSupplierDeliveryOrder(Integer excelId){
		return supplierDeliveryOrderEOList -> excelService.insertExcelSupplierDeliveryOrder(excelId, supplierDeliveryOrderEOList);
	}

	private Consumer<List<ExcelSupplierDeliveryOrderDetailEO>> batchInsertExcelSupplierDeliveryOrderDetail(Integer excelId){
		return supplierDeliveryOrderDetailEOList -> excelService.insertExcelSupplierDeliveryOrderDetail(excelId, supplierDeliveryOrderDetailEOList);
	}

	private Consumer<List<ExcelFbaRowEO>> batchInsertExcelFbaPackList(Integer excelId){
		return fbaPackListEoList -> excelService.insertFbaPackList(excelId, fbaPackListEoList);
	}

	private Consumer<List<ExcelFbatsvRowEO>> batchInsertExcelFbatsvPackList(Integer excelId){
		return fbatsvPackListEoList -> excelService.insertFbatsvPackList(excelId, fbatsvPackListEoList);
	}

	private Consumer<List<ExcelTransactionRowEO>> batchInsertTransactionRow(Integer excelId){
		return excelTransactionRowEOList -> excelService.insertTransactionRow(excelId, excelTransactionRowEOList);
	}

	private Consumer<List<ExcelCarrierBillCainiaoRowEO>> batchInsertCarrierBillRow(Integer excelId){
		return excelCarrierBillCainiaoRowEOList -> excelService.insertCarrierBillRow(excelId, excelCarrierBillCainiaoRowEOList);
	}

	public String convertTsvToXlsx(String tsvFileName){
		LoadOptions opts = new LoadOptions(LoadFormat.TSV);
		String timeStamp = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
		String tempExcelName = "temp-" + timeStamp + "-" + tsvFileName + ".xlsx";
		try{
			com.aspose.cells.Workbook wb = new Workbook(uploadFileFolder + tsvFileName, opts);
			wb.save(uploadFileFolder + tempExcelName, SaveFormat.XLSX);
			return tempExcelName;
		}catch (Exception e){
			log.error(e.getMessage());
			throw new SimpleCommonException("tsv转换xlsx失败");
		}
	}

	public String convertCsvToXlsx(String tsvFileName){
		LoadOptions opts = new LoadOptions(LoadFormat.CSV);
		String timeStamp = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
		String tempExcelName = "temp-" + timeStamp + "-" + tsvFileName + ".xlsx";
		try{
			com.aspose.cells.Workbook wb = new Workbook(uploadFileFolder + tsvFileName, opts);
			wb.save(uploadFileFolder + tempExcelName, SaveFormat.XLSX);
			return tempExcelName;
		}catch (Exception e){
			log.error(e.getMessage());
			throw new SimpleCommonException("csv转换xlsx失败");
		}
	}

	private String saveFile(MultipartFile multipartFile){
		if (multipartFile.isEmpty()) {
			throw new SimpleCommonException("上传失败，请选择文件");
		}
		String timeStamp = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
		String fileName = multipartFile.getOriginalFilename();
		String uploadFileName = timeStamp + "-" + fileName;
		File dest = new File(uploadFileFolder + uploadFileName);
		try {
			multipartFile.transferTo(dest);
			log.info("上传成功: " + uploadFileName);
			return uploadFileName;
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new SimpleCommonException("上传失败，未知");
		}
	}
}

