// labels_controller.js
const Labels = require("../models/labelsModel");
const { getUserById } = require("../models/usersModel");

// exports.getLabels = (req, res) => {
//   //const userId = req.headers["user-id"];
//   const userId = req.userId;
//   if (!userId) {
//     return res.status(401).json({ error: "Valid user-id is required" });
//   }

//   const userById = getUserById(userId);

//   if (!userById) {
//     return res.status(404).json({ error: "User with this user-id not found" });
//   }

//   const userLabels = Labels.getLabels(userId);
//   res.json(userLabels);
// };

exports.getLabels = (req, res) => {
  const userId = req.userId;
  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = getUserById(userId);

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  // Extract limit query parameter
  const { limit } = req.query;

  try {
    let userLabels = Labels.getLabels(userId);

    // Apply limit if provided
    if (limit) {
      const limitNum = parseInt(limit);
      if (isNaN(limitNum) || limitNum <= 0) {
        return res.status(400).json({
          error: "Invalid limit value. Must be a positive number",
        });
      }
      userLabels = userLabels.slice(0, limitNum);
    }

    res.json(userLabels);
  } catch (e) {
    return res.status(500).json({
      error: "Failed to fetch labels",
      detail: e.message || String(e),
    });
  }
};

exports.getLabelById = (req, res) => {
  const id = req.params.id;
  const userId = req.userId; // comes from the token

  //const userId = req.headers["user-id"];

  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = getUserById(userId);

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  if (!id) {
    return res.status(400).json({ error: "Invalid label ID" });
  }

  const label = Labels.getLabelByIdWithoutUserId(id);
  if (!label) {
    return res.status(404).json({ error: "Label not found" });
  }

  if (label.userId !== userId) {
    return res
      .status(403)
      .json({ error: "You are not authorized to update this label" });
  }
  res.json(label);
};

exports.addLabel = (req, res) => {
  const { name } = req.body;
  const userId = req.userId; // comes from the token
  //const userId = req.headers["user-id"];

  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = getUserById(userId);

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  if (!name) {
    return res.status(400).json({ error: "Name of label is required" });
  }
  if (typeof name !== "string" || name.trim() === "") {
    return res.status(400).json({ error: "Valid name is required" });
  }
  if (
    Labels.getLabels(userId).some(
      (label) => label.name.toLowerCase() === name.toLowerCase()
    )
  ) {
    return res
      .status(409)
      .json({ error: "Label with this name already exists" });
  }

  try {
    const newLabel = Labels.addLabel(name, userId);
    res.status(201).location(`/api/labels/${newLabel.id}`).json(newLabel);
  } catch (error) {
    res.status(500).json({ error: "Failed to create label" });
  }
};

exports.updateLabel = (req, res) => {
  const id = req.params.id;
  //const userId = req.headers["user-id"];
  const userId = req.userId;
  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = getUserById(userId);

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  if (!id) {
    return res.status(400).json({ error: "Invalid label ID" });
  }
  const { name } = req.body;
  if (!name || name.trim() === "") {
    return res.status(400).json({ error: "Name must be provided for update" });
  }
  if (name.length > 255) {
    return res
      .status(400)
      .json({ error: "Name must be 255 characters or less" });
  }

  const label = Labels.getLabelByIdWithoutUserId(id);
  if (!label) {
    return res.status(404).json({ error: "Label not found" });
  }

  if (label.userId !== userId) {
    return res
      .status(403)
      .json({ error: "You are not authorized to update this label" });
  }

  if (Labels.getLabels(userId).some((label) => label.name === name)) {
    return res
      .status(409)
      .json({ error: "Label with this name already exists" });
  }

  const updatedLabel = Labels.updateLabel(
    id,
    { name },
    userId,
    userById.username
  );
  if (!updatedLabel) {
    return res.status(404).json({ error: "Label not found" });
  }
  res
    .status(200) // use 200 (OK) instead of 204 (No Content)
    .json(updatedLabel);
};

exports.deleteLabel = (req, res) => {
  const id = req.params.id;
  //const userId = req.headers["user-id"];
  const userId = req.userId;
  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = getUserById(userId);

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  if (!id) {
    return res.status(400).json({ error: "Invalid label ID" });
  }

  const label = Labels.getLabelByIdWithoutUserId(id);
  if (!label) {
    return res.status(404).json({ error: "Label not found" });
  }

  if (label.userId !== userId) {
    return res
      .status(403)
      .json({ error: "You are not authorized to update this label" });
  }

  const deleted = Labels.deleteLabel(id, userId, userById.username);
  if (!deleted) {
    return res.status(404).json({ error: "Label not found" });
  }
  res.status(204).json({ deletedId: id, message: "Label deleted" });
};
