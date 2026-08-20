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
echo Descargando ms-inventario.zip...
curl -o ms-inventario.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-inventario&groupId=cl.sanrucho&artifactId=cl-sanrucho-inventario&name=tienda-inventario&description=servicio-inventario&packageName=cl.sanrucho.inventario&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-carrito.zip...
curl -o ms-carrito.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-carrito&groupId=cl.sanrucho&artifactId=cl-sanrucho-carrito&name=tienda-carrito&description=servicio-carrito&packageName=cl.sanrucho.carrito&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-pedidos.zip...
curl -o ms-pedidos.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-pedidos&groupId=cl.sanrucho&artifactId=cl-sanrucho-pedidos&name=tienda-pedidos&description=servicio-pedidos&packageName=cl.sanrucho.pedidos&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-pagos.zip...
curl -o ms-pagos.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-pagos&groupId=cl.sanrucho&artifactId=cl-sanrucho-pagos&name=tienda-pagos&description=servicio-pagos&packageName=cl.sanrucho.pagos&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-despacho.zip...
curl -o ms-despacho.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-despacho&groupId=cl.sanrucho&artifactId=cl-sanrucho-despacho&name=tienda-despacho&description=servicio-despacho&packageName=cl.sanrucho.despacho&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-notificaciones.zip...
curl -o ms-notificaciones.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-notificaciones&groupId=cl.sanrucho&artifactId=cl-sanrucho-notificaciones&name=tienda-notificaciones&description=servicio-notificaciones&packageName=cl.sanrucho.notificaciones&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-reportes.zip...
curl -o ms-reportes.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-reportes&groupId=cl.sanrucho&artifactId=cl-sanrucho-reportes&name=tienda-reportes&description=servicio-reportes&packageName=cl.sanrucho.reportes&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-reviews.zip...
curl -o ms-reviews.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-reviews&groupId=cl.sanrucho&artifactId=cl-sanrucho-reviews&name=tienda-reviews&description=servicio-reviews&packageName=cl.sanrucho.reviews&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-common.zip...
curl -o ms-common.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-common&groupId=cl.sanrucho&artifactId=cl-sanrucho-common&name=tienda-common&description=servicio-common&packageName=cl.sanrucho.common&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descargando ms-api-gateway.zip...
curl -o ms-api-gateway.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=4.1.0&baseDir=ms-api-gateway&groupId=cl.sanrucho&artifactId=cl-sanrucho-api-gateway&name=tienda-api-gateway&description=servicio-api-gateway&packageName=cl.sanrucho.api-gateway&packaging=jar&javaVersion=21&dependencies=web,data-jpa,lombok,postgresql,cloud-feign"
echo.
echo Descarga completada.
pause
