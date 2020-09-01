package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.service.PlanService;
import com.fiveamazon.erp.service.SkuService;
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
@RequestMapping(value = "/plan")
@Slf4j
public class PlanController extends SimpleCommonController {
	@Autowired
	PlanService planService;
	@Autowired
	SkuService skuService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showView() {
		this.parameters.put("pageName", "plan");
		ModelAndView mv = new ModelAndView("plan", parameters);
		return mv;
	}

	@RequestMapping(value = "/findAll", method= RequestMethod.POST)
	public String findAll(){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<PlanPO> planPOList = planService.findAll();
		for(PlanPO planPO: planPOList){
			array.put(planPO.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/getDetail", method= RequestMethod.POST)
	public String getDetail(@RequestParam("id")Integer id){
		JSONObject rs = new JSONObject();
		JSONObject data = new JSONObject();
		PlanPO planPO = planService.getById(id);
		data = planPO.toJson();
		JSONArray array = new JSONArray();
		List<PlanDetailPO> planDetailPOList = planService.findAllDetail(id);
		for(PlanDetailPO planDetailPO: planDetailPOList){
			Integer skuId = planDetailPO.getSkuId();
			SkuInfoVO skuInfoVO = skuService.getViewById(skuId);
			JSONObject planDetailJson = skuInfoVO.toJson();
			planDetailJson.putAll(planDetailPO.toJson());
			array.put(planDetailJson);
		}
		rs.put("array", array);
		rs.put("data", data);
		rs.put("error", false);
		return rs.toString();
	}
}

