<template>
  <div class="checkin-page">
    <div class="page-header">
      <h2 class="page-title">入住管理</h2>
      <el-button type="primary" @click="openCheckinDialog">
        <el-icon><Plus /></el-icon>办理入住
      </el-button>
    </div>

    <div class="search-bar" style="margin-bottom: 20px;">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="入住人">
          <el-input v-model="queryForm.guestName" placeholder="姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="关联订单号" width="160" />
        <el-table-column prop="guestName" label="入住人姓名" width="120" />
        <el-table-column prop="idCard" label="身份证号" min-width="180" />
        <el-table-column prop="roomType" label="房型" width="120" />
        <el-table-column prop="roomNo" label="房间号" width="100">
          <template #default="scope">
            <el-tag type="success">{{ scope.row.roomNo }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkinTime" label="办理入住时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已入住' ? 'primary' : 'info'">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.status === '已入住'" link type="danger" size="small" @click="handleCheckout(scope.row)">退房</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container" v-if="total > 0" style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </div>

    <!-- 办理入住弹窗 -->
    <el-dialog title="办理入住" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="订单编号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入关联订单号" @blur="loadAvailableRoomsByOrder" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入入住人身份证号" />
        </el-form-item>
        <el-form-item label="分配客房" prop="guestRoomId">
          <el-select
            v-model="form.guestRoomId"
            placeholder="请先输入有效订单号"
            style="width: 100%"
            :loading="roomsLoading"
            :disabled="!availableRooms.length"
          >
            <el-option
              v-for="room in availableRooms"
              :key="room.id"
              :label="room.roomNo + ' (' + room.roomType + ')'"
              :value="room.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitCheckin">确定办理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const queryForm = ref({ orderNo: '', guestName: '' })

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref(null)
const availableRooms = ref([])
const roomsLoading = ref(false)
const form = ref({
  orderNo: '',
  idCard: '',
  guestRoomId: null
})

const rules = {
  orderNo: [{ required: true, message: '请输入关联订单号', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  guestRoomId: [{ required: true, message: '请选择分配的客房', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/checkins', {
      params: { 
        pageNum: currentPage.value, 
        pageSize: pageSize.value,
        orderNo: queryForm.value.orderNo,
        guestName: queryForm.value.guestName
      }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (err) {
    console.error('获取入住列表失败:', err)
  } finally {
    loading.value = false
  }
}

const loadAvailableRoomsByOrder = async () => {
  const orderNo = form.value.orderNo?.trim()
  form.value.guestRoomId = null
  availableRooms.value = []

  if (!orderNo) {
    return
  }

  roomsLoading.value = true
  try {
    const orderRes = await request.get('/admin/orders', {
      params: { orderNo, pageNum: 1, pageSize: 10 }
    })
    const order = (orderRes.data?.list || []).find(item => item.orderNo === orderNo)
    if (!order) {
      ElMessage.warning('未找到该订单')
      return
    }
    const statusCode = order.status?.code ?? order.status
    if (![1, 2].includes(statusCode)) {
      ElMessage.warning('该订单状态不允许办理入住')
      return
    }

    const res = await request.get('/admin/guest-rooms/available', {
      params: {
        hotelId: order.hotelId,
        roomTypeId: order.roomTypeId
      }
    })
    availableRooms.value = res.data || []
    if (!availableRooms.value.length) {
      ElMessage.warning('该订单房型暂无空闲客房')
    }
  } catch (err) {
    console.error('获取空闲客房失败:', err)
  } finally {
    roomsLoading.value = false
  }
}

const openCheckinDialog = () => {
  form.value = { orderNo: '', idCard: '', guestRoomId: null }
  availableRooms.value = []
  dialogVisible.value = true
}

const submitCheckin = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await request.post('/admin/checkins', form.value)
        ElMessage.success('办理入住成功')
        dialogVisible.value = false
        fetchData()
      } catch (err) {
        console.error('办理入住失败:', err)
      } finally {
        saving.value = false
      }
    }
  })
}

const handleCheckout = (row) => {
  ElMessageBox.confirm(`确认对订单 ${row.orderNo} 对应的客房进行退房操作？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.post(`/admin/checkins/${row.id}/checkout`)
        ElMessage.success('退房成功')
        fetchData()
      } catch (err) {
        console.error('退房失败:', err)
      }
    })
    .catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header .page-title { margin-bottom: 0; }
</style>
