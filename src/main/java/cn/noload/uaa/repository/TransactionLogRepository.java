package cn.noload.uaa.repository;

import cn.noload.uaa.domain.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Menu entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, String> {
}
