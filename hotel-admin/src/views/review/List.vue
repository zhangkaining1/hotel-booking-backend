<template>
  <div class="review-list-page">
    <div class="page-header">
      <h2 class="page-title">评价管理</h2>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="hotelId" label="酒店ID" width="100" />
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column label="评分" width="150">
          <template #default="scope">
            <el-rate v-model="scope.row.score" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reply" label="商家回复" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.reply" style="color: var(--color-text-secondary)">
              {{ scope.row.reply }}
            </span>
            <span v-else style="color: var(--color-danger); font-size: 12px">未回复</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleReply(scope.row)">回复</el-button>
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

    <el-dialog v-model="dialogVisible" title="回复评价" width="500px">
      <el-form>
        <el-form-item label="用户评价">
          <div>{{ currentReview?.content }}</div>
        </el-form-item>
        <el-form-item label="您的回复">
          <el-input type="textarea" :rows="3" v-model="replyText" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

const dialogVisible = ref(false)
const submitLoading = ref(false)
const currentReview = ref(null)
const replyText = ref('')

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/reviews', {
      params: { pageNum: currentPage.value, pageSize: pageSize.value }
    })
    if (res.data) {
      tableData.value = (res.data.list || []).map(item => ({
        ...item,
        score: item.score ?? Number(item.overallScore || 0),
        userName: item.userName || `用户${item.userId}`
      }))
      total.value = res.data.total || 0
    }
  } catch (err) {
    console.error('获取评价失败', err)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleReply = (row) => {
  currentReview.value = row
  replyText.value = row.reply || ''
  dialogVisible.value = true
}

const submitReply = async () => {
  if (!replyText.value.trim()) {
    return ElMessage.warning('回复内容不能为空')
  }
  submitLoading.value = true
  try {
    await request.post(`/admin/reviews/${currentReview.value.id}/reply`, null, {
      params: { reply: replyText.value }
    })
    ElMessage.success('回复成功')
    dialogVisible.value = false
    fetchData()
  } catch (err) {
    console.error('回复评价失败', err)
  } finally {
    submitLoading.value = false
  }
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
