package cn.noload.uaa.web.rest;


import cn.noload.uaa.domain.AmountTest;
import cn.noload.uaa.service.AmountTestService;
import com.codahale.metrics.annotation.Timed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AmountTestResource {

    private final AmountTestService amountTestService;

    public AmountTestResource(AmountTestService amountTestService) {
        this.amountTestService = amountTestService;
    }

    @Timed
    @PutMapping("/amount-test/{id}")
    public ResponseEntity<AmountTest> createAmount(
        @PathVariable("id") String id,
        Double amount
    ) {
        AmountTest amountTest = amountTestService.save(id, amount);
        return ResponseEntity.ok(amountTest);
    }
}
