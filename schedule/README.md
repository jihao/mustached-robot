## start this app

* Spring Boot
> mvn clean package && java -jar target/*.war 

* STS IDE
> Run as "Spring Boot App"
  
Note: in order to run smoothly under IDE, I need add *src/main/webapp* (classic maven web app structure) as source folder, since "Static resources can be moved to /public (or /static or /resources or /META-INFO/resources) in the classpath root. Same for messages.properties (Spring Boot detects this automatically in the root of the classpath)."


### useful links when prototyping

 [http://projects.spring.io/spring-boot/docs/docs/howto.html](http://projects.spring.io/spring-boot/docs/docs/howto.html)

 [https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples)
