const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// Create and connect to the database
mongoose.connect('mongodb+srv://inventoryUser:Test123@cluster0.qkkzdvg.mongodb.net/?appName=Cluster0')
    .then(() => console.log("Connected to MongoDB Atlas"))
    .catch(err => console.error("MongoDB connection error:", err));

const itemSchema = new mongoose.Schema({
    UPC: { type: String, required: true, unique: true },
    description: String,
    quantity: Number
});
const Item = mongoose.model('Item', itemSchema);

// Get all items
app.get('/items', async (req, res) => {
    try {
        const items = await Item.find();
        res.json(items);
    } catch (err) {
        res.status(500).json(err);
    }
});

// Get an item by UPC
app.get('/items/:upc', async (req, res) => {
    try {
        const item = await Item.findOne({ UPC: req.params.upc });
        res.json(item);
    } catch (err) {
        res.status(500).json(err);
    }
});

// Add an item
app.post('/items', async (req, res) => {
    const { UPC, description, quantity } = req.body;

    if (!UPC) return res.status(400).json({ error: "UPC required" });

    try {
        const newItem = new Item({ UPC, description, quantity });
        await newItem.save();
        res.json({ message: "Item added", UPC });
    } catch (err) {
        res.status(400).json({ error: "Item may already exist" });
    }
});

// Update item
app.put('/items/:upc', async (req, res) => {
    const { description, quantity } = req.body;

    try {
        const result = await Item.updateOne(
            { UPC: req.params.upc },
            { description, quantity }
        );

        res.json({ message: "Item updated", changes: result.modifiedCount });
    } catch (err) {
        res.status(500).json(err);
    }
});

// Delete an item
app.delete('/items/:upc', async (req, res) => {
    try {
        const result = await Item.deleteOne({ UPC: req.params.upc });
        res.json({ message: "Item deleted", changes: result.deletedCount });
    } catch (err) {
        res.status(500).json(err);
    }
});

// Start the server
const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});