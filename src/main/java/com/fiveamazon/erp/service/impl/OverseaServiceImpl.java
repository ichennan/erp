package com.fiveamazon.erp.service.impl;

import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.OverseaBatchInsertDTO;
import com.fiveamazon.erp.dto.OverseaDTO;
import com.fiveamazon.erp.dto.OverseaDetailDTO;
import com.fiveamazon.erp.entity.OverseaDetailPO;
import com.fiveamazon.erp.entity.OverseaPO;
import com.fiveamazon.erp.entity.OverseaViewPO;
import com.fiveamazon.erp.entity.SkuInfoPO;
import com.fiveamazon.erp.entity.excel.ExcelCarrierBillDetailPO;
import com.fiveamazon.erp.entity.excel.ExcelCarrierBillPO;
import com.fiveamazon.erp.repository.OverseaDetailRepository;
import com.fiveamazon.erp.repository.OverseaRepository;
import com.fiveamazon.erp.repository.OverseaViewRepository;
import com.fiveamazon.erp.service.ExcelService;
import com.fiveamazon.erp.service.OverseaService;
import com.fiveamazon.erp.service.ShipmentService;
import com.fiveamazon.erp.service.SkuService;
import com.fiveamazon.erp.util.JsonRemarkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class OverseaServiceImpl implements OverseaService {
    @Autowired
    private OverseaRepository theRepository;
    @Autowired
    private OverseaViewRepository theViewRepository;
    @Autowired
    private OverseaDetailRepository theDetailRepository;
    @Autowired
    private SkuService skuService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private ExcelService excelService;

    public OverseaPO save(OverseaPO item) {
        if (StringUtils.isBlank(item.getSignedDate())) {
            item.setSignedDate("");
        }
        if (StringUtils.isBlank(item.getWeightRemark())) {
            item.setWeightRemark("");
        }
        if (null == item.getWeight()) {
            item.setWeight(new BigDecimal(0));
        }
        if (null == item.getAmount()) {
            item.setAmount(new BigDecimal(0));
        }
        if (null == item.getWarehouseAmount()) {
            item.setWarehouseAmount(new BigDecimal(0));
        }
        if (null == item.getUnitPrice()) {
            item.setUnitPrice(new BigDecimal(0));
        }
        if (null == item.getChargeWeight()) {
            item.setChargeWeight(new BigDecimal(0));
        }
        return theRepository.save(item);
    }

    private OverseaDetailPO saveDetail(OverseaDetailPO item) {
        if (StringUtils.isBlank(item.getFbaNo())) {
            item.setFbaNo("");
        }
        if (null == item.getQuantity()) {
            item.setQuantity(0);
        }
        if (null == item.getWeight()) {
            item.setWeight(new BigDecimal(0));
        }
        return theDetailRepository.save(item);
    }

    @Override
    public Long countAll() {
        return theRepository.count();
    }

    @Override
    public OverseaPO getById(Integer id) {
        return theRepository.getById(id);
    }

    @Override
    public OverseaPO getByDeliveryNo(String deliveryNo) {
        return theRepository.getByDeliveryNo(deliveryNo);
    }

    @Override
    public OverseaDetailPO getDetailById(Integer id) {
        return theDetailRepository.getById(id);
    }

    @Override
    public List<OverseaViewPO> findAll() {
        return theViewRepository.findAllByDeliveryDateDesc();
    }

    @Override
    public List<OverseaDetailPO> findAllDetail(Integer overseaId) {
        return theDetailRepository.findByOverseaIdOrderByFbaDateAscBoxDescriptionAscBoxAsc(overseaId);
    }

    @Override
    public OverseaPO save(OverseaDTO dto) {
        Date today = new Date();
        Integer id = dto.getId();
        if (SimpleConstant.ACTION_DELETE.equalsIgnoreCase(dto.getAction())) {
            theDetailRepository.deleteByOverseaId(id);
            theRepository.deleteById(id);
            return new OverseaPO();
        }
        OverseaPO item;
        if (id == null || id == 0) {
            item = new OverseaPO();
            item.setCreateDate(today);
            item.setCreateUser(dto.getUsername());
        } else {
            item = getById(id);
            item.setUpdateDate(today);
            item.setUpdateUser(dto.getUsername());
        }
        BeanUtils.copyProperties(dto, item, "id");
        return save(item);
    }

    @Override
    public OverseaDetailPO saveDetail(OverseaDetailDTO dto, Boolean refreshSku) {
        Date today = new Date();
        Integer id = dto.getId();
        if (SimpleConstant.ACTION_DELETE.equalsIgnoreCase(dto.getAction())) {
            theDetailRepository.deleteById(id);
            return null;
        }
        OverseaDetailPO item;
        if (id == null || id == 0) {
            item = new OverseaDetailPO();
            item.setCreateDate(today);
            item.setCreateUser(dto.getUsername());
        } else {
            item = getDetailById(id);
            item.setUpdateDate(today);
            item.setUpdateUser(dto.getUsername());
        }
        BeanUtils.copyProperties(dto, item, "id");
        if (refreshSku) {
            Integer skuId = dto.getSkuId();
            SkuInfoPO skuInfoPO = skuService.getById(skuId);
            if (skuInfoPO != null) {
                item.setSku(skuInfoPO.getSku());
                item.setProductId(skuInfoPO.getProductId());
                item.setStoreId(skuInfoPO.getStoreId());
            }
        }
        return saveDetail(item);
    }

    @Override
    public OverseaDetailPO saveFba(OverseaDetailDTO dto) {
        Date today = new Date();
        Integer id = dto.getId();
        OverseaDetailPO item = getDetailById(id);
        item.setUpdateDate(today);
        item.setUpdateUser(dto.getUsername());
        if (SimpleConstant.ACTION_DELETE.equalsIgnoreCase(dto.getAction())) {
            item.setFbaNo("");
            item.setFbaBox("");
            item.setFbaDate("");
            return saveDetail(item);
        }
        item.setFbaNo(dto.getFbaNo());
        item.setFbaBox(dto.getFbaBox());
        item.setFbaDate(dto.getFbaDate());
        // Not create shipment from 20230611
        // shipmentService.saveByOverseaDetail(item, dto);
        return saveDetail(item);
    }

    @Override
    public void batchInsert(OverseaBatchInsertDTO dto) {
        OverseaPO item = save(dto.getItem());
        Integer overseaId = item.getId();
        for (OverseaDetailDTO detailDTO : dto.getArray()) {
            detailDTO.setOverseaId(overseaId);
            saveDetail(detailDTO, false);
        }
    }

    @Override
    public List<OverseaPO> findByDate(String dateFrom, String dateTo, Integer storeId) {
        return theRepository.findByDeliveryDateBetweenAndStoreIdOrderByStoreIdAscDeliveryDateAsc(dateFrom, dateTo, storeId);
    }

    @Override
    public void updateCarrierBillByExcel(Integer excelId) {
        ExcelCarrierBillPO excelPO = excelService.getCarrierBillByExcelId(excelId);
        List<ExcelCarrierBillDetailPO> detailPOList = excelService.findCarrierBillDetailByExcelId(excelId);
        for (ExcelCarrierBillDetailPO detailPO : detailPOList) {
            Integer overseaId = detailPO.getRelatedOverseaId();
            if (null == overseaId || overseaId == 0) {
                continue;
            }
            OverseaPO overseaPO = getById(overseaId);
            overseaPO.setCarrier(excelPO.getCarrier());
            overseaPO.setAmount(new BigDecimal(detailPO.getAmount()));
            overseaPO.setChargeWeight(new BigDecimal(detailPO.getChargeWeight()));
            overseaPO.setRoute(detailPO.getRoute());
            overseaPO.setUnitPrice(new BigDecimal(detailPO.getUnitPrice()));
            overseaPO.setTrackingNumber(detailPO.getTrackingNumber());
            overseaPO.setJsonRemark(JsonRemarkUtils.setJsonRemark(
                    overseaPO.getJsonRemark(),
                    SimpleConstant.JSON_REMARK_CHARGE_UPDATE_BY_CARRIER_BILL_EXCEL,
                    detailPO.getId().toString()));
            save(overseaPO);
        }
    }
}
