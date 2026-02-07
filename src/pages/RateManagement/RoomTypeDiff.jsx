import React, { useState, useEffect } from 'react'
import { Typography, Table, Checkbox, Input, DatePicker, Button, Modal, Tabs, Space } from 'antd'
import { ApartmentOutlined, PlusOutlined, MinusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'

const { Title } = Typography
const { CheckboxGroup } = Checkbox
const { RangePicker } = DatePicker
const { TabPane } = Tabs

// 模拟房型差价数据
const mockRoomTypeDiff = [
  {
    id: 3,
    code: 'ST',
    name: '标准双床房',
    value: 40,
    startDate: '2026-01-22',
    endDate: '',
    weekdays: ['1', '2', '3', '4', '5', '6', '7'],
    expanded: true
  },
  {
    id: 4,
    code: 'SK',
    name: '高级大床房',
    value: 60,
    startDate: '2026-01-22',
    endDate: '',
    weekdays: ['1', '2', '3', '4', '5', '6', '7'],
    expanded: true
  },
  {
    id: 5,
    code: 'DT',
    name: '豪华双床房',
    value: 80,
    startDate: '2026-01-22',
    endDate: '',
    weekdays: ['1', '2', '3', '4', '5', '6', '7'],
    expanded: true
  },
  {
    id: 6,
    code: 'BS',
    name: '商务套房',
    value: 100,
    startDate: '2026-01-22',
    endDate: '',
    weekdays: ['1', '2', '3', '4', '5', '6', '7'],
    expanded: true
  }
]

const RoomTypeDiff = () => {
  // 状态管理
  const [pricingSystems, setPricingSystems] = useState([
    {
      id: '1',
      name: '标准差价体系',
      diffs: [...mockRoomTypeDiff],
      isEditing: false,
      editingName: '标准差价体系',
      selectedRowKeys: []
    }
  ])
  const [activeSystemId, setActiveSystemId] = useState('1')
  const [weekdaysOptions] = useState([
    { label: '日', value: '7' },
    { label: '一', value: '1' },
    { label: '二', value: '2' },
    { label: '三', value: '3' },
    { label: '四', value: '4' },
    { label: '五', value: '5' },
    { label: '六', value: '6' }
  ])
  
  // 日志状态管理
  const [logs, setLogs] = useState([])
  
  // 模拟当前用户
  const currentUser = '管理员'
  
  // 记录日志函数
  const recordLog = (actionType, details) => {
    const log = {
      id: Date.now(),
      operator: currentUser,
      time: dayjs().format('YYYY-MM-DD HH:mm:ss'),
      type: '房型差价',
      action: actionType,
      details
    }
    setLogs(prev => [log, ...prev]) // 新日志添加到开头
  }
  
  // 保存功能
  const handleSave = () => {
    console.log('保存房型差价数据:', pricingSystems)
    recordLog('保存', '保存了所有房型差价数据')
    alert('保存成功！')
  }

  // 日志功能 - 显示日志表格
  const handleLog = () => {
    // 日志表格列配置
    const logColumns = [
      {
        title: '操作人',
        dataIndex: 'operator',
        key: 'operator',
        width: 100
      },
      {
        title: '操作时间',
        dataIndex: 'time',
        key: 'time',
        width: 180
      },
      {
        title: '操作类型',
        dataIndex: 'type',
        key: 'type',
        width: 100
      },
      {
        title: '动作',
        dataIndex: 'action',
        key: 'action',
        width: 100
      },
      {
        title: '详细内容',
        dataIndex: 'details',
        key: 'details'
      }
    ]
    
    // 使用Modal显示日志表格
    Modal.info({
      title: '操作日志',
      content: (
        <div style={{ maxHeight: '400px', overflow: 'auto' }}>
          <Table
            columns={logColumns}
            dataSource={logs}
            rowKey="id"
            pagination={false}
            size="small"
          />
        </div>
      ),
      width: 800
    })
  }

  // 行选择配置
  const getRowSelection = (systemId) => {
    const currentSystem = pricingSystems.find(system => system.id === systemId)
    return {
      selectedRowKeys: currentSystem?.selectedRowKeys || [],
      onChange: (newSelectedRowKeys) => {
        setPricingSystems(pricingSystems.map(system => {
          if (system.id === systemId) {
            return { ...system, selectedRowKeys: newSelectedRowKeys }
          }
          return system
        }))
      }
    }
  }

  // 添加新的差价体系
  const handleAddSystem = () => {
    const newId = String(pricingSystems.length + 1)
    const newSystem = {
      id: newId,
      name: `差价体系${newId}`,
      diffs: [...mockRoomTypeDiff],
      isEditing: false,
      editingName: `差价体系${newId}`
    }
    setPricingSystems([...pricingSystems, newSystem])
    setActiveSystemId(newId)
    recordLog('新增差价体系', `新增了差价体系：${newSystem.name}`)
  }

  // 删除差价体系
  const handleDeleteSystem = (systemId) => {
    if (pricingSystems.length <= 1) {
      alert('至少需要保留一个差价体系！')
      return
    }
    
    const systemToDelete = pricingSystems.find(system => system.id === systemId)
    if (systemToDelete) {
      setPricingSystems(pricingSystems.filter(system => system.id !== systemId))
      
      // 如果删除的是当前激活的差价体系，切换到第一个差价体系
      if (systemId === activeSystemId) {
        setActiveSystemId(pricingSystems[0].id)
      }
      
      recordLog('删除差价体系', `删除了差价体系：${systemToDelete.name}`)
    }
  }

  // 开始编辑差价体系名称
  const handleStartEdit = (systemId) => {
    setPricingSystems(pricingSystems.map(system => {
      if (system.id === systemId) {
        return { 
          ...system, 
          isEditing: true,
          editingName: system.name // 初始化编辑名称为当前名称
        }
      }
      return system
    }))
  }

  // 取消编辑差价体系名称
  const handleCancelEdit = (systemId) => {
    setPricingSystems(pricingSystems.map(system => {
      if (system.id === systemId) {
        return { 
          ...system, 
          isEditing: false,
          editingName: system.name // 恢复编辑名称为当前名称
        }
      }
      return system
    }))
  }

  // 保存差价体系名称
  const handleSaveName = (systemId) => {
    setPricingSystems(pricingSystems.map(system => {
      if (system.id === systemId) {
        const newName = system.editingName.trim()
        if (newName && newName !== system.name) {
          const oldName = system.name
          recordLog('修改差价体系名称', `差价体系名称：${oldName} → ${newName}`)
          return { 
            ...system, 
            name: newName,
            isEditing: false
          }
        }
        // 如果名称没有变化，直接取消编辑
        return { 
          ...system, 
          isEditing: false,
          editingName: system.name
        }
      }
      return system
    }))
  }

  // 更新编辑中的差价体系名称
  const handleUpdateEditingName = (systemId, newName) => {
    setPricingSystems(pricingSystems.map(system => {
      if (system.id === systemId) {
        return { ...system, editingName: newName }
      }
      return system
    }))
  }

  // 更新差价数据
  const updateDiffData = (systemId, diffId, field, value) => {
    setPricingSystems(pricingSystems.map(system => {
      if (system.id === systemId) {
        return {
          ...system,
          diffs: system.diffs.map(diff => {
            if (diff.id === diffId) {
              return { ...diff, [field]: value }
            }
            return diff
          })
        }
      }
      return system
    }))
  }

  // 列配置
  const columns = [
    {
      title: '',
      dataIndex: 'expanded',
      key: 'expanded',
      width: 40,
      render: (expanded, record) => {
        // 检查是否为原始数据行，原始行显示+号用于添加，新增行显示-号用于删除
        const isOriginalRow = record.id <= 6
        
        return (
          <Button
            type="text"
            icon={isOriginalRow ? <PlusOutlined /> : <MinusOutlined />}
            onClick={() => {
              if (isOriginalRow) {
                // 生成新的唯一ID
                const currentSystem = pricingSystems.find(system => system.id === activeSystemId)
                const maxId = Math.max(...currentSystem.diffs.map(item => item.id))
                const newId = maxId + 1
                
                // 创建新行数据，基于当前记录复制
                const newRow = {
                  ...record,
                  id: newId,
                  expanded: true
                }
                
                // 获取当前记录的索引
                const currentIndex = currentSystem.diffs.findIndex(item => item.id === record.id)
                
                // 在当前记录后插入新行
                const updatedSystems = pricingSystems.map(system => {
                  if (system.id === activeSystemId) {
                    const newDiffs = [...system.diffs]
                    newDiffs.splice(currentIndex + 1, 0, newRow)
                    return { ...system, diffs: newDiffs }
                  }
                  return system
                })
                
                setPricingSystems(updatedSystems)
                
                // 记录日志
                recordLog('新增', `新增房型差价：${record.code} - ${record.name}，值：${record.value}`)
              } else {
                // 删除当前行
                const updatedSystems = pricingSystems.map(system => {
                  if (system.id === activeSystemId) {
                    return {
                      ...system,
                      diffs: system.diffs.filter(item => item.id !== record.id)
                    }
                  }
                  return system
                })
                
                setPricingSystems(updatedSystems)
                
                // 记录日志
                recordLog('删除', `删除房型差价：${record.code} - ${record.name}，值：${record.value}`)
              }
            }}
          />
        )
      }
    },
    {
      title: '代码',
      dataIndex: 'code',
      key: 'code',
      width: 100
    },
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width: 120
    },
    {
      title: '值',
      dataIndex: 'value',
      key: 'value',
      width: 100,
      render: (value, record) => (
        <Input
          type="number"
          value={value}
          onChange={(e) => {
            const oldValue = record.value
            const newValue = e.target.value ? Number(e.target.value) : 0
            updateDiffData(activeSystemId, record.id, 'value', newValue)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，值：${oldValue} → ${newValue}`)
          }}
        />
      )
    },
    {
      title: '起始日期',
      dataIndex: 'startDate',
      key: 'startDate',
      width: 150,
      render: (startDate, record) => (
        <DatePicker
          value={startDate ? dayjs(startDate) : null}
          onChange={(date) => {
            const oldStartDate = record.startDate
            const newStartDate = date ? date.format('YYYY-MM-DD') : ''
            updateDiffData(activeSystemId, record.id, 'startDate', newStartDate)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，起始日期：${oldStartDate} → ${newStartDate}`)
          }}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: '结束日期',
      dataIndex: 'endDate',
      key: 'endDate',
      width: 150,
      render: (endDate, record) => (
        <DatePicker
          value={endDate ? dayjs(endDate) : null}
          onChange={(date) => {
            const oldEndDate = record.endDate
            const newEndDate = date ? date.format('YYYY-MM-DD') : ''
            updateDiffData(activeSystemId, record.id, 'endDate', newEndDate)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，结束日期：${oldEndDate} → ${newEndDate}`)
          }}
          style={{ width: '100%' }}
        />
      )
    },
    {
      title: '金',
      dataIndex: 'weekdays',
      key: 'weekdays_6_gold',
      width: 40,
      render: (weekdays, record) => (
        <Checkbox
          checked={weekdays.includes('6')}
          onChange={(e) => {
            let newWeekdays = [...weekdays]
            const oldWeekdays = [...weekdays]
            if (e.target.checked) {
              newWeekdays.push('6')
            } else {
              newWeekdays = newWeekdays.filter(day => day !== '6')
            }
            updateDiffData(activeSystemId, record.id, 'weekdays', newWeekdays)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，适用星期：${oldWeekdays} → ${newWeekdays}`)
          }}
        />
      )
    },
    {
      title: '日',
      dataIndex: 'weekdays',
      key: 'weekdays_7',
      width: 40,
      render: (weekdays, record) => (
        <Checkbox
          checked={weekdays.includes('7')}
          onChange={(e) => {
            let newWeekdays = [...weekdays]
            const oldWeekdays = [...weekdays]
            if (e.target.checked) {
              newWeekdays.push('7')
            } else {
              newWeekdays = newWeekdays.filter(day => day !== '7')
            }
            updateDiffData(activeSystemId, record.id, 'weekdays', newWeekdays)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，适用星期：${oldWeekdays} → ${newWeekdays}`)
          }}
        />
      )
    },
    {
      title: '一',
      dataIndex: 'weekdays',
      key: 'weekdays_1',
      width: 40,
      render: (weekdays, record) => (
        <Checkbox
          checked={weekdays.includes('1')}
          onChange={(e) => {
            let newWeekdays = [...weekdays]
            const oldWeekdays = [...weekdays]
            if (e.target.checked) {
              newWeekdays.push('1')
            } else {
              newWeekdays = newWeekdays.filter(day => day !== '1')
            }
            updateDiffData(activeSystemId, record.id, 'weekdays', newWeekdays)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，适用星期：${oldWeekdays} → ${newWeekdays}`)
          }}
        />
      )
    },
    {
      title: '二',
      dataIndex: 'weekdays',
      key: 'weekdays_2',
      width: 40,
      render: (weekdays, record) => (
        <Checkbox
          checked={weekdays.includes('2')}
          onChange={(e) => {
            let newWeekdays = [...weekdays]
            const oldWeekdays = [...weekdays]
            if (e.target.checked) {
              newWeekdays.push('2')
            } else {
              newWeekdays = newWeekdays.filter(day => day !== '2')
            }
            updateDiffData(activeSystemId, record.id, 'weekdays', newWeekdays)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，适用星期：${oldWeekdays} → ${newWeekdays}`)
          }}
        />
      )
    },
    {
      title: '三',
      dataIndex: 'weekdays',
      key: 'weekdays_3',
      width: 40,
      render: (weekdays, record) => (
        <Checkbox
          checked={weekdays.includes('3')}
          onChange={(e) => {
            let newWeekdays = [...weekdays]
            const oldWeekdays = [...weekdays]
            if (e.target.checked) {
              newWeekdays.push('3')
            } else {
              newWeekdays = newWeekdays.filter(day => day !== '3')
            }
            updateDiffData(activeSystemId, record.id, 'weekdays', newWeekdays)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，适用星期：${oldWeekdays} → ${newWeekdays}`)
          }}
        />
      )
    },
    {
      title: '四',
      dataIndex: 'weekdays',
      key: 'weekdays_4',
      width: 40,
      render: (weekdays, record) => (
        <Checkbox
          checked={weekdays.includes('4')}
          onChange={(e) => {
            let newWeekdays = [...weekdays]
            const oldWeekdays = [...weekdays]
            if (e.target.checked) {
              newWeekdays.push('4')
            } else {
              newWeekdays = newWeekdays.filter(day => day !== '4')
            }
            updateDiffData(activeSystemId, record.id, 'weekdays', newWeekdays)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，适用星期：${oldWeekdays} → ${newWeekdays}`)
          }}
        />
      )
    },
    {
      title: '五',
      dataIndex: 'weekdays',
      key: 'weekdays_5',
      width: 40,
      render: (weekdays, record) => (
        <Checkbox
          checked={weekdays.includes('5')}
          onChange={(e) => {
            let newWeekdays = [...weekdays]
            const oldWeekdays = [...weekdays]
            if (e.target.checked) {
              newWeekdays.push('5')
            } else {
              newWeekdays = newWeekdays.filter(day => day !== '5')
            }
            updateDiffData(activeSystemId, record.id, 'weekdays', newWeekdays)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，适用星期：${oldWeekdays} → ${newWeekdays}`)
          }}
        />
      )
    },
    {
      title: '六',
      dataIndex: 'weekdays',
      key: 'weekdays_6',
      width: 40,
      render: (weekdays, record) => (
        <Checkbox
          checked={weekdays.includes('6')}
          onChange={(e) => {
            let newWeekdays = [...weekdays]
            const oldWeekdays = [...weekdays]
            if (e.target.checked) {
              newWeekdays.push('6')
            } else {
              newWeekdays = newWeekdays.filter(day => day !== '6')
            }
            updateDiffData(activeSystemId, record.id, 'weekdays', newWeekdays)
            
            // 记录日志
            recordLog('修改', `修改房型差价：${record.code} - ${record.name}，适用星期：${oldWeekdays} → ${newWeekdays}`)
          }}
        />
      )
    }
  ]

  return (
    <div className="fade-in">
      <h1 className="page-title">
        <ApartmentOutlined />
        房型差价设置
      </h1>
      <div style={{ padding: '20px 0', display: 'flex', gap: '10px', marginBottom: '16px' }}>
        <Button type="primary" onClick={handleSave}>
          保存
        </Button>
        <Button onClick={handleLog}>
          日志
        </Button>
        <Button type="dashed" onClick={handleAddSystem} icon={<PlusOutlined />}>
          添加差价体系
        </Button>
      </div>
      
      <Tabs
        activeKey={activeSystemId}
        onChange={setActiveSystemId}
        type="editable-card"
        onEdit={(targetKey, action) => {
          if (action === 'remove') {
            handleDeleteSystem(targetKey)
          }
        }}
      >
        {pricingSystems.map(system => (
          <TabPane 
            key={system.id} 
            closable={pricingSystems.length > 1}
            tab={
              <div style={{ display: 'flex', alignItems: 'center' }}>
                {system.isEditing ? (
                  <input
                    type="text"
                    value={system.editingName}
                    onChange={(e) => handleUpdateEditingName(system.id, e.target.value)}
                    onBlur={() => handleSaveName(system.id)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        handleSaveName(system.id)
                      } else if (e.key === 'Escape') {
                        handleCancelEdit(system.id)
                      }
                    }}
                    style={{
                      border: '1px solid #1890ff',
                      padding: '4px 8px',
                      borderRadius: '4px',
                      outline: 'none',
                      fontSize: '14px',
                      cursor: 'text',
                      width: '150px'
                    }}
                    autoFocus
                  />
                ) : (
                  <div
                    onDoubleClick={() => handleStartEdit(system.id)}
                    style={{
                      padding: '4px 8px',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontSize: '14px',
                      width: '150px'
                    }}
                    onMouseEnter={(e) => e.target.style.backgroundColor = '#f0f0f0'}
                    onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                  >
                    {system.name}
                  </div>
                )}
              </div>
            }
          >
            <Table
              rowSelection={{ ...getRowSelection(system.id), type: 'checkbox' }}
              columns={columns}
              dataSource={system.diffs}
              rowKey="id"
              scroll={{ x: 1200 }}
              pagination={false}
            />
          </TabPane>
        ))}
      </Tabs>
    </div>
  )
}

export default RoomTypeDiff