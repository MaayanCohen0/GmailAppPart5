const multer = require("multer");
const upload = require("../utils/uploadPic");
//const {loadUsers} = require('../models/usersModel');

const userModel = require("../models/usersModel");
const {
  isValidName,
  isValidUsername,
  isValidPassword,
  isValidPhoneNumber,
  isValidBirthDate,
  isValidGender,
  isValidProfilePic,
} = require("../utils/userValidation");
const jwt = require("jsonwebtoken");
const secret = process.env.JWT_SECRET || "supersecret"; // use env var in real apps

// get user by id
exports.getUserById = (req, res) => {
  const { id } = req.params;
  const user = userModel.getUserById(id);

  // not found - return 404 status
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  // found - return his json with his details
  res.status(200).json(user);
};

// get user by id
exports.getUserDetails = (req, res) => {
  //const { id } = req.params;
  const id = req.userId; // comes from the token
  const user = userModel.getUserById(id);

  // not found - return 404 status
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  // found - return his json with his details
  res.status(200).json(user);
};

exports.createUser = (req, res) => {
  upload.single("profilePic")(req, res, async (err) => {
    if (err instanceof multer.MulterError) {
      return res
        .status(400)
        .json({ error: "File upload error: " + err.message });
    } else if (err) {
      return res.status(500).json({ error: "Server error during file upload" });
    }

    const {
      firstName,
      lastName,
      username,
      password,
      phoneNumber,
      birthDate,
      gender,
    } = req.body;

    // Validate file extension
    if (req.file && !isValidProfilePic(req.file.filename)) {
      return res.status(400).json({
        error:
          "Profile picture must be a valid image file (.jpg, .jpeg, .png, .gif)",
      });
    }

    // Validate fields
    if (!isValidName(firstName)) {
      return res.status(400).json({ error: "Invalid first name" });
    }
    if (!isValidName(lastName)) {
      return res.status(400).json({ error: "Invalid last name" });
    }
    if (!isValidUsername(username)) {
      return res.status(400).json({ error: "Invalid username" });
    }
    if (!isValidPassword(password)) {
      return res
        .status(400)
        .json({ error: "Password must be at least 6 characters" });
    }
    if (!isValidPhoneNumber(phoneNumber)) {
      return res.status(400).json({
        error: "Phone number must be exactly 10 digits and starts with 05",
      });
    }
    if (!isValidBirthDate(birthDate)) {
      return res.status(400).json({
        error: "Birth date must be a valid past date in format YYYY-MM-DD",
      });
    }
    if (!isValidGender(gender)) {
      return res
        .status(400)
        .json({ error: "Gender must be one of: male, female, other" });
    }

    // Check if username already exists
    if (userModel.findUserByUsername(username)) {
      return res.status(409).json({ error: "Username already exists" });
    }

    // Determine profile picture path
    let profilePic = "/uploads/default-avatar.png"; // default
    if (req.file) {
      profilePic = `/uploads/${username}/${req.file.filename}`;
    }

    const newUser = userModel.createUser({
      firstName,
      lastName,
      username,
      password,
      phoneNumber,
      birthDate,
      gender,
      profilePic,
    });

    res
      .status(201)
      .location(`/api/users/${newUser.id}`)
      .json({
        message: "User registered successfully",
        user: {
          id: newUser.id,
          firstName: newUser.firstName,
          lastName: newUser.lastName,
          username: newUser.username,
          profilePic: newUser.profilePic,
        },
      });
  });
};

exports.login = async (req, res) => {
  const { username, password } = req.body;

  if (!username || !password) {
    return res.status(400).json({ error: "Missing credentials" });
  }

  userJson = userModel.checkUserByUsernameAndPassword(username, password);
  if (!userJson) {
    return res.status(401).json({ error: "Invalid username or password" });
  }
  const token = jwt.sign({ id: userJson.id }, secret, { expiresIn: "30d" });

  const id = userJson.id;
  //res.status(200).json({ message: "Login successful", id });
  res.status(200).json({
    message: "Login successful",
    token,
    userJson: {
      id: userJson.id,
      username: userJson.username,
      firstName: userJson.firstName,
      lastName: userJson.lastName,
      profilePic: userJson.profilePic,
      phoneNumber: userJson.phoneNumber,
      birthDate: userJson.birthDate,
      gender: userJson.gender,
    },
  });
};

exports.getUserByToken = (req, res) => {
  const userId = req.userId;
  const user = userModel.getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  // found - return his json with his details
  res.status(200).json(user);
};

exports.checkUsernameExists = async (req, res) => {
  try {
    const user = await userModel.findUserByUsername(req.params.username);
    if (!user) return res.status(200).json({ exists: false });
    res.status(200).json({ exists: true });
  } catch (err) {
    res.status(500).json({ error: "Server error" });
  }
};
