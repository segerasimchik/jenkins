FROM ubuntu
RUN apt-get install nginx
EXPOSE 80
CMD [ "tail -f/dev/null" ]