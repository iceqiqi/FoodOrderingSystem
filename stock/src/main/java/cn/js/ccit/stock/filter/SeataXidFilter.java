package cn.js.ccit.stock.filter;

import io.seata.core.context.RootContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Seata 全局事务 XID 绑定过滤器：从请求头读取 XID 并绑定到当前线程，
 * 使库存服务扣减操作作为分支事务参与全局事务，结束后清理线程上下文。
 */
@Component
public class SeataXidFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String xid = request.getHeader(RootContext.KEY_XID);
        if (StringUtils.hasText(xid)) {
            RootContext.bind(xid);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            String currentXid = RootContext.getXID();
            if (currentXid != null) {
                RootContext.unbind();
            }
        }
    }
}
