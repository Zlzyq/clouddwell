if tonumber(redis.call("GET", KEYS[1])) >= tonumber(ARGV[1]) then
    redis.call("DECRBY", KEYS[1], ARGV[1])
    return 1
end
return 0