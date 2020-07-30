package com.fiveamazon.erp.service.impl;

import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderDetailPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryPO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderDetailEO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderEO;
import com.fiveamazon.erp.repository.ExcelSupplierDeliveryOrderDetailRepository;
import com.fiveamazon.erp.repository.ExcelSupplierDeliveryOrderRepository;
import com.fiveamazon.erp.repository.ExcelSupplierDeliveryRepository;
import com.fiveamazon.erp.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private ExcelSupplierDeliveryRepository excelSupplierDeliveryRepository;

    @Autowired
    private ExcelSupplierDeliveryOrderRepository excelSupplierDeliveryOrderRepository;

    @Autowired
    private ExcelSupplierDeliveryOrderDetailRepository excelSupplierDeliveryOrderDetailRepository;

    @Override
    public void insertExcelSupplierDeliveryOrder(Integer excelId, List<ExcelSupplierDeliveryOrderEO> excelSupplierDeliveryOrderEOList) {
        log.warn("ExcelServiceImpl.insertExcelSupplierDeliveryOrder");
        for(ExcelSupplierDeliveryOrderEO excelSupplierDeliveryOrderEO : excelSupplierDeliveryOrderEOList){
            if(StringUtils.isBlank(excelSupplierDeliveryOrderEO.getDingdanhao())){
                continue;
            }
            ExcelSupplierDeliveryOrderPO excelSupplierDeliveryOrderPO = new ExcelSupplierDeliveryOrderPO();
            BeanUtils.copyProperties(excelSupplierDeliveryOrderEO, excelSupplierDeliveryOrderPO);
            excelSupplierDeliveryOrderPO.setExcelId(excelId);
            excelSupplierDeliveryOrderRepository.save(excelSupplierDeliveryOrderPO);
        }
    }

    @Override
    public void insertExcelSupplierDeliveryOrderDetail(Integer excelId, List<ExcelSupplierDeliveryOrderDetailEO> excelSupplierDeliveryOrderDetailEOList) {
        log.warn("ExcelServiceImpl.insertExcelSupplierDeliveryOrderDetail");
        for(ExcelSupplierDeliveryOrderDetailEO excelSupplierDeliveryOrderDetailEO : excelSupplierDeliveryOrderDetailEOList){
            if(StringUtils.isBlank(excelSupplierDeliveryOrderDetailEO.getDingdanhao())){
                continue;
            }
            ExcelSupplierDeliveryOrderDetailPO excelSupplierDeliveryOrderDetailPO = new ExcelSupplierDeliveryOrderDetailPO();
            BeanUtils.copyProperties(excelSupplierDeliveryOrderDetailEO, excelSupplierDeliveryOrderDetailPO);
            excelSupplierDeliveryOrderDetailPO.setExcelId(excelId);
            excelSupplierDeliveryOrderDetailRepository.save(excelSupplierDeliveryOrderDetailPO);
        }
    }

    @Override
    public Integer saveExcelSupplierDelivery(ExcelSupplierDeliveryPO excelSupplierDeliveryPO) {
        log.warn("ExcelServiceImpl.saveExcelSupplierDelivery");
        excelSupplierDeliveryPO.setCreateDate(new Date());
        excelSupplierDeliveryRepository.save(excelSupplierDeliveryPO);
        return excelSupplierDeliveryPO.getId();
    }

    @Override
    public List<ExcelSupplierDeliveryOrderPO> findOrderByExcelId(Integer excelId) {
        return excelSupplierDeliveryOrderRepository.findByExcelId(excelId);
    }

    @Override
    public List<ExcelSupplierDeliveryOrderDetailPO> findOrderDetailByExcelId(Integer excelId) {
        return excelSupplierDeliveryOrderDetailRepository.findByExcelId(excelId);
    }
}
