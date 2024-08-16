
# 🏷️ 프로젝트 소개
![zzanggiljjuks](https://github.com/user-attachments/assets/394efca0-6631-4649-a30a-c330034e73d4)


- Challengers-BOD (Being Organizing Doing)
- 나에게 필요한 좋은 습관을 골라 도전하는 프로젝트입니다.

- 아침기상, 운동, 책읽기, 취미연습, 금연까지 나에게 필요한 작은 미션을 선택하여 평균 2주의 짧은 기간으로 부담없이 도전할 수 있습니다.
- 같은 목표를 가진 사람들과 함께 하며 인증을 진행합니다.

<div id="b">
 
# ⚙️ 개발 환경
* <img src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazonwebservices&logoColor=white">
* <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
* <img src="https://img.shields.io/badge/aws loadbalancing-8C4FFF?style=for-the-badge&logo=awselasticloadbalancing&logoColor=white">
* <img src="https://img.shields.io/badge/Amazon s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
* <img src="https://img.shields.io/badge/Route 53-8C4FFF?style=for-the-badge&logo=amazonroute53&logoColor=white">
* <img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white"><img src="https://img.shields.io/badge/1.24.0-515151?style=for-the-badge">
* <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"><img src="https://img.shields.io/badge/27.1.1-515151?style=for-the-badge">
* <img src="https://img.shields.io/badge/Docker Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white"><img src="https://img.shields.io/badge/2.29.1-515151?style=for-the-badge">
* <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white"><img src="https://img.shields.io/badge/17-515151?style=for-the-badge">
* <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/3.3.0-515151?style=for-the-badge">
* <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">
* <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"><img src="https://img.shields.io/badge/8.0.39-515151?style=for-the-badge">
* <img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white"><img src="https://img.shields.io/badge/7.4.0-515151?style=for-the-badge">
* <img src="https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white"><img src="https://img.shields.io/badge/3.2.13-515151?style=for-the-badge">
* <img src="https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=white">
* <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">

<div id="c">

 # [📆 개발 기간 24.07.17 ~24.08.21]

 
![스크린샷 2024-08-14 100942](https://github.com/user-attachments/assets/479c36f2-abb5-4b63-9d9d-fc8d4a8f9249)

![스크린샷 2024-08-14 100915](https://github.com/user-attachments/assets/07a69d79-44e2-4a79-bc3f-4c3f3c89ef4c)

<div id="d">
 
# 🖇️ 팀 구성
| 류승범 [팀장]                         | 김형석                         | 한해정                         | 김기남                         |
|-------------------------------|-------------------------------|-------------------------------|-------------------------------|
| [![류승범](https://github.com/W-llama.png)](https://github.com/W-llama) | [![김형석](https://github.com/Hyungs0703.png)](https://github.com/Hyungs0703) | [![한해정](https://github.com/HaejungHan.png)](https://github.com/HaejungHan) | [![김기남](https://github.com/kimankim0001.png)](https://github.com/kimankim0001) |
|<br> - AWS 배포 <br> - 도커 CI/CD <br> - 챌린지 생성 |<br> - JWT 인증인가 <br> - 소셜로그인 <br> - Spring Security <br> - Front |<br> - http -> https <br> - 사용자 챌린지인증 CRUD <br> - Email 인증 <br> - 성능 최적화 <br> - Front |<br> - Admin 챌린지 CRUD <br> - 동시성 제어 <br> - Front |

<div id="e">
  
# 📑 [NOTION - Challengers-B.O.D](https://teamsparta.notion.site/Challengers-B-O-D-Being-Organizing-Doing-3230b076e9804b948eb35a6473c0dcf3)

<div id="f">

# [📋서비스 아키텍처]
![image](https://github.com/user-attachments/assets/dce64481-782f-4ffe-9b58-7aac8ef8bc6f)


<div id="g">

# 📑 ERD DIAGRAM
![image (10)](https://github.com/user-attachments/assets/d26b0f2a-35c8-4991-8edb-0569fa80451a)

<div id="h">

# 🗂️ API 명세서



<div id="i">

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



