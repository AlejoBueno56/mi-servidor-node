const express = require("express");
const { Pool } = require("pg");
const bcrypt = require("bcryptjs");  // 🔹 Usamos bcryptjs en vez de bcrypt

const app = express();
const PORT = process.env.PORT || 10000;

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

// 📌 Ruta para registrar usuario (con /api/)
app.post("/api/register", async (req, res) => {
  const { nombre, correo, password } = req.body;

  if (!nombre || !correo || !password) {
    return res.status(400).json({ success: false, message: "Faltan datos" });
  }

  try {
    // Encriptar la contraseña
    const hashedPassword = await bcrypt.hash(password, 10);

    // Insertar usuario en la base de datos
    await pool.query(
      "INSERT INTO usuarios (nombre, correo, password, creado_en) VALUES ($1, $2, $3, NOW())",
      [nombre, correo, hashedPassword]
    );

    res.json({ success: true, message: "Usuario registrado exitosamente" });
  } catch (error) {
    console.error("❌ Error al registrar usuario:", error);
    res.status(500).json({ success: false, message: "Error en el servidor" });
  }
});

// 📌 Ruta para iniciar sesión (con /api/)
app.post("/api/login", async (req, res) => {
  const { correo, password } = req.body;

  if (!correo || !password) {
    return res.status(400).json({ success: false, message: "Faltan datos" });
  }

  try {
    const result = await pool.query("SELECT * FROM usuarios WHERE correo = $1", [correo]);

    if (result.rows.length > 0) {
      const user = result.rows[0];

      // Comparar contraseña encriptada
      const isMatch = await bcrypt.compare(password, user.password);
      if (isMatch) {
        res.json({ success: true, message: "Inicio de sesión exitoso" });
      } else {
        res.status(401).json({ success: false, message: "Contraseña incorrecta" });
      }
    } else {
      res.status(404).json({ success: false, message: "Usuario no encontrado" });
    }
  } catch (error) {
    console.error("❌ Error en el login:", error);
    res.status(500).json({ success: false, message: "Error en el servidor" });
  }
});

// 📌 Ruta para obtener todos los usuarios (con /api/)
app.get("/api/usuarios", async (req, res) => {
  try {
    const result = await pool.query("SELECT id, nombre, correo, creado_en FROM usuarios");
    res.json(result.rows);
  } catch (error) {
    console.error("❌ Error al obtener usuarios:", error);
    res.status(500).json({ success: false, message: "Error en el servidor" });
  }
});

// 📌 Ruta de prueba para saber si el servidor está corriendo
app.get("/", (req, res) => {
  res.send("🚀 Servidor funcionando correctamente");
});

// Iniciar el servidor
app.listen(PORT, () => {
  console.log(`🚀 Servidor corriendo en el puerto ${PORT}`);
});

