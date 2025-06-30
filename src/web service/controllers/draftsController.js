//const { createMail } = require("../models/mailsModel");
const { findUserByUsername } = require("../models/usersModel");
const { getUserById } = require("../models/usersModel");
const { searchAllLabelsArray } = require("../models/labelsModel");
const draftModel = require("../models/draftsModel");


exports.writeNewDraft = (req, res) => {
  const {subject, body} = req.body;
  const labels = [];
  let { to = [] } = req.body;
  //const userId = req.headers["user-id"];
  const userId = req.userId; // comes from the token

  if (!to) {
  to = [];
  }


  // make it an array if it's not already
  if (!Array.isArray(to)) {
  to = [to];
  }

  if (to.length > 0) {
    const invalidRecipients = to.filter(
      (username) => !findUserByUsername(username)
    );
    if (invalidRecipients.length > 0) {
      return res.status(400).json({ error: "Some recipients do not exist", invalidRecipients });
    }
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

  const from = usernameById; // Use the username of the user making the request as the sender


  if (to.length > 0 ) {
      const invalidRecipients = to.filter(
      (username) => !findUserByUsername(username)
    );

    if (invalidRecipients.length > 0) {
      return res
        .status(400)
        .json({ error: "Some recipients do not exist", invalidRecipients });
    }
  }


  if (!findUserByUsername(from)) {
    return res.status(404).json({ error: "Sender dosen't exist" });
  }

  // Only allow sending emails from the user's own account
 // if (from !== usernameById) {
   // return res.status(403).json({
   //   error:
      //  "You are not authorized to send emails as this user. Change the 'from' field.",
    //});
  //}

  //if (!searchAllLabelsArray(labels, userId)) {
  //  return res.status(404).json({ error: "Labels do not exist for this user" });
  //}

  const newDraft = draftModel.createDraft({ from, to, subject, body, labels });

  // Check if the mail contains blacklisted URLS
  //if (!newDraft) {
  //  return res
  //    .status(200)
  //    .json({ message: "Have blacklisted URLs", warning: "Created in spam." });
  //}

  if (!newDraft) {
    return res
      .status(500)
      .json({ message: "Failed to update draft"});
  }

  return res
    .status(201)
    .json({ message: "Draft saved successfully", draft: newDraft });
};

exports.deleteDraft = (req, res) => {
  const draftId = req.params.id;
  //const userId = req.headers["user-id"];
   const userId = req.userId; // comes from the token

  //if (!userId) {
   // return res.status(400).json({ error: "user-id required in header" });
  //}

   if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const username = getUserById(userId).username;
  const draft = draftModel.getDraftById(draftId, username);
  // ******filter by  mail id or mail id and ownwer?
  if (!draft) {
    return res.status(404).send("Draft not found for this user");
  }



  const deleted = draftModel.deleteDraftById(draftId, username);
  if (!deleted) {
    return res.status(500).send("Failed to delete draft");
  }
  return res.status(200).send("Draft deleted successfully");
};

// get user by id
exports.getAllDraftsOfUser = async (req, res) => {
//  const username = req.query.username;
  const userId = req.userId; // comes from the token
  //const userId = req.headers["user-id"];
  // check that username is not empty
 // if (!username) {
   // return res.status(400).json({ error: "Username is required" });
  //}
 // if (!userId) {
//return res.status(401).json({ error: "Valid user-id is required" });
//}
  // check if the username exists
 // if (!findUserByUsername(username)) {
   // return res.status(404).json({ error: "Username doesn't exist" });
    // 404 or 400?
 // }

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
    // return all drafts of this username
    return res.status(200).json(draftModel.getAllDrafts(username));
  } catch (e) {
    // error in the server
    return res
      .status(500)
      .json({ error: "Failed to fetch drafts", detail: e.message || String(e) });
  }
};

// get draft by its id
exports.getDraftById = async (req, res) => {
  // Extract the mail id from the request parameters
  const { id } = req.params;
  //const userId = req.headers["user-id"];
  const userId = req.userId; // comes from the token
  // Check that user id is not empty
  //if (!userId) {
   // return res.status(401).json({ error: "User id required in header" });
  //}

 if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = getUserById(userId);
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;
  const draft = draftModel.getDraftById(id, username);
  // if doesn't exist draft with this id, return 404 status
  if (!draft) {
    return res.status(404).json({ error: "Draft not found for this user" });
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
  // if exists, return the draft json with status 200
  return res.status(200).json(draft);
};


exports.readDraft = (req, res) => {
  const { id } = req.params;
  //const userId = req.headers["user-id"];
  const userId = req.userId; // comes from the token

   if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of draft" });
  }
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  // Check if the mail exists
  const draftExists = draftModel.getDraftById(id, username);
  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }

  if (!draftModel.markReadDraft(draftExists)) {
    return res.status(500).json({ error: "Failed to mark draft as read" });
  }

  return res.status(204).send(); // No content to return, just a success status
}



exports.unreadDraft = (req, res) => {
  const { id } = req.params;
  //const userId = req.headers["user-id"];

  const userId = req.userId; // comes from the token

   if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

//if (!userId) {
  //  return res.status(401).json({ error: "user-id required in header" });
  //}
  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of draft" });
  }
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  // Check if the draft exists
  const draftExists = draftModel.getDraftById(id, username);
  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }

  if (!draftModel.markUnreadDraft(draftExists)) {
    return res.status(500).json({ error: "Failed to mark draft as unread" });
  }

  return res.status(204).send(); // No content to return, just a success status
}


// edit draft by its id
exports.editDraft = async (req, res) => {
  // draft id
  const { id } = req.params;
  // fields to update
  const { subject, body, to, labels } = req.body;
  const userId = req.userId; // comes from the token

   if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }
  //const userId = req.headers["user-id"];
  // Check that user id is not empty
  //if (!userId) {
    //return res.status(401).json({ error: "user-id required in header" });
  //}
  // Check that id of draft is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of draft" });
  }
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  // Check if the draft exists
  const draftExists = draftModel.getDraftById(id, username);
  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }

  let toArray = to;

  if (!toArray) {
    toArray = [];
  }

  if (!Array.isArray(toArray)) {
    toArray = [toArray];
  }

  if (toArray.length > 0) {
    const invalidRecipients = toArray.filter(
      (username) => !findUserByUsername(username)
    );

    if (invalidRecipients.length > 0) {
      return res.status(400).json({ error: "Some recipients do not exist", invalidRecipients });
    }
  }


  // Check if labels is an array and if all labels exist for this user
  //if (!searchAllLabelsArray(labels, userId)) {
  //  return res.status(404).json({ error: "Labels do not exist for this user" });
  //}
  //const hasBlacklistedURL = draftModel.searchDraftWithChangesForBlacklistedURLs(
  //  subject,
  //  body,
  //  labels,
  //  draftExists
  //);

  //if (hasBlacklistedURL) {
  //  return res
  //    .status(200)
  //    .json({ message: "Have blacklisted URLs", warning: "Moved to spam." });
  //}

  const updated = draftModel.editDraft(id, username, { subject, body, to });
  if (!updated) {
    return res.status(500).json({ error: "Failed to update draft" });
  }

  // If the update is successful, return a success message
  return res.status(200).json({ message: "Draft updated successfully" });
};



exports.convertDraftToMail = (req, res) => {
  const { draftId } = req.params;
  const userId = req.userId; // comes from the token

  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  } 

  if (!draftId) {
    return res.status(400).json({ error: "Draft ID is required" });
  }

  const draft = draftModel.getDraftById(draftId);

  if (!draft) {
    return res.status(404).json({ error: "Draft not found" });
  }

  const userById = getUserById(userId);

  // TO CHECK THIS
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }


  const to = draft.to;

  const usernameById = userById.username;
  const from = usernameById;

  const subject = draft.subject;
  const body = draft.body;
  const labels = draft.labels


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

  if (!searchAllLabelsArray(labels, userId)) {
    return res.status(404).json({ error: "Labels do not exist for this user" });
  }



  const newMail = mailModel.createMail({ from, to, subject, body, labels, draftId });

  // Check if the mail contains blacklisted URLS
  if (!newMail) {
    return res
      .status(200)
      .json({ message: "Have blacklisted URLs", warning: "Created in spam." });
  }
  // If the email creation fails, return a 500 status code
  // if (!newMail) {
  //   return res.status(500).json({ error: "Failed to create email" });
  // }

  return res
    .status(201)
    .json({ message: "Email sent successfully", mail: newMail });
};
/*
exports.updateLabelsInDraft = (req, res) => {
  const { id } = req.params;
  const { labels = [] } = req.body;
  const userId = req.userId; // from token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }

  if (!id) {
    return res.status(400).json({ error: "Missing required fields - id of draft" });
  }

  const uniqueLabels = [...new Set(labels)];

  const userById = getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;


  const draftExists = draftModel.getDraftById(id, username);
  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }


  const updated = draftModel.editDraft(
    id,
    username,
    {
      subject: draftExists.subject,
      body: draftExists.body,
      labels: uniqueLabels
    }
  );

  if (!updated) {
    return res.status(500).json({ error: "Failed to update labels in draft." });
  }

  return res.status(204).send(); 
};*/



exports.updateLabelsInDraft = (req, res) => {
  const { id } = req.params;
  const { labels = [] } = req.body;
  const userId = req.userId; // from token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }

  if (!id) {
    return res.status(400).json({ error: "Missing required fields - id of draft" });
  }

  const uniqueLabels = [...new Set(labels)];

  const userById = getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;


  const draftExists = draftModel.getDraftById(id, username);
  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }


  const updated = draftModel.editLabelsInDraft(
    id,
    username,
    uniqueLabels
  );

  if (!updated) {
    return res.status(500).json({ error: "Failed to update labels in draft." });
  }

  return res.status(204).send(); 
};


exports.unstarredDraft = (req, res) => {
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
  const draftExists = draftModel.getDraftById(id, username);
  if (!draftExists) {
      return res.status(404).json({ error: "Draft not found for this user" });
  }

  if (!draftModel.markUnstarredDraft(draftExists)) {
      return res.status(500).json({ error: "Failed to mark draft as unstarred" });
  }
  return res.status(204).send(); // No content to return, just a success status
};


exports.starredDraft = (req, res) => {
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
  const draftExists = draftModel.getDraftById(id, username);
  if (!draftExists) {
      return res.status(404).json({ error: "Draft not found for this user" });
  }

  if (!draftModel.markStarredDraft(draftExists)) {
      return res.status(500).json({ error: "Failed to mark draft as starred" });
  }
  return res.status(204).send(); // No content to return, just a success status
};
