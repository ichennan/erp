package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.CommonTable;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.search.TransactionSearchDTO;
import com.fiveamazon.erp.entity.TransactionPO;
import com.fiveamazon.erp.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@RestController
@RequestMapping(value = "/transaction")
@Slf4j
public class TransactionController extends SimpleCommonController {
    @Autowired
    TransactionService theService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showView() {
        this.parameters.put("pageName", "transaction");
        ModelAndView mv = new ModelAndView("transaction", parameters);
        return mv;
    }

//	@RequestMapping(value = "/findList", method= RequestMethod.POST)
//	public String findList(){
//		JSONObject rs = new JSONObject();
//		JSONArray array = new JSONArray();
//		List<TransactionPO> list = theService.findAll();
//		for(TransactionPO item: list){
//			array.put(item.toJson());
//		}
//		rs.put("array", array);
//		rs.put("error", false);
//		return rs.toString();
//	}

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestBody TransactionSearchDTO searchDTO) {
        log.info("TransactionController.search: " + new JSONObject(searchDTO).toString());
        JSONObject rs = new JSONObject();
        CommonTable commonTable = theService.search(searchDTO);
        rs.putAll(commonTable.toJson());
        rs.putOpt("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/getItem", method = RequestMethod.POST)
    public String getItem(@RequestParam("id") Integer id) {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        TransactionPO item = theService.getById(id);
        rs.put("data", item.toJson());
        rs.put("error", false);
        return rs.toString();
    }

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

