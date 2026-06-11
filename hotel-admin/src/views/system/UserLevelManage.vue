<template>
  <div class="user-level-page">
    <div class="page-header">
      <h2 class="page-title">会员等级管理</h2>
      <el-button type="primary" @click="openAddDialog">新增等级</el-button>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="levelName" label="等级名称" width="150" />
        <el-table-column label="积分门槛" width="200">
          <template #default="scope">
            {{ scope.row.minPoints }} - {{ scope.row.maxPoints || '无上限' }}
          </template>
        </el-table-column>
        <el-table-column prop="discountRate" label="折扣率" width="120">
          <template #default="scope">
            <el-tag type="warning">{{ scope.row.discountRate }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 等级编辑/新增弹窗 -->
    <el-dialog :title="form.id ? '编辑会员等级' : '新增会员等级'" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="等级名称" prop="levelName">
          <el-input v-model="form.levelName" placeholder="如 金卡会员" />
        </el-form-item>
        <el-form-item label="最小积分" prop="minPoints">
          <el-input-number v-model="form.minPoints" :min="0" :step="100" />
        </el-form-item>
        <el-form-item label="最大积分" prop="maxPoints">
          <el-input-number v-model="form.maxPoints" :min="0" :step="100" placeholder="留空表示无上限" />
        </el-form-item>
        <el-form-item label="折扣率" prop="discountRate">
          <el-input-number v-model="form.discountRate" :min="0.1" :max="1.0" :step="0.05" placeholder="如 0.9 表示9折" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref(null)
const form = ref({
  id: null,
  levelName: '',
  minPoints: 0,
  maxPoints: undefined,
  discountRate: 1.0,
  description: ''
})

const rules = {
  levelName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  minPoints: [{ required: true, message: '请输入最小积分', trigger: 'blur' }],
  discountRate: [{ required: true, message: '请输入折扣率', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/user-levels')
    tableData.value = res.data || []
  } catch (err) {
    console.error('获取会员等级失败:', err)
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  form.value = { id: null, levelName: '', minPoints: 0, maxPoints: undefined, discountRate: 1.0, description: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const submitSave = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await request.post('/admin/user-levels', form.value)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        fetchData()
      } catch (err) {
        console.error('保存失败:', err)
      } finally {
        saving.value = false
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除等级 [${row.levelName}] 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete(`/admin/user-levels/${row.id}`)
        ElMessage.success('删除成功')
        fetchData()
      } catch (err) {
        console.error('删除失败:', err)
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
.page-title { margin-bottom: 0; }
.table-card { background: var(--color-bg-card); padding: 20px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); }
</style>
