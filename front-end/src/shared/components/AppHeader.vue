<script setup>
defineProps({
  app: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <header class="topbar">
    <div>
      <p class="eyebrow">{{ app.isAuthed ? app.roleLabel + '工作台' : '课程设计演示' }}</p>
      <h1>美团外卖</h1>
    </div>
    <nav class="tabs">
      <button
        v-for="tab in app.tabs"
        :key="tab.key"
        :class="{ active: app.view === tab.key }"
        type="button"
        @click="app.view = tab.key"
      >
        {{ tab.label }}
        <span v-if="tab.key === 'cart' && app.cartCount" class="badge">{{ app.cartCount }}</span>
      </button>
    </nav>
    <div class="account">
      <template v-if="app.isAuthed">
        <span>{{ app.profile.displayName || app.profile.username }}</span>
        <button type="button" class="ghost" @click="app.logout">退出</button>
      </template>
      <template v-else>
        <button type="button" class="ghost" @click="app.authMode = 'login'">登录</button>
        <button type="button" @click="app.authMode = 'register'">注册</button>
      </template>
    </div>
  </header>
</template>
