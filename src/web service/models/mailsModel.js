const { v4: uuidv4 } = require("uuid");
const { findUserByUsername } = require("./usersModel");

const blacklistModel = require("../models/blacklistModel");
const labelsModel = require("../models/labelsModel");
const userModel = require("../models/usersModel");
const spamModel = require("../models/spamModel");

// mails object - in memory - we dont save between runs
const mails = [];

// return all the mails
const getAllMails = () => mails;

// return mail by id

const getMailById = (id, username) =>
  mails.find((mail) => mail.id === id && mail.owner === username);

function getRecentMailsOfUser(username) {
  // filter by username
  const filterByUser = mails.filter((mail) => mail.owner === username);
  // filter by time - 50 recent
  const sortedMails = filterByUser.sort(
    (m1, m2) => new Date(m2.timeStamp) - new Date(m1.timeStamp)
  );
  const recentSortedMails = sortedMails.slice(0, 50);
  return recentSortedMails;
  //return sortedMails;
}

function getMailsByUser(username) {
  // filter by username
  const filterByUser = mails.filter((mail) => mail.owner === username);
  // filter by time - 50 recent
  const sortedMails = filterByUser.sort(
    (m1, m2) => new Date(m2.timeStamp) - new Date(m1.timeStamp)
  );
  return sortedMails;
}

function getInboxMailsOfUser(username) {
  // filter by username
  const filterByUser = mails.filter(
    (mail) =>
      mail.owner === username &&
      (mail.mailType === "sent and received" || mail.mailType === "received")
  );
  const sortedMails = filterByUser.sort(
    (m1, m2) => new Date(m2.timeStamp) - new Date(m1.timeStamp)
  );
  return sortedMails;
}

function getSentMailsOfUser(username) {
  // filter by username
  const filterByUser = mails.filter(
    (mail) =>
      mail.owner === username &&
      (mail.mailType === "sent and received" || mail.mailType === "sent")
  );
  const sortedMails = filterByUser.sort(
    (m1, m2) => new Date(m2.timeStamp) - new Date(m1.timeStamp)
  );
  return sortedMails;
}

function getMailsByUser(username) {
  // filter by username
  const filterByUser = mails.filter((mail) => mail.owner === username);
  // filter by time - 50 recent
  const sortedMails = filterByUser.sort(
    (m1, m2) => new Date(m2.timeStamp) - new Date(m1.timeStamp)
  );
  return sortedMails;
}

function getInboxMailsOfUser(username) {
  // filter by username
  const filterByUser = mails.filter(
    (mail) =>
      mail.owner === username &&
      (mail.mailType === "sent and received" || mail.mailType === "received")
  );
  const sortedMails = filterByUser.sort(
    (m1, m2) => new Date(m2.timeStamp) - new Date(m1.timeStamp)
  );
  return sortedMails;
}

function getSentMailsOfUser(username) {
  // filter by username
  const filterByUser = mails.filter(
    (mail) =>
      mail.owner === username &&
      (mail.mailType === "sent and received" || mail.mailType === "sent")
  );
  const sortedMails = filterByUser.sort(
    (m1, m2) => new Date(m2.timeStamp) - new Date(m1.timeStamp)
  );
  return sortedMails;
}

function deleteMailById(id, username) {
  try {
    const index = mails.findIndex(
      (mail) => mail.id === id && mail.owner === username
    );
    // Delete the mail in index
    mails.splice(index, 1);
  } catch (error) {
    return false; // Return false if deletion fails
  }
  return true; // Return true if deletion is successful
}

function createCopyOfMail(mail) {
  try {
    const copyMail = { ...mail };
    mails.push(copyMail);
    return copyMail;
  } catch (error) {
    return null;
  }
}

const createMail = ({ from, to, subject, body, labels, mailId }) => {
  const timeStamp = new Date().toLocaleString("sv-SE", {
    timeZone: "Asia/Jerusalem",
  });
  // All the recipients should be in an array
  const recipients = Array.isArray(to) ? to : [to];

  const newMails = [];
  if (mailId === undefined || mailId === null) {
    mailId = uuidv4();
  }

  //const mailId = uuidv4();
  // if the sender is not in the recipients
  const isSelfSend = recipients.includes(from);
  const uniqueTo =
    recipients && Array.isArray(recipients)
      ? [
          ...new Set(
            recipients.filter((recipient) => typeof recipient === "string")
          ),
        ]
      : [];
  // create a copy for the person who sent the mail
  if (isSelfSend) {
    newMails.push({
      id: mailId,
      from,
      to: uniqueTo,
      subject,
      body,
      labels,
      timeStamp,
      mailType: "sent and received", // sent and received by the same user
      owner: from,
      isRead: false,
      isStarred: false,
    });
  } else {
    // create a copy for the person who sent the mail
    newMails.push({
      id: mailId,
      from,
      to: uniqueTo,
      subject,
      body,
      labels,
      timeStamp,
      mailType: "sent", // sent by the user
      owner: from,
      isRead: true,
      isStarred: false,
    });
  }

  // create a copy for each recipient
  uniqueTo.forEach((recipient) => {
    if (recipient !== from) {
      newMails.push({
        id: mailId,
        from,
        to: uniqueTo,
        subject,
        body,
        labels,
        timeStamp,
        mailType: "received",
        owner: recipient,
        isRead: false, // unread by default
        isStarred: false,
      });
    }
  });

  for (const mail of newMails) {
    if (searchMailForBlacklistedURLs(mail)) {
      spamModel.addSpamMailFromNew(mail);
      return null;
    }
  }

  mails.push(...newMails);
  return newMails;
};

function markReadMail(mail) {
  try {
    mail.isRead = true;
    return true;
  } catch (error) {
    return false; // Return false if while marking as read fails
  }
}

function markUnreadMail(mail) {
  try {
    mail.isRead = false;
    return true;
  } catch (error) {
    return false; // Return false if while marking as unread fails
  }
}

// edit a mail
function editMail(id, username, { subject, body, labels }) {
  // Find the mail by id
  const mail = mails.find((mail) => mail.id === id && mail.owner === username);
  // Update the mail properties
  try {
    if (subject !== undefined) {
      mail.subject = subject;
    }
    if (body !== undefined) {
      mail.body = body;
    }
    if (labels !== undefined) {
      // add labels array without duplicates
      // mail.labels = labels;
      const uniqueLabels = [
        ...new Set(labels.filter((label) => typeof label === "string")),
      ];
      mail.labels = uniqueLabels;
    }
    // Update the timestamp to the current time
    // mail.timeStamp = new Date().toLocaleString("sv-SE", {
    //  timeZone: "Asia/Jerusalem",
    // });
  } catch (e) {
    return false;
  }

  return true;
}

const filterMailsByQueryCaseSensitive = (query, username) => {
  return mails.filter((mail) => {
    // Return mails where the user is involved and the query matches the content
    return mail.owner === username && searchMailContentSensitive(query, mail);
  });
};

const filterMailsByQueryCaseInsensitive = (query, username) => {
  return mails.filter((mail) => {
    // Check if mail.to exists and is an array, and if the user is a sender or recipient
    // const isUserInvolved =
    //   mail.from === username ||
    //   (Array.isArray(mail.to) &&
    //     mail.to.some((recipient) => recipient === username));

    // Return mails where the user is involved and the query matches the content
    return mail.owner === username && searchMailContentInsensitive(query, mail);
  });
};

const searchMailContentInsensitive = (query, newMail) => {
  const mail = newMail;
  if (!mail) return false; // Handle case where mail is undefined
  const q = query.toLowerCase();
  // Check individual fields
  const hasQueryInFields =
    (mail.subject && mail.subject.toLowerCase().includes(q)) ||
    (mail.body && mail.body.toLowerCase().includes(q)) ||
    (mail.from && mail.from.toLowerCase().includes(q));

  const hasQueryInToRecipients =
    mail.to &&
    Array.isArray(mail.to) &&
    mail.to.some((recipient) => recipient.toLowerCase().includes(q));

  // Check all labels individually
  const labelsObject = labelsModel.convertLabelsArrayToObjects(mail.labels);
  const hasQueryInLabels =
    labelsObject &&
    Array.isArray(labelsObject) &&
    labelsObject.some((label) => {
      return label.name && label.name.toLowerCase().includes(q);
    });

  return hasQueryInFields || hasQueryInLabels || hasQueryInToRecipients;
};

const searchMailContentSensitive = (query, newMail) => {
  const mail = newMail;
  if (!mail) return false; // Handle case where mail is undefined

  // Check individual fields
  const hasQueryInFields =
    (mail.subject && mail.subject.includes(query)) ||
    (mail.body && mail.body.includes(query)) ||
    (mail.from && mail.from.includes(query));

  const hasQueryInToRecipients =
    mail.to &&
    Array.isArray(mail.to) &&
    mail.to.some((recipient) => recipient.includes(query));

  // Check all labels individually
  const labelsObject = labelsModel.convertLabelsArrayToObjects(mail.labels);
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

const searchMailForBlacklistedURLs = (mail) => {
  return blacklistModel.getUrls().some((entry) => {
    return searchMailContentInsensitive(entry.url, mail);
  });
};

const searchMailWithChangesForBlacklistedURLs = (
  subject,
  body,
  labels,
  mailExists
) => {
  const finalSubject = subject ?? mailExists.subject;
  const finalBody = body ?? mailExists.body;
  const finalLabels = labels !== undefined ? labels : mailExists.labels;

  const from = mailExists.from;
  const to = mailExists.to;
  const id = mailExists.id;
  const timeStamp = mailExists.timeStamp;
  const mailType = mailExists.mailType;
  const owner = mailExists.owner;
  const isRead = mailExists.isRead;
  const isStarred = mailExists.isStarred;

  const copyChangedMail = {
    id,
    from,
    to,
    subject: finalSubject,
    body: finalBody,
    labels: finalLabels,
    timeStamp,
    mailType,
    owner,
    isRead,
    isStarred,
  };
  return searchMailForBlacklistedURLs(copyChangedMail);
};

function markStarredMail(mail) {
  try {
    mail.isStarred = true;
    return true;
  } catch (error) {
    return false; // Return false if while marking as starred fails
  }
}

function markUnstarredMail(mail) {
  try {
    mail.isStarred = false;
    return true;
  } catch (error) {
    return false; // Return false if while marking as unstarred fails
  }
}

function editLabelsInMail(id, username, newLabels) {
  //  locate the mail
  const mail = mails.find((m) => m.id === id && m.owner === username);
  if (!mail) return false;

  try {
    if (newLabels !== undefined) {
      const uniqueLabels = [
        ...new Set(newLabels.filter((label) => typeof label === "string")),
      ];
      // assign back
      mail.labels = uniqueLabels;
    }
  } catch (e) {
    return false;
  }
  return true;
}

module.exports = {
  createMail,
  getInboxMailsOfUser,
  getSentMailsOfUser,
  getAllMails,
  getMailById,
  getRecentMailsOfUser,
  getMailsByUser,
  markReadMail,
  markUnreadMail,
  editMail,
  editLabelsInMail,
  filterMailsByQueryCaseSensitive,
  filterMailsByQueryCaseInsensitive,
  deleteMailById,
  searchMailForBlacklistedURLs,
  searchMailWithChangesForBlacklistedURLs,
  createCopyOfMail,
  searchMailContentInsensitive,
  searchMailContentSensitive,
  markStarredMail,
  markUnstarredMail,
};
