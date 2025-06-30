const { v4: uuidv4 } = require("uuid");
const blacklistModel = require("./blacklistModel");
// const mailsModel = require("./mailsModel");
// const mailsModel = require("./mailsModel");
const spamMails = [];

// addSpamMailFromExisting function creates a copy of the mail and adds it to the spamMails array
async function addSpamMailFromNew1(mail) {
  const spamMailCopy = {
    id: mail.id,
    from: mail.from,
    to: mail.to,
    subject: mail.subject,
    body: mail.body,
    labels: mail.labels ? [...mail.labels] : [],
    timeStamp: mail.timeStamp,
    mailType: mail.mailType,
    owner: mail.owner,
    isRead: mail.isRead,
  };

  spamMails.push(spamMailCopy);
  return spamMailCopy;
}
//-------- add msg to controller
const addSpamMailFromNew = async (mail) => {
  const recipients = Array.isArray(mail.to) ? mail.to : [mail.to];
  const mailId = mail.id;
  const isSelfSend = recipients.includes(mail.from);
  const spamCopies = [];

  const labels =
    mail.labels && Array.isArray(mail.labels)
      ? [...new Set(mail.labels.filter((label) => typeof label === "string"))]
      : [];
  if (isSelfSend) {
    spamMails.push({
      id: mailId,
      from: mail.from,
      to: recipients,
      subject: mail.subject,
      body: mail.body,
      labels,
      timeStamp: mail.timeStamp,
      mailType: "sent and received",
      owner: mail.from,
      isRead: false,
      isStarred: mail.isStarred,
      source : "inbox",
    });
  } else {
    //  create a copy for the person who sent the mail - not spam
    const mailCopy = {
      id: mailId,
      from: mail.from,
      to: recipients,
      subject: mail.subject,
      body: mail.body,
      labels,
      timeStamp: mail.timeStamp,
      mailType: "sent",
      owner: mail.from,
      isRead: false,
      isStarred: mail.isStarred,
      source : "inbox",
    };
    const mailsModel = require("./mailsModel");
    mailsModel.createCopyOfMail(mailCopy);
  }

  // create a copy for the users who received the mail -  spam
  recipients.forEach((recipient) => {
    if (recipient !== mail.from) {
      spamCopies.push({
        id: mailId,
        from: mail.from,
        to: recipients,
        subject: mail.subject,
        body: mail.body,
        labels,
        timeStamp: mail.timeStamp,
        mailType: "received",
        owner: recipient,
        isRead: false,
        isStarred: mail.isStarred,
        source : "inbox",
      });
    }
  });

  spamMails.push(...spamCopies);
  return spamCopies;
};

function createCopyOfSpam(spam) {
  try {
    const copySpam = { ...spam };
    spamMails.push(copySpam);
    return copySpam;
  } catch (error) {
    return null;
  }
}

// add SpamMailAndBlacklistLinks function creates a copy of the mail, extracts links from it, and adds them to the spamMails array and the blacklist
async function addSpamMailAndBlacklistLinks(mail, source) {
  const spamMailCopy = {
    id: mail.id,
    from: mail.from,
    to: mail.to,
    subject: mail.subject,
    body: mail.body,
    labels: mail.labels ? [...mail.labels] : [],
    timeStamp: mail.timeStamp,
    mailType: mail.mailType,
    owner: mail.owner,
    isRead: mail.isRead,
    isStarred: mail.isStarred,
    source : source,
  };

  // search for links in the mail fields
  const fieldsToCheck = [mail.subject, mail.body, ...(mail.labels || [])];
  const links = extractLinksFromFields(fieldsToCheck);

  for (const link of links) {
    try {
      const result = await blacklistModel.addToBlacklist(link);
      if (!result) {
        // If the link was not added to the blacklist, it means it already exists
        continue;
      }
    } catch (err) {
      return false;
    }
  }

  spamMails.push(spamMailCopy);
  return true;
}

// extractLinksFromFields function extracts links from the given fields
function extractLinksFromFields1(fields) {
  const linksSet = new Set();

  // Strict pattern – matches only valid URLs (full match)
  const strictPattern =
    /^((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$/;

  // Loose pattern – finds possible URL-like strings within text
  const urlPattern =
    /((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,}(\/\S*)?)/g;

  for (const field of fields) {
    let match;
    while ((match = urlPattern.exec(field)) !== null) {
      let candidate = match[0];

      // Clean up unwanted characters from the beginning and end of the match
      // Removes things like quotes, asterisks, brackets, etc., but keeps URL-valid characters
      candidate = candidate.replace(/^[^\w(https?:\/\/)]+|[^\w\/]+$/g, "");

      // Add only if the cleaned string matches the strict pattern for a valid URL
      if (strictPattern.test(candidate)) {
        linksSet.add(candidate);
      }
    }
  }

  return [...linksSet];
}

function extractLinksFromFields55(fields) {
  const linksSet = new Set();

  //  Strict pattern – matches only valid URLs (full match)
  const strictPatternInner =
    /((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?/g;

  for (const field of fields) {
    let match;
    while ((match = strictPatternInner.exec(field)) !== null) {
      linksSet.add(match[0]);
    }
  }

  return [...linksSet];
}

function extractLinksFromFields(fields) {
  const linksSet = new Set();

  for (const field of fields) {
    //  Split the field by whitespace to get individual tokens
    const options = field.split(/\s+/);
    // const options = field.split(/[\s\n\r]+/);
    for (const option of options) {
      if (isUrlValid(option)) {
        linksSet.add(option);
      }
    }
  }

  return [...linksSet];
}

// getSpamById function retrieves a spam mail by its id and owner username
function getSpamById(id, username) {
  return spamMails.find((spam) => spam.id === id && spam.owner === username);
}

// getAllSpamByUser function retrieves all spam mails for a specific user
function getAllSpamByUser(username) {
  return spamMails
    .filter((spam) => spam.owner === username)
    .sort((a, b) => new Date(b.timeStamp) - new Date(a.timeStamp));
}

// deleteSpamById function deletes a spam mail by its id and owner username
async function deleteSpamById(id, username) {
  const index = spamMails.findIndex(
    (spam) => spam.id === id && spam.owner === username
  );
  if (index === -1) return false;

  const mail = spamMails[index];

  // extract all candidate links
  const fields = [mail.subject, mail.body, ...(mail.labels || [])];
  const candidateLinks = extractLinksFromFields(fields);

  // get current blacklist
  const blacklist = blacklistModel.getUrls();

  // check urls in the blacklist if they are in the spam mail
  for (const url of blacklist) {
    const mailsModel = require("./mailsModel");
    if (mailsModel.searchMailContentInsensitive(url.url, mail)) {
      try {
        const removed = await blacklistModel.removeFromBlacklist(url.id);
        if (!removed) {
          console.warn(`Failed to remove ${url.url} from blacklist`);
          return false; // If any link removal fails, return false
        }
      } catch (err) {
        return false;
      }
    }
  }

  // remove mail from spam
  try {
    spamMails.splice(index, 1);
    return true;
    // in controller add to mail
  } catch (error) {
    return false;
  }

  /*// remove only those that are in the blacklist
  for (const link of candidateLinks) {
    if (urlsInBlacklist.includes(link)) {
      const entry = blacklist.find((e) => e.url === link);
      if (entry) {
        const removed = await blacklistModel.removeFromBlacklist(entry.id);
        if (!removed) {
          console.warn(`Failed to remove ${entry.url} from blacklist`);
          return false; // If any link removal fails, return false
        }
      }
    }
  }
  // remove mail from spam
  try {
    spamMails.splice(index, 1);
    return true;
    // in controller add to mail
  }
  catch (error) {
    return false;
  }*/
}
// finalDeleteSpamById function deletes a spam mail by its id and owner username without return to inbox
async function finalDeleteSpamById(id, username) {
  const index = spamMails.findIndex(
    (spam) => spam.id === id && spam.owner === username
  );
  if (index === -1) return false;

  try {
    spamMails.splice(index, 1);
    return true;
  } catch (error) {
    return false;
  }
}

function isUrlValid(url) {
  const pattern =
    /^((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$/;
  return pattern.test(url);
}

function markReadSpam(spam) {
  try {
    if (!spam) return false;
    spam.isRead = true;
    return true;
  } catch (error) {
    return false; // Return false if while marking as read fails
  }
}

function markUnreadSpam(spam) {
  try {
    if (!spam) return false;
    spam.isRead = false;
    return true;
  } catch (error) {
    return false; // Return false if while marking as unread fails
  }
}

function editLabelsInSpam(id, username, newLabels) {
  // Find the draft by id
  const spam = spamMails.find((spam) => spam.id === id && spam.owner === username);
  // Update the draft properties
  try {
    if (newLabels !== undefined) {
      const uniqueLabels = [
        ...new Set(newLabels.filter((label) => typeof label === "string")),
      ];
      spam.labels = uniqueLabels;
    }
  } catch (e) {
    return false;
  }
  return true;
}

function markStarredSpam(spam) {
  try {
    spam.isStarred = true;
    return true;
  } catch (error) {
    return false; // Return false if while marking as starred fails
  }
}

function markUnstarredSpam(spam) {
  try {
    spam.isStarred = false;
    return true;
  } catch (error) {
    return false; // Return false if while marking as unstarred fails
  }
}
module.exports = {
  addSpamMailFromNew,
  createCopyOfSpam,
  getSpamById,
  getAllSpamByUser,
  deleteSpamById,
  addSpamMailAndBlacklistLinks,
  finalDeleteSpamById,
  markReadSpam,
  markUnreadSpam,
  editLabelsInSpam,
  markStarredSpam,
  markUnstarredSpam,
};
