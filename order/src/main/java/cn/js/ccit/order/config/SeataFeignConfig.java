package cn.js.ccit.order.config;

import feign.RequestInterceptor;
import io.seata.core.context.RootContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Seata 全局事务 XID 透传：OpenFeign 调用库存服务时，把当前线程的 XID 放入请求头，
 * 使库存服务能够作为分支事务加入全局事务。
 */
@Configuration
public class SeataFeignConfig {

    @Bean
    public RequestInterceptor seataRequestInterceptor() {
        return template -> {
            String xid = RootContext.getXID();
            if (StringUtils.hasText(xid)) {
                template.header(RootContext.KEY_XID, xid);
            }
        };
    }
}
