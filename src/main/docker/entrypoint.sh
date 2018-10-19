#!/bin/sh

echo "The application will start in ${JHIPSTER_SLEEP}s..." && sleep ${JHIPSTER_SLEEP}
echo "再试一次?"
echo "${CONSUL_SERVER_HOST}"
echo "${JHIPSTER_SLEEP}"
exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar "${HOME}/app.war --spring.cloud.consul.host=${CONSUL_SERVER_HOST}" "$@"
