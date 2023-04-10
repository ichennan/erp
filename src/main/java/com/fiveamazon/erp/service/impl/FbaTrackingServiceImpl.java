package com.fiveamazon.erp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.fiveamazon.erp.dto.FbaTrackingBatchCreateDTO;
import com.fiveamazon.erp.dto.FbaTrackingBatchUpdateDTO;
import com.fiveamazon.erp.entity.FbaTrackingPO;
import com.fiveamazon.erp.repository.FbaTrackingRepository;
import com.fiveamazon.erp.service.FbaTrackingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class FbaTrackingServiceImpl implements FbaTrackingService {
    @Autowired
    private FbaTrackingRepository theRepository;


    public FbaTrackingPO save(FbaTrackingPO item) {
        if(StringUtils.isBlank(item.getDateSent())){
            item.setDateSent("");
        }
        if(StringUtils.isBlank(item.getShipper())){
            item.setShipper("-");
        }
        if(StringUtils.isBlank(item.getTrackingNo())){
            item.setTrackingNo("-");
        }
        if(null == item.getUnitPrice()){
            item.setUnitPrice(new BigDecimal(0));
        }
        if(null == item.getIsMainTrackingNo()){
            item.setIsMainTrackingNo(0);
        }
        return theRepository.save(item);
    }

    @Override
    public FbaTrackingPO getById(Integer id) {
        return theRepository.getById(id);
    }

    @Override
    public List<FbaTrackingPO> findAll() {
        return theRepository.findAll();
    }

    @Override
    public void batchCreate(FbaTrackingBatchCreateDTO dto) {
        log.info("FbaTrackingServiceImpl.create [{}]", dto);
        Date today = new Date();
        Integer boxCount = dto.getBoxCount();
        String fbaNo = dto.getFbaNo();
        for(Integer i = 1; i <= boxCount; i++){
            String boxLabelStr = StrUtil.padPre(i.toString(), 6, "0");
            FbaTrackingPO item = new FbaTrackingPO();
            item.setStoreId(dto.getStoreId());
            item.setFbaBoxLabel(fbaNo + "U" + boxLabelStr);
            item.setCreateDate(today);
            item.setCreateUser(dto.getUsername());
            item.setUpdateDate(today);
            item.setUpdateUser(dto.getUsername());
            log.info("item [{}]", item);
            save(item);
        }
    }

    @Override
    public void batchUpdate(FbaTrackingBatchUpdateDTO dto) {
        log.info("FbaTrackingServiceImpl.update [{}]", dto);
        Date today = new Date();
        String trakcingNo = dto.getTrackingNo();
        Boolean isDeleteAction = "delete".equals(dto.getAction());
        Integer i = 0;
        for(Integer id : dto.getIds()){
            if(isDeleteAction){
                theRepository.deleteById(id);
                continue;
            }
            FbaTrackingPO item = theRepository.getById(id);
            if(StringUtils.isBlank(trakcingNo)){
                item.setRoute(dto.getRoute());
                item.setShipper(dto.getShipper());
                item.setUnitPrice(dto.getUnitPrice());
                item.setDateSent(dto.getDateSent());
                item.setRemark(dto.getRemark());
            }else{
                String trackingNoSeq = i == 0 ? "" : ("_" + i);
                item.setTrackingNo(dto.getTrackingNo() + trackingNoSeq);
            }
            item.setUpdateDate(today);
            item.setUpdateUser(dto.getUsername());
            save(item);
            i++;
        }
    }
}
