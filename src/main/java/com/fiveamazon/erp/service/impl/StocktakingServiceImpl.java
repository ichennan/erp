package com.fiveamazon.erp.service.impl;

import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.StocktakingDTO;
import com.fiveamazon.erp.dto.StocktakingDetailDTO;
import com.fiveamazon.erp.entity.StocktakingDetailPO;
import com.fiveamazon.erp.entity.StocktakingPO;
import com.fiveamazon.erp.entity.StocktakingViewPO;
import com.fiveamazon.erp.repository.StocktakingDetailRepository;
import com.fiveamazon.erp.repository.StocktakingRepository;
import com.fiveamazon.erp.repository.StocktakingViewRepository;
import com.fiveamazon.erp.service.StocktakingService;
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
public class StocktakingServiceImpl implements StocktakingService {
    @Autowired
    private StocktakingRepository theRepository;
    @Autowired
    private StocktakingViewRepository theViewRepository;
    @Autowired
    private StocktakingDetailRepository theDetailRepository;

    @Override
    public Long countAll() {
        return theRepository.count();
    }

    @Override
    public StocktakingPO getById(Integer id) {
        return theRepository.getById(id);
    }

    @Override
    public StocktakingDetailPO getDetailById(Integer id) {
        return theDetailRepository.getById(id);
    }

    @Override
    public List<StocktakingViewPO> findAll() {
        return theViewRepository.findAll();
    }

    @Override
    public List<StocktakingDetailPO> findAllDetail(Integer id) {
        return theDetailRepository.findByStocktakingId(id);
    }

    public StocktakingPO save(StocktakingPO item) {
        return theRepository.save(item);
    }

    @Override
    public StocktakingPO save(StocktakingDTO dto) {
        Date today = new Date();
        Integer id = dto.getId();
        if(SimpleConstant.ACTION_DELETE.equalsIgnoreCase(dto.getAction())){
            theDetailRepository.deleteByStocktakingId(id);
            theRepository.deleteById(id);
            return new StocktakingPO();
        }
        StocktakingPO item;
        if(id == null || id == 0){
            item = new StocktakingPO();
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

    private StocktakingDetailPO saveDetail(StocktakingDetailPO item){
        if(null == item.getStocktakingQuantity()){
            item.setStocktakingQuantity(0);
        }
        if(null == item.getCalculatedQuantity()){
            item.setStocktakingQuantity(0);
        }
        if(null == item.getAdjustmentQuantity()){
            item.setAdjustmentQuantity(0);
        }
        return theDetailRepository.save(item);
    }

    @Override
    public StocktakingDetailPO saveDetail(StocktakingDetailDTO dto) {
        Date today = new Date();
        Integer id = dto.getId();
        if(SimpleConstant.ACTION_DELETE.equalsIgnoreCase(dto.getAction())){
            theDetailRepository.deleteById(id);
            return null;
        }
        StocktakingDetailPO item;
        if(id == null || id == 0){
            item = new StocktakingDetailPO();
            item.setCreateDate(today);
            item.setCreateUser(dto.getUsername());
        }else{
            item = getDetailById(id);
            item.setUpdateDate(today);
            item.setUpdateUser(dto.getUsername());
        }
        BeanUtils.copyProperties(dto, item, "id");
        return saveDetail(item);
    }
}
