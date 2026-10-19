### 1. Redis 사용처
- **공지사항 캐싱**: 공지사항 상세/요약 DTO 및 인덱스 ZSet 캐싱을 통한 DB 부하 감소


- **인기 주류 랭킹 캐싱**: 스케줄러로 집계된 기간별 인기 주류 랭킹 ZSet 저장 및 빠른 조회
- **실시간 주류 활동 점수 트래킹**: 리뷰/댓글/좋아요 이벤트 발생 시 기간별 활성 후보군 ZSet 점수 가산
  - 단위기간별 리뷰, 댓글, 좋아요 수로 점수를 산출해 TOP 10 인기 주류 랭킹을 집계하나, 매번 전체 주류에 대해 점수를 계산하면 DB 부하가 순간적으로 커짐.
  - 이벤트 기반으로 **변동이 발생한 주류만 점수를 가산** 하고 집계가 끝나면 통계 테이블에 저장, ZSet으로 순위 캐싱.


- **Redisson 분산 락**: 스케줄러 중복 실행 방지 등 분산 환경 동시성 제어
- **JWT Refresh Token 관리 (ZSet + Lua Script)**: 유저별 ZSet 단일 키와 Lua Script를 통한 원자적 세션 관리 및 동시 접속 기기(최대 3대) 제한


- **SSE 알림 라우팅 & 세션 관리**: 접속 WAS 추적 세션(`sse:session`) 및 타겟 서버 Pub/Sub(`sse:server`) 채널 발행


- **비밀번호 재설정 일회용 토큰**: 5분 TTL 기반의 비밀번호 재설정 인증 토큰 저장 및 검증
  - 영속성이 필요 없고, TTL 기반 자동 삭제가 가능하므로 Redis가 RDB보다 적절.
  - 연속 요청 시 Key-Value 덮어쓰기로 기존 링크 무효화 + 신규 링크 발급을 O(1)에 깔끔하게 처리 가능.


### 2. JWT Refresh Token 관리 개선: List 방식에서 ZSet + Lua Script 방식으로 전환

#### (1) 기존 방식 (List + String 역색인 + Redisson 분산 락)의 문제점
- **데이터 불일치 버그 (역색인 고아 데이터)**:
  - 동시 접속 제한(3대)을 초과하여 `LPOP`으로 토큰이 밀려나거나, 전체 로그아웃 시 `DEL userKey`를 해도 개별 역색인 String(`jwt:refresh:token:{token}`)이 남아있음.
  - 토큰 유효성 검사(`isRefreshTokenActive`)가 역색인의 `hasKey`만 조회했기 때문에, 밀려나거나 로그아웃된 세션이 계속 유효한 토큰으로 인정되는 심각한 결함 존재.
- **개별 토큰 만료 시간(TTL) 미반영**:
  - Redis List는 원소별 TTL을 지원하지 않아 새 기기 로그인 시 `EXPIRE`로 전체 List의 TTL이 연장되어 기존 기기들의 토큰 수명까지 연장됨.
- **분산 락 오버헤드**:
  - Redis 조작이 여러 단계(크기 확인, pop, push, 역색인 등록 등)로 나뉘어 있어 Redisson 분산 락 획득/반환으로 인한 네트워크 RTT 및 지연 발생.

#### (2) 개선된 구조 (ZSet 단일 키 + Lua Script 원자적 실행)
- **Key 구조**: `jwt:refresh:user:{userId}` (ZSet 단일 키)
- **Member**: `refreshToken` (문자열)
- **Score**: `만료 Timestamp` (`System.currentTimeMillis() + refreshTokenValidity`)

#### (3) Lua Script를 통한 원자적 처리 흐름
분산 락(Redisson Lock)을 사용하지 않고, Redis 싱글 스레드 특성을 활용하는 **Lua Script**로 단일 네트워크 요청 내에서 모든 작업을 원자적으로 처리합니다.

1. **토큰 등록 (`REGISTER_SCRIPT`)**:
   - `ZREMRANGEBYSCORE`: 현재 시각 기준으로 이미 만료된 토큰을 먼저 정리.
   - `ZADD`: 신규 발급된 토큰을 만료 Timestamp를 Score로 지정하여 추가.
   - `ZREMRANGEBYRANK`: 랭크 범위(0부터 `-4` 인덱스) 삭제를 통해 최대 기기 대수(3대)를 초과하는 오래된 토큰을 즉시 일괄 제거.
   - `EXPIRE`: 유저 Key 자체의 보존 TTL을 토큰 유효기간으로 갱신.

2. **토큰 로테이션 (`ROTATE_SCRIPT`)**:
   - `ZREM`: 이전 Refresh Token을 즉시 제거.
   - `ZREMRANGEBYSCORE`: 만료된 토큰 정리.
   - `ZADD`: 새로 발급된 Refresh Token 추가.
   - `ZREMRANGEBYRANK`: 초과분 제거.
   - `EXPIRE`: Key TTL 갱신.

3. **토큰 검증 및 로그아웃**:
   - **유효성 검사**: JWT Claim에서 추출한 `userId`를 바탕으로 `ZSCORE jwt:refresh:user:{userId} <token>`을 조회하여 Score가 존재하고 현재 시각보다 큰지 $O(1)$로 검증.
   - **전체 로그아웃**: `DEL jwt:refresh:user:{userId}` 한 줄로 해당 유저의 모든 활성 기기 토큰이 즉시 완벽하게 무효화됨.

#### (4) 전환 효과
- **완벽한 정합성 보장**: 단일 Key 관리로 역색인 누락 버그 및 고아 데이터 원천 차단.
- **성능 향상**: Redisson 분산 락(Lock 획득, 검증, 해제 등 다중 RTT)을 제거하고 Lua Script 1회 실행으로 왕복 시간 대폭 단축.
- **토큰별 독립 수명 관리**: Score 기반 만료 Timestamp 관리로 각 기기별 정확한 만료 시간 유지.



### 3. SSE 알림 라우팅 & 세션 관리 (타겟팅 Pub/Sub)
- **기존 방식의 문제점 (전체 브로드캐스트)**:
  - 단일 채널로 모든 서버에 메시지 전송 → 각 서버가 자신의 SSE 연결 목록을 전수 탐색(O(N))하므로 서버 수/접속자 활동 증가 시 리소스 낭비 발생.
- **개선된 라우팅 구조**:
  1. **접속 세션 추적**: 유저 SSE 연결 시 `sse:session:{userId}` = `{serverId}` (TTL 90초) 저장, 30초 주기 하트비트로 TTL 연장.
  2. **서버별 전용 채널 구독**: 각 WAS 인스턴스는 시작 시 자신 전용 채널(`sse:server:{serverId}`)만 구독.
  3. **타겟 발행 및 오프라인 최적화**:
     - 알림 발생 시 Redis에서 대상 유저의 `serverId`를 조회 (O(1)).
     - **오프라인 유저**: Redis Pub/Sub 발행 자체를 생략하여 불필요한 메시지 전송 및 트래픽 방지.
     - **온라인 유저**: 대상 WAS 채널(`sse:server:{targetServerId}`)로만 타겟 발행 → 해당 서버에서 수신자에게 즉시 SSE 전송.
