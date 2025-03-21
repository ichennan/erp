package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.dto.PacketDTO;
import com.fiveamazon.erp.dto.PacketDetailDTO;
import com.fiveamazon.erp.entity.PacketDetailPO;
import com.fiveamazon.erp.entity.PacketPO;
import com.fiveamazon.erp.service.PacketService;
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
@RequestMapping(value = "/packet")
@Slf4j
public class PacketController extends SimpleCommonController {
    @Autowired
    PacketService packetService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showView() {
        this.parameters.put("pageName", "packet");
        ModelAndView mv = new ModelAndView("packet", parameters);
        return mv;
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.POST)
    public String findAll() {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        List<PacketPO> packetPOS = packetService.findAll();
        for (PacketPO packetPO : packetPOS) {
            array.put(packetPO.toJson());
        }
        rs.put("array", array);
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/findAllDetail", method = RequestMethod.POST)
    public String findAllDetail(Integer packetId) {
        JSONObject rs = new JSONObject();
        JSONArray array = new JSONArray();
        List<PacketDetailPO> packetDetailPOS = packetService.findAllDetail(packetId);
        for (PacketDetailPO packetDetailPO : packetDetailPOS) {
            array.put(packetDetailPO.toJson());
        }
        rs.put("array", array);
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/getDetail", method = RequestMethod.POST)
    public String getDetail(@RequestParam("id") Integer id) {
        JSONObject rs = new JSONObject();
        JSONObject data = new JSONObject();
        PacketPO packetPO = packetService.getById(id);
        data = packetPO.toJson();
        JSONArray array = new JSONArray();
        List<PacketDetailPO> packetDetailPOS = packetService.findAllDetail(id);
        for (PacketDetailPO packetDetailPO : packetDetailPOS) {
            array.put(packetDetailPO.toJson());
        }
        data.put("array", array);
        rs.put("data", data);
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/saveDetail", method = RequestMethod.POST)
    public String saveDetail(@RequestBody PacketDTO packetDTO) {
        packetDTO.setUsername(getUsername());
        JSONObject rs = new JSONObject();
        PacketPO packetPO = packetService.save(packetDTO);
        rs.put("data", packetPO.toJson());
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/getDetailListDetail", method = RequestMethod.POST)
    public String getDetailListDetail(@RequestParam("id") Integer id) {
        JSONObject rs = new JSONObject();
        PacketDetailPO packetDetailPO = packetService.getDetailById(id);
        rs.put("data", packetDetailPO.toJson());
        rs.put("error", false);
        return rs.toString();
    }

    @RequestMapping(value = "/saveDetailListDetail", method = RequestMethod.POST)
    public String saveDetailListDetail(PacketDetailDTO packetDetailDTO) {
        packetDetailDTO.setUsername(getUsername());
        JSONObject rs = new JSONObject();
        PacketDetailPO packetDetailPO = packetService.saveDetail(packetDetailDTO);
        rs.put("data", packetDetailPO.toJson());
        rs.put("error", false);
        return rs.toString();
    }
}

