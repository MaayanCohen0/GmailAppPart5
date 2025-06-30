# 📧 Gmail-Like Web App

This is a full-stack Gmail-style email application built with React, Node.js, and JWT-based authentication. It allows users to register, log in, send emails, organize them with labels, and manage spam or blacklisted messages — all in a clean and responsive interface.

## 🔧 Features

### ✨ Frontend (React)

The frontend is a React application that provides a Gmail-like user interface for sending, receiving, and organizing emails. It includes pages for login, registration, inbox, spam, labels, and profile viewing, all styled with responsive custom CSS.

- User authentication (register, login, logout)

- JWT token handling via AuthContext

- Inbox, Spam, Drafts, and Label Management

- Email composition and editing

- User profile viewing

- Responsive design with CSS customization

- Protected routes based on login state

### 🛠️ Backend (Node.js + Express)

The backend is a Node.js application built with Express.js, following the MVC architecture. It provides RESTful API endpoints for user management, email operations, label handling, and search functionality. Users can register, log in, send and update emails, and manage their own label categories.

- JWT authentication middleware

- User model with secure ID handling

- Mail, Draft, Trash models (send, get, update, delete)

- Label system per user

- Blacklist filtering for disallowed malicious URLs

### 🛠️ Blacklist Server

A blacklist filtering mechanism that efficiently checks whether a URL is part of a blacklist. It integrates with a Bloom Filter TCP server to block blacklisted URLs from being sent. The server supports concurrent requests and ensures safe access to shared resorces using locks where necessary.

## 🔐 Authentication Flow

On login, the server returns a JWT token.

The frontend stores the token and sends it with all protected API requests via the Authorization: Bearer ... header.

The server decodes the token to verify the user and grant access to their data.

# Running the Application

To run the application, you need to navigate to the root directory, and run the docker-compose.

## 1. Execute the following command:

Explanation for command 1 (`docker-compose down`):

You should run this command 1 only if you have previously started your services using Docker Compose.

The command `docker-compose down` stops and removes all the containers, networks, and default volumes that were created by `docker-compose up`. This effectively cleans up your environment by deleting the running containers and freeing resources, so you start fresh next time.


➡️ **Important:** After running command 1, you must run command 2.

If command 1 is not relevant (e.g., you haven't started the containers yet), simply skip it and run command 2 directly.

---

1.
```bash
docker-compose down
```

2.
```bash
docker-compose up --build
```

## 2. Open localhost 3000:

```bash
http://localhost:3000
```


### 🛠️ Environment Variables
The system uses environment variables to configure server addresses, ports, security keys, and other settings. These variables are split between the root directory (/root) and the frontend directory (/frontend).

#### 🔁 Environment Variables in root dir. (./.env)
These are used by the backend servers and infrastructure:

FRONTEND_PORT – Port used by the frontend application.

BACKEND_PORT – Port for the backend server.

SERVER_PORT – Additional server port (e.g., for the blacklist service).

JWT_SECRET – Secret key used for signing JWT tokens.

BITS_ARRAY – Bit array size for the Bloom filter.

HASH_1, HASH_2 – Hash function identifiers for the Bloom filter (you can had more hash functions like "HASH_3, HASH_4, ....").

BLACKLIST_HOST – Hostname for the blacklist service.

#### 🌐 Environment Variables in /frontend (./src/frontend/.env)
These are used by the React frontend application:

REACT_APP_WEBSOCKET_URL – WebSocket URL for communication with the backend.

REACT_APP_FRONTEND_URL – Base URL for the frontend interface.

REACT_APP_BACKEND_URL – URL of the backend API server.

REACT_APP_FRONTEND_PORT / REACT_APP_BACKEND_PORT – Ports for the frontend and backend apps.


# Running Example:

## 📧 Register and Login

<p float="left">
  <img src="https://github.com/user-attachments/assets/780d8404-6d3a-4bb4-be8f-4adcb855308f" alt="Screenshot 1" width="300"/>
  <img src="https://github.com/user-attachments/assets/a2e2b777-68ea-419c-a461-a32c595978ae" alt="Screenshot 2" width="290"/>
  <img src="https://github.com/user-attachments/assets/68e87b7a-5f28-4433-8ca3-39363a50696e" alt="Screenshot 3" width="295"/>
  <img src="https://github.com/user-attachments/assets/ab30e5a7-c02a-46a2-9969-5503d21c3e2e" alt="Screenshot 4" width="300"/>
  <img src="https://github.com/user-attachments/assets/b1f95640-4092-4af7-b35a-f758aa0f5826" alt="Screenshot 5" width="305"/>
</p>

_Requirements and Information:_

Create and retrieve user profiles with details like username, password, name, phone number, birth date, gender, and profile picture.
The system allows creating and retrieving user profiles. Each user has the following fields, each with specific validation rules:

- First name and last name must be strings containing only English letters (A–Z or a–z), with no spaces. For example, "John" or "Doe".
- Username must be a string consisting only of English letters and/or numbers, with no spaces or special characters. The username must also be unique (no duplicates allowed). For example: "john123".

- Password must be at least 6 characters long and must not contain any spaces. For example: "secure1" is valid, but "abc 123" is not.

- Phone number must be exactly 10 digits and start with "05" (matching the Israeli mobile phone number format). For example: "0521234567".

- Birthdate must be a valid date in the past, in the format "YYYY-MM-DD". Future dates are not allowed. For example: "2001-05-30".

- Gender must be one of the following values (case-insensitive): "male", "female", or "other".

- Profile picture must be a string representing a valid image file, ending with .jpg, .jpeg, .png, or .gif. It can be a full URL or a relative file path. This field is required and must follow this format.

## 📧 Viewing profile pic, details, and logout option:

### Click the profile modal on the top right:

<p float="left">
  <img src="https://github.com/user-attachments/assets/39d10b71-1c87-403d-9fc8-cc9839f2ca63" alt="Screenshot 1" width="300"/>
</p>

### Click the details to view the user's details:

<p float="left">
  <img src="https://github.com/user-attachments/assets/a7211383-fe2f-458b-a2a1-6705453aa98e" alt="Screenshot 1" width="500"/>
</p>


## Home Page

After logging in, we get the following screen:

![image](https://github.com/user-attachments/assets/e747ccbe-6979-472f-bc82-7a95465a075e)

The home page also consists of additional components:

*The side menu, which allows switching between pages.
![image](https://github.com/user-attachments/assets/712d3793-b04e-456c-84f9-6770d9953d3f)



*The search bar, which allows a global search of all emails and drafts.

![image](https://github.com/user-attachments/assets/7eb7c361-f3cf-4bea-871d-0fc6519c5334)


*A button to change the mode - dark mode/light mode


![image](https://github.com/user-attachments/assets/bb29a7de-5e6f-460f-86ae-0d7bec8b07fc)

![image](https://github.com/user-attachments/assets/37d16bca-dd0a-4050-86f6-def4d6f4d17a)


*A button to create an email (or draft)

![image](https://github.com/user-attachments/assets/fb0a76ac-7334-4979-b1f7-f6feecc8ebd8)



*Creating, editing, and deleting labels

![image](https://github.com/user-attachments/assets/63077ade-4a94-4221-869d-2fdb6caababb)


*A user details menu and a logout button

![image](https://github.com/user-attachments/assets/eef73057-fb20-46c6-8cf9-97677fc1d19c)

![image](https://github.com/user-attachments/assets/916a2237-dfce-45cd-8b6e-50bbdded1493)


*Inbox

The first screen we see is the inbox screen.



## 📥 Inbox:

The inbox displays all of the user's incoming emails.
It separates incoming emails into two boxes, unread emails and everything else.
Unread emails are colored in a bright yellow, and those that have been read are white.
Each email record in the inbox consists of a sender name, email subject, email content, labels, and sending time.

![image](https://github.com/user-attachments/assets/16a0587e-a74f-40fa-9edd-3d114b0749d4)

In addition to each email record, there are the following buttons (also relevant to the other pages):

![image](https://github.com/user-attachments/assets/07286119-147c-4b4c-b03a-9daecc3265d6)

The 📩 button marks the email as "read". After an email becomes "read", the button changes to ✉️, and this is the button for marking the email as "unread".

The 🗑️ button moves the email to the trash. It can be found on the trash page.

The 🏷️ button allows you to update the email labels. The button opens a window with checkboxes on the labels, and you can mark and remove labels from labels as you wish.

![image](https://github.com/user-attachments/assets/e736b75b-916d-4cb8-bbd4-4e921f38ca90)


The 🚫 button moves the email to spam. It can be found on the spam page.
If there are links in this email, when you move the email to spam, the links in the email will be blacklisted.

Additionally, we will notice the ☆ button at the beginning of the email. 

![image](https://github.com/user-attachments/assets/f7946e40-b885-4ffc-b8a4-dcd89227eff7)

The button marks the email as "starred", and categorizes it into the starred mails group. That is, we will see the email on the Starred page (but it will still appear in the inbox, unlike trash or spam). After being marked, the button will change to ★. If we want to remove the "starred" marking, we will click the button again and return to ☆ and the "unstarred" state.

When you click on the email itself, a view of the email opens (also relevant to the other pages).

![image](https://github.com/user-attachments/assets/1ab04ff2-7a85-44cd-be9c-8941a7ca778a)


## ⭐ Starred Mails

This screen displays all emails and drafts that are in one of the following pages: Inbox, Sent Emails, Drafts, and marked with the ★ button as "starred".
(Note that this page can contain both emails and drafts, and there is a distinction between them).
![image](https://github.com/user-attachments/assets/68bfbc9c-7d6a-4ab5-9916-961c33fc9865)

You can add and remove emails from this page using the ★\☆ button respectively.


## 📤 Sent Mails

This screen displays all emails sent by the user. 

![image](https://github.com/user-attachments/assets/6769ef0d-afe9-4283-ba96-7ac3bd972892)


Each email record displays the recipient's name, email subject, email content, labels, and the time the email was sent. 
The buttons explained above are also present here.



## 📄 Drafts

This screen displays all of the user's drafts.

![image](https://github.com/user-attachments/assets/994b7401-2a7d-426d-b50f-93e8bf234b97)


When we click on one of the drafts, we can continue updating it, and save it as a draft (![image](https://github.com/user-attachments/assets/65e28fae-b397-42d4-88b1-9418a1851268))
or turn it into an email and send it (![image](https://github.com/user-attachments/assets/389e3e9f-df78-4236-9cc6-c6321a18f8f2))

![image](https://github.com/user-attachments/assets/1752a50a-fd5e-4648-baa6-a9ea6dcf28eb)

.
## Compose Button

To create a new email (or draft), click the button.

![image](https://github.com/user-attachments/assets/77ad0d2f-c956-465d-aeb5-1fb45dd7d715)


After clicking the button, the following window opens:

![image](https://github.com/user-attachments/assets/ebbe46e8-f6e1-4ef9-ad97-52d19a631ea5)


In the window, write the required email. Note that it is mandatory to fill in the "To" field both when creating an email and when creating a draft.
When you want to send to several recipients, separate the names with commas. For example: name1, name2, name3, ...

![image](https://github.com/user-attachments/assets/d448b433-3393-485c-ae7c-33454b79c211)


When you want to create a new email, click the "send" button. 
![image](https://github.com/user-attachments/assets/1892ecb9-806e-44ab-9e47-5ea34cf902ce)

When you click the send button, there is a shot of encouragement to send, and confetti is thrown into the air.


![Screenshot 2025-06-26 155530](https://github.com/user-attachments/assets/70bbe832-60b6-45c3-b4ce-487aaf7f7289)



When you want to save it as a draft, click the return button.
![image](https://github.com/user-attachments/assets/e9246578-d29c-48ee-81af-ef01f7a17eb9)

When you want to exit and not save the email at all, click the X.

![image](https://github.com/user-attachments/assets/2f7f5437-a0e4-42a6-84c0-4dc1100a3928)

When you want to minimize the window, click the minus button.

![image](https://github.com/user-attachments/assets/47260520-c1bf-4698-97dd-ca2936f0e6b2)

After minimizing, the email will be at the bottom of the screen on the left.

![image](https://github.com/user-attachments/assets/af97de68-1d0a-46dd-b178-36e52f716fc2)

You can drag the window anywhere on the screen.

![image](https://github.com/user-attachments/assets/d3efbaa6-9781-4991-90cd-ac7a31c1767e)

![image](https://github.com/user-attachments/assets/e9c10826-67d1-4e4a-bd04-a693e1880bc7)


If the user sends an email that contains a link that is on the blacklist, the email will go to the recipient's spam folder, and for him, as the sender, it will go into the regular inbox except in the case where he sent the email to himself, and he also serves as the recipient, in which case the email will only go into spam.
The link check is only performed when the email is sent, so there are no blacklist link scans in drafts.


## 📧 Mail Operations

### Send an email (from johndoe to joo):

_Requirements and Information:_

Create and send emails with subject, body, and labels.

- "from" and "to" can't be empty.

- "to" and "labels" are arrays.
- Can be sent to a single recipient or multiple recipients.
- When sending, copies are created for each recipient and sender, with the same mail id, but with a different "owner" and a different "mailType".
- The possible "mailType" are: sent, received, sent & received (when someone sends themselves an email - then they are also the recipient).

### Update an existing mail:

_Requirements and Information:_

Editing email content.

- The information is overwritten, so if you want to add something to the existing one, you will have to add the existing one to the request with the change addition.
- The only fields that can be editted are "subject", "body" and "labels".
- If you don't want to change certain fields, you can completely remove them from the request (like the second example).
- The mail is only changed in the copy of the user who updated the mail, not for the other copies of the users involved in the mail.

- You can only add a label to the labels field that exists for that user.

## 🗑️ Trash Operations

- Send mails, drafts, spam, sent and starred mails to trash.
- Permanent deletion - manually by clicking the "delete" button, or automatically after 30 days.
- All mail operations that are permitted in trash on Gmail, are permitted here - that is: Labels editing, mark as read, mark as spam. That excludes starring, which is denied on trashed mails on Gmail.
- Restoring - option to restore a mail, to its location before deletion - whether spam, inbox, or drafts.

### Sending an existing mail to trash-

<p float="left">
  <img src="https://github.com/user-attachments/assets/d32f35cf-e8b5-4b52-86dd-396b4ab89f60" alt="Screenshot 1" width="1000"/>
  <img src="https://github.com/user-attachments/assets/b904e991-d7d1-4bee-a969-75ae77f534c4" alt="Screenshot 1" width="1000"/>
  <img src="https://github.com/user-attachments/assets/7b7e2f05-fd50-4a5f-a7a3-f417ea074945" alt="Screenshot 1" width="1000"/>
</p>

## 🚫 Spam

![image](https://github.com/user-attachments/assets/8fece7e3-68bb-42a3-8dd5-56426ea70e02)

📨 Spam Mail Handling Logic:

1. The Spam screen displays all emails,drafts and trash that contain links listed in a shared blacklist (black list).
   - These links are considered suspicious or harmful.
   - Any email sent with such links will automatically appear in the recipient's Spam folder.
   - If a user sends such an email to themselves, it will also be classified as spam.

2. If a user manually marks an email as spam from another screen (by pressing the "Mark as Spam" button):
   - All links contained in that email will be added to the shared blacklist.
   - This ensures that similar suspicious content is flagged for all users in the system.

3. The ↩️ **Restore** button allows users to remove an email from the Spam folder:
   - When clicked, the email is removed from the Spam screen.
   - It is restored to its original screen (e.g., Inbox, Sent, or Drafts), based on its type.
   - If the email contains links that had previously been added to the blacklist, all of them are removed from the blacklist as part of the restoration process.

✅ This mechanism provides a dynamic and collaborative spam detection system that helps protect all users from malicious content, while also allowing recovery and correction in case of misclassification.

## 🔍 Search Emails

### Search for mails with keyword <STRING_FOR_SEARCH>
![image](https://github.com/user-attachments/assets/ec9d46a3-bdfe-4c54-9a33-72f058b7ff76)
Mail Search Bar

This is the search bar for emails.
The search works on drafts, sent mails, and outgoing mails, and is case-insensitive.
It searches for the typed substring within all fields of each mail.
If a match is found, the mail will be displayed in the results.

![image](https://github.com/user-attachments/assets/55a57268-2a9b-4547-82ff-c2578e753bbd)
Search Suggestions and History Behavior

When hovering or focusing on the search bar, a list of up to six recent search queries is displayed as suggestions. These suggestions represent the most recent search terms used by the user and are stored as local search history.

- Clicking the "❌" icon next to a search term will remove that specific item from the history. The next time the user focuses on the search bar, the list will update to show the most recent six remaining items.
  
- Clicking on the "Clear History" button at the bottom of the list will permanently delete all stored search terms.
  
- Users can click on any of the suggested search terms to immediately perform a new search using that term.

This feature helps users quickly repeat recent searches and manage their search history conveniently.

![image](https://github.com/user-attachments/assets/1b9fa440-315d-4c77-9a7c-d7ce03e662e3)

Live Search Suggestions for Emails

As the user types in the search bar, matching email results are dynamically displayed in real-time. These suggestions include emails that contain the typed text in any of their fields (such as subject, sender, or content).

Each result shows a preview of the email's subject and sender. The user can click on any of the suggested emails to open and view the full message instantly.

This feature helps users quickly locate relevant emails without needing to press Enter or complete the full search term.

![image](https://github.com/user-attachments/assets/ad32dbfc-a881-4679-849a-acb6deaa9f83)

After clicking an email from the suggested results, the full email is displayed. The subject of the selected email is also shown in the search bar above.

![image](https://github.com/user-attachments/assets/00ca5ac0-eb85-4e57-b349-3b6dc0a8a4aa)

To search for something, type your query into the search bar and press Enter. The system will then display all matching results.

If a result is a draft, it will be clearly labeled with the word "Draft" next to it.

![image](https://github.com/user-attachments/assets/a4463005-602f-407f-b648-863d1a4a1132)

If no emails match the search query, a message will appear stating that there are no matching results for the entered query.


## 🏷️ Label Management

_Requirements and Information:_

- Create, retrieve, update, and delete labels associated with a user, which can be attached to emails.
- Click a label to show only inbox mails and drafts that are labeled with that specific label.

### Creating, editing, and deleting a label:

<p float="left">
  <img src="https://github.com/user-attachments/assets/4b497c73-e5ba-437b-9abc-4139354a32a9" alt="Screenshot 1" width="300"/>
</p>

You can view up to 5 labels at a time, and any additional labels you create will be accessible by clicking the "more" button and scrolling.

<p float="left">
  <img src="https://github.com/user-attachments/assets/e26ef747-cebf-44c7-bda1-bbe3774fc488" alt="Screenshot 1" width="300"/>
  <img src="https://github.com/user-attachments/assets/ecfd02fc-f5f1-4020-b9df-a9de9b6f7349" alt="Screenshot 1" width="300"/>
</p>


### Example of before and after clicking a label:
Showing only inbox mails and drafts that are labeled with that specific label.
<p float="left">
  <img src="https://github.com/user-attachments/assets/d0130a8a-4e1f-4a2e-9fd2-396846bf6c16" alt="Screenshot 2" width="1000"/>
  <img src="https://github.com/user-attachments/assets/607f7740-83a1-41e5-9d65-964670467c4d" alt="Screenshot 3" width="1000"/>
</p>
