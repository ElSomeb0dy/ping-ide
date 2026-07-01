.PHONY: dev web api db-reset seed test

dev:
	npm run dev

web:
	npm --prefix apps/web run dev

api:
	docker compose up api db

db-reset:
	docker compose down -v
	docker compose up db

seed:
	mvn -f apps/api/pom.xml quarkus:dev -Dquarkus.profile=dev

test:
	npm --prefix apps/web run lint
	mvn -f apps/api/pom.xml test
