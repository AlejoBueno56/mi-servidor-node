require("dotenv").config();
console.log("JWT_SECRET:", process.env.JWT_SECRET || "No definido"); // 🔴 Verifica si la variable está cargada

const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const { Pool } = require("pg");
const cors = require("cors");

const app = express();
app.use(express.json());
app.use(cors({
    origin: "*",  // Cambia "*" por la URL de tu frontend en producción
    methods: ["GET", "POST"],
    allowedHeaders: ["Content-Type", "Authorization"]
}));

// Configuración de la base de datos PostgreSQL
const pool = new Pool({
    user: process.env.DB_USER,
    host: process.env.DB_HOST,
    database: process.env.DB_NAME,
    password: process.env.DB_PASS,
    port: 5432,
    ssl: { rejectUnauthorized: false }
});

// Ruta raíz para verificar el funcionamiento del servidor
app.get("/", (req, res) => {
    res.send("¡Servidor funcionando correctamente en Render!");
});

// 🔹 Ruta de LOGIN con manejo de errores mejorado
app.post("/login", async (req, res) => {
    try {
        const { nombre_usuario, password } = req.body;
        
        console.log("Datos recibidos:", nombre_usuario, password); // 🔴 Log para depuración

        const result = await pool.query("SELECT * FROM usuarios WHERE nombre_usuario = $1", [nombre_usuario]);

        if (result.rows.length === 0) {
            return res.status(401).json({ error: "Usuario no encontrado" });
        }

        const user = result.rows[0];

        console.log("Usuario encontrado:", user); // 🔴 Log para depuración

        const match = await bcrypt.compare(password, user.password);

        if (!match) {
            return res.status(401).json({ error: "Contraseña incorrecta" });
        }

        const token = jwt.sign({ id: user.id }, process.env.JWT_SECRET, { expiresIn: "1h" });

        res.json({ token });

    } catch (error) {
        console.error("Error en /login:", error);
        res.status(500).json({ error: "Error del servidor" });
    }
});

// 🔹 Ruta de REGISTRO de usuario con contraseña hasheada
app.post("/register", async (req, res) => {
    try {
        const { nombre_usuario, password } = req.body;

        // Verificar si el usuario ya existe
        const userExists = await pool.query("SELECT * FROM usuarios WHERE nombre_usuario = $1", [nombre_usuario]);

        if (userExists.rows.length > 0) {
            return res.status(400).json({ error: "El usuario ya existe" });
        }

        // Hashear la contraseña antes de guardarla
        const saltRounds = 10;
        const hashedPassword = await bcrypt.hash(password, saltRounds);

        // Insertar en la base de datos
        await pool.query("INSERT INTO usuarios (nombre_usuario, password) VALUES ($1, $2)", [nombre_usuario, hashedPassword]);

        res.status(201).json({ message: "Usuario registrado con éxito" });

    } catch (error) {
        console.error("Error en /register:", error);
        res.status(500).json({ error: "Error del servidor" });
    }
});
app.get("/informacion", async (req, res) => {
    try {
        const result = await pool.query("SELECT * FROM informacion_empresa");
        res.json(result.rows);
    } catch (error) {
        console.error("Error en /informacion:", error);
        res.status(500).json({ error: "Error del servidor" });
    }
});
// Ruta para obtener promociones
app.get("/promociones", async (req, res) => {
    try {
        const result = await pool.query("SELECT promo FROM informacion_empresa LIMIT 1");
        res.json(result.rows[0] || { promo: "" });
    } catch (error) {
        console.error("Error obteniendo promociones:", error);
        res.status(500).json({ error: "Error del servidor" });
    }
});

// Ruta para obtener ventajas
app.get("/ventajas", async (req, res) => {
    try {
        const result = await pool.query("SELECT ventajas FROM informacion_empresa LIMIT 1");
        res.json(result.rows[0] || { ventajas: "" });
    } catch (error) {
        console.error("Error obteniendo ventajas:", error);
        res.status(500).json({ error: "Error del servidor" });
    }
});

// Ruta para actualizar promociones
app.post("/promociones", async (req, res) => {
    try {
        const { promo } = req.body;
        await pool.query("UPDATE informacion_empresa SET promo = $1 WHERE id = 1", [promo]);
        res.json({ message: "Promoción actualizada" });
    } catch (error) {
        console.error("Error actualizando promoción:", error);
        res.status(500).json({ error: "Error del servidor" });
    }
});

// Ruta para actualizar ventajas
app.post("/ventajas", async (req, res) => {
    try {
        const { ventajas } = req.body;
        await pool.query("UPDATE informacion_empresa SET ventajas = $1 WHERE id = 1", [ventajas]);
        res.json({ message: "Ventajas actualizadas" });
    } catch (error) {
        console.error("Error actualizando ventajas:", error);
        res.status(500).json({ error: "Error del servidor" });
    }
});
app.put("/update-password", async (req, res) => {
    try {
        const { nombre_usuario, oldPassword, newPassword } = req.body;

        // Verificar si el usuario existe
        const userQuery = await pool.query("SELECT * FROM usuarios WHERE nombre_usuario = $1", [nombre_usuario]);

        if (userQuery.rows.length === 0) {
            return res.status(404).json({ error: "Usuario no encontrado" });
        }

        const user = userQuery.rows[0];

        // Verificar si la contraseña actual es correcta
        const passwordMatch = await bcrypt.compare(oldPassword, user.password);
        if (!passwordMatch) {
            return res.status(401).json({ error: "Contraseña actual incorrecta" });
        }

        // Hashear la nueva contraseña
        const saltRounds = 10;
        const hashedPassword = await bcrypt.hash(newPassword, saltRounds);

        // Actualizar la contraseña en la base de datos
        await pool.query("UPDATE usuarios SET password = $1 WHERE nombre_usuario = $2", [hashedPassword, nombre_usuario]);

        res.json({ message: "Contraseña actualizada con éxito" });

    } catch (error) {
        console.error("Error en /update-password:", error);
        res.status(500).json({ error: "Error del servidor" });
    }
});
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Servidor corriendo en el puerto ${PORT}`));


