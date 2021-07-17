package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.util.List;

@Data
public class OverseaBatchInsertDTO extends SimpleCommonDTO {
    OverseaDTO item;
    List<OverseaDetailDTO> array;
}
