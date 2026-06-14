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
        <h2>{{ app.editingAddressId ? '编辑地址' : '新增地址' }}</h2>
        <button v-if="app.editingAddressId" type="button" class="ghost" @click="app.resetAddressForm">取消编辑</button>
      </div>
      <form class="address-form" @submit.prevent="app.saveAddress">
        <label>联系人<input v-model.trim="app.addressForm.contactName" maxlength="32" required /></label>
        <label>联系电话<input v-model.trim="app.addressForm.contactPhone" pattern="^1\d{10}$" required /></label>
        <label class="wide">详细地址<input v-model.trim="app.addressForm.detailAddress" maxlength="255" required /></label>
        <label class="check"><input v-model="app.addressForm.defaultAddress" type="checkbox" />设为默认地址</label>
        <button type="submit" :disabled="app.loading">{{ app.editingAddressId ? '保存修改' : '保存地址' }}</button>
      </form>
    </div>

    <div class="panel">
      <h2>我的地址</h2>
      <p v-if="!app.addresses.length" class="muted">暂无地址。</p>
      <div v-for="address in app.addresses" :key="address.id" class="address-card">
        <div>
          <strong>{{ address.contactName }} {{ address.contactPhone }}</strong>
          <p>{{ address.detailAddress }}</p>
          <span v-if="address.defaultAddress" class="tag">默认</span>
        </div>
        <div class="row-actions">
          <button type="button" class="ghost" @click="app.editAddress(address)">编辑</button>
          <button v-if="!address.defaultAddress" type="button" class="ghost" @click="app.makeDefaultAddress(address.id)">设默认</button>
          <button type="button" class="danger" @click="app.removeAddress(address.id)">删除</button>
        </div>
      </div>
    </div>
  </section>
</template>
