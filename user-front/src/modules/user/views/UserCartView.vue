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
    <div class="panel">
      <div class="section-head">
        <h2>结算</h2>
        <span v-if="app.cart.shopName">{{ app.cart.shopName }}</span>
      </div>
      <p v-if="!app.cart.items.length" class="muted">购物车为空，请先选择商品。</p>
      <template v-else>
        <div v-for="item in app.cart.items" :key="item.id" class="checkout-line">
          <span>{{ item.productName }} x {{ item.quantity }}</span>
          <strong>¥{{ app.money(item.amount) }}</strong>
        </div>
        <div class="total-line large">
          <span>需支付</span>
          <strong>¥{{ app.money(app.checkoutPayAmount) }}</strong>
        </div>
      </template>
    </div>

    <div class="panel">
      <div class="section-head">
        <h2>收货地址</h2>
        <button type="button" class="ghost" @click="app.view = 'addresses'">管理地址</button>
      </div>
      <div v-if="app.addresses.length" class="address-options">
        <label v-for="address in app.addresses" :key="address.id" class="address-option">
          <input v-model="app.selectedAddressId" type="radio" :value="address.id" />
          <span>
            <strong>{{ address.contactName }} {{ address.contactPhone }}</strong>
            {{ address.detailAddress }}
          </span>
        </label>
      </div>
      <p v-else class="muted">请先添加收货地址。</p>
      <label class="remark">备注<textarea v-model.trim="app.checkoutRemark" maxlength="255" rows="3" /></label>
      <button type="button" :disabled="!app.canCheckout || app.loading" @click="app.createOrder">提交订单</button>
    </div>
  </section>
</template>
