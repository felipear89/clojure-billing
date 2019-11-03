FROM clojure:openjdk-11-lein-2.9.1 AS build
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps
COPY . /usr/src/app
RUN mv "$(lein ring uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" billing.jar

FROM openjdk:11
COPY --from=build /usr/src/app/billing.jar /usr/src/app/billing.jar
EXPOSE 3000
ENTRYPOINT ["java","-jar","/usr/src/app/billing.jar"]
