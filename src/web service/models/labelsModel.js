

const mongoose = require("mongoose");

const labelSchema = new mongoose.Schema({
  name: { type: String, required: true },
  userId: { type: String, required: true },
});

module.exports = mongoose.model("Label", labelSchema);
