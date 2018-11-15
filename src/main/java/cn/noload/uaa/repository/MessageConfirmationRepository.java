package cn.noload.uaa.repository;

import cn.noload.uaa.domain.MessageConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Menu entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MessageConfirmationRepository extends JpaRepository<MessageConfirmation, String> {

    @Modifying
    @Query("update MessageConfirmation mc set mc.status = 1, mc.updateTime = current_time where mc.msgId = ?1")
    void updateStatus(String msgId);
}
