-- KEYS[1] = 秒杀库存 key     → seckill:stock:1
-- KEYS[2] = 已购买用户 set    → seckill:bought:1
-- ARGV[1] = 用户ID
-- ARGV[2] = 购买数量

-- ① 判断这个人买过没
local bought = redis.call("SISMEMBER", KEYS[2], ARGV[1])
if bought == 1 then
    return 0  -- 买过了，不给买
end

-- ② 扣库存
local stock = redis.call("GET", KEYS[1])
if not stock or tonumber(stock) < tonumber(ARGV[2]) then
    return 1  -- 库存不足
end

redis.call("DECRBY", KEYS[1], ARGV[2])
redis.call("SADD", KEYS[2], ARGV[1])

return 2  -- 购买成功！