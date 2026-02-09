# Порядок тестирования
- Будет поднят Docker-образ gradle:8-jdk21
- На нём установятся:
  * postgresql 16.1
  * gradle 8, jdk 21
- Далее из корня репозитория будет вызван скрипт:
```
gradle build --no-daemon
gradle run --no-daemon --args='--port 8080'
```