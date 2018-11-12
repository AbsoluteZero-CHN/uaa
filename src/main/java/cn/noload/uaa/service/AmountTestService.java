package cn.noload.uaa.service;


import cn.noload.uaa.config.transaction.DistributedTransaction;
import cn.noload.uaa.domain.AmountTest;
import cn.noload.uaa.repository.AmountTestRespository;
import cn.noload.uaa.repository.MenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AmountTestService {

    private final Logger log = LoggerFactory.getLogger(AmountTestService.class);

    private final AmountTestRespository amountTestRespository;

    public AmountTestService(AmountTestRespository amountTestRespository) {
        this.amountTestRespository = amountTestRespository;
    }

    @DistributedTransaction(value = DistributedTransaction.Busness.TEST)
    public AmountTest save(String id, Double amount) {
        AmountTest amountTest = amountTestRespository.getOne(id);
        amountTest.setAmount(amountTest.getAmount() + amount);
        return amountTestRespository.save(amountTest);
    }
}
