<script setup>
defineProps({
  app: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <section class="content-grid">
    <div class="shop-list">
      <button
        v-for="shop in app.shops"
        :key="shop.id"
        type="button"
        class="shop-row"
        :class="{ active: shop.id === app.activeShopId }"
        @click="app.selectShop(shop.id)"
      >
        <span>{{ shop.name }}</span>
        <small>起送 ¥{{ app.money(shop.minOrderAmount) }} · 配送 ¥{{ app.money(shop.deliveryFee) }}</small>
      </button>
    </div>

    <article v-if="app.shopDetail" class="shop-detail">
      <div class="hero">
        <div>
          <p class="eyebrow">商家</p>
          <h2>{{ app.shopDetail.name }}</h2>
          <p>{{ app.shopDetail.announcement }}</p>
        </div>
        <div class="hero-price">
          <span>起送 ¥{{ app.money(app.shopDetail.minOrderAmount) }}</span>
          <span>配送 ¥{{ app.money(app.shopDetail.deliveryFee) }}</span>
        </div>
      </div>

      <section v-for="category in app.shopDetail.categories" :key="category.id" class="menu-section">
        <h3>{{ category.name }}</h3>
        <div class="product-grid">
          <article v-for="product in category.products" :key="product.id" class="product-card">
            <div>
              <h4>{{ product.name }}</h4>
              <p>{{ product.description }}</p>
              <small>库存 {{ product.stock }}</small>
            </div>
            <div class="product-action">
              <strong>¥{{ app.money(product.price) }}</strong>
              <button type="button" :disabled="product.stock < 1 || app.loading" @click="app.addProduct(product)">
                {{ app.cartProductIds.has(product.id) ? '再来一份' : '加入' }}
              </button>
            </div>
          </article>
        </div>
      </section>
    </article>
  </section>
</template>
