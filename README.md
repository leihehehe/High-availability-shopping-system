# High-availability-shopping-system
A high availability shopping system using SpringBoot, SpringCloud, Kafka, Redis, MySQL and Mybatis, Kubernetes.

# Redis
Redis is used as a cache in this project due to its high throughput. Compared to MySQL, it can take advantage of the reading speed as a cache. In my project, the Redis is responsible for storing deal and product information before the deal starts, then used to deal with a large number of requests for querying and updating deals. For instance, the Redis would be able to update the number of items held and the number of available items in stock, and it would limit the number of an item that one user could buy as well.

# MySQL & Mybatis
Mybatis is a framework supporting SQL for operating MySQL

# Kafka
I used Kafka in this project to implement a message queue. Kafka is doing excellent in the aspect of stream processing. In this project, Kafka is used for slowing down the requests - putting them in a queue, and picking them by consumers one by one - so that MySQL will not get crashed when it comes to a large number of operations. For the reason that Kafka has no function of delaying messages, I also implemented a task scheduler to finish the 15-minutes check for the order status.

# CDN
CDN(Content Delivery Network) was implemented for storing static resources so that users could access websites faster.

# Spring Cloud Microservices & resilience4j
To be done

# Docker & Kubernetes deployment
To be done

# Design rationale
To be done