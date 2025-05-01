# 상담 채팅 서비스

실시간 상담 채팅 서비스를 제공하는 Spring Boot 애플리케이션입니다.
WebSocket과 Redis를 활용한 실시간 채팅과 상담 관리 기능을 제공합니다.

## 기술 스택

- **Backend**: Java 17, Spring Boot 3.4.4
- **Database**: MySQL, Redis
- **인증**: JWT (JSON Web Token)
- **실시간 통신**: WebSocket (STOMP)
- **빌드 도구**: Gradle

## 주요 기능

- 추가예정

### Redis 실행

Redis 서버는 Docker를 사용하여 실행

```bash
# Redis 컨테이너 실행
docker run --name redis -p 6379:6379 -d redis redis-server --requirepass your_secure_password_2025!xAiRedisCounselingService

# 상태 확인
docker ps

# 중지
docker stop redis

# 재시작
docker start redis

# 삭제
docker rm redis
```

### MySQL 실행

MySQL 서버는 Docker를 통해 실행

```bash
# MySQL 컨테이너 실행
docker run --name mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=counsel_db -p 3306:3306 -d mysql:8.0

# 상태 확인
docker ps

# 로그 확인
docker logs mysql

# 중지
docker stop mysql

# 재시작
docker start mysql

# 삭제
docker rm mysql
```

### MySQL 접속

MySQL 데이터베이스 접속정보

```bash
MySQL Workbench, DBeaver, DataGrip 등 DB 접속 도구 설정:

호스트(Host): localhost 또는 127.0.0.1
포트(Port): 3306
사용자명(Username): root
비밀번호(Password): password 
데이터베이스(Database): counsel_db
```