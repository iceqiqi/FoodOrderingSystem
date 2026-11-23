package cn.js.ccit.order.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sentinel 规则：下单接口的流控规则 + 熔断降级规则。
 */
@Configuration
public class SentinelRuleConfig {

    @PostConstruct
    public void initRules() {
        initFlowRules();
        initDegradeRules();
    }

    private void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("/order/create");           // 资源名，与 @SentinelResource 的 value 一致
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);  // 按 QPS 限流
        rule.setCount(5);                            // 每秒最多放行 5 个请求
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    private void initDegradeRules() {
        DegradeRule rule = new DegradeRule();
        rule.setResource("/order/create");
        rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO); // 按异常比例熔断
        rule.setCount(0.5);       // 异常比例阈值 50%
        rule.setMinRequestAmount(10); // 最小请求数
        rule.setTimeWindow(10);   // 熔断时长 10 秒
        DegradeRuleManager.loadRules(Collections.singletonList(rule));
    }
}
