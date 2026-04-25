<template>
  <div id="appManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="应用名称">
        <a-input v-model:value="searchParams.appName" placeholder="输入应用名称" />
      </a-form-item>
      <a-form-item label="创建者">
        <a-input v-model:value="searchParams.userId" placeholder="输入用户ID" />
      </a-form-item>
      <a-form-item label="生成类型">
        <a-select
          v-model:value="searchParams.codeGenType"
          placeholder="选择生成类型"
          style="width: 150px"
        >
          <a-select-option value="">全部</a-select-option>
          <a-select-option
            v-for="option in CODE_GEN_TYPE_OPTIONS"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      @change="doTableChange"
      :scroll="{ x: 1200 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'cover'">
          <a-image v-if="record.cover" :src="record.cover" :width="80" :height="60" />
          <div v-else class="no-cover">无封面</div>
        </template>
        <template v-else-if="column.dataIndex === 'initPrompt'">
          <a-tooltip :title="record.initPrompt">
            <div class="prompt-text">{{ record.initPrompt }}</div>
          </a-tooltip>
        </template>
        <template v-else-if="column.dataIndex === 'codeGenType'">
          {{ formatCodeGenType(record.codeGenType) }}
        </template>
        <template v-else-if="column.dataIndex === 'priority'">
          <a-tag v-if="record.priority === 99" color="gold">精选</a-tag>
          <span v-else>{{ record.priority || 0 }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'deployedTime'">
          <span v-if="record.deployedTime">
            {{ formatTime(record.deployedTime) }}
          </span>
          <span v-else class="text-gray">未部署</span>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ formatTime(record.createTime) }}
        </template>
        <template v-else-if="column.dataIndex === 'user'">
          <UserInfo :user="record.user" size="small" />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="primary" size="small" @click="editApp(record)"> 编辑 </a-button>
            <a-button
              type="default"
              size="small"
              @click="toggleFeatured(record)"
              :class="{ 'featured-btn': record.priority === 99 }"
            >
              {{ record.priority === 99 ? '取消精选' : '精选' }}
            </a-button>
            <a-popconfirm title="确定要删除这个应用吗？" @confirm="deleteApp(record.id)">
              <a-button danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listAppVoByPageByAdmin, deleteAppByAdmin, updateAppByAdmin } from '@/api/appController'
import { CODE_GEN_TYPE_OPTIONS, formatCodeGenType } from '@/utils/codeGenTypes'
import { formatTime } from '@/utils/time'
import UserInfo from '@/components/UserInfo.vue'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    fixed: 'left',
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    width: 150,
  },
  {
    title: '封面',
    dataIndex: 'cover',
    width: 100,
  },
  {
    title: '初始提示词',
    dataIndex: 'initPrompt',
    width: 200,
  },
  {
    title: '生成类型',
    dataIndex: 'codeGenType',
    width: 100,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 80,
  },
  {
    title: '部署时间',
    dataIndex: 'deployedTime',
    width: 160,
  },
  {
    title: '创建者',
    dataIndex: 'user',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160,
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
  },
]

// 数据
const data = ref<API.AppVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.AppQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAppVoByPageByAdmin({
      ...searchParams,
    })
    if (res.data.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.totalRow ?? 0
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格变化处理
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 编辑应用
const editApp = (app: API.AppVO) => {
  router.push(`/app/edit/${app.id}`)
}

// 切换精选状态
const toggleFeatured = async (app: API.AppVO) => {
  if (!app.id) return

  const newPriority = app.priority === 99 ? 0 : 99

  try {
    const res = await updateAppByAdmin({
      id: app.id,
      priority: newPriority,
    })

    if (res.data.code === 0) {
      message.success(newPriority === 99 ? '已设为精选' : '已取消精选')
      // 刷新数据
      fetchData()
    } else {
      message.error('操作失败：' + res.data.message)
    }
  } catch (error) {
    console.error('操作失败：', error)
    message.error('操作失败')
  }
}

// 删除应用
const deleteApp = async (id: number | undefined) => {
  if (!id) return

  try {
    const res = await deleteAppByAdmin({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      // 刷新数据
      fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}
</script>

<style scoped>
#appManagePage {
  padding: 24px;
  background: transparent;
  margin-top: 16px;
  min-height: calc(100vh - 100px);
  position: relative;
}

#appManagePage::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    linear-gradient(180deg, rgba(10, 14, 23, 0.97) 0%, rgba(15, 23, 42, 0.95) 100%),
    radial-gradient(ellipse at 20% 0%, rgba(59, 130, 246, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 100%, rgba(139, 92, 246, 0.08) 0%, transparent 50%);
  pointer-events: none;
  z-index: -1;
}

#appManagePage::after {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(59, 130, 246, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(59, 130, 246, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  pointer-events: none;
  z-index: -1;
}

.no-cover {
  width: 80px;
  height: 60px;
  background: rgba(59, 130, 246, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  font-size: 12px;
  border-radius: 4px;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.prompt-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #94a3b8;
}

.text-gray {
  color: #64748b;
}

.featured-btn {
  background: rgba(250, 173, 20, 0.2) !important;
  border-color: rgba(250, 173, 20, 0.5) !important;
  color: #fbbf24 !important;
}

.featured-btn:hover {
  background: rgba(250, 173, 20, 0.3) !important;
  border-color: rgba(250, 173, 20, 0.7) !important;
}

:deep(.ant-table) {
  background: rgba(15, 23, 42, 0.8) !important;
  border-radius: 12px;
}

:deep(.ant-table-thead > tr > th) {
  background: rgba(59, 130, 246, 0.15) !important;
  color: #e2e8f0 !important;
  border-bottom: 1px solid rgba(59, 130, 246, 0.3) !important;
}

:deep(.ant-table-tbody > tr > td) {
  background: rgba(15, 23, 42, 0.6) !important;
  color: #94a3b8 !important;
  border-bottom: 1px solid rgba(59, 130, 246, 0.1) !important;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: rgba(59, 130, 246, 0.1) !important;
}

:deep(.ant-table-tbody > tr > td) {
  vertical-align: middle;
}

:deep(.ant-input) {
  background: rgba(15, 23, 42, 0.8) !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
  color: #e2e8f0 !important;
}

:deep(.ant-input::placeholder) {
  color: #64748b !important;
}

:deep(.ant-select-selector) {
  background: rgba(15, 23, 42, 0.8) !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
  color: #94a3b8 !important;
}

:deep(.ant-form-item-label > label) {
  color: #94a3b8 !important;
}

:deep(.ant-divider) {
  border-color: rgba(59, 130, 246, 0.2) !important;
}

:deep(.ant-btn-primary) {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%) !important;
  border: none !important;
}

:deep(.ant-btn-dangerous) {
  background: rgba(239, 68, 68, 0.2) !important;
  border-color: rgba(239, 68, 68, 0.5) !important;
  color: #f87171 !important;
}

:deep(.ant-btn-dangerous:hover) {
  background: rgba(239, 68, 68, 0.3) !important;
  border-color: rgba(239, 68, 68, 0.7) !important;
}

:deep(.ant-pagination-item) {
  background: rgba(15, 23, 42, 0.8) !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
}

:deep(.ant-pagination-item a) {
  color: #94a3b8 !important;
}

:deep(.ant-pagination-item-active) {
  background: rgba(59, 130, 246, 0.3) !important;
  border-color: rgba(59, 130, 246, 0.5) !important;
}

:deep(.ant-pagination-item-active a) {
  color: #60a5fa !important;
}

:deep(.ant-image img) {
  border-radius: 8px;
}

:deep(.ant-tag) {
  border-radius: 12px !important;
}
</style>
