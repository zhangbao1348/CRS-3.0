package com.crs.service;

import com.crs.entity.MarketCode;
import com.crs.repository.MarketCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据初始化服务
 * 在应用启动时初始化默认数据
 */
@Service
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MarketCodeRepository marketCodeRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("开始初始化数据...");
        
        // 初始化市场码数据
        initializeMarketCodes();
        
        System.out.println("数据初始化完成！");
    }

    /**
     * 初始化市场码数据
     */
    private void initializeMarketCodes() {
        try {
            // 检查是否已有市场码数据
            List<MarketCode> existingMarketCodes = marketCodeRepository.findAll();
            if (existingMarketCodes.isEmpty()) {
                System.out.println("数据库中不存在市场码数据，开始初始化...");
                
                // 创建线上市场
                MarketCode onlineMarket = new MarketCode();
                onlineMarket.setCode("ONLINE");
                onlineMarket.setName("线上市场");
                onlineMarket.setDescription("线上销售渠道");
                onlineMarket.setParentId(null);
                onlineMarket.setLevel(1);
                onlineMarket.setStatus(MarketCode.Status.active);
                marketCodeRepository.save(onlineMarket);
                
                System.out.println("市场码数据初始化完成");
            } else {
                System.out.println("数据库中已存在市场码数据，跳过初始化");
            }
        } catch (Exception e) {
            System.err.println("初始化市场码数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建市场码
     */
    private MarketCode createMarketCode(String code, String name, String description, Integer parentId, int level) {
        MarketCode marketCode = new MarketCode();
        marketCode.setCode(code);
        marketCode.setName(name);
        marketCode.setDescription(description);
        marketCode.setParentId(parentId);
        marketCode.setLevel(level);
        marketCode.setStatus(MarketCode.Status.active);
        return marketCodeRepository.save(marketCode);
    }
}