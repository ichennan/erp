package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderDetailPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderPO;
import lombok.Data;

import java.util.List;

@Data
public class UploadSupplierDeliveryDTO extends SimpleCommonDTO {
    String test;
    List<ExcelSupplierDeliveryOrderDetailPO> orderDetailArray;
    List<ExcelSupplierDeliveryOrderPO> orderArray;
}
