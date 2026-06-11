<template>
  <div class="resource-manage-page">
    <div class="page-header">
      <h2 class="page-title">菜单与权限管理</h2>
      <el-button type="primary" @click="openAddDialog(null)">新增顶级资源</el-button>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading" row-key="id" default-expand-all>
        <el-table-column prop="name" label="资源名称" min-width="180" />
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'primary' : 'success'">
              {{ scope.row.type === 1 ? '菜单' : '按钮/接口' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="scope">
            <el-icon v-if="scope.row.icon"><component :is="scope.row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button link type="success" size="small" @click="openAddDialog(scope.row)" v-if="scope.row.type === 1">添加子节点</el-button>
            <el-button link type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 资源编辑/新增弹窗 -->
    <el-dialog :title="form.id ? '编辑资源' : '新增资源'" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">菜单</el-radio>
            <el-radio :value="2">按钮/接口</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入资源名称" />
        </el-form-item>
        <el-form-item label="前端路由" prop="path" v-if="form.type === 1">
          <el-input v-model="form.path" placeholder="请输入前端路由路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permissionCode" v-if="form.type === 2">
          <el-input v-model="form.permissionCode" placeholder="请输入权限标识如 user:add" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="1" :max="999" />
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
  parentId: 0,
  name: '',
  permissionCode: '',
  type: 1,
  path: '',
  sort: 1
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/system/permissions/tree')
    tableData.value = res.data || []
  } catch (err) {
    console.error('获取权限树失败:', err)
  } finally {
    loading.value = false
  }
}

const openAddDialog = (parentRow) => {
  form.value = {
    id: null,
    parentId: parentRow ? parentRow.id : 0,
    name: '',
    permissionCode: '',
    type: 1,
    path: '',
    sort: 1
  }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  form.value = {
    id: row.id,
    parentId: row.parentId || 0,
    name: row.name,
    permissionCode: row.permissionCode,
    type: row.type,
    path: row.path,
    sort: row.sort
  }
  dialogVisible.value = true
}

const submitSave = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await request.post('/admin/system/permissions', form.value)
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
  ElMessageBox.confirm(`确认删除资源 [${row.name}] 吗？如果包含子节点将无法直接删除。`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete(`/admin/system/permissions/${row.id}`)
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
</style>
