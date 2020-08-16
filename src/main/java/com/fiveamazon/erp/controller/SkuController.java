package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

	@Value("${simple.folder.image.product}")
	private String productImageFolder;


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
//		JSONArray inventoryArray = new JSONArray();
////		List<SnapshotSkuInventoryPO> snapshotSkuInventoryPOList = skuService.findSnapshotBySkuId(id);
////		for(SnapshotSkuInventoryPO snapshotSkuInventoryPO: snapshotSkuInventoryPOList){
////			inventoryArray.put(snapshotSkuInventoryPO.toJson());
////		}
//		rs.put("inventoryArray", inventoryArray);
//		//
////		JSONArray shipmentArray = new JSONArray();
////		List<ShipmentDetailViewDTO> shipmentDetailViewDTOS = shipmentService.findByProductId(productId);
////		for(ShipmentDetailViewDTO shipmentDetailViewDTO: shipmentDetailViewDTOS){
////			shipmentArray.put(shipmentDetailViewDTO.toJson());
////		}
////		rs.put("shipmentArray", shipmentArray);
//		//
//		JSONArray purchaseArray = new JSONArray();
//		List<PurchaseDetailViewDTO> purchaseDetailViewDTOS = purchaseService.findByProductId(productId);
//		for(PurchaseDetailViewDTO purchaseDetailViewDTO: purchaseDetailViewDTOS){
//			purchaseArray.put(purchaseDetailViewDTO.toJson());
//		}
//		rs.put("purchaseArray", purchaseArray);
//		//
//		JSONArray packetArray = new JSONArray();
////		List<PacketDetailViewDTO> packetDetailViewDTOS = packetService.findByProductId(productId);
////		for(PacketDetailViewDTO packetDetailViewDTO: packetDetailViewDTOS){
////			packetArray.put(packetDetailViewDTO.toJson());
////		}
//		rs.put("packetArray", packetArray);
		//
		rs.put("error", false);
		return rs.toString();
	}
}

