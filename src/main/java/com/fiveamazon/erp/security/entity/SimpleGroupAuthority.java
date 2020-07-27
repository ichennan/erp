package com.fiveamazon.erp.security.entity;


import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Entity
@Data
@Table(name = "tbl_mgt_group_authority")
public class SimpleGroupAuthority extends SimpleCommonEntity {
    String status;
    Integer groupId;
    Integer authorityId;
//    SimpleAuthority simpleAuthority;
//
//    @OneToOne(targetEntity = SimpleAuthority.class)
//    @JoinColumn(name = "authority_id",referencedColumnName = "id", insertable = false, updatable = false)
//    public SimpleAuthority getSimpleAuthority() {
//        return simpleAuthority;
//    }
//
//    public void setSimpleAuthority(SimpleAuthority simpleAuthority) {
//        this.simpleAuthority = simpleAuthority;
//    }
}
