import { computed, onMounted, reactive, ref } from 'vue'
import { api, clearSession, getStoredProfile, getToken, saveSession } from '../api'
import { demoAccounts, guestTabs, roleNames, tabSets } from '../constants'
import { money, shortTime } from '../utils/format'

export function useAppState() {
  const view = ref('shops')
  const authMode = ref('login')
  const loading = ref(false)
  const message = ref('')
  const token = ref(getToken())
  const profile = ref(getStoredProfile())

  const shops = ref([])
  const activeShopId = ref(null)
  const shopDetail = ref(null)
  const cart = ref({ shopId: null, shopName: null, items: [], totalAmount: 0 })
  const addresses = ref([])
  const orders = ref([])
  const activeOrder = ref(null)
  const checkoutRemark = ref('')
  const selectedAddressId = ref(null)
  const editingAddressId = ref(null)

  const merchantShops = ref([])
  const merchantCategories = ref([])
  const merchantProducts = ref([])
  const merchantOrders = ref([])
  const merchantComments = ref([])
  const merchantShopId = ref(null)
  const activeMerchantOrder = ref(null)

  const availableOrders = ref([])
  const riderOrders = ref([])
  const activeRiderOrder = ref(null)

  const adminDashboard = ref(null)
  const adminUsers = ref([])
  const adminShops = ref([])
  const adminOrders = ref([])
  const activeAdminOrder = ref(null)

  const loginForm = reactive({ username: 'user', password: '123456' })
  const registerForm = reactive({
    username: '',
    password: '',
    displayName: '',
    phone: '',
    role: 'USER',
    shopName: '',
    shopAnnouncement: '',
    deliveryFee: 0,
    minOrderAmount: 0
  })
  const addressForm = reactive({ contactName: '', contactPhone: '', detailAddress: '', defaultAddress: false })
  const commentForm = reactive({ rating: 5, content: '' })
  const shopForm = reactive({ name: '', announcement: '', deliveryFee: 0, minOrderAmount: 0, open: true })
  const categoryForm = reactive({ id: null, shopId: null, name: '', sortOrder: 0 })
  const productForm = reactive({
    id: null,
    shopId: null,
    categoryId: null,
    name: '',
    description: '',
    price: 0,
    stock: 0,
    enabled: true
  })

  const isAuthed = computed(() => Boolean(token.value && profile.value))
  const currentRole = computed(() => profile.value?.role || 'USER')
  const roleLabel = computed(() => roleNames[currentRole.value] || '游客')
  const tabs = computed(() => (isAuthed.value ? tabSets[currentRole.value] || tabSets.USER : guestTabs))
  const isCustomerView = computed(() => !isAuthed.value || currentRole.value === 'USER')
  const isUserWorkspace = computed(() => isAuthed.value && currentRole.value === 'USER')
  const cartCount = computed(() => cart.value.items.reduce((sum, item) => sum + item.quantity, 0))
  const cartShop = computed(() => shops.value.find((shop) => shop.id === cart.value.shopId) || null)
  const checkoutPayAmount = computed(() => Number(cart.value.totalAmount || 0) + Number(cartShop.value?.deliveryFee || 0))
  const cartProductIds = computed(() => new Set(cart.value.items.map((item) => item.productId)))
  const canCheckout = computed(() => isAuthed.value && currentRole.value === 'USER' && cart.value.items.length > 0 && selectedAddressId.value)
  const merchantShopCategories = computed(() => merchantCategories.value.filter((item) => item.shopId === merchantShopId.value))
  const merchantShopProducts = computed(() => merchantProducts.value.filter((item) => item.shopId === merchantShopId.value))
  const merchantStats = computed(() => {
    const pending = merchantOrders.value.filter((order) => order.status === 'PAID').length
    const preparing = merchantOrders.value.filter((order) => ['ACCEPTED', 'PREPARING'].includes(order.status)).length
    const ready = merchantOrders.value.filter((order) => order.status === 'READY_FOR_DELIVERY').length
    const revenue = merchantOrders.value
      .filter((order) => order.status === 'COMPLETED')
      .reduce((sum, order) => sum + Number(order.payAmount || 0), 0)
    return { pending, preparing, ready, revenue }
  })

  function notify(text) {
    message.value = text
    window.clearTimeout(notify.timer)
    notify.timer = window.setTimeout(() => {
      message.value = ''
    }, 2800)
  }

  async function run(action, successText) {
    loading.value = true
    try {
      const result = await action()
      if (successText) notify(successText)
      return result === null ? true : result
    } catch (error) {
      notify(error.message || '操作失败')
      return null
    } finally {
      loading.value = false
    }
  }

  function fillLogin(account) {
    loginForm.username = account.username
    loginForm.password = account.password
    authMode.value = 'login'
  }

  function selectAuthRole(account) {
    if (authMode.value === 'register') {
      if (account.role === 'ADMIN') return
      registerForm.role = account.role
      return
    }
    fillLogin(account)
  }

  function homeViewFor(role) {
    if (role === 'MERCHANT') return 'merchant-dashboard'
    if (role === 'RIDER') return 'rider-available'
    if (role === 'ADMIN') return 'admin-dashboard'
    return 'shops'
  }

  function resetComment() {
    commentForm.rating = 5
    commentForm.content = ''
  }

  function resetAddressForm() {
    editingAddressId.value = null
    Object.assign(addressForm, { contactName: '', contactPhone: '', detailAddress: '', defaultAddress: false })
  }

  function emptyCart() {
    cart.value = { shopId: null, shopName: null, items: [], totalAmount: 0 }
  }

  async function loadPublicData() {
    const data = await api.shops()
    shops.value = data || []
    if (!activeShopId.value && shops.value.length > 0) {
      await selectShop(shops.value[0].id)
    } else if (activeShopId.value) {
      await selectShop(activeShopId.value)
    }
  }

  async function loadPrivateData() {
    if (!token.value) return
    const me = await run(() => api.me())
    if (!me) {
      clearSession()
      token.value = null
      profile.value = null
      view.value = 'shops'
      return
    }
    profile.value = { ...profile.value, ...me }
    view.value = homeViewFor(profile.value.role)

    if (profile.value.role === 'USER') await refreshUserData()
    if (profile.value.role === 'MERCHANT') await refreshMerchantData()
    if (profile.value.role === 'RIDER') await refreshRiderData()
    if (profile.value.role === 'ADMIN') await refreshAdminData()
  }

  async function selectShop(shopId) {
    activeShopId.value = shopId
    shopDetail.value = await run(() => api.shopDetail(shopId))
  }

  async function submitLogin() {
    const auth = await run(() => api.login(loginForm), '登录成功')
    if (!auth) return
    saveSession(auth)
    token.value = auth.token
    profile.value = auth
    view.value = homeViewFor(auth.role)
    await loadPublicData()
    await loadPrivateData()
  }

  async function submitRegister() {
    const auth = await run(() => api.register(registerForm), '注册成功')
    if (!auth) return
    saveSession(auth)
    token.value = auth.token
    profile.value = auth
    Object.assign(registerForm, {
      username: '',
      password: '',
      displayName: '',
      phone: '',
      role: 'USER',
      shopName: '',
      shopAnnouncement: '',
      deliveryFee: 0,
      minOrderAmount: 0
    })
    view.value = homeViewFor(auth.role)
    await loadPrivateData()
  }

  function logout() {
    clearSession()
    token.value = null
    profile.value = null
    view.value = 'shops'
    emptyCart()
    addresses.value = []
    orders.value = []
    activeOrder.value = null
    selectedAddressId.value = null
    merchantShops.value = []
    merchantCategories.value = []
    merchantProducts.value = []
    merchantOrders.value = []
    merchantComments.value = []
    activeMerchantOrder.value = null
    availableOrders.value = []
    riderOrders.value = []
    activeRiderOrder.value = null
    adminDashboard.value = null
    adminUsers.value = []
    adminShops.value = []
    adminOrders.value = []
    activeAdminOrder.value = null
    notify('已退出登录')
  }

  async function refreshUserData() {
    const [cartResult, addressResult, orderResult] = await Promise.allSettled([
      api.cart(),
      api.addresses(),
      api.orders()
    ])
    if (cartResult.status === 'fulfilled') cart.value = cartResult.value
    if (addressResult.status === 'fulfilled') {
      addresses.value = addressResult.value || []
      const stillExists = addresses.value.some((item) => item.id === selectedAddressId.value)
      if (!stillExists) {
        selectedAddressId.value = addresses.value.find((item) => item.defaultAddress)?.id || addresses.value[0]?.id || null
      }
    }
    if (orderResult.status === 'fulfilled') orders.value = orderResult.value || []
  }

  async function addProduct(product) {
    if (!isAuthed.value || currentRole.value !== 'USER') {
      authMode.value = 'login'
      notify('请使用用户账号登录后点餐')
      return
    }
    const nextCart = await run(() => api.addCartItem({ productId: product.id, quantity: 1 }), '已加入购物车')
    if (nextCart) cart.value = nextCart
  }

  async function changeCartItem(item, delta) {
    const quantity = item.quantity + delta
    const nextCart = quantity <= 0
      ? await run(() => api.removeCartItem(item.id), '已移除商品')
      : await run(() => api.updateCartItem(item.id, quantity))
    if (nextCart) cart.value = nextCart
  }

  async function clearMyCart() {
    const ok = await run(() => api.clearCart(), '购物车已清空')
    if (ok) emptyCart()
  }

  function editAddress(address) {
    editingAddressId.value = address.id
    Object.assign(addressForm, {
      contactName: address.contactName,
      contactPhone: address.contactPhone,
      detailAddress: address.detailAddress,
      defaultAddress: address.defaultAddress
    })
    view.value = 'addresses'
  }

  async function saveAddress() {
    const action = editingAddressId.value
      ? () => api.updateAddress(editingAddressId.value, addressForm)
      : () => api.createAddress(addressForm)
    const saved = await run(action, editingAddressId.value ? '地址已更新' : '地址已保存')
    if (!saved) return
    resetAddressForm()
    addresses.value = await api.addresses()
    selectedAddressId.value = saved.id
  }

  async function makeDefaultAddress(addressId) {
    const saved = await run(() => api.makeDefaultAddress(addressId), '默认地址已更新')
    if (!saved) return
    addresses.value = await api.addresses()
    selectedAddressId.value = saved.id
  }

  async function removeAddress(addressId) {
    const ok = await run(() => api.deleteAddress(addressId), '地址已删除')
    if (!ok) return
    addresses.value = await api.addresses()
    selectedAddressId.value = addresses.value.find((item) => item.defaultAddress)?.id || addresses.value[0]?.id || null
    if (editingAddressId.value === addressId) resetAddressForm()
  }

  async function createOrder() {
    if (!canCheckout.value) {
      notify('请选择收货地址')
      return
    }
    const order = await run(
      () => api.createOrder({ addressId: selectedAddressId.value, remark: checkoutRemark.value }),
      '下单成功'
    )
    if (!order) return
    checkoutRemark.value = ''
    activeOrder.value = order
    view.value = 'orders'
    cart.value = await api.cart()
    orders.value = await api.orders()
    await loadPublicData()
  }

  async function refreshOrder(orderId) {
    activeOrder.value = await run(() => api.orderDetail(orderId))
  }

  async function payOrder(orderId) {
    const order = await run(() => api.payOrder(orderId), '支付成功')
    if (!order) return
    activeOrder.value = order
    orders.value = await api.orders()
  }

  async function cancelOrder(orderId) {
    const order = await run(() => api.cancelOrder(orderId), '订单已取消')
    if (!order) return
    activeOrder.value = order
    orders.value = await api.orders()
    await loadPublicData()
  }

  async function requestRefund(orderId) {
    const order = await run(() => api.requestRefund(orderId), '退款申请已提交')
    if (!order) return
    activeOrder.value = order
    orders.value = await api.orders()
  }

  async function confirmOrder(orderId) {
    const order = await run(() => api.confirmOrder(orderId), '确认收货成功')
    if (!order) return
    activeOrder.value = order
    orders.value = await api.orders()
  }

  async function submitComment(orderId) {
    const comment = await run(() => api.commentOrder(orderId, commentForm), '评价成功')
    if (!comment) return
    resetComment()
    await refreshOrder(orderId)
    orders.value = await api.orders()
  }

  function selectMerchantShop(shopId) {
    merchantShopId.value = shopId
    const shop = merchantShops.value.find((item) => item.id === shopId)
    if (!shop) return
    Object.assign(shopForm, {
      name: shop.name,
      announcement: shop.announcement,
      deliveryFee: Number(shop.deliveryFee || 0),
      minOrderAmount: Number(shop.minOrderAmount || 0),
      open: Boolean(shop.open)
    })
    categoryForm.shopId = shopId
    productForm.shopId = shopId
    if (!productForm.categoryId) {
      productForm.categoryId = merchantShopCategories.value[0]?.id || null
    }
  }

  async function refreshMerchantData() {
    const [shopsResult, categoriesResult, productsResult, ordersResult, commentsResult] = await Promise.all([
      api.merchantShops(),
      api.merchantCategories(),
      api.merchantProducts(),
      api.merchantOrders(),
      api.merchantComments()
    ])
    merchantShops.value = shopsResult || []
    merchantCategories.value = categoriesResult || []
    merchantProducts.value = productsResult || []
    merchantOrders.value = ordersResult || []
    merchantComments.value = commentsResult || []
    if (!merchantShopId.value && merchantShops.value.length > 0) {
      selectMerchantShop(merchantShops.value[0].id)
    } else if (merchantShopId.value) {
      selectMerchantShop(merchantShopId.value)
    }
  }

  async function saveShop() {
    if (!merchantShopId.value) return
    const saved = await run(
      () => api.updateMerchantShop(merchantShopId.value, {
        name: shopForm.name,
        announcement: shopForm.announcement,
        deliveryFee: Number(shopForm.deliveryFee),
        minOrderAmount: Number(shopForm.minOrderAmount),
        open: shopForm.open
      }),
      '店铺已更新'
    )
    if (!saved) return
    await refreshMerchantData()
    await loadPublicData()
  }

  function resetCategoryForm() {
    Object.assign(categoryForm, { id: null, shopId: merchantShopId.value, name: '', sortOrder: 0 })
  }

  function editCategory(category) {
    Object.assign(categoryForm, {
      id: category.id,
      shopId: category.shopId,
      name: category.name,
      sortOrder: category.sortOrder
    })
  }

  async function saveCategory() {
    const body = {
      shopId: Number(categoryForm.shopId),
      name: categoryForm.name,
      sortOrder: Number(categoryForm.sortOrder)
    }
    const saved = categoryForm.id
      ? await run(() => api.updateMerchantCategory(categoryForm.id, body), '分类已更新')
      : await run(() => api.createMerchantCategory(body), '分类已新增')
    if (!saved) return
    resetCategoryForm()
    await refreshMerchantData()
    await loadPublicData()
  }

  async function removeCategory(categoryId) {
    const ok = await run(() => api.deleteMerchantCategory(categoryId), '分类已删除')
    if (!ok) return
    await refreshMerchantData()
    await loadPublicData()
  }

  function resetProductForm() {
    Object.assign(productForm, {
      id: null,
      shopId: merchantShopId.value,
      categoryId: merchantShopCategories.value[0]?.id || null,
      name: '',
      description: '',
      price: 0,
      stock: 0,
      enabled: true
    })
  }

  function editProduct(product) {
    Object.assign(productForm, {
      id: product.id,
      shopId: product.shopId,
      categoryId: product.categoryId,
      name: product.name,
      description: product.description,
      price: Number(product.price || 0),
      stock: product.stock,
      enabled: product.enabled
    })
    merchantShopId.value = product.shopId
    selectMerchantShop(product.shopId)
  }

  function changeProductShop(shopId) {
    const previousCategoryId = productForm.categoryId
    selectMerchantShop(shopId)
    if (!merchantShopCategories.value.some((category) => category.id === previousCategoryId)) {
      productForm.categoryId = merchantShopCategories.value[0]?.id || null
    }
  }

  async function saveProduct() {
    const body = {
      shopId: Number(productForm.shopId),
      categoryId: Number(productForm.categoryId),
      name: productForm.name,
      description: productForm.description,
      price: Number(productForm.price),
      stock: Number(productForm.stock),
      enabled: productForm.enabled
    }
    const saved = productForm.id
      ? await run(() => api.updateMerchantProduct(productForm.id, body), '菜品已更新')
      : await run(() => api.createMerchantProduct(body), '菜品已新增')
    if (!saved) return
    resetProductForm()
    await refreshMerchantData()
    await loadPublicData()
  }

  async function disableProduct(productId) {
    const saved = await run(() => api.disableMerchantProduct(productId), '菜品已下架')
    if (!saved) return
    await refreshMerchantData()
    await loadPublicData()
  }

  async function loadMerchantOrder(orderId) {
    activeMerchantOrder.value = await run(() => api.merchantOrderDetail(orderId))
  }

  async function merchantOrderAction(action, successText) {
    if (!activeMerchantOrder.value) return
    const order = await run(() => api[action](activeMerchantOrder.value.id), successText)
    if (!order) return
    activeMerchantOrder.value = order
    merchantOrders.value = await api.merchantOrders()
  }

  async function refreshRiderData() {
    const [availableResult, mineResult] = await Promise.all([api.riderAvailable(), api.riderMine()])
    availableOrders.value = availableResult || []
    riderOrders.value = mineResult || []
  }

  async function loadRiderOrder(orderId) {
    activeRiderOrder.value = await run(() => api.riderOrderDetail(orderId))
  }

  async function takeRiderOrder(orderId) {
    const order = await run(() => api.riderTake(orderId), '接单成功')
    if (!order) return
    activeRiderOrder.value = order
    await refreshRiderData()
  }

  async function markRiderDelivered(orderId) {
    const order = await run(() => api.riderDelivered(orderId), '已标记送达')
    if (!order) return
    activeRiderOrder.value = order
    await refreshRiderData()
  }

  async function refreshAdminData() {
    const [dashboardResult, usersResult, shopsResult, ordersResult] = await Promise.all([
      api.adminDashboard(),
      api.adminUsers(),
      api.adminShops(),
      api.adminOrders()
    ])
    adminDashboard.value = dashboardResult
    adminUsers.value = usersResult || []
    adminShops.value = shopsResult || []
    adminOrders.value = ordersResult || []
  }

  async function loadAdminOrder(orderId) {
    activeAdminOrder.value = await run(() => api.adminOrderDetail(orderId))
  }

  async function deleteAdminOrder(orderId) {
    const ok = await run(() => api.deleteAdminOrder(orderId), '订单已删除')
    if (!ok) return
    activeAdminOrder.value = null
    await refreshAdminData()
  }

  async function toggleAdminUserStatus(user) {
    const nextEnabled = !user.enabled
    const saved = await run(
      () => api.updateAdminUserStatus(user.id, nextEnabled),
      nextEnabled ? '用户已启用' : '用户已禁用'
    )
    if (!saved) return
    await refreshAdminData()
  }

  async function toggleAdminShopStatus(shop) {
    const nextOpen = !shop.open
    const saved = await run(
      () => api.updateAdminShopStatus(shop.id, nextOpen),
      nextOpen ? '店铺已恢复营业' : '店铺已关闭'
    )
    if (!saved) return
    await refreshAdminData()
    await loadPublicData()
  }

  onMounted(async () => {
    await run(async () => {
      await loadPublicData()
      await loadPrivateData()
    })
  })

  return {
    roleNames,
    demoAccounts,
    view,
    authMode,
    loading,
    message,
    token,
    profile,
    shops,
    activeShopId,
    shopDetail,
    cart,
    addresses,
    orders,
    activeOrder,
    checkoutRemark,
    selectedAddressId,
    editingAddressId,
    merchantShops,
    merchantCategories,
    merchantProducts,
    merchantOrders,
    merchantComments,
    merchantShopId,
    activeMerchantOrder,
    availableOrders,
    riderOrders,
    activeRiderOrder,
    adminDashboard,
    adminUsers,
    adminShops,
    adminOrders,
    activeAdminOrder,
    loginForm,
    registerForm,
    addressForm,
    commentForm,
    shopForm,
    categoryForm,
    productForm,
    isAuthed,
    currentRole,
    roleLabel,
    tabs,
    isCustomerView,
    isUserWorkspace,
    cartCount,
    checkoutPayAmount,
    cartProductIds,
    canCheckout,
    merchantShopCategories,
    merchantShopProducts,
    merchantStats,
    money,
    shortTime,
    fillLogin,
    selectAuthRole,
    homeViewFor,
    resetAddressForm,
    loadPublicData,
    loadPrivateData,
    selectShop,
    submitLogin,
    submitRegister,
    logout,
    refreshUserData,
    addProduct,
    changeCartItem,
    clearMyCart,
    editAddress,
    saveAddress,
    makeDefaultAddress,
    removeAddress,
    createOrder,
    refreshOrder,
    payOrder,
    cancelOrder,
    requestRefund,
    confirmOrder,
    submitComment,
    selectMerchantShop,
    refreshMerchantData,
    saveShop,
    resetCategoryForm,
    editCategory,
    saveCategory,
    removeCategory,
    resetProductForm,
    editProduct,
    changeProductShop,
    saveProduct,
    disableProduct,
    loadMerchantOrder,
    merchantOrderAction,
    refreshRiderData,
    loadRiderOrder,
    takeRiderOrder,
    markRiderDelivered,
    refreshAdminData,
    loadAdminOrder,
    deleteAdminOrder,
    toggleAdminUserStatus,
    toggleAdminShopStatus
  }
}
