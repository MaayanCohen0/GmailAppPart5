//const { findUserByUsername } = require("../services/userService");
const { getUserById } = require("../services/userService");
// { searchAllLabelsArray } = require("../models/labelsModel");
const mailService = require("../services/mailService");
const draftService = require("../services/draftService");

// CHANGE IT TO SERVICES
const trashModel = require("../models/trashModel");
const spamModel = require("../models/spamModel");


exports.determineObjectType = async (req, res) => {
    const userId = req.userId;
    const id = req.params.id;
    if (!userId) {
        return res.status(401).json({ error: "User ID is required" });
    }

    const user = await getUserById(userId);
    if (!user) {
        return res.status(404).json({ error: "User not found" });
    }

    const username = user.username;

    if (await mailService.getMailById(id,username)) {
        return res.status(200).json({message: "mails"});
    }  
    else if (await draftService.getDraftById(id,username)) {
        return res.status(200).json({message: "drafts"});
    }
    else if (spamModel.getSpamById(id, username)) {
        return res.status(200).json({message: "spam"});
    }
    else if (trashModel.getTrashById(id, username)) {
        return res.status(200).json({message: "trash"});
    }
    else {
        return res.status(404).json({error: null})
    }
}

