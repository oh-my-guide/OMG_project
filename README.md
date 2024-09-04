# 제목 없음

# OMG (Oh My Guide)

**목차**

1. [프로젝트 개요 (Overview)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
2. [기술 스택 (Tech Stack)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
3. [설치 및 실행 방법 (Installation & Setup)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
4. [주요 기능 (Features)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
5. [프로젝트 구조 (Project Structure)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
6. [데모 (Demo)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
7. [테스트 (Testing)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
8. [기여 방법 (Contributing)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
9. [라이선스 (License)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
10. [문제 해결 (Troubleshooting) 및 FAQ](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
11. [팀 소개 (Team)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
12. [향후 계획 (Future Work)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)
13. [기타 참고 자료 (Additional Resources)](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)

---

## 프로젝트 개요 (Overview)

OMG(Oh My Guide)는 사용자가 **여행을 계획**하고 **동행자를 모집**하며, **여행 후기를 공유**할 수 있는 종합 여행 지원 플랫폼입니다. 이 프로젝트는 사용자에게 맞춤형 여행 일정을 제공하고, 실시간 소통 기능을 통해 보다 원활하고 즐거운 여행 경험을 제공하도록 설계되었습니다.

- **문제 정의**:

  여행을 계획하는 과정은 복잡하고 시간 소모적입니다. 또한, 동행자를 찾고 함께 소통하는 것 또한 쉽지 않습니다. 여행 후기를 나누는 과정에서도 정보의 교류가 제한적일 수 있습니다.

- **해결 방안**:

  OMG는 사용자의 개인화된 여행 일정을 제안하고, 동행자를 모집할 수 있는 기능을 제공합니다. 또한, 실시간 채팅 및 커뮤니티 기능을 통해 사용자들이 여행 후기를 쉽게 공유하고 소통할 수 있도록 지원합니다.


---

## 기술 스택 (Tech Stack)

**프론트엔드**

- **Thymeleaf**: 서버 사이드 템플릿 엔진으로, 동적인 HTML 콘텐츠를 생성하여 사용자에게 제공.

**백엔드**

- **Java**: 애플리케이션 로직 구현을 위한 주요 언어.
- **Kafka**: 실시간 채팅 기능 구현을 위한 메시징 시스템.
- **Redis**: 알림 기능 및 세션 관리 용도로 사용되는 인메모리 데이터베이스.

**데이터베이스**

- **MySQL**: 데이터 저장 및 관리 용도로 사용하는 관계형 데이터베이스 관리 시스템.

**배포 및 인프라**

- **Docker**: 애플리케이션 및 서비스의 컨테이너화.
- **GithubAction**: CI/CD 파이프라인을 통한 자동화된 빌드 및 배포.
- **Jenkins**: CI/CD 파이프라인을 통한 자동화된 빌드 및 배포.

**기타**

- **IntelliJ IDEA**: 개발 환경으로 사용.
- **kakaomap API**: 여행 위치 정보 제공.
- **Tour API**: 여행지 관련 정보 제공.
- **Weather API**: 날씨 정보 제공.
- **Channel API**: 소통 및 알림 기능 제공.

---

## 설치 및 실행 방법 (Installation & Setup)

### 필수 설치 도구

- **Java 21 버전**: [Java 다운로드 링크](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)
- **Docker**: [Docker 설치 가이드](https://docs.docker.com/get-docker/)

### 실행 단계

1. **필수 도구 설치**: 위의 링크를 통해 Java 21 버전 및 Docker를 설치합니다.

2. **`docker-compose.yml` 파일 작성**: 프로젝트 디렉토리에 `docker` 폴더를 생성하고, 각 서비스에 맞는 하위 폴더와 `docker-compose.yml` 파일을 작성합니다.

    - **Kafka 폴더 및 `docker-compose.yml` 파일**

      `docker/kafka/docker-compose.yml` 파일 내용:

      ```yaml
      services:
        zookeeper:
          image: bitnami/zookeeper:latest
          ports:
            - "2181:2181"
          environment:
            - ALLOW_ANONYMOUS_LOGIN=yes
 
        kafka:
          image: bitnami/kafka:latest
          ports:
            - "9092:9092"
          environment:
            - KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181
            - ALLOW_PLAINTEXT_LISTENER=yes
            - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092
            - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
          depends_on:
            - zookeeper
      ```

    - **Redis 폴더 및 `docker-compose.yml` 파일**

      `docker/redis/docker-compose.yml` 파일 내용:

      ```yaml
      services:
        redis:
          image: redis:latest
          container_name: redis
          command: ["redis-server", "--requirepass", "1234"]
          ports:
            - "6379:6379"
          volumes:
            - ./redis-data:/data
      volumes:
        redis-data:
      ```

    - **MySQL 폴더 및 `docker-compose.yml` 파일**

      `docker/mysql/docker-compose.yml` 파일 내용:

      ```yaml
      services:
        vacation-db:
          image: mysql
          restart: always
          environment:
            MYSQL_ROOT_PASSWORD: "1234"
            MYSQL_DATABASE: "example"
            MYSQL_USER: "your_username"
            MYSQL_PASSWORD: "your_password"
          command:
            - "--character-set-server=utf8mb4"
            - "--collation-server=utf8mb4_unicode_ci"
          volumes:
            - "./database/init:/docker-entrypoint-initdb.d/"
            - "./database/datadir:/var/lib/mysql"
          platform: linux/x86_64
          ports:
            - "3306:3306"
      ```

3. **Docker 컨테이너 실행**: 터미널에서 각 서비스의 폴더로 이동하여 Docker Compose 명령어를 실행합니다.

   ```bash
   # Kafka 서비스 실행
   cd docker/kafka
   docker-compose up -d

   # Redis 서비스 실행
   cd ../redis
   docker-compose up -d

   # MySQL 서비스 실행
   cd ../mysql
   docker-compose up -d

---

## 주요 기능 (Features)

- **맞춤형 여행 일정 제공**: 사용자의 여행 선호에 맞춘 일정을 추천.
- **여행 크루 모집**: 동행자를 모집하고 커뮤니티 기능을 통해 소통.
- **일정 공유**: 친구나 가족과 여행 일정을 공유.
- **여행 플래너**: 여행 일정을 계획하고 관리할 수 있는 기능 제공.
- **여행 리뷰**: 여행 후기를 게시판에 공유하고 다른 사용자들과 소통.

---

## 프로젝트 구조 (Project Structure)

- **/src**: 소스 코드 디렉토리
    - **/domain**: 도메인 모델 정의
      - **/config**: 설정 파일들
      - **/controller**: 컨트롤러 레이어
      - **/service**: 서비스 레이어
          - **/impl**: 서비스 구현체
      - **/repository**: 데이터베이스 접근 레이어
      - **/dto**: 데이터 전송 객체
      - **/entity**: 엔티티 클래스
    - **/global**: 글로벌 설정 및 공통 유틸리티

---

## 데모 (Demo)

프로젝트의 주요 기능을 시연하는 [GIF 또는 비디오 링크](https://www.notion.so/7fc6cf4574554e988f683b6373234680?pvs=21)를 통해 OMG의 사용 방법을 확인할 수 있습니다.

데모가 제공되면, 실제 애플리케이션이 어떻게 작동하는지 시각적으로 보여줄 수 있습니다.

---

## 테스트 (Testing)

- **테스트 프레임워크**: JUnit, Mockito 등을 사용하여 단위 테스트와 통합 테스트를 수행합니다.
- **테스트 실행 방법**:
    - IntelliJ IDEA에서 테스트를 실행하거나, 터미널에서 다음 명령어로 실행할 수 있습니다:

    ```bash
    ./gradlew test
    ```


---

## 기여 방법 (Contributing)

- **이슈 제출**: 명확한 제목과 설명으로 이슈를 제출합니다.
- **Pull Request (PR) 작성**:
    - **제목 형식**: `[#[이슈번호]] 제목` 형식으로 작성합니다.
    - **설명**: 변경 사항과 관련된 정보를 상세히 기술합니다.
    - **체크리스트**: 코드 스타일 가이드 준수, 테스트 작성 등 확인.
- **코드 스타일 가이드**:
    - **Indentation**: 공백 4칸.
    - **Naming Conventions**: camelCase, PascalCase 등 사용.
    - **Braces**: 새 줄에 작성.
    - **Comments**: 주요 로직에 주석 추가.
    - **Error Handling**: 예외 처리 및 로그 작성.

---

## 라이선스 (License)

이 프로젝트는 [MIT 라이선스](https://www.notion.so/LICENSE) 하에 배포됩니다. 자세한 사항은 라이선스 파일을 참조하세요.

---

## 문제 해결 (Troubleshooting) 및 FAQ

- **자주 발생하는 문제**:
    - Docker 컨테이너가 시작되지 않는 경우: Docker 로그를 확인하고 필요한 설정을 검토하세요.
    - MySQL 연결 문제: 데이터베이스 설정과 사용자 인증 정보를 확인하세요.
- **FAQ**:
    - **Q**: 프로젝트를 실행하는데 필요한 필수 소프트웨어는 무엇인가요?**A**: Java 21, Docker, MySQL, Redis, Kafka가 필요합니다.
    - **Q**: 오류가 발생했을 때 어떻게 해결하나요?**A**: 오류 로그를 확인하고 문제를 추적하세요. 필요시 GitHub Issues를 통해 도움을 요청할 수 있습니다.

---

## 팀 소개 (Team)

- [전현진](https://github.com/HyeonJinJeon) - 팀장
- [곽유진](https://github.com/jinijavac) - 팀원
- [손설빈](https://github.com/seolbb) - 팀원
- [김혜주](https://github.com/kimoju01) - 팀원
- [박경서](https://github.com/kyongseo) - 팀원

---

## 향후 계획 (Future Work)

- **추가 기능 개발**: 새로운 여행지 추천 기능 및 사용자 리뷰 분석 알고리즘 개선.
- **성능 최적화**: 실시간 채팅 기능의 성능 향상 및 확장성 개선.
- **UI/UX 개선**: 사용자 경험을 향상시키기 위한 인터페이스 개편.

---

## 기타 참고 자료 (Additional Resources)

- [Java Documentation](https://docs.oracle.com/en/java/)
- Docker Documentation
- Thymeleaf Documentation
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- Redis Documentation

---