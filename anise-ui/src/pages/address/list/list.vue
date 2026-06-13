<template>
  <view class="page">
    <view class="wrap">
      <view class="item" v-for="res in siteList" :key="res.id" @click="onItemClick(res)">
        <view class="top">
          <view class="name">{{ res.realName }}</view>
          <view class="phone">{{ res.phone }}</view>
          <view class="tag">
            <text v-if="res.isDefault === 1" class="red">默认</text>
            <text v-for="(item, idx) in (res.tags || [])" :key="idx">{{ item }}</text>
          </view>
        </view>
        <view class="bottom">
          <text class="addr">{{ res.province }} {{ res.city }} {{ res.district }} {{ res.detailAddress }}</text>
          <view class="actions">
            <u-icon @click="editSite(res.id)" name="edit-pen" :size="40" color="#999999" />
            <u-icon @click="deleteSite(res.id)" name="trash" :size="40" color="#f56c6c" />
          </view>
        </view>
      </view>

      <view v-if="!siteList.length" class="empty">
        <u-gap height="200" />
        <u-empty mode="list" text="暂无地址，点击下方新增" />
      </view>

      <view class="addSite" @tap="toAddSite">
        <view class="add">
          <u-icon name="plus" color="#ffffff" class="icon" :size="30" />新建收货地址
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow, onLoad } from '@dcloudio/uni-app'
import { addressApi } from '@/api/businessApi'

const mode = ref('')
const siteList = ref<any[]>([])

onLoad((options: any) => {
  mode.value = options?.mode || ''
})

async function loadList() {
  try {
    const list = await addressApi.list() || []
    siteList.value = list.map((item: any) => ({
      ...item,
      tags: item.tags ? [item.tags] : []
    }))
  } catch (e) {
    console.error('load address error', e)
  }
}

function onItemClick(addr: any) {
  if (mode.value === 'select') {
    console.log("addr::",addr)
    getApp().globalData.selectedAddress = addr
    uni.navigateBack()
  } else {
    uni.$grouter.navigateTo('addressEdit', { query: { id: addr.id } })
  }
}

function toAddSite() {
  uni.$grouter.navigateTo('addressEdit')
}

function editSite(id: number) {
  uni.$grouter.navigateTo('addressEdit', { query: { id } })
}

function deleteSite(id: number) {
  uni.showModal({
    title: '删除地址',
    content: '确定要删除该地址吗？',
    success: async (res) => {
      if (res.confirm) {
        await addressApi.delete(id)
        loadList()
      }
    }
  })
}

onShow(loadList)
</script>

<style lang="scss" scoped>
.page {
  background: #f5f9f5;
  min-height: 100vh;
}
.wrap {
  background-color: $u-bg-white;
}
.item {
  padding: 40rpx 20rpx;
  .top {
    display: flex;
    font-weight: bold;
    font-size: 34rpx;
    .phone {
      margin-left: 60rpx;
    }
    .tag {
      display: flex;
      font-weight: normal;
      align-items: center;
      text {
        display: block;
        width: 60rpx;
        height: 34rpx;
        line-height: 34rpx;
        color: #ffffff;
        font-size: 20rpx;
        border-radius: 6rpx;
        text-align: center;
        margin-left: 30rpx;
        background-color: rgb(49, 145, 253);
      }
      .red {
        background-color: red;
      }
    }
  }
  .bottom {
    display: flex;
    margin-top: 20rpx;
    font-size: 28rpx;
    justify-content: space-between;
    color: #999999;
    .addr {
      flex: 1;
      padding-right: 20rpx;
    }
    .actions {
      display: flex;
      gap: 20rpx;
    }
  }
}
.addSite {
  display: flex;
  justify-content: space-around;
  width: 600rpx;
  line-height: 100rpx;
  position: fixed;
  bottom: calc(env(safe-area-inset-bottom) + 20px);
  left: 50%;
  transform: translateX(-50%);
  background-color: #4caf50;
  border-radius: 60rpx;
  font-size: 30rpx;
  .add {
    display: flex;
    align-items: center;
    color: #ffffff;
    .icon {
      margin-right: 10rpx;
    }
  }
}
</style>
