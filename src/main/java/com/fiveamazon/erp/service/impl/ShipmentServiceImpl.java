package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonException;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.common.StatusConstant;
import com.fiveamazon.erp.dto.*;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.repository.*;
import com.fiveamazon.erp.service.ShipmentService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private ShipmentViewRepository shipmentViewRepository;
    @Autowired
    private ShipmentDetailRepository shipmentDetailRepository;
    @Autowired
    private ShipmentProductViewRepository shipmentProductViewRepository;

    @Override
    public Long countAll() {
        return shipmentRepository.count();
    }

    @Override
    public ShipmentPO getById(Integer id) {
        return shipmentRepository.getOne(id);
    }

    @Override
    public ShipmentDetailPO getDetailById(Integer id) {
        return shipmentDetailRepository.getOne(id);
    }

    @Override
    public List<ShipmentViewPO> findAll() {
        return shipmentViewRepository.findAll();
    }

    @Override
    public ShipmentPO save(ShipmentPO shipmentPO) {
        if(StringUtils.isBlank(shipmentPO.getSignedDate())){
            shipmentPO.setSignedDate("");
        }
        if(StringUtils.isBlank(shipmentPO.getWeightRemark())){
            shipmentPO.setWeightRemark("");
        }
        if(null == shipmentPO.getWeight()){
            shipmentPO.setWeight(new BigDecimal(0));
        }
        return shipmentRepository.save(shipmentPO);
    }

    @Override
    public ShipmentDetailPO saveDetail(ShipmentDetailPO shipmentDetailPO) {
        if(StringUtils.isBlank(shipmentDetailPO.getBox())){
            shipmentDetailPO.setBox("");
        }
        if(null == shipmentDetailPO.getQuantity()){
            shipmentDetailPO.setQuantity(0);
        }
        if(null == shipmentDetailPO.getWeight()){
            shipmentDetailPO.setWeight(new BigDecimal(0));
        }
        return shipmentDetailRepository.save(shipmentDetailPO);
    }

    @Override
    public ShipmentPO save(ShipmentDTO shipmentDTO) {
        Integer shipmentId = shipmentDTO.getId();
        Integer excelId = shipmentDTO.getExcelId();
        ShipmentPO shipmentPO;
        if(shipmentId == null || shipmentId == 0){
            shipmentPO = new ShipmentPO();
            shipmentPO.setCreateDate(new Date());
            shipmentPO.setCreateUser(shipmentDTO.getUsername());
        }else{
            shipmentPO = getById(shipmentId);
            shipmentPO.setUpdateDate(new Date());
            shipmentPO.setUpdateUser(shipmentDTO.getUsername());
            //if excel, the detail can't be updated, because it will delete sku
            if(null == excelId || excelId == 0){
                shipmentDetailRepository.deleteByShipmentIdEqualsAndBoxNotLike(shipmentId, "Plan");
                for(ShipmentDetailDTO shipmentDetailDTO: shipmentDTO.getShipmentDetailList()){
                    shipmentDetailDTO.setUsername(shipmentDTO.getUsername());
                    saveDetail(shipmentDetailDTO);
                }
            }
        }

        if(null == excelId || excelId == 0){
            BeanUtils.copyProperties(shipmentDTO, shipmentPO, "id");
        }else{
            BeanUtils.copyProperties(shipmentDTO, shipmentPO, "id", "weight");
        }
        return save(shipmentPO);
    }

    @Override
    public ShipmentDetailPO saveDetail(ShipmentDetailDTO shipmentDetailDTO) {
        if(SimpleConstant.ACTION_DELETE.equalsIgnoreCase(shipmentDetailDTO.getAction())){
            shipmentDetailRepository.deleteById(shipmentDetailDTO.getId());
            return null;
        }
        Integer shipmentDetailId = shipmentDetailDTO.getId();
        ShipmentDetailPO shipmentDetailPO;
        if(shipmentDetailId == null || shipmentDetailId == 0){
            shipmentDetailPO = new ShipmentDetailPO();
            shipmentDetailPO.setCreateDate(new Date());
            shipmentDetailPO.setCreateUser(shipmentDetailDTO.getUsername());
        }else{
            shipmentDetailPO = getDetailById(shipmentDetailId);
            shipmentDetailPO.setUpdateDate(new Date());
            shipmentDetailPO.setUpdateUser(shipmentDetailDTO.getUsername());
        }
        BeanUtils.copyProperties(shipmentDetailDTO, shipmentDetailPO, "id");
        return saveDetail(shipmentDetailPO);
    }

    @Override
    public List<ShipmentDetailPO> findAllDetail(Integer shipmentId) {
        return shipmentDetailRepository.findAllByShipmentIdOrderByBox(shipmentId);
    }

    @Override
    public void createByExcel(UploadFbaDTO uploadFbaDTO) {
        Date today = new Date();
        Integer boxCount = uploadFbaDTO.getBoxCount();
        List<ExcelFbaPackListPO> array = uploadFbaDTO.getArray();
        String fbaNo = uploadFbaDTO.getShipmentId();
        if(shipmentRepository.countByFbaNo(fbaNo) > 0){
            throw new SimpleCommonException("Duplicate FBA Found ! 请勿重复上传该FBA: " + fbaNo);
        }
        String excelDate = DateUtil.format(new Date(), "yyyyMMdd");
        Integer excelId = uploadFbaDTO.getId();
        ShipmentPO shipmentPO = new ShipmentPO();
        shipmentPO.setExcelDate(excelDate);
        shipmentPO.setExcelId(excelId);
        shipmentPO.setBoxCount(uploadFbaDTO.getBoxCount());
        shipmentPO.setFbaNo(fbaNo);
        shipmentPO.setCreateDate(today);
        shipmentPO.setDeliveryDate(uploadFbaDTO.getDeliveryDate());
        shipmentPO.setCarrier(uploadFbaDTO.getCarrier());
        shipmentPO.setRoute(uploadFbaDTO.getRoute());
        shipmentPO.setStoreId(uploadFbaDTO.getStoreId());
        shipmentPO.setUnitPrice(uploadFbaDTO.getUnitPrice());
        shipmentPO.setWeight(uploadFbaDTO.getWeight());
        shipmentPO.setWeightRemark(uploadFbaDTO.getWeightRemark());
        shipmentPO.setRemark(uploadFbaDTO.getRemark());
        shipmentPO.setStatusDelivery(StatusConstant.SHIPMENT_STATUS_DELIVERY);
        save(shipmentPO);
        Integer shipmentId = shipmentPO.getId();
        //
        JSONObject allJson = new JSONObject();
        for(Integer i = 1; i <= boxCount; i++){
            String iString = StringUtils.leftPad(i.toString(), 2, "0");
            allJson.put("box" + iString, new JSONArray());
        }

        for(ExcelFbaPackListPO excelFbaPackListPO : array){
            JSONObject excelFbaPackListJson = new JSONObject(excelFbaPackListPO);
            Integer productId = excelFbaPackListJson.getInt("productId");
            Integer skuId = excelFbaPackListJson.getInt("skuId");
            String sku = excelFbaPackListPO.getMerchantSku();
            for(Integer i = 1; i <= boxCount; i++){
                String iString = StringUtils.leftPad(i.toString(), 2, "0");
                if(StringUtils.isNotBlank(excelFbaPackListJson.getStr("box" + iString + "Qty"))){
                    String boxString = "box" + iString;
                    JSONArray boxArray = allJson.getJSONArray(boxString);
                    JSONObject productJson = new JSONObject();
                    productJson.put("productId", productId);
                    productJson.put("skuId", skuId);
                    productJson.put("sku", sku);
                    productJson.put("quantity", excelFbaPackListJson.getStr(boxString + "Qty"));
                    boxArray.add(productJson);
                }
            }
        }

        log.warn("allJson");
        log.warn(allJson.toString());


        JSONObject planJson = new JSONObject();
        for(String boxString : allJson.keySet()){
            log.warn("boxString: " + boxString);
            String boxNumber = boxString.substring(3, 5);
            JSONArray boxArray = allJson.getJSONArray(boxString);
            log.warn(boxString + " size: " + boxArray.size());
            for(JSONObject boxJson : boxArray.jsonIter()){
                log.warn("boxJson: " + boxJson.toString());
                String skuId = boxJson.getStr("skuId");
                Integer quantity = boxJson.getInt("quantity");
                boxJson.put("shipmentId", shipmentId);
                boxJson.put("box", boxNumber);
                createShipmentDetailByJson(boxJson);
                //
                if(planJson.containsKey(skuId)){
                    JSONObject planSkuJson = planJson.getJSONObject(skuId);
                    planSkuJson.put("quantity", planSkuJson.getInt("quantity") + quantity);
                }else{
                    planJson.put(skuId, boxJson);
                }
            }
        }
        //
        for(String planJsonKey : planJson.keySet()){
            JSONObject planSkuJson = planJson.getJSONObject(planJsonKey);
            planSkuJson.put("box", "Plan");
            createShipmentDetailByJson(planSkuJson);
        }

    }

    private void createShipmentDetailByJson(JSONObject boxJson){
        ShipmentDetailPO shipmentDetailPO = new ShipmentDetailPO();
        shipmentDetailPO.setBox(boxJson.getStr("box"));
        shipmentDetailPO.setQuantity(boxJson.getInt("quantity"));
        shipmentDetailPO.setProductId(boxJson.getInt("productId"));
        shipmentDetailPO.setShipmentId(boxJson.getInt("shipmentId"));
        shipmentDetailPO.setSkuId(boxJson.getInt("skuId"));
        shipmentDetailPO.setWeight(new BigDecimal(0));
        shipmentDetailPO.setSku(boxJson.getStr("sku"));
        saveDetail(shipmentDetailPO);
    }

    @Override
    public List<ShipmentProductViewPO> findAllProducts(ShipmentProductSearchDTO searchDTO) {
        String dateFrom = searchDTO.getDateFrom().replaceAll("-", "");
        String dateTo = searchDTO.getDateTo().replaceAll("-", "");
        String dateType = searchDTO.getDateType();
        String productId = searchDTO.getProductId();
        String skuId = searchDTO.getSkuId();
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(0, 100000000, sort);
        Specification<ShipmentProductViewPO> specification = new Specification<ShipmentProductViewPO>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (StringUtils.isNotEmpty(productId)) {
                    predicates.add(criteriaBuilder.equal(root.get("productId"), productId));
                }
                if (StringUtils.isNotEmpty(skuId)) {
                    predicates.add(criteriaBuilder.equal(root.get("skuId"), skuId));
                }

                switch (dateType){
                    case "excelDate":
                        if (StringUtils.isNotEmpty(dateFrom)) {
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("excelDate"), dateFrom));
                        }
                        if (StringUtils.isNotEmpty(dateTo)) {
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("excelDate"), dateTo));
                        }
                        break;
                    case "signedDate":
                        if (StringUtils.isNotEmpty(dateFrom)) {
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("signedDate"), dateFrom));
                        }
                        if (StringUtils.isNotEmpty(dateTo)) {
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("signedDate"), dateTo));
                        }
                        break;
                    case "deliveryDate":
                        if (StringUtils.isNotEmpty(dateFrom)) {
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("deliveryDate"), dateFrom));
                        }
                        if (StringUtils.isNotEmpty(dateTo)) {
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deliveryDate"), dateTo));
                        }
                        break;
                    case "paymentDate":
                        if (StringUtils.isNotEmpty(dateFrom)) {
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("paymentDate"), dateFrom));
                        }
                        if (StringUtils.isNotEmpty(dateTo)) {
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("paymentDate"), dateTo));
                        }
                        break;
                    default:
                        break;
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return shipmentProductViewRepository.findAll(specification, pageRequest).getContent();
    }


    @Override
    public Long countBySkuId(Integer skuId) {
        return shipmentDetailRepository.countBySkuIdEquals(skuId);
    }

    @Override
    public ShipmentPO saveByOverseaDetail(OverseaDetailPO overseaDetailPO, OverseaDetailDTO overseaDetailDTO){
        Date today = new Date();
        String username = overseaDetailDTO.getUsername();
        String action = overseaDetailDTO.getAction();
        String fbaNo = overseaDetailDTO.getFbaNo();
        String fbaBox = overseaDetailDTO.getFbaBox();
        String fbaDate = overseaDetailDTO.getFbaDate();
        BigDecimal weight = overseaDetailPO.getWeight();
        log.info("weight: " + weight);
        Long fbaNoCount = shipmentRepository.countByFbaNo(fbaNo);
        ShipmentPO shipmentPO;
        if("create".equalsIgnoreCase(action)){
            if(fbaNoCount > 0){
                throw new SimpleCommonException("该FBA单号存在 ! 请勿重复创建 或者选择 追加至已有FBA " + fbaNo);
            }
            shipmentPO = new ShipmentPO();
            shipmentPO.setCreateDate(today);
            shipmentPO.setCreateUser(username);
            shipmentPO.setStoreId(overseaDetailPO.getStoreId());
            shipmentPO.setFbaNo(fbaNo);
            shipmentPO.setCarrier("FBA自提");
            shipmentPO.setRoute("FBA");
            //已发货
            shipmentPO.setStatusDelivery("1");
            shipmentPO.setRemark("create by overseaDetailId: " + overseaDetailPO.getId());
            shipmentPO.setWeightRemark("" + weight);
            shipmentPO.setWeight(weight);
        }else{
            //i.e. action = update
            if(fbaNoCount <= 0){
                throw new SimpleCommonException("该FBA单号不存在 ! 请点击创建FBA " + fbaNo);
            }
            shipmentPO = shipmentRepository.getByFbaNo(fbaNo);
            shipmentPO.setUpdateDate(today);
            shipmentPO.setUpdateUser(username);
            shipmentPO.setWeightRemark(shipmentPO.getWeightRemark() + "+" + weight);
            shipmentPO.setWeight(weight.add(shipmentPO.getWeight()));
        }
        if(StringUtils.isNotBlank(fbaDate)){
            shipmentPO.setDeliveryDate(fbaDate);
        }
        shipmentPO = save(shipmentPO);

        Integer shipmentId = shipmentPO.getId();
        ShipmentDetailPO shipmentDetailPO = new ShipmentDetailPO();
        shipmentDetailPO.setShipmentId(shipmentId);
        shipmentDetailPO.setBox(fbaBox);
        //
        shipmentDetailPO.setProductId(overseaDetailPO.getProductId());
        shipmentDetailPO.setSkuId(overseaDetailPO.getSkuId());
        shipmentDetailPO.setQuantity(overseaDetailPO.getQuantity());
        shipmentDetailPO.setWeight(weight);
        shipmentDetailPO.setRemark("create by overseaDetailId: " + overseaDetailPO.getId());
        saveDetail(shipmentDetailPO);
        return shipmentPO;
    }
}
