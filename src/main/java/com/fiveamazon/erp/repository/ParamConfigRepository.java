package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.ParamConfigPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ParamConfigRepository extends JpaRepository<ParamConfigPO, Integer> {
    ParamConfigPO getById(Integer id);

    List<ParamConfigPO> findAllByOrderByParamCategoryAscParamTypeAscParamSeqAscParamValueAsc();

    @Query("select distinct(p.paramCategory) from ParamConfigPO p order by p.paramCategory")
    List<String> findParamCategoryList();

    @Query("select p.paramValue from ParamConfigPO p where p.paramCategory = :paramCategory order by p.paramSeq, p.paramType, p.paramValue, p.id ")
    List<String> findListByCategory(String paramCategory);
}
