const fs = require("fs");
const path = require("path");

const dbPath = path.join(__dirname, "../../../data/users.json");

function loadUsers() {
  try {
    const data = fs.readFileSync(dbPath);
    return JSON.parse(data);
  } catch (e) {
    return {};
  }
}

const { v4: uuidv4 } = require("uuid");
// in memory - we dont save between runs
const registeredUsers = [];

// return all the registers users
const getAllUsers = () => registeredUsers;

// return user by id
const getUserById = (id) => registeredUsers.find((user) => user.id === id);

// return user by username
const findUserByUsername = (username) =>
  registeredUsers.find((user) => user.username === username);

// check if user exists by username and password
const checkUserByUsernameAndPassword = (username, password) =>
  registeredUsers.find(
    (user) => user.username === username && user.password === password
  );

// create a user
const createUser = ({
  firstName,
  lastName,
  username,
  password,
  phoneNumber,
  birthDate,
  gender,
  profilePic,
}) => {
  const newUser = {
    id: uuidv4(),
    firstName,
    lastName,
    username,
    password,
    phoneNumber,
    birthDate,
    gender,
    profilePic,
  };

  // add the new user to the user list
  registeredUsers.push(newUser);
  return newUser;
};

module.exports = {
  getAllUsers,
  getUserById,
  createUser,
  findUserByUsername,
  checkUserByUsernameAndPassword,
};
