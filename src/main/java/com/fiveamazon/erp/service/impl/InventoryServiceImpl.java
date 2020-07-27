package com.fiveamazon.erp.service.impl;

import com.fiveamazon.erp.entity.InventorySnapshotPO;
import com.fiveamazon.erp.entity.InventoryVPO;
import com.fiveamazon.erp.repository.InventoryRepository;
import com.fiveamazon.erp.repository.InventorySnapshotRepository;
import com.fiveamazon.erp.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private InventorySnapshotRepository inventorySnapshotRepository;

    @Override
    public Long countAll() {
        return inventoryRepository.count();
    }

    @Override
    public InventoryVPO getById(Integer id) {
        return inventoryRepository.getOne(id);
    }


    @Override
    public List<InventoryVPO> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public List<InventorySnapshotPO> findSnapshotByProductId(Integer productId) {
        return inventorySnapshotRepository.findByProductId(productId);
    }
}
