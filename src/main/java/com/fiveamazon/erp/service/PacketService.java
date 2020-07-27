package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.PacketDTO;
import com.fiveamazon.erp.dto.PacketDetailDTO;
import com.fiveamazon.erp.dto.PacketDetailViewDTO;
import com.fiveamazon.erp.entity.PacketDetailPO;
import com.fiveamazon.erp.entity.PacketPO;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface PacketService {
    Long countAll();

    PacketPO getById(Integer id);

    PacketDetailPO getDetailById(Integer id);

    List<PacketPO> findAll();

    PacketPO save(PacketPO purchasePO);

    PacketDetailPO saveDetail(PacketDetailPO packetDetailPO);

    List<PacketDetailPO> findAllDetail(Integer packetId);

    PacketPO save(PacketDTO packetDTO);

    PacketDetailPO saveDetail(PacketDetailDTO packetDetailDTO);

    List<PacketDetailViewDTO> findByProductId(Integer productId);
}
