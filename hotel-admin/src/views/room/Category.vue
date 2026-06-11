<template>
  <div class="category-page">
    <div class="page-header">
      <h2 class="page-title">房间分类管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增分类
      </el-button>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="sort" label="排序权重" width="120">
          <template #default="scope">
            <el-tag type="info" effect="plain">{{ scope.row.sort }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form ref="formRef" :model="categoryForm" label-width="100px">
        <el-form-item label="分类名称" prop="name" :rules="[{ required: true, message: '请输入分类名称', trigger: 'blur' }]">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序权重" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/room/categories', {
      params: { pageNum: currentPage.value, pageSize: pageSize.value }
    })
    if (res.data) {
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (err) {
    console.error('获取分类失败', err)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const submitting = ref(false)
const formRef = ref(null)
const categoryForm = ref({
  id: null,
  name: '',
  sort: 1
})

const handleAdd = () => {
  dialogTitle.value = '新增分类'
  categoryForm.value = { id: null, name: '', sort: 1 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  categoryForm.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/admin/room/categories/${row.id}`)
      ElMessage.success('删除成功')
      fetchData()
    } catch (err) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const submitForm = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (categoryForm.value.id) {
          await request.put(`/admin/room/categories/${categoryForm.value.id}`, categoryForm.value)
          ElMessage.success('更新成功')
        } else {
          await request.post('/admin/room/categories', categoryForm.value)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (err) {
        ElMessage.error(categoryForm.value.id ? '更新失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  fetchData()
})
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
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
