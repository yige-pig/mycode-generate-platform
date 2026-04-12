<template>
  <a-layout-footer class="global-footer">
    <div class="footer-content">
      <p>
        lly的原创项目 by 
        <a href="https://github.com/yige-pig" target="_blank" rel="noopener noreferrer">
          yige-pig
        </a>
      </p>
    </div>
  </a-layout-footer>
</template>

<style scoped>
.global-footer {
  background: #f5f5f5;
  border-top: 1px solid #e8e8e8;
  padding: 24px 0;
  position: relative;
  bottom: 0;
  width: 100%;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  text-align: center;
}

.footer-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.footer-content a {
  color: #1890ff;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-content a:hover {
  color: #40a9ff;
  text-decoration: underline;
}

@media (max-width: 768px) {
  .footer-content {
    padding: 0 16px;
  }
  
  .footer-content p {
    font-size: 12px;
  }
}
</style>