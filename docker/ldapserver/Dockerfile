# LDAP server based on ApacheDS (http://directory.apache.org/apacheds/)
# https://hub.docker.com/r/dwimberger/ldap-ad-it/
# https://github.com/kwart/ldap-server
FROM dwimberger/ldap-ad-it:latest
#RUN apt-get update && apt-get install -y ldap-utils vim lsof less htop tree && rm -rf /var/lib/apt/lists/*
#RUN apk add --update openldap-clients bash lsof less htop vim mc && rm -fr /var/cache/apk/*
COPY users.ldif /ldap/users.ldif
CMD ["java", "-jar", "ldap-server.jar", "users.ldif", "-p", "389"]
EXPOSE 389
