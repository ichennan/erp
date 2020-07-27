package com.fiveamazon.erp.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.common.SimpleCommonException;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.entity.ProductPO;
import com.fiveamazon.erp.service.ProductService;
import com.fiveamazon.erp.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping(value = "/product")
@Slf4j
public class ProductController extends SimpleCommonController {
	@Autowired
	ProductService productService;
	@Autowired
	SkuService skuService;

	@Value("${simple.folder.image.product}")
	private String productImageFolder;


	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showView() {
		this.parameters.put("pageName", "product");
		ModelAndView mv = new ModelAndView("product", parameters);
		return mv;
	}

	@RequestMapping(value = "/findAll", method= RequestMethod.POST)
	public String findAll(){
		JSONObject rs = new JSONObject();
		JSONArray array = new JSONArray();
		List<ProductPO> productPOS = productService.findAll("name");
		for(ProductPO productPO: productPOS){
			array.put(productPO.toJson());
		}
		rs.put("array", array);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/refreshCacheProducts", method= RequestMethod.POST)
	public String refreshCacheProducts(){
		JSONObject rs = new JSONObject();
		List<ProductPO> productPOS = productService.findAll("name");
		for(ProductPO productPO: productPOS){
			rs.put("id" + productPO.getId(), productPO.toJson());
//			rs.put("id" + productPO.getId(), (StringUtils.isEmpty(productPO.getSn()) ? "" : productPO.getSn() + "... ") + productPO.getName());
		}
		return rs.toString();
	}

	@RequestMapping(value = "/refreshCacheEnablePacketProducts", method= RequestMethod.POST)
	public String refreshCacheEnablePacketProducts(){
		JSONObject rs = new JSONObject();
		List<ProductPO> productPOS = productService.findEnablePacket();
		for(ProductPO productPO: productPOS){
			rs.put("id" + productPO.getId(), (StringUtils.isEmpty(productPO.getSn()) ? "" : productPO.getSn() + "... ") + productPO.getName());
		}
		return rs.toString();
	}

	@RequestMapping(value = "/getDetail", method= RequestMethod.POST)
	public String getDetail(@RequestParam("id")Integer id){
		JSONObject rs = new JSONObject();
		ProductPO productPO = productService.getById(id);
		JSONObject dataJson = productPO.toJson();
		dataJson.putAll(skuService.getByProductId(id));
		rs.put("data", dataJson);
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/saveDetail", method= RequestMethod.POST)
	public String saveDetail(ProductDTO productDTO){
		productDTO.setUsername(getUsername());
		JSONObject rs = new JSONObject();
		ProductPO productPO = productService.save(productDTO);
		rs.put("data", productPO.toJson());
		rs.put("error", false);
		return rs.toString();
	}

	@RequestMapping(value = "/uploadProductImage", method= RequestMethod.POST)
	public String uploadProductImage(MultipartFile file, String uploadImageData) throws IOException {
		JSONObject rs = new JSONObject();
		JSONObject uploadImageDataJson = new JSONObject(uploadImageData);
		String productId = uploadImageDataJson.getStr("productId");
//		if(StringUtils.isEmpty(productId) || "0".equalsIgnoreCase(productId)){
		if(StringUtils.isEmpty(productId)){
			throw new SimpleCommonException("productId is Empty");
		}
		if (file == null){
			throw new SimpleCommonException("File is Empty");
		}

		String path = null;
		String type = null;

		String originalFileName = file.getOriginalFilename();

		System.out.println("Original File Name:" + originalFileName);
		// 判断文件类型
		type = originalFileName.indexOf(".") != -1 ? originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length()) : null;
		if(StringUtils.isEmpty(type)){
			throw new SimpleCommonException("File Type is Empty");
		}
		if (("PNG".equals(type.toUpperCase())) || ("JPG".equals(type.toUpperCase())) || ("JPEG".equals(type.toUpperCase()))){
			String serverFileName = "id" + productId + ".jpg";
			// 设置存放图片文件的路径
			path = productImageFolder +  serverFileName;
			System.out.println("file uploaded in:" + path);
			// 转存文件到指定的路径
			file.transferTo(new File(path));
			System.out.println("file uploaded success");
		}else{
			throw new SimpleCommonException("File Type is Not PNG or JPG or JPEG");
		}

		return rs.toString();
	}

	@RequestMapping(value = "/getProductImage/{fileName}", method= RequestMethod.GET)
	public byte[] getProductImage(@PathVariable String fileName) throws IOException {
		System.out.println(fileName);
		String path = productImageFolder +  fileName;
		byte[] data = null;
		FileImageInputStream input = null;
		try {
			input = new FileImageInputStream(new File(path));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int numBytesRead = 0;
			while ((numBytesRead = input.read(buf)) != -1) {
				output.write(buf, 0, numBytesRead);
			}
			data = output.toByteArray();
			output.close();
			input.close();
		}
		catch (FileNotFoundException ex1) {
			path = productImageFolder +  "empty.png";
			input = new FileImageInputStream(new File(path));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int numBytesRead = 0;
			while ((numBytesRead = input.read(buf)) != -1) {
				output.write(buf, 0, numBytesRead);
			}
			data = output.toByteArray();
			output.close();
			input.close();
		}
		catch (IOException ex1) {
			ex1.printStackTrace();
		}
		return data;
	}

	@RequestMapping(value = "/updatePurchasePrice", method= RequestMethod.POST)
	public String updatePurchasePrice(ProductDTO productDTO){
		productDTO.setUsername(getUsername());
		JSONObject rs = new JSONObject();
		ProductPO productPO = productService.updatePurchasePrice(productDTO);
		rs.put("data", productPO.toJson());
		rs.put("error", false);
		return rs.toString();
	}
}

