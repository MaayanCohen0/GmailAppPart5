const { findUserByUsername } = require("../models/usersModel");
const { getUserById } = require("../models/usersModel");
// { searchAllLabelsArray } = require("../models/labelsModel");
const mailModel = require("../models/mailsModel");
const draftModel = require("../models/draftsModel");
const trashModel = require("../models/trashModel");
const spamModel = require("../models/spamModel")


exports.determineObjectType = (req, res) => {
    const userId = req.userId;
    const id = req.params.id;
    if (!userId) {
        return res.status(401).json({ error: "User ID is required" });
    }

    const user = getUserById(userId);
    if (!user) {
        return res.status(404).json({ error: "User not found" });
    }

    const username = user.username;

    if (mailModel.getMailById(id,username)) {
        return res.status(200).json({message: "mails"});
    }  
    else if (draftModel.getDraftById(id,username)) {
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

