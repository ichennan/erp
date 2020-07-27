package com.fiveamazon.erp.dto;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonView;
import com.fiveamazon.erp.entity.PacketDetailPO;
import com.fiveamazon.erp.entity.PacketPO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PacketDetailViewDTO extends SimpleCommonView {
    Integer packetId;
    Integer productId;
    Integer quantity;
    //
    String deliveryDate;

    public PacketDetailViewDTO(PacketDetailPO packetDetailPO, PacketPO packetPO){
        super();
        BeanUtils.copyProperties(packetPO, this, "id");
        BeanUtils.copyProperties(packetDetailPO, this);
    }

    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
