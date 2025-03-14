const express = require('express');
const { Pool } = require('pg');  // 🔹 Importar Pool de PostgreSQL

const app = express();
const port = 3000;

// Middleware para procesar JSON
app.use(express.json());

const pool = new Pool({
  user: "postgres",
  host: "database-diego.cbiw8sqka0hr.us-east-2.rds.amazonaws.com",
  database: "database-diego",
  password: "Diego.1005",
  port: 5432,
});

// Ruta para manejar el login
app.post("/login", async (req, res) => {
  const { username, password } = req.body;

  try {
    const result = await pool.query(
      "SELECT * FROM usuarios WHERE username = $1 AND password = $2",
      [username, password]
    );

    if (result.rows.length > 0) {
      res.json({ success: true, message: "Inicio de sesión exitoso" });
    } else {
      res.json({ success: false, message: "Usuario o contraseña incorrectos" });
    }
  } catch (error) {
    console.error("Error al conectar:", error);
    res.status(500).json({ success: false, message: "Error de conexión" });
  }
});

// Iniciar el servidor
app.listen(port, () => {
  console.log(`🚀 Servidor corriendo en http://localhost:${port}`);
});
