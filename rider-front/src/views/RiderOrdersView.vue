<script setup>
import { computed } from 'vue'
import OrderTimeline from '../components/OrderTimeline.vue'

const props = defineProps({
  app: {
    type: Object,
    required: true
  }
})

const isAvailable = computed(() => props.app.view === 'rider-available')
const orderList = computed(() => (isAvailable.value ? props.app.availableOrders : props.app.riderOrders))
</script>

<template>
  <section class="orders-layout">
    <div class="panel order-list">
      <div class="section-head">
        <h2>{{ isAvailable ? '可接订单' : '我的配送' }}</h2>
        <button type="button" class="ghost" @click="app.refreshRiderData">刷新</button>
      </div>
      <p v-if="!orderList.length" class="muted">{{ isAvailable ? '暂无可接订单。' : '暂无配送订单。' }}</p>
      <button
        v-for="order in orderList"
        :key="order.id"
        type="button"
        class="order-row"
        :class="{ active: app.activeRiderOrder?.id === order.id }"
        @click="app.loadRiderOrder(order.id)"
      >
        <span><strong>{{ order.shopName }}</strong><small>{{ app.shortTime(order.createdAt) }}</small></span>
        <span><b>{{ order.statusLabel }}</b><strong>¥{{ app.money(order.payAmount) }}</strong></span>
      </button>
    </div>
    <div class="panel order-detail">
      <template v-if="app.activeRiderOrder">
        <div class="section-head">
          <div>
            <p class="eyebrow">订单 {{ app.activeRiderOrder.orderNo }}</p>
            <h2>{{ app.activeRiderOrder.shopName }}</h2>
          </div>
          <span class="status">{{ app.activeRiderOrder.statusLabel }}</span>
        </div>
        <div class="info-grid">
          <p><span>联系人</span>{{ app.activeRiderOrder.contactName }} {{ app.activeRiderOrder.contactPhone }}</p>
          <p><span>地址</span>{{ app.activeRiderOrder.addressSnapshot }}</p>
          <p><span>{{ isAvailable ? '金额' : '商家' }}</span>{{ isAvailable ? '¥' + app.money(app.activeRiderOrder.payAmount) : app.activeRiderOrder.merchantName }}</p>
          <p><span>备注</span>{{ app.activeRiderOrder.remark || '无' }}</p>
        </div>
        <button v-if="app.activeRiderOrder.status === 'READY_FOR_DELIVERY'" type="button" @click="app.takeRiderOrder(app.activeRiderOrder.id)">接单配送</button>
        <button v-else-if="app.activeRiderOrder.status === 'DELIVERING'" type="button" @click="app.markRiderDelivered(app.activeRiderOrder.id)">确认送达</button>
        <p v-else-if="app.activeRiderOrder.status === 'DELIVERED'" class="muted">已送达，等待用户端确认收货。</p>
        <p v-else class="muted">订单已完成配送流程。</p>
        <OrderTimeline :app="app" :timeline="app.activeRiderOrder.timeline || []" />
      </template>
      <p v-else class="muted">{{ isAvailable ? '选择可接订单查看地址。' : '选择订单查看配送信息。' }}</p>
    </div>
  </section>
</template>
