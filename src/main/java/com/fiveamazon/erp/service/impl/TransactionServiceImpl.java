package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fiveamazon.erp.common.CommonTable;
import com.fiveamazon.erp.dto.search.TransactionSearchDTO;
import com.fiveamazon.erp.entity.TransactionPO;
import com.fiveamazon.erp.entity.excel.ExcelTransactionDetailPO;
import com.fiveamazon.erp.entity.excel.ExcelTransactionPO;
import com.fiveamazon.erp.repository.TransactionRepository;
import com.fiveamazon.erp.service.ExcelService;
import com.fiveamazon.erp.service.TransactionService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository theRepository;
    @Autowired
    ExcelService excelService;

    @Override
    public TransactionPO getById(Integer id) {
        return theRepository.getById(id);
    }

    @Override
    public TransactionPO save(TransactionPO item) {
        return theRepository.save(item);
    }

    @Override
    public void createByExcel(Integer excelId) {
        log.info("TransactionServiceImpl.createByExcel: " + excelId);
        ExcelTransactionPO excelTransactionPO = excelService.getTransactionByExcelId(excelId);
        Date dateFrom = excelTransactionPO.getDateFrom();
        Date dateTo = excelTransactionPO.getDateTo();
        Integer storeId = excelTransactionPO.getStoreId();
        theRepository.deleteByStoreIdAndTransactionTimeBetween(storeId, dateFrom, dateTo);
        log.info("theRepository.deleteByStoreIdAndTransactionTimeBetween [{}] [{}] [{}]", storeId, dateFrom, dateTo);
        List<ExcelTransactionDetailPO> list = excelService.findTransactionDetailByExcelId(excelId);
        for(ExcelTransactionDetailPO item : list){
            TransactionPO transactionPO = new TransactionPO();
            BeanUtils.copyProperties(item, transactionPO, "id");
            transactionPO.setExcelId(excelId);
            transactionPO.setStoreId(storeId);
            save(transactionPO);
        }
    }

    @Override
    public CommonTable search(TransactionSearchDTO searchDTO) {
        Sort sort = Sort.by(Sort.Direction.fromString(searchDTO.getOrder()), searchDTO.getSort());
        PageRequest pageRequest = PageRequest.of(searchDTO.getOffset()/searchDTO.getLimit(), searchDTO.getLimit(), sort);
        Specification<TransactionPO> specification = new Specification<TransactionPO>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (null != searchDTO.getStoreId()) {
                    predicates.add(criteriaBuilder.equal(root.get("storeId"), searchDTO.getStoreId()));
                }
                if (StringUtils.isNotEmpty(searchDTO.getOrderId())) {
                    predicates.add(criteriaBuilder.equal(root.get("orderId"), searchDTO.getOrderId()));
                }
                if (StringUtils.isNotEmpty(searchDTO.getSku())) {
                    predicates.add(criteriaBuilder.equal(root.get("sku"), searchDTO.getSku()));
                }
                if (StringUtils.isNotEmpty(searchDTO.getDescription())) {
                    predicates.add(criteriaBuilder.like(root.get("description"), "%" + searchDTO.getDescription() + "%"));
                }
                if (StringUtils.isNotEmpty(searchDTO.getType())) {
                    if("Others".equalsIgnoreCase(searchDTO.getType())){
                        Predicate p1 = criteriaBuilder.isNull(root.get("type"));
                        Predicate p2 = criteriaBuilder.equal(root.get("type"), "");
                        predicates.add(criteriaBuilder.or(p1, p2));
                    }else{
                        predicates.add(criteriaBuilder.equal(root.get("type"), searchDTO.getType()));
                    }
                }
//                //
//                if (StringUtils.isNotEmpty(searchDTO.getTransactionAmountFrom()) && searchDTO.getTransactionAmountFrom().compareTo(new BigDecimal(0)) != 0) {
//                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionAmount"), searchDTO.getTransactionAmountFrom()));
//                }
//                if (StringUtils.isNotEmpty(searchDTO.getTransactionAmountTo()) && searchDTO.getTransactionAmountTo().compareTo(new BigDecimal(0)) != 0) {
//                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionAmount"), searchDTO.getTransactionAmountTo()));
//                }
                if (StringUtils.isNotEmpty(searchDTO.getTransactionTimeFrom())) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionTime"), DateUtil.parse(searchDTO.getTransactionTimeFrom(), "yyyy-MM-dd HH:mm:ss")));
                }
                if (StringUtils.isNotEmpty(searchDTO.getTransactionTimeTo())) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionTime"), DateUtil.parse(searchDTO.getTransactionTimeTo(), "yyyy-MM-dd HH:mm:ss")));
                }
                if(StringUtils.isNotBlank(searchDTO.getSearch())){
                    String searchText = "%" + searchDTO.getSearch() + "%";
                    Predicate p1 = criteriaBuilder.like(root.get("orderId"), searchText);
                    Predicate p2 = criteriaBuilder.like(root.get("sku"), searchText);
                    predicates.add(criteriaBuilder.or(p1, p2));
                }
//
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
//        log.info("-----");
//        Page abc = theRepository.findAll(specification, pageRequest);
//        List<TransactionPO> list = abc.getContent();
//        log.info("content size: " + list.size());
//        log.info("abc size: " + abc.getTotalElements());
//        log.info("abc pages: " + abc.getTotalPages());
//        for(TransactionPO item: list){
//            log.info("item: " + item.toString());
//        }
        return new CommonTable(theRepository.count(specification), theRepository.findAll(specification, pageRequest).getContent());
    }

    @Override
    public List<TransactionPO> findByDate(Date dateFrom, Date dateTo, Integer storeId) {
        return theRepository.findByTransactionTimeBetweenAndStoreIdOrderByStoreIdAscTransactionTimeAsc(dateFrom, dateTo, storeId);
    }
}
