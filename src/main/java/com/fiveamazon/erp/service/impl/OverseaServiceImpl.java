package com.fiveamazon.erp.service.impl;

import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.OverseaDTO;
import com.fiveamazon.erp.dto.OverseaDetailDTO;
import com.fiveamazon.erp.entity.OverseaDetailPO;
import com.fiveamazon.erp.entity.OverseaPO;
import com.fiveamazon.erp.repository.OverseaDetailRepository;
import com.fiveamazon.erp.repository.OverseaRepository;
import com.fiveamazon.erp.service.OverseaService;
import com.fiveamazon.erp.service.ProductService;
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
public class OverseaServiceImpl implements OverseaService {
    @Autowired
    private OverseaRepository theRepository;
    @Autowired
    private OverseaDetailRepository theDetailRepository;
    @Autowired
    private ProductService productService;

    @Override
    public Long countAll() {
        return theRepository.count();
    }

    @Override
    public OverseaPO getById(Integer id) {
        return theRepository.getById(id);
    }

    @Override
    public OverseaDetailPO getDetailById(Integer id) {
        return theDetailRepository.getOne(id);
    }

    @Override
    public List<OverseaPO> findAll() {
        return theRepository.findAll();
    }

    @Override
    public List<OverseaDetailPO> findAllDetail(Integer overseaId) {
        return theDetailRepository.findByOverseaId(overseaId);
    }

    public OverseaPO save(OverseaPO item) {
        return theRepository.save(item);
    }

    @Override
    public OverseaPO save(OverseaDTO dto) {
        Date today = new Date();
        Integer id = dto.getId();
        if(SimpleConstant.ACTION_DELETE.equalsIgnoreCase(dto.getAction())){
            theDetailRepository.deleteByOverseaId(id);
            theRepository.deleteById(id);
            return new OverseaPO();
        }
        OverseaPO item;
        if(id == null || id == 0){
            item = new OverseaPO();
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

    private OverseaDetailPO saveDetail(OverseaDetailPO item){
        return theDetailRepository.save(item);
    }

    @Override
    public OverseaDetailPO saveDetail(OverseaDetailDTO dto) {
        Date today = new Date();
        Integer id = dto.getId();
        if(SimpleConstant.ACTION_DELETE.equalsIgnoreCase(dto.getAction())){
            theDetailRepository.deleteById(id);
            return null;
        }
        OverseaDetailPO item;
        if(id == null || id == 0){
            item = new OverseaDetailPO();
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
