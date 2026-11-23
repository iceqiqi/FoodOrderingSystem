package cn.js.ccit.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient.Builder 配置。
 * <p>
 * 说明：Spring Boot Admin 客户端、Zipkin 等组件默认按类型注入 WebClient.Builder，
 * 若直接把带负载均衡的 Builder 声明为唯一 Bean，会被这些组件误用，
 * 导致它们把 http://127.0.0.1:9090 之类的直连地址当成“服务名”去做负载均衡而报 503。
 * 因此这里：
 * 1. 提供一个普通（@Primary）的 Builder 作为默认注入；
 * 2. 负载均衡的 Builder 以具名 Bean 形式提供，仅供网关鉴权过滤器按服务名调用下游。
 */
@Configuration
public class WebClientConfig {

    /** 默认 WebClient.Builder（普通、不负载均衡），供 Spring Boot Admin 等默认注入使用。 */
    @Bean
    @Primary
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /** 具备负载均衡能力的 Builder，仅供 AuthGlobalFilter 按服务名（http://user/...）调用。 */
    @Bean("loadBalancedBuilder")
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
