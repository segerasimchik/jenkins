FROM ubuntu
RUN apt install nginx
EXPOSE 80
CMD [ "tail -f/dev/null" ]