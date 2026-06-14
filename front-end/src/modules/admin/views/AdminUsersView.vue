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
      <h2>用户管理</h2>
      <button type="button" class="ghost" @click="app.refreshAdminData">刷新</button>
    </div>
    <div class="table-head user-head"><span>ID</span><span>用户名</span><span>昵称</span><span>手机号</span><span>角色</span><span>状态</span><span>操作</span></div>
    <div v-for="user in app.adminUsers" :key="user.id" class="table-row user-head">
      <span>{{ user.id }}</span>
      <span>{{ user.username }}</span>
      <span>{{ user.displayName }}</span>
      <span>{{ user.phone }}</span>
      <span>{{ app.roleNames[user.role] }}</span>
      <span class="tag" :class="{ off: !user.enabled }">{{ user.enabled ? '正常' : '已禁用' }}</span>
      <span>
        <button
          type="button"
          class="ghost"
          :class="{ danger: user.enabled && user.role !== 'ADMIN' }"
          :disabled="user.role === 'ADMIN' || user.id === app.profile?.userId || app.loading"
          @click="app.toggleAdminUserStatus(user)"
        >
          {{ user.enabled ? '禁用' : '启用' }}
        </button>
      </span>
    </div>
  </section>
</template>
