# 仿美团外卖完整接口文档

## 1. 文档说明

本文档是“仿美团外卖”项目的目标版接口规范，适合作为全组协作时的统一标准。

适用对象：

- 后端开发
- 用户端前端开发
- 商家端前端开发
- 骑手端前端开发
- 管理端前端开发
- 测试与项目负责人

使用原则：

- 本文档是项目的“目标接口契约”
- 当前仓库后端已实现一部分接口
- 尚未实现但项目必须具备的接口，也在本文档中给出统一规范
- 前后端联调时，以本文档字段名、路径、请求方式、返回结构为准

状态说明：

- `已实现`：当前后端代码已具备
- `待实现`：当前后端还没有，但项目建议补齐
- `可选`：增强项，不影响第一版闭环

第一版范围：

- 用户端
- 商家端
- 骑手端
- 管理端

不纳入第一版：

- 第三方支付
- 地图导航
- 智能派单
- 秒杀
- 推荐算法

## 2. 基础规范

### 2.1 服务地址

- 开发环境：`http://localhost:8080`
- 接口前缀：`/api`

### 2.2 认证方式

需要登录的接口统一通过请求头传 JWT：

```http
Authorization: Bearer <token>
```

### 2.3 角色定义

- `USER`：普通用户
- `MERCHANT`：商家
- `RIDER`：骑手
- `ADMIN`：管理员

### 2.4 统一返回格式

成功：

```json
{
  "success": true,
  "message": "OK",
  "data": {}
}
```

失败：

```json
{
  "success": false,
  "message": "库存不足",
  "data": null
}
```

### 2.5 建议统一错误码

当前后端只有 `success/message/data`，建议后续补充 `code` 字段。

建议错误码：

| code | 含义 |
| --- | --- |
| `SUCCESS` | 请求成功 |
| `UNAUTHORIZED` | 未登录或 token 失效 |
| `FORBIDDEN` | 无权限 |
| `PARAM_ERROR` | 参数错误 |
| `NOT_FOUND` | 资源不存在 |
| `STOCK_NOT_ENOUGH` | 库存不足 |
| `ORDER_STATUS_ERROR` | 订单状态不允许当前操作 |
| `SHOP_CLOSED` | 店铺已打烊 |
| `MIN_ORDER_NOT_REACHED` | 未达到起送价 |
| `DUPLICATE_ACTION` | 重复操作 |
| `SYSTEM_ERROR` | 系统异常 |

### 2.6 分页参数

涉及列表查询时统一使用：

- `pageNum`：页码，从 1 开始
- `pageSize`：每页条数

建议分页返回：

```json
{
  "list": [],
  "pageNum": 1,
  "pageSize": 10,
  "total": 100
}
```

## 3. 核心业务状态

### 3.1 订单状态

| 状态值 | 含义 |
| --- | --- |
| `PENDING_PAYMENT` | 待支付 |
| `PAID` | 已支付 |
| `ACCEPTED` | 商家已接单 |
| `PREPARING` | 制作中 |
| `READY_FOR_DELIVERY` | 待骑手接单 |
| `DELIVERING` | 配送中 |
| `COMPLETED` | 已完成 |
| `CANCELLED` | 已取消 |
| `REJECTED` | 商家拒单，可与 `CANCELLED` 二选一 |
| `REFUNDING` | 退款中，可选 |
| `REFUNDED` | 已退款，可选 |

建议第一版统一流程：

```text
PENDING_PAYMENT -> PAID -> ACCEPTED -> PREPARING -> READY_FOR_DELIVERY -> DELIVERING -> COMPLETED
```

取消链路：

```text
PENDING_PAYMENT -> CANCELLED
PAID -> CANCELLED
PAID -> REJECTED
```

### 3.2 店铺状态

- `OPEN`：营业中
- `CLOSED`：休息中

### 3.3 商品状态

- `ENABLED`：上架
- `DISABLED`：下架

## 4. 公共数据结构

### 4.1 用户信息

```json
{
  "id": 1,
  "username": "user",
  "displayName": "测试用户",
  "phone": "13800000000",
  "avatar": "https://example.com/avatar.png",
  "role": "USER",
  "status": "NORMAL",
  "createdAt": "2026-04-10T10:00:00"
}
```

### 4.2 地址信息

```json
{
  "id": 1,
  "contactName": "张三",
  "contactPhone": "13800000000",
  "province": "上海市",
  "city": "上海市",
  "district": "浦东新区",
  "detailAddress": "软件园 100 号",
  "addressTag": "公司",
  "longitude": 121.123456,
  "latitude": 31.123456,
  "defaultAddress": true
}
```

### 4.3 店铺卡片

```json
{
  "id": 1,
  "name": "川湘小馆",
  "logo": "https://example.com/shop.png",
  "announcement": "现炒现做，30 分钟送达",
  "deliveryFee": 4.00,
  "minOrderAmount": 20.00,
  "avgDeliveryTime": 30,
  "score": 4.8,
  "monthlySales": 2300,
  "open": true
}
```

### 4.4 商品信息

```json
{
  "id": 101,
  "shopId": 1,
  "categoryId": 11,
  "name": "毛血旺",
  "description": "招牌川味，下饭必点",
  "price": 36.00,
  "originPrice": 42.00,
  "stock": 50,
  "monthlySales": 520,
  "imageUrl": "https://example.com/product.png",
  "enabled": true
}
```

### 4.5 购物车信息

```json
{
  "shopId": 1,
  "shopName": "川湘小馆",
  "items": [
    {
      "id": 1,
      "productId": 101,
      "productName": "毛血旺",
      "price": 36.00,
      "quantity": 2,
      "amount": 72.00,
      "imageUrl": "https://example.com/product.png"
    }
  ],
  "goodsAmount": 72.00,
  "deliveryFee": 4.00,
  "discountAmount": 0.00,
  "payAmount": 76.00
}
```

### 4.6 订单摘要

```json
{
  "id": 1,
  "orderNo": "202604101030008abcde",
  "shopId": 1,
  "shopName": "川湘小馆",
  "status": "PAID",
  "statusLabel": "已支付",
  "payAmount": 40.00,
  "riderName": null,
  "createdAt": "2026-04-10T10:30:00"
}
```

### 4.7 订单详情

```json
{
  "id": 1,
  "orderNo": "202604101030008abcde",
  "shopId": 1,
  "shopName": "川湘小馆",
  "merchantName": "川湘小馆",
  "status": "DELIVERING",
  "statusLabel": "配送中",
  "goodsAmount": 36.00,
  "deliveryFee": 4.00,
  "discountAmount": 0.00,
  "payAmount": 40.00,
  "contactName": "张三",
  "contactPhone": "13800000000",
  "addressSnapshot": "上海市浦东新区软件园 100 号",
  "remark": "少辣",
  "riderId": 3,
  "riderName": "极速骑手",
  "commented": false,
  "estimatedDeliveryTime": 30,
  "createdAt": "2026-04-10T10:30:00",
  "acceptedAt": "2026-04-10T10:35:00",
  "deliveringAt": "2026-04-10T10:50:00",
  "completedAt": null,
  "items": [
    {
      "productId": 101,
      "productName": "毛血旺",
      "productPrice": 36.00,
      "quantity": 1,
      "amount": 36.00
    }
  ],
  "comment": null
}
```

## 5. 认证模块

### 5.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 用户注册 | `POST` | `/api/auth/register` | 无 | 已实现 |
| 用户登录 | `POST` | `/api/auth/login` | 无 | 已实现 |
| 退出登录 | `POST` | `/api/auth/logout` | 全角色 | 待实现 |
| 刷新 token | `POST` | `/api/auth/refresh` | 全角色 | 待实现 |
| 当前登录用户 | `GET` | `/api/auth/me` | 全角色 | 已实现 |
| 修改个人信息 | `PUT` | `/api/auth/profile` | 全角色 | 待实现 |
| 修改密码 | `PUT` | `/api/auth/password` | 全角色 | 待实现 |
| 管理员创建账号 | `POST` | `/api/admin/accounts` | `ADMIN` | 待实现 |

### 5.2 用户注册

- 方法：`POST`
- 路径：`/api/auth/register`

请求体：

```json
{
  "username": "zhangsan",
  "password": "123456",
  "displayName": "张三",
  "phone": "13800000000"
}
```

返回：

```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "userId": 5,
    "username": "zhangsan",
    "displayName": "张三",
    "role": "USER",
    "token": "jwt-token"
  }
}
```

### 5.3 用户登录

- 方法：`POST`
- 路径：`/api/auth/login`

请求体：

```json
{
  "username": "user",
  "password": "123456"
}
```

返回：

```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "userId": 4,
    "username": "user",
    "displayName": "测试用户",
    "role": "USER",
    "token": "jwt-token"
  }
}
```

### 5.4 退出登录

- 方法：`POST`
- 路径：`/api/auth/logout`
- 状态：`待实现`

说明：

- 如果后端不做 token 黑名单，也可以前端本地删除 token 即可
- 为统一规范，建议保留此接口

### 5.5 刷新 token

- 方法：`POST`
- 路径：`/api/auth/refresh`
- 状态：`待实现`

请求体：

```json
{
  "token": "old-token"
}
```

### 5.6 当前登录用户

- 方法：`GET`
- 路径：`/api/auth/me`

返回：

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "userId": 4,
    "username": "user",
    "displayName": "测试用户",
    "phone": "13800000004",
    "role": "USER"
  }
}
```

### 5.7 修改个人信息

- 方法：`PUT`
- 路径：`/api/auth/profile`
- 状态：`待实现`

请求体：

```json
{
  "displayName": "新昵称",
  "phone": "13800000001",
  "avatar": "https://example.com/avatar.png"
}
```

### 5.8 修改密码

- 方法：`PUT`
- 路径：`/api/auth/password`
- 状态：`待实现`

请求体：

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

## 6. 用户地址模块

### 6.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 地址列表 | `GET` | `/api/addresses` | `USER` | 已实现 |
| 地址详情 | `GET` | `/api/addresses/{addressId}` | `USER` | 待实现 |
| 新增地址 | `POST` | `/api/addresses` | `USER` | 已实现 |
| 修改地址 | `PUT` | `/api/addresses/{addressId}` | `USER` | 待实现 |
| 删除地址 | `DELETE` | `/api/addresses/{addressId}` | `USER` | 已实现 |
| 设为默认地址 | `PUT` | `/api/addresses/{addressId}/default` | `USER` | 待实现 |

### 6.2 地址列表

- 方法：`GET`
- 路径：`/api/addresses`

返回：

```json
[
  {
    "id": 1,
    "contactName": "张三",
    "contactPhone": "13800000000",
    "detailAddress": "上海市浦东新区软件园 100 号",
    "defaultAddress": true
  }
]
```

### 6.3 新增地址

- 方法：`POST`
- 路径：`/api/addresses`

请求体：

```json
{
  "contactName": "张三",
  "contactPhone": "13800000000",
  "province": "上海市",
  "city": "上海市",
  "district": "浦东新区",
  "detailAddress": "软件园 100 号",
  "addressTag": "公司",
  "longitude": 121.123456,
  "latitude": 31.123456,
  "defaultAddress": true
}
```

说明：

- 当前后端已实现最小字段版本
- 建议后续扩展 `province/city/district/addressTag/longitude/latitude`

### 6.4 修改地址

- 方法：`PUT`
- 路径：`/api/addresses/{addressId}`
- 状态：`待实现`

请求体与“新增地址”一致。

### 6.5 设为默认地址

- 方法：`PUT`
- 路径：`/api/addresses/{addressId}/default`
- 状态：`待实现`

## 7. 用户端店铺与商品浏览模块

### 7.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 店铺列表 | `GET` | `/api/shops` | 无 | 已实现 |
| 店铺详情 | `GET` | `/api/shops/{shopId}` | 无 | 已实现 |
| 店铺评论列表 | `GET` | `/api/shops/{shopId}/comments` | 无 | 待实现 |
| 商品搜索 | `GET` | `/api/products/search` | 无 | 待实现 |

### 7.2 店铺列表

- 方法：`GET`
- 路径：`/api/shops`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `keyword` | `string` | 否 | 搜索店铺名 |
| `pageNum` | `int` | 否 | 页码 |
| `pageSize` | `int` | 否 | 每页数量 |
| `sortBy` | `string` | 否 | `sales` / `score` / `deliveryTime` |

当前后端最小返回：

```json
[
  {
    "id": 1,
    "name": "川湘小馆",
    "announcement": "现炒现做，30 分钟送达",
    "deliveryFee": 4.00,
    "minOrderAmount": 20.00
  }
]
```

目标返回建议升级为分页结构，元素参考“店铺卡片”。

### 7.3 店铺详情

- 方法：`GET`
- 路径：`/api/shops/{shopId}`

返回：

```json
{
  "id": 1,
  "name": "川湘小馆",
  "announcement": "现炒现做，30 分钟送达",
  "deliveryFee": 4.00,
  "minOrderAmount": 20.00,
  "merchantName": "川湘小馆",
  "categories": [
    {
      "id": 1,
      "name": "热销",
      "sortOrder": 1,
      "products": [
        {
          "id": 101,
          "name": "毛血旺",
          "description": "招牌川味，下饭必点",
          "price": 36.00,
          "stock": 50
        }
      ]
    }
  ]
}
```

建议扩展字段：

- `logo`
- `score`
- `monthlySales`
- `avgDeliveryTime`
- `address`
- `open`

### 7.4 店铺评论列表

- 方法：`GET`
- 路径：`/api/shops/{shopId}/comments`
- 状态：`待实现`

返回：

```json
{
  "list": [
    {
      "id": 1,
      "userName": "测试用户",
      "rating": 5,
      "content": "很好吃",
      "createdAt": "2026-04-10T11:10:00"
    }
  ],
  "pageNum": 1,
  "pageSize": 10,
  "total": 1
}
```

### 7.5 商品搜索

- 方法：`GET`
- 路径：`/api/products/search`
- 状态：`待实现`

查询参数：

- `keyword`
- `pageNum`
- `pageSize`

## 8. 购物车模块

### 8.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 获取购物车 | `GET` | `/api/cart` | `USER` | 已实现 |
| 添加商品 | `POST` | `/api/cart/items` | `USER` | 已实现 |
| 修改数量 | `PUT` | `/api/cart/items/{cartItemId}` | `USER` | 已实现 |
| 删除单项 | `DELETE` | `/api/cart/items/{cartItemId}` | `USER` | 已实现 |
| 清空购物车 | `DELETE` | `/api/cart` | `USER` | 已实现 |
| 购物车预结算 | `GET` | `/api/cart/preview` | `USER` | 待实现 |

### 8.2 获取购物车

- 方法：`GET`
- 路径：`/api/cart`

返回：

```json
{
  "shopId": 1,
  "shopName": "川湘小馆",
  "items": [
    {
      "id": 1,
      "productId": 101,
      "productName": "毛血旺",
      "shopId": 1,
      "shopName": "川湘小馆",
      "price": 36.00,
      "quantity": 2,
      "amount": 72.00
    }
  ],
  "totalAmount": 72.00
}
```

建议目标结构升级为“购物车信息”。

### 8.3 添加商品

- 方法：`POST`
- 路径：`/api/cart/items`

请求体：

```json
{
  "productId": 101,
  "quantity": 2
}
```

业务约束：

- 购物车只允许同一店铺商品
- 校验商品是否上架
- 校验库存是否足够

### 8.4 修改数量

- 方法：`PUT`
- 路径：`/api/cart/items/{cartItemId}`

请求体：

```json
{
  "quantity": 3
}
```

### 8.5 删除单项

- 方法：`DELETE`
- 路径：`/api/cart/items/{cartItemId}`

### 8.6 清空购物车

- 方法：`DELETE`
- 路径：`/api/cart`

### 8.7 购物车预结算

- 方法：`GET`
- 路径：`/api/cart/preview`
- 状态：`待实现`

说明：

- 用于确认订单页展示商品总额、配送费、优惠金额、应付金额

## 9. 用户订单模块

### 9.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 订单预览 | `POST` | `/api/orders/preview` | `USER` | 待实现 |
| 提交订单 | `POST` | `/api/orders` | `USER` | 已实现 |
| 支付订单 | `POST` | `/api/orders/{orderId}/pay` | `USER` | 已实现 |
| 取消订单 | `POST` | `/api/orders/{orderId}/cancel` | `USER` | 已实现 |
| 确认收货 | `POST` | `/api/orders/{orderId}/confirm` | `USER` | 已实现 |
| 订单评价 | `POST` | `/api/orders/{orderId}/comment` | `USER` | 已实现 |
| 我的订单列表 | `GET` | `/api/orders` | `USER` | 已实现 |
| 订单详情 | `GET` | `/api/orders/{orderId}` | `USER` | 已实现 |
| 再来一单 | `POST` | `/api/orders/{orderId}/reorder` | `USER` | 待实现 |
| 催单 | `POST` | `/api/orders/{orderId}/remind` | `USER` | 可选 |

### 9.2 订单预览

- 方法：`POST`
- 路径：`/api/orders/preview`
- 状态：`待实现`

请求体：

```json
{
  "addressId": 1,
  "remark": "少辣"
}
```

返回：

```json
{
  "goodsAmount": 36.00,
  "deliveryFee": 4.00,
  "discountAmount": 0.00,
  "payAmount": 40.00,
  "availableCoupons": []
}
```

### 9.3 提交订单

- 方法：`POST`
- 路径：`/api/orders`

请求体：

```json
{
  "addressId": 1,
  "remark": "少辣，多放米饭"
}
```

说明：

- 当前实现是“从购物车生成订单”
- 请求体里不直接传商品列表

### 9.4 支付订单

- 方法：`POST`
- 路径：`/api/orders/{orderId}/pay`

说明：

- 第一版为模拟支付
- 只有 `PENDING_PAYMENT` 才能支付

### 9.5 取消订单

- 方法：`POST`
- 路径：`/api/orders/{orderId}/cancel`

说明：

- 只有 `PENDING_PAYMENT` 或 `PAID` 可以取消
- 取消时需要回滚库存

### 9.6 确认收货

- 方法：`POST`
- 路径：`/api/orders/{orderId}/confirm`

说明：

- 当前实现是用户在 `DELIVERING` 状态直接确认收货

### 9.7 订单评价

- 方法：`POST`
- 路径：`/api/orders/{orderId}/comment`

请求体：

```json
{
  "rating": 5,
  "content": "很好吃，送达很快"
}
```

### 9.8 我的订单列表

- 方法：`GET`
- 路径：`/api/orders`

查询参数：

- `status`
- `pageNum`
- `pageSize`

当前后端返回数组，建议目标升级为分页。

### 9.9 订单详情

- 方法：`GET`
- 路径：`/api/orders/{orderId}`

返回参考“订单详情”结构。

### 9.10 再来一单

- 方法：`POST`
- 路径：`/api/orders/{orderId}/reorder`
- 状态：`待实现`

说明：

- 将历史订单商品重新加入购物车

## 10. 商家店铺模块

### 10.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 我的店铺列表 | `GET` | `/api/merchant/shops` | `MERCHANT` | 已实现 |
| 店铺详情 | `GET` | `/api/merchant/shops/{shopId}` | `MERCHANT` | 待实现 |
| 修改店铺 | `PUT` | `/api/merchant/shops/{shopId}` | `MERCHANT` | 已实现 |
| 营业开关 | `PUT` | `/api/merchant/shops/{shopId}/open-status` | `MERCHANT` | 待实现 |

### 10.2 我的店铺列表

- 方法：`GET`
- 路径：`/api/merchant/shops`

### 10.3 店铺详情

- 方法：`GET`
- 路径：`/api/merchant/shops/{shopId}`
- 状态：`待实现`

### 10.4 修改店铺

- 方法：`PUT`
- 路径：`/api/merchant/shops/{shopId}`

请求体：

```json
{
  "name": "川湘小馆",
  "announcement": "现炒现做，30 分钟送达",
  "deliveryFee": 4.00,
  "minOrderAmount": 20.00,
  "logo": "https://example.com/shop.png",
  "avgDeliveryTime": 30,
  "open": true
}
```

说明：

- 当前后端已实现基础字段
- 建议补齐 `logo`、`avgDeliveryTime`

### 10.5 营业开关

- 方法：`PUT`
- 路径：`/api/merchant/shops/{shopId}/open-status`
- 状态：`待实现`

请求体：

```json
{
  "open": true
}
```

## 11. 商家分类与商品模块

### 11.1 分类接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 分类列表 | `GET` | `/api/merchant/categories` | `MERCHANT` | 待实现 |
| 新增分类 | `POST` | `/api/merchant/categories` | `MERCHANT` | 待实现 |
| 修改分类 | `PUT` | `/api/merchant/categories/{categoryId}` | `MERCHANT` | 待实现 |
| 删除分类 | `DELETE` | `/api/merchant/categories/{categoryId}` | `MERCHANT` | 待实现 |

### 11.2 商品接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 商品列表 | `GET` | `/api/merchant/products` | `MERCHANT` | 已实现 |
| 商品详情 | `GET` | `/api/merchant/products/{productId}` | `MERCHANT` | 待实现 |
| 新增商品 | `POST` | `/api/merchant/products` | `MERCHANT` | 已实现 |
| 修改商品 | `PUT` | `/api/merchant/products/{productId}` | `MERCHANT` | 已实现 |
| 删除商品 | `DELETE` | `/api/merchant/products/{productId}` | `MERCHANT` | 待实现 |
| 上下架商品 | `PUT` | `/api/merchant/products/{productId}/status` | `MERCHANT` | 待实现 |

### 11.3 新增分类

- 方法：`POST`
- 路径：`/api/merchant/categories`

请求体：

```json
{
  "shopId": 1,
  "name": "热销",
  "sortOrder": 1
}
```

### 11.4 商品列表

- 方法：`GET`
- 路径：`/api/merchant/products`

查询参数：

- `shopId`
- `categoryId`
- `keyword`
- `pageNum`
- `pageSize`

### 11.5 新增商品

- 方法：`POST`
- 路径：`/api/merchant/products`

请求体：

```json
{
  "shopId": 1,
  "categoryId": 11,
  "name": "鱼香肉丝",
  "description": "下饭菜",
  "price": 22.00,
  "originPrice": 25.00,
  "stock": 100,
  "imageUrl": "https://example.com/product.png",
  "enabled": true
}
```

说明：

- 当前后端已实现基础字段
- 建议补充 `originPrice` 和 `imageUrl`

### 11.6 修改商品

- 方法：`PUT`
- 路径：`/api/merchant/products/{productId}`

请求体同“新增商品”。

### 11.7 删除商品

- 方法：`DELETE`
- 路径：`/api/merchant/products/{productId}`
- 状态：`待实现`

### 11.8 上下架商品

- 方法：`PUT`
- 路径：`/api/merchant/products/{productId}/status`
- 状态：`待实现`

请求体：

```json
{
  "enabled": false
}
```

## 12. 商家订单模块

### 12.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 商家订单列表 | `GET` | `/api/merchant/orders` | `MERCHANT` | 已实现 |
| 商家订单详情 | `GET` | `/api/merchant/orders/{orderId}` | `MERCHANT` | 待实现 |
| 接单 | `POST` | `/api/merchant/orders/{orderId}/accept` | `MERCHANT` | 已实现 |
| 拒单 | `POST` | `/api/merchant/orders/{orderId}/reject` | `MERCHANT` | 已实现 |
| 开始制作 | `POST` | `/api/merchant/orders/{orderId}/preparing` | `MERCHANT` | 已实现 |
| 待配送 | `POST` | `/api/merchant/orders/{orderId}/ready` | `MERCHANT` | 已实现 |
| 出餐完成备注 | `PUT` | `/api/merchant/orders/{orderId}/remark` | `MERCHANT` | 可选 |

### 12.2 商家订单列表

- 方法：`GET`
- 路径：`/api/merchant/orders`

查询参数：

- `status`
- `shopId`
- `pageNum`
- `pageSize`

### 12.3 商家订单详情

- 方法：`GET`
- 路径：`/api/merchant/orders/{orderId}`
- 状态：`待实现`

说明：

- 当前后端已有订单详情组装能力
- 建议补一个商家端专用详情接口

### 12.4 接单

- 方法：`POST`
- 路径：`/api/merchant/orders/{orderId}/accept`

业务规则：

- 只有 `PAID` 订单可接单

### 12.5 拒单

- 方法：`POST`
- 路径：`/api/merchant/orders/{orderId}/reject`

业务规则：

- 只有 `PAID` 订单可拒单
- 拒单后要回滚库存

请求体建议：

```json
{
  "reason": "商品已售罄"
}
```

### 12.6 开始制作

- 方法：`POST`
- 路径：`/api/merchant/orders/{orderId}/preparing`

### 12.7 待配送

- 方法：`POST`
- 路径：`/api/merchant/orders/{orderId}/ready`

## 13. 骑手模块

### 13.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 可接订单列表 | `GET` | `/api/rider/orders/available` | `RIDER` | 已实现 |
| 我的配送订单 | `GET` | `/api/rider/orders/mine` | `RIDER` | 已实现 |
| 订单详情 | `GET` | `/api/rider/orders/{orderId}` | `RIDER` | 待实现 |
| 接单 | `POST` | `/api/rider/orders/{orderId}/take` | `RIDER` | 已实现 |
| 到店取餐 | `POST` | `/api/rider/orders/{orderId}/pickup` | `RIDER` | 待实现 |
| 送达订单 | `POST` | `/api/rider/orders/{orderId}/complete` | `RIDER` | 待实现 |
| 异常上报 | `POST` | `/api/rider/orders/{orderId}/exception` | `RIDER` | 可选 |

### 13.2 可接订单列表

- 方法：`GET`
- 路径：`/api/rider/orders/available`

说明：

- 当前后端返回 `READY_FOR_DELIVERY` 订单

### 13.3 我的配送订单

- 方法：`GET`
- 路径：`/api/rider/orders/mine`

### 13.4 接单

- 方法：`POST`
- 路径：`/api/rider/orders/{orderId}/take`

业务规则：

- 只有 `READY_FOR_DELIVERY` 才能接单
- 接单后状态进入 `DELIVERING`

### 13.5 到店取餐

- 方法：`POST`
- 路径：`/api/rider/orders/{orderId}/pickup`
- 状态：`待实现`

说明：

- 如果你们想简化，也可以不单独做
- 第一版允许“接单后直接配送中”

### 13.6 送达订单

- 方法：`POST`
- 路径：`/api/rider/orders/{orderId}/complete`
- 状态：`待实现`

说明：

- 当前系统缺这个接口
- 建议补齐后让订单进入“待用户确认”或直接“已送达”

请求体建议：

```json
{
  "deliveryRemark": "已送到前台"
}
```

## 14. 管理端模块

### 14.1 仪表盘与账号管理接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 仪表盘 | `GET` | `/api/admin/dashboard` | `ADMIN` | 已实现 |
| 用户列表 | `GET` | `/api/admin/users` | `ADMIN` | 已实现 |
| 用户详情 | `GET` | `/api/admin/users/{userId}` | `ADMIN` | 待实现 |
| 用户状态修改 | `PUT` | `/api/admin/users/{userId}/status` | `ADMIN` | 待实现 |
| 商家列表 | `GET` | `/api/admin/merchants` | `ADMIN` | 待实现 |
| 骑手列表 | `GET` | `/api/admin/riders` | `ADMIN` | 待实现 |
| 新增商家 | `POST` | `/api/admin/merchants` | `ADMIN` | 待实现 |
| 新增骑手 | `POST` | `/api/admin/riders` | `ADMIN` | 待实现 |

### 14.2 店铺与订单管理接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 店铺列表 | `GET` | `/api/admin/shops` | `ADMIN` | 已实现 |
| 店铺详情 | `GET` | `/api/admin/shops/{shopId}` | `ADMIN` | 待实现 |
| 店铺状态修改 | `PUT` | `/api/admin/shops/{shopId}/status` | `ADMIN` | 待实现 |
| 订单列表 | `GET` | `/api/admin/orders` | `ADMIN` | 已实现 |
| 订单详情 | `GET` | `/api/admin/orders/{orderId}` | `ADMIN` | 已实现 |

### 14.3 仪表盘

- 方法：`GET`
- 路径：`/api/admin/dashboard`

返回：

```json
{
  "userCount": 1,
  "merchantCount": 1,
  "riderCount": 1,
  "shopCount": 1,
  "orderCount": 10,
  "completedOrderCount": 8,
  "totalRevenue": 320.00
}
```

### 14.4 用户列表

- 方法：`GET`
- 路径：`/api/admin/users`

建议查询参数：

- `keyword`
- `role`
- `pageNum`
- `pageSize`

### 14.5 新增商家

- 方法：`POST`
- 路径：`/api/admin/merchants`
- 状态：`待实现`

请求体：

```json
{
  "username": "merchant2",
  "password": "123456",
  "displayName": "幸福小炒",
  "phone": "13800000011",
  "shopName": "幸福小炒"
}
```

### 14.6 新增骑手

- 方法：`POST`
- 路径：`/api/admin/riders`
- 状态：`待实现`

请求体：

```json
{
  "username": "rider2",
  "password": "123456",
  "displayName": "张骑手",
  "phone": "13800000012"
}
```

### 14.7 店铺状态修改

- 方法：`PUT`
- 路径：`/api/admin/shops/{shopId}/status`
- 状态：`待实现`

请求体：

```json
{
  "open": false
}
```

## 15. 评价模块

### 15.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 订单评价 | `POST` | `/api/orders/{orderId}/comment` | `USER` | 已实现 |
| 我的评价列表 | `GET` | `/api/comments/mine` | `USER` | 待实现 |
| 店铺评价列表 | `GET` | `/api/shops/{shopId}/comments` | 无 | 待实现 |

## 16. 文件上传模块

### 16.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 上传图片 | `POST` | `/api/upload/image` | 登录用户 | 待实现 |

### 16.2 上传图片

- 方法：`POST`
- 路径：`/api/upload/image`
- 内容类型：`multipart/form-data`

返回：

```json
{
  "url": "https://example.com/upload/20260410/a.png"
}
```

说明：

- 如果你们不想处理文件服务器，第一版也可以直接把图片 URL 作为文本输入

## 17. 通知模块

### 17.1 接口列表

| 接口 | 方法 | 路径 | 权限 | 状态 |
| --- | --- | --- | --- | --- |
| 消息列表 | `GET` | `/api/notifications` | 登录用户 | 可选 |
| 已读消息 | `PUT` | `/api/notifications/{id}/read` | 登录用户 | 可选 |

说明：

- 第一版可以不做站内信
- 前端可以先通过轮询订单状态代替

## 18. 推荐的数据表与接口映射

建议表：

- `user_account`
- `user_address`
- `shop`
- `product_category`
- `product`
- `cart_item`
- `orders`
- `order_item`
- `order_comment`
- `notification`

可选扩展表：

- `coupon`
- `user_coupon`
- `payment_record`
- `delivery_trace`

接口与模块对应：

- 用户端：`/api/auth`、`/api/addresses`、`/api/shops`、`/api/cart`、`/api/orders`
- 商家端：`/api/merchant/shops`、`/api/merchant/categories`、`/api/merchant/products`、`/api/merchant/orders`
- 骑手端：`/api/rider/orders`
- 管理端：`/api/admin/*`

## 19. 最低可交付接口清单

如果你们时间有限，必须先完成这些接口，项目才算闭环。

### 19.1 用户端最低清单

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/addresses`
- `POST /api/addresses`
- `DELETE /api/addresses/{addressId}`
- `GET /api/shops`
- `GET /api/shops/{shopId}`
- `GET /api/cart`
- `POST /api/cart/items`
- `PUT /api/cart/items/{cartItemId}`
- `DELETE /api/cart/items/{cartItemId}`
- `DELETE /api/cart`
- `POST /api/orders`
- `POST /api/orders/{orderId}/pay`
- `POST /api/orders/{orderId}/cancel`
- `GET /api/orders`
- `GET /api/orders/{orderId}`
- `POST /api/orders/{orderId}/confirm`
- `POST /api/orders/{orderId}/comment`

### 19.2 商家端最低清单

- `GET /api/merchant/shops`
- `PUT /api/merchant/shops/{shopId}`
- `GET /api/merchant/products`
- `POST /api/merchant/products`
- `PUT /api/merchant/products/{productId}`
- `GET /api/merchant/orders`
- `POST /api/merchant/orders/{orderId}/accept`
- `POST /api/merchant/orders/{orderId}/reject`
- `POST /api/merchant/orders/{orderId}/preparing`
- `POST /api/merchant/orders/{orderId}/ready`

### 19.3 骑手端最低清单

- `GET /api/rider/orders/available`
- `GET /api/rider/orders/mine`
- `POST /api/rider/orders/{orderId}/take`
- `POST /api/rider/orders/{orderId}/complete`

说明：

- 当前后端缺少“送达订单”，建议优先补上

### 19.4 管理端最低清单

- `GET /api/admin/dashboard`
- `GET /api/admin/users`
- `GET /api/admin/shops`
- `GET /api/admin/orders`
- `GET /api/admin/orders/{orderId}`

## 20. 当前后端已实现与待补总结

### 20.1 当前已实现的主要接口

- 认证：注册、登录、当前用户
- 地址：列表、新增、删除
- 店铺：店铺列表、店铺详情
- 购物车：查、新增、修改、删除、清空
- 用户订单：下单、支付、取消、确认收货、评价、列表、详情
- 商家：店铺修改、商品列表/新增/修改、订单列表与流转
- 骑手：可接单列表、我的订单、接单
- 管理端：仪表盘、用户列表、店铺列表、订单列表、订单详情

### 20.2 当前后端最该优先补的接口

- `PUT /api/addresses/{addressId}`
- `PUT /api/addresses/{addressId}/default`
- `GET /api/merchant/orders/{orderId}`
- `DELETE /api/merchant/products/{productId}`
- `GET /api/merchant/categories`
- `POST /api/merchant/categories`
- `PUT /api/merchant/categories/{categoryId}`
- `DELETE /api/merchant/categories/{categoryId}`
- `GET /api/rider/orders/{orderId}`
- `POST /api/rider/orders/{orderId}/complete`
- `GET /api/admin/shops/{shopId}`
- `PUT /api/admin/shops/{shopId}/status`
- `POST /api/admin/merchants`
- `POST /api/admin/riders`

## 21. 联调要求

前后端必须统一这些规则：

- 路径、字段名、状态值不能口头改
- 任何接口变更必须先改文档再改代码
- 订单状态严格按文档流转
- 金额字段统一用 `decimal`，保留两位小数
- 时间字段统一返回 ISO 格式字符串
- 列表接口尽量统一补分页

## 22. 建议落地方式

推荐你们这样使用这份文档：

1. 项目负责人把本文档作为“总接口标准”
2. 后端按 `已实现 / 待实现` 开工补接口
3. 前端直接按目标字段写页面和 TS 类型
4. 联调时先接 `已实现` 接口
5. 对于 `待实现` 接口，前端先 mock

如果你们需要，我下一步可以继续直接给你补两份配套文件：

- 一份“数据库表设计文档”
- 一份“5 个人任务分配和排期表”
