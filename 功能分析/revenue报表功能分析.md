# Revenue报表功能分析

## 1. 页面内容

### 1.1 页面标题
- **营收报表**：页面顶部显示的标题

### 1.2 筛选区域
- **酒店**：下拉选择框，包含选项：
  - 全集团
  - 上海宝丽嘉
  - 杭州钓美
  - 北京王府井
  - 深圳南山
- **统计方式**：下拉选择框，包含选项：
  - 按酒店纬度
  - 按房型纬度
- **月份**：月份选择器
- **搜索按钮**：点击执行搜索
- **导出按钮**：点击导出报表

### 1.3 报表区域
- **报表表格**：
  - 酒店：显示酒店名称，合并相同酒店的行
  - 房型：当统计方式为"按房型纬度"时显示，合并相同房型的行
  - 指标类型：显示指标类型，包括：
    - 总订单数
    - 平均房价
  - 日期列：显示当月的每一天，格式为"MM-DD (星期)"
  - 数据单元格：显示对应日期的数值，平均房价显示为货币格式

## 2. 交互逻辑

### 2.1 筛选功能
- **选择酒店**：根据选择的酒店筛选数据
  - 全集团：显示所有酒店的数据
  - 特定酒店：只显示该酒店的数据
- **选择统计方式**：切换统计维度
  - 按酒店纬度：按酒店分组显示数据
  - 按房型纬度：按酒店和房型分组显示数据
- **选择月份**：选择要查看的月份
- **点击搜索按钮**：根据筛选条件执行查询，显示报表数据
- **点击导出按钮**：导出报表数据

### 2.2 数据展示
- **行合并**：相同酒店或房型的行自动合并
- **数据格式**：平均房价数据显示为货币格式
- **日期显示**：每个日期列显示日期和星期

## 3. 数据结构

### 3.1 酒店数据
```javascript
const hotels = ['全集团', '上海宝丽嘉', '杭州钓美', '北京王府井', '深圳南山']
```

### 3.2 房型数据
```javascript
const roomTypes = {
  '上海宝丽嘉': ['豪华大床房', '行政套房', '标准双床房', '总统套房'],
  '杭州钓美': ['湖景房', '山景房', '豪华套房', '标准间'],
  '北京王府井': ['城景房', '豪华间', '套房', '标准房'],
  '深圳南山': ['海景房', '行政房', '豪华套房', '标准间']
}
```

### 3.3 报表数据结构
```javascript
const data = [
  {
    key: '1',
    hotel: '上海宝丽嘉',
    inventoryType: '总订单数',
    day1: 95,
    day2: 98,
    // 更多日期数据...
  },
  {
    key: '2',
    hotel: '上海宝丽嘉',
    inventoryType: '平均房价',
    day1: 1200,
    day2: 1250,
    // 更多日期数据...
  },
  // 更多数据...
]
```

## 4. 技术实现

### 4.1 前端框架
- React 18.2.0
- Ant Design 5.12.0
- dayjs 1.11.19

### 4.2 组件使用
- Select：用于下拉选择
- Button：用于操作按钮
- DatePicker：用于月份选择
- Table：用于显示报表数据
- Icon：用于按钮图标

### 4.3 状态管理
- useState：用于管理组件内部状态
  - selectedMonth：选中的月份
  - selectedHotel：选中的酒店
  - selectedStatisticMethod：选中的统计方式

### 4.4 数据生成
- generateDailyOrders：生成每日订单数数据
- generateDailyRates：生成每日平均房价数据
- generateGroupDailyOrders：生成全集团每日订单数数据
- generateGroupDailyRates：生成全集团每日平均房价数据
- getHotelDataSource：获取按酒店纬度的数据源
- getRoomTypeDataSource：获取按房型纬度的数据源
- getFilteredData：根据筛选条件获取过滤后的数据

### 4.5 表格配置
- generateDateTitle：生成日期列标题
- getColumns：生成表格列配置
  - 酒店列：实现行合并
  - 房型列：实现行合并（当统计方式为按房型纬度时）
  - 指标类型列：显示指标类型
  - 日期列：显示每日数据

### 4.6 行合并逻辑
- 按酒店纬度：每个酒店的2行数据（2种指标类型）合并为一行
- 按房型纬度：全集团的2行数据合并为一行，每个酒店的8行数据（4种房型 × 2种指标类型）合并为一行，每个房型的2行数据合并为一行

## 5. 代码结构

### 5.1 组件结构
```javascript
const RevenueReports = () => {
  // 状态管理
  // 酒店和房型数据
  // 数据生成函数
  // 数据源获取函数
  // 数据过滤函数
  // 日期标题生成函数
  // 表格列配置函数
  // 渲染筛选区域
  // 渲染报表表格
}
```

### 5.2 数据过滤逻辑
```javascript
const getFilteredData = () => {
  let data
  if (selectedStatisticMethod === '按酒店纬度') {
    data = getHotelDataSource()
  } else {
    data = getRoomTypeDataSource()
  }

  if (selectedHotel === '全集团') {
    return data
  }

  return data.filter(item => item.hotel === selectedHotel)
}
```

### 5.3 表格列配置逻辑
```javascript
const getColumns = () => {
  const columns = []

  // 添加酒店列
  columns.push({
    title: '酒店',
    dataIndex: 'hotel',
    key: 'hotel',
    // 行合并逻辑
  })

  // 添加房型列（当统计方式为按房型纬度时）
  if (selectedStatisticMethod === '按房型纬度') {
    columns.push({
      title: '房型',
      dataIndex: 'roomType',
      key: 'roomType',
      // 行合并逻辑
    })
  }

  // 添加指标类型列
  columns.push({
    title: '指标类型',
    dataIndex: 'inventoryType',
    key: 'inventoryType',
  })

  // 添加日期列
  for (let i = 1; i <= 31; i++) {
    columns.push({
      title: generateDateTitle(i),
      dataIndex: `day${i}`,
      key: `day${i}`,
      // 数据渲染逻辑
    })
  }

  return columns
}
```

## 6. 总结

营收报表是CRS系统中用于分析酒店营收数据的功能模块。通过该页面，用户可以查看不同酒店、不同房型在特定月份的总订单数和平均房价等数据。

页面设计清晰，功能完整，交互流畅。用户可以通过选择酒店、统计方式和月份来筛选数据，通过表格查看详细的每日数据。

报表数据展示详细，包括总订单数和平均房价等关键指标，并按日期显示每日数据，帮助用户直观了解酒店的营收情况。

未来可以通过后端API获取实时营收数据，实现更复杂的报表功能，如数据趋势分析、图表展示、自定义报表等。