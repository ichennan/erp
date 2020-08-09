package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.common.SimpleCommonException;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.entity.ProductPO;
import com.fiveamazon.erp.entity.SkuInfoPO;
import com.fiveamazon.erp.entity.SkuViewPO;
import com.fiveamazon.erp.entity.StorePO;
import com.fiveamazon.erp.service.ProductService;
import com.fiveamazon.erp.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.stream.FileImageInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	SkuInfoService skuInfoService;

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
		List<SkuViewPO> skuViewPOList = skuInfoService.findAll();
		for(SkuViewPO skuViewPO: skuViewPOList){
			array.put(skuViewPO.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}
}

