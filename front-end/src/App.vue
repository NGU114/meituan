<script setup>
import { reactive } from 'vue'
import AppHeader from './shared/components/AppHeader.vue'
import SidePanel from './shared/components/SidePanel.vue'
import { useAppState } from './composables/useAppState'
import AdminDashboardView from './modules/admin/views/AdminDashboardView.vue'
import AdminOrdersView from './modules/admin/views/AdminOrdersView.vue'
import AdminShopsView from './modules/admin/views/AdminShopsView.vue'
import AdminUsersView from './modules/admin/views/AdminUsersView.vue'
import CatalogView from './modules/user/views/CatalogView.vue'
import GuestHomeView from './shared/views/GuestHomeView.vue'
import MerchantCommentsView from './modules/merchant/views/MerchantCommentsView.vue'
import MerchantDashboardView from './modules/merchant/views/MerchantDashboardView.vue'
import MerchantOrdersView from './modules/merchant/views/MerchantOrdersView.vue'
import MerchantProductsView from './modules/merchant/views/MerchantProductsView.vue'
import MerchantShopView from './modules/merchant/views/MerchantShopView.vue'
import RiderOrdersView from './modules/rider/views/RiderOrdersView.vue'
import UserAddressesView from './modules/user/views/UserAddressesView.vue'
import UserCartView from './modules/user/views/UserCartView.vue'
import UserOrdersView from './modules/user/views/UserOrdersView.vue'

const app = reactive(useAppState())
</script>

<template>
  <div class="app-shell">
    <p v-if="app.message" class="toast">{{ app.message }}</p>

    <GuestHomeView v-if="!app.isAuthed" :app="app" />

    <template v-else>
      <AppHeader :app="app" />

      <main class="layout" :class="{ 'workspace-layout': !app.isCustomerView }">
      <SidePanel :app="app" />

      <CatalogView v-if="app.view === 'shops'" :app="app" />
      <UserCartView v-else-if="app.view === 'cart'" :app="app" />
      <UserAddressesView v-else-if="app.view === 'addresses'" :app="app" />
      <UserOrdersView v-else-if="app.view === 'orders'" :app="app" />

      <MerchantDashboardView v-else-if="app.view === 'merchant-dashboard'" :app="app" />
      <MerchantShopView v-else-if="app.view === 'merchant-shop'" :app="app" />
      <MerchantProductsView v-else-if="app.view === 'merchant-products'" :app="app" />
      <MerchantOrdersView v-else-if="app.view === 'merchant-orders'" :app="app" />
      <MerchantCommentsView v-else-if="app.view === 'merchant-comments'" :app="app" />

      <RiderOrdersView v-else-if="['rider-available', 'rider-mine'].includes(app.view)" :app="app" />

      <AdminDashboardView v-else-if="app.view === 'admin-dashboard'" :app="app" />
      <AdminUsersView v-else-if="app.view === 'admin-users'" :app="app" />
      <AdminShopsView v-else-if="app.view === 'admin-shops'" :app="app" />
      <AdminOrdersView v-else-if="app.view === 'admin-orders'" :app="app" />
      </main>
    </template>
  </div>
</template>
