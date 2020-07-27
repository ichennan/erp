package com.fiveamazon.erp.common;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;


/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@MappedSuperclass
@Data
public class SimpleCommonEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String createUser;
    String updateUser;
    Date createDate;
    Date updateDate;
    String remark;
}
