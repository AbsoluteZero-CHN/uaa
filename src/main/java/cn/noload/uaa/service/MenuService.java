package cn.noload.uaa.service;

import cn.noload.uaa.config.transaction.DistributedTransaction;
import cn.noload.uaa.domain.Menu;
import cn.noload.uaa.repository.MenuRepository;
import cn.noload.uaa.service.dto.MenuDTO;
import cn.noload.uaa.service.mapper.MenuMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Menu.
 */
@Service
@Transactional
public class MenuService {

    private final Logger log = LoggerFactory.getLogger(MenuService.class);

    private final MenuRepository menuRepository;

    private final MenuMapper menuMapper;
    private final PlatformTransactionManager platformTransactionManager;
    private final ThreadLocal threadLocal;

    public MenuService(MenuRepository menuRepository, MenuMapper menuMapper, PlatformTransactionManager platformTransactionManager, @Qualifier("transactionKey") ThreadLocal threadLocal) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
        this.platformTransactionManager = platformTransactionManager;
        this.threadLocal = threadLocal;
    }

    /**
     * Save a menu.
     *
     * @param menuDTO the entity to save
     * @return the persisted entity
     */
    @DistributedTransaction(values = "menu")
    public MenuDTO save(MenuDTO menuDTO) {
        log.debug("Request to save Menu : {}", menuDTO);
        System.out.println(threadLocal.get());
        Menu menu = menuMapper.toEntity(menuDTO);
        menu = menuRepository.save(menu);
        return menuMapper.toDto(menu);
    }

    /**
     * Get all the menus.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MenuDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Menus");
        return menuRepository.findAll(pageable)
            .map(menuMapper::toDto);
    }


    /**
     * Get one menu by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<MenuDTO> findOne(String id) {
        log.debug("Request to get Menu : {}", id);
        return menuRepository.findById(id)
            .map(menuMapper::toDto);
    }

    /**
     * Delete the menu by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Menu : {}", id);
        menuRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MenuDTO> getMenuTree() {
        List<Menu> menuList = menuRepository.findAllByActivatedIsTrueAndParentIsNull();
        return menuList
            .stream().map((menu) -> {
                menu.getChildren().forEach((child) -> {
                    child.setParent(null);
                });
                return menuMapper.toDto(menu);
            }).collect(Collectors.toList());
    }
}
