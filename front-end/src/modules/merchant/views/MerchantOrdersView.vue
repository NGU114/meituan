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
      <div class="section-head">
        <h2>商家订单</h2>
        <button type="button" class="ghost" @click="app.refreshMerchantData">刷新</button>
      </div>
      <p v-if="!app.merchantOrders.length" class="muted">暂无订单。</p>
      <button
        v-for="order in app.merchantOrders"
        :key="order.id"
        type="button"
        class="order-row"
        :class="{ active: app.activeMerchantOrder?.id === order.id }"
        @click="app.loadMerchantOrder(order.id)"
      >
        <span><strong>{{ order.shopName }}</strong><small>{{ app.shortTime(order.createdAt) }}</small></span>
        <span><b>{{ order.statusLabel }}</b><strong>¥{{ app.money(order.payAmount) }}</strong></span>
      </button>
    </div>
    <div class="panel order-detail">
      <template v-if="app.activeMerchantOrder">
        <div class="section-head">
          <div>
            <p class="eyebrow">订单 {{ app.activeMerchantOrder.orderNo }}</p>
            <h2>{{ app.activeMerchantOrder.shopName }}</h2>
          </div>
          <span class="status">{{ app.activeMerchantOrder.statusLabel }}</span>
        </div>
        <div class="info-grid">
          <p><span>联系人</span>{{ app.activeMerchantOrder.contactName }} {{ app.activeMerchantOrder.contactPhone }}</p>
          <p><span>地址</span>{{ app.activeMerchantOrder.addressSnapshot }}</p>
          <p><span>骑手</span>{{ app.activeMerchantOrder.riderName || '待分配' }}</p>
          <p><span>备注</span>{{ app.activeMerchantOrder.remark || '无' }}</p>
        </div>
        <div v-for="item in app.activeMerchantOrder.items" :key="item.productName" class="checkout-line">
          <span>{{ item.productName }} x {{ item.quantity }}</span>
          <strong>¥{{ app.money(item.amount) }}</strong>
        </div>
        <div class="action-row">
          <button v-if="app.activeMerchantOrder.status === 'PAID'" type="button" @click="app.merchantOrderAction('merchantAccept', '已接单')">接单</button>
          <button v-if="app.activeMerchantOrder.status === 'PAID'" type="button" class="danger" @click="app.merchantOrderAction('merchantReject', '已拒单')">拒单</button>
          <button v-if="app.activeMerchantOrder.status === 'ACCEPTED'" type="button" @click="app.merchantOrderAction('merchantPreparing', '已开始制作')">开始制作</button>
          <button v-if="app.activeMerchantOrder.status === 'PREPARING'" type="button" @click="app.merchantOrderAction('merchantReady', '已标记待配送')">待配送</button>
          <button v-if="app.activeMerchantOrder.status === 'REFUND_REQUESTED'" type="button" @click="app.merchantOrderAction('merchantApproveRefund', '已同意退款')">同意退款</button>
          <button v-if="app.activeMerchantOrder.status === 'REFUND_REQUESTED'" type="button" class="danger" @click="app.merchantOrderAction('merchantRejectRefund', '已拒绝退款')">拒绝退款</button>
        </div>
        <OrderTimeline :app="app" :timeline="app.activeMerchantOrder.timeline || []" />
      </template>
      <p v-else class="muted">选择订单查看详情。</p>
    </div>
  </section>
</template>
