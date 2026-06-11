<template>
  <div class="pay-result-page">
    <el-card class="result-card">
      <el-result
        v-if="checking"
        icon="info"
        title="正在查询支付结果..."
        sub-title="请稍候"
      />
      <el-result
        v-else-if="paid"
        icon="success"
        title="支付成功"
        sub-title="您的订单已支付成功，酒店将尽快确认"
      >
        <template #extra>
          <el-button type="primary" @click="goOrder">查看订单</el-button>
          <el-button @click="router.push('/user/orders')">我的订单</el-button>
        </template>
      </el-result>
      <el-result
        v-else
        icon="warning"
        title="支付结果确认中"
        :sub-title="subTitle"
      >
        <template #extra>
          <el-button type="primary" :loading="checking" @click="checkStatus">刷新状态</el-button>
          <el-button @click="goOrder">查看订单</el-button>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { queryPayStatus } from '@/api/modules/pay'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 优先从 query.orderId 获取，否则尝试从支付宝回调参数 out_trade_no 解析
const outTradeNo = ref(route.query.out_trade_no || '')
const orderId = ref(route.query.orderId || '')
const paid = ref(false)
const checking = ref(true)
const errorMsg = ref('')

const subTitle = computed(() => {
  if (errorMsg.value) return errorMsg.value
  return '若您已完成支付，系统将在稍后自动更新订单状态，请手动刷新'
})

const goOrder = () => {
  if (orderId.value) {
    router.push(`/user/orders/${orderId.value}`)
  } else {
    router.push('/user/orders')
  }
}

const checkStatus = async () => {
  if (!orderId.value) {
    checking.value = false
    errorMsg.value = '缺少订单信息，无法查询支付状态'
    return
  }
  checking.value = true
  errorMsg.value = ''
  try {
    const res = await queryPayStatus(orderId.value)
    paid.value = res.data === true
    if (paid.value) {
      ElMessage.success('支付成功')
    }
  } catch (e) {
    errorMsg.value = '查询失败，请稍后重试'
  } finally {
    checking.value = false
  }
}

onMounted(() => {
  // 如果URL中有支付宝回调参数但没有orderId，展示提示
  if (!orderId.value && outTradeNo.value) {
    errorMsg.value = '支付回调已收到，请从订单列表查看支付结果'
  }
  checkStatus()
})
</script>

<style scoped>
.pay-result-page {
  max-width: 600px;
  margin: 40px auto;
  padding: 0 16px;
}
.result-card {
  border-radius: 12px;
}
</style>
