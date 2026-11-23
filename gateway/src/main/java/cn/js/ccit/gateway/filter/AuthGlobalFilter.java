package cn.js.ccit.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 网关统一鉴权过滤器：
 * 1. 白名单（/oauth/**、/user/login、/actuator/**）直接放行；
 * 2. 其余请求校验 Authorization: Bearer <token>；
 * 3. 通过调用认证服务器 /oauth/check_token 校验令牌；
 * 4. 将用户名与角色透传给下游（X-User-Name / X-User-Roles），并区分 ADMIN/USER 角色。
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> WHITELIST = Arrays.asList(
            "/oauth/**",
            "/user/login",
            "/actuator/**"
    );

    /** 需要管理员角色的路径（可按需扩展） */
    private static final List<String> ADMIN_PATHS = Arrays.asList(
            "/admin/**"
    );

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    public AuthGlobalFilter(@Qualifier("loadBalancedBuilder") WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return unauthorized(exchange, "缺少访问令牌");
        }

        String token = auth.substring(7);
        return checkToken(token).flatMap(authInfo -> {
            if (authInfo == null) {
                return unauthorized(exchange, "令牌无效或已过期");
            }
            if (isAdminPath(path) && !authInfo.roles.contains("ROLE_ADMIN")) {
                return forbidden(exchange, "无管理员权限");
            }
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-User-Name", authInfo.username)
                    .header("X-User-Roles", String.join(",", authInfo.roles))
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        });
    }

    private boolean isWhitelisted(String path) {
        return matchAny(WHITELIST, path);
    }

    private boolean isAdminPath(String path) {
        return matchAny(ADMIN_PATHS, path);
    }

    private boolean matchAny(List<String> patterns, String path) {
        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private Mono<AuthInfo> checkToken(String token) {
        String uri = UriComponentsBuilder.fromUriString("http://user/oauth/check_token")
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();
        return webClient.get()
                .uri(uri)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).map(this::parseAuthInfo);
                    }
                    return Mono.empty();
                })
                .onErrorResume(e -> Mono.empty());
    }

    private AuthInfo parseAuthInfo(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            String username = root.path("user_name").asText("");
            List<String> roles = new ArrayList<>();
            JsonNode authorities = root.path("authorities");
            if (authorities.isArray()) {
                authorities.forEach(n -> roles.add(n.asText()));
            }
            return new AuthInfo(username, roles);
        } catch (Exception e) {
            return null;
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        return writeError(exchange, HttpStatus.UNAUTHORIZED, msg);
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String msg) {
        return writeError(exchange, HttpStatus.FORBIDDEN, msg);
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String msg) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"code\":" + status.value() + ",\"msg\":\"" + msg + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private static class AuthInfo {
        final String username;
        final List<String> roles;

        AuthInfo(String username, List<String> roles) {
            this.username = username;
            this.roles = roles;
        }
    }
}
