package cn.noload.uaa.repository;

import cn.noload.uaa.domain.AmountTest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface AmountTestRepository extends JpaRepository<AmountTest, String> {
}
