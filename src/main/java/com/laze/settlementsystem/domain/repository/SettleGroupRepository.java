package com.laze.settlementsystem.domain.repository;

import com.laze.settlementsystem.domain.SettleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SettleGroupRepository extends JpaRepository<SettleGroup, Long> {
    @Query(
            value = """
                    SELECT new settlegroup (detail.customerId, detail.serviceId, sum(detail.count), sum(detail.fee))
                    FROM settledetail detail
                    WHERE detail.targetDate between :start and :end
                    AND detail.customerId = :customerId
                    GROUP BY detail.customerId, detail.serviceId
                    """
    )
    List<SettleGroup> findGroupByCustomerIdAndServiceId(LocalDate start, LocalDate end, Long customerId);
}
