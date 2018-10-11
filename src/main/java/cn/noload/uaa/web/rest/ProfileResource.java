package cn.noload.uaa.web.rest;

import cn.noload.uaa.domain.ApplicationConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileResource {

    @GetMapping("/app")
    public ResponseEntity getApplication() {
        ApplicationConfig app = new ApplicationConfig();
        app.setName("空载");
        app.setDescription("一个自己写着玩的个人项目");
        return ResponseEntity.ok().body(app);
    }
}
