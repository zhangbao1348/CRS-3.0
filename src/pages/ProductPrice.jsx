import React from 'react';
import { Card, Tabs, Typography } from 'antd';

const { Title, Text } = Typography;
const { TabPane } = Tabs;

/**
 * 基础价格设置页面
 * 对应左侧菜单： 价格设置 -> 基础价格设置
 */
class ProductPrice extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      activeTab: '1',
      hotelCode: 'SHBLJ001',
      roomTypeDiffs: {
        '1': {
          diffName: '默认差价',
          diffItems: [],
        },
        '2': {
          diffName: '旺季差价',
          diffItems: [],
        },
        '3': {
          diffName: '淡季差价',
          diffItems: [],
        },
      },
    };
  }

  render() {
    return (
      <Card
        title={
          <Title level={4}>
            基础价格设置
          </Title>
        }
      >
        {/* 这里将实现基础价格设置的逻辑 */}
      </Card>
    );
  }
}

export default ProductPrice;