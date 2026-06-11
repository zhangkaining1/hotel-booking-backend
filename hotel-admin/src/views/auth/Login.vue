<template>
  <div class="login-page">
    <section class="login-shell">
      <aside class="brand-panel">
        <img
          class="brand-image"
          src="https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=85"
          alt="酒店大堂"
        />
        <div class="brand-overlay">
          <div class="brand-top">
            <div class="brand-mark">HA</div>
            <div>
              <p class="eyebrow">Hotel Assist</p>
              <h1>酒店运营管理后台</h1>
            </div>
          </div>

          <div class="brand-copy">
            <p>统一管理酒店、房型、订单、入住和评价，让每日运营更清楚。</p>
          </div>

          <div class="metric-grid">
            <div class="metric-item">
              <strong>24h</strong>
              <span>订单响应</span>
            </div>
            <div class="metric-item">
              <strong>500+</strong>
              <span>酒店资源</span>
            </div>
            <div class="metric-item">
              <strong>4.8</strong>
              <span>平均评分</span>
            </div>
          </div>
        </div>
      </aside>

      <main class="form-panel">
        <div class="form-card">
          <div class="form-heading">
            <el-tag type="primary" effect="light" round>Admin Console</el-tag>
            <h2>欢迎回来</h2>
            <p>登录后继续处理今日酒店运营任务。</p>
          </div>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="rules"
            class="login-form"
            size="large"
            label-position="top"
          >
            <el-form-item label="管理员账号" prop="username">
              <el-input v-model="loginForm.username" placeholder="请输入管理员账号" clearable>
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="登录密码" prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入登录密码"
                show-password
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <div class="form-tools">
              <el-checkbox v-model="rememberAccount">记住账号</el-checkbox>
              <span>演示：admin / admin123</span>
            </div>

            <el-button
              type="primary"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              登录管理后台
            </el-button>
          </el-form>

          <div class="security-note">
            <el-icon><Lock /></el-icon>
            <span>仅限授权管理员访问，操作日志将被系统记录。</span>
          </div>
        </div>
      </main>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Lock, User } from '@element-plus/icons-vue'
import { useAdminStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()
const adminStore = useAdminStore()
const loginFormRef = ref(null)
const loading = ref(false)
const rememberAccount = ref(true)

const loginForm = ref({
  username: localStorage.getItem('admin_remembered_username') || 'admin',
  password: 'admin123',
})

const rules = {
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }],
}

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const res = await request.post('/admin/auth/login', {
        username: loginForm.value.username,
        password: loginForm.value.password,
      })

      if (rememberAccount.value) {
        localStorage.setItem('admin_remembered_username', loginForm.value.username)
      } else {
        localStorage.removeItem('admin_remembered_username')
      }

      adminStore.setLoginInfo(res.data.token, res.data.admin)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } catch (error) {
      console.error('Login failed', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at 18% 20%, rgba(0, 180, 182, 0.12), transparent 28%),
    radial-gradient(circle at 86% 76%, rgba(229, 158, 60, 0.12), transparent 26%),
    #eef2f6;
}

.login-shell {
  width: min(1120px, 100%);
  min-height: 640px;
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(420px, 0.92fr);
  overflow: hidden;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 24px 70px rgba(22, 35, 54, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.brand-panel {
  position: relative;
  min-height: 640px;
  overflow: hidden;
  background: #132338;
}

.brand-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scale(1.02);
}

.brand-overlay {
  position: absolute;
  inset: 0;
  padding: 38px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: #fff;
  background:
    linear-gradient(135deg, rgba(15, 31, 52, 0.88), rgba(0, 116, 128, 0.48)),
    linear-gradient(0deg, rgba(6, 15, 28, 0.28), rgba(6, 15, 28, 0.12));
}

.brand-top {
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand-mark {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.24);
  font-weight: 800;
  letter-spacing: 0;
}

.eyebrow {
  margin: 0 0 6px;
  color: rgba(255, 255, 255, 0.72);
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0;
}

.brand-top h1 {
  margin: 0;
  font-size: 26px;
  line-height: 1.25;
  font-weight: 700;
  letter-spacing: 0;
}

.brand-copy {
  max-width: 420px;
}

.brand-copy p {
  margin: 0;
  font-size: 30px;
  line-height: 1.42;
  font-weight: 700;
  letter-spacing: 0;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.metric-item {
  min-height: 86px;
  padding: 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(10px);
}

.metric-item strong {
  display: block;
  font-size: 24px;
  line-height: 1.1;
  margin-bottom: 8px;
}

.metric-item span {
  color: rgba(255, 255, 255, 0.76);
  font-size: 13px;
}

.form-panel {
  min-width: 0;
  padding: 56px 64px;
  display: flex;
  align-items: center;
  background:
    linear-gradient(180deg, rgba(250, 252, 255, 0.96), rgba(255, 255, 255, 1));
}

.form-card {
  width: 100%;
}

.form-heading {
  margin-bottom: 32px;
}

.form-heading :deep(.el-tag) {
  margin-bottom: 18px;
  font-weight: 600;
}

.form-heading h2 {
  margin: 0 0 10px;
  color: #1f2d3d;
  font-size: 32px;
  line-height: 1.2;
  font-weight: 800;
  letter-spacing: 0;
}

.form-heading p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.login-form {
  width: 100%;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item__label) {
  margin-bottom: 8px;
  color: #344054;
  font-weight: 700;
  line-height: 1.2;
}

.login-form :deep(.el-input__wrapper) {
  min-height: 48px;
  border-radius: 8px;
  background: #f8fafc;
  box-shadow: 0 0 0 1px #e4e7ed inset;
  transition: box-shadow 0.2s, background 0.2s;
}

.login-form :deep(.el-input__wrapper:hover) {
  background: #fff;
  box-shadow: 0 0 0 1px #b8c2cc inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  background: #fff;
  box-shadow: 0 0 0 2px rgba(0, 180, 182, 0.26), 0 0 0 1px var(--color-primary) inset;
}

.form-tools {
  margin: 2px 0 24px;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  color: #667085;
  font-size: 13px;
}

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-weight: 700;
  letter-spacing: 0;
  background: linear-gradient(135deg, #00aeb0, #1677ff);
  border: none;
  box-shadow: 0 12px 26px rgba(0, 126, 176, 0.22);
}

.login-btn:hover {
  filter: brightness(1.03);
}

.security-note {
  margin-top: 24px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-radius: 8px;
  background: #f5f8fb;
  color: #667085;
  font-size: 13px;
  line-height: 1.5;
}

.security-note .el-icon {
  flex: 0 0 auto;
  color: var(--color-primary-dark-2);
}

@media (max-width: 960px) {
  .login-page {
    padding: 20px;
  }

  .login-shell {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .brand-panel {
    min-height: 320px;
  }

  .brand-copy p {
    font-size: 24px;
  }

  .form-panel {
    padding: 36px 28px;
  }
}

@media (max-width: 560px) {
  .login-page {
    padding: 0;
  }

  .login-shell {
    min-height: 100vh;
    border-radius: 0;
  }

  .brand-panel {
    min-height: 260px;
  }

  .brand-overlay {
    padding: 24px;
  }

  .metric-grid {
    grid-template-columns: 1fr;
  }

  .metric-item {
    min-height: auto;
    padding: 12px 14px;
  }

  .form-tools {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
  }
}
</style>
