FROM python:alpine
RUN apk add sed
RUN pip install tavern[pytest]

WORKDIR /tests

COPY . /tests

CMD ["/bin/sh", "test_runner.sh"]