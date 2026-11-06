package com.cloud.business.es;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class RestaurantAmenityEsTest {

    @Resource
    private org.elasticsearch.client.RestHighLevelClient esClient;


    @Test
    void testCreateIndex() throws Exception {
        // 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("restaurant_amenity");
        // 设置 mapping（不带外层的 mappings，request.mapping() 会自动包）
        String mapping = """
                {
                  "properties": {
                    "id": { "type": "long" },
                    "restaurantName": { "type": "text", "analyzer": "ik_smart", "copy_to": "all", "fields": { "keyword": { "type": "keyword" } } },
                    "address": { "type": "keyword", "index": false },
                    "phone": { "type": "keyword", "index": false },
                    "rating": { "type": "integer" },
                    "cuisineType": { "type": "text", "analyzer": "ik_smart", "fields": { "keyword": { "type": "keyword" } } },
                    "description": { "type": "text", "analyzer": "ik_smart", "copy_to": "all" },
                    "amenities": { "type": "keyword" },
                    "location": { "type": "geo_point" },
                    "status": { "type": "integer", "copy_to": "all" },
                    "createTime": { "type": "date", "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss||epoch_millis" },
                    "updateTime": { "type": "date", "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss||epoch_millis" },
                    "all": { "type": "text", "analyzer": "ik_smart" }
                  }
                }""";
        // 把 mapping 设置到请求上
        request.mapping(mapping, XContentType.JSON);

        // 执行创建
        CreateIndexResponse response = esClient.indices().create(request, RequestOptions.DEFAULT);

        log.info("索引创建成功：{}", response.isAcknowledged());
    }

    @Test
    void testAddLocationField() throws Exception {
        PutMappingRequest request = new PutMappingRequest("restaurant_amenity")
                .source("{\"properties\":{\"location\":{\"type\":\"geo_point\"}}}", XContentType.JSON);

        AcknowledgedResponse response = esClient.indices().putMapping(request, RequestOptions.DEFAULT);
        log.info("加字段成功：{}", response.isAcknowledged());
    }

    @Test
    void testDeleteIndex() throws Exception {
        DeleteIndexRequest request = new DeleteIndexRequest("restaurant_amenity");
        AcknowledgedResponse delete = esClient.indices().delete(request, RequestOptions.DEFAULT);
        log.info("索引删除成功：{}", delete.isAcknowledged());
    }

    // ========== 文档增删改查 ==========

    @Test
    void testAddDoc() throws Exception {

        // 新增文档
        IndexRequest request = new IndexRequest("restaurant_amenity").id("1");
        String doc = """
                {
                  "id": 1,
                  "restaurantName": "海底捞火锅",
                  "address": "北京东城区美食街88号",
                  "cuisineType": "火锅",
                  "rating": 5,
                  "amenities": ["WiFi", "包间", "停车位"],
                  "status": 1
                }""";
        request.source(doc, XContentType.JSON);

        IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);
        log.info("新增文档成功，id：{}，result：{}", response.getId(), response.getResult());
    }

    @Test
    void testGetDoc() throws Exception {
        GetRequest request = new GetRequest("restaurant_amenity", "1");
        GetResponse response = esClient.get(request, RequestOptions.DEFAULT);
        log.info("查询结果：{}", response.getSourceAsString());
    }

    @Test
    void testUpdateDoc() throws Exception {
        UpdateRequest request = new UpdateRequest("restaurant_amenity", "1");
        String doc = """
                { "doc": { "rating": 4 } }""";
        request.doc(doc, XContentType.JSON);

        UpdateResponse response = esClient.update(request, RequestOptions.DEFAULT);
        log.info("更新结果：{}", response.getResult());
    }

    @Test
    void testDeleteDoc() throws Exception {
        DeleteRequest request = new DeleteRequest("restaurant_amenity", "1");
        DeleteResponse delete = esClient.delete(request, RequestOptions.DEFAULT);
        log.info("删除结果：{}", delete.getResult());
    }
}
