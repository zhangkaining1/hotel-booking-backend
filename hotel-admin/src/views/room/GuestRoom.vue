<template>
  <div class="guest-room-page">
    <div class="page-header">
      <h2 class="page-title">房号管理</h2>
      <el-button type="primary" class="btn-add" @click="openAddDialog">
        <el-icon class="el-icon--left"><Plus /></el-icon>
        新增房间
      </el-button>
    </div>

    <div class="table-card">
      <el-table 
        :data="tableData" 
        style="width: 100%" 
        v-loading="loading"
        :row-class-name="tableRowClassName"
        class="custom-table"
      >
        <el-table-column prop="roomNo" label="房间号" width="120">
          <template #default="scope">
            <span class="room-no">{{ scope.row.roomNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="roomType" label="所属房型" width="200">
          <template #default="scope">
            <div class="room-type-cell">
              <el-icon><HomeFilled /></el-icon>
              <span>{{ scope.row.roomType }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="floor" label="楼层" width="120">
          <template #default="scope">
            <el-tag effect="plain" type="info" class="floor-tag">{{ scope.row.floor || '-' }}F</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="150">
          <template #default="scope">
            <span :class="['status-badge', getStatusClass(scope.row.status)]">
              {{ scope.row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button class="action-btn edit-btn" link type="primary" @click="openEditDialog(scope.row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button class="action-btn delete-btn" link type="danger" @click="handleDelete(scope.row)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchData"
          @size-change="fetchData"
          class="custom-pagination"
        />
      </div>
    </div>

    <!-- 房间编辑/新增弹窗 -->
    <el-dialog 
      :title="form.id ? '编辑房间' : '新增房间'" 
      v-model="dialogVisible" 
      width="500px"
      class="custom-dialog"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="custom-form">
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="form.roomNumber" placeholder="请输入房间号，如801" />
        </el-form-item>
        <el-form-item label="所属房型" prop="roomTypeId">
          <el-select v-model="form.roomTypeId" placeholder="请选择房型" class="w-full">
            <el-option
              v-for="item in roomTypes"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层" prop="floor">
              <el-input-number v-model="form.floor" :min="1" :max="100" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面积(㎡)" prop="area">
              <el-input-number v-model="form.area" :min="0" :precision="2" :step="1" class="w-full" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status" class="status-radio-group">
            <el-radio-button :label="0">空闲</el-radio-button>
            <el-radio-button :label="1">预订</el-radio-button>
            <el-radio-button :label="2">入住</el-radio-button>
            <el-radio-button :label="3">维修</el-radio-button>
            <el-radio-button :label="4">停用</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="btn-cancel">取消</el-button>
          <el-button type="primary" :loading="saving" @click="submitSave" class="btn-confirm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, HomeFilled } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const roomTypes = ref([])

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref(null)
const form = ref({
  id: null,
  roomNumber: '',
  roomTypeId: null,
  floor: null,
  area: null,
  status: 0,
  remark: ''
})

const rules = {
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  roomTypeId: [{ required: true, message: '请选择所属房型', trigger: 'change' }]
}

const getStatusClass = (status) => {
  switch (status) {
    case '空闲': return 'status-free'
    case '已预订': return 'status-booked'
    case '已入住': return 'status-occupied'
    case '维修': return 'status-maintenance'
    case '停用': return 'status-disabled'
    default: return 'status-default'
  }
}

const tableRowClassName = ({ rowIndex }) => {
  return 'custom-row'
}

const loadRoomTypes = async () => {
  try {
    const res = await request.get('/admin/room/types', { params: { pageSize: 100 } })
    roomTypes.value = res.data?.list || []
  } catch (error) {
    console.error('获取房型失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/guest-rooms', {
      params: { pageNum: currentPage.value, pageSize: pageSize.value }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取房间列表失败:', error)
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  form.value = { id: null, roomNumber: '', roomTypeId: null, floor: null, area: null, status: 0, remark: '' }
  dialogVisible.value = true
  // Reset validation state
  if (formRef.value) formRef.value.clearValidate()
}

const openEditDialog = (row) => {
  form.value = {
    id: row.id,
    roomNumber: row.roomNo,
    roomTypeId: row.roomTypeId,
    floor: row.floor,
    area: row.area,
    status: parseStatus(row.status),
    remark: row.remark
  }
  dialogVisible.value = true
  // Reset validation state
  if (formRef.value) formRef.value.clearValidate()
}

const parseStatus = (statusStr) => {
  const map = { '空闲': 0, '已预订': 1, '已入住': 2, '维修': 3, '停用': 4 }
  return map[statusStr] ?? 0
}

const submitSave = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await request.post('/admin/guest-rooms', form.value)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error(error.message || '保存失败')
      } finally {
        saving.value = false
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除房间 <span style="color: #ef4444; font-weight: bold;">${row.roomNo}</span> 吗？此操作不可逆。`, '警告', { 
    type: 'warning',
    dangerouslyUseHTMLString: true,
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    confirmButtonClass: 'btn-danger-confirm'
  })
    .then(async () => {
      try {
        await request.delete(`/admin/guest-rooms/${row.id}`)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error(error.message || '删除失败')
      }
    })
    .catch(() => {})
}

onMounted(() => {
  loadRoomTypes()
  fetchData()
})
</script>

<style scoped>
/* 全局页面背景 */
.guest-room-page {
  padding: 24px;
  min-height: calc(100vh - 84px);
  background: #f0f2f5;
  background-image: radial-gradient(circle at 100% 0%, #e0e8f5 0%, transparent 25%),
                    radial-gradient(circle at 0% 100%, #e0e8f5 0%, transparent 25%);
}

/* 顶部栏样式 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 0.5px;
}

/* 添加按钮样式 */
.btn-add {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  border-radius: 8px;
  padding: 10px 24px;
  height: 40px;
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.25);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.4);
}

/* 卡片容器 */
.table-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

/* 表格全局样式 */
:deep(.custom-table) {
  --el-table-border-color: #f3f4f6;
  --el-table-header-bg-color: #f8fafc;
  --el-table-header-text-color: #475569;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.custom-table th.el-table__cell) {
  font-weight: 600;
  padding: 16px 0;
}

:deep(.custom-table td.el-table__cell) {
  padding: 16px 0;
  transition: background-color 0.3s;
}

:deep(.custom-table .custom-row:hover > td.el-table__cell) {
  background-color: #f8fafc !important;
}

/* 房间号样式 */
.room-no {
  font-weight: 700;
  color: #1e293b;
  font-size: 15px;
}

/* 房型单元格样式 */
.room-type-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #334155;
  font-weight: 500;
}
.room-type-cell .el-icon {
  color: #64748b;
  font-size: 16px;
}

/* 楼层标签 */
.floor-tag {
  border-radius: 6px;
  font-weight: 600;
}

/* 状态徽章 */
.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.status-free { background: #dcfce7; color: #166534; }
.status-booked { background: #fef9c3; color: #854d0e; }
.status-occupied { background: #fee2e2; color: #991b1b; }
.status-maintenance { background: #f1f5f9; color: #475569; }
.status-disabled { background: #f3f4f6; color: #9ca3af; text-decoration: line-through; }

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
}
.action-btn {
  font-weight: 600;
  transition: all 0.2s;
  padding: 4px 8px;
  height: auto;
}
.action-btn:hover {
  transform: scale(1.05);
}
.edit-btn { color: #3b82f6; }
.delete-btn { color: #ef4444; }

/* 分页组件 */
.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
:deep(.custom-pagination) {
  --el-pagination-hover-color: #3b82f6;
  --el-pagination-button-bg-color: #f8fafc;
}

/* 弹窗样式 */
:deep(.custom-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}
:deep(.custom-dialog .el-dialog__header) {
  background: #f8fafc;
  margin-right: 0;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}
:deep(.custom-dialog .el-dialog__title) {
  font-weight: 700;
  color: #1e293b;
}
:deep(.custom-dialog .el-dialog__body) {
  padding: 30px 24px;
}
:deep(.custom-dialog .el-dialog__footer) {
  background: #f8fafc;
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  margin-top: 0;
}

/* 表单组件美化 */
.w-full {
  width: 100%;
}
.status-radio-group {
  display: flex;
  width: 100%;
}
:deep(.status-radio-group .el-radio-button) {
  flex: 1;
}
:deep(.status-radio-group .el-radio-button__inner) {
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
.btn-cancel {
  border-radius: 8px;
  font-weight: 600;
}
.btn-confirm {
  border-radius: 8px;
  font-weight: 600;
  background: #3b82f6;
  border: none;
}
.btn-confirm:hover {
  background: #2563eb;
}
</style>
