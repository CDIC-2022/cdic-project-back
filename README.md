## Scrooge Project

------

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) 

Spring 2.7.3

아두이노와 통신하여 전력 복구 버튼 on/off, 전력량을 아두이노로부터 받아와 안드로이드에 보내주는 역할을 합니다.

아마존 AWS 서비스를 이용하여 도메인 활용, RDS를 이용하여 데이터베이스 연동하였습니다.



------

### 활용 API

- 하루전력량 모니터링
  - /arduino/dayWatt
  - parameter : android mac address (JSON)
  - return : int dailyWatt
- 한달전력량 모니터링
  - /arduino/monthWatt
  - parameter : android mac address (JSON)
  - return : int monthWatt
- 한달전력 요금
  - /arduino/monthPayment
  - parameter : android mac address (JSON)
  - return : int monthPay
- 예측전력량
  - /arduino/expectWatt
  - parameter : android mac address (JSON)
  - return : int expectWatt
- 안드로이드-서버 복구 버튼 on/off
  - /arduino/restoreOnOff
  - parameter : android mac address (JSON)
  - return
    - 1 : 복구 버튼 on/off 성공
    - -1 : 복구 버튼 on/off 실패
- 안드로이드-서버 복구 버튼 현재 on/off 상태
  - /arduino/checkOnOff
  - parameter : android mac address (JSON)
  - return
    - 1 : On 상태
    - 0 : Off 상태
    - -1 : 통신 에러
- 아두이노-서버 통신 api (5초마다 아두이노로부터 전력량 확인 및 on/off 상태 전송)
  - /arduino/receiveCondition
  - parameter : android mac address (JSON)
  - return
    - 1 : on/off disconnect 연결 해제로 상태 변경
    - 0 : on/off connect 연결로 상태 변경
    - 2 : on/off 현재 상태 유지
- 아두이노-서버 통신 api (60초마다 아두이노로부터 전력량 확인 및 on/off 상태 전송)
  - /arduino/receiveAverageCondition
  - parameter : android mac address (JSON)
  - return
    - 1 : on/off disconnect 연결 해제로 상태 변경
    - 0 : on/off connect 연결로 상태 변경
    - 2 : on/off 현재 상태 유지

---

### 팀원 

- [Sungchan Cho](https://github.com/JoeSeongchan)
    - 서버, 아두이노 통신
- [SeuYoon Joo](https://github.com/JooSeuYoon)
    - 안드로이드, 서버
