<template>
  <div class="config-page">
    <div class="page-header">
      <h2 class="page-title">系统配置</h2>
      <el-button type="primary" @click="openAddDialog">新增配置</el-button>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="configKey" label="配置键 (Key)" width="200" />
        <el-table-column prop="configValue" label="配置值 (Value)" width="250">
          <template #default="scope">
            <el-input v-model="scope.row.configValue" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" min-width="200" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
              link
              type="primary"
              size="small"
              :loading="savingId === scope.row.id"
              @click="saveConfig(scope.row)"
            >保存</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog title="新增配置" v-model="dialogVisible" width="480px">
      <el-form label-width="90px">
        <el-form-item label="配置键" required>
          <el-input v-model="form.configKey" placeholder="如 system_name" />
        </el-form-item>
        <el-form-item label="配置值">
          <el-input v-model="form.configValue" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" placeholder="请输入说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="createConfig">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const savingId = ref(null)
const dialogVisible = ref(false)
const creating = ref(false)
const form = ref({
  configKey: '',
  configValue: '',
  description: ''
})

const normalizeConfig = (item) => ({
  ...item,
  configKey: item.configKey || item.key || item.name || '',
  configValue: item.configValue ?? item.value ?? '',
  description: item.description || item.remark || ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/system_configs')
    tableData.value = (res.data?.list || res.data || []).map(normalizeConfig)
  } catch (err) {
    console.error('获取配置失败', err)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const saveConfig = async (row) => {
  if (!row.configKey) {
    return ElMessage.warning('配置键不能为空')
  }
  savingId.value = row.id
  try {
    await request.put('/admin/system_configs', {
      id: row.id,
      configKey: row.configKey,
      configValue: row.configValue,
      description: row.description
    })
    ElMessage.success('保存成功')
    fetchData()
  } catch (err) {
    console.error('保存配置失败', err)
  } finally {
    savingId.value = null
  }
}

const openAddDialog = () => {
  form.value = { configKey: '', configValue: '', description: '' }
  dialogVisible.value = true
}

const createConfig = async () => {
  if (!form.value.configKey) {
    return ElMessage.warning('配置键不能为空')
  }
  creating.value = true
  try {
    await request.post('/admin/system_configs', form.value)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (err) {
    console.error('新增配置失败', err)
  } finally {
    creating.value = false
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
