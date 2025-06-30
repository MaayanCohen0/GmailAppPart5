require("dotenv").config({ path: "../../.env" });
const express = require("express");
const bodyParser = require("body-parser");
const usersRoutes = require("./routes/usersRoutes");
const mailsRoutes = require("./routes/mailsRoutes");
const blacklistRoutes = require("./routes/blacklistRoute");
const labelsRoutes = require("./routes/labelsRoute");
const searchRoutes = require("./routes/searchRoute");
const historyRoutes = require("./routes/historyRoute");
const blacklistModel = require("./models/blacklistModel");
const draftsRoutes = require("./routes/draftsRoute");
const starredRoutes = require("./routes/starredRoute");
const spamRoutes = require("./routes/spamsRoute");
const trashRoutes = require("./routes/trashRoute");
const searchAllRoutes = require("./routes/searchAllRoute");
const objectsRoutes = require("./routes/objectsRoute");
const connectDB = require("./config/db");
require("dotenv").config();

const cors = require("cors"); // add
const app = express();
const path = require("path");
const { startTrashCleanup } = require("./utils/cronJobs");
// Connect to MongoDB
connectDB();

// origin for CORS - use the port in .env file or default to 3000
const corsPort = process.env.REACT_APP_FRONTEND_PORT || 3000;
const corsOrigin = `http://localhost:${corsPort}`;

app.use(express.json());
app.use(express.urlencoded({ extended: true })); // form data
app.set("json spaces", 2);
app.use((req, res, next) => {
  res.removeHeader("Date");
  next();
});
app.use(bodyParser.json());
app.use(
  cors({
    origin: corsOrigin,
    methods: ["GET", "POST", "PUT", "DELETE", "PATCH"],
    credentials: true,
  })
); // add

// Serve static files from uploads directory
app.use("/uploads", express.static(path.join(__dirname, "./uploads")));

app.use("/api/history", historyRoutes);
app.use("/api/mails", mailsRoutes);
app.use("/api/drafts", draftsRoutes);
app.use("/api/spam", spamRoutes);
app.use("/api/trash", trashRoutes);
app.use("/api/mails/search", searchRoutes);
app.use("/api/blacklist", blacklistRoutes);
app.use("/api/labels", labelsRoutes);
app.use("/api/starred", starredRoutes);
app.use("/api/searchAll", searchAllRoutes);
app.use("/api/objects", objectsRoutes);
app.use("/api", usersRoutes);

module.exports = app;
const port = process.argv[3] || process.env.BACKEND_PORT;
if (isNaN(port) || port < 1 || port > 65535) {
  console.error(
    "Invalid port number. Please provide a valid port between 1 and 65535."
  );
  process.exit(1);
}
blacklistModel.loadBlacklistFromFile();
startTrashCleanup();
app.listen(port);
