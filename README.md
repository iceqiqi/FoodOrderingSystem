# 基于 Spring Cloud Alibaba 的校园外卖系统

《微服务架构》课程期末大作业。7 个微服务，覆盖考核点：Nacos 注册/配置、OpenFeign、Gateway 动态路由、OAuth2.0 令牌入库 + 网关鉴权、Seata AT 分布式事务、Sentinel 限流/熔断/热点、Sleuth+Zipkin+Admin 链路监控。

## 1. 技术栈

| 项 | 版本 |
|---|---|
| JDK | 1.8（必须，机器上装了 JDK 8） |
| Spring Boot | 2.6.3 |
| Spring Cloud | 2021.0.1 |
| Spring Cloud Alibaba | 2021.0.1.0 |
| Nacos | 1.4.2（客户端 1.4.2，Docker 编排 server 1.4.2） |
| Seata | 1.4.2 |
| Sentinel | 1.8.3（SCA 内置） |
| MyBatis-Plus / MySQL | 3.5.3.1 / 8.0 |
| Zipkin / Sleuth | 2.23 / 3.1.1 |
| Spring Boot Admin | 2.6.7 |
| Prometheus / Grafana | 2.36.0 / 8.5.2 |
| SkyWalking / ES | 8.9.1 / 7.17.3 |

## 2. 模块与端口

| 模块 | 端口 | 职责 |
|---|---|---|
| gateway | 8080 | 统一网关：动态路由 + OAuth2 令牌校验 |
| user | 8004 | 用户 + OAuth2 认证/授权中心 |
| shop | 8001 | 店铺 |
| dish | 8002 | 菜品（Sentinel 热点参数限流） |
| order | 8003 | 订单（Feign 调用库存、Seata 全局事务、Sentinel 流控/熔断） |
| stock | 8005 | 库存（扣减 + Seata 分支事务） |
| pay | 8006 | 支付 |

## 3. 目录结构

```
demo/
├── pom.xml                 # 父工程（依赖版本统一管理）
├── docker-compose.yml      # Docker 编排（MySQL/Nacos/Seata/Zipkin/Sentinel + 7 服务）
├── database.txt            # 建库建表脚本（可直接复制执行，Docker 会自动加载）
├── deploy/
│   ├── seata/registry.conf                # Seata Server 本地版（Nacos 127.0.0.1）
│   ├── seata/registry-docker.conf         # Seata Server Docker 版（Nacos → nacos:8848）
│   ├── seata/seataServer.properties       # Seata 配置本地版（MySQL 127.0.0.1）
│   ├── seata/seataServer-docker.properties# Seata 配置 Docker 版（MySQL → mysql:3306）
│   ├── prometheus/prometheus.yml          # 7 个 scrape job
│   └── grafana/provisioning/datasources/datasource.yml
├── gateway/ user/ shop/ dish/ order/ stock/ pay/   # 7 个服务（各自含 Dockerfile）
└── common/                  # 公共模块
```

## 4. 环境准备

1. **JDK 8**：`C:\Program Files\Java\jdk1.8.0_241`（机器上已有）。
2. **Maven**：可直接复用 IDEA 自带的 Maven：
   `/c/Program Files/JetBrains/IntelliJ IDEA 2026.3/plugins/maven-plugin/lib/maven3/bin/mvn`
   （或自行安装 Maven 3.6+）。
3. **Docker Desktop**（推荐部署方式）：包含 Docker Compose v2，一键起整套中间件 + 服务。
   若不用 Docker，也可本机直装 Nacos、Seata Server、Zipkin、Sentinel、Spring Boot Admin、
   Prometheus + Grafana、SkyWalking + Elasticsearch（默认地址 `127.0.0.1`，见第 6 节）。

## 5. Docker 一键部署（推荐）

整套编排见 `docker-compose.yml`：**MySQL、Nacos、Seata Server、Zipkin、Sentinel Dashboard + 7 个微服务**，
自动处理建库建表、Seata 配置发布、健康检查与启动顺序。

### 5.1 前置条件

- 已安装并启动 Docker Desktop（命令行 `docker version` 能出结果）。
- 国内网络建议在 Docker Desktop 设置里配一个镜像加速器，避免拉取镜像过慢。

### 5.2 构建与启动

```bash
# ① 在项目根目录先构建一次可执行 jar（联网，跳过测试）
"/c/Program Files/JetBrains/IntelliJ IDEA 2026.3/plugins/maven-plugin/lib/maven3/bin/mvn" \
  -B -DskipTests package

# ② 构建镜像并后台启动整套（首次会拉镜像 + 构建，较慢）
docker compose up -d --build

# ③ 查看状态（全部 Up 即成功）
docker compose ps

# ④ 查看某个服务日志
docker compose logs -f order
```

### 5.3 启动后的访问地址

| 组件 | 地址 | 说明 |
|---|---|---|
| 网关 | http://localhost:8080 | 统一入口 |
| Nacos | http://localhost:8848/nacos | 注册中心（nacos/nacos） |
| Sentinel | http://localhost:8858 | 限流控制台（sentinel/sentinel） |
| Zipkin | http://localhost:9411 | 链路追踪 |
| Seata Server | localhost:8091 | 事务协调器 |
| MySQL | localhost:3306 | root / 123456 |

### 5.4 停止

```bash
docker compose down            # 停止并删除容器（保留数据卷）
docker compose down -v         # 连同 MySQL / Nacos 数据卷一起删除（彻底重置）
```

### 5.5 Docker 配置说明（为什么不用改源码）

源码配置里的地址都是 `127.0.0.1`（便于本地跑），容器内通过 `docker-compose.yml` 的
**环境变量**覆盖成容器名（Spring Boot 松散绑定自动生效），因此源码完全不动：

- `127.0.0.1:8848` → `nacos:8848`（`SPRING_CLOUD_NACOS_CONFIG/DISCOVERY_SERVER_ADDR`）
- `127.0.0.1:3306` → `mysql:3306`（`SPRING_DATASOURCE_URL`）
- `127.0.0.1:9411` → `zipkin:9411`（`SPRING_ZIPKIN_BASE_URL`）
- `127.0.0.1:8858` → `sentinel-dashboard:8858`（`SPRING_CLOUD_SENTINEL_TRANSPORT_DASHBOARD`）
- Seata 的 Nacos 地址 → `nacos:8848`（`SEATA_REGISTRY/CONFIG_NACOS_SERVER_ADDR`，order/stock）

自动化的两件事（无需手动操作）：
1. **建库建表**：`database.txt` 挂载为 MySQL 首次启动的初始化脚本，自动创建 6 个业务库 +
   OAuth2 表 + 两张 `undo_log` + `seata` 库。
2. **Seata 配置**：一次性容器 `seata-config-init` 会把 `seataServer-docker.properties` 自动发布到
   Nacos 的 `SEATA_GROUP` 分组（Data Id `seataServer.properties`），Seata Server 与 order/stock 客户端都从那里读取。

> 说明：SCA 2021.0.1.0 已移除 Seata 模块，因此服务端用独立 Seata 1.4.2；客户端用
> `io.seata:seata-spring-boot-starter`，全局事务 XID 通过 order 的 Feign 拦截器 + stock 的 Servlet 过滤器手工透传。

## 6. 本地手工部署（不用 Docker）

如果不用 Docker，按下面步骤手工起中间件（默认地址均为 `127.0.0.1`）。

### 6.1 数据库初始化

1. 启动 MySQL 8.0（root / 123456）。
2. 打开 `database.txt`，在 Navicat / DataGrip 的 SQL 窗口**整段执行**（每行注释已是标准 `--` 注释，
   可直接复制运行）。脚本幂等（`IF NOT EXISTS` + `INSERT IGNORE`），会创建：
   - `user_db / shop_db / dish_db / stock_db / order_db / pay_db` 六个业务库及测试数据；
   - `order_db`、`stock_db` 两张 `undo_log`（Seata AT 回滚日志）；
   - `user_db` 三张 OAuth2 表 + 客户端 `client/secret`；
   - `seata` 库（`global_table / branch_table / lock_table`，Seata Server db 存储）。

### 6.2 Seata Server 配置（关键）

1. **注册 Seata Server 到 Nacos**：启动 Seata 前，把 `deploy/seata/registry.conf` 放到
   Seata 1.4.2 安装目录的 `conf/` 下（覆盖原文件），它指定注册中心与配置中心都用 Nacos（`SEATA_GROUP` 分组）。
2. **在 Nacos 新建配置**：进入 Nacos 控制台 `http://127.0.0.1:8848` → 配置管理 → 配置列表，
   在 `SEATA_GROUP` 分组下新建配置，Data Id 为 `seataServer.properties`，内容即
   `deploy/seata/seataServer.properties` 全文。其中 `service.vgroupMapping.my_test_tx_group=default`
   与各服务的 `seata.tx-service-group: my_test_tx_group` 保持一致。
3. **启动 Seata Server**：`seata-server.bat`（默认端口 8091）。启动后能在 Nacos 服务列表看到 `seata-server`。

### 6.3 构建

已在本机验证通过：**编译 + 打 jar 全部 9 个模块 `BUILD SUCCESS`**。

```bash
export JAVA_HOME="C:\Program Files\Java\jdk1.8.0_241"

# 编译（跳过测试）
"/c/Program Files/JetBrains/IntelliJ IDEA 2026.3/plugins/maven-plugin/lib/maven3/bin/mvn" \
  -B -DskipTests compile

# 打可执行 jar（跳过测试）
"/c/Program Files/JetBrains/IntelliJ IDEA 2026.3/plugins/maven-plugin/lib/maven3/bin/mvn" \
  -B -DskipTests package
```

各服务 jar 输出在 `{模块}/target/{模块}-1.0-SNAPSHOT.jar`（共 7 个可执行 fat jar + common 薄 jar）。

### 6.4 本地启动（含 SkyWalking 探针）

先确保 Nacos、MySQL、Seata Server、Zipkin 已启动。每个服务带上 SkyWalking agent：

```bash
SKYAGENT="-javaagent:/opt/skywalking-agent/skywalking-agent.jar"

java -jar user/target/user-1.0-SNAPSHOT.jar $SKYAGENT \
  -Dskywalking.agent.service_name=user-service \
  -Dskywalking.collector.backend_service=127.0.0.1:11800
```

其余服务同理，`service_name` 分别换成 `gateway-service / shop-service / dish-service / order-service / stock-service / pay-service`。
（Windows 下 `$SKYAGENT` 写法改为把 `-javaagent:...` 直接写在命令里即可。）

启动顺序建议：user → shop/dish/stock/pay → order → gateway（网关最后，避免启动时路由不到）。

## 7. 测试接口

**7.1 获取令牌（密码模式）**

```bash
curl -X POST "http://127.0.0.1:8080/oauth/token" \
  -H "Authorization: Basic Y2xpZW50OnNlY3JldA==" \
  -d "grant_type=password&username=user1&password=123456"
```

返回 JSON 中的 `access_token` 即为令牌（`Y2xpZW50OnNlY3JldA==` 是 `client:secret` 的 Base64）。

**7.2 带令牌访问（网关鉴权）**

```bash
TOKEN=<上一步的 access_token>

# 查店铺
curl "http://127.0.0.1:8080/shop/list" -H "Authorization: Bearer $TOKEN"
# 查菜品（走 Sentinel 热点参数限流）
curl "http://127.0.0.1:8080/dish/get/1" -H "Authorization: Bearer $TOKEN"
# 下单（order 调 stock 扣库存，Seata 全局事务）
curl -X POST "http://127.0.0.1:8080/order/create" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"userId":3,"shopId":1,"items":[{"dishId":1,"quantity":2}]}'
```

不带令牌返回 401；带普通 `USER` 令牌访问管理员接口返回 403（网关按 `authorities` 中的 `ROLE_*` 判断）。

## 8. 监控与链路

- **Spring Boot Admin**：`http://127.0.0.1:9090`（各服务已引入 admin-client，自动注册）。
- **Prometheus**：`http://127.0.0.1:9090`，scrape 目标见 `deploy/prometheus/prometheus.yml`。
  服务暴露 `/actuator/prometheus` 指标（micrometer）。
- **Grafana**：`http://127.0.0.1:3000`，已预置 Prometheus 数据源（`deploy/grafana/provisioning/...`）。
- **Zipkin**：`http://127.0.0.1:9411`（Sleuth 采样率 1.0，每个请求都上报；Docker 编排已内置）。
- **Sentinel Dashboard**：`http://127.0.0.1:8858`（Docker 编排已内置，登录 sentinel/sentinel）。
- **SkyWalking**：`http://127.0.0.1:8081`（UI），链路数据存 ES（需本地自建，未纳入 Docker 编排）。

## 9. 已知注意点

1. **必须 JDK 8**：工程 `java.version=1.8`，JDK 25 会编译失败。
2. **`spring-cloud-starter-bootstrap`**：Spring Cloud 2021.x 默认不加载 bootstrap.yml，已显式引入。
3. **Zipkin starter 改名**：2021.0.1 BOM 移除了 `spring-cloud-starter-zipkin`，统一用 `spring-cloud-sleuth-zipkin`。
4. **OAuth2 循环依赖**：user 服务用 `@Lazy` + `spring.main.allow-circular-references: true` 规避 Spring Boot 2.6 / Security 5.6 的循环引用。
5. **网关 WebClient 负载均衡隔离**：`@LoadBalanced WebClient.Builder` 不能作为唯一默认 Bean，否则会被 Spring Boot Admin 客户端误用，把 `http://127.0.0.1:9090` 当成服务名做负载均衡而报 503。现已拆成 `@Primary` 普通 Builder + 具名 `@LoadBalanced` Builder，鉴权过滤器按名称注入。
6. **Seata 启动依赖**：order / stock 的 Seata 客户端启动时会向 Seata Server 建立连接，Seata Server 或 Nacos 未启动时会阻塞在 `Initializing Global Transaction Clients ...`，属正常现象——中间件就绪后即可快速完成启动。Docker 编排已通过健康检查 + `depends_on` 保证顺序。
"# FooddOrderingSystem" 
