<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
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
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger @click="doDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUser, listUserVoByPage } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 展示的数据
const data = ref<API.UserVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  const res = await listUserVoByPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

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

// 表格分页变化时的操作
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索数据
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteUser({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#userManagePage {
  padding: 24px;
  background: transparent;
  margin-top: 16px;
  min-height: calc(100vh - 100px);
  position: relative;
}

#userManagePage::before {
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

#userManagePage::after {
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

:deep(.ant-table) {
  background: rgba(15, 23, 42, 0.8) !important;
  border-radius: 12px;
  overflow: hidden;
}

:deep(.ant-table-thead > tr > th) {
  background: rgba(59, 130, 246, 0.15) !important;
  color: #e2e8f0 !important;
  border-bottom: 1px solid rgba(59, 130, 246, 0.3) !important;
  font-weight: 600;
}

:deep(.ant-table-tbody > tr > td) {
  background: rgba(15, 23, 42, 0.6) !important;
  color: #94a3b8 !important;
  border-bottom: 1px solid rgba(59, 130, 246, 0.1) !important;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: rgba(59, 130, 246, 0.1) !important;
}

:deep(.ant-table-wrapper .ant-pagination) {
  background: transparent !important;
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

:deep(.ant-pagination-prev .ant-pagination-item-link),
:deep(.ant-pagination-next .ant-pagination-item-link) {
  background: rgba(15, 23, 42, 0.8) !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
  color: #94a3b8 !important;
}

:deep(.ant-select-selector) {
  background: rgba(15, 23, 42, 0.8) !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
  color: #94a3b8 !important;
}

:deep(.ant-input) {
  background: rgba(15, 23, 42, 0.8) !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
  color: #e2e8f0 !important;
}

:deep(.ant-input::placeholder) {
  color: #64748b !important;
}

:deep(.ant-input:focus) {
  border-color: rgba(59, 130, 246, 0.6) !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
}

:deep(.ant-btn-primary) {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%) !important;
  border: none !important;
}

:deep(.ant-form-item-label > label) {
  color: #94a3b8 !important;
}

:deep(.ant-divider) {
  border-color: rgba(59, 130, 246, 0.2) !important;
}

:deep(.ant-tag) {
  border-radius: 12px !important;
}

:deep(.ant-image img) {
  border-radius: 8px;
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
</style>
