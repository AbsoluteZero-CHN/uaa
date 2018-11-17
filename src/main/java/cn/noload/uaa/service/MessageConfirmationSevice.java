package cn.noload.uaa.service;


import cn.noload.uaa.domain.MessageConfirmation;
import cn.noload.uaa.repository.MessageConfirmationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class MessageConfirmationSevice {

    private final MessageConfirmationRepository messageConfirmationRepository;

    public MessageConfirmationSevice(MessageConfirmationRepository messageConfirmationRepository) {
        this.messageConfirmationRepository = messageConfirmationRepository;
    }

    public MessageConfirmation save(MessageConfirmation messageConfirmation) {
        return messageConfirmationRepository.save(messageConfirmation);
    }

    @Transactional(readOnly = true)
    public Optional<MessageConfirmation> findByMsgId(String msgId) {
        return messageConfirmationRepository.findByMsgId(msgId);
    }

    public void updateStatus(String msgId) {
        messageConfirmationRepository.updateStatus(msgId);
    }
}
