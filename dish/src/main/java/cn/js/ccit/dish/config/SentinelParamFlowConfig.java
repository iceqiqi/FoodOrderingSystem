package cn.js.ccit.dish.config;

import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Collections;

/**
 * Sentinel 热点参数限流：对高频访问的菜品（菜品 id 作为热点参数）进行精准限流。
 */
@Configuration
public class SentinelParamFlowConfig {

    @PostConstruct
    public void initParamFlowRules() {
        ParamFlowRule rule = new ParamFlowRule();
        rule.setResource("getDishDetail"); // 与 @SentinelResource 的 value 一致
        rule.setParamIdx(0);               // 第 0 个参数：菜品 id
        rule.setCount(10);                 // 单个热点参数的 QPS 阈值（默认按 QPS 限流）
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
    }
}
