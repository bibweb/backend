INSERT INTO user (id, username, password) VALUES (1, 'user1', '$2a$04$lQy2JmnTrdB7Nyv/XadQZ.nRiUcXGbXuRcnjPolBC9nYiDI2iJn3i'); -- 12345
INSERT INTO user (id, username, password) VALUES (2, 'user2', '$2a$04$guukvXyZTW4Rm2g3nhf.V.NwZxVdiitWbSvpg5q.6.gTvAq6.35jm'); -- abcdef
INSERT INTO user (id, username, password) VALUES (3, 'admin', '$2a$04$K4n6dpeDK4RXp.t/HY6tDOtfzmEoQzytd9zoBoZ0MzfNNYgwV51sO'); -- admin

INSERT INTO role (id, name) VALUES(1, 'ADMIN');
INSERT INTO role (id, name) VALUES(2, 'USER');

INSERT INTO user_roles(user_id, role_id) VALUES(1,2);
INSERT INTO user_roles(user_id, role_id) VALUES(3,1);
INSERT INTO user_roles(user_id, role_id) VALUES(3,2);