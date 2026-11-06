package com.cloud.business.mq;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cloud.common.constant.Es;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Canal 数据同步消费者
 * <p>
 * MySQL binlog → Canal（偷看binlog） → RabbitMQ → 这个消费者 → ES
 * <p>
 * Canal 消息格式（JSON）：
 * {
 *   "type": "INSERT",           // INSERT / UPDATE / DELETE
 *   "table": "restaurant_amenity",
 *   "data": [{ "id": "1", "restaurant_name": "xxx", ... }],  // 新数据
 *   "old": [{ "restaurant_name": "旧名" }]                    // UPDATE 时才有
 * }
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CanalConsumer {

    private final RestHighLevelClient restHighLevelClient;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "canal.order.queue"),
            exchange = @Exchange(name = "canal.exchange", type = ExchangeTypes.TOPIC),
            key = "canal.order"))
    public void handleCanalMessage(String message) {
        log.info("收到 Canal 消息：{}", message);
        try {
            JSONObject root = JSONUtil.parseObj(message);
            String type = root.getStr("type");      // INSERT / UPDATE / DELETE
            String table = root.getStr("table");    // 表名（MySQL 原始表名）

            // 只处理关心的表
            if (!isWatchedTable(table)) {
                return;
            }

            // data 是 Canal 给的数据数组（一条 SQL 可能影响多行）
            List<JSONObject> dataList = root.getJSONArray("data").toList(JSONObject.class);
            if (dataList == null || dataList.isEmpty()) {
                return;
            }

            // 确定 ES 索引名
            String esIndex = getEsIndex(table);

            for (JSONObject row : dataList) {
                String id = row.getStr("id");
                if (id == null) continue;

                if ("DELETE".equals(type)) {
                    handleDelete(esIndex, id);
                } else {
                    handleInsertOrUpdate(esIndex, id, row, type);
                }
            }
        } catch (Exception e) {
            log.error("Canal 消息处理失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 判断是否是需要监听同步的表
     */
    private boolean isWatchedTable(String table) {
        return "restaurant_amenity".equals(table) || "home_service_provider".equals(table);
    }

    /**
     * 根据 MySQL 表名返回 ES 索引名
     */
    private String getEsIndex(String table) {
        if ("restaurant_amenity".equals(table)) {
            return Es.INDEX_NAME;
        }
        if ("home_service_provider".equals(table)) {
            return Es.HOME_SERVICE_INDEX;
        }
        return null;
    }

    /**
     * 处理 INSERT / UPDATE → 写入 ES
     */
    private void handleInsertOrUpdate(String esIndex, String id, JSONObject row, String type) throws IOException {
        // Canal 给的键名是 snake_case（restaurant_name），转成 camelCase（restaurantName）
        JSONObject doc = convertKeysToCamelCase(row);

        // 处理经纬度 → location 嵌套对象（ES geo_point 需要）
        Object latObj = doc.remove("latitude");
        Object lonObj = doc.remove("longitude");
        if (latObj != null && lonObj != null) {
            Map<String, Object> location = new HashMap<>();
            location.put("lat", Double.parseDouble(latObj.toString()));
            location.put("lon", Double.parseDouble(lonObj.toString()));
            doc.set("location", location);
        }

        // 写入 ES（upsert：有就更新，没有就新增）
        IndexRequest indexRequest = new IndexRequest(esIndex).id(id).source(doc);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info("Canal 同步写入 ES [{}] id={} type={}", esIndex, id, type);
    }

    /**
     * 处理 DELETE → 删除 ES 文档
     */
    private void handleDelete(String esIndex, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(esIndex, id);
        restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        log.info("Canal 同步删除 ES [{}] id={}", esIndex, id);
    }

    /**
     * 将 Canal 的 snake_case 键名转为 camelCase
     * 例：restaurant_name → restaurantName
     * <p>
     * Canal 给的是 MySQL 原始列名（下划线风格），
     * 但 ES 文档用的是 Java 实体字段名（驼峰风格）
     */
    private JSONObject convertKeysToCamelCase(JSONObject original) {
        JSONObject result = new JSONObject();
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            String camelKey = StrUtil.toCamelCase(entry.getKey());
            result.set(camelKey, entry.getValue());
        }
        return result;
    }
}
