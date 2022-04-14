# BlogCompose
A Simple Blog App using Jetpack Compose, Flow, Navigation Compose, Room and Firebase

## Instructions
1. Download your Firebase configuration file (Enabling authentication and Cloud Firestore)
2. Create or register your application in the Facebook developer console to enable authentication with Facebook and add the codes of:

* facebook_application_id
* facebook_login_protocol_scheme
* facebook_client_token

3. Enable Cloud Vision API in your Google Cloud Platform
4. Deploy this cloud functions [BlogComposeFunctions](https://github.com/Hcnc100/BlogComposeFunctions)

### Note 1
To upload the images, a moderation mechanism was chosen using cloud functions, if the profile image is detected as adult content, it will be marked as invalid and will not be established as a profile image, in the case of uploading a post with the image will be uploaded, but if it is detected that it is adult content it will be censored, just as in this case the content of the post is moderated. Note that no optimal mechanism was found to moderate usernames, so usernames are not validated.

### Note 2
Notifications use cloud Functions
[BlogComposeFunctions](https://github.com/Hcnc100/BlogComposeFunctions)

## Screenshots
### Auth
<p>
  <img src="https://i.imgur.com/oI9IObR.png" alt="auth" width="200"/>
  <img src="https://i.imgur.com/oza8oUO.png" alt="registry" width="200"/>
 </p>

### Empty home

<p>
  <img src="https://i.imgur.com/q2PgiuQ.png" alt="empty post" width="200"/>
  <img src="https://i.imgur.com/A9EV78Z.png" alt="empty notify" width="200"/>
  <img src="https://i.imgur.com/cE2btGx.png" alt="empty profile" width="200"/>
</p>

### Create and details post

<p>
  <img src="https://i.imgur.com/DOY6EBI.png" alt="create post" width="200"/>
  <img src="https://i.imgur.com/1OwN5p6.png" alt="example post" width="200"/>
  <img src="https://i.imgur.com/e4vytt6.png" alt="post no empty" width="200"/>
  <img src="https://i.imgur.com/MxMngxx.png" alt="details post" width="200"/>
</p>

### Notifications
<p>
  <img src="https://i.imgur.com/8pxdMTn.png" alt="notifications" width="200"/>
  <img src="https://i.imgur.com/oAfYX3h.png" alt="notify 2" width="200"/>
</p>
