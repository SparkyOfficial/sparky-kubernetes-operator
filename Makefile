# Makefile для Sparky Kubernetes Operator
# Makefile for Sparky Kubernetes Operator
# Мейкфайл для Sparky Kubernetes Operator

# Змінні
# Variables
# Переменные
IMAGE_NAME = sparky/sparky-kubernetes-operator
IMAGE_TAG = latest
NAMESPACE = kube-system

# Команди
# Commands
# Команды

.PHONY: build
build:
	mvn clean package -DskipTests
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) .

.PHONY: deploy-crd
deploy-crd:
	kubectl apply -f deploy/crd.yaml

.PHONY: deploy-operator
deploy-operator:
	kubectl apply -f deploy/operator.yaml

.PHONY: deploy
deploy: deploy-crd deploy-operator

.PHONY: clean
clean:
	kubectl delete -f deploy/operator.yaml || true
	kubectl delete -f deploy/crd.yaml || true

.PHONY: test
test:
	mvn test

.PHONY: install-deps
install-deps:
	mvn dependency:resolve

.PHONY: help
help:
	@echo "Доступні команди:"
	@echo "Available commands:"
	@echo "Доступные команды:"
	@echo "  build         - Зібрати проект та створити Docker-образ"
	@echo "  build         - Build the project and create Docker image"
	@echo "  build         - Собрать проект и создать Docker-образ"
	@echo "  deploy-crd    - Розгорнути Custom Resource Definition"
	@echo "  deploy-crd    - Deploy Custom Resource Definition"
	@echo "  deploy-crd    - Развернуть Custom Resource Definition"
	@echo "  deploy-operator - Розгорнути оператор"
	@echo "  deploy-operator - Deploy the operator"
	@echo "  deploy-operator - Развернуть оператор"
	@echo "  deploy        - Розгорнути CRD та оператор"
	@echo "  deploy        - Deploy CRD and operator"
	@echo "  deploy        - Развернуть CRD и оператор"
	@echo "  clean         - Видалити розгортання"
	@echo "  clean         - Remove deployment"
	@echo "  clean         - Удалить развертывание"
	@echo "  test          - Запустити тести"
	@echo "  test          - Run tests"
	@echo "  test          - Запустить тесты"
	@echo "  install-deps  - Встановити залежності"
	@echo "  install-deps  - Install dependencies"
	@echo "  install-deps  - Установить зависимости"
	@echo "  help          - Показати цю довідку"
	@echo "  help          - Show this help"
	@echo "  help          - Показать эту справку"