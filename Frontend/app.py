from flask import Flask, render_template

app = Flask(__name__)

@app.route("/adminoffcanvas")
def admin_offcanvas():
    return render_template("_admin-offcanvas.html")

@app.route("/adminsidebar")
def admin_sidebar():
    return render_template("_admin-sidebar.html")

@app.route("/footer")
def footer():
    return render_template("_footer.html")

@app.route("/navbar")
def navbar():
    return render_template("_navbar.html")

@app.route("/adminproductoform")
def admin_producto_form():
    return render_template("_admin-producto-form.html")

@app.route("/adminproductos")
def admin_productos():
    return render_template("_admin-productos.html")

@app.route("/adminusuarioform")
def admin_usuario_form():
    return render_template("_admin-usuario-form.html")

@app.route("/adminusuarios")
def admin_usuarios():
    return render_template("_admin-usuarios.html")


#-------------------------------------------------------------------------------profe 

@app.route("/")
def home():
    return render_template("index.html")


@app.route("/productos")
def productos():
    return render_template("productos.html")


@app.route("/producto-detalle")
def producto_detalle():
    return render_template("producto-detalle.html")


@app.route("/carrito")
def carrito():
    return render_template("carrito.html")


@app.route("/login")
def login():
    return render_template("login.html")


@app.route("/registro")
def registro():
    return render_template("registro.html")


@app.route("/nosotros")
def nosotros():
    return render_template("nosotros.html")


@app.route("/blog")
def blog():
    return render_template("blog.html")


@app.route("/blog/detalle-1")
def blog_detalle_1():
    return render_template("blog-detalle-1.html")


@app.route("/blog/detalle-2")
def blog_detalle_2():
    return render_template("blog-detalle-2.html")


@app.route("/contacto")
def contacto():
    return render_template("contacto.html")


@app.route("/admin")
def admin():
    return render_template("admin.html")


if __name__ == "__main__":
    app.run(debug=True)
