FROM ubuntu
RUN apt-get update && apt-get install nginx -y
RUN apt-get install -y nmap
EXPOSE 80
