# 인터넷 네트워크

## IP(인터넷 프로토콜)

![image](https://user-images.githubusercontent.com/83503188/206897219-fe7528b7-0580-425f-8517-4c4873625943.png)

복잡한 인터넷망에서 메시지를 보내야 한다면 최소한의 규칙이 필요하다.

**IP 주소 부여**

![image](https://user-images.githubusercontent.com/83503188/206897251-162c4d8e-66f0-4ced-a25c-55c0e23b2f31.png)
- 메시지를 보내는 클라이언트와 메시지를 받는 서버는 IP 주소를 할당받는다.

**IP 역할**
- 지정한 IP 주소(IP Address)에 데이터 전달
- 패킷(Packet)이라는 통신 단위로 데이터 전달 

**IP 패킷 정보**
- 출발지 IP(100.100.100.1), 목적지 IP(200,200.200.2), ...

**클라이언트 패킷 전달**

![image](https://user-images.githubusercontent.com/83503188/206897364-6f2d87b3-83f7-4ad3-9afb-c61264fb6811.png)

**서버 패킷 전달**

![image](https://user-images.githubusercontent.com/83503188/206897395-97f137fc-dd7a-435a-b1a6-87f952f56f83.png)

인터넷망이 복잡하기 떄문에 클라이언트가 보낸 노드와 서버가 보낸 노드가 다를 수 있다.

**IP 프로토콜의 한계**

- 비연결성
  - 패킷을 받을 대상이 없거나 서비스 불능 상태여도 패킷 전송
- 비신뢰성
  - 중간에 패킷이 사라지면?
  - 패킷이 순서대로 안오면?
- 프로그램 구분
  - 같은 IP를 사용하는 서버에서 통신하는 애플리케이션이 둘 이상이면?

**대상이 서비스 불능, 패킷 전송**

![image](https://user-images.githubusercontent.com/83503188/206897529-38b89895-0f53-4311-b34e-24bc3e139734.png)

**패킷 소실**

![image](https://user-images.githubusercontent.com/83503188/206897548-b2fdbf03-9079-4d77-a36b-1395840e471e.png)

**패킷 전달 순서 문제 발생**

![image](https://user-images.githubusercontent.com/83503188/206897558-00e85b5a-93a7-4639-b78b-8c804b2160f2.png)

## TCP, UDP

대상이 서비스 불능, 패킷 전송 / 패킷 소실 / 패킷 전달 순서 문제를 해결하기 위해 TCP 등장

**인터넷 프로토콜 스택의 4계층**

![image](https://user-images.githubusercontent.com/83503188/206897683-cae10d49-12db-4010-ac6a-cd451182cd5a.png)

**프로토콜 계층**

![image](https://user-images.githubusercontent.com/83503188/206897735-6f80d43f-d12c-44eb-8d02-32a67db8f556.png)

- SOCKET 라이브러리를 통해 os 계층에 메시지를 전달하면 os 계층의 TCP 는 메시지에 TCP 정보를 감싼다.
- 이어서 전송 계층(TCP)에서 인터넷 계층(IP)로 메시지를 전달하여 IP 정보를 감싼다.
- 마지막으로 네트워크 인터페이스의 LAN 카드를 통해서 나갈때 Ethernet Frame(MAC 주소)이 포함되어 나간다.

**IP 패킷 정보**

![image](https://user-images.githubusercontent.com/83503188/206898036-a59b7817-31af-4886-8de2-81278b79853d.png)

**TCP/IP 패킷 정보**

기존의 IP 패킷의 문제를 해결하기 위해서 TCP/IP 패킷이 등장

![image](https://user-images.githubusercontent.com/83503188/206898007-58ddcc59-09c7-4435-896b-6641279bb4ab.png)
- 출발지 PORT 정보, 목적지 PORT 정보, 전송 제어 정보, 순서 정보, 검증 정보, ... 포함

**TCP 특징**

TCP(Transmission Control Protocol)은 전송 제어 프로토콜
- 신뢰할 수 있는 프로토콜
- 현재는 대부분 TCP 사용

- **연결지향 - TCP 3 way handshake(가상 연결)**
  - 클라이언트와 서버가 연결을 확인하고 메시지를 전달한다. 
  - 만약 메시지를 받는 대상이 서비스 불능 상태인 경우에 메시지를 보내지 않는다.
- **데이터 전달 보증**
  - 패킷이 중간에 소실되는 경우에 클라이언트가 알 수 있다.
- **순서 보장**

**TCP 3 way handshake**

![image](https://user-images.githubusercontent.com/83503188/206898206-057621bc-b4cd-4d52-b391-4b816bb92e37.png)
1. 클라이언트에서 서버로 접속 요청에 해당하는 SYN 패킷을 전달
2. SYN 패킷을 받은 서버에서 SYN, ACK 패킷을 클라이언트에 전달
3. 클라이언트에서는 SYN, ACK 패킷을 받고 수락을 의미하는 ACK 패킷을 서버에 전달

3 way handshake 를 통해 연결을 확인하고나면 데이터를 전송하기 시작한다. 최근에는 최적화가 되어서 3번 단계에서 ACK 를 보낼 때 데이터도 같이 전달한다.

**데이터 전달 보증**

![image](https://user-images.githubusercontent.com/83503188/206898396-6a296a4d-f8b0-466d-a036-74d245655b11.png)
- 클라이언트에서 데이터를 전송하면 서버에서는 데이터를 잘 받았음에 해당하는 응답 메시지를 보내준다.
- 따라서 클라이언트는 응답 메시지를 받음으로써 데이터 전달이 정상적으로 이뤄졌는지 확인할 수 있다.

**순서 보장**

![image](https://user-images.githubusercontent.com/83503188/206898467-e291b740-7c4a-492b-a026-131d762d84c4.png)
- 패킷의 순서가 맞지 않게 도작하는 경우에 순서가 잘못된 패킷부터 다시 보내라는 메시지를 전달

TCP 세그먼트에는 전송 제어 정보, 순서 정보, 검증 정보, ... 이 포함되어 있기 때문에 위와 같은 서비스가 가능하다.

**UDP 특징**

UDP(User Datagram Protocol)은 사용자 데이터그램 프로토콜
- 하얀 도화지에 비유(기능이 거의 없음)
- 연결지향 - TCP 3 way handshake X
- 데이터 전달 보증 X
- 순서 보장 X
- 데이터 전달 및 순서가 보장되지 않지만, 단순하고 빠름
- 정리
  - IP와 거의 같다. + **PORT** + 체크섬 정도만 추가
  - 애플리케이션에서 추가 작업 필요

## PORT

![image](https://user-images.githubusercontent.com/83503188/206898820-18430ef5-ea0d-4ced-9b37-c6b504d8acb5.png)

한번에 둘 이상을 실행하는 경우 서버에 대한 응답 패킷이 하나의 클라이언트 IP 로 도착하기 때문에 문제가 발생할 수 있다. 이를 해결하기 위해서 PORT 등장

**TCP/IP 패킷 정보**

![image](https://user-images.githubusercontent.com/83503188/206898910-00f40445-b1ba-4187-9422-770d33be1221.png)
- TCP 세그먼트의 정보 중에 출발지 PORT, 목적지 PORT 정보를 통해 해결할 수 있다.
- IP 를 통해 목적지 서버를 구분하고, PORT 를 통해 서버 내의 애플리케이션을 구분할 수 있다.

**PORT - 같은 IP 내에서 프로세스 구분**

![image](https://user-images.githubusercontent.com/83503188/206898968-53cc9123-c645-4316-bb14-015599046ff2.png)
- 0 ~ 65535 할당 가능
- 0 ~ 1023: 잘 알려진 포트, 사용하지 않는 것이 좋음
  - FTP - 20, 21
  - TELNET - 23
  - HTTP - 80
  - HTTPS - 443

## DNS

IP 는 기억하기 어렵고, IP 는 변경될 수 있기 때문에 DNS(Domain Name System)을 사용한다.

**DNS**
- 전화번호부
- 도메인 명을 IP 주소로 변환하는 시스템

**DNS 사용**

![image](https://user-images.githubusercontent.com/83503188/206899127-59ab2c73-7c09-4262-8a1e-2bd683af6daa.png)
- DNS 서버에 IP 에 해당하는 도메인 명을 등록한다.
- 이후에 서버로 요청을 보내는 경우 먼저 DNS 서버로 요청을 보내 도메인 명에 해당하는 IP 주소를 얻는다.
