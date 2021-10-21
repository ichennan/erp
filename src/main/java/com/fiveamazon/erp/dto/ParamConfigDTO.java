package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

@Data
public class ParamConfigDTO extends SimpleCommonDTO {
    String paramCategory;
    String paramType;
    Integer paramSeq;
    String paramValue;
    String remark1;
    String remark2;
    String remark3;
    String remark4;
    String remark5;
}
