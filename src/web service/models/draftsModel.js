const { v4: uuidv4 } = require("uuid");
const { findUserByUsername } = require("./usersModel");

const blacklistModel = require("../models/blacklistModel");
const labelsModel = require("../models/labelsModel");
const userModel = require("../models/usersModel");
const mailsModel = require("../models/mailsModel");

// drafts object - in memory - we dont save between runs
const drafts = [];

// return all the drafts
const getAllDrafts = () => drafts;

// return draft by id

const getDraftById = (id, username) =>
  drafts.find((draft) => draft.id === id && draft.owner === username);

function deleteDraftById(id, username) {
  try {
    const index = drafts.findIndex(
      (draft) => draft.id === id && draft.owner === username
    );
    if (index === -1) {
      return false; // Return false if draft not found
    }

    // Delete the draft in index
    drafts.splice(index, 1);
  } catch (error) {
    return false; // Return false if deletion fails
  }
  return true; // Return true if deletion is successful
}

const createDraft = ({ from, to, subject, body, labels }) => {
  const timeStamp = new Date().toLocaleString("sv-SE", {
    timeZone: "Asia/Jerusalem",
  });
  // All the recipients should be in an array
  const recipients = Array.isArray(to) ? to : [to];

  const newDrafts = [];
  const draftId = uuidv4();
  // if the sender is not in the recipients;
  const uniqueTo =
    recipients && Array.isArray(recipients)
      ? [
          ...new Set(
            recipients.filter((recipient) => typeof recipient === "string")
          ),
        ]
      : [];
  // create a copy for the person who sent the mail
  newDrafts.push({
    id: draftId,
    from,
    to: uniqueTo,
    subject,
    body,
    labels,
    timeStamp,
    mailType: null,
    owner: from,
    isRead: false,
    isStarred: false,
  });

 // for (const draft of newDrafts) {
 //   if (searchDraftsForBlacklistedURLs(draft)) {
 //     return null;
 //   }
 // }

  drafts.push(...newDrafts);
  return newDrafts;
};

function createCopyOfDraft(draft) {
  try {
    const copyDraft = { ...draft };
    drafts.push(copyDraft);
    return copyDraft;
  } catch (error) {
    return null;
  }
}

function markReadDraft(draft) {
  try {
    if (!draft) return false;
    draft.isRead = true;
    return true;
  } catch (error) {
    return false; // Return false if while marking as read fails
  }
}

function markUnreadDraft(draft) {
  try {
    if (!draft) return false;
    draft.isRead = false;
    return true;
  } catch (error) {
    return false; // Return false if while marking as unread fails
  }
}

// edit a draft
function editDraft(id, username, { subject, body, to }) {
  // Find the draft by id
  const draft = drafts.find(
    (draft) => draft.id === id && draft.owner === username
  );
  // Update the draft properties
  try {
    if (subject !== undefined) {
      draft.subject = subject;
    }
    if (body !== undefined) {
      draft.body = body;
    }
    if (to !== undefined) {
      const uniqueTo = [
        ...new Set(to.filter((recipient) => typeof recipient === "string")),
      ];
      draft.to = uniqueTo;
    }
    // Update the timestamp to the current time
    draft.timeStamp = new Date().toLocaleString("sv-SE", {
      timeZone: "Asia/Jerusalem",
    });
  } catch (e) {
    return false;
  }

  return true;
}



function editLabelsInDraft(id, username, newLabels) {
  // Find the draft by id
  const draft = drafts.find((draft) => draft.id === id && draft.owner === username);
  // Update the draft properties
  try {
    if (newLabels !== undefined) {
      const uniqueLabels = [
        ...new Set(newLabels.filter((label) => typeof label === "string")),
      ];
      draft.labels = uniqueLabels;
    }
  } catch (e) {
    return false;
  }
  return true;
}

const filterDraftsByQueryCaseSensitive = (query, username) => {
  return drafts.filter((draft) => {
    // Return drafts where the user is involved and the query matches the content
    return (
      draft.owner === username && searchDraftContentSensitive(query, draft)
    );
  });
};

const searchDraftsForBlacklistedURLs = (mail) => {
  return blacklistModel.getUrls().some((entry) => {
    return searchDraftContentInsensitive(entry.url, mail);
  });
};

const filterDraftsByQueryCaseInsensitive = (query, username) => {
  return drafts.filter((draft) => {
    // Check if draft.to exists and is an array, and if the user is a sender or recipient
    // const isUserInvolved =
    //   draft.from === username ||
    //   (Array.isArray(draft.to) &&
    //     draft.to.some((recipient) => recipient === username));

    // Return drafts where the user is involved and the query matches the content
    return (
      draft.owner === username && searchDraftContentInsensitive(query, draft)
    );
  });
};

const searchDraftContentInsensitive = (query, newDraft) => {
  const draft = newDraft;
  if (!draft) return false; // Handle case where draft is undefined
  const q = query.toLowerCase();
  // Check individual fields
  const hasQueryInFields =
    (draft.subject && draft.subject.toLowerCase().includes(q)) ||
    (draft.body && draft.body.toLowerCase().includes(q)) ||
    (draft.from && draft.from.toLowerCase().includes(q));

  const hasQueryInToRecipients =
    draft.to &&
    Array.isArray(draft.to) &&
    draft.to.some((recipient) => recipient.toLowerCase().includes(q));

  // Check all labels individually
  const labelsObject = labelsModel.convertLabelsArrayToObjects(draft.labels);
  const hasQueryInLabels =
    labelsObject &&
    Array.isArray(labelsObject) &&
    labelsObject.some((label) => {
      return label.name && label.name.toLowerCase().includes(q);
    });

  return hasQueryInFields || hasQueryInLabels || hasQueryInToRecipients;
};

const searchDraftContentSensitive = (query, newDraft) => {
  const draft = newDraft;
  if (!draft) return false; // Handle case where draft is undefined

  // Check individual fields
  const hasQueryInFields =
    (draft.subject && draft.subject.includes(query)) ||
    (draft.body && draft.body.includes(query)) ||
    (draft.from && draft.from.includes(query));

  const hasQueryInToRecipients =
    draft.to &&
    Array.isArray(draft.to) &&
    draft.to.some((recipient) => recipient.includes(query));

  // Check all labels individually
  const labelsObject = labelsModel.convertLabelsArrayToObjects(draft.labels);
  const hasQueryInLabels =
    labelsObject &&
    Array.isArray(labelsObject) &&
    labelsObject.some((label) => {
      return label.name && label.name.includes(query);
    });

  // // labels is an array of strings
  // const hasQueryInLabels =
  //   mail.labels &&
  //   Array.isArray(mail.labels) &&
  //   mail.labels.some((label) => {
  //     return label && label.includes(query);
  //   });

  return hasQueryInFields || hasQueryInLabels || hasQueryInToRecipients;
};

const searchDraftForBlacklistedURLs = (draft) => {
  return blacklistModel.getUrls().some((entry) => {
    return searchDraftContentInsensitive(entry.url, draft);
  });
};

const searchDraftWithChangesForBlacklistedURLs = (
  subject,
  body,
  labels,
  draftExists
) => {
  const finalSubject = subject ?? draftExists.subject;
  const finalBody = body ?? draftExists.body;
  const finalLabels = labels !== undefined ? labels : draftExists.labels;

  const from = draftExists.from;
  const to = draftExists.to;
  const id = draftExists.id;
  const timeStamp = draftExists.timeStamp;
  const draftType = draftExists.draftType;
  const owner = draftExists.owner;
  const isRead = draftExists.isRead;
  const isStarred = draftExists.isStarred;

  const copyChangedDraft = {
    id,
    from,
    to,
    subject: finalSubject,
    body: finalBody,
    labels: finalLabels,
    timeStamp,
    draftType,
    owner,
    isRead,
    isStarred,
  };
  return searchDraftForBlacklistedURLs(copyChangedDraft);
};

function getDraftsByUser(username) {
  // filter by username
  const filterByUser = drafts.filter((draft) => draft.owner === username);
  // filter by time
  const sortedDrafts = filterByUser.sort(
    (m1, m2) => new Date(m2.timeStamp) - new Date(m1.timeStamp)
  );
  return sortedDrafts;
}

function markStarredDraft(draft) {
  try {
    draft.isStarred = true;
    return true;
  } catch (error) {
    return false; // Return false if while marking as starred fails
  }
}

function markUnstarredDraft(draft) {
  try {
    draft.isStarred = false;
    return true;
  } catch (error) {
    return false; // Return false if while marking as unstarred fails
  }
}

module.exports = {
  getAllDrafts,
  getDraftById,
  deleteDraftById,
  createDraft,
  createCopyOfDraft,
  markReadDraft,
  markUnreadDraft,
  markStarredDraft,
  markUnstarredDraft,
  editDraft,
  editLabelsInDraft,
  getDraftsByUser,
  filterDraftsByQueryCaseSensitive,
  filterDraftsByQueryCaseInsensitive,
  searchDraftContentInsensitive,
  searchDraftContentSensitive,
  searchDraftForBlacklistedURLs,
  searchDraftWithChangesForBlacklistedURLs,
  searchDraftsForBlacklistedURLs,
};
