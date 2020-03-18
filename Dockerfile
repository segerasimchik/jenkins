FROM ubuntu
RUN apt-get install nginx -y
EXPOSE 80
CMD [ "tail -f/dev/null" ]