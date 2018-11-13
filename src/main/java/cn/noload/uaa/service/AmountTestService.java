package cn.noload.uaa.service;


import cn.noload.uaa.config.transaction.DistributedTransaction;
import cn.noload.uaa.domain.AmountTest;
import cn.noload.uaa.repository.AmountTestRepository;
import cn.noload.uaa.repository.MenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AmountTestService {

    private final Logger log = LoggerFactory.getLogger(AmountTestService.class);

    private final AmountTestRepository amountTestRepository;
    private final MenuRepository menuRepository;

    public AmountTestService(AmountTestRepository amountTestRepository, MenuRepository menuRepository) {
        this.amountTestRepository = amountTestRepository;
        this.menuRepository = menuRepository;
    }

    @DistributedTransaction(value = DistributedTransaction.Busness.TEST)
    public AmountTest save(String id, Double amount) throws Exception {
        AmountTest amountTest = amountTestRepository.getOne(id);
        amountTest.setAmount(amountTest.getAmount() + amount);
        if(true) {
            throw new Exception("事务回滚测试");
        }
        return amountTestRepository.save(amountTest);
    }
}
