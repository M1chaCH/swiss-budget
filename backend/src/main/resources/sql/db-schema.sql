CREATE TABLE IF NOT EXISTS registered_user
(
    id         serial primary key,
    mail       varchar(250) not null unique,
    password   varchar(250) not null,
    salt       varchar(20)  not null,
    username   varchar(250),
    disabled   bool         not null default false,
    created_at timestamp    not null default CURRENT_TIMESTAMP,
    last_login timestamp    not null default CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS session
(
    id             serial primary key,
    session_token  varchar(50) not null unique,
    created_at     timestamp   not null default CURRENT_TIMESTAMP,
    user_id        int          not null unique,
    user_agent     varchar(250) not null,
    remote_address varchar(50) not null,
    stay           bool        not null default false,
    FOREIGN KEY (user_id) REFERENCES registered_user (id)
);

-- A tag contains a list of keywords and a list of transactions that were mapped to the tag due
-- to the keywords
-- a tag is also a budget "topic"
CREATE TABLE IF NOT EXISTS tag
(
    id      serial primary key,
    icon    varchar(50),
    color   varchar(10),
    name    varchar(250) not null,
    user_id int          not null,
    FOREIGN KEY (user_id) REFERENCES registered_user (id) ON DELETE CASCADE
);

-- keywords are used to map transactions to tags
-- if a tag of a keyword is deleted, we don't care, because we want to have a list of keywords,
-- also the ones that are not in a tag currently (can be a reminder for the user to create a
-- new tag with this dangling keyword)
CREATE TABLE IF NOT EXISTS keyword
(
    id      serial primary key,
    keyword varchar(250) not null,
    tag_id  int,
    user_id int          not null,
    FOREIGN KEY (user_id) REFERENCES registered_user (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE SET NULL
);

-- transactions are the equivalent of a bank transaction
-- (matching keyword id): if the matching keyword is deleted we don't care and just set it to null
--      in the DB, BUT we need to keep in mind to then also delete the tag foreign key in the transaction
CREATE TABLE IF NOT EXISTS transaction
(
    id                  serial primary key,
    expense             bool             not null,
    transaction_date    date             not null,
    bankAccount         varchar(250)     not null,
    amount              double precision not null check ( amount > 0 ),
    receiver            varchar(250)     not null,
    tag_id              int,
    matching_keyword_id int,
    alias               varchar(250),
    note                varchar(250),
    user_id             int              not null,
    FOREIGN KEY (user_id) REFERENCES registered_user (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE SET NULL,
    FOREIGN KEY (matching_keyword_id) REFERENCES keyword (id) ON DELETE SET NULL
);

-- stores the mail transactions in the most raw possible form to not lose any data if we delete the
-- actual mail
CREATE TABLE IF NOT EXISTS transaction_mail
(
    id             serial primary key,
    message_number int          not null,
    from_mail      varchar(250) not null,
    to_mail        varchar(250) not null,
    received_date  date         not null,
    subject        varchar(250) not null,
    raw_message    text         not null,
    transaction_id int,
    user_id        int          not null,
    FOREIGN KEY (transaction_id) REFERENCES transaction (id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES registered_user (id) ON DELETE CASCADE
);

