const trashMails = [];

function addTrashMail(mail, source) {
  // Create a copy to avoid mutating the original object
  const trashedMail = {
    ...mail,
    trashedAt: new Date().toISOString(),
    trashSource: source, // string indicating where it came from
  };

  trashMails.push(trashedMail);
  return trashedMail; // Return the trashed mail for confirmation
}

function getTrashById(id, username) {
  return trashMails.find((mail) => mail.id === id && mail.owner === username);
}

function getAllTrashByUser(username) {
  return trashMails
    .filter((mail) => mail.owner === username)
    .sort((a, b) => new Date(b.trashedAt) - new Date(a.trashedAt));
}

function getAllTrash() {
  return trashMails;
}

function deleteTrashById(id, username) {
  const index = trashMails.findIndex(
    (mail) => mail.id === id && mail.owner === username
  );
  if (index !== -1) {
    trashMails.splice(index, 1);
    return true;
  }
  return false;
}

function finalDeleteTrashById(id, username) {
  return deleteTrashById(id, username);
}

function markReadTrash(trash) {
  try {
    if (!trash) return false;
    trash.isRead = true;
    return true;
  } catch (error) {
    return false;
  }
}

function markUnreadTrash(trash) {
  try {
    if (!trash) return false;
    trash.isRead = false;
    return true;
  } catch (error) {
    return false;
  }
}

function createCopyOfTrash(mail) {
  try {
    const copyMail = { ...mail };
    trashMails.push(copyMail);
    return copyMail;
  } catch (error) {
    return null;
  }
}

function editLabelsInTrash(id, username, newLabels) {
  // Find the trash by id
  const trash = trashMails.find(
    (trash) => trash.id === id && trash.owner === username
  );
  // Update the trash properties
  try {
    if (newLabels !== undefined) {
      const uniqueLabels = [
        ...new Set(newLabels.filter((label) => typeof label === "string")),
      ];
      trash.labels = uniqueLabels;
    }
  } catch (e) {
    return false;
  }
  return true;
}

module.exports = {
  addTrashMail,
  getTrashById,
  getAllTrashByUser,
  getAllTrash,
  deleteTrashById,
  finalDeleteTrashById,
  createCopyOfTrash,
  markReadTrash,
  markUnreadTrash,
  editLabelsInTrash,
};
