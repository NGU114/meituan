<script setup>
defineProps({
  app: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <aside class="side-panel">
    <section v-if="!app.isAuthed" class="panel">
      <div class="segmented">
        <button type="button" :class="{ active: app.authMode === 'login' }" @click="app.authMode = 'login'">登录</button>
        <button type="button" :class="{ active: app.authMode === 'register' }" @click="app.authMode = 'register'">注册</button>
      </div>
      <div class="demo-grid">
        <button v-for="account in app.demoAccounts" :key="account.role" type="button" class="ghost" @click="app.fillLogin(account)">
          {{ account.label }}
        </button>
      </div>

      <form v-if="app.authMode === 'login'" class="form" @submit.prevent="app.submitLogin">
        <label>用户名<input v-model.trim="app.loginForm.username" required /></label>
        <label>密码<input v-model="app.loginForm.password" type="password" required /></label>
        <button type="submit" :disabled="app.loading">登录</button>
      </form>

      <form v-else class="form" @submit.prevent="app.submitRegister">
        <label>用户名<input v-model.trim="app.registerForm.username" minlength="4" maxlength="20" required /></label>
        <label>密码<input v-model="app.registerForm.password" type="password" minlength="6" maxlength="20" required /></label>
        <label>昵称<input v-model.trim="app.registerForm.displayName" maxlength="32" required /></label>
        <label>手机号<input v-model.trim="app.registerForm.phone" pattern="^1\d{10}$" required /></label>
        <button type="submit" :disabled="app.loading">创建用户账号</button>
      </form>
    </section>

    <section v-else class="panel profile-card">
      <p class="eyebrow">当前账号</p>
      <h2>{{ app.profile.displayName }}</h2>
      <p>{{ app.roleLabel }} · {{ app.profile.phone || app.profile.username }}</p>
      <button type="button" @click="app.view = app.homeViewFor(app.profile.role)">返回首页</button>
    </section>

    <section v-if="app.isUserWorkspace" class="panel compact-cart">
      <div class="section-head">
        <h2>购物车</h2>
        <button v-if="app.cart.items.length" type="button" class="ghost" @click="app.clearMyCart">清空</button>
      </div>
      <p v-if="!app.cart.items.length" class="muted">还没有添加商品</p>
      <div v-else class="cart-list">
        <p class="cart-shop">{{ app.cart.shopName }}</p>
        <div v-for="item in app.cart.items" :key="item.id" class="cart-line">
          <div>
            <strong>{{ item.productName }}</strong>
            <span>¥{{ app.money(item.price) }}</span>
          </div>
          <div class="stepper">
            <button type="button" @click="app.changeCartItem(item, -1)">-</button>
            <span>{{ item.quantity }}</span>
            <button type="button" @click="app.changeCartItem(item, 1)">+</button>
          </div>
        </div>
        <div class="total-line">
          <span>商品小计</span>
          <strong>¥{{ app.money(app.cart.totalAmount) }}</strong>
        </div>
        <button type="button" @click="app.view = 'cart'">去结算</button>
      </div>
    </section>

    <section v-if="app.currentRole === 'MERCHANT'" class="panel quick-panel">
      <div class="section-head">
        <h2>店铺</h2>
        <button type="button" class="ghost" @click="app.refreshMerchantData">刷新</button>
      </div>
      <select v-model.number="app.merchantShopId" @change="app.selectMerchantShop(app.merchantShopId)">
        <option v-for="shop in app.merchantShops" :key="shop.id" :value="shop.id">{{ shop.name }}</option>
      </select>
      <div class="mini-stats">
        <p><span>待接单</span><strong>{{ app.merchantStats.pending }}</strong></p>
        <p><span>制作中</span><strong>{{ app.merchantStats.preparing }}</strong></p>
        <p><span>待配送</span><strong>{{ app.merchantStats.ready }}</strong></p>
      </div>
    </section>

    <section v-if="app.currentRole === 'RIDER'" class="panel quick-panel">
      <div class="section-head">
        <h2>配送</h2>
        <button type="button" class="ghost" @click="app.refreshRiderData">刷新</button>
      </div>
      <div class="mini-stats">
        <p><span>可接订单</span><strong>{{ app.availableOrders.length }}</strong></p>
        <p><span>我的订单</span><strong>{{ app.riderOrders.length }}</strong></p>
      </div>
    </section>

    <section v-if="app.currentRole === 'ADMIN' && app.adminDashboard" class="panel quick-panel">
      <div class="section-head">
        <h2>平台</h2>
        <button type="button" class="ghost" @click="app.refreshAdminData">刷新</button>
      </div>
      <div class="mini-stats">
        <p><span>用户</span><strong>{{ app.adminDashboard.userCount }}</strong></p>
        <p><span>商家</span><strong>{{ app.adminDashboard.merchantCount }}</strong></p>
        <p><span>骑手</span><strong>{{ app.adminDashboard.riderCount }}</strong></p>
      </div>
    </section>
  </aside>
</template>
