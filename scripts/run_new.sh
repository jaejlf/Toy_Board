#!/bin/bash

CURRENT_PORT=$(cat /home/ec2-user/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> 현재 실행 중인 포트 : ${CURRENT_PORT}."

if [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
  TARGET_PORT=8081
else
  echo "> No WAS is connected to nginx"
fi

TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

if [ ! -z ${TARGET_PID} ]; then
  echo "> ${TARGET_PORT} 에서 실행 중인 WAS 종료"
  sudo kill ${TARGET_PID}
fi

nohup java -jar \
       -Dserver.port=${TARGET_PORT}
       -Dspring.config.location=classpath:/application.yml,/home/ec2-user/app/application-secret.yml \
       -Dspring.profiles.active=prod \
       $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

echo "> 새로운 버전이 ${TARGET_PORT}에서 실행 중"
exit 0