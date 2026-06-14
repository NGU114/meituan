<script setup>
defineProps({
  app: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <section class="panel table-panel">
    <div class="section-head">
      <h2>店铺管理</h2>
      <button type="button" class="ghost" @click="app.refreshAdminData">刷新</button>
    </div>
    <div class="table-head shop-head"><span>ID</span><span>店铺</span><span>配送费</span><span>起送价</span><span>状态</span><span>操作</span></div>
    <div v-for="shop in app.adminShops" :key="shop.id" class="table-row shop-head">
      <span>{{ shop.id }}</span>
      <span><strong>{{ shop.name }}</strong><small>{{ shop.announcement }}</small></span>
      <span>¥{{ app.money(shop.deliveryFee) }}</span>
      <span>¥{{ app.money(shop.minOrderAmount) }}</span>
      <span class="tag" :class="{ off: !shop.open }">{{ shop.open ? '营业中' : '已关闭' }}</span>
      <span>
        <button
          type="button"
          class="ghost"
          :class="{ danger: shop.open }"
          :disabled="app.loading"
          @click="app.toggleAdminShopStatus(shop)"
        >
          {{ shop.open ? '关闭' : '恢复' }}
        </button>
      </span>
    </div>
  </section>
</template>
