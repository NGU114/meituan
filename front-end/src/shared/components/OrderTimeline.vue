<script setup>
defineProps({
  app: {
    type: Object,
    required: true
  },
  timeline: {
    type: Array,
    default: () => []
  }
})
</script>

<template>
  <section v-if="timeline.length" class="order-timeline">
    <h3>订单进度</h3>
    <div
      v-for="step in timeline"
      :key="step.status"
      class="timeline-step"
      :class="{ reached: step.reached, current: step.current }"
    >
      <i />
      <span>
        <strong>{{ step.label }}</strong>
        <small>{{ step.time ? app.shortTime(step.time) : (step.reached ? '已到达' : '待进行') }}</small>
      </span>
    </div>
  </section>
</template>
