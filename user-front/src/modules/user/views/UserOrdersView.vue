<script setup>
import OrderTimeline from '../../../shared/components/OrderTimeline.vue'

defineProps({
  app: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <section class="orders-layout">
    <div class="panel order-list">
      <h2>我的订单</h2>
      <p v-if="!app.orders.length" class="muted">暂无订单。</p>
      <button
        v-for="order in app.orders"
        :key="order.id"
        type="button"
        class="order-row"
        :class="{ active: app.activeOrder?.id === order.id }"
        @click="app.refreshOrder(order.id)"
      >
        <span>
          <strong>{{ order.shopName }}</strong>
          <small>{{ app.shortTime(order.createdAt) }}</small>
        </span>
        <span>
          <b>{{ order.statusLabel }}</b>
          <strong>¥{{ app.money(order.payAmount) }}</strong>
        </span>
      </button>
    </div>

    <div class="panel order-detail">
      <template v-if="app.activeOrder">
        <div class="section-head">
          <div>
            <p class="eyebrow">订单 {{ app.activeOrder.orderNo }}</p>
            <h2>{{ app.activeOrder.shopName }}</h2>
          </div>
          <span class="status">{{ app.activeOrder.statusLabel }}</span>
        </div>
        <div class="info-grid">
          <p><span>联系人</span>{{ app.activeOrder.contactName }} {{ app.activeOrder.contactPhone }}</p>
          <p><span>地址</span>{{ app.activeOrder.addressSnapshot }}</p>
          <p><span>骑手</span>{{ app.activeOrder.riderName || '待分配' }}</p>
          <p><span>备注</span>{{ app.activeOrder.remark || '无' }}</p>
        </div>
        <div v-for="item in app.activeOrder.items" :key="item.productName" class="checkout-line">
          <span>{{ item.productName }} x {{ item.quantity }}</span>
          <strong>¥{{ app.money(item.amount) }}</strong>
        </div>
        <div class="price-lines">
          <p><span>商品</span><strong>¥{{ app.money(app.activeOrder.goodsAmount) }}</strong></p>
          <p><span>配送费</span><strong>¥{{ app.money(app.activeOrder.deliveryFee) }}</strong></p>
          <p class="total"><span>合计</span><strong>¥{{ app.money(app.activeOrder.payAmount) }}</strong></p>
        </div>
        <div class="action-row">
          <button v-if="app.activeOrder.status === 'PENDING_PAYMENT'" type="button" @click="app.payOrder(app.activeOrder.id)">模拟支付</button>
          <button v-if="app.activeOrder.status === 'PENDING_PAYMENT'" type="button" class="danger" @click="app.cancelOrder(app.activeOrder.id)">取消订单</button>
          <button v-if="['PAID', 'ACCEPTED', 'PREPARING', 'READY_FOR_DELIVERY'].includes(app.activeOrder.status)" type="button" class="danger" @click="app.requestRefund(app.activeOrder.id)">申请退款</button>
          <button v-if="app.activeOrder.status === 'DELIVERED'" type="button" @click="app.confirmOrder(app.activeOrder.id)">确认收货</button>
        </div>
        <OrderTimeline :app="app" :timeline="app.activeOrder.timeline || []" />
        <form v-if="app.activeOrder.status === 'COMPLETED' && !app.activeOrder.commented" class="comment-form" @submit.prevent="app.submitComment(app.activeOrder.id)">
          <label>评分<input v-model.number="app.commentForm.rating" type="number" min="1" max="5" required /></label>
          <label>评价<textarea v-model.trim="app.commentForm.content" maxlength="255" rows="3" required /></label>
          <button type="submit">提交评价</button>
        </form>
        <div v-if="app.activeOrder.comment" class="comment-view">
          <strong>{{ app.activeOrder.comment.rating }} 分评价</strong>
          <p>{{ app.activeOrder.comment.content }}</p>
        </div>
      </template>
      <p v-else class="muted">选择左侧订单查看详情。</p>
    </div>
  </section>
</template>
