<template>
  <div class="room-type-page">
    <div class="page-header">
      <h2 class="page-title">房型管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增房型
      </el-button>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="房型名称" min-width="150" />
        <el-table-column prop="bedType" label="床型" width="120" />
        <el-table-column prop="breakfast" label="早餐" width="120" />
        <el-table-column label="入住人数" width="100">
          <template #default="scope">
            {{ scope.row.maxAdults }}大{{ scope.row.maxChildren }}小
          </template>
        </el-table-column>
        <el-table-column prop="basePrice" label="基础价格" width="120">
          <template #default="scope">
            <span style="color: var(--color-warning); font-weight: bold;">
              ￥{{ scope.row.basePrice }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="inventory" label="库存" width="80" />
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form ref="formRef" :model="typeForm" label-width="100px">
        <el-form-item label="所属酒店" prop="hotelId" :rules="[{ required: true, message: '请选择所属酒店', trigger: 'change' }]">
          <el-select v-model="typeForm.hotelId" placeholder="请选择酒店" style="width: 100%">
            <el-option
              v-for="hotel in hotelOptions"
              :key="hotel.id"
              :label="formatHotelLabel(hotel)"
              :value="hotel.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房型名称" prop="name" :rules="[{ required: true, message: '请输入房型名称', trigger: 'blur' }]">
          <el-input v-model="typeForm.name" placeholder="请输入房型名称" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="床型" prop="bedType">
              <el-input v-model="typeForm.bedType" placeholder="如: 大床/双床" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="早餐" prop="breakfast">
              <el-input v-model="typeForm.breakfast" placeholder="如: 无早/单早/双早" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最多成人" prop="maxAdults">
              <el-input-number v-model="typeForm.maxAdults" :min="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最多儿童" prop="maxChildren">
              <el-input-number v-model="typeForm.maxChildren" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="基础价格" prop="basePrice">
              <el-input-number v-model="typeForm.basePrice" :min="0" :precision="2" :step="10" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存数量" prop="inventory">
              <el-input-number v-model="typeForm.inventory" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="免费取消" prop="freeCancel">
          <el-switch v-model="typeForm.freeCancel" />
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
const hotelOptions = ref([])

const fetchHotels = async () => {
  try {
    const res = await request.get('/admin/hotels', {
      params: { pageNum: 1, pageSize: 100 }
    })
    hotelOptions.value = res.data?.list || []
  } catch (err) {
    console.error('获取酒店列表失败', err)
    hotelOptions.value = []
  }
}

const formatHotelLabel = (hotel) => {
  return hotel.city ? `${hotel.name}（${hotel.city}）` : hotel.name
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/room/types', {
      params: { pageNum: currentPage.value, pageSize: pageSize.value }
    })
    if (res.data) {
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (err) {
    console.error('获取房型失败', err)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增房型')
const submitting = ref(false)
const formRef = ref(null)
const typeForm = ref({
  id: null,
  hotelId: null,
  name: '',
  bedType: '',
  breakfast: '',
  maxAdults: 2,
  maxChildren: 0,
  freeCancel: true,
  inventory: 10,
  basePrice: 0
})

const handleAdd = () => {
  dialogTitle.value = '新增房型'
  typeForm.value = {
    id: null,
    hotelId: hotelOptions.value[0]?.id || null,
    name: '',
    bedType: '',
    breakfast: '',
    maxAdults: 2,
    maxChildren: 0,
    freeCancel: true,
    inventory: 10,
    basePrice: 0
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑房型'
  typeForm.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该房型吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/admin/room/types/${row.id}`)
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
        if (typeForm.value.id) {
          await request.put(`/admin/room/types/${typeForm.value.id}`, typeForm.value)
          ElMessage.success('更新成功')
        } else {
          await request.post('/admin/room/types', typeForm.value)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (err) {
        ElMessage.error(typeForm.value.id ? '更新失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(async () => {
  await fetchHotels()
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
