<template>
  <div class="user-list-page">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-button type="primary" @click="handleAdd">新增用户</el-button>
    </div>

    <div class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="昵称">
          <el-input v-model="searchForm.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全选" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column label="头像" width="70">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.avatar">
              {{ scope.row.nickname ? scope.row.nickname.charAt(0) : 'U' }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ scope.row.gender === 1 ? '男' : (scope.row.gender === 2 ? '女' : '未知') }}
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80">
          <template #default="scope">
            <span style="color: var(--color-primary); font-weight: bold;">
              {{ scope.row.points }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="账号状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="正常"
              inactive-text="禁用"
              inline-prompt
              @change="(val) => handleStatusChange(scope.row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleDetail(scope.row)">详情</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="success" size="small" @click="handleRecharge(scope.row)">充值积分</el-button>
            <el-popconfirm title="确定重置该用户的密码为 123456 吗？" @confirm="handleResetPassword(scope.row)">
              <template #reference>
                <el-button link type="warning" size="small">重置密码</el-button>
              </template>
            </el-popconfirm>
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

    <!-- 详情弹窗 -->
    <el-dialog title="用户详情" v-model="detailVisible" width="500px">
      <el-descriptions v-if="currentUser" :column="1" border>
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="头像">
          <el-avatar :size="60" :src="currentUser.avatar">
            {{ currentUser.nickname ? currentUser.nickname.charAt(0) : 'U' }}
          </el-avatar>
        </el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="积分">
          <span style="color: var(--color-primary); font-weight: bold;">{{ currentUser.points }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogType === 'add' ? '新增用户' : '编辑用户'" v-model="formVisible" width="500px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            action="/api/admin/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="uploadHeaders"
          >
            <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="formData.password" placeholder="为空则默认为 123456" show-password />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
            <el-radio :value="0">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 积分充值弹窗 -->
    <el-dialog title="充值积分" v-model="rechargeVisible" width="400px">
      <el-form ref="rechargeRef" :model="rechargeData" :rules="rechargeRules" label-width="80px">
        <el-form-item label="变动数值" prop="points">
          <el-input-number v-model="rechargeData.points" :step="100" placeholder="正数增加，负数扣除" style="width: 100%" />
          <div style="font-size: 12px; color: #999; margin-top: 5px;">输入正数表示增加积分，负数表示扣减积分</div>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="rechargeData.remark" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecharge" :loading="rechargeLoading">确定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const uploadHeaders = ref({
  Authorization: localStorage.getItem('admin_token') || ''
})

const handleAvatarSuccess = (response) => {
  if (response.code === 200 || response.data) {
    formData.value.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('上传头像图片只能是图片格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 5MB!')
  }
  return isImage && isLt2M
}

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

const searchForm = ref({
  nickname: '',
  phone: '',
  email: '',
  status: null
})

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const resetSearch = () => {
  searchForm.value = { nickname: '', phone: '', email: '', status: null }
  handleSearch()
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    }
    const res = await request.get('/admin/users', { params })
    if (res.data) {
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (err) {
    console.error('获取用户列表失败', err)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row, val) => {
  try {
    await request.post(`/admin/users/${row.id}/status`, null, {
      params: { status: val }
    })
    ElMessage.success('状态更新成功')
  } catch (err) {
    ElMessage.error('状态更新失败')
    row.status = val === 1 ? 0 : 1
  }
}

const handleResetPassword = async (row) => {
  try {
    await request.post(`/admin/users/${row.id}/password/reset`)
    ElMessage.success('密码已重置为 123456')
  } catch (err) {
    ElMessage.error('重置密码失败')
  }
}

// 详情
const detailVisible = ref(false)
const currentUser = ref(null)

const handleDetail = async (row) => {
  try {
    const res = await request.get(`/admin/users/${row.id}`)
    if (res.data) {
      currentUser.value = res.data
      detailVisible.value = true
    }
  } catch (err) {
    ElMessage.error('获取用户详情失败')
  }
}

// 新增/编辑
const formVisible = ref(false)
const dialogType = ref('add')
const submitLoading = ref(false)
const formRef = ref(null)
const formData = ref({
  id: null,
  nickname: '',
  avatar: '',
  phone: '',
  email: '',
  password: '',
  gender: 0,
  status: 1
})
const formRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const handleAdd = () => {
  dialogType.value = 'add'
  formData.value = { id: null, nickname: '', avatar: '', phone: '', email: '', password: '', gender: 0, status: 1 }
  formVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  formData.value = { ...row }
  formVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (dialogType.value === 'add') {
          await request.post('/admin/users', formData.value)
          ElMessage.success('新增成功')
        } else {
          await request.put(`/admin/users/${formData.value.id}`, formData.value)
          ElMessage.success('更新成功')
        }
        formVisible.value = false
        fetchData()
      } catch (err) {
        ElMessage.error(dialogType.value === 'add' ? '新增失败' : '更新失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 积分充值
const rechargeVisible = ref(false)
const rechargeLoading = ref(false)
const rechargeRef = ref(null)
const rechargeData = ref({
  id: null,
  points: 100,
  remark: ''
})
const rechargeRules = {
  points: [{ required: true, message: '请输入数值', trigger: 'blur' }]
}

const handleRecharge = (row) => {
  rechargeData.value = { id: row.id, points: 100, remark: '' }
  rechargeVisible.value = true
}

const submitRecharge = async () => {
  if (!rechargeRef.value) return
  await rechargeRef.value.validate(async (valid) => {
    if (valid) {
      rechargeLoading.value = true
      try {
        await request.post(`/admin/users/${rechargeData.value.id}/points`, {
          points: rechargeData.value.points,
          remark: rechargeData.value.remark
        })
        ElMessage.success('充值成功')
        rechargeVisible.value = false
        fetchData()
      } catch (err) {
        ElMessage.error('充值失败')
      } finally {
        rechargeLoading.value = false
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
.search-card {
  background: #fff;
  padding: 18px 18px 0 18px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.table-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
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
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
}
.avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 6px;
  object-fit: cover;
}
</style>
