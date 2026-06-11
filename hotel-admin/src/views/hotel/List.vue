<template>
  <div class="hotel-list-page">
    <div class="page-header">
      <h2 class="page-title">酒店管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增酒店
      </el-button>
    </div>

    <!-- 搜索筛选区 -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="所在城市">
          <el-input v-model="searchForm.city" placeholder="输入城市名称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="酒店名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入酒店名称/品牌" clearable />
        </el-form-item>
        <el-form-item label="星级">
          <el-select v-model="searchForm.star" placeholder="选择星级" clearable style="width: 120px">
            <el-option label="五星级" :value="5" />
            <el-option label="四星级" :value="4" />
            <el-option label="三星级" :value="3" />
            <el-option label="二星及以下" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 酒店表格区 -->
    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column label="酒店图片" width="100">
          <template #default="scope">
            <el-image
              class="hotel-img"
              :src="scope.row.image"
              :preview-src-list="scope.row.image ? [scope.row.image] : []"
              fit="cover"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="酒店名称" min-width="180">
          <template #default="scope">
            <div class="hotel-name">{{ scope.row.name }}</div>
            <div class="hotel-en-name">{{ scope.row.enName }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column label="星级" width="140">
          <template #default="scope">
            <el-rate v-model="scope.row.star" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="80">
          <template #default="scope">
            <el-tag type="success" effect="dark" round size="small">{{ scope.row.score || '暂无' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              active-text="上架"
              inactive-text="下架"
              @change="(val) => handleStatusChange(scope.row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="primary" size="small">房型管理</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增酒店' : '编辑酒店'"
      width="60%"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="酒店名称" prop="nameCn">
              <el-input v-model="form.nameCn" placeholder="请输入中文名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="英文名称" prop="nameEn">
              <el-input v-model="form.nameEn" placeholder="请输入英文名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="星级" prop="starLevel">
              <el-select v-model="form.starLevel" placeholder="选择星级" style="width: 100%">
                <el-option label="五星级" :value="5" />
                <el-option label="四星级" :value="4" />
                <el-option label="三星级" :value="3" />
                <el-option label="二星及以下" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属城市ID" prop="cityId">
              <el-input-number v-model="form.cityId" :min="1" placeholder="城市ID" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="酒店地址" prop="address">
          <el-input v-model="form.address" placeholder="详细地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="酒店电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="form.brand" placeholder="所属品牌" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="酒店图片" prop="image">
          <el-upload
            class="avatar-uploader"
            action="/api/admin/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            :headers="uploadHeaders"
          >
            <img v-if="form.image" :src="form.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="酒店描述">
          <el-input type="textarea" :rows="3" v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="submitSave">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { Plus, Picture } from '@element-plus/icons-vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = ref({
  city: '',
  keyword: '',
  star: ''
})

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/hotels', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        city: searchForm.value.city,
        keyword: searchForm.value.keyword,
        star: searchForm.value.star
      }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (err) {
    console.error('获取酒店列表失败:', err)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const resetSearch = () => {
  searchForm.value = { city: '', keyword: '', star: '' }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}

const handleStatusChange = async (row, val) => {
  try {
    await request.put(`/admin/hotels/${row.id}/status`, null, {
      params: { status: val }
    })
    ElMessage.success('状态更新成功')
  } catch (err) {
    row.status = val === 1 ? 0 : 1
    console.error('更新状态失败:', err)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除酒店 [${row.name}] 吗？`, '警告', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete(`/admin/hotels/${row.id}`)
        ElMessage.success('删除成功')
        fetchData()
      } catch (err) {
        console.error('删除失败:', err)
      }
    })
    .catch(() => {})
}

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const saving = ref(false)
const form = ref({
  id: null,
  nameCn: '',
  nameEn: '',
  cityId: null,
  address: '',
  starLevel: null,
  brand: '',
  phone: '',
  description: '',
  status: 1,
  image: ''
})

const uploadHeaders = computed(() => ({
  Authorization: localStorage.getItem('admin_token') || ''
}))

const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    form.value.image = res.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传 JPG/PNG/WEBP 格式的图片!')
  }
  return isJpgOrPng
}

const rules = {
  nameCn: [{ required: true, message: '请输入中文名称', trigger: 'blur' }],
  cityId: [{ required: true, message: '请输入城市ID', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
}

const handleAdd = () => {
  dialogType.value = 'add'
  form.value = { id: null, nameCn: '', nameEn: '', cityId: null, address: '', starLevel: 5, brand: '', phone: '', description: '', status: 1, image: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  // row mapping to entity fields
  form.value = { 
    id: row.id, 
    nameCn: row.name, 
    nameEn: row.enName, 
    cityId: row.cityId,
    address: row.address, 
    starLevel: row.star, 
    brand: row.brand, 
    phone: row.phone, 
    description: row.description, 
    status: row.status,
    image: row.image || ''
  }
  dialogVisible.value = true
}

const submitSave = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        if (form.value.id) {
          await request.put('/admin/hotels', form.value)
        } else {
          await request.post('/admin/hotels', form.value)
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
.search-bar {
  margin-bottom: 20px;
}
.hotel-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
}
.hotel-name {
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}
.hotel-en-name {
  font-size: 12px;
  color: var(--color-text-secondary);
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}
</style>
