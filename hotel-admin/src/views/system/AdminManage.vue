<template>
  <div class="admin-manage-page">
    <div class="page-header">
      <h2 class="page-title">管理员管理</h2>
      <el-button type="primary" @click="openAddDialog">新增管理员</el-button>
    </div>

    <!-- 搜索筛选区 -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键字">
          <el-input v-model="searchForm.keyword" placeholder="请输入账号或姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="账号" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="150" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              active-text="正常"
              inactive-text="禁用"
              @change="(val) => handleStatusChange(scope.row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 管理员编辑/新增弹窗 -->
    <el-dialog :title="form.id ? '编辑管理员' : '新增管理员'" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="不填默认123456" show-password />
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
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  keyword: ''
})

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref(null)
const form = ref({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/system/admins', {
      params: { 
        pageNum: currentPage.value, 
        pageSize: pageSize.value,
        keyword: searchForm.value.keyword 
      }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (err) {
    console.error('获取管理员列表失败:', err)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const resetSearch = () => {
  searchForm.value.keyword = ''
  handleSearch()
}

const handleStatusChange = async (row, val) => {
  try {
    await request.put(`/admin/system/admins/${row.id}/status`, null, {
      params: { status: val }
    })
    ElMessage.success('状态更新成功')
  } catch (err) {
    row.status = val === 1 ? 0 : 1 // 回滚状态
    console.error('更新状态失败:', err)
  }
}

const openAddDialog = () => {
  form.value = { id: null, username: '', realName: '', phone: '', email: '', password: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  form.value = { id: row.id, username: row.username, realName: row.realName, phone: row.phone, email: row.email, password: '' }
  dialogVisible.value = true
}

const submitSave = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        if (form.value.id) {
          await request.put('/admin/system/admins', form.value)
        } else {
          await request.post('/admin/system/admins', form.value)
        }
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
  ElMessageBox.confirm(`确认删除管理员 [${row.username}] 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete(`/admin/system/admins/${row.id}`)
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
.page-header .page-title { margin-bottom: 0; }
.search-bar { margin-bottom: 20px; }
</style>
