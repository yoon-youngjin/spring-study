# 웹 서버, 웹 애플리케이션 서버

웹 서버(Web Server)
- HTTP 기반으로 동작
- 정적 리소스 제공, 기타 부가기능
- 정적(파일) HTML, CSS, JS, 이미지, 영상
- 예) NGINX, APACHE 

![image](https://user-images.githubusercontent.com/83503188/200546831-94b71d49-7429-4143-9137-44ca094f2dd8.png)

웹 애플리케이션 서버(WAS - Web Application Server)
- HTTP 기반으로 동작
- 웹 서버 기능 포함 + (정적 리소스 제공 가능)
- 프로그램 코드를 실행해서 애플리케이션 로직 수행
  - 동적 HTML, HTTP API(JSON)
  - 서블릿, JSP, 스프링 MVC
- 예) 톰캣(Tomcat), Jetty, Undertow

![image](https://user-images.githubusercontent.com/83503188/200547233-0ca9aa6d-2191-493a-b650-4f4d7d6910cf.png)

웹 서버, 웹 애플리케이션 서버(WAS) 차이 

- 웹 서버는 정적 리소스(파일), WAS는 애플리케이션 로직
- 사실은 둘의 용어도 경계도 모호함
  - 웹 서버도 프로그램을 실행하는 기능을 포함하기도 함
  - 웹 애플리케이션 서버도 웹 서버의 기능을 제공함
- 자바는 서블릿 컨테이너 기능을 제공하면 WAS
  - 서블릿 없이 자바코드를 실행하는 서버 프레임워크도 있음
- WAS는 애플리케이션 코드를 실행하는데 더 특화 

웹 시스템 구성 - WAS, DB
- WAS, DB 만으로 시스템 구성 가능
- WAS는 정적 리소스, 애플리케이션 로직 모두 제공 가능

![image](https://user-images.githubusercontent.com/83503188/200548237-8db23419-335f-4313-ac4a-39f5ccad6179.png)

- WAS가 너무 많은 역할을 담당, 서버 과부하 우려
- 가장 비싼 애플리케이션 로직이 정적 리소스 때문에 수행이 어려울 수 있음
- WAS 장애시 오류 화면도 노출 불가능

![image](https://user-images.githubusercontent.com/83503188/200548506-6734ffda-bee3-4d38-8eb0-2a48d67898e3.png)

웹 시스템 구성 - WEB, WAS, DB
- 정적 리소스는 웹 서버가 처리
- 웹 서버는 애플리케이션 로직같은 동적인 처리가 필요하면 WAS에 요청을 위임
- WAS는 중요한 애플리케이션 로직 처리 담당

![image](https://user-images.githubusercontent.com/83503188/200549040-bcac7dc2-933f-4b96-b8e5-4318d9b3c7d9.png)

- 효율적인 리소스 관리
  - 정적 리소스가 많이 사용되면 Web 서버 증설
  - 애플리케이션 리소스가 많이 사용되면 WAS 증설

![image](https://user-images.githubusercontent.com/83503188/200549291-696b60b7-c9a3-4880-851c-cd4d1543c72b.png)

- 정적 리소스만 제공하는 웹 서버는 잘 죽지 않음
- 애플리케이션 로직이 동작하는 WAS 서버는 잘 죽음
- WAS, DB 장애시 WEB 서버가 오류 화면 제공 가능

![image](https://user-images.githubusercontent.com/83503188/200549469-b6972e28-09a5-4897-9a76-e986ec7aa45c.png)


서블릿

특징

![image](https://user-images.githubusercontent.com/83503188/200550784-be9cb2ab-dd52-42a4-8f40-34844d6e0c87.png)

- urlPatterns(/hello)의 URL이 호출되면 서블릿 코드가 실행
- HTTP 요청 정보를 편리하게 사용할 수 있는 HttpServletRequest
- HTTP 응답 정보를 편리하게 제공할 수 있는 HttpServletResponse
- 개발자는 HTTP 스펙을 매우 편리하게 사용

![image](https://user-images.githubusercontent.com/83503188/200551179-3a0303b4-980c-473d-a8d8-a85bd639fe71.png)

웹 브라우저에서 (localhost:8080/hello) 요청 WAS내에서 요청 메시지를 기반으로 request, response 객체를 새로 생성
새로 생성한 request, response객체를 파라미터로 넘기면서 helloServlet 실행
helloServlet이 종료되면 response객체를 기반으로 hhtp 응답 메시지를 생성 후 웹 브라우저에 전달한다.

HTTP 요청, 응답 흐름

- HTTP 요청시
  - WAS는 Request, Response 객체를 새로 만들어서 서블릿 객체 호출
  - 개발자는 Request 객체에서 HTTP 요청 정보를 편리하게 꺼내서 사용
  - 개발자는 Response 객체에 HTTP 응답 정보를 편리하게 입력
  - WAS는 Response 객체에 담겨있는 내용으로 HTTP 응답 정보를 생성

서블릿 컨테이너

서블릿 컨테이너가 서블릿 객체를 생성, 호출, 관리 

- 톰캣처럼 서블릿을 지원하는 WAS를 서블릿 컨테이너라고 함
- 서블릿 컨테이너는 서블릿 객체를 생성, 초기화, 호출, 종료하는 생명주기 관리
- 서블릿 객체는 싱글톤으로 관리
  - 고객의 요청이 올 때 마다 계속 객체를 생성하는 것은 비효율
  - 최초 로딩 시점에 서블릿 객체를 미리 만들어두고 재활용
  - 모든 고객 요청은 동일한 서블릿 객체 인스턴스에 접근
  - 공유 변수 사용 주의
  - 서블릿 컨테이너 종료시 함께 종료
- JSP도 서블릿으로 변환 되어서 사용
- 동시 요청을 위한 멀티 쓰레드 처리 지원

request, response 객체는 사용자의 요청마다 새로 생성되지만, servlet 객체는 싱글톤으로 관리

동시 요청 - 멀티 쓰레드

![image](https://user-images.githubusercontent.com/83503188/200555192-46c805ac-9684-40b0-a256-1173321b2e3f.png)

쓰레드가 호출! 

쓰레드

- 애플리케이션 코드를 하나하나 순차적으로 실행하는 것은 쓰레드
- 자바 메인 메서드를 처음 실행하면 main이라는 이름의 쓰레드가 실행
- 쓰레드가 없다면 자바 애플리케이션 실행이 불가능
- 쓰레드는 한번에 하나의 코드 라인만 수행
- 동시 처리가 필요하면 쓰레드를 추가로 생성

단일 요청 - 쓰레드 하나 사용

![image](https://user-images.githubusercontent.com/83503188/200556033-b62a38e4-4ed8-4a0e-829e-8b3244aa94b7.png)

![image](https://user-images.githubusercontent.com/83503188/200556085-1618a605-7c41-4c25-9705-63be2e5de9d5.png)

![image](https://user-images.githubusercontent.com/83503188/200556122-928be418-eb15-4c53-a845-5ce43197056a.png)

클라이언트 요청이 오면 쓰레드를 할당 -> 해당 쓰레드를 가지고 servlet을 실행 

다중 요청 - 쓰레드 하나 사용

![image](https://user-images.githubusercontent.com/83503188/200556274-8eba44e3-34e7-4b2a-98a5-15b80018858c.png)

![image](https://user-images.githubusercontent.com/83503188/200556296-be97bb8f-0ed2-4465-a650-e44a6cdf4324.png)

![image](https://user-images.githubusercontent.com/83503188/200556323-f71ce196-015a-486c-ab03-f61ea8b2442a.png)

요청1에 대해서 처리가 지연되는 경우에 요청2가 들어오면 쓰레드가 하나 뿐이므로 대기하는 상황이 생긴다. 

요청 마다 쓰레드 생성

![image](https://user-images.githubusercontent.com/83503188/200556635-9ce8dbd4-c56a-4a16-8925-a42ee2c5e649.png)

장단점

- 장점
  - 동시 요청을 처리할 수 있다.
  - 리소스(CPU, 메모리)가 허용할 때 까지 처리가능
  - 하나의 쓰레드가 지연 되어도, 나머지 쓰레드는 정상 동작한다.
- 단점
  - 쓰레드는 생성 비용은 매우 비싸다.
    - 고객의 요청이 올 때 마다 쓰레드를 생성하면, 응답 속도가 늦어진다.
  - 쓰레드는 컨텍스트 스위칭 비용이 발생한다.
  - 쓰레드 생성에 제한이 없다. -> 요청마다 쓰레드를 생성
    - 고객 요청이 너무 많이 오면, CPU, 메모리 임계점을 넘어서 서버가 죽을 수 있다.

이러한 문제를 해결하기 위해서 WAS는 대부분 내부에서 쓰레드 풀을 사용한다.

쓰레드 풀

![image](https://user-images.githubusercontent.com/83503188/200557345-75fe737b-e356-4580-9e4f-690a4662379f.png)

사용자의 요청이 오면 쓰레드 풀에서 가용할 수 있는 쓰레드를 가져와서 사용하고 반납한다.

![image](https://user-images.githubusercontent.com/83503188/200557652-8362782f-5c35-497d-b12f-9f3996a57aaa.png)

200개의 쓰레드를 만들어두고 200개의 쓰레드가 모두 사용중인데 이후에 요청이 들어오면 쓰레드 풀은 쓰레드를 달라는 요청을 대기하거나 거절한다.

- 특징
  - 필요한 쓰레드를 쓰레드 풀에 보관하고 관리한다.
  - 쓰레드 풀에 생성 가능한 쓰레드의 최대치를 관리한다. 톰캣은 최대 200개 기본 설정 (변경 가능)
- 사용
  - 쓰레드가 필요하면, 이미 생성되어 있는 쓰레드를 쓰레드 풀에서 꺼내서 사용한다.
  - 사용을 종료하면 쓰레드 풀에 해당 쓰레드를 반납한다.
  - 최대 쓰레드가 모두 사용중이어서 쓰레드 풀에 쓰레드가 없으면?
    - 기다리는 요청은 거절하거나 특정 숫자만큼만 대기하도록 설정할 수 있다.
- 장점
  - 쓰레드가 미리 생성되어 있으므로, 쓰레드를 생성하고 종료하는 비용(CPU)이 절약되고, 응답 시간이 빠르다.
  - 생성 가능한 쓰레드의 최대치가 있으므로 너무 많은 요청이 들어와도 기존 요청은 안전하게 처리할 수 있다.

실무 팁

- WAS의 주요 튜닝 포인트는 최대 쓰레드 수이다.
- 이 값을 너무 낮게 설정하면?
  - 동시 요청이 많으면, 서버 리소스는 여유롭지만, 클라이언트는 금방 응답 지연
- 이 값을 너무 높게 설정하면?
  - 동시 요청이 많으면, CPU, 메모리 리소스 임계점 초과로 서버 다운

![image](https://user-images.githubusercontent.com/83503188/200558606-b10138d1-405c-431c-a44b-35b813c7de4b.png)

핵심

- 멀티 쓰레드에 대한 부분은 WAS가 처리
- 개발자가 멀티 쓰레드 관련 코드를 신경쓰지 않아도 됨
- 개발자는 마치 싱글 쓰레드 프로그래밍을 하듯이 편리하게 소스 코드를 개발
- 멀티 쓰레드 환경이므로 싱글톤 객체(서블릿, 스프링 빈)는 주의해서 사용

HTML, HTTP API, CSR, SSR

전달하는 방식 3가지 

정적 리소스
- 고정된 HTML 파일, CSS, JS, 이미지, 영상 등을 제공
- 주로 웹 브라우저 

![image](https://user-images.githubusercontent.com/83503188/200559984-d022beb3-8566-4529-83e5-b6d2aa70f110.png)

HTML 페이지
- 동적으로 필요한 HTML 파일을 생성해서 전달
- 웹 브라우저: HTML 해석

![image](https://user-images.githubusercontent.com/83503188/200560057-b6753bb6-64f4-4629-b278-04052f118b12.png)
- JSP, 타임리프를 가지고 동적으로 HTML을 생성 후 반환 

HTTP API

- HTML이 아니라 데이터를 전달
- 주로 JSON 형식 사용
- 다양한 시스템에서 호출

![image](https://user-images.githubusercontent.com/83503188/200560259-e4079cfb-22da-4bd6-9cb5-d980233b108c.png)

- 다양한 시스템에서 호출
- 데이터만 주고 받음, UI 화면이 필요하면, 클라이언트가 별도 처리
- 앱, 웹 클라이언트, 서버 to 서버 

![image](https://user-images.githubusercontent.com/83503188/200560626-e7ce2767-fceb-45b8-bc8e-ee4552a43f9e.png)
- 크게 3가지 상황에서 사용 -> 웹 클라이언트 to 서버(ajax, fetch(), ...), 앱 클라이언트 to 서버, 서버 to 서버 

서버사이드 렌더링, 클라이언트 사이드 렌더링 

- SSR - 서버 사이드 렌더링
  - HTML 최종 결과를 서버에서 만들어서 웹 브라우저에 전달
  - 주로 정적인 화면에 사용
  - 관련기술: JSP, 타임리프 -> 백엔드 개발자
- CSR - 클라이언트 사이드 렌더링
  - HTML 결과를 자바스크립트를 사용해 웹 브라우저에서 동적으로 생성해서 적용
  - 주로 동적인 화면에 사용, 웹 환경을 마치 앱 처럼 필요한 부분부분 변경할 수 있음
  - 예) 구글 지도, Gmail, 구글 캘린더
  - 관련기술: React, Vue.js -> 웹 프론트엔드 개발자

SSR - 서버 사이드 렌더링
서버에서 최종 HTML을 생성해서 클라이언트에 전달 

![image](https://user-images.githubusercontent.com/83503188/200561808-02a34457-6d83-4ed8-94b1-cf7a906652dd.png)

CSR - 클라이언트 사이드 렌더링

![image](https://user-images.githubusercontent.com/83503188/200561865-6f859366-971a-467c-805c-8ea5a5dfbbd3.png)

자바 웹 기술 역사 

과거 기술

- 서블릿 - 1997
  - HTML 생성이 어려움
- JSP - 1999
  - HTML 생성은 편리하지만, 비즈니스 로직까지 너무 많은 역할 담당
- 서블릿, JSP 조합 MVC 패턴 사용
  - 모델, 뷰, 컨트롤러 역할을 나누어 개발
- MVC 프레임워크 - 2000년 초 ~ 2010년 초
  - MVC 패턴 자동화, 복잡한 웹 기술을 편리하게 사용 할 수 있는 다양한 기능 지원
  - 스트럿츠, 웹워크, 스프링 MVC(과거 버전)

현재 사용 기술

- 애노테이션 기반의 스프링 MVC 등장
  - @Controller
  - MVC 프레임워크의 춘추 전국 시대 마무리
- 스프링 부트의 등장
  - 스프링 부트는 서버를 내장
  - 과거에는 서버에 WAS를 직접 설치하고, 소스는 War 파일을 만들어서 설치한 WAS에 배포
  - 스프링 부트는 빌드 결과(Jar)에 WAS 서버 포함 -> 빌드 배포 단순화

빌드 결과(jar)에 톰캣 서버를 포함시켜서 서버에 WAS를 설치하지 않아도 된다. 
따라서 jar파일만 실행하면 서버가 뜬다. 

최신 기술 - 스프링 웹 기술의 분화

- Web Servlet - Spring MVC
- Web Reactive - Spring WebFlux

최신 기술 - 스프링 웹 플럭스(WebFlux)

- 특징
  - 비동기 넌 블러킹 처리
  - 최소 쓰레드로 최대 성능 - 쓰레드 컨텍스트 스위칭 비용 효율화
  - 함수형 스타일로 개발 - 동시처리 코드 효율화
  - 서블릿 기술 사용X
- 그런데
  - 웹 플럭스는 기술적 난이도 매우 높음
  - 아직은 RDB 지원 부족
  - 일반 MVC의 쓰레드 모델도 충분히 빠르다.
  - 실무에서 아직 많이 사용하지는 않음 (전체 1% 이하)

자바 뷰 템플릿 역사 

뷰 템플릿 - HTML을 편리하게 생성하는 뷰 기능

- JSP
  - 속도 느림, 기능 부족
- 프리마커(Freemarker), Velocity(벨로시티)
  - 속도 문제 해결, 다양한 기능
- 타임리프(Thymeleaf)
  - 내추럴 템플릿: HTML의 모양을 유지하면서 뷰 템플릿 적용 가능
  - 스프링 MVC와 강력한 기능 통합
  - 최선의 선택, 단 성능은 프리마커, 벨로시티가 더 빠름
