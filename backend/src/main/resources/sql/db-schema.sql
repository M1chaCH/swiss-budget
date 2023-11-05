CREATE TABLE IF NOT EXISTS registered_user
(
    id              varchar(250) primary key,
    mail            varchar(250) not null unique,
    password        varchar(250) not null,
    salt            varchar(20)  not null,
    mail_password   varchar(250) not null,
    username        varchar(250),
    disabled        bool         not null default false,
    created_at      timestamp    not null default CURRENT_TIMESTAMP,
    last_login      timestamp    not null default CURRENT_TIMESTAMP,
    current_session varchar(250)
);

CREATE TABLE IF NOT EXISTS transaction_meta_data
(
    user_id                   varchar(250) primary key,
    bank                      varchar(250) not null,
    last_import_check         timestamp,
    last_imported_transaction timestamp,
    transactions_folder       varchar(250) not null default 'INBOX',
    FOREIGN KEY (user_id) REFERENCES registered_user (id)
);

CREATE TABLE IF NOT EXISTS verified_device
(
    id         serial primary key,
    user_id    varchar(250) not null,
    user_agent varchar(250) not null,
    FOREIGN KEY (user_id) REFERENCES registered_user (id)
);

CREATE TABLE IF NOT EXISTS mfa_code
(
    id         varchar(250) not null primary key,
    code       int unique,
    user_id    varchar(250) not null,
    expires_at timestamp    not null,
    tries      int default 0,
    user_agent varchar(250) not null,
    FOREIGN KEY (user_id) REFERENCES registered_user (id)
);

-- A tag contains a list of keywords and a list of transactions that were mapped to the tag due
-- to the keywords
-- a tag is also a budget "topic"
CREATE TABLE IF NOT EXISTS tag
(
    id          serial primary key,
    icon        varchar(50)  not null,
    color       varchar(10)  not null,
    name        varchar(250) not null,
    user_id     varchar(250) not null,
    default_tag bool         not null default false,
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
    user_id varchar(250) not null,
    FOREIGN KEY (user_id) REFERENCES registered_user (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE SET NULL
);

-- transactions are the equivalent of a bank transaction
-- (matching keyword id): if the matching keyword is deleted we don't care and just set it to null
--      in the DB, BUT we need to keep in mind to then also delete the tag foreign key in the transaction
CREATE TABLE IF NOT EXISTS transaction
(
    id                  varchar(250) primary key,
    expense             bool             not null,
    transaction_date    date             not null,
    bankAccount         varchar(250)     not null,
    amount              double precision not null check ( amount > 0 ),
    receiver            varchar(250)     not null,
    tag_id              int,
    matching_keyword_id int,
    need_user_attention bool not null default true,
    alias               varchar(250),
    note                varchar(250),
    user_id             varchar(250)     not null,
    FOREIGN KEY (user_id) REFERENCES registered_user (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE SET NULL,
    FOREIGN KEY (matching_keyword_id) REFERENCES keyword (id) ON DELETE SET NULL
);

-- there exists the possibility that a transaction matches with multiple tags. in this case save the first tag in the transaction and all
-- the remaining matches in this table
CREATE TABLE IF NOT EXISTS transaction_tag_duplicate
(
    id                  serial primary key,
    transaction_id      varchar(250) not null,
    tag_id              int          not null,
    matching_keyword_id int          not null,
    FOREIGN KEY (transaction_id) REFERENCES transaction (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE,
    FOREIGN KEY (matching_keyword_id) REFERENCES keyword (id) ON DELETE CASCADE
);

-- stores the mail transactions in the most raw possible form to not lose any data if we delete the
-- actual mail
CREATE TABLE IF NOT EXISTS transaction_mail
(
    id             varchar(250) primary key,
    message_number int          not null,
    from_mail      varchar(250) not null,
    to_mail        varchar(250) not null,
    received_date  timestamp    not null,
    subject        varchar(250) not null,
    raw_message    text         not null,
    transaction_id varchar(250) not null,
    user_id        varchar(250) not null,
    bank           varchar(250) not null,
    FOREIGN KEY (transaction_id) REFERENCES transaction (id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES registered_user (id) ON DELETE CASCADE
);

