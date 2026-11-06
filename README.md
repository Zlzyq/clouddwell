# 云邸 · 酒旅交易平台（CloudDwell）

基于 Spring Cloud Alibaba 的微服务酒旅交易平台，覆盖酒店 / 房型 / 预订、订单交易、库存扣减、秒杀营销、搜索派单等核心链路。

项目重点不在 CRUD，而在交易域的一致性设计：

- 订单六态状态机与并发边界控制
- Redis + Lua 原子预扣库存，MQ 异步落库与失败补偿
- RabbitMQ TTL + DLX 实现 30 分钟超时关单
- 秒杀一人一单与独立库存隔离
- Elasticsearch 分词检索 + 地理位置 + 加权派单

## 模块

| 模块 | 端口 | 职责 |
|---|---|---|
| cloud-gateway | 9000 | 统一路由、JWT 全局校验、跨域、静态页面 |
| cloud-auth | 9101 | 登录认证、JWT 签发 |
| cloud-system | 9102 | 用户 / 商品内部查询服务，供 Feign 调用 |
| cloud-business | 9103 | 订单、商品、库存、秒杀、管理端、搜索派单 |
| cloud-hotel | 9111 | 酒店信息 |
| cloud-room | 9112 | 房型信息 |
| cloud-booking | 9113 | 预订记录 |
| cloud-customer | 9114 | 客户信息 |
| cloud-common | - | 统一返回、异常、TraceId、用户上下文、工具类 |

## 核心设计

### 1. 订单状态机

`CREATED(10) → PAID(20) → REFUNDING(50) → REFUNDED(60)`，分支为 `CANCELLED(30)` 与 `TIMEOUT(40)`。

支付、取消、退款、超时统一使用条件更新：

```sql
UPDATE biz_order
SET status = ?
WHERE order_no = ? AND status = ?
```

以影响行数判断是否抢占成功，避免并发下的重复支付、重复取消与状态覆盖。

### 2. Redis + Lua 预扣库存

```text
下单 → Lua 原子扣 Redis 库存 → 本地事务写订单 → MQ 异步扣 MySQL 库存
                    ↓ 失败                         ↓ 异常
                直接返回失败                  补偿恢复 Redis 库存
```

- `lua/deduct_stock.lua`：库存充足才 `DECRBY`，整个脚本原子执行
- `lua/restore_stock.lua`：取消 / 退款 / 超时场景恢复库存
- 启动时将 MySQL 库存批量预热到 Redis，避免脚本读到空值

### 3. RabbitMQ TTL + DLX 超时关单

```text
下单 → order.direct / order.pay.timeout
     → order.delay.queue（TTL = 30 分钟）
     → 过期成为死信，按 order.timeout 路由
     → order.timeout.queue
     → OrderTimeoutConsumer：未支付则置为 TIMEOUT 并恢复库存
```

延迟队列通过 `x-dead-letter-exchange` 与 `x-dead-letter-routing-key` 指向原交换机，换 routing key 防止消息循环。

### 4. 秒杀一人一单

`lua/seckill.lua` 在一次脚本内完成：

1. `SISMEMBER` 判断用户是否已购买
2. 库存不足直接返回
3. `DECRBY` 扣减库存
4. `SADD` 记录购买用户

返回 `0 / 1 / 2` 区分「已参与 / 库存不足 / 成功」。秒杀库存与普通商品库存使用不同 key，互不影响。

### 5. Elasticsearch 搜索与智能派单

- `geo_point` 存储服务者经纬度
- `geo_distance` 放入 `filter`，做附近范围硬筛选且不影响评分
- `function_score` 合成评分与距离：`rating × 2` + `3km` 高斯衰减
- `scoreMode / boostMode = MULTIPLY`，实现「评分高且距离近」的排序
- 同时支持分词检索、菜系过滤、高亮与聚合

### 6. Canal 增量同步

```text
MySQL binlog → Canal → RabbitMQ(canal.exchange / canal.order) → CanalConsumer → ES
```

消费者将 snake_case 列名转驼峰、经纬度转 `location` 对象，按 INSERT / UPDATE / DELETE 执行 upsert 或删除。

## 技术栈

Java 17、Spring Boot 3.3、Spring Cloud Alibaba（Nacos / Gateway / OpenFeign）、MyBatis-Plus、MySQL 8、Redis、RabbitMQ、Elasticsearch 7.12、MongoDB、Canal、JWT、Knife4j、JMeter、Docker

## 目录结构

```text
clouddwell
├── cloud-common       # 公共返回、异常、TraceId、上下文
├── cloud-gateway      # 网关、JWT、静态页面
├── cloud-auth         # 认证服务
├── cloud-system       # 用户 / 商品内部服务
├── cloud-business     # 订单、库存、秒杀、搜索
│   └── src/main/resources/lua   # 库存与秒杀脚本
├── cloud-hotel        # 酒店
├── cloud-room         # 房型
├── cloud-booking      # 预订
├── cloud-customer     # 客户
└── sql                # 表结构参考
```

## 本地启动

### 环境

JDK 17、Maven 3.8+、MySQL 8、Redis、RabbitMQ、Elasticsearch 7.12、MongoDB；使用 Nacos 时再启动 Nacos。

### 环境变量

配置已改为环境变量注入，启动前按需设置：

| 变量 | 默认值 | 说明 |
|---|---|---|
| `MYSQL_HOST` / `MYSQL_PORT` | `localhost` / `3306` | MySQL 地址 |
| `MYSQL_USERNAME` | `root` | MySQL 用户 |
| `MYSQL_PASSWORD` | 空 | MySQL 密码，必须设置 |
| `RABBITMQ_HOST` / `RABBITMQ_PORT` | `localhost` / `5672` | RabbitMQ 地址 |
| `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` | `guest` / `guest` | RabbitMQ 账号 |
| `ES_URIS` | `http://localhost:9200` | Elasticsearch 地址 |
| `MONGO_HOST` / `MONGO_PORT` | `localhost` / `27017` | MongoDB 地址 |
| `NACOS_ADDR` | `localhost:8848` | Nacos 地址 |
| `JWT_SECRET` | 内置开发密钥 | 生产环境务必替换 |

PowerShell 示例：

```powershell
$env:MYSQL_PASSWORD = "你的密码"
```

### 启动顺序

按端口依次启动：`cloud-auth(9101)` → `cloud-system(9102)` → `cloud-business(9103)` → `cloud-hotel(9111)` → `cloud-room(9112)` → `cloud-booking(9113)` → `cloud-customer(9114)` → `cloud-gateway(9000)`。

### 访问

- 网关：`http://localhost:9000`
- 管理页面：`http://localhost:9000/admin.html`
- 接口文档：`http://localhost:9103/doc.html`

## 压测

JMeter 三组混合场景（下单支付 / 下单取消 / 下单退款，20 线程 × 100 轮）：

| 接口 | 改造前 | Redis + Lua 改造后 |
|---|---|---|
| 提交订单 | 66ms / 异常率 36.95% | 7ms / 异常率 0% |
| 取消订单 | 32ms / 异常率 29.00% | 4ms / 异常率 0% |

改造前异常主要为库存行与订单行加锁顺序不一致导致的 MySQL 死锁，统一加锁顺序后消除。

查询接口另有单独压测（20 用户 × 100 次）：339ms → 建索引后 5ms → 加 Redis 后 2ms → 加分布式锁后 3ms。

完整压测记录见 [docs/order-stress-test.md](docs/order-stress-test.md)（Word 原件：[order-stress-test.docx](docs/order-stress-test.docx)）。

## 说明

个人实践项目，用于学习微服务协作、缓存一致性、消息可靠性与交易边界设计。配置中的密钥均为开发默认值，请勿直接用于生产。
