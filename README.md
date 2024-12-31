# kafka-post

Kafka MSA Consumer + DB 동기화 역할을 수행하는 kafka-post 프로젝트입니다. <br>
현재 DB 동기화까지 수행을 완료한 상태입니다.

## 🌊 이 프로젝트의 로직은 다음과 같습니다.
1. Kafka Listener를 통해 kafka-user에서 발행한 메시지 수신
2. gRPC를 통해 kafka-user에 정확한 user 내용 요청 및 수신
3. 받은 데이터를 User Entity로 변환 후 DB 저장
4. ack 처리를 통해 Kafka 메시지 수동 커밋
