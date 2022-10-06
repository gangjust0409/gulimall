## ElasticSeach

### 入门

什么是：分布式的开源搜索和分析引擎，基本概念：索引、类型、document（json文档）、倒排索引机制

安装：

```
docker 安装 elasticSearch
docker pull elasticsearch:7.4.2
// 可视化界面
docker pull kibana:7.4.2

// 创建实例
mkdir -p /mydata/elasticsearch/config
mkdir -p /mydata/elasticsearch/data
// 将http.host:000写入yml文件
echo "http.host: 0.0.0.0">>/mydata/elasticsearch/config/elasticsearch.yml

docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
// 设置为单节点
-e "discovery.type=single-node" \
// 设置启动内存，一启动64m，启动完毕128m
-e ES_JAVA_OPTS="-Xms64m-Xmx128m" \
// 挂载
-v /mydata/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
-v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
-d elasticsearch:7.4.2

docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
-e "discovery.type=single-node" \
-e ES_JAVA_OPTS="-Xms64m -Xmx512m" \
-v /mydata/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
-v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
-d elasticsearch:7.4.2


// 将普通用户设置权限
chmod -R 777 需要设置权限文件，从第一层目录开始
```

安装 kibana

```
docker run --name kibana -e ELASTICSEARCH_HOSTS=http://123.56.145.199:9200 -p 5601:5601 \
-d kibana:7.4.2

## ELASTICSEARCH_HOSTS 一定要改成自己的ip地址
```

#### 普通resut风格crud

```
查询
get http://123.56.145.199:9200/customer/external/1 
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 10,
    "_seq_no": 10,
    "_primary_term": 1,
    "found": true,
    "_source": {
        "name": "just"
    }
}
post http://123.56.145.199:9200/customer/external/1
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 11,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 11,
    "_primary_term": 1
}

post http://123.56.145.199:9200/customer/external
{
    "_index": "customer",  // 索引
    "_type": "external", // 类型
    "_id": "Kfk89YAB57Qw2DiN5TNS",  // 自增的id
    "_version": 1,  // 版本
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    // 做乐观锁用的
    "_seq_no": 12,
    "_primary_term": 1
}

put http://123.56.145.199:9200/customer/external/1
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 12,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 13,
    "_primary_term": 1
}

// 更新操作
post 
http://123.56.145.199:9200/customer/external/1/_update
json格式必须指定在"doc"下
{
    "doc":{
        "name":"just",
        "age":28
    }
}
// 输出
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 10,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 10,
    "_primary_term": 1
}

put和post两种方式都可以提交保存
post提交不指定id时，则默认自增一个id

delete http://123.56.145.199:9200/customer/external/1
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 13,
    "result": "deleted",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 14,
    "_primary_term": 1
}
// 再查询后
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "found": false  // 响应数据无
}

// 可以删除索引，不可删除类型
delete http://123.56.145.199:9200/customer
{
    "acknowledged": true
}
// 删除索引后，就不存在
{
    "error": {
        "root_cause": [
            {
                "type": "index_not_found_exception",
                "reason": "no such index [customer]",
                "resource.type": "index_expression",
                "resource.id": "customer",
                "index_uuid": "_na_",
                "index": "customer"
            }
        ],
        "type": "index_not_found_exception",
        "reason": "no such index [customer]",
        "resource.type": "index_expression",
        "resource.id": "customer",
        "index_uuid": "_na_",
        "index": "customer"
    },
    "status": 404
}
```

##### 批量插入

```json
post customer/external/_bulk
语法格式： { action: { metadata }}\n { request body }\n { action: { metadata }}\n { request body }\n

{"index":{"_id":"1"}}
{"name": "John Doe" } 
{"index":{"_id":"2"}} 
{"name": "Jane Doe" }

// 响应内容
{
  "took" : 303, // 响应的秒数
  "errors" : false, // 是否错误
  "items" : [ // 数据合集
    {
      "index" : {
        "_index" : "customer",
        "_type" : "external",
        "_id" : "1",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 0,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "index" : {
        "_index" : "customer",
        "_type" : "external",
        "_id" : "2",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 1,
        "_primary_term" : 1,
        "status" : 201
      }
    }
  ]
}
// 复杂实例  格式必须写对，不然报错400
POST /_bulk
{"delete":{"_index":"website","_type":"blog","_id":"123"}}
{"create":{"_index":"website", "_type":"blog","_id":"123"}}
{"title": "My first blog post"}
{"index":{"_index": "website", "_type": "blog"}}
{"title":"My second blog post"}
{"update": {"_index": "website", "_type": "blog", "_id": "123"}}
{ "doc" : {"title":"My updated blog post"}}
```



### 进阶

































