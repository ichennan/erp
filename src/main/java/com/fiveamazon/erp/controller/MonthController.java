package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.entity.MonthPO;
import com.fiveamazon.erp.service.MonthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@RestController
@RequestMapping(value = "/month")
@Slf4j
public class MonthController extends SimpleCommonController {
    @Autowired
    MonthService theService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showView() {
        this.parameters.put("pageName", "month");
        ModelAndView mv = new ModelAndView("month", parameters);
        return mv;
    }

    @RequestMapping(value = "/findList", method = RequestMethod.POST)
    public String findList() {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        Sort sort = Sort.by("storeId");
        List<MonthPO> list = theService.findAll(sort);
        for (MonthPO item : list) {
            array.put(item.toJson());
        }
        rs.put("array", array);
        rs.put("error", false);
        return rs.toString();
    }

//	@RequestMapping(value = "/search", method= RequestMethod.POST)
//	public String search(@RequestBody TransactionSearchDTO searchDTO){
//		log.info("TransactionController.search: " + new JSONObject(searchDTO).toString());
//		JSONObject rs = new JSONObject();
//		CommonTable commonTable = theService.search(searchDTO);
//		rs.putAll(commonTable.toJson());
//		rs.putOpt("error", false);
//		return rs.toString();
//	}

    @RequestMapping(value = "/getItem", method = RequestMethod.POST)
    public String getItem(@RequestParam("id") Integer id) {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        MonthPO item = theService.getById(id);
        rs.put("data", item.toJson());
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public String generate(@RequestParam("year") Integer year
            , @RequestParam("monthStart") Integer monthStart
            , @RequestParam("monthEnd") Integer monthEnd
            , @RequestParam("storeIds") String storeIds) {
        log.info("MonthController.genereate(): [{}] [{}] [{}] [{}]", year, monthStart, monthEnd, storeIds);
        JSONObject rs = new JSONObject();
        String[] storeIdStrList = storeIds.split(",");
        List<Integer> storeIdList = new ArrayList<>();
        for (String storeIdStr : storeIdStrList) {
            try {
                Integer storeId = Integer.valueOf(storeIdStr);
                storeIdList.add(storeId);
            } catch (Exception e) {
            }
        }
        theService.generate(year, monthStart, monthEnd, storeIdList);
        JSONObject data = new JSONObject();
        rs.put("data", data);
        rs.put("error", false);
        return rs.toString();
    }

//	@RequestMapping(value = "/autoCreate", method= RequestMethod.POST)
//	public String generate(){
//		log.info("MonthController.autoCreate()");
//		JSONObject rs = new JSONObject();
//		theService.autoCreate();
//		JSONObject data = new JSONObject();
//		rs.put("data", data);
//		rs.put("error", false);
//		return rs.toString();
//	}

//	@RequestMapping(value = "/saveItem", method= RequestMethod.POST)
//	public String saveItem(@RequestBody OverseaDTO dto){
//		dto.setUsername(getUsername());
//		JSONObject rs = new JSONObject();
//		OverseaPO item = theService.save(dto);
//		rs.put("data", item == null ? null : item.toJson());
//		rs.put("error", false);
//		return rs.toString();
//	}
}

