import PropTypes from 'prop-types'
import { Alert, Button, Card, Empty, Space, Spin, Typography } from 'antd'

const { Text, Title } = Typography

/** 统一业务页面的标题、说明、辅助信息和主操作层级。关联模块：所有 CRS 页面。 */
export const PageScaffold = ({ title, description, eyebrow, actions, meta, className = '', children }) => (
  <section className={`ui-page ${className}`.trim()}>
    <header className="ui-page__header">
      <div className="ui-page__heading">
        {eyebrow && <Text className="ui-page__eyebrow">{eyebrow}</Text>}
        <Title level={2} className="ui-page__title">{title}</Title>
        {description && <Text className="ui-page__description">{description}</Text>}
        {meta && <div className="ui-page__meta">{meta}</div>}
      </div>
      {actions && <Space className="ui-page__actions" wrap>{actions}</Space>}
    </header>
    <div className="ui-page__body">{children}</div>
  </section>
)

PageScaffold.propTypes = {
  title: PropTypes.node.isRequired,
  description: PropTypes.node,
  eyebrow: PropTypes.node,
  actions: PropTypes.node,
  meta: PropTypes.node,
  className: PropTypes.string,
  children: PropTypes.node.isRequired,
}

/** 统一查询区，避免筛选条件与页面主操作混在同一行。 */
export const FilterPanel = ({ title = '筛选条件', extra, children, className = '' }) => (
  <Card className={`ui-panel ui-filter-panel ${className}`.trim()} variant="outlined">
    <div className="ui-panel__header">
      <div>
        <Text strong>{title}</Text>
        <Text className="ui-panel__hint">调整条件后执行查询</Text>
      </div>
      {extra && <Space wrap>{extra}</Space>}
    </div>
    <div className="ui-filter-panel__body">{children}</div>
  </Card>
)

FilterPanel.propTypes = {
  title: PropTypes.node,
  extra: PropTypes.node,
  children: PropTypes.node.isRequired,
  className: PropTypes.string,
}

/** 统一列表容器及表格上方工具栏。 */
export const TablePanel = ({ title, description, actions, children, className = '' }) => (
  <Card className={`ui-panel ui-table-panel ${className}`.trim()} variant="outlined">
    {(title || description || actions) && (
      <div className="ui-panel__header ui-table-panel__header">
        <div>
          {title && <Title level={4}>{title}</Title>}
          {description && <Text className="ui-panel__hint">{description}</Text>}
        </div>
        {actions && <Space wrap>{actions}</Space>}
      </div>
    )}
    <div className="ui-table-panel__body">{children}</div>
  </Card>
)

TablePanel.propTypes = {
  title: PropTypes.node,
  description: PropTypes.node,
  actions: PropTypes.node,
  children: PropTypes.node.isRequired,
  className: PropTypes.string,
}

/** 复杂表单的语义分区。 */
export const FormSection = ({ title, description, extra, children, className = '' }) => (
  <section className={`ui-form-section ${className}`.trim()}>
    <div className="ui-form-section__header">
      <div>
        <Title level={4}>{title}</Title>
        {description && <Text className="ui-panel__hint">{description}</Text>}
      </div>
      {extra}
    </div>
    <div className="ui-form-section__body">{children}</div>
  </section>
)

FormSection.propTypes = {
  title: PropTypes.node.isRequired,
  description: PropTypes.node,
  extra: PropTypes.node,
  children: PropTypes.node.isRequired,
  className: PropTypes.string,
}

/** 统一加载、错误与空数据反馈，避免页面静默失败。 */
export const AsyncState = ({ loading, error, empty, onRetry, emptyDescription = '暂无数据', children }) => {
  if (loading) {
    return <div className="ui-async-state"><Spin size="large" /><Text>正在加载业务数据</Text></div>
  }
  if (error) {
    return (
      <Alert
        type="error"
        showIcon
        message="数据加载失败"
        description={String(error)}
        action={onRetry ? <Button onClick={onRetry}>重新加载</Button> : null}
      />
    )
  }
  if (empty) {
    return <div className="ui-async-state"><Empty description={emptyDescription} /></div>
  }
  return children
}

AsyncState.propTypes = {
  loading: PropTypes.bool,
  error: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
  empty: PropTypes.bool,
  onRetry: PropTypes.func,
  emptyDescription: PropTypes.node,
  children: PropTypes.node.isRequired,
}
