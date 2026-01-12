package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fiveamazon.erp.common.CommonTable;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.PaymentDTO;
import com.fiveamazon.erp.dto.search.PaymentSearchDTO;
import com.fiveamazon.erp.entity.PaymentPO;
import com.fiveamazon.erp.repository.PaymentRepository;
import com.fiveamazon.erp.service.PaymentService;
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

@Slf4j
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository theRepository;

    private PaymentPO save(PaymentPO item) {
        return theRepository.save(item);
    }

    @Override
    public Long countAll() {
        return theRepository.count();
    }

    @Override
    public PaymentPO getById(Integer id) {
        return theRepository.findById(id).orElse(null);
    }

    @Override
    public List<PaymentPO> findAll() {
        return theRepository.findAllByOrderByTransactionDateDescCreateDateDesc();
    }

    @Override
    public PaymentPO save(PaymentDTO dto) {
        Date today = new Date();
        Integer id = dto.getId();
        if (SimpleConstant.ACTION_DELETE.equalsIgnoreCase(dto.getAction())) {
            theRepository.deleteById(id);
            return new PaymentPO();
        }
        PaymentPO item;
        if (id == null || id == 0) {
            item = new PaymentPO();
            item.setCreateDate(today);
            item.setCreateUser(dto.getUsername());
        } else {
            item = getById(id);
            if (item == null) {
                item = new PaymentPO();
                item.setCreateDate(today);
                item.setCreateUser(dto.getUsername());
            } else {
                item.setUpdateDate(today);
                item.setUpdateUser(dto.getUsername());
            }
        }
        BeanUtils.copyProperties(dto, item, "id");
        return save(item);
    }

    @Override
    public CommonTable search(PaymentSearchDTO searchDTO) {
        Sort sort = Sort.by(Sort.Direction.fromString(searchDTO.getOrder()), searchDTO.getSort());
        PageRequest pageRequest = PageRequest.of(searchDTO.getOffset() / searchDTO.getLimit(), searchDTO.getLimit(), sort);
        Specification<PaymentPO> specification = new Specification<PaymentPO>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                // 默认筛选：inex 不为空
                predicates.add(criteriaBuilder.isNotNull(root.get("inex")));
                if (StringUtils.isNotEmpty(searchDTO.getInex())) {
                    predicates.add(criteriaBuilder.equal(root.get("inex"), searchDTO.getInex()));
                }
                if (StringUtils.isNotEmpty(searchDTO.getTransactionStore())) {
                    predicates.add(criteriaBuilder.like(root.get("transactionStore"), "%" + searchDTO.getTransactionStore() + "%"));
                }
                if (StringUtils.isNotEmpty(searchDTO.getTransactionSubject())) {
                    predicates.add(criteriaBuilder.like(root.get("transactionSubject"), "%" + searchDTO.getTransactionSubject() + "%"));
                }
                if (StringUtils.isNotEmpty(searchDTO.getCategory1st())) {
                    predicates.add(criteriaBuilder.like(root.get("category1st"), "%" + searchDTO.getCategory1st() + "%"));
                }
                if (StringUtils.isNotEmpty(searchDTO.getCategory2nd())) {
                    predicates.add(criteriaBuilder.like(root.get("category2nd"), "%" + searchDTO.getCategory2nd() + "%"));
                }
                if (searchDTO.getTransactionAmountFrom() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionAmount"), searchDTO.getTransactionAmountFrom()));
                }
                if (searchDTO.getTransactionAmountTo() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionAmount"), searchDTO.getTransactionAmountTo()));
                }
                if (StringUtils.isNotEmpty(searchDTO.getTransactionDateFrom())) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"),
                            DateUtil.format(DateUtil.parse(searchDTO.getTransactionDateFrom(), "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd")
                    ));
                }
                if (StringUtils.isNotEmpty(searchDTO.getTransactionDateTo())) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"),
                            DateUtil.format(DateUtil.parse(searchDTO.getTransactionDateTo(), "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd")
                    ));
                }
                if (StringUtils.isNotEmpty(searchDTO.getRemark())) {
                    predicates.add(criteriaBuilder.like(root.get("remark"), "%" + searchDTO.getRemark() + "%"));
                }
                if (StringUtils.isNotBlank(searchDTO.getSearch())) {
                    String searchText = "%" + searchDTO.getSearch() + "%";
                    Predicate p1 = criteriaBuilder.like(root.get("transactionStore"), searchText);
                    Predicate p2 = criteriaBuilder.like(root.get("category1st"), searchText);
                    Predicate p3 = criteriaBuilder.like(root.get("remark"), searchText);
                    predicates.add(criteriaBuilder.or(p1, p2, p3));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return new CommonTable(theRepository.count(specification), theRepository.findAll(specification, pageRequest).getContent());
    }
}
