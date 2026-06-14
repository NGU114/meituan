# meituan

课程设计版“仿美团外卖”全栈演示系统，采用“前后端分离 + 单体 Spring Boot”实现，覆盖用户端、商家端、骑手端、管理端四类角色。

**已完成功能**
- 用户端：注册登录、浏览店铺/菜品、购物车、地址新增/编辑/默认地址、下单、模拟支付、取消、确认收货、评价
- 商家端：店铺信息维护、分类管理、菜品新增/编辑/下架、接单、拒单、制作中、待配送
- 骑手端：查看可接订单、查看订单详情、接单配送、查看我的配送
- 管理端：平台统计、用户列表、店铺列表、订单监控、订单详情

**技术栈**
- 后端：Spring Boot 4、Spring Web、Spring Data JPA、Bean Validation、JWT、H2/MySQL
- 前端：Vue 3、Vite、原生 CSS

**业务闭环**
订单状态围绕以下流程流转：

`PENDING_PAYMENT -> PAID -> ACCEPTED -> PREPARING -> READY_FOR_DELIVERY -> DELIVERING -> COMPLETED`

取消单和拒单会进入：

`CANCELLED`

**快速启动**
1. 启动后端：

```bash
cd back-end
./mvnw spring-boot:run
```

2. 启动前端：

```bash
cd front-end
npm install
npm run dev
```

前端默认地址：`http://localhost:5173`，通过 Vite 代理访问后端 `http://localhost:8080/api`。

3. 运行测试和构建：

```bash
cd back-end
./mvnw test

cd ../front-end
npm run build
```

默认使用本地 H2 文件数据库，地址在 `back-end/data/`，首次启动会自动注入演示数据。

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

前端登录面板提供四个演示账号快捷按钮，登录后会自动进入对应角色工作台。

**核心接口**
- 认证：`/api/auth/register`、`/api/auth/login`、`/api/auth/me`
- 用户地址：`/api/addresses`
- 店铺浏览：`/api/shops`、`/api/shops/{shopId}`
- 购物车：`/api/cart`、`/api/cart/items`
- 用户订单：`/api/orders`
- 商家管理：`/api/merchant/shops`、`/api/merchant/categories`、`/api/merchant/products`、`/api/merchant/orders`
- 骑手端：`/api/rider/orders/available`、`/api/rider/orders/mine`
- 管理端：`/api/admin/dashboard`、`/api/admin/users`、`/api/admin/shops`、`/api/admin/orders`

所有需要登录的接口都通过请求头传 JWT：

```http
Authorization: Bearer <token>
```

**MySQL 切换**
如果准备部署到服务器，可将 [application.properties](/Users/harrye/Java/meituan/back-end/src/main/resources/application.properties) 的数据源改成 MySQL：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/meituan?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=你的密码
spring.jpa.hibernate.ddl-auto=update
```
