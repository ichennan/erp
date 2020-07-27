package com.fiveamazon.erp.service.impl;

import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.ShipmentDTO;
import com.fiveamazon.erp.dto.ShipmentDetailDTO;
import com.fiveamazon.erp.dto.ShipmentDetailViewDTO;
import com.fiveamazon.erp.entity.ShipmentDetailPO;
import com.fiveamazon.erp.entity.ShipmentPO;
import com.fiveamazon.erp.entity.ShipmentViewPO;
import com.fiveamazon.erp.repository.ShipmentDetailRepository;
import com.fiveamazon.erp.repository.ShipmentRepository;
import com.fiveamazon.erp.repository.ShipmentViewRepository;
import com.fiveamazon.erp.service.ShipmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private ShipmentViewRepository shipmentViewRepository;
    @Autowired
    private ShipmentDetailRepository shipmentDetailRepository;

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
        return shipmentRepository.save(shipmentPO);
    }

    @Override
    public ShipmentDetailPO saveDetail(ShipmentDetailPO shipmentDetailPO) {
        return shipmentDetailRepository.save(shipmentDetailPO);
    }

    @Override
    public ShipmentPO save(ShipmentDTO shipmentDTO) {
        Integer shipmentId = shipmentDTO.getId();
        ShipmentPO shipmentPO;
        if(shipmentId == null || shipmentId == 0){
            shipmentPO = new ShipmentPO();
            shipmentPO.setCreateDate(new Date());
            shipmentPO.setCreateUser(shipmentDTO.getUsername());
        }else{
            shipmentPO = getById(shipmentId);
            shipmentPO.setUpdateDate(new Date());
            shipmentPO.setUpdateUser(shipmentDTO.getUsername());
            shipmentDetailRepository.deleteByShipmentIdEqualsAndBoxNotLike(shipmentId, "Plan");
            for(ShipmentDetailDTO shipmentDetailDTO: shipmentDTO.getShipmentDetailList()){
                shipmentDetailDTO.setUsername(shipmentDTO.getUsername());
                saveDetail(shipmentDetailDTO);
            }
        }
        BeanUtils.copyProperties(shipmentDTO, shipmentPO, "id");
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
        return shipmentDetailRepository.findAllByShipmentId(shipmentId);
    }

    @Override
    public List<ShipmentDetailViewDTO> findByProductId(Integer productId) {
        return shipmentDetailRepository.findByProductId(productId);
    }
}
