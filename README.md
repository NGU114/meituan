# meituan

课程设计版“仿美团外卖”后端 MVP，按“前后端分离 + 单体 Spring Boot”路线实现，先打通完整闭环：

- 用户端：注册登录、地址管理、浏览店铺/菜品、购物车、下单、模拟支付、确认收货、评价
- 商家端：店铺信息维护、菜品管理、接单、制作中、待配送
- 骑手端：查看可接订单、接单配送
- 管理端：用户列表、店铺列表、订单查看、基础统计

当前仓库主要完成的是后端工程，目录在 [back-end](/Users/harrye/Java/meituan/back-end)。

**技术栈**
- Spring Boot 4
- Spring Web
- Spring Data JPA
- H2（默认演示库，可切换 MySQL）
- JWT
- Bean Validation

**业务闭环**
订单状态围绕以下流程流转：

`PENDING_PAYMENT -> PAID -> ACCEPTED -> PREPARING -> READY_FOR_DELIVERY -> DELIVERING -> COMPLETED`

取消单和拒单会进入：

`CANCELLED`

**快速启动**
1. 进入后端目录：

```bash
cd back-end
```

2. 启动项目：

```bash
./mvnw spring-boot:run
```

3. 运行测试：

```bash
./mvnw test
```

默认使用本地 H2 文件数据库，地址在 `back-end/data/`，启动后会自动注入演示数据。

H2 控制台：
- 地址：`http://localhost:8080/h2-console`
- JDBC URL：`jdbc:h2:file:./data/meituan`
- 用户名：`sa`
- 密码：留空

**演示账号**
- 用户：`user / 123456`
- 商家：`merchant / 123456`
- 骑手：`rider / 123456`
- 管理员：`admin / 123456`

**核心接口**
- 认证：`/api/auth/register`、`/api/auth/login`、`/api/auth/me`
- 用户地址：`/api/addresses`
- 店铺浏览：`/api/shops`、`/api/shops/{shopId}`
- 购物车：`/api/cart`、`/api/cart/items`
- 用户订单：`/api/orders`
- 商家管理：`/api/merchant/shops`、`/api/merchant/products`、`/api/merchant/orders`
- 骑手端：`/api/rider/orders/available`、`/api/rider/orders/{orderId}/take`
- 管理端：`/api/admin/dashboard`、`/api/admin/users`、`/api/admin/shops`、`/api/admin/orders`

所有需要登录的接口都通过请求头传 JWT：

```http
Authorization: Bearer <token>
```

**MySQL 切换**
如果你们后续联前端或准备部署到服务器，建议把 [application.properties](/Users/harrye/Java/meituan/back-end/src/main/resources/application.properties) 的数据源改成 MySQL：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/meituan?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=你的密码
spring.jpa.hibernate.ddl-auto=update
```

**当前适合的小组分工**
- 后端 A：认证鉴权、用户、地址、店铺浏览
- 后端 B：购物车、订单流转、库存回滚、评价
- 前端 A：用户端页面
- 前端 B：商家端、管理端、骑手端
- 负责人：数据库设计、接口文档、联调、部署、答辩材料
