<script setup>
defineProps({
  app: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <section class="content-stack">
    <div class="metric-grid">
      <article class="metric"><span>待接单</span><strong>{{ app.merchantStats.pending }}</strong></article>
      <article class="metric"><span>制作中</span><strong>{{ app.merchantStats.preparing }}</strong></article>
      <article class="metric"><span>待配送</span><strong>{{ app.merchantStats.ready }}</strong></article>
      <article class="metric"><span>完成收入</span><strong>¥{{ app.money(app.merchantStats.revenue) }}</strong></article>
    </div>
    <div class="panel">
      <div class="section-head">
        <h2>最近订单</h2>
        <button type="button" class="ghost" @click="app.view = 'merchant-orders'">处理订单</button>
      </div>
      <div v-for="order in app.merchantOrders.slice(0, 6)" :key="order.id" class="data-row">
        <span>{{ order.shopName }}</span>
        <span>{{ order.statusLabel }}</span>
        <strong>¥{{ app.money(order.payAmount) }}</strong>
      </div>
    </div>
  </section>
</template>
