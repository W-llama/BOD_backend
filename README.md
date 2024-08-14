# 🏷️ 프로젝트 소개
### Challengers-BOD
- Challengers-BOD (Being Organizing Doing)
- 나에게 필요한 좋은 습관을 골라 도전하는 프로젝트입니다.

- 아침기상, 운동, 책읽기, 취미연습, 금연까지 나에게 필요한 작은 미션을 선택하여 평균 2주의 짧은 기간으로 부담없이 도전할 수 있습니다.
- 동기부여를 높이기위해서 돈을걸고 내가 실천한 만큼 돌려받습니다.
- 실천할 때마다 환급 및 못지키면 벌금이 나옵니다.
- 같은 목표를 가진 사람들과 함께 하며 인증을 진행합니다.

# [📆 개발 기간]
<br>24.07.17 ~24.08.21

# [📑서비스 아키텍처]
![스크린샷 2024-08-12 173229](https://github.com/user-attachments/assets/65f00820-e76a-459e-9a6e-da4134a8404a)

# 🖇️ 팀 구성
| 류승범 [팀장]                         | 김형석                         | 한해정                         | 김기남                         |
|-------------------------------|-------------------------------|-------------------------------|-------------------------------|
| [![류승범](https://github.com/W-llama.png)](https://github.com/W-llama) | [![김형석](https://github.com/Hyungs0703.png)](https://github.com/Hyungs0703) | [![한해정](https://github.com/HaejungHan.png)](https://github.com/HaejungHan) | [![김기남](https://github.com/kimankim0001.png)](https://github.com/kimankim0001) |
|<br> - AWS 배포 <br> - 도커 CI/CD <br> - 챌린지 생성 |<br> - JWT 인증인가 <br> - 소셜로그인 <br> - Spring Security <br> - Front |<br> - 사용자 챌린지인증 CRUD <br> - Front <br> - Email 인증 <br> - 성능 최적화|<br> - Admin 챌린지 CRUD <br> - 동시성 제어 <br> - Front |

<div id="d">
 
# ⚙️ 개발 환경
* AWS : IAM, EC2, 로드밸런서, S3, Route53
* NginX : 1.24.0
* Docker: 27.1.1
* Docker-Compose :2.29.1
* JDK : 17
* SpringBoot : 3.3.0
* SpringSecurity
* MySQL : 8.0.39
* Redis : 7.4.0
* Vue.js : 3.2.13
<br>

<div id="e">

# 🔑 환경 변수
```
ENDPOINT= {database_url}
DATABASE_USERNAME= {your_database_username}
DATABASE_PASSWORD= {your_database_password}
DATABASE_NAME= {your_database_name}
JWT_SECRET_KEY= {your_key}
JWT_ACCESS_EXPIRE_TIME= 3600000
JWT_REFRESH_EXPIRE_TIME= 1209600000
NAVER_CLIENT_ID= {your_never_client_id}
NAVER_CLIENT_SECRET= {your_never_client_secret}
GOOGLE_CLIENT_ID= {your_google_client_id}
GOOGLE_CLIENT_SECRET= {your_google_client_secret}
ACCESS_KEY= {your_access_key}
SECRET_KEY= {your_secret_key}
BUCKET_NAME= {your_bucket_name}
REDIS_HOST= {your_redis_host}
REDIS_PORT= {your_redis_port}
REDIS_PASSWORD= {your_redis_password}
SMTP_ADDRESS= {your_smtp_address}
SMTP_PASSWORD= {your_smtp_password}
SMTP_PORT= {your_smtp_port}
SMTP_USERNAME= {your_smtp_username}
```
---
<br>

<div id="f">

# 📑 ERD DIAGRAM
![image](https://github.com/user-attachments/assets/27c50488-db91-4d25-96a6-fc149ce55aa7)

<br>

<div id="g">

# 🗂️ API 명세서
![API](https://github.com/user-attachments/assets/27804bba-520c-49fa-acfc-470aeb43940a)
