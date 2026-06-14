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
    <div v-if="app.adminDashboard" class="metric-grid admin-metrics">
      <article class="metric"><span>用户</span><strong>{{ app.adminDashboard.userCount }}</strong></article>
      <article class="metric"><span>商家</span><strong>{{ app.adminDashboard.merchantCount }}</strong></article>
      <article class="metric"><span>骑手</span><strong>{{ app.adminDashboard.riderCount }}</strong></article>
      <article class="metric"><span>店铺</span><strong>{{ app.adminDashboard.shopCount }}</strong></article>
      <article class="metric"><span>订单</span><strong>{{ app.adminDashboard.orderCount }}</strong></article>
      <article class="metric"><span>完成单</span><strong>{{ app.adminDashboard.completedOrderCount }}</strong></article>
      <article class="metric"><span>成交额</span><strong>¥{{ app.money(app.adminDashboard.totalRevenue) }}</strong></article>
    </div>
  </section>
</template>
