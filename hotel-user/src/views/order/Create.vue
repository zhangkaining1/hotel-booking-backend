<template>
  <div class="order-create-page">
    <div class="container" v-loading="loading">
      
      <div class="page-header">
        <h2>确认您的预订</h2>
        <p>只需几步，即可完成酒店预订</p>
      </div>

      <el-row :gutter="24" class="layout-row">
        <!-- 左侧：表单区 -->
        <el-col :xs="24" :md="16">
          <el-card class="form-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><User /></el-icon>
                <span>填写您的详细信息</span>
              </div>
            </template>
            
            <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
              
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="选择酒店" prop="hotelId">
                    <el-select v-model="form.hotelId" placeholder="请选择您心仪的酒店" @change="onHotelChange" class="full-width">
                      <el-option v-for="item in hotelOptions" :key="item.id" :label="item.nameCn" :value="item.id" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="选择房型" prop="roomTypeId">
                    <el-select v-model="form.roomTypeId" placeholder="请选择房型" class="full-width" :disabled="!form.hotelId">
                      <el-option v-for="item in roomTypeOptions" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="入住日期" prop="checkinDate">
                    <el-date-picker v-model="form.checkinDate" type="date" placeholder="选择入住日期" value-format="YYYY-MM-DD" class="full-width" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="离店日期" prop="checkoutDate">
                    <el-date-picker v-model="form.checkoutDate" type="date" placeholder="选择离店日期" value-format="YYYY-MM-DD" class="full-width" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="需要几间客房？" prop="roomCount">
                <el-input-number v-model="form.roomCount" :min="1" :max="10" />
              </el-form-item>

              <el-divider border-style="dashed" />

              <div class="section-title">住客信息</div>
              
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="入住人姓名" prop="guestName">
                    <el-input v-model="form.guestName" placeholder="如：张三" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="联系手机号" prop="guestPhone">
                    <el-input v-model="form.guestPhone" placeholder="用于接收预订确认信息" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="特殊要求（选填）" class="special-request">
                <template #label>
                  <span>特殊要求（选填）</span>
                  <span class="hint-text">酒店会尽量满足您的要求，但无法保证。</span>
                </template>
                <el-input v-model="form.specialRequest" type="textarea" :rows="3" placeholder="例如：尽量安排高楼层，或者需要无烟房等..." />
              </el-form-item>

            </el-form>
          </el-card>
        </el-col>

        <!-- 右侧：订单摘要 -->
        <el-col :xs="24" :md="8">
          <el-card class="summary-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>预订明细</span>
              </div>
            </template>
            
            <div class="summary-content">
              <div v-if="selectedHotel" class="hotel-info">
                <h3>{{ selectedHotel.nameCn }}</h3>
                <p class="hotel-address"><el-icon><Location /></el-icon> {{ selectedHotel.address || '日本, 广岛' }}</p>
              </div>
              <div v-else class="empty-placeholder">
                请先在左侧选择酒店
              </div>

              <el-divider />

              <div class="booking-details">
                <div class="detail-row">
                  <span class="label">入住日期</span>
                  <span class="value">{{ form.checkinDate || '-' }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">离店日期</span>
                  <span class="value">{{ form.checkoutDate || '-' }}</span>
                </div>
                <div class="detail-row highlight-row">
                  <span class="label">总共入住</span>
                  <span class="value">{{ nights }} 晚</span>
                </div>
                <el-divider border-style="dashed" />
                <div class="detail-row">
                  <span class="label">房型</span>
                  <span class="value">{{ selectedRoom ? selectedRoom.name : '-' }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">房间数</span>
                  <span class="value">{{ form.roomCount }} 间</span>
                </div>
              </div>

              <div class="price-section" v-if="totalPrice > 0">
                <div class="price-row">
                  <span class="price-label">总价</span>
                  <span class="price-value">¥{{ totalPrice }}</span>
                </div>
                <p class="tax-info">包含税费和其他费用</p>
              </div>

              <div class="submit-action">
                <el-button type="primary" size="large" class="submit-btn" :loading="submitting" @click="submitOrder">
                  提交订单并支付
                </el-button>
                <el-button size="large" class="back-btn" plain @click="router.back()">返回</el-button>
              </div>

            </div>
          </el-card>
          
          <!-- 保障提示 -->
          <div class="trust-badges">
            <div class="badge-item">
              <el-icon><CircleCheck /></el-icon> <span>安全支付保障</span>
            </div>
            <div class="badge-item">
              <el-icon><Lock /></el-icon> <span>您的信息将被加密传输</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Location, CircleCheck, Lock } from '@element-plus/icons-vue'
import { searchHotels, getHotelRooms } from '@/api/modules/hotel'
import { createOrder } from '@/api/modules/order'

const route = useRoute()
const router = useRouter()
const formRef = ref()

const loading = ref(false)
const submitting = ref(false)
const hotelOptions = ref([])
const roomTypeOptions = ref([])

const form = reactive({
  hotelId: route.query.hotelId ? String(route.query.hotelId) : null,
  roomTypeId: route.query.roomTypeId ? String(route.query.roomTypeId) : null,
  checkinDate: route.query.checkinDate || '',
  checkoutDate: route.query.checkoutDate || '',
  roomCount: Number(route.query.roomCount || 1),
  guestName: '',
  guestPhone: '',
  specialRequest: '',
})

const rules = {
  hotelId: [{ required: true, message: '请选择酒店', trigger: 'change' }],
  roomTypeId: [{ required: true, message: '请选择房型', trigger: 'change' }],
  checkinDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
  checkoutDate: [{ required: true, message: '请选择离店日期', trigger: 'change' }],
  guestName: [{ required: true, message: '请输入入住人姓名', trigger: 'blur' }],
  guestPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
}

// Computed Properties for Summary
const selectedHotel = computed(() => {
  return hotelOptions.value.find(h => h.id === form.hotelId) || null
})

const selectedRoom = computed(() => {
  return roomTypeOptions.value.find(r => r.id === form.roomTypeId) || null
})

const nights = computed(() => {
  if (form.checkinDate && form.checkoutDate) {
    const start = new Date(form.checkinDate)
    const end = new Date(form.checkoutDate)
    const diffTime = end - start
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays > 0 ? diffDays : 0
  }
  return 0
})

const totalPrice = computed(() => {
  if (selectedRoom.value && nights.value > 0) {
    return selectedRoom.value.price * nights.value * form.roomCount
  }
  return 0
})

const fetchHotels = async () => {
  loading.value = true
  try {
    const res = await searchHotels({
      city: route.query.city || '',
      checkinDate: form.checkinDate,
      checkoutDate: form.checkoutDate,
      pageNum: 1,
      pageSize: 50,
    })
    hotelOptions.value = res.data.list || []
    if (form.hotelId) {
      await fetchRooms(form.hotelId)
    }
  } finally {
    loading.value = false
  }
}

const fetchRooms = async (hotelId) => {
  const res = await getHotelRooms(hotelId, {
    checkinDate: form.checkinDate,
    checkoutDate: form.checkoutDate,
  })
  roomTypeOptions.value = res.data || []
  const exists = roomTypeOptions.value.some((item) => item.id === form.roomTypeId)
  if (!exists) {
    form.roomTypeId = roomTypeOptions.value[0]?.id || null
  }
}

const onHotelChange = async (hotelId) => {
  form.roomTypeId = null
  await fetchRooms(hotelId)
}

const submitOrder = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (error) {
    // Validation failed, element-plus will show error messages automatically
    return
  }
  
  if (form.checkoutDate <= form.checkinDate) {
    ElMessage.warning('离店日期必须晚于入住日期')
    return
  }
  submitting.value = true
  try {
    const res = await createOrder(form)
    ElMessage.success('订单创建成功，即将跳转至详情...')
    router.push(`/user/orders/${res.data.orderId}`)
  } catch (error) {
    // API errors are handled by the global axios interceptor, so we just catch them here to avoid unhandled promise rejection in the console
  } finally {
    submitting.value = false
  }
}

fetchHotels()
</script>

<style scoped>
.order-create-page {
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 40px 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.container {
  max-width: 1140px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.page-header p {
  color: #666;
  margin: 0;
  font-size: 15px;
}

.form-card, .summary-card {
  border-radius: 12px;
  border: none;
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.card-header .el-icon {
  margin-right: 8px;
  color: #0071c2;
  font-size: 20px;
}

.full-width {
  width: 100%;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #1a1a1a;
}

.hint-text {
  font-size: 12px;
  color: #999;
  margin-left: 10px;
  font-weight: normal;
}

/* 覆盖默认的 label 样式以增加质感 */
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #333;
}

/* Summary Card Styles */
.summary-content {
  padding: 0 8px;
}

.hotel-info h3 {
  font-size: 18px;
  margin: 0 0 8px 0;
  color: #1a1a1a;
}

.hotel-address {
  font-size: 13px;
  color: #666;
  margin: 0;
  display: flex;
  align-items: flex-start;
}

.hotel-address .el-icon {
  margin-top: 2px;
  margin-right: 4px;
  color: #0071c2;
}

.empty-placeholder {
  color: #999;
  text-align: center;
  padding: 20px 0;
}

.booking-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.detail-row .label {
  color: #666;
}

.detail-row .value {
  font-weight: 600;
  color: #333;
}

.highlight-row {
  background-color: #f0f7fd;
  padding: 8px 12px;
  border-radius: 6px;
  margin: 4px -12px;
}

.price-section {
  background-color: #e6f0ff;
  border-radius: 8px;
  padding: 16px;
  margin-top: 24px;
  margin-bottom: 24px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-label {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.price-value {
  font-size: 28px;
  font-weight: 700;
  color: #0071c2;
}

.tax-info {
  margin: 8px 0 0 0;
  font-size: 12px;
  color: #666;
  text-align: right;
}

.submit-action {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.submit-btn {
  width: 100%;
  font-size: 16px;
  font-weight: bold;
  height: 48px;
  border-radius: 8px;
  background-color: #0071c2;
  border-color: #0071c2;
}

.submit-btn:hover {
  background-color: #005999;
}

.back-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
}

.trust-badges {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 0 16px;
}

.badge-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #2e7d32;
}

.badge-item .el-icon {
  font-size: 16px;
  margin-right: 8px;
}
</style>
