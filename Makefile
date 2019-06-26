.DEFAULT_GOAL := tests

.PHONY: jenkins
jenkins:
	docker-compose up mvn

.PHONY: tests
tests:
	docker-compose run --rm mvn mvn clean test
