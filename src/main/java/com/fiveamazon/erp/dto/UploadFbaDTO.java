package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import com.fiveamazon.erp.entity.ExcelFbaPackListPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderDetailPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderPO;
import lombok.Data;

import java.util.List;

@Data
public class UploadFbaDTO extends SimpleCommonDTO {
    String fileName;
    String status;
    String fbaName;
    String shipmentId;
    String planId;
    String shipTo;
    Integer boxCount;
    List<ExcelFbaPackListPO> array;
}
