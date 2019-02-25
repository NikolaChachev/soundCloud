use soundcloud;

CREATE TABLE users(
username VARCHAR(50) UNIQUE,
password VARCHAR(50) NOT NULL, 
user_type  TINYINT,
user_id  INT   NOT NULL AUTO_INCREMENT PRIMARY KEY
); 

CREATE TABLE songs(
song_id INT AUTO_INCREMENT PRIMARY KEY,
user_id INT NOT NULL,
song_name VARCHAR(100) NOT NULL,
likes INT NOT NULL,
dislikes INT NOT NULL,
is_public BOOLEAN,
file_path VARCHAR(200)
);

CREATE TABLE playlists(
playlist_id INT PRIMARY KEY AUTO_INCREMENT,
user_id INT NOT NULL,
playlist_name VARCHAR(50)
);


CREATE TABLE comments(
comment_id INT PRIMARY KEY AUTO_INCREMENT,
song_id INT NOT NULL,
user_id INT NOT NULL,
text VARCHAR(250),
likes INT,
song_time DATETIME,
real_time DATETIME,
parent_id INT,
CONSTRAINT FOREIGN KEY (song_id) REFERENCES songs(song_id),
CONSTRAINT FOREIGN KEY (user_id) REFERENCES users(user_id)

);


CREATE TABLE favourites(
user_id INT NOT NULL,
song_id INT,
CONSTRAINT FOREIGN KEY (user_id) REFERENCES users(user_id),
CONSTRAINT FOREIGN KEY (song_id) REFERENCES songs(song_id),
CONSTRAINT PRIMARY KEY (user_id,song_id)
);

CREATE TABLE playlists_songs(
playlist_id INT,
song_id INT ,
CONSTRAINT FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id),
CONSTRAINT FOREIGN KEY (song_id) REFERENCES songs(song_id),
CONSTRAINT PRIMARY KEY (playlist_id,song_id)
);


CREATE TABLE users_playlists(
user_id INT,
playlist_id INT ,
CONSTRAINT FOREIGN KEY (user_id) REFERENCES users(user_id),
CONSTRAINT FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id),
CONSTRAINT PRIMARY KEY (user_id,playlist_id)
);
CREATE TABLE users_disliked_songs(
user_id INT,
song_id INT,
CONSTRAINT FOREIGN KEY (user_id) REFERENCES users(user_id),
CONSTRAINT FOREIGN KEY (song_id) REFERENCES songs(song_id),
CONSTRAINT PRIMARY KEY (user_id,song_id)
);
CREATE TABLE followers(
user_id INT,
follower_id INT,
CONSTRAINT FOREIGN KEY (user_id) REFERENCES users(user_id),
CONSTRAINT FOREIGN KEY (follower_id) REFERENCES users(user_id),
CONSTRAINT PRIMARY KEY (user_id,follower_id)
);

CREATE TABLE users_liked_songs(
user_id INT,
song_id INT,
CONSTRAINT FOREIGN KEY (user_id) REFERENCES users(user_id),
CONSTRAINT FOREIGN KEY (song_id) REFERENCES songs(song_id),
CONSTRAINT PRIMARY KEY (user_id,song_id)
);
CREATE TABLE users_history(
user_id INT,
song_id INT,
date_and_time DATETIME,
CONSTRAINT FOREIGN KEY (user_id) REFERENCES users(user_id),
CONSTRAINT FOREIGN KEY (song_id) REFERENCES songs(song_id),
CONSTRAINT PRIMARY KEY (user_id,song_id)
);
ALTER TABLE songs ADD (file_path VARCHAR (200) );
ALTER TABLE users ADD (profile_picture VARCHAR (200) );
ALTER TABLE songs ADD (picture VARCHAR(200));
ALTER TABLE songs ADD(lenght INT);
ALTER TABLE users ADD(email VARCHAR(250));
ALTER TABLE playlists ADD(wallpaper VARCHAR(250));
CREATE TABLE users_reposts(
user_id INT,
song_id INT,
CONSTRAINT FOREIGN KEY(user_id) REFERENCES users(user_id),
CONSTRAINT FOREIGN KEY(song_id) REFERENCES songs(song_id),
CONSTRAINT PRIMARY KEY(user_id,song_id)
);
CREATE TABLE users_liked_playlists(
user_id INT,
playlist_id INT,
CONSTRAINT FOREIGN KEY (user_id) REFERENCES users(user_id),
CONSTRAINT FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id),
CONSTRAINT PRIMARY KEY (user_id,playlist_id)
);























