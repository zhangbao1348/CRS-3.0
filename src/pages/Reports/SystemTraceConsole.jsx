import { useState, useEffect } from 'react'
import { Input, Button, Table, Card, Timeline, Tag, Collapse, Modal, Space, Alert, Empty, message, Drawer, Spin, Tooltip } from 'antd'
import { SearchOutlined, BugOutlined, CheckCircleOutlined, CloseCircleOutlined, ExclamationCircleOutlined, NodeIndexOutlined, FileTextOutlined, CopyOutlined, ReloadOutlined } from '@ant-design/icons'
import { traceApi } from '../../utils/api'
import dayjs from 'dayjs'

const { Panel } = Collapse

// 递归 JSON 树状呈现节点组件
const JsonTreeNode = ({ name, value, level = 0, isLast = true }) => {
  const [collapsed, setCollapsed] = useState(level > 1) // 超过第 1 层默认折叠，保持页面整洁

  const renderValue = (val) => {
    if (val === null) return <span style={{ color: '#bfbfbf', fontStyle: 'italic' }}>null</span>
    if (val === undefined) return <span style={{ color: '#bfbfbf', fontStyle: 'italic' }}>undefined</span>
    if (typeof val === 'boolean') {
      return <span style={{ color: '#722ed1', fontWeight: 'bold' }}>{val ? 'true' : 'false'}</span>
    }
    if (typeof val === 'number') {
      return <span style={{ color: '#13c2c2', fontFamily: 'Consolas, Courier New, monospace' }}>{val}</span>
    }
    if (typeof val === 'string') {
      return <span style={{ color: '#389e0d', wordBreak: 'break-all' }}>{`"${val}"`}</span>
    }
    return null
  }

  const isObject = value !== null && typeof value === 'object'
  const isArray = Array.isArray(value)

  if (!isObject) {
    return (
      <div style={{ paddingLeft: 12, margin: '2px 0', fontFamily: 'Consolas, Courier New, monospace', fontSize: '12px', lineHeight: '1.6' }}>
        {name && <span style={{ color: '#d4380d', marginRight: 4 }}>{name}:</span>}
        {renderValue(value)}
        {!isLast && <span style={{ color: '#8c8c8c' }}>,</span>}
      </div>
    )
  }

  const keys = isArray ? value : Object.keys(value)
  const openBracket = isArray ? '[' : '{'
  const closeBracket = isArray ? ']' : '}'

  if (keys.length === 0) {
    return (
      <div style={{ paddingLeft: 12, margin: '2px 0', fontFamily: 'Consolas, Courier New, monospace', fontSize: '12px', lineHeight: '1.6' }}>
        {name && <span style={{ color: '#d4380d', marginRight: 4 }}>{name}:</span>}
        <span>{openBracket}{closeBracket}</span>
        {!isLast && <span style={{ color: '#8c8c8c' }}>,</span>}
      </div>
    )
  }

  return (
    <div style={{ paddingLeft: 12, margin: '2px 0', fontFamily: 'Consolas, Courier New, monospace', fontSize: '12px', lineHeight: '1.6' }}>
      <div 
        style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', userSelect: 'none' }}
        onClick={() => setCollapsed(!collapsed)}
      >
        <span style={{ color: '#bfbfbf', fontSize: '9px', marginRight: 6, width: '10px', display: 'inline-block' }}>
          {collapsed ? '▶' : '▼'}
        </span>
        {name && <span style={{ color: '#d4380d', marginRight: 4 }}>{name}:</span>}
        <span style={{ color: '#8c8c8c' }}>{openBracket}</span>
        {collapsed && (
          <span style={{ color: '#1890ff', fontSize: '11px', margin: '0 4px', backgroundColor: '#e6f7ff', padding: '0 4px', borderRadius: 2 }}>
            {isArray ? `${value.length} items` : `${Object.keys(value).length} keys`}
          </span>
        )}
        {collapsed && <span style={{ color: '#8c8c8c' }}>{closeBracket}</span>}
      </div>
      
      {!collapsed && (
        <div style={{ borderLeft: '1px dashed #d9d9d9', marginLeft: 20, paddingLeft: 4 }}>
          {isArray ? (
            value.map((item, index) => (
              <JsonTreeNode 
                key={index} 
                name={null} 
                value={item} 
                level={level + 1} 
                isLast={index === value.length - 1} 
              />
            ))
          ) : (
            Object.keys(value).map((key, index, arr) => (
              <JsonTreeNode 
                key={key} 
                name={key} 
                value={value[key]} 
                level={level + 1} 
                isLast={index === arr.length - 1} 
              />
            ))
          )}
        </div>
      )}
      
      {!collapsed && (
        <div style={{ paddingLeft: 12 }}>
          <span style={{ color: '#8c8c8c' }}>{closeBracket}</span>
          {!isLast && <span style={{ color: '#8c8c8c' }}>,</span>}
        </div>
      )}
    </div>
  )
}

const SystemTraceConsole = () => {
  const [loading, setLoading] = useState(false)
  const [searchVal, setSearchVal] = useState('')
  const [searchType, setSearchType] = useState('traceId') // 'traceId' or 'referenceCode'
  const [logs, setLogs] = useState([])
  const [activeLog, setActiveLog] = useState(null)
  const [detailDrawerVisible, setDetailDrawerVisible] = useState(false)
  const [detailLogs, setDetailLogs] = useState([])
  const [detailLoading, setDetailLoading] = useState(false)
  const [aiModalVisible, setAiModalVisible] = useState(false)
  const [aiAnalysisResult, setAiAnalysisResult] = useState('')

  // 默认加载最近日志
  useEffect(() => {
    fetchLogs()
  }, [])

  const fetchLogs = async (value = '', type = 'traceId') => {
    setLoading(true)
    try {
      const params = {}
      if (value) {
        if (type === 'traceId') params.traceId = value
        else params.referenceCode = value
      }
      const data = await traceApi.getTraceLogs(params)
      setLogs(data || [])
    } catch (err) {
      message.error('加载系统追踪日志失败: ' + (err.message || err))
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = () => {
    let detectedType = 'traceId'
    if (/^[R]\d+/.test(searchVal.trim())) {
      detectedType = 'referenceCode'
    }
    setSearchType(detectedType)
    fetchLogs(searchVal.trim(), detectedType)
  }

  const getStatusTag = (status) => {
    switch (status) {
      case 'SUCCESS':
        return <Tag color="success" icon={<CheckCircleOutlined />}>SUCCESS</Tag>
      case 'BLOCKED_BY_RULE':
        return <Tag color="warning" icon={<ExclamationCircleOutlined />}>BLOCKED</Tag>
      case 'ERROR':
        return <Tag color="error" icon={<CloseCircleOutlined />}>ERROR</Tag>
      default:
        return <Tag color="default">{status}</Tag>
    }
  }

  const getSourceTag = (source) => {
    switch (source) {
      case 'OPEN_API':
        return <Tag color="purple">OPEN API</Tag>
      case 'INTERNAL_API':
        return <Tag color="cyan">INTERNAL API</Tag>
      case 'SCHEDULED_JOB':
        return <Tag color="blue">SCHEDULED JOB</Tag>
      case 'FRONTEND':
        return <Tag color="orange">FRONTEND</Tag>
      default:
        return <Tag>{source}</Tag>
    }
  }

  const handleViewDetail = async (log) => {
    setActiveLog(log)
    setDetailDrawerVisible(true)
    setDetailLoading(true)
    try {
      const data = await traceApi.getTraceDetail(log.traceId)
      setDetailLogs(data || [])
    } catch (err) {
      message.error('加载级联链路明细失败: ' + (err.message || err))
    } finally {
      setDetailLoading(false)
    }
  }

  const parseSnapshot = (snapshotStr) => {
    if (!snapshotStr) return {}
    try {
      return JSON.parse(snapshotStr)
    } catch (e) {
      return { raw: snapshotStr }
    }
  }

  const runAiDiagnosis = (log) => {
    setActiveLog(log)
    setAiModalVisible(true)
    
    // 构造 AI 诊断所需的 prompt
    const snapshot = parseSnapshot(log.decisionSnapshot)
    const prompt = `【系统全链路 AI 根因预诊断请求】
- TraceId: ${log.traceId}
- 关联单据: ${log.referenceCode || '无'}
- 操作类型: ${log.operationName}
- 触发来源: ${log.sourceType}
- 校验状态: ${log.status}
- 报错定位: ${log.errorClass || '无'}.${log.errorMethod || '无'} (行号: ${log.errorLine || '无'})
- 关联PRD规范: ${log.relatedPrdLink || '无'}

- 决策路径快照 (DecisionSnapshot):
${JSON.stringify(snapshot, null, 2)}

- 异常堆栈 (ErrorStack):
${log.errorStack || '无'}

请根据以上决策快照数据与报错栈，比对关联的 PRD 路径规范，分析为什么会出现此结果（若是价格不匹配，请说明是哪一天触发了什么配置或保底价；若是库存拦截，请指明是哪个维度的配额不足），并给出具体的代码/配置修改建议。`

    setAiAnalysisResult(prompt)
  }

  const copyPrompt = () => {
    navigator.clipboard.writeText(aiAnalysisResult)
    message.success('AI 诊断 Prompt 已成功复制到剪贴板，请发送给您的 AI 助理！')
  }

  const handleCopyText = (text, label) => {
    navigator.clipboard.writeText(text)
    message.success(`${label} 已成功复制！`)
  }

  const columns = [
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 170,
      render: (text) => dayjs(text).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: '来源类型',
      dataIndex: 'sourceType',
      key: 'sourceType',
      width: 130,
      render: (text) => getSourceTag(text)
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (text) => getStatusTag(text)
    },
    {
      title: '操作名称',
      dataIndex: 'operationName',
      key: 'operationName',
      width: 240,
      ellipsis: true
    },
    {
      title: '关联单据',
      dataIndex: 'referenceCode',
      key: 'referenceCode',
      width: 140,
      render: (text) => text ? <span style={{ fontWeight: '500' }}>{text}</span> : '-'
    },
    {
      title: '追踪 ID (TraceId)',
      dataIndex: 'traceId',
      key: 'traceId',
      width: 300,
      render: (text) => (
        <span 
          style={{ fontFamily: 'Consolas, monospace', cursor: 'pointer', color: '#595959' }} 
          onClick={() => handleCopyText(text, 'TraceId')}
        >
          <Tooltip title="点击复制 TraceId">
            {text} <CopyOutlined style={{ fontSize: '11px', color: '#bfbfbf', marginLeft: 4 }} />
          </Tooltip>
        </span>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button size="small" icon={<NodeIndexOutlined />} onClick={() => handleViewDetail(record)}>
            决策链路
          </Button>
          <Button size="small" type="primary" danger={record.status === 'ERROR'} icon={<BugOutlined />} onClick={() => runAiDiagnosis(record)}>
            AI 诊断
          </Button>
        </Space>
      )
    }
  ]

  const renderTimeline = () => {
    if (!detailLogs || detailLogs.length === 0) return <Empty description="暂无该 TraceId 的级联调用信息" />

    return (
      <Timeline mode="left" style={{ marginTop: 16 }}>
        {detailLogs.map((log) => {
          let dotColor = 'blue'
          let borderStyle = '1px solid #f0f0f0'
          if (log.status === 'ERROR') {
            dotColor = 'red'
            borderStyle = '1px solid #ffa39e'
          } else if (log.status === 'BLOCKED_BY_RULE') {
            dotColor = 'orange'
            borderStyle = '1px solid #ffd591'
          } else if (log.status === 'SUCCESS') {
            dotColor = 'green'
            borderStyle = '1px solid #b7eb8f'
          }

          const snapshotData = parseSnapshot(log.decisionSnapshot)

          return (
            <Timeline.Item 
              key={log.id} 
              color={dotColor}
              label={dayjs(log.createdAt).format('HH:mm:ss.SSS')}
            >
              <Card 
                size="small" 
                title={
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>{getSourceTag(log.sourceType)} {log.operationName}</span>
                    {snapshotData.duration && <span style={{ fontSize: '11px', color: '#bfbfbf' }}>{snapshotData.duration}</span>}
                  </div>
                } 
                style={{ marginBottom: 12, border: borderStyle, borderRadius: 6, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.02)' }}
              >
                <div style={{ marginBottom: 8, display: 'flex', gap: 16, fontSize: '12px' }}>
                  <span><strong>状态: </strong> {getStatusTag(log.status)}</span>
                  {log.referenceCode && <span><strong>关联单据: </strong> {log.referenceCode}</span>}
                </div>
                
                {log.errorClass && (
                  <Alert 
                    message={`异常定位: ${log.errorClass}.${log.errorMethod} (Line: ${log.errorLine})`} 
                    type="error" 
                    showIcon 
                    style={{ marginBottom: 8, fontSize: '12px' }}
                  />
                )}
                
                {log.relatedPrdLink && (
                  <div style={{ marginBottom: 8, fontSize: '12px' }}>
                    <FileTextOutlined style={{ marginRight: 4, color: '#8c8c8c' }} />
                    <strong>参考规范: </strong> 
                    <span style={{ color: '#1890ff', fontFamily: 'monospace' }}>{log.relatedPrdLink}</span>
                  </div>
                )}
                
                <Collapse ghost size="small" style={{ backgroundColor: '#fafafa', borderRadius: 4 }}>
                  <Panel header="展开查看业务决策快照 (DecisionSnapshot)" key="1">
                    <div style={{ padding: '4px 0', borderTop: '1px solid #f0f0f0', maxHeight: 350, overflowY: 'auto' }}>
                      <JsonTreeNode value={snapshotData} level={0} isLast={true} />
                    </div>
                  </Panel>
                </Collapse>
              </Card>
            </Timeline.Item>
          )
        })}
      </Timeline>
    )
  }

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <span>全系统 AI-Ready 诊断与决策追踪控制台</span>
      </h1>

      <div style={{ marginBottom: 16, padding: 16, backgroundColor: '#f5f5f5', borderRadius: 8, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Space size="middle">
          <Space.Compact style={{ width: 500 }}>
            <Input
              placeholder="输入 Trace ID 或订单号 (支持R开头的订单编号自动识别)"
              value={searchVal}
              onChange={(e) => setSearchVal(e.target.value)}
              onPressEnter={handleSearch}
            />
            <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch} loading={loading}>搜索</Button>
          </Space.Compact>
          <Button onClick={() => { setSearchVal(''); fetchLogs(); }}>重置</Button>
        </Space>
        
        <Button icon={<ReloadOutlined />} onClick={() => fetchLogs(searchVal.trim(), searchType)}>
          刷新日志
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={logs}
        rowKey="id"
        loading={loading}
        pagination={{ pageSize: 20 }}
        size="middle"
        style={{ backgroundColor: '#fff', borderRadius: 8, overflow: 'hidden', boxShadow: '0 4px 12px rgba(0,0,0,0.03)' }}
      />

      {/* 决策链路时序详情抽屉 (Drawer) */}
      <Drawer
        title={
          <div>
            <span style={{ fontSize: 16, fontWeight: 'bold', color: '#262626' }}>TraceID 决策时序分析</span>
            <div style={{ fontSize: 12, color: '#8c8c8c', marginTop: 4, fontFamily: 'Consolas, monospace' }}>
              TraceId: {activeLog?.traceId}
            </div>
          </div>
        }
        placement="right"
        width={750}
        onClose={() => setDetailDrawerVisible(false)}
        open={detailDrawerVisible}
        extra={
          <Space>
            {activeLog && (
              <Button 
                type="primary" 
                danger={activeLog.status === 'ERROR'} 
                icon={<BugOutlined />} 
                onClick={() => runAiDiagnosis(activeLog)}
              >
                AI 诊断
              </Button>
            )}
          </Space>
        }
      >
        {detailLoading ? (
          <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', flexDirection: 'column', gap: 16 }}>
            <Spin size="large" />
            <span style={{ color: '#8c8c8c' }}>全链路时序数据检索中...</span>
          </div>
        ) : (
          <div style={{ padding: '10px 0' }}>
            {renderTimeline()}
          </div>
        )}
      </Drawer>

      {/* AI 一键诊断数据准备弹窗 */}
      <Modal
        title={
          <span>
            <BugOutlined style={{ color: '#ff4d4f', marginRight: 8 }} />
            AI 智能诊断就绪面板
          </span>
        }
        open={aiModalVisible}
        onCancel={() => setAiModalVisible(false)}
        footer={[
          <Button key="cancel" onClick={() => setAiModalVisible(false)}>取消</Button>,
          <Button key="copy" type="primary" onClick={copyPrompt}>复制 Prompt 并打开 AI 诊断</Button>
        ]}
        width={750}
      >
        <Alert
          message="AI-Ready 诊断提示"
          description="系统已经自动将故障时刻的代码行号、决策数据快照（如比价差异、税额计算过程、可用配额）以及对应的 PRD 路径打包为标准 Prompt。您可以一键复制并发送给您的 AI 助理进行秒级精准定位。"
          type="info"
          showIcon
          style={{ marginBottom: 16 }}
        />
        <Card title="生成的 AI 诊断上下文 (包含 DecisionSnapshot)" size="small">
          <pre style={{ maxHeight: 350, overflowY: 'auto', fontSize: 11, backgroundColor: '#f8f8f8', padding: 8, borderRadius: 4 }}>
            {aiAnalysisResult}
          </pre>
        </Card>
      </Modal>
    </div>
  )
}

export default SystemTraceConsole
