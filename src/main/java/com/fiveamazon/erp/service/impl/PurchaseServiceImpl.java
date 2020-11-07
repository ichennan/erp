package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.PurchaseDTO;
import com.fiveamazon.erp.dto.PurchaseDetailDTO;
import com.fiveamazon.erp.dto.PurchaseProductSearchDTO;
import com.fiveamazon.erp.dto.UploadSupplierDeliveryDTO;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.repository.PurchaseDetailRepository;
import com.fiveamazon.erp.repository.PurchaseRepository;
import com.fiveamazon.erp.repository.PurchaseViewRepository;
import com.fiveamazon.erp.service.ProductService;
import com.fiveamazon.erp.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PurchaseViewRepository purchaseViewRepository;
    @Autowired
    private PurchaseDetailRepository purchaseDetailRepository;
    @Autowired
    private ProductService productService;

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
    public void createByExcel(UploadSupplierDeliveryDTO uploadSupplierDeliveryDTO) {
        log.info("PurchaseServiceImpl.createByExcel: " + new JSONObject(uploadSupplierDeliveryDTO).toString());
        List<ExcelSupplierDeliveryOrderPO> orderArray = uploadSupplierDeliveryDTO.getOrderArray();
        List<ExcelSupplierDeliveryOrderDetailPO> orderDetailArray = uploadSupplierDeliveryDTO.getOrderDetailArray();

        for(ExcelSupplierDeliveryOrderPO order : orderArray){
            Integer excelId = order.getExcelId();
            String dingdanhao = order.getDingdanhao();
            String fahuoshijian = order.getFahuoshijian();
            String shijijiesuan = order.getShijijiesuan();
            String deliveryDate = "";
            try{
                deliveryDate = DateUtil.format(DateUtil.parse(fahuoshijian, "yyyy-MM-dd HH:mm:ss"), "yyyyMMdd");
            }catch (Exception e){
            }
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
            Integer productId = orderDetail.getProductId();
            String dingdanhao = orderDetail.getDingdanhao();
            BigDecimal danjia = new BigDecimal(0);
            Integer shuliang = 0;
            try{
                danjia = new BigDecimal(orderDetail.getDanjia());
            }catch (Exception e){
            }
            try{
                shuliang = Integer.valueOf(orderDetail.getShuliang());
            }catch (Exception e){
            }
            PurchasePO purchasePO = purchaseRepository.getByExcelIdAndExcelDingdan(excelId, dingdanhao);
            Integer purchaseId = purchasePO.getId();
            PurchaseDetailPO purchaseDetailPO = new PurchaseDetailPO();
            purchaseDetailPO.setReceivedQuantity(shuliang);
            purchaseDetailPO.setBookQuantity(shuliang);
            purchaseDetailPO.setProductId(productId);
            purchaseDetailPO.setPurchaseId(purchaseId);
            purchaseDetailPO.setUnitPrice(danjia);
            //
            purchaseDetailPO.setCreateDate(new Date());
            saveDetail(purchaseDetailPO);
            //
            productService.updatePurchasePrice(productId, danjia);
        }
    }

    @Override
    public List<PurchaseDetailPO> findAllProducts(PurchaseProductSearchDTO purchaseProductSearchDTO) {
        String dateFrom = purchaseProductSearchDTO.getDateFrom();
        String dateTo = purchaseProductSearchDTO.getDateTo();
        String dateType = purchaseProductSearchDTO.getDateType();
        String productId = purchaseProductSearchDTO.getProductId();
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(0, 100000000, sort);
        Specification<PurchaseDetailPO> specification = new Specification<PurchaseDetailPO>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (StringUtils.isNotEmpty(productId)) {
                    predicates.add(criteriaBuilder.equal(root.get("productId"), productId));
                }

//                if (StringUtils.isNotEmpty(accessLogSearchDTO.getAccessUser())) {
//                    predicates.add(criteriaBuilder.equal(root.get("accessUser"), accessLogSearchDTO.getAccessUser()));
//                }
//
//
//                SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHH:mm:ss");
//                if (StringUtils.isNotEmpty(accessLogSearchDTO.getAccessDateFrom())) {
//                    try {
//                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("accessDate"), sdf.parse(UpiDateUtils.toStr(accessLogSearchDTO.getAccessDateFrom()))));
//                    } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//
//                if (StringUtils.isNotEmpty(accessLogSearchDTO.getAccessDateTo())) {
//                    try {
//                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("accessDate"), sdf.parse(UpiDateUtils.toStr(accessLogSearchDTO.getAccessDateTo()))));
//                    } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return purchaseDetailRepository.findAll(specification, pageRequest).getContent();
    }
}
