-- 更新房价码衍生级别示例数据
-- 日期：2026-04-11

USE CRS;

-- 更新一些房价码的衍生级别作为示例
UPDATE group_rate_codes SET derivative_level = 'basic' WHERE rate_code IN ('RACK1', 'WEEKEND', 'CORP', 'MEMBER', 'GROUP');
UPDATE group_rate_codes SET derivative_level = 'level1' WHERE rate_code IN ('PROMO', 'LONG_STAY', 'GOV', 'MILITARY', 'STUDENT');
UPDATE group_rate_codes SET derivative_level = 'level2' WHERE rate_code IN ('PEAK', 'OFF_PEAK', 'HOLIDAY', 'BIRTHDAY', 'COUPLE');

-- 查看更新结果
SELECT 
    id,
    rate_code AS '房价码',
    rate_name AS '房价码名称',
    derivative_level AS '衍生级别'
FROM group_rate_codes
WHERE id <= 15
ORDER BY id;

SELECT '房价码衍生级别更新完成！' AS message;
