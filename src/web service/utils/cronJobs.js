const cron = require("node-cron");
const trashModel = require("../models/trashModel");

function startTrashCleanup() {
  cron.schedule("0 0 * * *", () => {
    const now = Date.now();
    const thirtyDaysAgo = now - 30 * 24 * 60 * 60 * 1000;
    const allTrash = trashModel.getAllTrash();
    allTrash.forEach((mail) => {
      const trashedTime = new Date(mail.trashedAt).getTime();
      if (trashedTime < thirtyDaysAgo) {
        trashModel.finalDeleteTrashById(mail.id, mail.owner);
      }
    });
  });
}

module.exports = { startTrashCleanup };
