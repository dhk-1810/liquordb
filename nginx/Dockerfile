# Dockerfile
FROM nginx:alpine

# 기본 설정파일 삭제
RUN rm /etc/nginx/conf.d/default.conf
# 직접 만든 설정파일 주입
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

# -g: 글로벌, daemon off: background off = Foreground에서 실행
CMD ["nginx", "-g", "daemon off;"]