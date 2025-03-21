package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.StocktakingDTO;
import com.fiveamazon.erp.dto.StocktakingDetailDTO;
import com.fiveamazon.erp.entity.StocktakingDetailPO;
import com.fiveamazon.erp.entity.StocktakingPO;
import com.fiveamazon.erp.entity.StocktakingViewPO;
import com.fiveamazon.erp.service.StocktakingService;
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
@RequestMapping(value = "/stocktaking")
@Slf4j
public class StocktakingController extends SimpleCommonController {
    @Autowired
    StocktakingService theService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showView() {
        this.parameters.put("pageName", "stocktaking");
        ModelAndView mv = new ModelAndView("stocktaking", parameters);
        return mv;
    }

    @RequestMapping(value = "/findList", method = RequestMethod.POST)
    public String findList() {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        List<StocktakingViewPO> list = theService.findAll();
        for (StocktakingViewPO item : list) {
            array.put(item.toJson());
        }
        rs.put("array", array);
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/getItem", method = RequestMethod.POST)
    public String getItem(@RequestParam("id") Integer id) {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        StocktakingPO item = theService.getById(id);
        List<StocktakingDetailPO> list = theService.findAllDetail(id);
        for (StocktakingDetailPO obj : list) {
            array.add(obj.toJson());
        }
        rs.put("data", item.toJson());
        rs.put("array", array);
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/saveItem", method = RequestMethod.POST)
    public String saveItem(@RequestBody StocktakingDTO dto) {
        dto.setUsername(getUsername());
        JSONObject rs = new JSONObject();
        StocktakingPO item = theService.save(dto);
        rs.put("data", item == null ? null : item.toJson());
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/saveDetail", method = RequestMethod.POST)
    public String saveDetail(StocktakingDetailDTO dto) {
        dto.setUsername(getUsername());
        JSONObject rs = new JSONObject();
        StocktakingDetailPO item = theService.saveDetail(dto);
        rs.put("data", item == null ? null : item.toJson());
        rs.put("error", false);
        return rs.toString();
    }
}

