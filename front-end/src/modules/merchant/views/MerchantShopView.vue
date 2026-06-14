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
        <h2>店铺信息</h2>
        <span v-if="app.merchantShopId" class="tag">ID {{ app.merchantShopId }}</span>
      </div>
      <form class="management-form" @submit.prevent="app.saveShop">
        <label>店铺名称<input v-model.trim="app.shopForm.name" maxlength="64" required /></label>
        <label>配送费<input v-model.number="app.shopForm.deliveryFee" type="number" min="0" step="0.01" required /></label>
        <label>起送价<input v-model.number="app.shopForm.minOrderAmount" type="number" min="0" step="0.01" required /></label>
        <label class="wide">公告<input v-model.trim="app.shopForm.announcement" maxlength="255" required /></label>
        <label class="check"><input v-model="app.shopForm.open" type="checkbox" />营业中</label>
        <button type="submit" :disabled="app.loading || !app.merchantShopId">保存店铺</button>
      </form>
    </div>

    <div class="panel">
      <div class="section-head">
        <h2>分类管理</h2>
        <button type="button" class="ghost" @click="app.resetCategoryForm">新增分类</button>
      </div>
      <form class="management-form compact" @submit.prevent="app.saveCategory">
        <label>所属店铺
          <select v-model.number="app.categoryForm.shopId" required>
            <option v-for="shop in app.merchantShops" :key="shop.id" :value="shop.id">{{ shop.name }}</option>
          </select>
        </label>
        <label>分类名称<input v-model.trim="app.categoryForm.name" maxlength="32" required /></label>
        <label>排序<input v-model.number="app.categoryForm.sortOrder" type="number" min="0" required /></label>
        <button type="submit">{{ app.categoryForm.id ? '保存分类' : '新增分类' }}</button>
      </form>
      <div v-for="category in app.merchantShopCategories" :key="category.id" class="data-row">
        <span>{{ category.name }}</span>
        <small>排序 {{ category.sortOrder }}</small>
        <span class="row-actions">
          <button type="button" class="ghost" @click="app.editCategory(category)">编辑</button>
          <button type="button" class="danger" @click="app.removeCategory(category.id)">删除</button>
        </span>
      </div>
    </div>
  </section>
</template>
