# Logstash Java Plugin

[![Travis Build Status](https://travis-ci.com/logstash-plugins/logstash-filter-java_filter_example.svg)](https://travis-ci.com/logstash-plugins/logstash-filter-java_filter_example)

This is a Java plugin for [Logstash](https://github.com/elastic/logstash).

It is fully free and fully open source. The license is Apache 2.0, meaning you are free to use it however you want.

The documentation for Logstash Java plugins is available [here](https://www.elastic.co/guide/en/logstash/current/contributing-java-plugin.html).

### 背景
自2025年9月左右，原本依赖elasticsearch的geoip的Ingest Pipelines失效了，导致原本用于ip解析geo的功能失效，致使运维看板无法显示Map信息 \
实际上是MaxMind公司以为中美政策的影响，关闭了中国地区的City库的下载渠道，如果使用country库那么在我们的Map上可能一个国家或地区的ip集将显示一个点 \
联系阿里云的技术支持，也无法第一时间给出解决方案，由于此前下载过离线库，所以先用logstash的geoip插件进行ip解析，不过失去了更新能力 \
logstash、nginx、es等插件都是基于GeoLite2-City.mmdb库进行的插件开发，如果想要第三方的库则都需要进行对应的开发\
### 解析库的比较
相较于GeoLite2-City.mmdb，国产的ip2region库也是不错的选择，但是ip2region开源离线库没有geoPoint的数据集，这点很头疼，商业版本则需要收费\
但如果必须要使用则还是需要依赖此库的，目前看板使用离线库虽然准确度不够，但好在作为全球化业务，在整个map上显示是可以忽略的
这里开个坑，暂时只集成ip2region库的开源数据集，就不自费商业版的库进行扩展开发了，等将来真正需要用到再进行扩展

### Todo
ip2region库商业版本更多丰富的字段适配

### 记录一下
github上有个ip2region-es-ingest-pipeline属于在es插件层面适配ip2region,不过没有进行测试，看未来需要可以进行参考
https://github.com/yongplus/ip2region-es-ingest-pipeline