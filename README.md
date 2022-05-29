# High-availability-shopping-system
A high availability shopping system using SpringBoot, SpringCloud, resillience4j, Kafka, Redis and MySQL, Kubernetes

# Redis
Redis is used as a cache in this project due to its high throughput. Compared to MySQL, it can take advantage of the reading speed as a cache. In my project, the Redis is responsible for storing deal and product information before the deal starts, then used to deal with a large number of requests for querying and updating deals. For instance, the Redis would be able to update the number of items held and the number of available items in stock, and it would limit the number of an item that one user could buy as well.

# MySQL & Mybatis
Mybatis is a framework supporting SQL for operating MySQL

# Kafka
I used Kafka in this project to implement a message queue. Kafka is doing excellent in the aspect of stream processing. In this project, Kafka is used for slowing down the requests - putting them in a queue, and picking them by consumers one by one - so that MySQL will not get crashed when it comes to a large number of operations. For the reason that Kafka has no function of delaying messages, I also implemented a task scheduler to finish the 15-minutes check for the order status.

# Time Counter for deals(not fully implemented in this project)

A synchronized time counter could be achieved by getting the time difference from the server, instead of clients' local times.

# CDN
CDN(Content Delivery Network) could be used for storing static resources so that users could access websites faster.

# Spring Cloud Microservices & resilience4j
Since I did not implement RESTful API for my project, microservices was not used. However, it is still a good choice if you do a pure bank-end application(RESTful API)
## Why use resilience4j?
- The downstream service may be down and the request will keep going to the down service, exhausting network resources and slowing performances.
- Bad user experience
- The failure of one service could cascade to other services throughout the whole application.

In this project, **rate limiter** was used to limit the rate of accessing the website.

# Docker & Kubernetes deployment
To be done

# Design rationale
To be done
