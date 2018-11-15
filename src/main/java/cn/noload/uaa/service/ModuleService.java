package cn.noload.uaa.service;

import cn.noload.uaa.domain.Module;
import cn.noload.uaa.repository.ModuleRepository;
import cn.noload.uaa.service.dto.ModuleDTO;
import cn.noload.uaa.service.mapper.ModuleMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
/**
 * Service Implementation for managing Module.
 */
@Service
@Transactional
public class ModuleService {

    private final Logger log = LoggerFactory.getLogger(ModuleService.class);

    private final ModuleRepository moduleRepository;

    private final ModuleMapper moduleMapper;

    public ModuleService(ModuleRepository moduleRepository, ModuleMapper moduleMapper) {
        this.moduleRepository = moduleRepository;
        this.moduleMapper = moduleMapper;
    }

    /**
     * Save a module.
     *
     * @param moduleDTO the entity to save
     * @return the persisted entity
     */
    public ModuleDTO save(ModuleDTO moduleDTO) {
        log.debug("Request to save Module : {}", moduleDTO);
        Module module = moduleMapper.toEntity(moduleDTO);
        module = moduleRepository.save(module);
        return moduleMapper.toDto(module);
    }

    /**
     * Get all the modules.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ModuleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Modules");
        return moduleRepository.findAll(pageable)
            .map(moduleMapper::toDto);
    }


    /**
     * Get one module by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<ModuleDTO> findOne(Long id) {
        log.debug("Request to get Module : {}", id);
        return moduleRepository.findById(id)
            .map(moduleMapper::toDto);
    }

    /**
     * Delete the module by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Module : {}", id);
        moduleRepository.deleteById(id);
    }


    public static void main(String[] args) throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer("transaction");
        producer.setNamesrvAddr("10.0.0.203:9876");
        producer.setTransactionListener(new Listener());
        producer.start();
        TransactionSendResult result = producer.sendMessageInTransaction(new Message("transaction", "消息测试".getBytes()), null);
        result.getSendStatus();
        result.getMsgId();
    }

    static class Listener implements TransactionListener {

        @Override
        public LocalTransactionState executeLocalTransaction(Message message, Object o) {
            System.out.println(message.getTransactionId());
            return LocalTransactionState.COMMIT_MESSAGE;
        }

        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
            return null;
        }
    }
}
