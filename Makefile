.DEFAULT_GOAL := jenkins_test

.PHONY: jenkins_test
jenkins_test:
	docker-compose up mvn

.PHONY: java_test
java_test:
	docker-compose run --rm mvn mvn clean test
