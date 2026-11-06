# 订单链路压测记录

> 由 `order-stress-test.docx` 转换，原始 Word 文件保留在仓库中。

模拟20个用户 每个用户查询100次

平均值339ms

![压测截图 1](images/order-stress-test/image1.png)

![压测截图 2](images/order-stress-test/image2.png)

在数据库加了索引之后

平均值 5 ms

![压测截图 3](images/order-stress-test/image3.png)

加入Redis

平均值 2 ms

![压测截图 4](images/order-stress-test/image4.png)

加入分布式锁

平均值 3 ms

![压测截图 5](images/order-stress-test/image5.png)

模拟20个用户

![压测截图 6](images/order-stress-test/image6.png)

![压测截图 7](images/order-stress-test/image7.png)

Caused by: com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException: Deadlock found when trying to get lock; tryrestarting transaction —— 尝试获取锁时发现死锁；请重新启动事务

死锁原因是 下单的时候 先扣库存再改订单

取消和退款的时候 先改订单 再回复库存

![压测截图 8](images/order-stress-test/image8.png)

测试100个库存是否超卖

![压测截图 9](images/order-stress-test/image9.png)

![压测截图 10](images/order-stress-test/image10.png)
