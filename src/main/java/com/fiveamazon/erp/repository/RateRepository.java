package com.fiveamazon.erp.repository;

import com.fiveamazon.erp.entity.RatePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface RateRepository extends JpaRepository<RatePO, Integer> {
	RatePO getById(Integer id);
	RatePO getTopByCurrencyAndEffectiveTimeLessThanEqualOrderByEffectiveTimeDesc(String currency, Date date);
}
