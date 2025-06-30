// labels.js
// const { getRecentMailsOfUser, editMail } = require("./mailsModel");

const { v4: uuidv4 } = require("uuid");
const labels = [];

const addLabel = (name, userId) => {
  const newLabel = { id: uuidv4(), name, userId };
  labels.push(newLabel);
  return newLabel;
};

const getLabels = (userId) => labels.filter((label) => label.userId === userId);

const getLabelById = (id, userId) => {
  const label = labels.find(
    (label) => label.id === id && label.userId === userId
  );
  return label || null;
};

const getLabelByIdWithoutUserId = (id) => {
  const label = labels.find((label) => label.id === id);
  return label || null;
};

function updateLabelInAllElements(oldName, newName, username) {
  // 1. Mails
  const { getMailsByUser, editLabelsInMail } = require("./mailsModel");
  const mails = getMailsByUser(username);
  mails.forEach((mail) => {
    if (mail.owner !== username) return;
    if (!Array.isArray(mail.labels) || !mail.labels.includes(oldName)) return;

    const updatedLabels = mail.labels.map((lbl) =>
      lbl === oldName ? newName : lbl
    );
    editLabelsInMail(mail.id, username, updatedLabels);
  });

  // 2. Drafts
  const { getDraftsByUser, editLabelsInDraft } = require("./draftsModel");
  const drafts = getDraftsByUser(username);
  drafts.forEach((draft) => {
    if (draft.owner !== username) return;
    if (!Array.isArray(draft.labels) || !draft.labels.includes(oldName)) return;

    const updatedLabels = draft.labels.map((lbl) =>
      lbl === oldName ? newName : lbl
    );
    editLabelsInDraft(draft.id, username, updatedLabels);
  });

  // 3. Spam
  const { getAllSpamByUser, editLabelsInSpam } = require("./spamModel");
  const spams = getAllSpamByUser(username);
  spams.forEach((spam) => {
    if (spam.owner !== username) return;
    if (!Array.isArray(spam.labels) || !spam.labels.includes(oldName)) return;

    const updatedLabels = spam.labels.map((lbl) =>
      lbl === oldName ? newName : lbl
    );
    editLabelsInSpam(spam.id, username, updatedLabels);
  });

  // 4. Trash
  const { getAllTrashByUser, editLabelsInTrash } = require("./trashModel");
  const trash = getAllTrashByUser(username);
  trash.forEach((trashItem) => {
    if (trashItem.owner !== username) return;
    if (!Array.isArray(trashItem.labels) || !trashItem.labels.includes(oldName))
      return;

    const updatedLabels = trashItem.labels.map((lbl) =>
      lbl === oldName ? newName : lbl
    );
    editLabelsInTrash(trashItem.id, username, updatedLabels);
  });
}

function deleteLabelInAllElements(nameToRemove, username) {
  // 1. Mails
  const { getMailsByUser, editLabelsInMail } = require("./mailsModel");
  const mails = getMailsByUser(username);
  mails.forEach((mail) => {
    if (mail.owner !== username) return;
    if (!Array.isArray(mail.labels) || !mail.labels.includes(nameToRemove))
      return;

    const updatedLabels = mail.labels.filter((lbl) => lbl !== nameToRemove);
    editLabelsInMail(mail.id, username, updatedLabels); // Fixed: pass array directly
  });

  // 2. Drafts
  const { getDraftsByUser, editLabelsInDraft } = require("./draftsModel");
  const drafts = getDraftsByUser(username);
  drafts.forEach((draft) => {
    if (draft.owner !== username) return;
    if (!Array.isArray(draft.labels) || !draft.labels.includes(nameToRemove))
      return;

    const updatedLabels = draft.labels.filter((lbl) => lbl !== nameToRemove);
    editLabelsInDraft(draft.id, username, updatedLabels); // Fixed: pass array directly
  });

  // 3. Spam
  const { getAllSpamByUser, editLabelsInSpam } = require("./spamModel");
  const spams = getAllSpamByUser(username);
  spams.forEach((spam) => {
    if (spam.owner !== username) return;
    if (!Array.isArray(spam.labels) || !spam.labels.includes(nameToRemove))
      return;

    const updatedLabels = spam.labels.filter((lbl) => lbl !== nameToRemove);
    editLabelsInSpam(spam.id, username, updatedLabels); // Fixed: pass array directly
  });

  // 4. Trash
  const { getAllTrashByUser, editLabelsInTrash } = require("./trashModel");
  const trash = getAllTrashByUser(username);
  trash.forEach((trashItem) => {
    if (trashItem.owner !== username) return;
    if (
      !Array.isArray(trashItem.labels) ||
      !trashItem.labels.includes(nameToRemove)
    )
      return;

    const updatedLabels = trashItem.labels.filter(
      (lbl) => lbl !== nameToRemove
    );
    editLabelsInTrash(trashItem.id, username, updatedLabels); // Fixed: pass array directly
  });
}
function updateLabel(id, update, userId, username) {
  // Find & update the label itself
  const idx = labels.findIndex((l) => l.id === id && l.userId === userId);
  if (idx === -1) return null;

  const oldName = labels[idx].name;
  labels[idx] = { ...labels[idx], ...update };
  const newName = labels[idx].name;

  updateLabelInAllElements(oldName, newName, username);
  return labels[idx];
}

const deleteLabel = (id, userId, username) => {
  const idx = labels.findIndex((l) => l.id === id && l.userId === userId);
  if (idx === -1) return false;
  const nameToRemove = labels[idx].name;

  // remove the label from the array in-place
  labels.splice(idx, 1);
  deleteLabelInAllElements(nameToRemove, username);

  return true;
};

function convertLabelsArrayToObjects(labelStrings) {
  if (!Array.isArray(labelStrings)) return [];
  return labelStrings.map((label) => ({ name: label }));
}

const searchAllLabelsArray = (labelsArray, userId) => {
  if (!Array.isArray(labelsArray)) return false;
  // Get all label names for this user
  const userLabelNames = labels
    .filter((l) => l.userId === userId)
    .map((l) => l.name);

  // Check if all requested labels exist in user's labels
  return labelsArray.every((labelName) => userLabelNames.includes(labelName));
};
module.exports = {
  addLabel,
  getLabels,
  getLabelById,
  updateLabel,
  deleteLabel,
  searchAllLabelsArray,
  convertLabelsArrayToObjects,
  getLabelByIdWithoutUserId,
};
