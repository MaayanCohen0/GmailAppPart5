const blacklistService = require("../services/blacklistService");
const { getUserById } = require("../services/userService");

exports.getUrls = (req, res) => {
  const userId = req.userId;
  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }
  const userById = getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  res.json(blacklistService.getUrls());
};

exports.addToBlacklist = async (req, res) => {
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

  if (blacklistService.getUrls().some((item) => item.url === url)) {
    return res.status(409).json({ error: "URL already exists in blacklist" });
  }

  try {
    const result = await blacklistService.addToBlacklist(url);
    res.status(201).location(`/api/blacklist/${result.id}`).end();
  } catch (err) {
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
    const result = await blacklistService.removeFromBlacklist(id);
    if (!result) {
      return res.status(404).json({ error: "Url not found" });
    }
    res.status(204).end();
  } catch (err) {
    res
      .status(500)
      .json({ error: "Failed to reach blacklist server", detail: err.message });
  }
};
