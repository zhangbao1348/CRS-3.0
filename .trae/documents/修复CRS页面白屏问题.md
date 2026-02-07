# 修复CRS页面白屏问题

## 问题分析

经过检查，发现router/index.jsx文件中存在一个图标未导入的问题：

* 第206行使用了`<DollarOutlined />`图标

* 但在顶部导入列表中没有导入这个图标

* 这导致了运行时错误，造成页面白屏

## 解决方案

### 修复步骤

1. 在router/index.jsx文件的顶部导入中添加DollarOutlined图标
2. 重启开发服务器（如果需要）

### 预期效果

* 解决页面白屏问题

* 确保所有图标都正确导入

* 页面能够正常渲染

## 实现代码

修改router/index.jsx文件的导入部分：

```javascript
import { 
  HomeOutlined, 
  PlusOutlined, 
  ExportOutlined, 
  ApartmentOutlined, 
  Badge, 
  Card, 
  Row, 
  Col, 
  Button,
  BarChartOutlined,
  LineChartOutlined,
  DollarOutlined, // 添加这个图标导入
  HomeOutlined as HomeIcon
} from 'antd'
```

这个修复很简单，但能解决页面白屏的问题。让我立即实施这个修复。
