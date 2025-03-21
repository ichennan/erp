package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.FbaTrackingBatchCreateDTO;
import com.fiveamazon.erp.dto.FbaTrackingBatchUpdateDTO;
import com.fiveamazon.erp.entity.FbaTrackingPO;
import com.fiveamazon.erp.service.FbaTrackingService;
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
@RequestMapping(value = "/fbaTracking")
@Slf4j
public class FbaTrackingController extends SimpleCommonController {
    @Autowired
    FbaTrackingService theService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showView() {
        this.parameters.put("pageName", "fbaTracking");
        ModelAndView mv = new ModelAndView("fbaTracking", parameters);
        return mv;
    }

    @RequestMapping(value = "/findList", method = RequestMethod.POST)
    public String findList() {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        List<FbaTrackingPO> list = theService.findAll();
        for (FbaTrackingPO item : list) {
            array.put(item.toJson());
        }
        rs.put("array", array);
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/getItem", method = RequestMethod.POST)
    public String getItem(@RequestParam("id") Integer id) {
        JSONObject rs = new JSONObject();
        FbaTrackingPO item = theService.getById(id);
        rs.put("data", item.toJson());
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/batchCreate", method = RequestMethod.POST)
    public String batchCreate(@RequestBody FbaTrackingBatchCreateDTO dto) {
        log.info("FbaTrackingController.batchCreate [{}]", dto);
        dto.setUsername(getUsername());
        JSONObject rs = new JSONObject();
        theService.batchCreate(dto);
        rs.put("data", new JSONObject());
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/batchUpdate", method = RequestMethod.POST)
    public String batchUpdate(@RequestBody FbaTrackingBatchUpdateDTO dto) {
        log.info("FbaTrackingController.batchUpdate [{}]", dto);
        dto.setUsername(getUsername());
        JSONObject rs = new JSONObject();
        theService.batchUpdate(dto);
        rs.put("data", new JSONObject());
        rs.put("error", false);
        return rs.toString();
    }

//	@RequestMapping(value = "/batchInsert", method= RequestMethod.POST)
//	public String batchInsert(@RequestBody String abc){
//		log.info("batchInsert");
//		JSONObject rs = new JSONObject();
//		JSONObject json = new JSONObject(abc);
//		JSONObject itemJson = json.getJSONObject("item");
//		OverseaDTO dto = JSONUtil.toBean(itemJson, OverseaDTO.class);
//		dto.setUsername(getUsername());
//		OverseaPO item = theService.save(dto);
//		Integer overseaId = item.getId();
//		JSONArray jsonArray = json.getJSONArray("array");
//		for(JSONObject detailJson : jsonArray.jsonIter()){
//			OverseaDetailDTO detailDTO = JSONUtil.toBean(detailJson, OverseaDetailDTO.class);
//			detailDTO.setUsername(getUsername());
//			detailDTO.setOverseaId(overseaId);
//			theService.saveDetail(detailDTO, false);
//		}
//		rs.put("data", new JSONObject());
//		rs.put("error", false);
//		return rs.toString();
//	}
}

