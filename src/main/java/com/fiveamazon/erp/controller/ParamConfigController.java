package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.ParamConfigDTO;
import com.fiveamazon.erp.entity.ParamConfigPO;
import com.fiveamazon.erp.service.ParamConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@RestController
@RequestMapping(value = "/paramConfig")
@Slf4j
public class ParamConfigController extends SimpleCommonController {
    @Autowired
    ParamConfigService theService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showView() {
        this.parameters.put("pageName", "paramConfig");
        ModelAndView mv = new ModelAndView("paramConfig", parameters);
        return mv;
    }

    @RequestMapping(value = "/findList", method = RequestMethod.POST)
    public String findList() {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        List<ParamConfigPO> list = theService.findAll();
        for (ParamConfigPO item : list) {
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
        ParamConfigPO item = theService.getById(id);
        rs.put("data", item.toJson());
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/saveItem", method = RequestMethod.POST)
    public String saveItem(@RequestBody ParamConfigDTO dto) {
        dto.setUsername(getUsername());
        JSONObject rs = new JSONObject();
        ParamConfigPO item = theService.save(dto);
        rs.put("data", item == null ? null : item.toJson());
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/findListByParamCategory", method = RequestMethod.POST)
    public String findListByParamCategory(@RequestParam("paramCategory") String paramCategory) {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        List<String> list = theService.findListByCategory(paramCategory);
        for (String item : list) {
            if (StringUtils.isNotEmpty(item)) {
                array.put(item);
            }
        }
        rs.put("array", array);
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/findAutoCompleteParamCategory", method = RequestMethod.POST)
    public String findAutoCompleteSupplier() {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        List<String> list = theService.findParamCategoryList();
        for (String item : list) {
            if (StringUtils.isNotEmpty(item)) {
                array.put(item);
            }
        }
        rs.put("array", array);
        rs.put("error", false);
        return rs.toString();
    }
}

