const Label = require("../models/labelsModel");

const getLabels = async (userId, limit = null) => {
  const query = Label.find({ userId });
  if (limit) query.limit(limit);
  return await query.exec();
};

const getLabelById = async (id) => {
  return await Label.findById(id).exec();
};

const addLabel = async (name, userId) => {
  const exists = await Label.findOne({
    name: { $regex: new RegExp(`^${name}$`, "i") },
    userId,
  }).exec();

  if (exists) throw new Error("Label with this name already exists");

  const newLabel = new Label({ name, userId });
  return await newLabel.save();
};

const updateLabel = async (id, name, userId) => {
  const label = await Label.findById(id).exec();
  if (!label) throw new Error("Label not found");
  if (label.userId !== userId) throw new Error("Unauthorized");

  const conflict = await Label.findOne({
    _id: { $ne: id },
    name,
    userId,
  });
  if (conflict) throw new Error("Label with this name already exists");

  label.name = name;
  return await label.save();
};

const deleteLabel = async (id, userId) => {
  const label = await Label.findById(id).exec();
  if (!label) throw new Error("Label not found");
  if (label.userId !== userId) throw new Error("Unauthorized");

  await label.deleteOne();
  return true;
};

module.exports = {
  getLabels,
  getLabelById,
  addLabel,
  updateLabel,
  deleteLabel,
};
