package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.OverseaDTO;
import com.fiveamazon.erp.dto.OverseaDetailDTO;
import com.fiveamazon.erp.entity.OverseaDetailPO;
import com.fiveamazon.erp.entity.OverseaPO;
import com.fiveamazon.erp.entity.OverseaViewPO;
import com.fiveamazon.erp.service.OverseaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@RestController
@RequestMapping(value = "/oversea")
@Slf4j
public class OverseaController extends SimpleCommonController {
	@Autowired
	OverseaService theService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showView() {
		this.parameters.put("pageName", "oversea");
		ModelAndView mv = new ModelAndView("oversea", parameters);
		return mv;
	}

	@RequestMapping(value = "/findList", method= RequestMethod.POST)
	public String findAll(){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<OverseaViewPO> list = theService.findAll();
		for(OverseaViewPO item: list){
			array.put(item.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

//	@RequestMapping(value = "/findAllDetail", method= RequestMethod.POST)
//	public String findAllDetail(Integer shipmentId){
//		JSONObject rs = new JSONObject();
//		JSONArray array = new JSONArray();
//		List<ShipmentDetailPO> shipmentDetailPOS = shipmentService.findAllDetail(shipmentId);
//		for(ShipmentDetailPO shipmentDetailPO: shipmentDetailPOS){
//			array.put(shipmentDetailPO.toJson());
//		}
//		rs.put("array", array);
//		rs.put("error", false);
//		return rs.toString();
//	}

	@RequestMapping(value = "/getItem", method= RequestMethod.POST)
	public String getDetail(@RequestParam("id")Integer id){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		OverseaPO item = theService.getById(id);
		List<OverseaDetailPO> list = theService.findAllDetail(id);
		for(OverseaDetailPO obj : list){
			array.add(obj.toJson());
		}
		rs.put("data", item.toJson());
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/saveItem", method= RequestMethod.POST)
	public String saveDetail(@RequestBody OverseaDTO dto){
		dto.setUsername(getUsername());
		JSONObject rs = new JSONObject();
		OverseaPO item = theService.save(dto);
		rs.put("data", item == null ? null : item.toJson());
		rs.put("error", false);
		return rs.toString();
	}
//
//	@RequestMapping(value = "/getDetailListDetail", method= RequestMethod.POST)
//	public String getDetailListDetail(@RequestParam("id")Integer id){
//		JSONObject rs = new JSONObject();
//		ShipmentDetailPO shipmentDetailPO = shipmentService.getDetailById(id);
//		rs.put("data", shipmentDetailPO.toJson());
//		rs.put("error", false);
//		return rs.toString();
//	}
//
	@RequestMapping(value = "/saveDetail", method= RequestMethod.POST)
	public String saveDetailListDetail(OverseaDetailDTO dto){
		dto.setUsername(getUsername());
		JSONObject rs = new JSONObject();
		OverseaDetailPO item = theService.saveDetail(dto, true);
		rs.put("data", item == null ? null : item.toJson());
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/batchInsert", method= RequestMethod.POST)
	public String batchInsert(@RequestBody String abc){
		log.info("batchInsert");
		log.info(abc);
		JSONObject rs = new JSONObject();
		JSONObject json = new JSONObject(abc);
		JSONObject itemJson = json.getJSONObject("item");
		OverseaDTO dto = JSONUtil.toBean(itemJson, OverseaDTO.class);
		dto.setUsername(getUsername());
		OverseaPO item = theService.save(dto);
		Integer overseaId = item.getId();
		JSONArray jsonArray = json.getJSONArray("array");
		for(JSONObject detailJson : jsonArray.jsonIter()){
			OverseaDetailDTO detailDTO = JSONUtil.toBean(detailJson, OverseaDetailDTO.class);
			detailDTO.setUsername(getUsername());
			detailDTO.setOverseaId(overseaId);
			theService.saveDetail(detailDTO, false);
		}
		rs.put("data", new JSONObject());
		rs.put("error", false);
		return rs.toString();
	}

//	//
//
//	@RequestMapping(value = "/findAllProducts", method= RequestMethod.POST)
//	public String findAllProducts(ShipmentProductSearchDTO searchDTO){
//		JSONObject rs = new JSONObject();
//		JSONArray array = new JSONArray();
//		List<ShipmentProductViewPO> list = shipmentService.findAllProducts(searchDTO);
//		for(ShipmentProductViewPO item: list){
//			array.put(item.toJson());
//		}
//		rs.put("array", array);
//		rs.put("error", false);
//		return rs.toString();
//	}
}

