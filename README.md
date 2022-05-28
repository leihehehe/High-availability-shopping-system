# High-availability-shopping-system
A high availability shopping system using SpringBoot, Kafka, Redis, MySQL and Mybatis.

# Redis
Redis is used as a cache in this proejct due to its high throughput. Compared to MySQL, it can take adavantage of the reading speed as a cache. In my project, the reids is responsible for storing deal and product inforamtion before the deal starts, then used to deal with a large amount of requests of querying and updating deals. For instance, the redis would be able to updating the number of items hold and the number of avialble items in stock, and it would limit the number of an item that one user could buy as well.

# MySQL & Mybatis
Mybatis is a framework supporting SQL ofr operating MySQL

# Kafka
I used Kafka in this project to implement a message queue. Kafka is doing excellent in the aspect of stream processing. In this project, Kafka is used for slowing down the requests - putting them in a queue, and picking them by consumers one by one - so that MySQL will not get crashed when it comes to a large number of operations. For the reason that Kafka has no function of delaying messages, I also implemented a task scheduler to finish the 5-minutes check for the order status.

# CDN
CDN(Content Delivery Network) was implemented for storing static resources so that users could access websites faster.

# Spring Cloud
TBC

# Design rational
TBC
