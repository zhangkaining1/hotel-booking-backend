<template>
  <div class="role-manage-page">
    <div class="page-header">
      <h2 class="page-title">角色管理</h2>
      <el-button type="primary" @click="openAddDialog">新增角色</el-button>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="roleCode" label="角色编码" width="180" />
        <el-table-column prop="roleName" label="角色名称" width="180" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button link type="success" size="small" @click="openAssignDialog(scope.row)">分配权限</el-button>
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

    <!-- 角色编辑/新增弹窗 -->
    <el-dialog :title="form.id ? '编辑角色' : '新增角色'" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="如 ADMIN" />
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

    <!-- 分配权限弹窗 -->
    <el-dialog title="分配权限" v-model="assignDialogVisible" width="500px">
      <el-tree
        ref="treeRef"
        :data="permissionTree"
        show-checkbox
        node-key="id"
        :props="{ label: 'name', children: 'children' }"
        default-expand-all
      />
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitAssign">保存</el-button>
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

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref(null)
const form = ref({
  id: null,
  roleName: '',
  roleCode: '',
  description: ''
})

const rules = {
  roleName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入编码', trigger: 'blur' }]
}

// 分配权限相关
const assignDialogVisible = ref(false)
const treeRef = ref(null)
const permissionTree = ref([])
const currentRoleId = ref(null)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/system/roles', {
      params: { pageNum: currentPage.value, pageSize: pageSize.value }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (err) {
    console.error('获取角色失败:', err)
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  form.value = { id: null, roleName: '', roleCode: '', description: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  form.value = { id: row.id, roleName: row.roleName, roleCode: row.roleCode, description: row.description }
  dialogVisible.value = true
}

const submitSave = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await request.post('/admin/system/roles', form.value)
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
  ElMessageBox.confirm(`确认删除角色 [${row.roleName}] 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete(`/admin/system/roles/${row.id}`)
        ElMessage.success('删除成功')
        fetchData()
      } catch (err) {
        console.error('删除失败:', err)
      }
    })
    .catch(() => {})
}

// 获取完整的权限树
const loadPermissionTree = async () => {
  try {
    const res = await request.get('/admin/system/permissions/tree')
    permissionTree.value = res.data || []
  } catch (err) {
    console.error(err)
  }
}

const openAssignDialog = async (row) => {
  currentRoleId.value = row.id
  await loadPermissionTree()
  assignDialogVisible.value = true
  
  // 渲染完毕后设置勾选
  setTimeout(async () => {
    try {
      const res = await request.get(`/admin/system/roles/${row.id}/permissions`)
      const checkedIds = res.data || []
      // ElementPlus tree: if you check a parent, it checks all children. 
      // Safe approach: only set checked keys, then the tree resolves half-checked states.
      treeRef.value.setCheckedKeys(checkedIds)
    } catch (err) {
      console.error('加载角色已有权限失败', err)
    }
  }, 100)
}

const submitAssign = async () => {
  saving.value = true
  try {
    const checkedKeys = treeRef.value.getCheckedKeys()
    const halfCheckedKeys = treeRef.value.getHalfCheckedKeys()
    const allKeys = [...checkedKeys, ...halfCheckedKeys] // 后端通常需要保存完整的链路ID
    
    await request.post(`/admin/system/roles/${currentRoleId.value}/permissions`, {
      permissionIds: allKeys
    })
    ElMessage.success('权限分配成功')
    assignDialogVisible.value = false
  } catch (err) {
    console.error('权限分配失败:', err)
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header .page-title { margin-bottom: 0; }
</style>
