const express = require('express');
const { Pool } = require('pg');  // 🔹 Importar Pool de PostgreSQL

const app = express();
const PORT = process.env.PORT || 3000;  // 🔹 Usar puerto dinámico

// Middleware para procesar JSON
app.use(express.json());

// Configuración de conexión a PostgreSQL
const pool = new Pool({
  user: process.env.DB_USER || "postgres",
  host: process.env.DB_HOST || "database-diego.cbiw8sqka0hr.us-east-2.rds.amazonaws.com",
  database: process.env.DB_NAME || "database-diego",
  password: process.env.DB_PASS || "Diego.1005",
  port: process.env.DB_PORT || 5432,
});

// Ruta para manejar el login
app.post("/login", async (req, res) => {
  const { correo, password } = req.body;

  if (!username || !password) {
    return res.status(400).json({ success: false, message: "Faltan datos" });
  }

  try {
    const result = await pool.query(
      "SELECT * FROM usuarios WHERE correo = $1 AND password = $2",
      [correo, password]
    );

    if (result.rows.length > 0) {
      res.json({ success: true, message: "Inicio de sesión exitoso" });
    } else {
      res.status(401).json({ success: false, message: "Usuario o contraseña incorrectos" });
    }
  } catch (error) {
    console.error("❌ Error al conectar:", error);
    res.status(500).json({ success: false, message: "Error de conexión con la base de datos" });
  }
});

// Iniciar el servidor
app.listen(PORT, () => {
  console.log(`🚀 Servidor corriendo en el puerto ${PORT}`);
});
