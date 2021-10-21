package com.fiveamazon.erp.service.impl;

import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.ParamConfigDTO;
import com.fiveamazon.erp.entity.ParamConfigPO;
import com.fiveamazon.erp.repository.ParamConfigRepository;
import com.fiveamazon.erp.service.ParamConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ParamConfigServiceImpl implements ParamConfigService {
    @Autowired
    private ParamConfigRepository theRepository;

    private ParamConfigPO save(ParamConfigPO item) {
        return theRepository.save(item);
    }

    @Override
    public Long countAll() {
        return theRepository.count();
    }

    @Override
    public ParamConfigPO getById(Integer id) {
        return theRepository.getById(id);
    }

    @Override
    public List<ParamConfigPO> findAll() {
        return theRepository.findAllByOrderByParamCategoryAscParamTypeAscParamSeqAscParamValueAsc();
    }

    @Override
    public ParamConfigPO save(ParamConfigDTO dto) {
        Date today = new Date();
        Integer id = dto.getId();
        if(SimpleConstant.ACTION_DELETE.equalsIgnoreCase(dto.getAction())){
            theRepository.deleteById(id);
            return new ParamConfigPO();
        }
        ParamConfigPO item;
        if(id == null || id == 0){
            item = new ParamConfigPO();
            item.setCreateDate(today);
            item.setCreateUser(dto.getUsername());
        }else{
            item = getById(id);
            item.setUpdateDate(today);
            item.setUpdateUser(dto.getUsername());
        }
        BeanUtils.copyProperties(dto, item, "id");
        return save(item);
    }

    @Override
    public List<String> findListByCategory(String paramCategory) {
        return theRepository.findListByCategory(paramCategory);
    }

    @Override
    public List<String> findParamCategoryList() {
        return theRepository.findParamCategoryList();
    }
}
