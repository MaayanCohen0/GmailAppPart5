const blacklistModel = require("../models/blacklistModel");
const { getUserById } = require("../models/usersModel");

exports.getUrls = (req, res) => {
  const userId = req.userId;
  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }
  const userById = getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  res.json(blacklistModel.getUrls());
};

exports.addToBlacklist = async (req, res) => {
  // check that user is registered and logged in
  const userId = req.userId;
  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }
  const userById = getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const { url } = req.body;
  if (!url) return res.status(400).json({ error: "URL is required" });

  if (blacklistModel.getUrls().some((item) => item.url === url)) {
    return res.status(409).json({ error: "URL already exists in blacklist" });
  }

  try {
    const result = await blacklistModel.addToBlacklist(url);
    res.status(201).location(`/api/blacklist/${result.id}`).end();
  } catch (err) {
    //res.status(400).json({ detail: err.message });
    if (err.message.includes("400 Bad Request")) {
      return res.status(400).end();
    }
    res.status(500).json({
      error: "Failed to reach blacklist server",
      detail: err.message,
    });
  }
};

exports.removeFromBlacklist = async (req, res) => {
  // check that user is registered and logged in
  const userId = req.userId;
  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }
  const userById = getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const id = req.params.id;
  if (!id) {
    return res.status(400).json({ error: "Invalid URL ID" });
  }

  try {
    const result = await blacklistModel.removeFromBlacklist(id);
    if (!result) {
      return res.status(404).json({ error: "Url not found" });
    }
    res.status(204).end();
    //res.json({ result });
    // return res.status(200).json({
    //   message: { id },
    // });
  } catch (err) {
    res
      .status(500)
      .json({ error: "Failed to reach blacklist server", detail: err.message });
  }
};
