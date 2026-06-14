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
      <div>
        <p class="eyebrow">收到的评价</p>
        <h2>商家评价</h2>
      </div>
      <button type="button" class="ghost" @click="app.refreshMerchantData">刷新</button>
    </div>

    <p v-if="!app.merchantComments.length" class="muted">暂无评价。</p>
    <div v-else class="comment-list">
      <article v-for="comment in app.merchantComments" :key="`${comment.orderId}-${comment.createdAt}`" class="comment-card">
        <div class="section-head">
          <div>
            <p class="eyebrow">订单 {{ comment.orderNo }}</p>
            <h3>{{ comment.shopName }}</h3>
          </div>
          <strong class="rating">{{ comment.rating }} 分</strong>
        </div>
        <p>{{ comment.content }}</p>
        <div class="meta-line">
          <span>{{ comment.userName }}</span>
          <span>{{ app.shortTime(comment.createdAt) }}</span>
        </div>
      </article>
    </div>
  </section>
</template>
