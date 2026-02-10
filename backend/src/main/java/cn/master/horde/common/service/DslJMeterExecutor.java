package cn.master.horde.common.service;

import cn.master.horde.common.util.JsonHelper;
import cn.master.horde.model.dto.api.request.DslExecuteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.http.DslHttpSampler;

import java.util.Map;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

/**
 * @author : 11's papa
 * @since : 2026/2/6, 星期五
 **/
@Slf4j
@Component
public class DslJMeterExecutor {


    public void execute(DslExecuteRequest request) {
        try {
            DslHttpSampler httpSampler = httpSampler(request.url()).method(request.method());
            request.headers().forEach(httpSampler::header);
            httpSampler.body(request.body());
            TestPlanStats stats = testPlan(
                    threadGroup(1, 1, httpSampler
                            .children(jsr223PostProcessor(s -> {
                                String response = s.prev.getResponseDataAsString();
                                // 解析响应
                                // 将响应转换为Map
                                Map<String, Object> apply = JsonHelper.stringToObject(new TypeReference<Map<String, Object>>() {
                                }).apply(response); // 解析响应
                                // 获取data字段
                                Map<String, Object> data = JsonHelper.toObject(new TypeReference<Map<String, Object>>() {
                                }).apply(apply.get("data")); // 获取data字段
                                data.forEach((k, v) -> log.info("{}: {}", k, v));
                            }))
                    )
                    // influxDbListener("http://172.16.2.10:8086/write?db=jmeter")
                    // responseFileSaver(Instant.now().toString().replace(":", "-") + "-response")
            ).run(); // 同步执行
            // assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
            System.out.println(stats.overall());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
