<template>
  <div class="order-list-page">
    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
      <el-button type="primary" @click="handleExport" :loading="exporting">
        <el-icon><Download /></el-icon>导出订单
      </el-button>
    </div>

    <!-- 搜索筛选区 -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="输入订单号" clearable />
        </el-form-item>
        <el-form-item label="入住人">
          <el-input v-model="searchForm.guestName" placeholder="姓名或手机号" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="待支付" value="pending_pay" />
            <el-option label="待确认" value="pending_confirm" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已入住" value="checked_in" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
            <el-option label="退款审核" value="refunding" />
            <el-option label="已退款" value="refunded" />
            <el-option label="退款拒绝" value="refund_rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 订单表格区 -->
    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column label="酒店/房型" min-width="200">
          <template #default="scope">
            <div class="hotel-name">{{ scope.row.hotelName }}</div>
            <div class="room-type">{{ scope.row.roomType }} ({{ scope.row.roomCount }}间)</div>
          </template>
        </el-table-column>
        <el-table-column label="入离日期" width="180">
          <template #default="scope">
            <div class="date-text">入：{{ scope.row.checkInDate }}</div>
            <div class="date-text">离：{{ scope.row.checkOutDate }}</div>
          </template>
        </el-table-column>
        <el-table-column label="入住人" width="120">
          <template #default="scope">
            <div>{{ scope.row.guestName }}</div>
            <div style="font-size: 12px; color: var(--color-text-secondary)">{{ scope.row.phone }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额(元)" width="100">
          <template #default="scope">
            <span class="amount-text">￥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="light" round size="small">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleDetail(scope.row)">详情</el-button>
            <el-button 
              v-if="scope.row.status === 'pending_confirm'" 
              link type="success" size="small"
              @click="handleConfirm(scope.row)"
            >确认订单</el-button>
            <el-button 
              v-if="['pending_pay', 'pending_confirm', 'confirmed'].includes(scope.row.status)" 
              link type="danger" size="small"
              @click="handleCancel(scope.row)"
            >取消</el-button>
            <el-button 
              v-if="scope.row.status === 'refunding'" 
              link type="danger" size="small"
              @click="openAuditDialog(scope.row)"
            >审核退款</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </div>

    <!-- 退款审核弹窗 -->
    <el-dialog title="退款审核" v-model="auditDialogVisible" width="400px">
      <el-form label-width="80px">
        <el-form-item label="订单号">{{ currentAuditOrder?.orderNo }}</el-form-item>
        <el-form-item label="退款金额">￥{{ currentAuditOrder?.amount }}</el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.approve">
            <el-radio :label="true">同意退款</el-radio>
            <el-radio :label="false">拒绝退款</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注" v-if="!auditForm.approve">
          <el-input type="textarea" v-model="auditForm.reason" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit" :loading="auditing">确定</el-button>
      </template>
    </el-dialog>
    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" v-model="detailVisible" width="600px">
      <el-descriptions v-if="currentOrder" :column="2" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status?.code || currentOrder.status)" effect="light">
            {{ currentOrder.status?.description || getStatusLabel(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="酒店名称" :span="2">{{ currentOrder.hotelName }}</el-descriptions-item>
        <el-descriptions-item label="房型名称" :span="2">{{ currentOrder.roomTypeName || currentOrder.roomType }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ currentOrder.checkinDate }}</el-descriptions-item>
        <el-descriptions-item label="离店日期">{{ currentOrder.checkoutDate }}</el-descriptions-item>
        <el-descriptions-item label="间数">{{ currentOrder.roomCount }}</el-descriptions-item>
        <el-descriptions-item label="晚数">{{ currentOrder.nights }}</el-descriptions-item>
        <el-descriptions-item label="入住人">{{ currentOrder.guestName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.guestPhone || currentOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="支付金额" :span="2">
          <span class="amount-text">￥{{ currentOrder.payAmount || currentOrder.amount }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Download } from '@element-plus/icons-vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = ref({
  orderNo: '',
  guestName: '',
  status: '',
  dateRange: []
})

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const exporting = ref(false)

const viewStatusToApi = {
  pending_pay: 0,
  pending_confirm: 1,
  confirmed: 2,
  cancelled: 3,
  checked_in: 4,
  completed: 5,
  refunding: 6,
  refunded: 7,
  refund_rejected: 8
}

const apiStatusToView = Object.fromEntries(
  Object.entries(viewStatusToApi).map(([key, value]) => [value, key])
)

const buildQueryParams = () => {
  const [startDate, endDate] = searchForm.value.dateRange || []
  return {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    orderNo: searchForm.value.orderNo || undefined,
    guestName: searchForm.value.guestName || undefined,
    status: searchForm.value.status ? viewStatusToApi[searchForm.value.status] : undefined,
    startDate: startDate || undefined,
    endDate: endDate || undefined
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/orders', {
      params: buildQueryParams()
    })
    if (res.data) {
      tableData.value = (res.data.list || []).map(normalizeOrder)
      total.value = res.data.total || 0
    }
  } catch (err) {
    console.error('获取订单列表失败', err)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})

const statusMap = {
  'pending_pay': { label: '待支付', type: 'warning' },
  'pending_confirm': { label: '待确认', type: 'warning' },
  'confirmed': { label: '已确认', type: 'success' },
  'checked_in': { label: '已入住', type: 'success' },
  'completed': { label: '已完成', type: 'info' },
  'cancelled': { label: '已取消', type: 'danger' },
  'refunding': { label: '退款审核', type: 'danger' },
  'refunded': { label: '已退款', type: 'info' },
  'refund_rejected': { label: '退款拒绝', type: 'warning' }
}

const getStatusLabel = (status) => statusMap[status]?.label || '未知状态'
const getStatusType = (status) => statusMap[status]?.type || 'info'

const normalizeOrder = (item) => {
  const statusCode = item.status?.code ?? item.status
  return {
    ...item,
    roomType: item.roomTypeName,
    amount: item.payAmount,
    phone: item.guestPhone,
    checkInDate: item.checkinDate,
    checkOutDate: item.checkoutDate,
    statusCode,
    status: apiStatusToView[statusCode] || item.status
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const resetSearch = () => {
  searchForm.value = { orderNo: '', guestName: '', status: '', dateRange: [] }
  handleSearch()
}

const handleExport = async () => {
  exporting.value = true
  try {
    const res = await request.get('/admin/orders/export', {
      params: buildQueryParams(),
      responseType: 'blob'
    })
    const blob = new Blob([res.data], { type: 'text/csv;charset=utf-8' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `orders-${new Date().toISOString().slice(0, 10)}.csv`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (err) {
    console.error('导出订单失败', err)
  } finally {
    exporting.value = false
  }
}

const handleConfirm = (row) => {
  ElMessageBox.confirm(`确认订单 ${row.orderNo} 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await request.post(`/admin/orders/${row.id}/confirm`)
      ElMessage.success('订单已确认')
      fetchData()
    })
    .catch(() => {})
}

const handleCancel = (row) => {
  ElMessageBox.confirm(`确认取消订单 ${row.orderNo} 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await request.post(`/admin/orders/${row.id}/cancel`, {
        reason: '管理员取消订单'
      })
      ElMessage.success('订单已取消')
      fetchData()
    })
    .catch(() => {})
}

const detailVisible = ref(false)
const currentOrder = ref(null)

const handleDetail = async (row) => {
  try {
    const res = await request.get(`/admin/orders/${row.id}`)
    if (res.data) {
      currentOrder.value = normalizeOrder(res.data)
      detailVisible.value = true
    }
  } catch (err) {
    ElMessage.error('获取订单详情失败')
    currentOrder.value = { ...row }
    detailVisible.value = true
  }
}

const auditDialogVisible = ref(false)
const auditing = ref(false)
const currentAuditOrder = ref(null)
const auditForm = ref({ approve: true, reason: '' })

const openAuditDialog = (row) => {
  currentAuditOrder.value = row
  auditForm.value = { approve: true, reason: '' }
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  if (!auditForm.value.approve && !auditForm.value.reason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  auditing.value = true
  try {
    const refundId = currentAuditOrder.value?.refundId
    if (!refundId) {
      ElMessage.error('该订单缺少退款记录ID，请刷新后重试')
      return
    }
    await request.post(`/admin/orders/refunds/${refundId}/audit`, {
      approve: auditForm.value.approve,
      reason: auditForm.value.reason
    })
    ElMessage.success('审核处理成功')
    auditDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('退款审核失败:', error)
  } finally {
    auditing.value = false
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header .page-title {
  margin-bottom: 0;
}

.hotel-name {
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}
.room-type {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.date-text {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.amount-text {
  font-weight: 600;
  color: var(--color-warning);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
