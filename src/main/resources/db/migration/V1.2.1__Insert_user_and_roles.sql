INSERT INTO bibweb_user (username, password) VALUES ('user1', '{bcrypt}$2a$04$lQy2JmnTrdB7Nyv/XadQZ.nRiUcXGbXuRcnjPolBC9nYiDI2iJn3i');
INSERT INTO bibweb_user (username, password) VALUES ('user2', '{bcrypt}$2a$04$guukvXyZTW4Rm2g3nhf.V.NwZxVdiitWbSvpg5q.6.gTvAq6.35jm');
INSERT INTO bibweb_user (username, password) VALUES ('admin', '{bcrypt}$2a$04$K4n6dpeDK4RXp.t/HY6tDOtfzmEoQzytd9zoBoZ0MzfNNYgwV51sO');

INSERT INTO role (rolename) VALUES('ADMIN');
INSERT INTO role (rolename) VALUES('USER');

INSERT INTO user_roles(user_id, role_id) VALUES(1,2);
INSERT INTO user_roles(user_id, role_id) VALUES(3,1);
INSERT INTO user_roles(user_id, role_id) VALUES(3,2);