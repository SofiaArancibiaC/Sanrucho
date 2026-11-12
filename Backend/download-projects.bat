@echo off
echo Descargando microservicios Spring Boot...
echo.
echo Descargando eureka.zip...
curl -o eureka.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=eureka&groupId=cl.sanrucho&artifactId=cl-sanrucho-eureka&name=tienda-eureka&description=servicio-eureka&packageName=cl.sanrucho.eureka&packaging=jar&javaVersion=21&dependencies=cloud-eureka-server,devtools"
echo.
echo Descargando ms-usuarios.zip...
curl -o ms-usuarios.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-usuarios&groupId=cl.sanrucho&artifactId=cl-sanrucho-usuarios&name=tienda-usuarios&description=servicio-usuarios&packageName=cl.sanrucho.usuarios&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-catalogo.zip...
curl -o ms-catalogo.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-catalogo&groupId=cl.sanrucho&artifactId=cl-sanrucho-catalogo&name=tienda-catalogo&description=servicio-catalogo&packageName=cl.sanrucho.catalogo&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-pedidos.zip...
curl -o ms-pedidos.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-pedidos&groupId=cl.sanrucho&artifactId=cl-sanrucho-pedidos&name=tienda-pedidos&description=servicio-pedidos&packageName=cl.sanrucho.pedidos&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descarga completada.
pause
