CREATE ROLE mojoz LOGIN ENCRYPTED PASSWORD 'md51e59303897b66983b10ce629f79858cd'
   VALID UNTIL 'infinity';
CREATE DATABASE mojoz
  WITH ENCODING='UTF8'
       OWNER=mojoz
       CONNECTION LIMIT=-1;
