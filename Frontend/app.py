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

if __name__ == "__main__":
    app.run(debug=True)