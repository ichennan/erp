package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.CommonTable;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.PaymentDTO;
import com.fiveamazon.erp.dto.search.PaymentSearchDTO;
import com.fiveamazon.erp.entity.PaymentPO;
import com.fiveamazon.erp.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "/payment")
@Slf4j
public class PaymentController extends SimpleCommonController {
    @Autowired
    PaymentService theService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showView() {
        this.parameters.put("pageName", "payment");
        ModelAndView mv = new ModelAndView("payment", parameters);
        return mv;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestBody PaymentSearchDTO searchDTO) {
        log.info("PaymentController.search: " + new JSONObject(searchDTO).toString());
        JSONObject rs = new JSONObject();
        CommonTable commonTable = theService.search(searchDTO);
        rs.putAll(commonTable.toJson());
        rs.putOpt("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/getItem", method = RequestMethod.POST)
    public String getItem(@RequestParam("id") Integer id) {
        JSONObject rs = new JSONObject();
        PaymentPO item = theService.getById(id);
        rs.put("data", item == null ? null : item.toJson());
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/saveItem", method = RequestMethod.POST)
    public String saveItem(@RequestBody PaymentDTO dto) {
        dto.setUsername(getUsername());
        JSONObject rs = new JSONObject();
        PaymentPO item = theService.save(dto);
        rs.put("data", item == null ? null : item.toJson());
        rs.put("error", false);
        return rs.toString();
    }
}
