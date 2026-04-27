<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { addApp, listMyAppVoByPage, listGoodAppVoByPage } from '@/api/appController'
import { getDeployUrl } from '@/config/env'
import AppCard from '@/components/AppCard.vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const userPrompt = ref('')
const creating = ref(false)

const myApps = ref<API.AppVO[]>([])
const myAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

const featuredApps = ref<API.AppVO[]>([])
const featuredAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

const setPrompt = (prompt: string) => {
  userPrompt.value = prompt
}

const createApp = async () => {
  if (!userPrompt.value.trim()) {
    message.warning('请输入应用描述')
    return
  }

  if (!loginUserStore.loginUser.id) {
    message.warning('请先登录')
    await router.push('/user/login')
    return
  }

  creating.value = true
  try {
    const res = await addApp({
      initPrompt: userPrompt.value.trim(),
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('应用创建成功')
      const appId = String(res.data.data)
      await router.push(`/app/chat/${appId}`)
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch (error) {
    console.error('创建应用失败：', error)
    message.error('创建失败，请重试')
  } finally {
    creating.value = false
  }
}

const loadMyApps = async () => {
  if (!loginUserStore.loginUser.id) {
    return
  }

  try {
    const res = await listMyAppVoByPage({
      pageNum: myAppsPage.current,
      pageSize: myAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      myApps.value = res.data.data.records || []
      myAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载我的应用失败：', error)
  }
}

const loadFeaturedApps = async () => {
  try {
    const res = await listGoodAppVoByPage({
      pageNum: featuredAppsPage.current,
      pageSize: featuredAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      featuredApps.value = res.data.data.records || []
      featuredAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载精选应用失败：', error)
  }
}

const viewChat = (appId: string | number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}?view=1`)
  }
}

const viewWork = (app: API.AppVO) => {
  if (app.deployKey) {
    const url = getDeployUrl(app.deployKey)
    window.open(url, '_blank')
  }
}

let animationFrameId: number
let canvas: HTMLCanvasElement | null = null
let ctx: CanvasRenderingContext2D | null = null
const particles: Particle[] = []
let mouseX = 0
let mouseY = 0
let gridOffset = 0

interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  size: number
  color: string
  alpha: number
  life: number
  maxLife: number
}

const colors = ['#3b82f6', '#8b5cf6', '#10b981', '#06b6d4', '#f472b6', '#a855f7']

const createParticle = (x?: number, y?: number): Particle => {
  return {
    x: x ?? Math.random() * window.innerWidth,
    y: y ?? Math.random() * window.innerHeight,
    vx: (Math.random() - 0.5) * 2,
    vy: (Math.random() - 0.5) * 2,
    size: Math.random() * 3 + 1,
    color: colors[Math.floor(Math.random() * colors.length)],
    alpha: Math.random() * 0.5 + 0.3,
    life: 0,
    maxLife: Math.random() * 200 + 100,
  }
}

const drawGrid = () => {
  if (!ctx || !canvas) return
  const { width, height } = canvas

  ctx.strokeStyle = 'rgba(59, 130, 246, 0.08)'
  ctx.lineWidth = 1

  const gridSize = 60
  const offsetY = gridOffset % gridSize

  for (let y = -gridSize + offsetY; y < height + gridSize; y += gridSize) {
    ctx.beginPath()
    ctx.moveTo(0, y)
    ctx.lineTo(width, y)
    ctx.stroke()
  }

  for (let x = 0; x < width; x += gridSize) {
    ctx.beginPath()
    ctx.moveTo(x, 0)
    ctx.lineTo(x, height)
    ctx.stroke()
  }

  ctx.strokeStyle = 'rgba(139, 92, 246, 0.1)'
  for (let i = -height; i < width + height; i += 200) {
    ctx.beginPath()
    ctx.moveTo(i, 0)
    ctx.lineTo(i + height, height)
    ctx.stroke()
    ctx.beginPath()
    ctx.moveTo(i, height)
    ctx.lineTo(i + height, 0)
    ctx.stroke()
  }
}

const drawParticles = () => {
  if (!ctx || !canvas) return

  particles.forEach((p, index) => {
    p.x += p.vx
    p.y += p.vy
    p.life++

    const lifeRatio = p.life / p.maxLife
    const currentAlpha = p.alpha * (lifeRatio < 0.3 ? lifeRatio / 0.3 : 1 - (lifeRatio - 0.3) / 0.7)

    if (p.life > p.maxLife || currentAlpha <= 0) {
      particles[index] = createParticle()
      return
    }

    ctx!.beginPath()
    ctx!.arc(p.x, p.y, p.size, 0, Math.PI * 2)
    ctx!.fillStyle = p.color.replace(')', `, ${currentAlpha})`).replace('rgb', 'rgba').replace('#', '')

    const gradient = ctx!.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.size * 3)
    gradient.addColorStop(0, p.color + Math.floor(currentAlpha * 255).toString(16).padStart(2, '0'))
    gradient.addColorStop(1, 'transparent')
    ctx!.fillStyle = gradient
    ctx!.fill()
  })
}

const drawConnections = () => {
  if (!ctx || !canvas) return
  const maxDistance = 150

  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[i].x - particles[j].x
      const dy = particles[i].y - particles[j].y
      const distance = Math.sqrt(dx * dx + dy * dy)

      if (distance < maxDistance) {
        const alpha = (1 - distance / maxDistance) * 0.3
        ctx.beginPath()
        ctx.moveTo(particles[i].x, particles[i].y)
        ctx.lineTo(particles[j].x, particles[j].y)
        ctx.strokeStyle = `rgba(59, 130, 246, ${alpha})`
        ctx.lineWidth = 1
        ctx.stroke()
      }
    }
  }
}

const drawMouseEffect = () => {
  if (!ctx || !canvas) return

  const gradient = ctx.createRadialGradient(mouseX, mouseY, 0, mouseX, mouseY, 200)
  gradient.addColorStop(0, 'rgba(59, 130, 246, 0.08)')
  gradient.addColorStop(0.5, 'rgba(139, 92, 246, 0.04)')
  gradient.addColorStop(1, 'transparent')
  ctx.fillStyle = gradient
  ctx.fillRect(0, 0, canvas.width, canvas.height)

  ctx.beginPath()
  ctx.arc(mouseX, mouseY, 50, 0, Math.PI * 2)
  ctx.strokeStyle = 'rgba(59, 130, 246, 0.15)'
  ctx.lineWidth = 2
  ctx.stroke()

  ctx.beginPath()
  ctx.arc(mouseX, mouseY, 100, 0, Math.PI * 2)
  ctx.strokeStyle = 'rgba(139, 92, 246, 0.08)'
  ctx.lineWidth = 1
  ctx.stroke()
}

const animate = () => {
  if (!ctx || !canvas) return

  ctx.fillStyle = 'rgba(10, 14, 23, 0.95)'
  ctx.fillRect(0, 0, canvas.width, canvas.height)

  drawGrid()
  drawMouseEffect()
  drawConnections()
  drawParticles()

  gridOffset += 0.3

  animationFrameId = requestAnimationFrame(animate)
}

const initCanvas = () => {
  canvas = document.getElementById('particleCanvas') as HTMLCanvasElement
  if (!canvas) return

  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  ctx = canvas.getContext('2d')
  if (!ctx) return

  for (let i = 0; i < 80; i++) {
    particles.push(createParticle())
  }

  animate()
}

const handleResize = () => {
  if (canvas) {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
}

const handleMouseMove = (e: MouseEvent) => {
  mouseX = e.clientX
  mouseY = e.clientY

  document.documentElement.style.setProperty('--mouse-x', `${(e.clientX / window.innerWidth) * 100}%`)
  document.documentElement.style.setProperty('--mouse-y', `${(e.clientY / window.innerHeight) * 100}%`)

  if (Math.random() < 0.3) {
    particles.push(createParticle(e.clientX, e.clientY))
    if (particles.length > 150) {
      particles.shift()
    }
  }
}

onMounted(() => {
  loadMyApps()
  loadFeaturedApps()
  initCanvas()
  document.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
  document.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('resize', handleResize)
})
</script>

<template>
  <div id="homePage">
    <canvas id="particleCanvas"></canvas>
    <div class="cyber-overlay"></div>
    <div class="container">
      <div class="hero-section">
        <div class="glitch-container">
          <h1 class="hero-title">AI 应用生成平台</h1>
        </div>
        <p class="hero-description">一句话轻松创建网站应用</p>
        <div class="typing-text">
          <span class="typed-text"></span>
          <span class="cursor"></span>
        </div>
      </div>

      <div class="input-section">
        <a-textarea
          v-model:value="userPrompt"
          placeholder="帮我创建个人博客网站"
          :rows="4"
          :maxlength="1000"
          class="prompt-input cyber-input"
        />
        <div class="input-actions">
          <a-button type="primary" size="large" @click="createApp" :loading="creating" class="cyber-button">
            <template #icon>
              <span class="btn-icon">↑</span>
            </template>
          </a-button>
        </div>
      </div>

      <div class="quick-actions">
        <a-button
          type="default"
          @click="
            setPrompt(
              '创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。',
            )
          "
          >个人博客网站</a-button
        >
        <a-button
          type="default"
          @click="
            setPrompt(
              '设计一个专业的企业官网，包含公司介绍、产品服务展示、新闻资讯、联系我们等页面。采用商务风格的设计，包含轮播图、产品展示卡片、团队介绍、客户案例展示，支持多语言切换和在线客服功能。',
            )
          "
          >企业官网</a-button
        >
        <a-button
          type="default"
          @click="
            setPrompt(
              '构建一个功能完整的在线商城，包含商品展示、购物车、用户注册登录、订单管理、支付结算等功能。设计现代化的商品卡片布局，支持商品搜索筛选、用户评价、优惠券系统和会员积分功能。',
            )
          "
          >在线商城</a-button
        >
        <a-button
          type="default"
          @click="
            setPrompt(
              '制作一个精美的作品展示网站，适合设计师、摄影师、艺术家等创作者。包含作品画廊、项目详情页、个人简历、联系方式等模块。采用瀑布流或网格布局展示作品，支持图片放大预览和作品分类筛选。',
            )
          "
          >作品展示网站</a-button
        >
      </div>

      <!-- 我的作品 -->
      <div class="section">
        <h2 class="section-title">我的作品</h2>
        <div class="app-grid">
          <AppCard
            v-for="app in myApps"
            :key="app.id"
            :app="app"
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="myAppsPage.current"
            v-model:page-size="myAppsPage.pageSize"
            :total="myAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个应用`"
            @change="loadMyApps"
          />
        </div>
      </div>

      <!-- 精选案例 -->
      <div class="section">
        <h2 class="section-title">精选案例</h2>
        <div class="featured-grid">
          <AppCard
            v-for="app in featuredApps"
            :key="app.id"
            :app="app"
            :featured="true"
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="featuredAppsPage.current"
            v-model:page-size="featuredAppsPage.pageSize"
            :total="featuredAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个案例`"
            @change="loadFeaturedApps"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#particleCanvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

#homePage {
  width: 100%;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  background: transparent;
}

.cyber-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(ellipse 80% 50% at 50% 0%, rgba(59, 130, 246, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse 60% 40% at 80% 100%, rgba(139, 92, 246, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse 50% 30% at 10% 80%, rgba(16, 185, 129, 0.08) 0%, transparent 50%);
  pointer-events: none;
  z-index: 1;
  animation: overlayPulse 8s ease-in-out infinite alternate;
}

@keyframes overlayPulse {
  0% {
    opacity: 0.6;
  }
  100% {
    opacity: 1;
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
  z-index: 10;
  width: 100%;
  box-sizing: border-box;
}

.hero-section {
  text-align: center;
  padding: 80px 0 60px;
  margin-bottom: 28px;
  color: #e2e8f0;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  left: 50%;
  transform: translateX(-50%);
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, rgba(139, 92, 246, 0.1) 40%, transparent 70%);
  animation: heroFloat 10s ease-in-out infinite;
  pointer-events: none;
}

@keyframes heroFloat {
  0%, 100% {
    transform: translateX(-50%) translateY(0) scale(1);
  }
  50% {
    transform: translateX(-50%) translateY(-20px) scale(1.05);
  }
}

.glitch-container {
  position: relative;
  display: inline-block;
}

.hero-title {
  font-size: 56px;
  font-weight: 700;
  margin: 0 0 20px;
  line-height: 1.2;
  background: linear-gradient(135deg, #60a5fa 0%, #a78bfa 25%, #34d399 50%, #60a5fa 75%, #a78bfa 100%);
  background-size: 200% 200%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -1px;
  position: relative;
  z-index: 2;
  animation: titleShimmer 4s ease-in-out infinite;
  text-shadow: none;
  filter: drop-shadow(0 0 30px rgba(59, 130, 246, 0.5));
}

@keyframes titleShimmer {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.glitch-container::before,
.glitch-container::after {
  content: 'AI 应用生成平台';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #60a5fa 0%, #a78bfa 25%, #34d399 50%, #60a5fa 75%, #a78bfa 100%);
  background-size: 200% 200%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: titleShimmer 4s ease-in-out infinite;
}

.glitch-container::before {
  animation: glitch1 2s infinite linear alternate-reverse;
  clip-path: polygon(0 0, 100% 0, 100% 35%, 0 35%);
  opacity: 0.8;
}

.glitch-container::after {
  animation: glitch2 3s infinite linear alternate-reverse;
  clip-path: polygon(0 65%, 100% 65%, 100% 100%, 0 100%);
  opacity: 0.8;
}

@keyframes glitch1 {
  0%, 100% {
    transform: translate(0);
  }
  20% {
    transform: translate(-2px, 1px);
  }
  40% {
    transform: translate(2px, -1px);
  }
  60% {
    transform: translate(-1px, 2px);
  }
  80% {
    transform: translate(1px, -2px);
  }
}

@keyframes glitch2 {
  0%, 100% {
    transform: translate(0);
  }
  20% {
    transform: translate(2px, -1px);
  }
  40% {
    transform: translate(-2px, 1px);
  }
  60% {
    transform: translate(1px, -2px);
  }
  80% {
    transform: translate(-1px, 2px);
  }
}

.hero-description {
  font-size: 20px;
  margin: 0 0 20px;
  color: #94a3b8;
  position: relative;
  z-index: 2;
}

.typing-text {
  font-size: 16px;
  color: #64748b;
  height: 24px;
  position: relative;
  z-index: 2;
}

.cursor {
  display: inline-block;
  width: 2px;
  height: 20px;
  background: #60a5fa;
  margin-left: 4px;
  animation: blink 1s infinite;
  vertical-align: middle;
}

@keyframes blink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0;
  }
}

.input-section {
  position: relative;
  margin: 0 auto 24px;
  max-width: 800px;
}

.cyber-input {
  border-radius: 16px;
  border: 1px solid rgba(59, 130, 246, 0.3);
  font-size: 16px;
  padding: 20px 60px 20px 20px;
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(20px);
  box-shadow:
    0 10px 40px rgba(0, 0, 0, 0.4),
    inset 0 0 20px rgba(59, 130, 246, 0.05),
    0 0 0 1px rgba(59, 130, 246, 0.1);
  color: #e2e8f0;
  transition: all 0.3s ease;
}

.cyber-input::placeholder {
  color: #64748b;
}

.cyber-input:focus {
  background: rgba(15, 23, 42, 0.9);
  border-color: rgba(59, 130, 246, 0.6);
  box-shadow:
    0 15px 50px rgba(0, 0, 0, 0.5),
    inset 0 0 30px rgba(59, 130, 246, 0.1),
    0 0 0 1px rgba(59, 130, 246, 0.3),
    0 0 30px rgba(59, 130, 246, 0.2);
  transform: translateY(-2px);
}

.input-actions {
  position: absolute;
  bottom: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  align-items: center;
}

.cyber-button {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%) !important;
  border: none !important;
  border-radius: 12px !important;
  box-shadow:
    0 4px 15px rgba(59, 130, 246, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.cyber-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.cyber-button:hover::before {
  left: 100%;
}

.cyber-button:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow:
    0 8px 25px rgba(59, 130, 246, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.btn-icon {
  position: relative;
  z-index: 2;
}

.quick-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 60px;
  flex-wrap: wrap;
}

.quick-actions .ant-btn {
  border-radius: 25px;
  padding: 8px 20px;
  height: auto;
  background: rgba(15, 23, 42, 0.6) !important;
  border: 1px solid rgba(59, 130, 246, 0.3) !important;
  color: #94a3b8 !important;
  backdrop-filter: blur(15px);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.quick-actions .ant-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.15), transparent);
  transition: left 0.5s ease;
}

.quick-actions .ant-btn:hover::before {
  left: 100%;
}

.quick-actions .ant-btn:hover {
  background: rgba(59, 130, 246, 0.2) !important;
  border-color: rgba(59, 130, 246, 0.6) !important;
  color: #60a5fa !important;
  transform: translateY(-2px);
  box-shadow:
    0 8px 25px rgba(59, 130, 246, 0.3),
    0 0 15px rgba(59, 130, 246, 0.2);
}

.section {
  margin-bottom: 60px;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 32px;
  color: #e2e8f0;
  position: relative;
  display: inline-block;
}

.section-title::before {
  content: '';
  position: absolute;
  left: -20px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #3b82f6, #8b5cf6);
  border-radius: 2px;
}

.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
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

:deep(.ant-pagination-total-text) {
  color: #e2e8f0 !important;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }

  .hero-description {
    font-size: 16px;
  }

  .app-grid,
  .featured-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 60px 0 40px;
  }

  .hero-title {
    font-size: 28px;
  }
}
</style>
