package cn.master.horde.controller;

import cn.master.horde.common.service.DslJMeterExecutor;
import cn.master.horde.model.dto.api.request.DslExecuteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 11's papa
 * @since : 2026/2/6, 星期五
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/dsl")
public class DslController {
    private final DslJMeterExecutor executor;

    @PostMapping("/execute")
    public void execute(@RequestBody DslExecuteRequest request) {
        executor.execute(request);
    }

}
