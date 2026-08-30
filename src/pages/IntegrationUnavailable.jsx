import { Alert, Button, Card, Result, Tag } from 'antd'
import { ApiOutlined, ArrowLeftOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'

/** 对尚未接入真实厂商接口的能力统一 fail-closed，避免把演示数据误认为生产结果。 */
const IntegrationUnavailable = ({ title = '外部能力尚未接入', system = '渠道厂商' }) => {
  const navigate = useNavigate()
  return (
    <div className="fade-in" style={{ maxWidth: 920, margin: '32px auto' }}>
      <Card>
        <Result
          icon={<ApiOutlined style={{ color: '#1677ff' }} />}
          status="info"
          title={title}
          subTitle={`当前环境尚未配置 ${system} 的正式接口、凭证和回调验签信息，因此不会展示模拟数据或返回伪成功。`}
          extra={<Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>返回上一页</Button>}
        />
        <Alert
          showIcon
          type="warning"
          message={<span><Tag color="gold">安全关闭</Tag> 接入完成前所有写操作保持禁用</span>}
          description="启用条件：厂商测试环境、接口协议、签名密钥、幂等键规则和失败补偿流程全部通过联调验收。"
        />
      </Card>
    </div>
  )
}

export default IntegrationUnavailable
