from flask import Flask, render_template

app = Flask(__name__)

@app.route("/")
def inicio():
    return render_template("/index.html")

@app.route("/base")
def ficha():
    return render_template("/base.html")

@app.route("/contacto")
def contacto():
    return render_template("/contacto.html")

@app.route("/registro")
def registro():
    return render_template("/registro.html")

@app.route("/login")
def login():
    return render_template("/login.html")

if __name__ == "__main__":
    app.run(debug=True)