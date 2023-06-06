FROM tomcat
COPY target/alertmns-api-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/alertmns-api-0.0.1-SNAPSHOT.war
ENTRYPOINT ["/bin/bash", "/usr/local/tomcat/bin/catalina.sh", "run"]