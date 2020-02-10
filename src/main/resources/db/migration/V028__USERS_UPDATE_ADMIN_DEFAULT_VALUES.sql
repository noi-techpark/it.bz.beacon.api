update "user" set name = 'Admin' where username = 'admin' and name is null;
update "user" set surname = 'Admin' where username = 'admin' and surname is null;
update "user" set email = 'admin@localhost' where username = 'admin' and email is null;