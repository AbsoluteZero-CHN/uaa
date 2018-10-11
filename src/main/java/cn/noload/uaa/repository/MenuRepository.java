package cn.noload.uaa.repository;

import cn.noload.uaa.domain.Menu;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Menu entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {


    List<Menu> findAllByActivatedIsTrueAndParentIsNull();
}
