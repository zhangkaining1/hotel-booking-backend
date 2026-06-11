<template>
  <div class="price-page">
    <div class="page-header">
      <h2 class="page-title">房价与库存日历</h2>
    </div>

    <div class="filter-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="选择房型">
          <el-select v-model="queryForm.roomTypeId" placeholder="请选择房型" style="width: 200px" @change="loadCalendar">
            <el-option
              v-for="item in roomTypes"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="showBatchDialog" :disabled="!queryForm.roomTypeId">
            批量设置价格/库存
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card" v-loading="loading">
      <el-calendar v-model="currentDate" v-if="queryForm.roomTypeId">
        <template #date-cell="{ data }">
          <div class="calendar-cell" @click="handleDateClick(data.day)">
            <div class="date-num">{{ data.day.split('-')[2] }}</div>
            <div v-if="getCalendarData(data.day)" class="cal-info">
              <div class="cal-price">¥{{ getCalendarData(data.day).price || '-' }}</div>
              <div class="cal-inv">
                库: {{ getCalendarData(data.day).totalInventory || 0 }}
                余: {{ (getCalendarData(data.day).totalInventory || 0) - (getCalendarData(data.day).usedInventory || 0) - (getCalendarData(data.day).lockedInventory || 0) }}
              </div>
            </div>
            <div v-else class="cal-info empty">
              暂无配置
            </div>
          </div>
        </template>
      </el-calendar>
      <div v-else style="padding: 40px; text-align: center; color: var(--color-text-secondary);">
        请先选择需要配置的房型
      </div>
    </div>

    <!-- 批量设置弹窗 -->
    <el-dialog title="批量设置价格/库存" v-model="batchDialogVisible" width="500px">
      <el-form :model="batchForm" :rules="rules" ref="batchFormRef" label-width="100px">
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="batchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="batchForm.price" :min="0" :precision="2" :step="10" placeholder="不填则不修改" />
        </el-form-item>
        <el-form-item label="总库存" prop="inventory">
          <el-input-number v-model="batchForm.inventory" :min="0" :step="1" placeholder="不填则不修改" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBatchSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const queryForm = ref({
  roomTypeId: ''
})
const roomTypes = ref([])
const currentDate = ref(new Date())
const calendarData = ref([])
const loading = ref(false)

const batchDialogVisible = ref(false)
const saving = ref(false)
const batchFormRef = ref(null)
const batchForm = ref({
  dateRange: [],
  price: undefined,
  inventory: undefined
})
const rules = {
  dateRange: [{ required: true, message: '请选择日期范围', trigger: 'change' }]
}

onMounted(() => {
  loadRoomTypes()
})

watch(currentDate, () => {
  if (queryForm.value.roomTypeId) {
    loadCalendar()
  }
})

const loadRoomTypes = async () => {
  try {
    const res = await request.get('/admin/room/types', { params: { pageSize: 100 } })
    roomTypes.value = res.data?.list || []
    if (roomTypes.value.length > 0) {
      queryForm.value.roomTypeId = roomTypes.value[0].id
      loadCalendar()
    }
  } catch (error) {
    console.error('获取房型失败:', error)
  }
}

const loadCalendar = async () => {
  if (!queryForm.value.roomTypeId) return
  
  loading.value = true
  try {
    const date = dayjs(currentDate.value)
    const startDate = date.startOf('month').subtract(7, 'day').format('YYYY-MM-DD')
    const endDate = date.endOf('month').add(7, 'day').format('YYYY-MM-DD')
    
    const res = await request.get('/admin/room/calendar', {
      params: {
        roomTypeId: queryForm.value.roomTypeId,
        startDate,
        endDate
      }
    })
    calendarData.value = res.data || []
  } catch (error) {
    console.error('加载日历失败:', error)
  } finally {
    loading.value = false
  }
}

const getCalendarData = (day) => {
  return calendarData.value.find(item => item.date === day)
}

const handleDateClick = (day) => {
  batchForm.value.dateRange = [day, day]
  batchForm.value.price = undefined
  batchForm.value.inventory = undefined
  batchDialogVisible.value = true
}

const showBatchDialog = () => {
  const start = dayjs(currentDate.value).startOf('month').format('YYYY-MM-DD')
  const end = dayjs(currentDate.value).endOf('month').format('YYYY-MM-DD')
  batchForm.value.dateRange = [start, end]
  batchForm.value.price = undefined
  batchForm.value.inventory = undefined
  batchDialogVisible.value = true
}

const submitBatchSave = () => {
  batchFormRef.value.validate(async (valid) => {
    if (valid) {
      if (batchForm.value.price === undefined && batchForm.value.inventory === undefined) {
        ElMessage.warning('价格和库存至少填一项')
        return
      }
      
      saving.value = true
      try {
        await request.post('/admin/room/calendar', {
          roomTypeId: queryForm.value.roomTypeId,
          startDate: batchForm.value.dateRange[0],
          endDate: batchForm.value.dateRange[1],
          price: batchForm.value.price,
          inventory: batchForm.value.inventory
        })
        ElMessage.success('保存成功')
        batchDialogVisible.value = false
        loadCalendar()
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        saving.value = false
      }
    }
  })
}
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { margin-bottom: 0; }
.filter-card { background: var(--color-bg-card); padding: 20px; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); }
.table-card { background: var(--color-bg-card); padding: 20px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); }

.calendar-cell {
  height: 100%;
  padding: 4px;
  display: flex;
  flex-direction: column;
}
.date-num {
  font-weight: bold;
  margin-bottom: 4px;
}
.cal-info {
  font-size: 12px;
  line-height: 1.5;
}
.cal-price {
  color: var(--el-color-danger);
  font-weight: bold;
}
.cal-inv {
  color: var(--color-text-secondary);
}
.cal-info.empty {
  color: var(--color-text-placeholder);
  font-style: italic;
}
</style>
