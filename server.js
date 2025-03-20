require("dotenv").config();
const express = require("express");
const bcrypt = require("bcrypt");
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

// Configurar el puerto dinámico para Render
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Servidor corriendo en el puerto ${PORT}`));


