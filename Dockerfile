FROM jboss-webserver30-tomcat8-openshift:latest

COPY target/tomcat-jdbc.war /opt/webserver/webapps/ROOT.war
COPY configuration/context.xml /opt/webserver/conf

EXPOSE 8080 9443 8778

USER jboss

CMD /usr/local/s2i/run
