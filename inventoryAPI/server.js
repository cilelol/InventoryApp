const express = require('express');
const sqlite3 = require('sqlite3').verbose();
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// Create and connect to the database
const db = new sqlite3.Database('./inventory.db');

// Create the database table
db.run(`CREATE TABLE IF NOT EXISTS inventory (
        UPC TEXT PRIMARY Key,
        description TEXT,
        quantity INTEGER)`);

// Get all items in the database
app.get('/items', (req, res) => {
    db.all("SELECT * FROM inventory", [], (err, rows) => {
        if (err) {
            return res.status(500).json(err);
        }
        res.json(rows);
    });
});


// Gey an item by the UPC
app.get('/items/:upc', (req, res) => {
    db.get("SELECT * FROM inventory WHERE UPC = ?", [req.params.upc], (err, row) => {
        if (err) return res.status(500).json(err);
        res.json(row);
    });
});

// Add an item into the database
app.post('/items', (req, res) => {
    const { UPC, description, quantity } = req.body;

    if (!UPC) return res.status(400).json({ error: "UPC required" });

    const sql = `
        INSERT INTO inventory (UPC, description, quantity)
        VALUES (?, ?, ?)
    `;

    db.run(sql, [UPC, description, quantity], function (err) {
        if (err) {
            return res.status(400).json({ error: "Item may already exist" });
        }
        res.json({ message: "Item added", UPC: UPC });
    });
});

// Update an item in the database
app.put('/items/:upc', (req, res) => {
    const { description, quantity } = req.body;

    const sql = `
        UPDATE inventory
        SET description = ?, quantity = ?
        WHERE UPC = ?
    `;

    db.run(sql, [description, quantity, req.params.upc], function (err) {
        if (err) return res.status(500).json(err);

        res.json({ message: "Item updated", changes: this.changes });
    });
});

// Delete an item in the database
app.delete('/items/:upc', (req, res) => {
    db.run("DELETE FROM inventory WHERE UPC = ?", [req.params.upc], function (err) {
        if (err) return res.status(500).json(err);

        res.json({ message: "Item deleted", changes: this.changes });
    });
});

// Start the server
const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});