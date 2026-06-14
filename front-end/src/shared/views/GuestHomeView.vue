<script setup>
defineProps({
  app: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <main class="login-shell">
    <section class="login-brand" aria-label="美团外卖课程设计演示入口">
      <div class="login-topline">
        <strong>美团外卖</strong>
        <span>课程设计演示系统</span>
      </div>

      <div class="login-copy">
        <p class="eyebrow">四端业务闭环</p>
        <h1>把外卖从下单到送达跑通</h1>
        <p>用户点餐、商家接单、骑手配送、平台管理，从一组演示账号进入完整业务现场。</p>
      </div>

      <div class="login-preview">
        <p>
          <span>今日营业</span>
          <strong>{{ app.shops.length || 3 }}</strong>
        </p>
        <article v-for="shop in app.shops.slice(0, 3)" :key="shop.id">
          <span>{{ shop.name }}</span>
          <small>{{ shop.announcement }}</small>
        </article>
      </div>
    </section>

    <section class="login-card">
      <div class="login-card-head">
        <div>
          <p class="eyebrow">账号入口</p>
          <h2>{{ app.authMode === 'login' ? '欢迎回来' : '创建账号' }}</h2>
        </div>
        <div class="segmented">
          <button type="button" :class="{ active: app.authMode === 'login' }" @click="app.authMode = 'login'">登录</button>
          <button type="button" :class="{ active: app.authMode === 'register' }" @click="app.authMode = 'register'">注册</button>
        </div>
      </div>

      <p class="login-hint">{{ app.authMode === 'login' ? '选择角色即可自动填入演示账号。' : '选择用户、商家或骑手创建对应账号。' }}</p>
      <div class="demo-grid login-demo-grid">
        <button
          v-for="account in app.demoAccounts"
          :key="account.role"
          type="button"
          class="ghost"
          :class="{ active: app.authMode === 'register' && app.registerForm.role === account.role }"
          :disabled="app.authMode === 'register' && account.role === 'ADMIN'"
          @click="app.selectAuthRole(account)"
        >
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
        <template v-if="app.registerForm.role === 'MERCHANT'">
          <label>店铺名称<input v-model.trim="app.registerForm.shopName" maxlength="64" required /></label>
          <label>店铺公告<input v-model.trim="app.registerForm.shopAnnouncement" maxlength="255" required /></label>
          <div class="form-grid">
            <label>配送费<input v-model.number="app.registerForm.deliveryFee" type="number" min="0" step="0.01" required /></label>
            <label>起送价<input v-model.number="app.registerForm.minOrderAmount" type="number" min="0" step="0.01" required /></label>
          </div>
        </template>
        <button type="submit" :disabled="app.loading">创建{{ app.roleNames[app.registerForm.role] }}账号</button>
      </form>
    </section>
  </main>
</template>
