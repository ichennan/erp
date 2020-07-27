package com.fiveamazon.erp.service.impl;

import com.fiveamazon.erp.dto.PacketDTO;
import com.fiveamazon.erp.dto.PacketDetailDTO;
import com.fiveamazon.erp.dto.PacketDetailViewDTO;
import com.fiveamazon.erp.entity.PacketDetailPO;
import com.fiveamazon.erp.entity.PacketPO;
import com.fiveamazon.erp.repository.PacketDetailRepository;
import com.fiveamazon.erp.repository.PacketRepository;
import com.fiveamazon.erp.service.PacketService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PacketServiceImpl implements PacketService {
    @Autowired
    private PacketRepository packetRepository;
    @Autowired
    private PacketDetailRepository packetDetailRepository;

    @Override
    public Long countAll() {
        return packetRepository.count();
    }

    @Override
    public PacketPO getById(Integer id) {
        return packetRepository.getOne(id);
    }

    @Override
    public PacketDetailPO getDetailById(Integer id) {
        return packetDetailRepository.getOne(id);
    }

    @Override
    public List<PacketPO> findAll() {
        return packetRepository.findAll();
    }

    @Override
    public PacketPO save(PacketPO packetPO) {
        return packetRepository.save(packetPO);
    }

    @Override
    public PacketPO save(PacketDTO packetDTO) {
        Integer packetId = packetDTO.getId();
        PacketPO packetPO;
        if(packetId == null || packetId == 0){
            packetPO = new PacketPO();
            packetPO.setCreateDate(new Date());
            packetPO.setCreateUser(packetDTO.getUsername());
        }else{
            packetPO = getById(packetId);
            packetPO.setUpdateDate(new Date());
            packetPO.setUpdateUser(packetDTO.getUsername());
        }
        BeanUtils.copyProperties(packetDTO, packetPO, "id");
        save(packetPO);
        packetDetailRepository.deleteByPacketIdEquals(packetId);
        for(PacketDetailDTO packetDetailDTO: packetDTO.getPacketDetailDTOList()){
            packetDetailDTO.setUsername(packetDTO.getUsername());
            packetDetailDTO.setPacketId(packetPO.getId());
            saveDetail(packetDetailDTO);
        }
        return packetPO;
    }

    @Override
    public PacketDetailPO saveDetail(PacketDetailPO packetDetailPO) {
        return packetDetailRepository.save(packetDetailPO);
    }

    @Override
    public PacketDetailPO saveDetail(PacketDetailDTO packetDetailDTO) {
        Integer packetDetailId = packetDetailDTO.getId();
        PacketDetailPO packetDetailPO;
        if(packetDetailId == null || packetDetailId == 0){
            packetDetailPO = new PacketDetailPO();
            packetDetailPO.setCreateDate(new Date());
            packetDetailPO.setCreateUser(packetDetailDTO.getUsername());
        }else{
            packetDetailPO = getDetailById(packetDetailId);
            packetDetailPO.setUpdateDate(new Date());
            packetDetailPO.setUpdateUser(packetDetailDTO.getUsername());
        }
        BeanUtils.copyProperties(packetDetailDTO, packetDetailPO, "id");
        return saveDetail(packetDetailPO);
    }

    @Override
    public List<PacketDetailPO> findAllDetail(Integer packetId) {
        return packetDetailRepository.findAllByPacketId(packetId);
    }

    @Override
    public List<PacketDetailViewDTO> findByProductId(Integer productId) {
        return packetDetailRepository.findByProductId(productId);
    }
}
