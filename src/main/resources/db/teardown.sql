SET REFERENTIAL_INTEGRITY FALSE; -- 비활성화
truncate table transaction_tb; -- truncate: 테이블 내용만 지우기
truncate table account_tb;
truncate table user_tb;
SET REFERENTIAL_INTEGRITY TRUE; -- 활성화
-- 컨트롤러 테스트 -> 다음과 같이