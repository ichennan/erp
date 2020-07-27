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
@Table(name = "tbl_mgt_authority")
public class SimpleAuthority extends SimpleCommonEntity {
    String status;
    String authorityName;
    String description;
}
