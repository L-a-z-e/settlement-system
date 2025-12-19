# Settlement System - 정산 시스템 완전 구현

**Spring Boot 기반 대규모 정산 시스템**입니다. **배치 처리(Batch Processing)**를 활용한 **일괄 정산**, **API 연동**, **그룹 정산**, **유연한 정산 정책** 등을 구현하여 **복잡한 정산 로직**을 효율적으로 관리합니다. 전자상거래, 마켓플레이스 등에서 자주 사용되는 **실무 정산 시스템**입니다.

---

## 🎯 프로젝트 목표

| 목표 | 설명 |
|------|------|
| **배치 정산** | Spring Batch를 활용한 대규모 정산 자동화 |
| **정산 정책** | 유연한 정산 규칙 정의 및 적용 |
| **API 연동** | 외부 시스템과의 데이터 연동 |
| **그룹 정산** | 여러 판매자의 일괄 정산 처리 |
| **정산 상세** | 상세한 정산 내역 관리 및 추적 |
| **실무 패턴** | 정산 시스템의 실무 설계 패턴 학습 |

---

## 🛠 기술 스택

| 분야 | 기술 |
|------|------|
| **프레임워크** | Spring Boot 3.5.3 |
| **배치** | Spring Batch |
| **ORM** | Spring Data JPA |
| **데이터베이스** | MariaDB |
| **Java 버전** | 17 LTS |
| **빌드 도구** | Gradle |
| **설정 관리** | Spring DotEnv |

---

## 📦 프로젝트 구조

```
settlement-system/                              # 루트 프로젝트
│
├── build.gradle                                # 빌드 설정
├── settings.gradle                             # 프로젝트 설정
│
└── src/
    ├── main/
    │   ├── java/com/laze/settlementsystem/
    │   │   │
    │   │   ├── SettlementSystemApplication.java # 진입점
    │   │   │
    │   │   ├── domain/                         # 도메인 엔티티
    │   │   │   ├── ApiOrder.java              # API 주문 데이터
    │   │   │   ├── Customer.java              # 고객/판매자 정보
    │   │   │   ├── ServicePolicy.java         # 정산 정책
    │   │   │   ├── SettleDetail.java          # 정산 상세 내역
    │   │   │   ├── SettleGroup.java           # 정산 그룹
    │   │   │   └── repository/
    │   │   │       ├── ApiOrderRepository.java
    │   │   │       ├── CustomerRepository.java
    │   │   │       ├── SettleDetailRepository.java
    │   │   │       └── SettleGroupRepository.java
    │   │   │
    │   │   ├── detail/                        # 정산 상세 로직
    │   │   │   ├── SettleDetailService.java  # 상세 정산 서비스
    │   │   │   ├── SettleDetailController.java # 상세 조회 API
    │   │   │   └── SettleDetailRepository.java
    │   │   │
    │   │   ├── group/                         # 정산 그룹 관리
    │   │   │   ├── SettleGroupService.java   # 그룹 정산 서비스
    │   │   │   ├── SettleGroupController.java # 그룹 조회 API
    │   │   │   └── SettleGroupRepository.java
    │   │   │
    │   │   ├── generator/                     # 정산 데이터 생성
    │   │   │   └── SettleDataGenerator.java  # 정산 데이터 생성기
    │   │   │
    │   │   └── support/                       # 배치 지원 클래스
    │   │       ├── SettlementBatchConfig.java # 배치 설정
    │   │       ├── SettlementJobListener.java # 배치 리스너
    │   │       ├── SettleDetailProcessor.java # 정산 데이터 처리기
    │   │       ├── SettleDetailWriter.java    # 정산 데이터 저장
    │   │       └── SettleDetailReader.java    # 정산 데이터 읽기
    │   │
    │   └── resources/
    │       ├── application.yml                # 애플리케이션 설정
    │       ├── application-dev.yml            # 개발 환경 설정
    │       └── application-prod.yml           # 운영 환경 설정
    │
    └── test/
        └── java/com/laze/settlementsystem/
            ├── SettlementBatchTest.java       # 배치 테스트
            ├── SettleDetailServiceTest.java   # 상세 정산 테스트
            └── SettleGroupServiceTest.java    # 그룹 정산 테스트
```

---

## 🚀 빠른 시작

### 필수 요구사항

```bash
# Java 17+ 확인
java --version

# Gradle 확인
gradle --version

# MariaDB 설치 및 실행
# MariaDB 서비스가 실행 중인지 확인
```

### 프로젝트 설정 및 실행

**1단계: 환경 변수 설정**
```bash
# .env 파일 생성
cat > .env << EOF
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/settlement_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password
EOF
```

**2단계: 클론 및 빌드**
```bash
git clone https://github.com/L-a-z-e/settlement-system.git
cd settlement-system
gradle build
```

**3단계: 데이터베이스 생성**
```sql
CREATE DATABASE settlement_db;
```

**4단계: 애플리케이션 실행**
```bash
gradle bootRun

# 또는 JAR 파일로 실행
java -jar build/libs/settlement-system-0.0.1-SNAPSHOT.jar
```

**5단계: 정산 배치 실행**
```bash
# 배치 작업 실행 (일일 정산)
curl -X POST http://localhost:8080/api/batch/run-settlement-job
```

---

## 💡 핵심 개념

### 1. 정산 시스템이란?

**정산(Settlement)**은 판매자와 플랫폼 간의 **매출금 정산**을 자동화하는 시스템입니다.

```
주문 발생 → 결제 완료 → 배송 → 정산 대상 → 자동 정산
  ↓          ↓        ↓        ↓        ↓
 API      결제 DB   배송 상태  정산 규칙  정산 계산
```

### 2. 주요 흐름

```
1. API 주문 수집 (ApiOrder)
   ↓
2. 정산 정책 조회 (ServicePolicy)
   ↓
3. 정산 금액 계산 (SettleDetailProcessor)
   ↓
4. 정산 상세 생성 (SettleDetail)
   ↓
5. 정산 그룹 생성 (SettleGroup)
   ↓
6. 정산 완료 및 보고
```

### 3. 정산 정책(ServicePolicy)

```java
// 정산 정책 예시
- 판매 수수료율: 5%
- 정산 주기: 주 1회 (매주 금요일)
- 최소 정산 금액: 10,000원
- 선정산(미리 정산): 불가능
- 환불 수수료: 별도 계산
```

### 4. 정산 상세(SettleDetail)

```java
// 단일 주문에 대한 정산
- 주문 ID
- 고객 ID
- 주문 금액
- 수수료
- 정산 금액
- 정산 상태 (미정산/정산완료/환불)
- 정산 날짜
```

---

## 📊 데이터 모델

### 1. ApiOrder (API 주문)

```java
@Entity
@Table(name = "api_order")
public class ApiOrder {
    @Id
    private Long id;              // 주문 ID
    private Long customerId;      // 고객 ID
    private BigDecimal amount;    // 주문 금액
    private String status;        // 주문 상태
    private LocalDateTime createdAt;
}
```

### 2. Customer (고객/판매자)

```java
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    private Long id;              // 고객 ID
    private String name;          // 고객명
    private String email;         // 이메일
    private String bankAccount;   // 계좌번호
    private LocalDateTime createdAt;
}
```

### 3. ServicePolicy (정산 정책)

```java
@Entity
@Table(name = "service_policy")
public class ServicePolicy {
    @Id
    private Long id;
    private String policyName;       // 정책명
    private BigDecimal commissionRate; // 수수료율
    private BigDecimal minAmount;    // 최소 정산 금액
    private Integer settleCycleDays; // 정산 주기
    private LocalDateTime createdAt;
}
```

### 4. SettleDetail (정산 상세)

```java
@Entity
@Table(name = "settle_detail")
public class SettleDetail {
    @Id
    private Long id;
    @ManyToOne
    private ApiOrder apiOrder;           // 주문
    @ManyToOne
    private Customer customer;           // 고객
    @ManyToOne
    private ServicePolicy servicePolicy; // 정산 정책
    private BigDecimal orderAmount;      // 주문 금액
    private BigDecimal commissionAmount; // 수수료
    private BigDecimal settleAmount;     // 정산 금액
    private String status;               // 정산 상태
    @ManyToOne
    private SettleGroup settleGroup;     // 정산 그룹
    private LocalDateTime createdAt;
}
```

### 5. SettleGroup (정산 그룹)

```java
@Entity
@Table(name = "settle_group")
public class SettleGroup {
    @Id
    private Long id;
    private LocalDate settleDate;       // 정산 날짜
    private BigDecimal totalAmount;     // 총 정산 금액
    private Integer detailCount;        // 정산 건수
    private String status;              // 정산 상태
    @OneToMany(mappedBy = "settleGroup")
    private List<SettleDetail> details; // 정산 상세 목록
    private LocalDateTime createdAt;
}
```

---

## 🔧 Spring Batch 설정

### 1. 배치 작업 구성

```java
@Configuration
public class SettlementBatchConfig {
    
    @Bean
    public Job settlementJob(Step settlementStep) {
        return jobBuilderFactory.get("settlementJob")
                .start(settlementStep)
                .listener(jobListener())
                .build();
    }
    
    @Bean
    public Step settlementStep(
            ItemReader<ApiOrder> reader,
            ItemProcessor<ApiOrder, SettleDetail> processor,
            ItemWriter<SettleDetail> writer) {
        return stepBuilderFactory.get("settlementStep")
                .<ApiOrder, SettleDetail>chunk(100)  // 100건씩 처리
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
```

### 2. ItemReader (데이터 읽기)

```java
@Component
public class SettleDetailReader implements ItemReader<ApiOrder> {
    
    private final ApiOrderRepository apiOrderRepository;
    
    @Override
    public ApiOrder read() throws Exception {
        // 미정산 주문을 순차적으로 읽기
        return apiOrderRepository.findFirstByStatus("COMPLETED");
    }
}
```

### 3. ItemProcessor (데이터 처리)

```java
@Component
public class SettleDetailProcessor 
        implements ItemProcessor<ApiOrder, SettleDetail> {
    
    private final ServicePolicyRepository policyRepository;
    
    @Override
    public SettleDetail process(ApiOrder apiOrder) throws Exception {
        // 정산 정책 조회
        ServicePolicy policy = policyRepository.findDefault();
        
        // 정산 금액 계산
        BigDecimal commission = apiOrder.getAmount()
                .multiply(policy.getCommissionRate());
        BigDecimal settleAmount = apiOrder.getAmount()
                .subtract(commission);
        
        // SettleDetail 생성
        return SettleDetail.builder()
                .apiOrder(apiOrder)
                .orderAmount(apiOrder.getAmount())
                .commissionAmount(commission)
                .settleAmount(settleAmount)
                .status("PENDING")
                .build();
    }
}
```

### 4. ItemWriter (데이터 저장)

```java
@Component
public class SettleDetailWriter implements ItemWriter<SettleDetail> {
    
    private final SettleDetailRepository repository;
    
    @Override
    public void write(List<? extends SettleDetail> items) throws Exception {
        // 배치로 저장
        repository.saveAll(items);
    }
}
```

---

## 🎓 학습 경로

### 1주: 기초 설계
- [ ] 정산 시스템 개념 이해
- [ ] 도메인 엔티티 설계
- [ ] 데이터베이스 스키마 작성
- [ ] 저장소 계층 구현

### 2주: 비즈니스 로직
- [ ] 정산 정책 관리
- [ ] 정산 금액 계산 로직
- [ ] 그룹 정산 처리
- [ ] 상태 관리

### 3주: 배치 처리
- [ ] Spring Batch 기초
- [ ] ItemReader/Processor/Writer 구현
- [ ] 배치 작업 스케줄링
- [ ] 배치 모니터링

### 4주: API & 통합
- [ ] REST API 설계
- [ ] 정산 조회 기능
- [ ] 상세 정산 조회
- [ ] 시스템 통합 테스트

---

## 📈 정산 시나리오

### 시나리오 1: 일일 정산 배치

```
일일 정산 배치 실행 (매일 오전 3시)
   ↓
미정산 주문 수집 (상태: COMPLETED)
   ↓
정산 정책 조회
   ↓
주문별 정산 금액 계산
   ↓
SettleDetail 생성
   ↓
SettleGroup 생성
   ↓
정산 완료 알림
   ↓
DB 저장
```

### 시나리오 2: 판매자별 정산 조회

```
GET /api/settle-groups/{customerId}
   ↓
판매자의 정산 그룹 조회
   ↓
정산 상세 내역 조회
   ↓
JSON 응답
```

### 시나리오 3: 정산 상세 조회

```
GET /api/settle-details/{settleDetailId}
   ↓
해당 정산 내역 조회
   ↓
주문/고객/정책 정보 포함
   ↓
JSON 응답
```

---

## 🔍 주요 API 엔드포인트

### 배치 관리

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| **POST** | `/api/batch/run-settlement-job` | 정산 배치 실행 |
| **GET** | `/api/batch/job-history` | 배치 실행 이력 조회 |
| **GET** | `/api/batch/job-status/{jobId}` | 배치 상태 조회 |

### 정산 조회

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| **GET** | `/api/settle-groups` | 정산 그룹 목록 |
| **GET** | `/api/settle-groups/{id}` | 정산 그룹 상세 |
| **GET** | `/api/settle-groups/{customerId}/history` | 판매자 정산 이력 |
| **GET** | `/api/settle-details` | 정산 상세 목록 |
| **GET** | `/api/settle-details/{id}` | 정산 상세 조회 |

### 정산 정책

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| **GET** | `/api/policies` | 정산 정책 목록 |
| **GET** | `/api/policies/{id}` | 정산 정책 상세 |
| **POST** | `/api/policies` | 정산 정책 생성 |
| **PUT** | `/api/policies/{id}` | 정산 정책 수정 |

---

## 🧪 테스트

### 배치 테스트

```java
@SpringBatchTest
@SpringBootTest
public class SettlementBatchTest {
    
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    
    @Test
    public void testSettlementJob() throws Exception {
        // 배치 작업 실행
        JobExecution jobExecution = 
            jobLauncherTestUtils.launchJob();
        
        // 결과 확인
        assertEquals(BatchStatus.COMPLETED, 
                     jobExecution.getStatus());
    }
}
```

### 서비스 테스트

```java
@SpringBootTest
public class SettleDetailServiceTest {
    
    @Autowired
    private SettleDetailService service;
    
    @Test
    public void testCalculateSettlementAmount() {
        BigDecimal orderAmount = new BigDecimal("100000");
        BigDecimal commissionRate = new BigDecimal("0.05");
        
        BigDecimal result = service.calculateSettleAmount(
            orderAmount, commissionRate);
        
        assertEquals(new BigDecimal("95000"), result);
    }
}
```

---

## ⚙️ 설정 및 환경

### application.yml

```yaml
spring:
  application:
    name: settlement-system
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MariaDB103Dialect
  
  batch:
    jdbc:
      initialize-schema: always
    job:
      enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

### 환경 변수 (.env)

```bash
# 데이터베이스
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/settlement_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# 배치 설정
BATCH_JOB_CHUNK_SIZE=100
BATCH_JOB_SCHEDULE=0 0 3 * * *  # 매일 오전 3시
```

---

## 🔗 정산 상태 플로우

```
┌──────────────────────────────────┐
│     주문 생성 (PENDING)           │
└──────────────────────┬───────────┘
                       │
                       ▼
┌──────────────────────────────────┐
│   배송 완료 (SHIPPED)             │
└──────────────────────┬───────────┘
                       │
                       ▼
┌──────────────────────────────────┐
│  배송 완료 (COMPLETED)            │
│  정산 대상 상태 변경              │
└──────────────────────┬───────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
        ▼                             ▼
┌─────────────────┐        ┌──────────────────┐
│ 환불 요청       │        │ 정산 배치 실행   │
│ (REFUND)        │        │                  │
└─────────────────┘        └────────┬─────────┘
                                   │
                                   ▼
                        ┌──────────────────────┐
                        │ 정산 완료 (SETTLED)   │
                        └──────────────────────┘
```

---

## 💼 실무 고려사항

### 1. 성능 최적화

```java
// 배치 청크 크기 조정
.chunk(1000)  // 대량 데이터는 1000씩 처리

// 인덱스 활용
@Entity
public class SettleDetail {
    @Index(columnList = "customer_id, settle_date")
    // 판매자별 정산 조회 성능 향상
}
```

### 2. 에러 처리

```java
.faultTolerant()
    .skipLimit(100)
    .skip(DataAccessException.class)
    .retryLimit(3)
    .retry(OptimisticLockingFailureException.class)
```

### 3. 정산 검증

```java
// 정산 금액 검증
private boolean validateSettlementAmount(SettleDetail detail) {
    BigDecimal orderAmount = detail.getOrderAmount();
    BigDecimal settleAmount = detail.getSettleAmount();
    
    // 정산 금액이 주문 금액을 초과하면 안됨
    return settleAmount.compareTo(orderAmount) <= 0;
}
```

### 4. 감시(Audit) 추적

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SettleDetail {
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime modifiedAt;
    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String modifiedBy;
}
```

---

## 📋 체크리스트

### 개발 단계
- [ ] 도메인 엔티티 설계
- [ ] 데이터베이스 스키마 작성
- [ ] 저장소 계층 구현
- [ ] 비즈니스 로직 구현
- [ ] 배치 작업 구성
- [ ] REST API 개발

### 테스트 단계
- [ ] 단위 테스트
- [ ] 통합 테스트
- [ ] 배치 테스트
- [ ] 성능 테스트
- [ ] 부하 테스트

### 배포 단계
- [ ] 환경 설정
- [ ] 마이그레이션 스크립트
- [ ] 모니터링 설정
- [ ] 백업 전략
- [ ] 롤백 계획

---

## 🔧 트러블슈팅

### 1. 배치 작업이 실행되지 않음

```bash
# 문제: 배치 DB가 초기화되지 않음
해결: spring.batch.jdbc.initialize-schema: always 설정 확인

# 문제: 스케줄러 비활성화
해결: @EnableScheduling 어노테이션 확인
```

### 2. 정산 금액 계산 오류

```bash
# 문제: BigDecimal 정밀도 손실
해결: setScale() 사용하여 소수점 자릿수 지정

# 문제: 나눗셈 오류
해결: ROUND_HALF_UP 모드 사용
```

### 3. 성능 저하

```bash
# 문제: 대량 정산 데이터 처리 시 느림
해결: 
  1. 청크 크기 조정 (100 → 1000)
  2. 배치 INSERT 활용
  3. 인덱스 추가

# 문제: 쿼리 N+1 문제
해결: @EntityGraph 또는 fetch join 사용
```

---

## 📚 추가 학습 자료

### Spring Batch
- [Spring Batch 공식 문서](https://spring.io/projects/spring-batch)
- 청크 기반 처리 이해
- 파티셔닝 전략

### 정산 시스템 설계
- 분산 트랜잭션 관리
- 멱등성(Idempotency) 보장
- 장애 복구 전략

### 마켓플레이스 구축
- 정산 주기 관리
- 수수료 정책 다양화
- 정산 리포트 생성

---

## 📝 의존성

```gradle
// Spring Boot
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

// Spring Batch
implementation 'org.springframework.boot:spring-boot-starter-batch'
testImplementation 'org.springframework.batch:spring-batch-test'

// 데이터베이스
runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'

// 설정 관리
implementation 'me.paulschwarz:spring-dotenv:4.0.0'

// Lombok
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'

// 테스트
testImplementation 'org.springframework.boot:spring-boot-starter-test'
```
