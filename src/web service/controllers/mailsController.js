//const { createMail } = require("../models/mailsModel");
const { findUserByUsername } = require("../models/usersModel");
const { getUserById } = require("../models/usersModel");
// { searchAllLabelsArray } = require("../models/labelsModel");
const mailModel = require("../models/mailsModel");
const draftModel = require("../models/draftsModel");
const labelsModel = require("../models/labelsModel");
//const { blacklistedUrls } = require("../blacklist/blacklistedUrls");

//function extractUrls(text) {
//  const urlRegex = /https?:\/\/[^\s]+/g;
//  return text.match(urlRegex) || [];
//}

exports.writeNewMail = (req, res) => {
  const { subject, body } = req.body;
  const labels = [];
  let { to = [] } = req.body;
  //const userId = req.headers["user-id"];

  const userId = req.userId; // comes from the token

  // make it an array if it's not already
  if (!Array.isArray(to)) {
    to = [to];
  }

  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = getUserById(userId);

  // TO CHECK THIS
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const usernameById = userById.username;
  const from = usernameById;

  // Validate required fields

  if (to.length === 0) {
    return res
      .status(400)
      .json({ error: "Missing required fields or empty field - 'to' field" });
  }

  const invalidRecipients = to.filter(
    (username) => !findUserByUsername(username)
  );

  if (invalidRecipients.length > 0) {
    return res
      .status(400)
      .json({ error: "Some recipients do not exist", invalidRecipients });
  }

  if (!findUserByUsername(from)) {
    return res.status(404).json({ error: "Sender dosen't exist" });
  }

  // Only allow sending emails from the user's own account
  if (from !== usernameById) {
    return res.status(403).json({
      error:
        "You are not authorized to send emails as this user. Change the 'from' field.",
    });
  }

  //if (!searchAllLabelsArray(labels, userId)) {
  //  return res.status(404).json({ error: "Labels do not exist for this user" });
  //}

  const newMail = mailModel.createMail({ from, to, subject, body, labels, mailId: null });

  // Check if the mail contains blacklisted URLS
  if (!newMail) {
    return res
      .status(200)
      .json({ message: "Have blacklisted URLs", warning: "Created in spam." });;
  }
  // If the email creation fails, return a 500 status code
  // if (!newMail) {
  //   return res.status(500).json({ error: "Failed to create email" });
  // }

  return res
    .status(201)
    .json({ message: "Email sent successfully", mail: newMail });
};

exports.deleteMail = (req, res) => {
  const mailId = req.params.id;
  //const userId = req.headers["user-id"];
  const userId = req.userId; // comes from the token
  if (!userId) {
    return res.status(400).json({ error: "user-id required in header" });
  }

  const username = getUserById(userId).username;
  const mail = mailModel.getMailById(mailId, username);
  // ******filter by  mail id or mail id and ownwer?
  if (!mail) {
    return res.status(404).send("Mail not found for this user");
  }

  /*
   Check if the user is the sender or receiver of the mail, if not return 403
  if (mail.from !== username && mail.to !== username) {
    return res.status(403).send('You are not authorized to delete this mail');
  }*/

  const deleted = mailModel.deleteMailById(mailId, username);
  if (!deleted) {
    return res.status(500).send("Failed to delete mail");
  }
  return res.status(200).send("Mail deleted successfully");
};




exports.getInboxMailsOfUser = async (req, res) => {
  const userId = req.userId; // comes from the token
 
  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = getUserById(userId);

  // TO CHECK THIS
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const username = userById.username;


  try {
    // return 50 recent mails of this username
    return res.status(200).json(mailModel.getInboxMailsOfUser(username));
  } catch (e) {
    // error in the server
    return res
      .status(500)
      .json({ error: "Failed to fetch mails", detail: e.message || String(e) });
  }
};




exports.getSentMails = async (req, res) => {
  const userId = req.userId; // comes from the token
 
  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = getUserById(userId);

  // TO CHECK THIS
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const username = userById.username;


  try {
    // return 50 recent mails of this username
    return res.status(200).json(mailModel.getSentMailsOfUser(username));
  } catch (e) {
    // error in the server
    return res
      .status(500)
      .json({ error: "Failed to fetch mails", detail: e.message || String(e) });
  }
};




exports.getRecentMailsOfUser = async (req, res) => {
  const userId = req.userId;
  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }
  const userById = getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;

  // Extract query parameters
  const { range, labelName, mailType } = req.query;

  try {
    // Get all mails first
    let mails = mailModel.getInboxMailsOfUser(username);

    // Filter by label if labelName is provided
    if (labelName) {
      mails = mails.filter(
        (mail) => mail.labels && mail.labels.includes(labelName)
      );
    }
    /*
    // Filter by mail type if specified
    if (mailType) {
      if (mailType === "sent") {
        mails = mails.filter((mail) => mail.from === username);
      } else if (mailType === "received") {
        mails = mails.filter((mail) => mail.from !== username);
      } else if (mailType === "all") {
        // No filtering needed - return all mails
      } else {
        return res.status(400).json({
          error: "Invalid mailType. Use 'sent', 'received', or 'all'",
        });
      }
    }
      */

    // Apply range if provided
    if (range) {
      const rangeMatch = range.match(/^(\d+)(?:-(\d+))?$/);
      if (rangeMatch) {
        const start = parseInt(rangeMatch[1]);
        const end = rangeMatch[2] ? parseInt(rangeMatch[2]) : start;

        if (rangeMatch[2]) {
          // Range format: "1-3" - get mails from position 1 to 3 (1-indexed)
          // Convert to 0-indexed: position 1 = index 0, position 3 = index 2
          const startIndex = Math.max(0, start - 1);
          const endIndex = Math.min(mails.length, end);
          mails = mails.slice(startIndex, endIndex);
        } else {
          // Single number format: "50" - get last 50 mails
          mails = mails.slice(-start);
        }
      } else {
        return res.status(400).json({
          error:
            "Invalid range format. Use 'N' for last N mails or 'N-M' for range from position N to M",
        });
      }
    }

    return res.status(200).json(mails);
  } catch (e) {
    // error in the server
    return res
      .status(500)
      .json({ error: "Failed to fetch mails", detail: e.message || String(e) });
  }
};

// get mail by its id
exports.getMailById = async (req, res) => {
  // Extract the mail id from the request parameters
  const { id } = req.params;
  //const userId = req.headers["user-id"];
  const userId = req.userId; // comes from the token
  // Check that user id is not empty
  if (!userId) {
    return res.status(401).json({ error: "User id required in header" });
  }
  const userById = getUserById(userId);
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;
  const mail = mailModel.getMailById(id, username);
  // if doesn't exist mail with this id, return 404 status
  if (!mail) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }
  // Check that user id is related to the mail (from or to)
  // get id of the user who sent the mail
  /* const useFrom = findUserByUsername(mail.from);
  // get id of the user who received the mail
  const useTo = findUserByUsername(mail.to);
  // Check that idFrom and idTo are not empty - delete it
  if (!useFrom || !useTo) {
    return res.status(500).json({ error: "Original mail has invalid 'from' or 'to' fields" });
  }
  // Get the ids of the users who sent and received the mail
  const idFrom = useFrom.id;
  const idTo = useTo.id;
  // Check if the user is authorized to view this mail
  if (idFrom !== userId && idTo !== userId) {
    return res.status(403).json({ error: "You are not authorized to view this mail" });
  }*/
  // if exists, return the mail json with status 200
  return res.status(200).json(mail);
};

exports.readMail = (req, res) => {
  const { id } = req.params;
  //const userId = req.headers["user-id"];
  const userId = req.userId; // comes from the token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }
  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of mail" });
  }
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  // Check if the mail exists
  const mailExists = mailModel.getMailById(id, username);
  if (!mailExists) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }

  if (!mailModel.markReadMail(mailExists)) {
    return res.status(500).json({ error: "Failed to mark email as read" });
  }

  return res.status(204).send(); // No content to return, just a success status
};

exports.unreadMail = (req, res) => {
  const { id } = req.params;
  //const userId = req.headers["user-id"];
  const userId = req.userId; // comes from the token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }
  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of mail" });
  }
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  // Check if the mail exists
  const mailExists = mailModel.getMailById(id, username);
  if (!mailExists) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }

  if (!mailModel.markUnreadMail(mailExists)) {
    return res.status(500).json({ error: "Failed to mark email as read" });
  }

  return res.status(204).send(); // No content to return, just a success status
};

// edit mail by its id
exports.editMail = async (req, res) => {
  // mail id
  const { id } = req.params;
  // fields to update
  const { subject, body } = req.body;
  //const userId = req.headers["user-id"];
  const userId = req.userId; // comes from the token
  // Check that user id is not empty
  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }
  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of mail" });
  }
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  // Check if the mail exists
  const mailExists = mailModel.getMailById(id, username);
  if (!mailExists) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }
  // Check if labels is an array and if all labels exist for this user
  //if (!searchAllLabelsArray(labels, userId)) {
  //  return res.status(404).json({ error: "Labels do not exist for this user" });
  //}
  const hasBlacklistedURL = mailModel.searchMailWithChangesForBlacklistedURLs(
    subject,
    body,
    labels,
    mailExists
  );

  if (hasBlacklistedURL) {
    return res
      .status(200)
      .json({ message: "Have blacklisted URLs", warning: "Moved to spam." });
  }

  const updated = mailModel.editMail(id, username, { subject, body, labels });
  if (!updated) {
    return res.status(500).json({ error: "Failed to update email" });
  }

  // If the update is successful, return a success message
  return res.status(200).json({ message: "Email updated successfully" });
};



exports.addLabelToMail = (req, res) => {
  const { mailId } = req.params;
  const { labelId } = req.body;
  const userId = req.userId; // comes from the token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }

  // Check that id of email is not empty
  if (!mailId) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of mail" });
  }

  if (!labelId){
      return res
      .status(400)
      .json({ error: "Missing required fields - id of label" });
  }


  const label  = labelsModel.getLabelById(labelId, userId);

  if (!label) {
    return res.status(404).json({ error:"Label not found for this user" });
  }

  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  // Check if the mail exists
  const mailExists = mailModel.getMailById(id, username);
  if (!mailExists) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }

  const labelName = label.name;
  if (labels.includes(labelName)) {
    return res.status(300).json({ error: "Label already exists in mail." });
  }

  const newLabels = labels.push(labelName);


  if (!mailModel.editMail(mailId, username,mailExists.subject, mailExists.body, newLabels)) {
    return res.status(500).json({ error: "Failed to add label to mail." });
  }

  return res.status(204).send(); // No content to return, just a success status
};


exports.removeLabelFromMail = (req, res) => {
  const { mailId } = req.params;
  const { labelId } = req.body;
  const userId = req.userId; // comes from the token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }

  // Check that id of email is not empty
  if (!mailId) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of mail" });
  }

  if (!labelId){
      return res
      .status(400)
      .json({ error: "Missing required fields - id of label" });
  }


  const label  = labelsModel.getLabelById(labelId, userId);

  if (!label) {
    return res.status(404).json({ error:"Label not found for this user" });
  }

  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  // Check if the mail exists
  const mailExists = mailModel.getMailById(id, username);
  if (!mailExists) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }

  const labelName = label.name;
  if (!labels.includes(labelName)) {
    return res.status(300).json({ error: "Label doesn't exist in mail." });
  }

  const newLabels = labels.filter(label => label !== labelName);


  if (!mailModel.editMail(mailId, username,mailExists.subject, mailExists.body, newLabels)) {
    return res.status(500).json({ error: "Failed to remove label from mail." });
  }

  return res.status(204).send(); // No content to return, just a success status
};



exports.updateLabelsInMail = (req, res) => {
  const { id } = req.params;
  const { labels = [] } = req.body;
  const userId = req.userId; // from token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }

  if (!id) {
    return res.status(400).json({ error: "Missing required fields - id of mail" });
  }

  const uniqueLabels = [...new Set(labels)];

  const userById = getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;


  const mailExists = mailModel.getMailById(id, username);
  if (!mailExists) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }


  const updated = mailModel.editMail(
    id,
    username,
    {
      subject: mailExists.subject,
      body: mailExists.body,
      labels: uniqueLabels
    }
  );

  if (!updated) {
    return res.status(500).json({ error: "Failed to update labels in mail." });
  }

  return res.status(204).send(); 
};



exports.starredMail = (req, res) => {
  const { id } = req.params;
  const userId = req.userId; // comes from the token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }
  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of mail" });
  }
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  // Check if the mail exists
  const mailExists = mailModel.getMailById(id, username);
  if (!mailExists) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }

  if (!mailModel.markStarredMail(mailExists)) {
    return res.status(500).json({ error: "Failed to mark email as read" });
  }

  return res.status(204).send(); // No content to return, just a success status
};

exports.unstarredMail = (req, res) => {
  const { id } = req.params;
  const userId = req.userId; // comes from the token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }
  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of mail" });
  }
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  // Check if the mail exists
  const mailExists = mailModel.getMailById(id, username);
  if (!mailExists) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }

  if (!mailModel.markUnstarredMail(mailExists)) {
    return res.status(500).json({ error: "Failed to mark email as read" });
  }

  return res.status(204).send(); // No content to return, just a success status
};

