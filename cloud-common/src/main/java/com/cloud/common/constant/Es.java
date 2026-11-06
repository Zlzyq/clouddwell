package com.cloud.common.constant;

public final class Es {
    public static final String INDEX_NAME = "restaurant_amenity";
    public static final int BATCH_SIZE = 1000;
    public static final String SEARCH_FIELD = "all";

    // ========== 人工上门 ==========
    public static final String HOME_SERVICE_INDEX = "home_service_provider";
    public static final String HOME_SERVICE_SEARCH_FIELD = "all";

    public static final String HOME_SERVICE_INDEX_MAPPING = """
            {
              "properties": {
                "id": { "type": "long" },
                "name": { "type": "text", "analyzer": "ik_smart", "copy_to": "all", "fields": { "keyword": { "type": "keyword" } } },
                "phone": { "type": "keyword", "index": false },
                "skills": { "type": "keyword" },
                "location": { "type": "geo_point" },
                "rating": { "type": "double" },
                "acceptRate": { "type": "double" },
                "orderCount": { "type": "integer" },
                "status": { "type": "integer" },
                "serviceRadius": { "type": "integer" },
                "createTime": { "type": "date", "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss||epoch_millis" },
                "updateTime": { "type": "date", "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss||epoch_millis" },
                "all": { "type": "text", "analyzer": "ik_smart" }
              }
            }""";

    public static final String INDEX_MAPPING = """
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

}
