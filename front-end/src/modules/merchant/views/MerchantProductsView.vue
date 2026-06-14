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
        <h2>{{ app.productForm.id ? '编辑菜品' : '新增菜品' }}</h2>
        <button type="button" class="ghost" @click="app.resetProductForm">清空表单</button>
      </div>
      <form class="management-form" @submit.prevent="app.saveProduct">
        <label>所属店铺
          <select v-model.number="app.productForm.shopId" required @change="app.changeProductShop(app.productForm.shopId)">
            <option v-for="shop in app.merchantShops" :key="shop.id" :value="shop.id">{{ shop.name }}</option>
          </select>
        </label>
        <label>分类
          <select v-model.number="app.productForm.categoryId" required>
            <option v-for="category in app.merchantShopCategories" :key="category.id" :value="category.id">{{ category.name }}</option>
          </select>
        </label>
        <label>菜品名称<input v-model.trim="app.productForm.name" maxlength="64" required /></label>
        <label>价格<input v-model.number="app.productForm.price" type="number" min="0" step="0.01" required /></label>
        <label>库存<input v-model.number="app.productForm.stock" type="number" min="0" required /></label>
        <label class="wide">描述<input v-model.trim="app.productForm.description" maxlength="255" required /></label>
        <label class="check"><input v-model="app.productForm.enabled" type="checkbox" />上架销售</label>
        <button type="submit" :disabled="app.loading || !app.merchantShopCategories.length">{{ app.productForm.id ? '保存菜品' : '新增菜品' }}</button>
      </form>
    </div>

    <div class="panel table-panel">
      <div class="section-head">
        <h2>菜品列表</h2>
        <span>{{ app.merchantShopProducts.length }} 个</span>
      </div>
      <div class="table-head product-head">
        <span>菜品</span><span>分类</span><span>价格</span><span>库存</span><span>状态</span><span>操作</span>
      </div>
      <div v-for="product in app.merchantShopProducts" :key="product.id" class="table-row product-head">
        <span><strong>{{ product.name }}</strong><small>{{ product.description }}</small></span>
        <span>{{ product.categoryName }}</span>
        <span>¥{{ app.money(product.price) }}</span>
        <span>{{ product.stock }}</span>
        <span><b :class="product.enabled ? 'ok-text' : 'muted-text'">{{ product.enabled ? '上架' : '下架' }}</b></span>
        <span class="row-actions">
          <button type="button" class="ghost" @click="app.editProduct(product)">编辑</button>
          <button v-if="product.enabled" type="button" class="danger" @click="app.disableProduct(product.id)">下架</button>
        </span>
      </div>
    </div>
  </section>
</template>
