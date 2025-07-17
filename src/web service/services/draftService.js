const Draft = require("../models/draftsModel");
const blacklistModel = require("../models/blacklistModel");
const labelsModel = require("../models/labelsModel");
const { v4: uuidv4 } = require("uuid");


const getAllDrafts = async (username) => {
  return Draft.find({ owner: username }).exec();
};

const getDraftById = async (id, username) => {
  return Draft.findOne({ id, owner: username }).exec();
};

const createDraft = async (username, draftData) => {

  const uniqueTo = Array.isArray(draftData.to)
    ? [...new Set(draftData.to.filter((recipient) => typeof recipient === "string"))]
    : [];

  const uniqueLabels = Array.isArray(draftData.labels)
    ? [...new Set(draftData.labels.filter((label) => typeof label === "string"))]
    : [];

  const newDraft = new Draft({
    id: uuidv4(),
    from: username,
    owner: username,
    to: uniqueTo,
    subject: draftData.subject || "",
    body: draftData.body || "",
    labels: uniqueLabels,
    mailType: draftData.mailType || "",
  });

  return await newDraft.save();
};

const editDraft = async (id, username, updateData) => {
  if (updateData.to) {
    updateData.to = [...new Set(updateData.to.filter((r) => typeof r === "string"))];
  }
  if (updateData.labels) {
    updateData.labels = [...new Set(updateData.labels.filter((l) => typeof l === "string"))];
  }
  updateData.timeStamp = new Date().toLocaleString("sv-SE", {
    timeZone: "Asia/Jerusalem",
  });
  return Draft.findOneAndUpdate({ id, owner: username }, updateData, { new: true }).exec();
};



const deleteDraftById = async (id, username) => {
  return Draft.findOneAndDelete({ id, owner: username }).exec();
};


const createCopyOfDraft = async (draft) => {
  try {
    if (!draft) return null;
    const draftObj = draft.toObject();
    delete draftObj._id;
    draftObj.timeStamp = new Date().toLocaleString("sv-SE", {
      timeZone: "Asia/Jerusalem",
    });

    const copy = new Draft(draftObj);
    return copy.save();
  } catch (error) {
    return null;
  }
};



const markReadDraft = async (id, username) => {
  return Draft.findOneAndUpdate({ id, owner: username }, { isRead: true }, { new: true }).exec();
};


const markUnreadDraft = async (id, username) => {
  return Draft.findOneAndUpdate({ id, owner: username }, { isRead: false }, { new: true }).exec();
};


const markStarredDraft = async (id, username) => {
  return Draft.findOneAndUpdate({ id, owner: username }, { isStarred: true }, { new: true }).exec();
};


const markUnstarredDraft = async (id, username) => {
  return Draft.findOneAndUpdate({ id, owner: username }, { isStarred: false }, { new: true }).exec();
};


const editLabelsInDraft = async (id, username, newLabels) => {
  if (!Array.isArray(newLabels)) return null;
  const uniqueLabels = [...new Set(newLabels.filter((l) => typeof l === "string"))];
  return Draft.findOneAndUpdate({ id, owner: username }, { labels: uniqueLabels }, { new: true }).exec();
};

// מחפש טיוטות לפי שאילתה רגישה לאותיות גדולות/קטנות
const filterDraftsByQueryCaseSensitive = async (query, username) => {
  const drafts = await Draft.find({ owner: username }).exec();
  return drafts.filter((draft) => searchDraftContentSensitive(query, draft));
};

// מחפש טיוטות לפי שאילתה לא רגישה לאותיות גדולות/קטנות
const filterDraftsByQueryCaseInsensitive = async (query, username) => {
  const drafts = await Draft.find({ owner: username }).exec();
  return drafts.filter((draft) => searchDraftContentInsensitive(query, draft));
};


const searchDraftContentInsensitive = (query, draft) => {
  if (!draft) return false;
  const q = query.toLowerCase();

  const hasQueryInFields =
    (draft.subject && draft.subject.toLowerCase().includes(q)) ||
    (draft.body && draft.body.toLowerCase().includes(q)) ||
    (draft.from && draft.from.toLowerCase().includes(q));

  const hasQueryInToRecipients =
    draft.to && draft.to.some((r) => r.toLowerCase().includes(q));

  const labelsObject = labelsModel.convertLabelsArrayToObjects(draft.labels);

  const hasQueryInLabels =
    labelsObject &&
    labelsObject.some((label) => label.name && label.name.toLowerCase().includes(q));

  return hasQueryInFields || hasQueryInToRecipients || hasQueryInLabels;
};


const searchDraftContentSensitive = (query, draft) => {
  if (!draft) return false;

  const hasQueryInFields =
    (draft.subject && draft.subject.includes(query)) ||
    (draft.body && draft.body.includes(query)) ||
    (draft.from && draft.from.includes(query));

  const hasQueryInToRecipients =
    draft.to && draft.to.some((r) => r.includes(query));

  const labelsObject = labelsModel.convertLabelsArrayToObjects(draft.labels);

  const hasQueryInLabels =
    labelsObject &&
    labelsObject.some((label) => label.name && label.name.includes(query));

  return hasQueryInFields || hasQueryInToRecipients || hasQueryInLabels;
};


const searchDraftForBlacklistedURLs = (draft) => {
  return blacklistModel.getUrls().some((entry) =>
    searchDraftContentInsensitive(entry.url, draft)
  );
};


const searchDraftsForBlacklistedURLs = (mail) => {
  return blacklistModel.getUrls().some((entry) =>
    searchDraftContentInsensitive(entry.url, mail)
  );
};


const searchDraftWithChangesForBlacklistedURLs = (subject, body, labels, draftExists) => {
  const finalSubject = subject ?? draftExists.subject;
  const finalBody = body ?? draftExists.body;
  const finalLabels = labels !== undefined ? labels : draftExists.labels;

  const copyChangedDraft = {
    id: draftExists.id,
    from: draftExists.from,
    to: draftExists.to,
    subject: finalSubject,
    body: finalBody,
    labels: finalLabels,
    timeStamp: draftExists.timeStamp,
    draftType: draftExists.draftType,
    owner: draftExists.owner,
    isRead: draftExists.isRead,
    isStarred: draftExists.isStarred,
  };

  return searchDraftForBlacklistedURLs(copyChangedDraft);
};


const getDraftsByUser = async (username) => {
  const drafts = await Draft.find({ owner: username }).exec();
  return drafts.sort((a, b) => new Date(b.timeStamp) - new Date(a.timeStamp));
};

module.exports = {
  getAllDrafts,
  getDraftById,
  createDraft,
  editDraft,
  deleteDraftById,
  createCopyOfDraft,
  markReadDraft,
  markUnreadDraft,
  markStarredDraft,
  markUnstarredDraft,
  editLabelsInDraft,
  filterDraftsByQueryCaseSensitive,
  filterDraftsByQueryCaseInsensitive,
  searchDraftContentInsensitive,
  searchDraftContentSensitive,
  searchDraftForBlacklistedURLs,
  searchDraftWithChangesForBlacklistedURLs,
  searchDraftsForBlacklistedURLs,
  getDraftsByUser,
};