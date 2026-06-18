import { payApi } from '@/api/businessApi'

interface PayParams {
  prepayId?: string
  timeStamp?: string
  nonceStr?: string
  signType?: string
  paySign?: string
}

export interface PayOrderResult {
  orderNo?: string
  payAmount?: number | string
}

const isMockPay = (params: PayParams) => {
  return !params.prepayId || String(params.prepayId).startsWith('prepay_mock_')
}

const requestMiniProgramPay = (params: PayParams): Promise<void> => {
  return new Promise((resolve, reject) => {
    // #ifdef MP-WEIXIN
    if (!isMockPay(params)) {
      uni.requestPayment({
        provider: 'wxpay',
        timeStamp: params.timeStamp || '',
        nonceStr: params.nonceStr || '',
        package: `prepay_id=${params.prepayId}`,
        signType: params.signType || 'MD5',
        paySign: params.paySign || '',
        success: () => resolve(),
        fail: reject,
      })
      return
    }
    // #endif
    resolve()
  })
}

/**
 * 小程序支付骨架。
 * 当前后端返回 mock prepay 参数时直接走开发模拟支付；接入真实微信支付后，小程序端会拉起 requestPayment。
 */
export const payOrder = async (orderId: number): Promise<PayOrderResult> => {
  const payParams = await payApi.unifiedOrder(orderId) as PayParams & PayOrderResult
  await requestMiniProgramPay(payParams)
  await payApi.mockPaySuccess(orderId)
  return payParams
}
