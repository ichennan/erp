package com.fiveamazon.erp.service.impl;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.entity.ProductPO;
import com.fiveamazon.erp.repository.ProductRepository;
import com.fiveamazon.erp.service.ProductService;
import com.fiveamazon.erp.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SkuService skuService;

    @Override
    public Long countAll() {
        return productRepository.count();
    }

    @Override
    public ProductPO getById(Integer id) {
        return productRepository.getOne(id);
    }

    @Override
    public ProductPO getByName(String name){
        return productRepository.getByName(name);
    }

    @Override
    public List<ProductPO> findAll(String sort) {
        return productRepository.findAll(Sort.by(sort));
    }

    @Override
    public List<ProductPO> findEnablePacket() {
        return productRepository.findByEnablePacketSeqGreaterThanOrderByEnablePacketSeq(0);
    }

    @Override
    public ProductPO save(ProductPO productPO) {
        return productRepository.save(productPO);
    }

    @Override
    public ProductPO save(ProductDTO productDTO) {
        log.warn("ProductServiceImpl.save");
        log.warn(new JSONObject(productDTO).toString());
        Integer productId = productDTO.getId();
        ProductPO productPO;
        if(productId == null || productId == 0){
            productPO = new ProductPO();
            productPO.setCreateDate(new Date());
            productPO.setCreateUser(productDTO.getUsername());
            BeanUtils.copyProperties(productDTO, productPO, "id");
            productPO = save(productPO);
            productDTO.setId(productPO.getId());
            skuService.save(productDTO);
        }else{
            productPO = getById(productId);
            if("updateSku".equalsIgnoreCase(productDTO.getAction())){
                skuService.save(productDTO);
            }else{
                productPO.setUpdateDate(new Date());
                productPO.setUpdateUser(productDTO.getUsername());
                BeanUtils.copyProperties(productDTO, productPO, "id");
                productPO = save(productPO);
            }
        }
        return productPO;

    }

    @Override
    public ProductPO updatePurchasePrice(ProductDTO productDTO) {
        ProductPO productPO = getById(productDTO.getId());
        productPO.setPurchasePrice(productDTO.getPurchasePrice());
        productPO.setUpdateDate(new Date());
        productPO.setUpdateUser(productDTO.getUsername());
        return save(productPO);
    }
}
