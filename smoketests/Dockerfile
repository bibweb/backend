FROM python:alpine
RUN apk add sed
RUN apk add wget

RUN pip install tavern[pytest]

WORKDIR /tests

COPY . /tests
RUN mkdir -p /tests/out

CMD ["/bin/sh", "test_runner.sh"]