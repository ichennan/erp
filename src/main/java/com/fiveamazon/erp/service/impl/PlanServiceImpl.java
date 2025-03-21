package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.PlanCreateDTO;
import com.fiveamazon.erp.dto.PlanDetailCreateDTO;
import com.fiveamazon.erp.entity.PlanDetailPO;
import com.fiveamazon.erp.entity.PlanPO;
import com.fiveamazon.erp.repository.PlanDetailRepository;
import com.fiveamazon.erp.repository.PlanRepository;
import com.fiveamazon.erp.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class PlanServiceImpl implements PlanService {
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private PlanDetailRepository planDetailRepository;

    @Override
    public List<PlanPO> findAll() {
        return planRepository.findAll();
    }

    @Override
    public PlanPO getById(Integer id) {
        return planRepository.getById(id);
    }

    @Override
    public List<PlanDetailPO> findAllDetail(Integer planId) {
        return planDetailRepository.findAllByPlanId(planId);
    }

    private PlanPO create(PlanPO planPO) {
        Date today = new Date();
        planPO.setPlanDate(DateUtil.format(today, "yyyyMMdd"));
        planPO.setCreateDate(today);
        planPO.setUpdateDate(today);
        return planRepository.save(planPO);
    }

    private PlanDetailPO create(PlanDetailPO planDetailPO) {
        planDetailPO.setCreateDate(new Date());
        planDetailPO.setUpdateDate(planDetailPO.getCreateDate());
        return planDetailRepository.save(planDetailPO);
    }

    @Override
    public Integer create(PlanCreateDTO planCreateDTO) {
        log.info("PlanServiceImpl.create: " + new JSONObject(planCreateDTO).toString());
        PlanPO planPO = new PlanPO();
        BeanUtils.copyProperties(planCreateDTO, planPO, "id");
        planPO = create(planPO);
        Integer planId = planPO.getId();
        List<PlanDetailCreateDTO> planDetailCreateDTOList = planCreateDTO.getArray();
        for (PlanDetailCreateDTO planDetailCreateDTO : planDetailCreateDTOList) {
            PlanDetailPO planDetailPO = new PlanDetailPO();
            BeanUtils.copyProperties(planDetailCreateDTO, planDetailPO, "id");
            planDetailPO.setPlanId(planId);
            create(planDetailPO);
        }
        return planId;
    }
}
