package com.fiveamazon.erp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.entity.ProductPO;
import com.fiveamazon.erp.entity.StorePO;
import com.fiveamazon.erp.repository.ProductRepository;
import com.fiveamazon.erp.repository.StoreRepository;
import com.fiveamazon.erp.service.ProductService;
import com.fiveamazon.erp.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StoreRepository storeRepository;
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
        if(null == productPO.getPurchasePrice()){
            productPO.setPurchasePrice(new BigDecimal(0));
        }
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
            BeanUtil.copyProperties(productDTO, productPO,  "id");
            productPO.setCreateDate(new Date());
            productPO.setCreateUser(productDTO.getUsername());
            productPO = save(productPO);
        }else{
            productPO = getById(productId);
            BeanUtils.copyProperties(productDTO, productPO, "id");
            productPO.setUpdateDate(new Date());
            productPO.setUpdateUser(productDTO.getUsername());
            productPO = save(productPO);
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

    @Override
    public List<StorePO> findAllStore() {
        return storeRepository.findAll();
    }

    @Override
    public StorePO getStoreById(Integer storeId) {
        return storeRepository.getById(storeId);
    }

    @Override
    public void updatePurchasePrice(Integer productId, BigDecimal purchasePrice){
        log.info("ProductServiceImpl.updatePurchasePrice: productId: " + productId + " purchasePrice: " + purchasePrice);
        if(purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0){
            return;
        }
        ProductPO productPO = getById(productId);
        if(productPO != null){
            if(productPO.getPurchasePrice().compareTo(purchasePrice) != 0){
                productPO.setPurchasePrice(purchasePrice);
                save(productPO);
            }
        }
    }
}
