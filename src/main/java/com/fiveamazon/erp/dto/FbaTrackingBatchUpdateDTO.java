package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FbaTrackingBatchUpdateDTO extends SimpleCommonDTO {
    String shipper;
    String route;
    String dateSent;
    String trackingNo;
    BigDecimal unitPrice;
    String remark;
    List<Integer> ids;
}
