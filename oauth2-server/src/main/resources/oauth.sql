drop database if exists OAUTH;
CREATE DATABASE OAUTH DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
use OAUTH;
create table oauth_client_details (
  client_id VARCHAR(256) PRIMARY KEY,
  resource_ids VARCHAR(256),
  client_secret VARCHAR(256),
  scope VARCHAR(256),
  authorized_grant_types VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(256)
);
CREATE TABLE oauth_client_token (
  token_id VARCHAR(255),
  token BLOB,
  authentication_id VARCHAR(255),
  user_name VARCHAR(255),
  client_id VARCHAR(255)
);

CREATE TABLE oauth_access_token (
  token_id VARCHAR(255),
  token BLOB,
  authentication_id VARCHAR(255),
  user_name VARCHAR(255),
  client_id VARCHAR(255),
  authentication BLOB,
  refresh_token VARCHAR(255)
);

CREATE TABLE oauth_refresh_token (
  token_id VARCHAR(255),
  token BLOB,
  authentication BLOB
);

CREATE TABLE oauth_code (
  code VARCHAR(255),
  authentication BLOB
);

insert into oauth.oauth_client_details
values
('sybd_znld_test', 'znld-web','E75F5A44F80B2E19D7828ED6F9D7C8AF', 'read,write,execute','client_credentials,password,authorization_code,refresh_token,implicit','http://notexists.com','USER',36000,604800,null,null),
('znld-public','znld-web','E75F5A44F80B2E19D7828ED6F9D7C8AF','read','client_credentials',null,'USER',1800,null,null,null);