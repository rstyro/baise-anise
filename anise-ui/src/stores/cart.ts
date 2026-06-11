import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 购物车商品类型
interface CartItem {
  id: number
  name: string
  price: number
  quantity: number
}

export const useCartStore = defineStore('cart', () => {
  // State: 购物车商品列表
  const items = ref<CartItem[]>([])

  // Getter: 商品总数量
  const totalCount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  // Getter: 商品总金额
  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  // Action: 添加商品到购物车
  function addItem(goods: { id: number; name: string; price: number }) {
    const existingItem = items.value.find(item => item.id === goods.id)
    if (existingItem) {
      // 如果商品已存在，数量+1
      existingItem.quantity++
    } else {
      // 如果商品不存在，添加新商品
      items.value.push({
        id: goods.id,
        name: goods.name,
        price: goods.price,
        quantity: 1
      })
    }
  }

  // Action: 增加商品数量
  function increase(id: number) {
    const item = items.value.find(item => item.id === id)
    if (item) {
      item.quantity++
    }
  }

  // Action: 减少商品数量
  function decrease(id: number) {
    const item = items.value.find(item => item.id === id)
    if (item) {
      item.quantity--
      // 如果数量为0，从购物车移除
      if (item.quantity <= 0) {
        removeItem(id)
      }
    }
  }

  // Action: 移除商品
  function removeItem(id: number) {
    const index = items.value.findIndex(item => item.id === id)
    if (index > -1) {
      items.value.splice(index, 1)
    }
  }

  // Action: 清空购物车
  function clear() {
    items.value = []
  }

  return {
    items,
    totalCount,
    totalPrice,
    addItem,
    increase,
    decrease,
    removeItem,
    clear
  }
})
