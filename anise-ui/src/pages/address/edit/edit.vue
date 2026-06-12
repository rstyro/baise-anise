<template>
  <demo-page hide-tabs show-wx-tips nav-title="地址编辑">
    <view class="wrap">
      <view class="top">
        <view class="item">
          <view class="left">收货人</view>
          <input v-model="form.realName" type="text" placeholder-class="line" placeholder="请填写收货人姓名" />
        </view>
        <view class="item">
          <view class="left">手机号码</view>
          <input v-model="form.phone" type="text" placeholder-class="line" placeholder="请填写收货人手机号" />
        </view>
        <view class="item" @click="show = true">
          <view class="left">所在地区</view>
          <u-input
            type="select"
            v-model="regionText"
            readonly
            placeholder="省市区县、乡镇等"
            @click="show = true"
          />
        </view>
        <view class="item address">
          <view class="left">详细地址</view>
          <textarea v-model="form.detailAddress" placeholder-class="line" placeholder="街道、楼牌等" />
        </view>
      </view>
      <view class="bottom">
        <view class="tag">
          <view class="left">标签</view>
          <view class="right">
            <text
              v-for="tag in tagOptions"
              :key="tag"
              class="tags"
              :class="{ active: form.tags.includes(tag) }"
              @click="toggleTag(tag)"
            >{{ tag }}</text>
            <view class="tags plus" @click="startAddTag"><u-icon size="22" name="plus" /></view>
          </view>
        </view>
        <view v-if="editingTag" class="tag-input">
          <input
            v-model="newTag"
            type="text"
            placeholder-class="line"
            placeholder="请输入标签名称"
            confirm-type="done"
            @confirm="addTag"
          />
          <view class="tag-input-actions">
            <u-button size="mini" type="primary" @click="addTag">添加</u-button>
            <u-button size="mini" type="info" plain @click="cancelAddTag">取消</u-button>
          </view>
        </view>
        <view class="default">
          <view class="left">
            <view class="set">设置默认地址</view>
            <view class="tips">提醒：每次下单会默认推荐该地址</view>
          </view>
          <view class="right">
            <switch :checked="form.isDefault === 1" @change="setDefault" color="#4caf50" />
          </view>
        </view>
      </view>
      <view class="option">
        <u-button type="primary" shape="circle" @click="submit">提交</u-button>
      </view>
      <u-picker mode="region" v-model="show" @confirm="confirm" />
    </view>
  </demo-page>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { addressApi } from '@/api/businessApi'

const form = reactive({
  id: null as number | null,
  realName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0,
  tags: [] as string[]
})

const tagOptions = ref(['家', '公司', '学校'])
const editingTag = ref(false)
const newTag = ref('')

const show = ref(false)

const regionText = computed(() => {
  if (!form.province) return ''
  return form.province + ' ' + form.city + ' ' + form.district
})

function setDefault(e: any) {
  form.isDefault = e.detail.value ? 1 : 0
}

function toggleTag(tag: string) {
  const exists = form.tags.includes(tag)
  form.tags = exists ? [] : [tag]
}

function startAddTag() {
  editingTag.value = true
  newTag.value = ''
}

function cancelAddTag() {
  editingTag.value = false
  newTag.value = ''
}

function addTag() {
  const value = newTag.value.trim()
  if (!value) { uni.showToast({ title: '请输入标签名称', icon: 'none' }); return }
  if (tagOptions.value.includes(value)) {
    form.tags = form.tags.includes(value) ? [] : [value]
    editingTag.value = false
    newTag.value = ''
    return
  }
  tagOptions.value.push(value)
  form.tags = [value]
  editingTag.value = false
  newTag.value = ''
}

function confirm(e: any) {
  form.province = e.province?.label || ''
  form.city = e.city?.label || ''
  form.district = e.area?.label || ''
}

async function loadAddress(id: number) {
  try {
    const list = await addressApi.list()
    const addr = (list || []).find((a: any) => a.id === id)
    if (addr) {
      form.id = addr.id
      form.realName = addr.realName || ''
      form.phone = addr.phone || ''
      form.province = addr.province || ''
      form.city = addr.city || ''
      form.district = addr.district || ''
      form.detailAddress = addr.detailAddress || ''
      form.isDefault = addr.isDefault || 0
      form.tags = addr.tags ? [addr.tags] : []
    }
  } catch (e) {
    console.error('loadAddress error', e)
  }
}

function validate() {
  if (!form.realName.trim()) { uni.showToast({ title: '请填写收货人姓名', icon: 'none' }); return false }
  if (!form.phone.trim()) { uni.showToast({ title: '请填写手机号', icon: 'none' }); return false }
  if (!form.province) { uni.showToast({ title: '请选择地区', icon: 'none' }); return false }
  if (!form.detailAddress.trim()) { uni.showToast({ title: '请填写详细地址', icon: 'none' }); return false }
  return true
}

async function submit() {
  if (!validate()) return
  try {
    const data = { ...form, tags: form.tags[0] || '' }
    if (form.id) {
      await addressApi.edit(data)
    } else {
      await addressApi.add(data)
    }
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) {
    console.error('save address error', e)
    uni.showToast({ title: '保存失败', icon: 'error' })
  }
}

onLoad((options: any) => {
  if (options && options.id) {
    loadAddress(Number(options.id))
  }
})
</script>

<style lang="scss" scoped>
::v-deep .line {
  color: $u-light-color;
  font-size: 28rpx;
}
.wrap {
  .top {
    background-color: $u-bg-white;
    border-top: solid 2rpx $u-border-color;
    padding: 22rpx;
    .item {
      display: flex;
      font-size: 32rpx;
      min-height: 100rpx;
      align-items: center;
      border-bottom: solid 2rpx $u-border-color;
      .left {
        width: 180rpx;
      }
      input {
        text-align: left;
      }
    }
    .address {
      padding: 20rpx 0;
      textarea {
        height: 150rpx;
        background-color: $u-bg-color;
        line-height: 60rpx;
        margin: 40rpx auto;
        padding: 20rpx;
      }
    }
  }
  .bottom {
    margin-top: 20rpx;
    padding: 40rpx;
    padding-right: 0;
    background-color: $u-bg-white;
    font-size: 28rpx;
    .tag {
      display: flex;
      .left { width: 160rpx; }
      .right {
        display: flex; flex-wrap: wrap;
        .tags {
          width: 140rpx; padding: 16rpx 8rpx; border: solid 2rpx $u-border-color;
          text-align: center; border-radius: 50rpx; margin: 0 10rpx 20rpx;
          display: flex; font-size: 28rpx; align-items: center; justify-content: center;
          color: $u-content-color; line-height: 1;
          &.active {
            border-color: $u-type-primary; color: $u-type-primary;
            background-color: rgba($u-type-primary, 0.08);
          }
        }
      }
    }
    .tag-input {
      margin: 20rpx 10rpx 0; padding: 20rpx;
      border: solid 2rpx $u-border-color; border-radius: 12rpx;
      background-color: $u-bg-color;
      input { background: $u-bg-white; padding: 16rpx 20rpx; border-radius: 8rpx; }
      .tag-input-actions { display: flex; margin-top: 20rpx; .u-button { margin-right: 16rpx; } }
    }
    .default {
      margin-top: 10rpx;
      display: flex;
      justify-content: space-between;
      border-bottom: solid 2rpx $u-border-color;
      line-height: 64rpx;
      .tips {
        font-size: 24rpx;
      }
    }
  }
  .option {
    background-color: $u-bg-white;
    padding: 40rpx;
    padding-bottom: 120rpx;
  }
}
</style>
