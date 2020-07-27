package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.PacketDetailViewDTO;
import com.fiveamazon.erp.dto.PurchaseDetailViewDTO;
import com.fiveamazon.erp.dto.ShipmentDetailViewDTO;
import com.fiveamazon.erp.entity.InventorySnapshotPO;
import com.fiveamazon.erp.entity.InventoryVPO;
import com.fiveamazon.erp.service.InventoryService;
import com.fiveamazon.erp.service.PacketService;
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
@RequestMapping(value = "/inventory")
@Slf4j
public class InventoryController extends SimpleCommonController {
	@Autowired
	InventoryService inventoryService;
	@Autowired
	ShipmentService shipmentService;
	@Autowired
	PurchaseService purchaseService;
	@Autowired
	PacketService packetService;


	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showView() {
		this.parameters.put("pageName", "inventory");
		ModelAndView mv = new ModelAndView("inventory", parameters);
		return mv;
	}

	@RequestMapping(value = "/findAll", method= RequestMethod.POST)
	public String findAll(){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<InventoryVPO> inventoryPOS = inventoryService.findAll();
		for(InventoryVPO inventoryPO: inventoryPOS){
			array.put(inventoryPO.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/getDetail", method= RequestMethod.POST)
	public String getDetail(@RequestParam("id")Integer id){
		JSONObject rs = new JSONObject();
		//
		JSONArray inventoryArray = new JSONArray();
		List<InventorySnapshotPO> inventorySnapshotPOS = inventoryService.findSnapshotByProductId(id);
		for(InventorySnapshotPO inventorySnapshotPO: inventorySnapshotPOS){
			inventoryArray.put(inventorySnapshotPO.toJson());
		}
		rs.put("inventoryArray", inventoryArray);
		//
		JSONArray shipmentArray = new JSONArray();
		List<ShipmentDetailViewDTO> shipmentDetailViewDTOS = shipmentService.findByProductId(id);
		for(ShipmentDetailViewDTO shipmentDetailViewDTO: shipmentDetailViewDTOS){
			shipmentArray.put(shipmentDetailViewDTO.toJson());
		}
		rs.put("shipmentArray", shipmentArray);
		//
		JSONArray purchaseArray = new JSONArray();
		List<PurchaseDetailViewDTO> purchaseDetailViewDTOS = purchaseService.findByProductId(id);
		for(PurchaseDetailViewDTO purchaseDetailViewDTO: purchaseDetailViewDTOS){
			purchaseArray.put(purchaseDetailViewDTO.toJson());
		}
		rs.put("purchaseArray", purchaseArray);
		//
		JSONArray packetArray = new JSONArray();
		List<PacketDetailViewDTO> packetDetailViewDTOS = packetService.findByProductId(id);
		for(PacketDetailViewDTO packetDetailViewDTO: packetDetailViewDTOS){
			packetArray.put(packetDetailViewDTO.toJson());
		}
		rs.put("packetArray", packetArray);
		//







		rs.put("error", false);
		return rs.toString();
	}
}

