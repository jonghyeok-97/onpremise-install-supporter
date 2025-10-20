### Docker Engine
- 도커 엔진은 리눅스 컨테이너 기술을 기반

#### Docker Engine 구성 요소
1. Docker Daemon
- `dockerd`로 불리며, 도커 엔진의 핵심 백그라운드 프로세스.
- 컨테이너를 생성,관리,모니터링,삭제 작업을 수행
  - 이미지 빌드, 컨테이너 실행 및 관리, 네트워킹, 데이터 볼륨 관리 등 작업 처리
2. Docker Client
- `docker` 명령어를 통해 사용자와 도커 데몬 간의 인터페이스
3. Docker Image
- 애플리케이션과 필요한 모든 종속성을 포함한 불변의 파일 시스템
- 도커 이미지는 소프트웨어를 실행하기 위한 설치 프로그램 패키지
  - 한 번 빌드되면 변경되지 않음.
- 이미지는 여러 계층으로 구성, 각 계층은 이전 계층을 기반으로 함
  - 이미지 빌드 과정 중, 각 계층은 캐시로 활용될 수 있음. -> 동일한 계층이 이미 존재 하면, 캐싱
      ```angular2html
      FROM ubuntu:latest
      RUN apt-get update \
        && apt-get install build-ess...
      COPY main.c Makefile /src/
      WORKDIR /src
      RUN make build
      ```
4. Docker Container
- 도커 이미지 실행 상태, 리소스와 네트워크 설정 포함
- 각 컨테이너는 독립적, 데이터 변경, 경량화
5. Docker Registry
- 도커 이미지를 저장하고, 배포. 도커 허브 같은 퍼블릭 레지스트리도 있고, 프라이빗도 설정 가능.
- 즉, 중앙 저장소임

#### Run(실행)
- docker run = docker pull + docker create + docker start
  - 독립된 파일 시스템, 네트워크 인터페이스, PID공간 등 가지며, 네트워크 설정이나 볼륨 마운트 같은 추가 옵션 지정 가능.