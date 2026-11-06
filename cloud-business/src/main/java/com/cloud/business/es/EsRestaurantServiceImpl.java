package com.cloud.business.es;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cloud.business.entity.RestaurantAmenityEntity;
import com.cloud.business.service.RestaurantAmenityService;
import com.cloud.common.domain.PageResult;
import com.cloud.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Stats;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cloud.common.constant.Es.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class EsRestaurantServiceImpl implements EsRestaurantService {

    private final RestHighLevelClient restHighLevelClient;
    private final RestaurantAmenityService restaurantAmenityService;

    @Override
    public String importFromMysql() {
        List<RestaurantAmenityEntity> restaurantAmenityEntities = restaurantAmenityService.list();
        BulkRequest bulkRequest = new BulkRequest();
        int count = 0;
        //遍历循环每一个实体 然后把每一个实体插入bulkRequest 一桶请求
        for (RestaurantAmenityEntity entity : restaurantAmenityEntities) {
            //拿到entity的json  parseObj变成map 方便把里面的经纬度拆开
            JSONObject jsonObj = JSONUtil.parseObj(JSONUtil.toJsonStr(entity));
            //专门创建的方便平级
            Map<String, Object> location = new HashMap<>();
            location.put("lat", entity.getLatitude());
            location.put("lon", entity.getLongitude());
            jsonObj.set("location", location);
            //移除原本的
            jsonObj.remove("latitude");
            jsonObj.remove("longitude");
            bulkRequest.add(new IndexRequest(INDEX_NAME).id(entity.getId().toString()).source(jsonObj));
            count++;
            if (count % BATCH_SIZE == 0) {
                try {
                    BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                    if (bulkResponse.hasFailures()) {
                        String failMsg = bulkResponse.buildFailureMessage();
                        log.error("ES批量导入失败：{}", failMsg);
                        throw new GlobalException("ES批量导入失败：" + failMsg);
                    }
                } catch (IOException e) {
                    throw new GlobalException("es崩溃啦");
                }
                bulkRequest = new BulkRequest();
            }
        }
        if (!bulkRequest.requests().isEmpty()) {
            try {
                BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                if (bulkResponse.hasFailures()) {
                    String failMsg = bulkResponse.buildFailureMessage();
                    log.error("ES批量导入失败：{}", failMsg);
                    throw new GlobalException("ES批量导入失败：" + failMsg);
                }
            } catch (IOException e) {
                throw new GlobalException("es崩溃啦");
            }
        }
        return "✅ 成功导入 " + count + " 条数据到 ES";
    }

    @Override
    public PageResult<RestaurantAmenityEntity> searchFromEs(String keyword, String cuisineType, int minRating, String sortBy, String sortOrder, Double lat, Double lon, Integer maxDistance, int page, int size, boolean useSecondaryFilter) {
        //指定索引
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        //第一层 搭建查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //分词后匹配
        boolQuery.must(QueryBuilders.matchQuery(SEARCH_FIELD, keyword));
        //传了菜系 就精确匹配（不可分词）
        if (!cuisineType.isBlank()) {
            boolQuery.filter(QueryBuilders.termQuery("cuisineType.keyword", cuisineType));
        }
        //传了至少的评分标准
        if (minRating != 0) {
            boolQuery.filter(QueryBuilders.rangeQuery("rating").gte(minRating));
        }
        //传了距离信息
        if (lat != null && lon != null && maxDistance != null && maxDistance > 0) {
            boolQuery.filter(QueryBuilders.geoDistanceQuery("location").point(lat, lon).distance(maxDistance + "km"));
        }

        //第二层 建sourceBuilder
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("restaurantName");
        highlightBuilder.field("description");
        highlightBuilder.requireFieldMatch(false);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //加入聚合
        sourceBuilder.aggregation((AggregationBuilders.terms("ratingDistribution").field("rating").size(5)));
        sourceBuilder.aggregation(AggregationBuilders.stats("ratingStats").field("rating"));

        if (useSecondaryFilter) {
            // 初筛不用 function_score，只跑基础查询，多捞候选数据
            sourceBuilder.query(boolQuery);
            sourceBuilder.from(0).size(30); // 多捞点，给 Java 层算分用
            if (lat != null && lon != null) {
                sourceBuilder.sort("_score", SortOrder.DESC);
            } else {
                sourceBuilder.sort(sortBy, SortOrder.fromString(sortOrder));
            }
        } else {
            // 如果传了经纬度，就用加权算分
            if (lat != null && lon != null) {
                sourceBuilder.query(QueryBuilders.functionScoreQuery(
                        boolQuery,
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        ScoreFunctionBuilders.fieldValueFactorFunction("rating").factor(2f)
                                ),
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        ScoreFunctionBuilders.gaussDecayFunction("location", new GeoPoint(lat, lon), "3km")
                                )
                        }
                ).scoreMode(FunctionScoreQuery.ScoreMode.MULTIPLY).boostMode(CombineFunction.MULTIPLY));
            } else {
                sourceBuilder.query(boolQuery);
            }
            if (lat != null && lon != null) {
                sourceBuilder.sort("_score", SortOrder.DESC);
            } else {
                sourceBuilder.sort(sortBy, SortOrder.fromString(sortOrder));
            }
            // 分页交给 ES
            sourceBuilder.from((page - 1) * size).size(size);
        }

        sourceBuilder.highlighter(highlightBuilder);
        //第三层 把建sourceBuilder塞进searchRequest里面
        searchRequest.source(sourceBuilder);
        //执行
        try {
            //第四层 把 searchRequest 发出去
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = search.getHits().getHits();
            long total = search.getHits().getTotalHits().value;
            List<RestaurantAmenityEntity> list = new ArrayList<>();
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                //解析成json object(类似map)
                JSONObject sourceJson = JSONUtil.parseObj(sourceAsString);
                JSONObject location = sourceJson.getJSONObject("location");
                if (!(location == null)) {
                    sourceJson.set("latitude", location.getDouble("lat"));
                    sourceJson.set("longitude", location.getDouble("lon"));
                }
                //移除原有字段
                sourceJson.remove("location");
                RestaurantAmenityEntity restaurantAmenityEntity = JSONUtil.toBean(sourceJson, RestaurantAmenityEntity.class);
                //高亮：把原始字段值替换成带 <em> 标签的高亮值
                if (highlightFields.containsKey("restaurantName")) {
                    restaurantAmenityEntity.setRestaurantName(highlightFields.get("restaurantName").getFragments()[0].toString());
                }
                if (highlightFields.containsKey("description")) {
                    restaurantAmenityEntity.setDescription(highlightFields.get("description").getFragments()[0].toString());
                }
                list.add(restaurantAmenityEntity);
            }

            //Java 算分
            if (useSecondaryFilter && lat != null && lon != null) {
                for (RestaurantAmenityEntity item : list) {
                    double score = 0;
                    // ① 评分 × 2：评分越高分越多
                    score += (item.getRating() != null ? item.getRating() : 0) * 2;
                    // ② 距离高斯衰减：越近分越高（3km 内满分，越远越低）
                    if (item.getLatitude() != null && item.getLongitude() != null) {
                        double dist = haversine(lat, lon, item.getLatitude(), item.getLongitude());
                        double distScore = Math.exp(-Math.pow(dist / 3000.0, 2));
                        score += distScore;
                    }
                    item.setScore(score);
                }
                // 按总分降序排列
                list.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
                // 手动分页：取当前页的数据
                int from = (page - 1) * size;
                int to = Math.min(from + size, list.size());
                List<RestaurantAmenityEntity> paged;
                if (from >= list.size()) {
                    paged = new ArrayList<>();
                } else {
                    paged = list.subList(from, to);
                }
                // 二次过滤的 total 用候选数（最多 30），而不是 ES 总命中数
                total = list.size();
                list = paged;
            }

            //解析聚合结果（在循环外面，只需要解析一次）
            Map<String, Object> aggMap = new HashMap<>();
            //评分分布：按 rating 分组，统计 1~5 分各有多少家
            Terms terms = search.getAggregations().get("ratingDistribution");
            List<Map<String, Object>> distList = new ArrayList<>();
            for (Terms.Bucket bucket : terms.getBuckets()) {
                Map<String, Object> item = new HashMap<>();
                item.put("key", bucket.getKeyAsNumber());
                item.put("count", bucket.getDocCount());
                distList.add(item);
            }
            aggMap.put("ratingDistribution", distList);
            //总体概览：总分、最低分、最高分、平均分、总评分和
            Stats stats = search.getAggregations().get("ratingStats");
            Map<String, Object> statsMap = new HashMap<>();
            statsMap.put("count", stats.getCount());
            statsMap.put("min", stats.getMin());
            statsMap.put("max", stats.getMax());
            statsMap.put("avg", stats.getAvg());
            statsMap.put("sum", stats.getSum());
            aggMap.put("ratingStats", statsMap);

            PageResult<RestaurantAmenityEntity> result = new PageResult<>(page, size, total, list, null);
            result.setAggregations(aggMap);
            return result;
        } catch (IOException e) {
            throw new GlobalException("es崩溃啦");
        }
    }

    /**
     * Haversine 公式计算两点之间的距离（米）
     * haversine = 半正矢公式，用于计算球面两点距离
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // 地球半径（米）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public String clearEs() {
        //  创建 DeleteByQueryRequest，指定索引名
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(INDEX_NAME);
        // 设置查询条件为 match_all（匹配全部）
        deleteByQueryRequest.setQuery(QueryBuilders.matchAllQuery());
        //  用 restHighLevelClient.deleteByQuery() 发出去
        try {
            restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new GlobalException("清除es数据失败");
        }
        return "✅ ES 数据已清空";
    }

}
