package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.PurchaseDTO;
import com.fiveamazon.erp.dto.PurchaseDetailDTO;
import com.fiveamazon.erp.dto.PurchaseProductSearchDTO;
import com.fiveamazon.erp.dto.ShipmentDetailDTO;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.service.ExcelService;
import com.fiveamazon.erp.service.PurchaseService;
import com.fiveamazon.erp.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
	public String findAll(){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<PurchaseViewPO> purchaseViewPOS = purchaseService.findAll();
		for(PurchaseViewPO purchaseViewPO: purchaseViewPOS){
			array.put(purchaseViewPO.toJson());
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

	//

	@RequestMapping(value = "/findAllProducts", method= RequestMethod.POST)
	public String findAllProducts(PurchaseProductSearchDTO purchaseProductSearchDTO){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<PurchaseDetailPO> list = purchaseService.findAllProducts(purchaseProductSearchDTO);
		for(PurchaseDetailPO item: list){
			array.put(item.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}
}

