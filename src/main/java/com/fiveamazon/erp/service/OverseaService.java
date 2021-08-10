package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.OverseaBatchInsertDTO;
import com.fiveamazon.erp.dto.OverseaDTO;
import com.fiveamazon.erp.dto.OverseaDetailDTO;
import com.fiveamazon.erp.entity.OverseaDetailPO;
import com.fiveamazon.erp.entity.OverseaPO;
import com.fiveamazon.erp.entity.OverseaViewPO;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface OverseaService {
    Long countAll();

    OverseaPO getById(Integer id);

    OverseaDetailPO getDetailById(Integer id);

    List<OverseaViewPO> findAll();

    OverseaPO save(OverseaDTO dto);

    OverseaDetailPO saveDetail(OverseaDetailDTO dto, Boolean refreshSku);

    OverseaDetailPO saveFba(OverseaDetailDTO dto);

    List<OverseaDetailPO> findAllDetail(Integer overseaId);

    void batchInsert(OverseaBatchInsertDTO dto);

    List<OverseaPO> findByDate(String dateFrom, String dateTo);
}
