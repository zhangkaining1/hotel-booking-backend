<template>
  <div class="dashboard-page">
    <h2 class="page-title">数据概览</h2>
    
    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="stat in statistics" :key="stat.title">
        <el-card class="admin-card stat-card" shadow="never">
          <div class="stat-icon" :style="{ backgroundColor: stat.color + '20', color: stat.color }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">{{ stat.title }}</div>
            <div class="stat-value">{{ stat.value }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 订单趋势图 -->
      <el-col :span="16">
        <el-card class="admin-card chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>近7日订单趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="trendChartOption" autoresize />
          </div>
        </el-card>
      </el-col>

      <!-- 今日入住率 -->
      <el-col :span="8">
        <el-card class="admin-card chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>今日入住率</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="occupancyChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 营业额趋势图 -->
      <el-col :span="16">
        <el-card class="admin-card chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>近7日营业额趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="revenueChartOption" autoresize />
          </div>
        </el-card>
      </el-col>

      <!-- 客户画像 -->
      <el-col :span="8">
        <el-card class="admin-card chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>客户画像</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="radarChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 房型销售占比 -->
      <el-col :span="8">
        <el-card class="admin-card chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>房型销售占比</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="roomChartOption" autoresize />
          </div>
        </el-card>
      </el-col>

      <!-- 订单来源分布 -->
      <el-col :span="8">
        <el-card class="admin-card chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>订单来源分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="sourceChartOption" autoresize />
          </div>
        </el-card>
      </el-col>

      <!-- 订单转化漏斗 -->
      <el-col :span="8">
        <el-card class="admin-card chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>订单转化漏斗</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="funnelChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新动态列表 -->
    <el-row :gutter="20" class="list-row">
      <el-col :span="12">
        <el-card class="admin-card list-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>待处理预约</span>
              <el-button link type="primary">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" style="width: 100%" :show-header="false">
            <el-table-column prop="orderNo" label="订单号" width="120" />
            <el-table-column prop="hotel" label="酒店" />
            <el-table-column prop="user" label="用户" width="100" />
            <el-table-column label="状态" width="100" align="right">
              <template #default>
                <el-tag type="warning" size="small" effect="light" round>待确认</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="admin-card list-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>最新评价</span>
              <el-button link type="primary">查看全部</el-button>
            </div>
          </template>
          <div class="review-list">
            <div class="review-item" v-for="(review, index) in recentReviews" :key="index">
              <div class="review-header">
                <span class="review-user">{{ review.user }}</span>
                <el-rate v-model="review.score" disabled text-color="#ff9900" />
              </div>
              <div class="review-content">{{ review.content }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart, RadarChart, GaugeChart, FunnelChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  RadarComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { Ticket, OfficeBuilding, Money, Bell } from '@element-plus/icons-vue'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  BarChart,
  RadarChart,
  GaugeChart,
  FunnelChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  RadarComponent
])

// 顶部统计数据
const statistics = ref([
  { title: '今日订单数', value: '128', icon: 'Ticket', color: '#00b4b6' },
  { title: '今日入住数', value: '86', icon: 'OfficeBuilding', color: '#67c23a' },
  { title: '今日营业额(元)', value: '￥45,280', icon: 'Money', color: '#faad14' },
  { title: '待处理事项', value: '12', icon: 'Bell', color: '#ff4d4f' }
])

// 折线图配置
const trendChartOption = ref({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [], // dynamic
    axisLine: { lineStyle: { color: '#dcdfe6' } },
    axisLabel: { color: '#606266' }
  },
  yAxis: {
    type: 'value',
    axisLine: { show: false },
    axisTick: { show: false },
    splitLine: { lineStyle: { color: '#ebeef5', type: 'dashed' } }
  },
  series: [
    {
      name: '订单数',
      type: 'line',
      smooth: true,
      data: [], // dynamic
      itemStyle: { color: '#00b4b6' },
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(0, 180, 182, 0.3)' },
            { offset: 1, color: 'rgba(0, 180, 182, 0)' }
          ]
        }
      }
    }
  ]
})

// 饼图配置
const roomChartOption = ref({
  tooltip: { trigger: 'item' },
  legend: { top: 'bottom' },
  series: [
    {
      name: '房型销量',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false, position: 'center' },
      emphasis: {
        label: { show: true, fontSize: '18', fontWeight: 'bold' }
      },
      labelLine: { show: false },
      data: [] // dynamic
    }
  ]
})

// 营业额柱状图配置
const revenueChartOption = ref({
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: ['05-30', '05-31', '06-01', '06-02', '06-03', '06-04', '06-05'], // mock data by default
    axisLine: { lineStyle: { color: '#dcdfe6' } },
    axisLabel: { color: '#606266' }
  },
  yAxis: {
    type: 'value',
    axisLine: { show: false },
    axisTick: { show: false },
    splitLine: { lineStyle: { color: '#ebeef5', type: 'dashed' } }
  },
  series: [
    {
      name: '营业额',
      type: 'bar',
      barWidth: '40%',
      data: [3000, 4500, 3200, 5000, 4800, 6000, 7500], // mock data by default
      itemStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#79bbff' }
          ]
        },
        borderRadius: [4, 4, 0, 0]
      }
    }
  ]
})

// 订单来源分布配置
const sourceChartOption = ref({
  tooltip: { trigger: 'item' },
  legend: { top: 'bottom' },
  series: [
    {
      name: '订单来源',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false, position: 'center' },
      emphasis: {
        label: { show: true, fontSize: '18', fontWeight: 'bold' }
      },
      labelLine: { show: false },
      data: [
        { value: 1048, name: '微信小程序' },
        { value: 735, name: 'APP应用' },
        { value: 580, name: '网页端' },
        { value: 300, name: '线下前台' }
      ] // mock data by default
    }
  ]
})

// 入住率仪表盘配置
const occupancyChartOption = ref({
  tooltip: { formatter: '{a} <br/>{b} : {c}%' },
  series: [
    {
      name: '今日入住率',
      type: 'gauge',
      progress: { show: true, width: 10 },
      axisLine: { lineStyle: { width: 10 } },
      axisTick: { show: false },
      splitLine: { length: 15, lineStyle: { width: 2, color: '#999' } },
      axisLabel: { distance: 15, color: '#999', fontSize: 10 },
      anchor: { show: true, showAbove: true, size: 15, itemStyle: { borderWidth: 4 } },
      title: { show: false },
      detail: { valueAnimation: true, fontSize: 24, offsetCenter: [0, '70%'], formatter: '{value}%' },
      data: [{ value: 78, name: '入住率' }]
    }
  ]
})

// 客户画像雷达图配置
const radarChartOption = ref({
  tooltip: {},
  radar: {
    indicator: [
      { name: '商务出差', max: 100 },
      { name: '亲子旅游', max: 100 },
      { name: '情侣度假', max: 100 },
      { name: '个人休闲', max: 100 },
      { name: '会议团队', max: 100 }
    ],
    radius: '60%',
    center: ['50%', '55%']
  },
  series: [
    {
      name: '客户画像',
      type: 'radar',
      data: [
        {
          value: [85, 60, 70, 50, 40],
          name: '占比',
          areaStyle: { color: 'rgba(64, 158, 255, 0.4)' },
          itemStyle: { color: '#409EFF' },
          lineStyle: { color: '#409EFF' }
        }
      ]
    }
  ]
})

// 订单转化漏斗图配置
const funnelChartOption = ref({
  tooltip: { trigger: 'item', formatter: '{a} <br/>{b} : {c}%' },
  series: [
    {
      name: '转化率',
      type: 'funnel',
      left: '10%',
      top: 20,
      bottom: 20,
      width: '80%',
      min: 0,
      max: 100,
      minSize: '0%',
      maxSize: '100%',
      sort: 'descending',
      gap: 2,
      label: { show: true, position: 'inside' },
      itemStyle: { borderColor: '#fff', borderWidth: 1 },
      data: [
        { value: 100, name: '访问浏览' },
        { value: 60, name: '加入订单' },
        { value: 40, name: '成功支付' },
        { value: 35, name: '最终入住' }
      ]
    }
  ]
})

// 接入近期订单/评价接口
const recentOrders = ref([])

const recentReviews = ref([])

import request from '@/api/request'
import { onMounted } from 'vue'

const fetchDashboardData = async () => {
  try {
    const res = await request.get('/admin/statistics/dashboard')
    const data = res.data || {}
    
    // Update top cards
    statistics.value[0].value = data.todayOrders || 0
    statistics.value[1].value = data.todayCheckins || 0
    statistics.value[2].value = '￥' + (data.todayRevenue || 0)
    statistics.value[3].value = data.pendingTasks || 0
    
    // Update line chart
    if (data.trendData) {
      trendChartOption.value.xAxis.data = data.trendData.map(item => item.name)
      trendChartOption.value.series[0].data = data.trendData.map(item => item.value)
    }
    
    // Update pie chart
    if (data.roomSalesData) {
      roomChartOption.value.series[0].data = data.roomSalesData.map(item => {
        return { value: item.value, name: item.name }
      })
    }

    // Update revenue bar chart (mock if not present in API)
    if (data.revenueData) {
      revenueChartOption.value.xAxis.data = data.revenueData.map(item => item.name)
      revenueChartOption.value.series[0].data = data.revenueData.map(item => item.value)
    }

    // Update source pie chart (mock if not present in API)
    if (data.sourceData) {
      sourceChartOption.value.series[0].data = data.sourceData.map(item => {
        return { value: item.value, name: item.name }
      })
    }

    // Update lists
    if (data.recentOrders) {
      recentOrders.value = data.recentOrders
    }
    if (data.recentReviews) {
      recentReviews.value = data.recentReviews
    }
  } catch(err) {
    console.error('获取大盘数据失败', err)
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stat-cards {
  margin-bottom: 0;
}

.stat-card {
  height: 100px;
}
.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 20px;
  height: 100%;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--color-text-primary);
}

.chart-row {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: var(--color-text-primary);
}

.chart-container {
  height: 300px;
}
.chart {
  width: 100%;
  height: 100%;
}

.list-card {
  height: 360px;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}
.review-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.review-user {
  font-weight: 600;
  color: var(--color-text-primary);
  font-size: 14px;
}

.review-content {
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.5;
}
</style>
