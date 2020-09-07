#Kafka Spring Retry

###Description

The main goal of this project is to test two ways of doing a "retry":
  
- First One  
    - Using two topics:
        - The Main topic
        - The DLT (Dead Letter Topic)
    - The main consumer gets blocked and retries until the operation is successful, or the maximum of attempts is
     reached. If the latest happens, the message goes to the DLT.
- Second One
    - Using three topics:
        - The Main topic
        - The Retry topic
        - The Error topic
    - The main consumer tries to consume the message and if an error occurs it goes to the Retry Topic. The Retry
     consumer will retry until the operation is successful, or the maximum of attempts is reached. If the latest
      happens, the message goes to the Error topic.
  

###Execution 

When trying to call a successful topic send the message containing "oi".

To block the main consumer:

```console
curl --location --request GET 'http://localhost:8080/block/consumer/message/<<message>>'
```

To block the "retry" consumer:

```console
curl --location --request GET 'http://localhost:8080/block/retry/message/<<message>>'
```

###Sources

Docker taken from this article: https://medium.com/azure-na-pratica/apache-kafka-kafdrop-docker-compose-montando-rapidamente-um-ambiente-para-testes-606cc76aa66
                                