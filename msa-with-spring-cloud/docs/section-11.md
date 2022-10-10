# 데이터 동기화를 위한 Apache Kafka 활용 - 1

### Apache Kafka 개요

- Apache software Foundation의 Scalar 언어로 된 오픈 소스 메시지 브로커 프로젝트
- 실시간 데이터 피드를 관리하기 위해 통일된 높은 처리량, 낮은 지연 시간을 지닌 플랫폼 제공

![image](https://user-images.githubusercontent.com/83503188/194717048-79a2640d-1583-4cf4-b821-18e633f6f42d.png)

- 각각의 DB는 다양한 서비스에 데이터를 End-to-End 방식으로 전달한다고 가정하면, 서로 다른 데이터 파이프라인 연결 구조를 가졌기 때문에 Mysql에서 전달해줄 수 있는 시스템을 Oracle or mongodb에서 사용할 수 없다는 단점을 가진다.
- 따라서 확장이 어려운 구조 

![image](https://user-images.githubusercontent.com/83503188/194717134-7354caac-1e02-4c00-ba97-b74db7816b5c.png)

- Kafka의 등장으로 각각의 DB는 자신들이 전송하는 데이터가 어떠한 시스템으로 전달되는지 상관하지않고 Kafka에만 보내면된다.
- 또한 각각의 서비스에서도 DB를 상관하지않고 Kafka에서만 데이터를 받아오면 되기때문에 단일 format을 유지할 수 있다.

#### Kafka Broker

카프카의 서버

- 실행 된 Kafka 애플리케이션 서버
- 3대 이상의 Broker Cluster 구성
- Zookeeper 연동
    - 역할: 메타데이터(Broker ID, Controller ID 등) 저장
    - Controller 정보 저장
- n개 Broker 중 1대는 Controller 기능 수행
    - Controller 역할
        - 각 Broker에게 담당 파티션 할당 수행
        - Broker 정상 동작 모니터링 관리

![image](https://user-images.githubusercontent.com/83503188/194717281-eb05717f-d248-466e-95d7-6ac446e28599.png)
- 3개 이상의 Broker와 클러스터 구조를 가지는 것을 권장한다.
- 여러 개의 브로커들이 서로 밀접하게 연결되면서 한 곳에 저장된 메시지를 다른 곳에 공유해줌으로써 하나의 Broker가 문제가 생기면 대신할 수 있는 Broker를 둔다
- 서버의 상태, 장애 체크, 복구를 해주는 코디네이터 시스템과 연동하는데 Kafka에서 사용하는 코디네이터가 Zookeeper

### Apache Kafka 사용 - Producer/Consumer
1. 카프카에 메시지를 보내고 카프카가 메시지를 저장하고 있다가 다른 Consumer에게 메시지를 전달해주는 시나리오
2. 데이터베이스의 자료가 변경되었을 때(INSERT, UPDATE) 데이터베이스로부터 카프카가 변경된 데이터에 대한 메시지를 가져오고 값을 다른 쪽의 데이터베이스, 스토리지, 서비스에 전달해주는 카프카 커넥트 기능 시나리오

#### Ecosystem(시나리오) 1 - Kafka Client

- Kafka와 데이터를 주고받기 위해 사용하는 Java 라이브러리
- Producer, Consumer, Admin, Stream 등 Kafka관련 API 제공
- 다양한 3rd party library 존재: C/C++, Node.js, Python, .NET 등

![image](https://user-images.githubusercontent.com/83503188/194742816-96f50386-9c59-4abb-a9da-96a81c0de79e.png)

필요한 메시지를 각 서비스끼리 End-to-End 방식으로 전달하는게 아니라 가운데 Kafka라는 클러스터링 시스템을 두고 보내는 쪽에서는 카프카에 데이터를 보내고 받는 쪽에서도 Kafka를 통해서 받음으로써
누가 메시지를 보내고 누구에게 메시지를 보내는지에 대한 의존성을 제거할 수 있다.

**Kafka 서버 기동**

![image](https://user-images.githubusercontent.com/83503188/194742956-64db1bab-88cb-4c9b-9cc9-4bda087ce97e.png)

- kafka로 producer가 메시지를 보내게되면 데이터는 Topic에 저장된다.
- Topic에 관심이 있는 consumer는 해당 Topic을 등록하게 된다.
- Topic에 전달된 내용물이 있을 경우에 해당 Topic에 전달된 메시지를 Topic을 등록한 Consumer들에게 일괄적으로 전달해주는 방식
  - --topic {토픽 이름}
  - --bootstrap-server {카프카 서버 주소}: 해당 주소에 토픽을 생성하겠다
  - --partition 1: 멀티 클러스터링 구조를 구성했을 때 토픽에 전달된 메시지를 몇군데 나눠서 저장할지 정하는 옵션 

**Kafka Producer/Consumer 테스트**

![image](https://user-images.githubusercontent.com/83503188/194743374-9b4203c8-cb5e-483e-a658-7721f77cd007.png)

#### Ecosystem(시나리오) 2 - Kafka Connect

특별하게 프로그래밍없이 Configuration만 가지고 데이터를 특정한 곳에서 받아와서 다른 쪽으로 이동시켜주는 기능 
- Kafka Connect를 통해 Data를 Import/Export 가능
- 코드 없이 Configuration으로 데이터를 이동
- Standalone mode, Distribution mode 지원
    - RESTful API 통해 지원
    - Stream 또는 Batch 형태로 데이터 전송 가능
    - 커스텀 Connector를 통한 다양한 Plugin 제공 (File, S3, Hive, Mysql, etc ...)
- 파일로부터 데이터를 받아와서(import) 파일로 데이터를 전송(export) / 데이터베이스로부터 데이터를 받아와서 파일로 데이터 전송

![image](https://user-images.githubusercontent.com/83503188/194744847-33de1446-8fc3-4d0a-8750-020039b0296e.png)

- Kafka Connect Source: 데이터를 가져오는 쪽
- Kafka Connect Sink: 데이터를 보내는 쪽

### Orders Microservice에서 MariaDB 연동 

**라이브러리 추가**

- mariadb-java-client

![image](https://user-images.githubusercontent.com/83503188/194745843-b197d65a-2519-4daf-8b02-177df25de448.png)

![image](https://user-images.githubusercontent.com/83503188/194747315-d15d4390-4fc6-4d3f-944a-ccbc0b2edc2e.png)

![image](https://user-images.githubusercontent.com/83503188/194747328-b9ae9fec-598d-45dd-81d7-1ebf9d7262cd.png)

![image](https://user-images.githubusercontent.com/83503188/194747359-336b26d4-0f29-42f2-b11a-de645224d688.png)


### Kafka Connect 설치 


curl -O http://packages.confluent.io/archive/5.5/confluent-community-5.5.2-2.12.tar.gz

curl -O http://packages.confluent.io/archive/6.1/confluent-community-6.1.0.tar.gz

tar xvf confluent-community-6.1.0.tar.gz

cd  $KAFKA_CONNECT_HOME

**Kafka Connect 실행**

./bin/windows/connect-distributed ./etc/kafka/connect-distributed.properties

**JDBC Connector 설치**

- https://docs.confluent.io/5.5.1/connect/kafka-connect-jdbc/index.html
- confluentinc-kafka-connect-jdbc-10.0.1.zip 

카프카 커넥트를 통해서 데이터를 한쪽에서 읽어와서 다른쪽으로 전달하기 위해서는 사용하고자하는 타겟에 맞는 JDBC Connector가 필요하다.

etc/kafka/connect-distributed.properties 파일 마지막에 아래 plugin 정보 추가
- plugin.path=C:\Users\dudwl\work\kafka-connect-jdbc\lib

카프카 커넥트의 설정파일인 connect-distributed.properties 에서 커넥터를 추가연동하기 위해서는 파일을 변경해야 한다.

JdbcSourceConnector에서 MariaDB 사용하기 위해 mariadb 드라이버 복사
./share/java/kafka/ 폴더에 mariadb-java-client-2.7.2.jar  파일 복사

**kafka-connect 실행 후 토픽**

![image](https://user-images.githubusercontent.com/83503188/194766096-5fd2d99d-bf0f-4d58-b04f-08d414ddbf38.png)
- connect-configs, connect-offsets, connect-status 추가된 모습

### Kafka Source Connect 사용

![image](https://user-images.githubusercontent.com/83503188/194766539-a17c268c-f96f-48ab-9cea-189cec374a29.png)

**Kafka Source Connect 추가 (MariaDB)**

```json
{
    "name" : "my-source-connect",
    "config" : {
        "connector.class" : "io.confluent.connect.jdbc.JdbcSourceConnector",
        "connection.url":"jdbc:mysql://127.0.0.1:3306/mydb",
        "connection.user":"root",
        "connection.password":"1234",
        "mode": "incrementing",
        "incrementing.column.name" : "id",
        "table.whitelist":"users",
        "topic.prefix" : "my_topic_",
        "tasks.max" : "1"
    }
}
```

curl -X POST -d @- http://localhost:8083/connectors --header "content-Type:application/json"

- name: 커넥트이름
- mode: incrementing - 데이터가 등록되면서 데이터를 자동으로 증가시키는 모드
- incrementing.column.name: 자동으로 증가될 컬럼
- table.whitelist: 데이터베이스에 특정한 값을 저장하면 데이터베이스를 체크하고있다가 변경사항이 생기면 가져와서 토픽에 저장한다. 해당 작업 시에 whitelist의 테이블을 체크하는 것
- topic.prefix: 감지 내용을 저장할 위치 -> my_topic_users

**kafka Connect 목록 확인**
- curl http://localhost:8083/connectors | jq
**kafka Connect 확인**
- curl http://localhost:8083/connectors/my-source-connect/status | jq

![image](https://user-images.githubusercontent.com/83503188/194767580-e88fb1bc-8b95-40e2-b439-cf4f0681b670.png)

![image](https://user-images.githubusercontent.com/83503188/194767613-6f0c3a31-fd16-4429-98aa-02f642bfaf52.png)

![image](https://user-images.githubusercontent.com/83503188/194767672-91081ed2-1942-44a5-a331-17dd882c5041.png)

**mydb 변동사항 생성 - insert**

![image](https://user-images.githubusercontent.com/83503188/194767802-b4a7a6c0-c710-4b94-981d-8ecbb7616a85.png)

![image](https://user-images.githubusercontent.com/83503188/194767840-0c76e3e3-d8cd-44ca-9031-c2aa845fb91f.png)

토픽이 생성된 모습

![image](https://user-images.githubusercontent.com/83503188/194767933-6baa27de-1c4a-4b0d-a387-4978c05084ee.png)

Consumer를 통해 topic에 들어온 json정보를 확인할 수 있다.

### Kafka Sink Connect 사용

**Kafka Connect에 아래 내용 전달**

```json
{

    "name":"my-sink-connect2",
    "config":{
        "connector.class":"io.confluent.connect.jdbc.JdbcSinkConnector",
        "connection.url":"jdbc:mysql://127.0.0.1:3306/mydb",
        "connection.user":"root",
        "connection.password":"1234",
        "auto.create":"true",
        "auto.evolve":"true",
        "delete.enabled":"false",
        "tasks.max":"1",
        "topics":"my_topic_users"
    }
}
```
curl -X POST -d @- http://localhost:8083/connectors --header "content-Type:application/json"

- 싱크 커넥트는 토픽에서 데이터를 가져와서 사용하는 사용처
- topics의 value가 사용처가 된다. 따라서 현재 설정은 mydb에 my_topic_users라는 테이블이 생성된다.
- auto.create: 토픽과 같은 이름의 테이블을 생성해주겠다는 옵션
- 스키마는 토픽에 저장된 메시지를 바탕으로 결정된다.

![image](https://user-images.githubusercontent.com/83503188/194768551-fd3cc62e-6a10-45f5-832f-bfc5e9a9f0c9.png)
- 데이터를 insert하면 새로운 테이블이 생성됨을 확인할 수 있다.

**kafka producer를 이용해서 Kafka Topic에 데이터 직접 전송**
- kafka-console-producer에서 데이터 전송 -> Topic에 추가 -> MariaDB에 추가

![image](https://user-images.githubusercontent.com/83503188/194768743-4fa6139c-91b2-44ca-9d92-3be84ae1894d.png)

![image](https://user-images.githubusercontent.com/83503188/194768724-3d28ee4e-7137-493a-8a77-f1fb0358bd0b.png)

