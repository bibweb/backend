FROM python:3.7.0-stretch
RUN pip install tavern[pytest]

WORKDIR /tests

COPY . /tests
RUN mkdir -p /tests/out

CMD ["/bin/sh", "test_runner.sh"]