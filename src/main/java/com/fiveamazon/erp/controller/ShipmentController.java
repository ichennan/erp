package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.ShipmentDTO;
import com.fiveamazon.erp.dto.ShipmentDetailDTO;
import com.fiveamazon.erp.entity.ShipmentDetailPO;
import com.fiveamazon.erp.entity.ShipmentPO;
import com.fiveamazon.erp.entity.ShipmentViewPO;
import com.fiveamazon.erp.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping(value = "/shipment")
@Slf4j
public class ShipmentController extends SimpleCommonController {
	@Autowired
	ShipmentService shipmentService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showView() {
		this.parameters.put("pageName", "shipment");
		ModelAndView mv = new ModelAndView("shipment", parameters);
		return mv;
	}

	@RequestMapping(value = "/findAll", method= RequestMethod.POST)
	public String findAll(){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<ShipmentViewPO> shipmentViewPOS = shipmentService.findAll();
		for(ShipmentViewPO shipmentViewPO: shipmentViewPOS){
			array.put(shipmentViewPO.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/findAllDetail", method= RequestMethod.POST)
	public String findAllDetail(Integer shipmentId){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<ShipmentDetailPO> shipmentDetailPOS = shipmentService.findAllDetail(shipmentId);
		for(ShipmentDetailPO shipmentDetailPO: shipmentDetailPOS){
			array.put(shipmentDetailPO.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/getDetail", method= RequestMethod.POST)
	public String getDetail(@RequestParam("id")Integer id){
		JSONObject rs = new JSONObject();
		ShipmentPO shipmentPO = shipmentService.getById(id);
		rs.put("data", shipmentPO.toJson());
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/saveDetail", method= RequestMethod.POST)
	public String saveDetail(@RequestBody ShipmentDTO shipmentDTO){
		shipmentDTO.setUsername(getUsername());
		JSONObject rs = new JSONObject();
		ShipmentPO shipmentPO = shipmentService.save(shipmentDTO);
		rs.put("data", shipmentPO.toJson());
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/getDetailListDetail", method= RequestMethod.POST)
	public String getDetailListDetail(@RequestParam("id")Integer id){
		JSONObject rs = new JSONObject();
		ShipmentDetailPO shipmentDetailPO = shipmentService.getDetailById(id);
		rs.put("data", shipmentDetailPO.toJson());
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/saveDetailListDetail", method= RequestMethod.POST)
	public String saveDetailListDetail(ShipmentDetailDTO shipmentDetailDTO){
		shipmentDetailDTO.setUsername(getUsername());
		JSONObject rs = new JSONObject();
		ShipmentDetailPO shipmentDetailPO = shipmentService.saveDetail(shipmentDetailDTO);
		rs.put("data", shipmentDetailPO == null ? null : shipmentDetailPO.toJson());
		rs.put("error", false);
		return rs.toString();
	}
}

