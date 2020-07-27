package com.fiveamazon.erp.security.entity;


import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Entity
@Data
@Table(name = "tbl_mgt_user")
public class SimpleUser extends SimpleCommonEntity {
    String status;
    String userName;
    String nickName;
    String password;
    @Column(name="pwdChgDate")
    Date passwordChangeDate;
    String email;
//    Set<SimpleGroup> simpleGroups;
//
//    @ManyToMany
//    @JoinTable(name = "tbl_mgt_group_user", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
//    public Set<SimpleGroup> getSimpleGroups() {
//        return simpleGroups;
//    }
//
//    public void setSimpleGroups(Set<SimpleGroup> simpleGroups) {
//        this.simpleGroups = simpleGroups;
//    }
}
