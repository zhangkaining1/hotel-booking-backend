<template>
  <div class="dict-manage-page">
    <div class="page-header">
      <h2 class="page-title">基础字典管理</h2>
    </div>

    <el-tabs v-model="activeName">
      <el-tab-pane label="系统配置" name="system_configs" />
      <el-tab-pane label="国家字典" name="countrys" />
      <el-tab-pane label="城市字典" name="citys" />
      <el-tab-pane label="设施字典" name="facilitys" />
    </el-tabs>

    <div class="table-card" style="margin-top: 15px;">
      <div style="margin-bottom: 15px;">
        <el-button type="primary" @click="openDialog()">新增数据</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="name" label="名称/Key" min-width="150" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openDialog(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="deleteData(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="form.id ? '编辑' : '新增'" v-model="dialogVisible" width="400px">
      <el-form label-width="80px">
        <el-form-item label="名称/Key" required>
          <el-input v-model="form.name" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitData" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeName = ref('system_configs')
const loading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const saving = ref(false)
const form = ref({ id: null, name: '' })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get(`/admin/${activeName.value}`)
    tableData.value = res.data || []
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

watch(activeName, () => {
  fetchData()
})

const openDialog = (row) => {
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { id: null, name: '' }
  }
  dialogVisible.value = true
}

const submitData = async () => {
  if (!form.value.name) return ElMessage.warning('请输入名称')
  saving.value = true
  try {
    if (form.value.id) {
      await request.put(`/admin/${activeName.value}`, form.value)
    } else {
      await request.post(`/admin/${activeName.value}`, form.value)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch (err) {
    console.error(err)
  } finally {
    saving.value = false
  }
}

const deleteData = (id) => {
  ElMessageBox.confirm('确认删除吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await request.delete(`/admin/${activeName.value}/${id}`)
      ElMessage.success('删除成功')
      fetchData()
    } catch (err) {
      console.error(err)
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.page-title { margin-bottom: 0; }
.table-card { background: var(--color-bg-card); padding: 20px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); }
</style>
