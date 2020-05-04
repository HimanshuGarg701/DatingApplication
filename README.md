## Dating Application:
### Summary:
Dating Application allows different users to communicate with each other if they like each other’s picture.  The whole implementation is done using kotlin and XML inside Android Studio. I used third party library like Groupie, Circle Image View, Picasso and I used Firebase as database for storing user info, photographs and messages. 
### Implementation:
### User Sign Up/ Sign In:
There is a sign in/up function in the very beginning of the application. The users are allowed to make an account. All the information of the user is stored in Firebase Database.

![User sign in and sign up](https://user-images.githubusercontent.com/40909157/56942016-eb982480-6acc-11e9-8736-8e83d98d3d46.png)

### Home Feed Page:
After the user is able to sign in/up we come across the photos of all the current users of the application. We can drop a message to any of the users after liking their picture and they will be able to communicate with us after they like back our photograph. The users pics are displayed with the help of Recycler View.
There is a like and dislike button below the picture of each user and Username in between. There is a ‘chat’ and ‘Signout’ option at the top of the Home feed page.

![Homefeed](https://user-images.githubusercontent.com/40909157/56942109-9c9ebf00-6acd-11e9-8c0e-4890ce58268b.png)

### Recent Chat:
All the recent chats are accessible after clicking on the ‘chat’ option at the top of the Home Feed page. All the users are displayed with the help of Recycler View. And the last message delivered/received is displayed under the username.

![Recent Chat](https://user-images.githubusercontent.com/40909157/56942175-08812780-6ace-11e9-8b57-702ab8c1905b.png)

### Chat Log:
Chat Log is referred to user to user conversation where all the to and from messages are displayed with the help of Recycler View and the implementation of Adapter class became easy with the help of third-party library Groupie.

![ChatLog](https://user-images.githubusercontent.com/40909157/56942256-7e858e80-6ace-11e9-81d0-50957bc1dfa5.png)

### Third Party Libraries:
#### Picasso –
   Picasso is used to fetch image URL from the web and paste the pic to the desired location inside the application.
#### Groupie-
   Groupie makes the access of Recycler View easier because of easy implementation of adapter class using Groupie adapter.
#### Circle Image View-
   This allows to make the images appear in  a circular representation.
