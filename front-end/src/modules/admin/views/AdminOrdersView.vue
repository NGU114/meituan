<script setup>
import OrderTimeline from '../../../shared/components/OrderTimeline.vue'

defineProps({
  app: {
    type: Object,
    required: true
  }
})

function confirmDeleteOrder(app) {
  if (window.confirm('确认彻底删除该订单吗？此操作不可恢复。')) {
    app.deleteAdminOrder(app.activeAdminOrder.id)
  }
}
</script>

<template>
  <section class="orders-layout">
    <div class="panel order-list">
      <div class="section-head">
        <h2>订单监控</h2>
        <button type="button" class="ghost" @click="app.refreshAdminData">刷新</button>
      </div>
      <p v-if="!app.adminOrders.length" class="muted">暂无订单。</p>
      <button
        v-for="order in app.adminOrders"
        :key="order.id"
        type="button"
        class="order-row"
        :class="{ active: app.activeAdminOrder?.id === order.id }"
        @click="app.loadAdminOrder(order.id)"
      >
        <span><strong>{{ order.shopName }}</strong><small>{{ app.shortTime(order.createdAt) }}</small></span>
        <span><b>{{ order.statusLabel }}</b><strong>¥{{ app.money(order.payAmount) }}</strong></span>
      </button>
    </div>
    <div class="panel order-detail">
      <template v-if="app.activeAdminOrder">
        <div class="section-head">
          <div>
            <p class="eyebrow">订单 {{ app.activeAdminOrder.orderNo }}</p>
            <h2>{{ app.activeAdminOrder.shopName }}</h2>
          </div>
          <span class="status">{{ app.activeAdminOrder.statusLabel }}</span>
        </div>
        <div class="info-grid">
          <p><span>商家</span>{{ app.activeAdminOrder.merchantName }}</p>
          <p><span>骑手</span>{{ app.activeAdminOrder.riderName || '待分配' }}</p>
          <p><span>联系人</span>{{ app.activeAdminOrder.contactName }} {{ app.activeAdminOrder.contactPhone }}</p>
          <p><span>地址</span>{{ app.activeAdminOrder.addressSnapshot }}</p>
        </div>
        <div class="price-lines">
          <p><span>商品</span><strong>¥{{ app.money(app.activeAdminOrder.goodsAmount) }}</strong></p>
          <p><span>配送费</span><strong>¥{{ app.money(app.activeAdminOrder.deliveryFee) }}</strong></p>
          <p class="total"><span>合计</span><strong>¥{{ app.money(app.activeAdminOrder.payAmount) }}</strong></p>
        </div>
        <div class="action-row">
          <button
            type="button"
            class="danger"
            @click="confirmDeleteOrder(app)"
          >
            删除订单
          </button>
        </div>
        <OrderTimeline :app="app" :timeline="app.activeAdminOrder.timeline || []" />
      </template>
      <p v-else class="muted">选择订单查看详情。</p>
    </div>
  </section>
</template>
