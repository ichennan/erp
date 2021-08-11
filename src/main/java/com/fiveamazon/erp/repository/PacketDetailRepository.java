package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.dto.PacketDetailViewDTO;
import com.fiveamazon.erp.entity.PacketDetailPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface PacketDetailRepository extends JpaRepository<PacketDetailPO, Integer> {
    PacketDetailPO getById(Integer id);
    List<PacketDetailPO> findAllByPacketId(Integer packetId);
    void deleteByPacketIdEquals(Integer shipmentId);
    @Query("select new com.fiveamazon.erp.dto.PacketDetailViewDTO(pd, p) from PacketDetailPO pd left join PacketPO p on  pd.packetId = p.id where pd.productId = :productId")
    List<PacketDetailViewDTO> findByProductId(Integer productId);
}
