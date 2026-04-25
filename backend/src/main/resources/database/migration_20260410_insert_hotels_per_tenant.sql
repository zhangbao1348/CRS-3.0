-- 为每个租户插入20个模拟酒店数据

USE CRS;

-- 清空现有酒店数据
-- TRUNCATE TABLE hotels;

-- 为租户1 - 锦江酒店集团（20家）
INSERT INTO hotels (hotel_code, tenant_id, chinese_name, english_name, star_rating, province, city, address, longitude, latitude, phone, email, introduction, total_rooms, status, created_at, updated_at) VALUES
('JJSH001', 1, '上海锦江饭店', 'Jinjiang Hotel Shanghai', '5', '上海市', '上海市', '上海市黄浦区茂名南路59号', 121.4576, 31.2206, '021-62582582', 'shanghai@jinjiang.com', '上海锦江饭店是一家历史悠久的五星级豪华酒店，坐落于繁华的南京东路商业区，拥有精美的客房和完善的设施。', 450, 'active', NOW(), NOW()),
('JJBJ001', 1, '北京长城饭店', 'Great Wall Hotel Beijing', '5', '北京市', '北京市', '北京市朝阳区东三环北路10号', 116.4700, 39.9200, '010-65905566', 'beijing@jinjiang.com', '北京长城饭店位于CBD核心区域，是商务和休闲旅客的理想选择。', 520, 'active', NOW(), NOW()),
('JJGZ001', 1, '广州白云宾馆', 'Baiyun Hotel Guangzhou', '5', '广东省', '广州市', '广州市越秀区环市东路367号', 113.2800, 23.1300, '020-83333998', 'guangzhou@jinjiang.com', '广州白云宾馆位于繁华的环市东路，交通便利，周边配套设施完善。', 380, 'active', NOW(), NOW()),
('JJSZ001', 1, '深圳东华假日酒店', 'Holiday Inn Donghua Shenzhen', '4', '广东省', '深圳市', '深圳市南山区南海大道2061号', 113.9200, 22.5200, '0755-26688888', 'shenzhen@jinjiang.com', '深圳东华假日酒店位于南山中心区，距离科技园和深圳湾口岸仅需15分钟车程。', 320, 'active', NOW(), NOW()),
('JJHZ001', 1, '杭州黄龙饭店', 'Dragon Hotel Hangzhou', '5', '浙江省', '杭州市', '杭州市西湖区曙光路120号', 120.1300, 30.2600, '0571-87998833', 'hangzhou@jinjiang.com', '杭州黄龙饭店毗邻西湖，环境优雅，是商务会议和休闲度假的绝佳选择。', 400, 'active', NOW(), NOW()),
('JJNJ001', 1, '南京金陵饭店', 'Jinling Hotel Nanjing', '5', '江苏省', '南京市', '南京市鼓楼区汉中路2号', 118.7800, 32.0500, '025-84711888', 'nanjing@jinjiang.com', '南京金陵饭店是南京的标志性建筑，位于市中心，交通便利。', 580, 'active', NOW(), NOW()),
('JJCD001', 1, '成都锦江宾馆', 'Jinjiang Hotel Chengdu', '5', '四川省', '成都市', '成都市锦江区人民南路二段80号', 104.0700, 30.6500, '028-85506666', 'chengdu@jinjiang.com', '成都锦江宾馆位于成都市中心，距离天府广场仅5分钟步行路程。', 420, 'active', NOW(), NOW()),
('JJXA001', 1, '西安索菲特人民大厦', 'Sofitel Xian on Renmin Square', '5', '陕西省', '西安市', '西安市新城区东新街319号', 108.9500, 34.2600, '029-87928888', 'xian@jinjiang.com', '西安索菲特人民大厦位于西安市中心，毗邻钟鼓楼和回民街。', 390, 'active', NOW(), NOW()),
('JJQD001', 1, '青岛海景花园大酒店', 'Qingdao Seaview Garden Hotel', '5', '山东省', '青岛市', '青岛市市南区彰化路2号', 120.3900, 36.0600, '0532-85875777', 'qingdao@jinjiang.com', '青岛海景花园大酒店位于黄海之滨，拥有无敌海景和完善的度假设施。', 450, 'active', NOW(), NOW()),
('JJDL001', 1, '大连富丽华大酒店', 'Furama Hotel Dalian', '5', '辽宁省', '大连市', '大连市中山区人民路60号', 121.6300, 38.9200, '0411-82630888', 'dalian@jinjiang.com', '大连富丽华大酒店位于大连市中心商务区，是商务和休闲的理想选择。', 400, 'active', NOW(), NOW()),
('JJWH001', 1, '武汉锦江国际大酒店', 'Jinjiang International Hotel Wuhan', '5', '湖北省', '武汉市', '武汉市江汉区建设大道933号', 114.2900, 30.6500, '027-85786666', 'wuhan@jinjiang.com', '武汉锦江国际大酒店位于汉口金融区，交通便利，是商务出行的理想选择。', 480, 'active', NOW(), NOW()),
('JJTJ001', 1, '天津锦江大酒店', 'Jinjiang Hotel Tianjin', '4', '天津市', '天津市', '天津市和平区和平路178号', 117.2100, 39.0900, '022-27116688', 'tianjin@jinjiang.com', '天津锦江大酒店位于市中心，毗邻劝业场，购物便利。', 350, 'active', NOW(), NOW()),
('JJCS001', 1, '长沙锦江大酒店', 'Jinjiang Hotel Changsha', '4', '湖南省', '长沙市', '长沙市芙蓉区五一大道838号', 112.9800, 28.2000, '0731-84445555', 'changsha@jinjiang.com', '长沙锦江大酒店位于五一广场附近，交通便利，周边商业繁华。', 320, 'active', NOW(), NOW()),
('JJKM001', 1, '昆明锦江大酒店', 'Jinjiang Hotel Kunming', '4', '云南省', '昆明市', '昆明市五华区东风西路99号', 102.7100, 25.0400, '0871-63646666', 'kunming@jinjiang.com', '昆明锦江大酒店位于市中心，距离翠湖公园仅10分钟步行路程。', 280, 'active', NOW(), NOW()),
('JJXMN001', 1, '厦门锦江大酒店', 'Jinjiang Hotel Xiamen', '4', '福建省', '厦门市', '厦门市思明区湖滨北路97号', 118.0800, 24.4700, '0592-5088888', 'xiamen@jinjiang.com', '厦门锦江大酒店位于筼筜湖畔，环境优美，是商务和休闲的理想选择。', 300, 'active', NOW(), NOW()),
('JJHZ002', 1, '杭州西湖皇冠假日酒店', 'Crowne Plaza West Lake Hangzhou', '5', '浙江省', '杭州市', '杭州市西湖区宝石一路6号', 120.1400, 30.2500, '0571-88886666', 'hangzhou2@jinjiang.com', '杭州西湖皇冠假日酒店毗邻西湖，拥有绝佳的湖景视野。', 360, 'active', NOW(), NOW()),
('JJSZ002', 1, '深圳锦江大酒店', 'Jinjiang Hotel Shenzhen', '4', '广东省', '深圳市', '深圳市罗湖区嘉宾路2002号', 114.1200, 22.5400, '0755-82282222', 'shenzhen2@jinjiang.com', '深圳锦江大酒店位于罗湖商业中心，距离火车站仅5分钟车程。', 290, 'active', NOW(), NOW()),
('JJBJ002', 1, '北京锦江大酒店', 'Jinjiang Hotel Beijing', '4', '北京市', '北京市', '北京市西城区宣武门西大街129号', 116.3600, 39.8900, '010-63016688', 'beijing2@jinjiang.com', '北京锦江大酒店位于西城区，交通便利，周边景点众多。', 320, 'active', NOW(), NOW()),
('JJGZ002', 1, '广州锦江大酒店', 'Jinjiang Hotel Guangzhou', '4', '广东省', '广州市', '广州市天河区天河路45号', 113.3300, 23.1300, '020-83356688', 'guangzhou2@jinjiang.com', '广州锦江大酒店位于天河商圈，购物和餐饮便利。', 340, 'active', NOW(), NOW()),
('JJNJ002', 1, '南京锦江大酒店', 'Jinjiang Hotel Nanjing', '4', '江苏省', '南京市', '南京市建邺区江东中路233号', 118.7700, 32.0300, '025-86886688', 'nanjing2@jinjiang.com', '南京锦江大酒店位于河西新城，距离奥体中心仅10分钟车程。', 310, 'active', NOW(), NOW());

-- 为租户2 - 华住酒店集团（20家）
INSERT INTO hotels (hotel_code, tenant_id, chinese_name, english_name, star_rating, province, city, address, longitude, latitude, phone, email, introduction, total_rooms, status, created_at, updated_at) VALUES
('HZSH001', 2, '上海全季酒店', 'JI Hotel Shanghai', '4', '上海市', '上海市', '上海市静安区南京西路1266号', 121.4400, 31.2300, '021-62488888', 'shanghai@huazhu.com', '上海全季酒店位于静安寺商圈，交通便利，是商务出行的理想选择。', 220, 'active', NOW(), NOW()),
('HZBJ001', 2, '北京汉庭酒店', 'Hanting Hotel Beijing', '3', '北京市', '北京市', '北京市朝阳区建国路88号', 116.4600, 39.9000, '010-65668888', 'beijing@huazhu.com', '北京汉庭酒店位于CBD核心区，性价比高，适合商务和休闲旅客。', 180, 'active', NOW(), NOW()),
('HZGZ001', 2, '广州桔子水晶酒店', 'Crystal Orange Hotel Guangzhou', '4', '广东省', '广州市', '广州市天河区天河路385号', 113.3300, 23.1300, '020-38888888', 'guangzhou@huazhu.com', '广州桔子水晶酒店位于天河路商圈，设计时尚，设施完善。', 200, 'active', NOW(), NOW()),
('HZSZ001', 2, '深圳亚朵酒店', 'Atour Hotel Shenzhen', '4', '广东省', '深圳市', '深圳市福田区深南大道6011号', 114.0500, 22.5400, '0755-88888888', 'shenzhen@huazhu.com', '深圳亚朵酒店位于福田中心区，注重人文体验，是高品质住宿的选择。', 240, 'active', NOW(), NOW()),
('HZHZ001', 2, '杭州漫心酒店', 'Manxin Hotel Hangzhou', '4', '浙江省', '杭州市', '杭州市上城区解放路108号', 120.1700, 30.2500, '0571-87888888', 'hangzhou@huazhu.com', '杭州漫心酒店位于西湖附近，融合当地文化，提供独特的住宿体验。', 160, 'active', NOW(), NOW()),
('HZNJ001', 2, '南京宜必思酒店', 'Ibis Hotel Nanjing', '3', '江苏省', '南京市', '南京市玄武区中山路18号', 118.7800, 32.0400, '025-84777777', 'nanjing@huazhu.com', '南京宜必思酒店位于市中心，价格实惠，设施齐全。', 150, 'active', NOW(), NOW()),
('HZCD001', 2, '成都美居酒店', 'Mercure Hotel Chengdu', '4', '四川省', '成都市', '成都市武侯区人民南路四段46号', 104.0600, 30.6200, '028-85555555', 'chengdu@huazhu.com', '成都美居酒店位于城南商务区，交通便利，环境舒适。', 190, 'active', NOW(), NOW()),
('HZXA001', 2, '西安桔子酒店', 'Orange Hotel Xian', '3', '陕西省', '西安市', '西安市碑林区南大街32号', 108.9400, 34.2500, '029-87666666', 'xian@huazhu.com', '西安桔子酒店位于市中心，毗邻钟鼓楼，是游览西安的理想起点。', 140, 'active', NOW(), NOW()),
('HZQD001', 2, '青岛星程酒店', 'Starway Hotel Qingdao', '3', '山东省', '青岛市', '青岛市市南区香港中路68号', 120.3700, 36.0700, '0532-85777777', 'qingdao@huazhu.com', '青岛星程酒店位于香港中路，交通便利，周边配套完善。', 170, 'active', NOW(), NOW()),
('HZDL001', 2, '大连海友酒店', 'Hi Inn Dalian', '2', '辽宁省', '大连市', '大连市中山区鲁迅路88号', 121.6400, 38.9300, '0411-82777777', 'dalian@huazhu.com', '大连海友酒店位于市中心，价格实惠，适合预算有限的旅客。', 120, 'active', NOW(), NOW()),
('HZWH001', 2, '武汉汉庭酒店', 'Hanting Hotel Wuhan', '3', '湖北省', '武汉市', '武汉市江汉区江汉路128号', 114.3000, 30.5800, '027-85857777', 'wuhan@huazhu.com', '武汉汉庭酒店位于江汉路步行街，购物和餐饮便利。', 160, 'active', NOW(), NOW()),
('HZTJ001', 2, '天津全季酒店', 'JI Hotel Tianjin', '4', '天津市', '天津市', '天津市河西区友谊路16号', 117.1800, 39.0800, '022-28368888', 'tianjin@huazhu.com', '天津全季酒店位于友谊路金融区，交通便利，环境舒适。', 190, 'active', NOW(), NOW()),
('HZCS001', 2, '长沙桔子酒店', 'Orange Hotel Changsha', '3', '湖南省', '长沙市', '长沙市芙蓉区黄兴中路88号', 112.9900, 28.2100, '0731-84438888', 'changsha@huazhu.com', '长沙桔子酒店位于黄兴步行街，购物和餐饮便利。', 150, 'active', NOW(), NOW()),
('HZKM001', 2, '昆明汉庭酒店', 'Hanting Hotel Kunming', '3', '云南省', '昆明市', '昆明市五华区南屏街123号', 102.7100, 25.0400, '0871-63628888', 'kunming@huazhu.com', '昆明汉庭酒店位于市中心，距离金马碧鸡坊仅5分钟步行路程。', 140, 'active', NOW(), NOW()),
('HZXMN001', 2, '厦门全季酒店', 'JI Hotel Xiamen', '4', '福建省', '厦门市', '厦门市思明区湖滨南路99号', 118.0800, 24.4500, '0592-5108888', 'xiamen@huazhu.com', '厦门全季酒店位于市中心，交通便利，周边商业繁华。', 180, 'active', NOW(), NOW()),
('HZHZ002', 2, '杭州汉庭酒店', 'Hanting Hotel Hangzhou', '3', '浙江省', '杭州市', '杭州市西湖区文三路123号', 120.1300, 30.2700, '0571-88889999', 'hangzhou2@huazhu.com', '杭州汉庭酒店位于文三路科技街，适合商务旅客。', 160, 'active', NOW(), NOW()),
('HZSZ002', 2, '深圳汉庭酒店', 'Hanting Hotel Shenzhen', '3', '广东省', '深圳市', '深圳市罗湖区人民南路123号', 114.1300, 22.5400, '0755-82228888', 'shenzhen2@huazhu.com', '深圳汉庭酒店位于罗湖商业中心，交通便利。', 150, 'active', NOW(), NOW()),
('HZBJ002', 2, '北京全季酒店', 'JI Hotel Beijing', '4', '北京市', '北京市', '北京市海淀区中关村大街123号', 116.3300, 39.9800, '010-62668888', 'beijing2@huazhu.com', '北京全季酒店位于中关村科技园区，适合商务和学术交流。', 200, 'active', NOW(), NOW()),
('HZGZ002', 2, '广州汉庭酒店', 'Hanting Hotel Guangzhou', '3', '广东省', '广州市', '广州市越秀区北京路123号', 113.2600, 23.1300, '020-83337777', 'guangzhou2@huazhu.com', '广州汉庭酒店位于北京路步行街，购物和餐饮便利。', 140, 'active', NOW(), NOW()),
('HZNJ002', 2, '南京全季酒店', 'JI Hotel Nanjing', '4', '江苏省', '南京市', '南京市秦淮区夫子庙123号', 118.7900, 32.0200, '025-86887777', 'nanjing2@huazhu.com', '南京全季酒店位于夫子庙景区，是游览南京的理想选择。', 180, 'active', NOW(), NOW());

-- 为租户3 - 首旅如家酒店集团（20家）
INSERT INTO hotels (hotel_code, tenant_id, chinese_name, english_name, star_rating, province, city, address, longitude, latitude, phone, email, introduction, total_rooms, status, created_at, updated_at) VALUES
('SLSH001', 3, '上海和平饭店', 'Peace Hotel Shanghai', '5', '上海市', '上海市', '上海市黄浦区南京东路20号', 121.4900, 31.2400, '021-63216888', 'shanghai@shoulv.com', '上海和平饭店是上海的标志性建筑，拥有悠久的历史和传奇的故事。', 370, 'active', NOW(), NOW()),
('SLBJ001', 3, '北京建国饭店', 'Jianguo Hotel Beijing', '5', '北京市', '北京市', '北京市朝阳区建国门外大街5号', 116.4500, 39.9100, '010-65002233', 'beijing@shoulv.com', '北京建国饭店位于CBD核心区域，是商务旅客的首选。', 480, 'active', NOW(), NOW()),
('SLGZ001', 3, '广州白天鹅宾馆', 'White Swan Hotel Guangzhou', '5', '广东省', '广州市', '广州市荔湾区沙面南街1号', 113.2400, 23.1100, '020-81886968', 'guangzhou@shoulv.com', '广州白天鹅宾馆坐落于沙面岛，环境优雅，是商务和休闲的理想选择。', 520, 'active', NOW(), NOW()),
('SLSZ001', 3, '深圳威尼斯酒店', 'Venice Hotel Shenzhen', '5', '广东省', '深圳市', '深圳市南山区华侨城深南大道9026号', 113.9700, 22.5400, '0755-26936888', 'shenzhen@shoulv.com', '深圳威尼斯酒店位于华侨城，毗邻世界之窗和欢乐谷，是度假的绝佳选择。', 380, 'active', NOW(), NOW()),
('SLHZ001', 3, '杭州西湖国宾馆', 'West Lake State Guesthouse Hangzhou', '5', '浙江省', '杭州市', '杭州市西湖区杨公堤18号', 120.1400, 30.2400, '0571-87979888', 'hangzhou@shoulv.com', '杭州西湖国宾馆位于西湖西岸，环境清幽，是休闲度假的理想之地。', 260, 'active', NOW(), NOW()),
('SLNJ001', 3, '南京状元楼酒店', 'Zhuangyuanlou Hotel Nanjing', '5', '江苏省', '南京市', '南京市秦淮区建康路106号', 118.7900, 32.0200, '025-86628888', 'nanjing@shoulv.com', '南京状元楼酒店位于秦淮河畔，毗邻夫子庙，是体验南京文化的绝佳选择。', 340, 'active', NOW(), NOW()),
('SLCD001', 3, '成都香格里拉大酒店', 'Shangri-La Hotel Chengdu', '5', '四川省', '成都市', '成都市锦江区滨江东路9号', 104.0800, 30.6400, '028-88889999', 'chengdu@shoulv.com', '成都香格里拉大酒店位于成都市中心，俯瞰锦江，是商务和休闲的理想选择。', 580, 'active', NOW(), NOW()),
('SLXA001', 3, '西安威斯汀大酒店', 'Westin Xian Hotel', '5', '陕西省', '西安市', '西安市雁塔区慈恩路66号', 108.9700, 34.2200, '029-68938888', 'xian@shoulv.com', '西安威斯汀大酒店毗邻大雁塔和大唐不夜城，是商务和文化之旅的理想选择。', 420, 'active', NOW(), NOW()),
('SLQD001', 3, '青岛海尔洲际酒店', 'InterContinental Qingdao', '5', '山东省', '青岛市', '青岛市市南区澳门路98号', 120.3800, 36.0500, '0532-66566666', 'qingdao@shoulv.com', '青岛海尔洲际酒店位于奥帆中心，拥有无敌海景和完善的会议设施。', 450, 'active', NOW(), NOW()),
('SLDL001', 3, '大连棒棰岛宾馆', 'Bangchuidao Hotel Dalian', '5', '辽宁省', '大连市', '大连市中山区迎宾路1号', 121.7000, 38.8700, '0411-82893888', 'dalian@shoulv.com', '大连棒棰岛宾馆位于海滨风景区，环境优美，是度假的理想选择。', 320, 'active', NOW(), NOW()),
('SLWH001', 3, '武汉香格里拉大酒店', 'Shangri-La Hotel Wuhan', '5', '湖北省', '武汉市', '武汉市江岸区建设大道700号', 114.2800, 30.6200, '027-85806888', 'wuhan@shoulv.com', '武汉香格里拉大酒店位于汉口金融区，是商务和休闲的理想选择。', 450, 'active', NOW(), NOW()),
('SLTJ001', 3, '天津丽思卡尔顿酒店', 'The Ritz-Carlton Tianjin', '5', '天津市', '天津市', '天津市和平区大沽北路167号', 117.2100, 39.0900, '022-58095888', 'tianjin@shoulv.com', '天津丽思卡尔顿酒店位于和平区，是奢华住宿的代表。', 380, 'active', NOW(), NOW()),
('SLCS001', 3, '长沙凯悦酒店', 'Hyatt Regency Changsha', '5', '湖南省', '长沙市', '长沙市天心区湘江中路36号', 112.9700, 28.1900, '0731-88888888', 'changsha@shoulv.com', '长沙凯悦酒店位于湘江边，拥有绝佳的江景视野。', 420, 'active', NOW(), NOW()),
('SLKM001', 3, '昆明洲际酒店', 'InterContinental Kunming', '5', '云南省', '昆明市', '昆明市西山区西福路1号', 102.6800, 25.0200, '0871-64006666', 'kunming@shoulv.com', '昆明洲际酒店位于滇池边，环境优美，是休闲度假的理想选择。', 400, 'active', NOW(), NOW()),
('SLXMN001', 3, '厦门鹭江宾馆', 'Lujiang Hotel Xiamen', '4', '福建省', '厦门市', '厦门市思明区鹭江道54号', 118.0800, 24.4500, '0592-2022922', 'xiamen@shoulv.com', '厦门鹭江宾馆位于鼓浪屿对面，拥有绝佳的海景视野。', 280, 'active', NOW(), NOW()),
('SLHZ002', 3, '杭州香格里拉大酒店', 'Shangri-La Hotel Hangzhou', '5', '浙江省', '杭州市', '杭州市西湖区北山路78号', 120.1300, 30.2600, '0571-87977951', 'hangzhou2@shoulv.com', '杭州香格里拉大酒店毗邻西湖，环境优雅，是商务和休闲的理想选择。', 480, 'active', NOW(), NOW()),
('SLSZ002', 3, '深圳福田香格里拉大酒店', 'Shangri-La Hotel Shenzhen', '5', '广东省', '深圳市', '深圳市福田区益田路4088号', 114.0600, 22.5300, '0755-88284088', 'shenzhen2@shoulv.com', '深圳福田香格里拉大酒店位于福田中心区，是商务和休闲的理想选择。', 520, 'active', NOW(), NOW()),
('SLBJ002', 3, '北京丽思卡尔顿酒店', 'The Ritz-Carlton Beijing', '5', '北京市', '北京市', '北京市朝阳区建国路87号', 116.4600, 39.9000, '010-59088888', 'beijing2@shoulv.com', '北京丽思卡尔顿酒店位于CBD核心区，是奢华住宿的代表。', 380, 'active', NOW(), NOW()),
('SLGZ002', 3, '广州四季酒店', 'Four Seasons Hotel Guangzhou', '5', '广东省', '广州市', '广州市天河区珠江西路5号', 113.3200, 23.1200, '020-88833888', 'guangzhou2@shoulv.com', '广州四季酒店位于珠江新城，是奢华住宿的代表。', 340, 'active', NOW(), NOW()),
('SLNJ002', 3, '南京威斯汀大酒店', 'Westin Nanjing Hotel', '5', '江苏省', '南京市', '南京市建邺区庐山路128号', 118.7600, 32.0200, '025-85568888', 'nanjing2@shoulv.com', '南京威斯汀大酒店位于河西新城，是商务和休闲的理想选择。', 420, 'active', NOW(), NOW());

-- 更新租户表中的酒店数量
UPDATE tenants SET hotel_count = 20 WHERE id IN (1, 2, 3);

-- 显示插入结果
SELECT CONCAT('酒店数据初始化完成！共插入 ', COUNT(*), ' 家酒店') AS result FROM hotels;