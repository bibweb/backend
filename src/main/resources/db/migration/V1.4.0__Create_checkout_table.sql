CREATE TABLE checkout (
  id BIGSERIAL NOT NULL,
  book_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  checkout_date TIMESTAMP NOT NULL,
  due_date TIMESTAMP NOT NULL,
  still_out BOOLEAN NOT NULL,
  PRIMARY KEY(id)
);

ALTER TABLE checkout ADD CONSTRAINT co_book_id FOREIGN KEY(book_id) REFERENCES book(id);
ALTER TABLE checkout ADD CONSTRAINT co_user_id FOREIGN KEY(user_id) REFERENCES bibweb_user(id);

INSERT INTO checkout(id, book_id, user_id, checkout_date, due_date, still_out) VALUES
(1,7,1, NOW(), NOW(), true),
(2,14,2, NOW(), NOW(), true),
(3,18,1, NOW(), NOW(), true);