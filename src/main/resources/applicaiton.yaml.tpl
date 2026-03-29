server:
  port: {{SERVER_PORT}}
  # graceful shutdown makes sure we have time to finnish any ongoing rest requests before terminating
  # default value will be 30s before terminating
  shutdown: graceful

spring:
  application:
    name: server-auth
  datasource:
    url: jdbc:postgresql://{{DB_HOST}}:{{DB_PORT}}/{{DB_NAME}}?prepareThreshold=0
    driver-class-name: org.postgresql.Driver
    username: {{DB_USERNAME}}
    password: {{DB_PASSWORD}}
    hikari.connectionTimeout: {{DB_HIKARI_CONNECTION_TIMEOUT}}
    hikari.idleTimeout: {{DB_HIKARI_IDLE_TIMEOUT}}
    hikari.maxLifetime: {{DB_HIKARI_MAX_LIFETIME}}
    hikari.maximumPoolSize: {{DB_HIKARI_MAXIMUM_POOL_SIZE}}

  liquibase:
    enabled: true
    change-log: classpath:db/changelog-master.xml

#springdoc:
#  api-docs:
#    path: /authentication-docs
#  swagger-ui:
#    path: /authentication-docs/swagger-ui-custom.html

logging.level:
  root: INFO
  liquibase: INFO