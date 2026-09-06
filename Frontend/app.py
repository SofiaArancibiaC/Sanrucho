from flask import Flask, render_template

app = Flask(__name__)

@app.route("/")
def inicio():
    return render_template("index.html")

@app.route("/base")
def ficha():
    return render_template("base.html")

@app.route("/admin-usuarios")
def admin_usuarios():
    return render_template("admin-usuarios.html")

@app.route("/admin")
def admin():
    return render_template("admin.html")

@app.route("/blog")
def blog():
    return render_template("blog.html")

@app.route("/blog/detalle-1")
def blog_detalle_1():
    return render_template("blog-detalle-1.html")

@app.route("/blog/detalle-2")
def blog_detalle_2():
    return render_template("blog-detalle-2.html")

@app.route("/carrito")
def carrito():
    return render_template("carrito.html")


if __name__ == "__main__":
    app.run(debug=True)