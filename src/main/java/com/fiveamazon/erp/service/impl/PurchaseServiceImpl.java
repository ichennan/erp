package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.PurchaseDTO;
import com.fiveamazon.erp.dto.PurchaseDetailDTO;
import com.fiveamazon.erp.dto.PurchaseDetailViewDTO;
import com.fiveamazon.erp.dto.UploadSupplierDeliveryDTO;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.repository.PurchaseDetailRepository;
import com.fiveamazon.erp.repository.PurchaseRepository;
import com.fiveamazon.erp.repository.PurchaseViewRepository;
import com.fiveamazon.erp.service.PurchaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PurchaseViewRepository purchaseViewRepository;
    @Autowired
    private PurchaseDetailRepository purchaseDetailRepository;

    @Override
    public Long countAll() {
        return purchaseRepository.count();
    }

    @Override
    public PurchasePO getById(Integer id) {
        return purchaseRepository.getOne(id);
    }

    @Override
    public PurchaseDetailPO getDetailById(Integer id) {
        return purchaseDetailRepository.getOne(id);
    }

    @Override
    public List<PurchaseViewPO> findAll() {
        return purchaseViewRepository.findAll();
    }

    @Override
    public PurchasePO save(PurchasePO purchasePO) {
        return purchaseRepository.save(purchasePO);
    }

    @Override
    public PurchasePO save(PurchaseDTO purchaseDTO) {
        Integer purchaseId = purchaseDTO.getId();
        if(SimpleConstant.ACTION_DELETE.equalsIgnoreCase(purchaseDTO.getAction())){
            purchaseDetailRepository.deleteByPurchaseId(purchaseId);
            purchaseRepository.deleteById(purchaseId);
            return new PurchasePO();
        }
        PurchasePO purchasePO;
        if(purchaseId == null || purchaseId == 0){
            purchasePO = new PurchasePO();
            purchasePO.setCreateDate(new Date());
            purchasePO.setCreateUser(purchaseDTO.getUsername());
        }else{
            purchasePO = getById(purchaseId);
            purchasePO.setUpdateDate(new Date());
            purchasePO.setUpdateUser(purchaseDTO.getUsername());
        }
        BeanUtils.copyProperties(purchaseDTO, purchasePO, "id");
        return save(purchasePO);
    }

    @Override
    public PurchaseDetailPO saveDetail(PurchaseDetailPO purchaseDetailPO) {
        return purchaseDetailRepository.save(purchaseDetailPO);
    }

    @Override
    public PurchaseDetailPO saveDetail(PurchaseDetailDTO purchaseDetailDTO) {
        Integer purchaseDetailId = purchaseDetailDTO.getId();
        if(SimpleConstant.ACTION_DELETE.equalsIgnoreCase(purchaseDetailDTO.getAction())){
            purchaseDetailRepository.deleteById(purchaseDetailId);
            return new PurchaseDetailPO();
        }
        PurchaseDetailPO purchaseDetailPO;
        if(purchaseDetailId == null || purchaseDetailId == 0){
            purchaseDetailPO = new PurchaseDetailPO();
            purchaseDetailPO.setCreateDate(new Date());
            purchaseDetailPO.setCreateUser(purchaseDetailDTO.getUsername());
        }else{
            purchaseDetailPO = getDetailById(purchaseDetailId);
            purchaseDetailPO.setUpdateDate(new Date());
            purchaseDetailPO.setUpdateUser(purchaseDetailDTO.getUsername());
        }
        BeanUtils.copyProperties(purchaseDetailDTO, purchaseDetailPO, "id");
        return saveDetail(purchaseDetailPO);
    }

    @Override
    public List<PurchaseDetailPO> findAllDetail(Integer purchaseId) {
        return purchaseDetailRepository.findAllByPurchaseId(purchaseId);
    }

    @Override
    public List<PurchaseDetailViewDTO> findByProductId(Integer productId) {
        return purchaseDetailRepository.findByProductId(productId);
    }

    @Override
    public void createByExcel(UploadSupplierDeliveryDTO uploadSupplierDeliveryDTO) {
        List<ExcelSupplierDeliveryOrderPO> orderArray = uploadSupplierDeliveryDTO.getOrderArray();
        List<ExcelSupplierDeliveryOrderDetailPO> orderDetailArray = uploadSupplierDeliveryDTO.getOrderDetailArray();

        for(ExcelSupplierDeliveryOrderPO order : orderArray){
            Integer excelId = order.getExcelId();
            String dingdanhao = order.getDingdanhao();
            String fahuoshijian = order.getFahuoshijian();
            String shijijiesuan = order.getShijijiesuan();
            String deliveryDate = DateUtil.format(DateUtil.parse(fahuoshijian, "yyyy-MM-dd HH:mm:ss"), "yyyyMMdd");
            String excelDate = DateUtil.format(new Date(), "yyyyMMdd");
            //
            PurchasePO purchasePO = new PurchasePO();
            purchasePO.setExcelId(excelId);
            purchasePO.setExcelDingdan(dingdanhao);
            purchasePO.setAmount(new BigDecimal(shijijiesuan));
            purchasePO.setCreateDate(new Date());
            purchasePO.setDeliveryDate(deliveryDate);
            purchasePO.setSupplier("芳姐");
            purchasePO.setSupplierOrderNo(order.getWuliufangshi() + " " + order.getHuoyundanhao());
            purchasePO.setExcelDate(excelDate);
            save(purchasePO);
        }

        for(ExcelSupplierDeliveryOrderDetailPO orderDetail : orderDetailArray){
            Integer excelId = orderDetail.getExcelId();
            String dingdanhao = orderDetail.getDingdanhao();
            BigDecimal danjia = new BigDecimal(orderDetail.getDanjia());
            Integer shuliang = Integer.valueOf(orderDetail.getShuliang());
            PurchasePO purchasePO = purchaseRepository.getByExcelIdAndExcelDingdan(excelId, dingdanhao);
            Integer purchaseId = purchasePO.getId();
            PurchaseDetailPO purchaseDetailPO = new PurchaseDetailPO();
            purchaseDetailPO.setReceivedQuantity(shuliang);
            purchaseDetailPO.setBookQuantity(shuliang);
            purchaseDetailPO.setProductId(orderDetail.getProductId());
            purchaseDetailPO.setPurchaseId(purchaseId);
            purchaseDetailPO.setUnitPrice(danjia);
            //
            purchaseDetailPO.setCreateDate(new Date());
            saveDetail(purchaseDetailPO);
        }
    }
}
