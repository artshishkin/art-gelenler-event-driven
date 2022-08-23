CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('ca496e25-08dd-4fef-8eaf-67d02a599807', 'app.user', 'App', 'User');
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('781f2f08-1633-4f0c-87c7-1662a4ad048b', 'app.admin', 'AppKate', 'Admin');
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('30f75050-92fa-48d1-8383-a070ebd23024', 'app.superuser', 'AppArt', 'SuperUser');


insert into documents(id, document_id)
values ('c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 6210305696719765116);
insert into documents(id, document_id)
values ('f2b2d644-3a08-4acb-ae07-20569f6f2a01', 7836132853803420909);
insert into documents(id, document_id)
values ('90573d2b-9a5d-409e-bbb6-b94189709a19', 2534708466246257458);

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(),'ca496e25-08dd-4fef-8eaf-67d02a599807', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(),'781f2f08-1633-4f0c-87c7-1662a4ad048b', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(),'781f2f08-1633-4f0c-87c7-1662a4ad048b', 'f2b2d644-3a08-4acb-ae07-20569f6f2a01', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(), '781f2f08-1633-4f0c-87c7-1662a4ad048b', '90573d2b-9a5d-409e-bbb6-b94189709a19', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(), '30f75050-92fa-48d1-8383-a070ebd23024', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');


