/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.infrastructure.saas.cloudsvc.nosql.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.infrastructure.saas.common.util.GsonUtil;
import com.alipay.autotuneservice.infrastructure.saas.common.util.SpringContextUtil;
import com.alipay.autotuneservice.infrastructure.saas.common.util.SpringPropertiesCache;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.Document;
import org.springframework.core.env.StandardEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yiqi
 * @version 1.0
 * @description mongo标准版provider
 * @date 2022/7/7 15:50
 **/
@Slf4j
@Deprecated
public class StandardMongoProvider {

    MongoClient    mongoClient;

    MongoDatabase  db;

    private String connectionUrl;
    private String databaseName;

    public StandardMongoProvider() {
        //try {
        //    init();
        //    mongoClient = MongoClients.create(connectionUrl);
        //    db = mongoClient.getDatabase(databaseName);
        //} catch (Exception e) {
        //    log.error("StandardMongoProvider - create mongodb client occurs ann error.", e);
        //}
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public Query query() {
        return new Query(db);
    }

    public void createTable(String tableName) {
        db.createCollection(tableName);
    }

    public void deleteCollection(String tableName) {
        db.getCollection(tableName).drop();
    }

    public <T> void insert(String tableName, T data) {
        Document document = Document.parse(GsonUtil.toJson(data));
        db.getCollection(tableName).insertOne(document);
    }

    public <T> void batchInsert(List<T> data, String tableName) {
        List<Document> documents = new ArrayList<>();
        for (T datum : data) {
            documents.add(Document.parse(GsonUtil.toJson(datum)));
        }
        db.getCollection(tableName).insertMany(documents);
    }

    public static class Query {
        private String              tableName;
        private Map<String, Object> query;
        private Object              fromValue;
        private Object              toValue;
        private String              indexName;

        private String              rangeKey;

        MongoDatabase               db;

        public Query(MongoDatabase db) {
            this.db = db;
        }

        public Query withQueryParam(String key, String value) {
            if (query == null) {
                query = new HashMap<>();
            }
            query.put(key, value);
            return this;
        }

        public Query withQueryParam(String key, Object value) {
            MapUtils.emptyIfNull(query).put(key, value);
            return this;
        }

        public Query withQueryParams(Map<String, Object> params) {
            query = params;
            return this;
        }

        public Query withTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Query withFrom(Object fromValue) {
            this.fromValue = fromValue;
            return this;
        }

        public Query withTo(Object toValue) {
            this.toValue = toValue;
            return this;
        }

        public Query withRangeKey(String rangeKey) {
            this.rangeKey = rangeKey;
            return this;
        }

        public Query withIndex(String indexName) {
            this.indexName = indexName;
            return this;
        }

        public <T> List<T> queryWithParam(Class<T> clazz) {
            BsonDocument bsonDocument = new BsonDocument();
            Set<String> keys = query.keySet();
            for (String key : keys) {
                Object value = query.get(key);
                if (value instanceof String) {
                    bsonDocument.put(key, new BsonString((String) value));
                } else if (value instanceof Integer) {
                    bsonDocument.put(key, new BsonInt32((Integer) value));
                } else if (value instanceof Long) {
                    bsonDocument.put(key, new BsonInt64((Long) value));
                }
            }
            FindIterable<Document> documents = db.getCollection(tableName).find(bsonDocument);
            List<T> data = new ArrayList<>();
            for (Document document : documents) {
                T t = JSONObject.parseObject(GsonUtil.toJson(document), clazz);
                data.add(t);
            }
            return data;
        }

        public <T> List<T> queryWithRange(Class<T> clazz) {
            BasicDBObject basic = new BasicDBObject();
            basic.put(rangeKey, new BasicDBObject("$gte", fromValue).append("$lte", toValue));
            FindIterable<Document> documents = db.getCollection(tableName).find(basic);
            List<T> data = new ArrayList<>();
            for (Document document : documents) {
                T t = JSONObject.parseObject(GsonUtil.toJson(document), clazz);
                data.add(t);
            }
            return data;
        }

    }

    private void init() {
        String include = SpringContextUtil.getSdkEnv();
        Map<String, Object> data = SpringPropertiesCache.get(include);
        Map<String, Object> aliyunMap = (HashMap<String, Object>) data.get("aliyun");
        Map<String, String> mongoMap = (HashMap<String, String>) aliyunMap.get("mongo");
        StandardEnvironment standardEnvironment = new StandardEnvironment();
        String url = standardEnvironment.resolvePlaceholders(mongoMap.get("url"));
        String username = standardEnvironment.resolvePlaceholders(mongoMap.get("username"));
        String password = standardEnvironment.resolvePlaceholders(mongoMap.get("password"));
        databaseName = standardEnvironment.resolvePlaceholders(mongoMap.get("database"));
        // mongodb://<username>:<password>@<host:ip>
        connectionUrl = String.format("mongodb://%s:%s@%s", username, password, url);
    }

    public String getConnectionUrl() {
        return this.connectionUrl;
    }
}
