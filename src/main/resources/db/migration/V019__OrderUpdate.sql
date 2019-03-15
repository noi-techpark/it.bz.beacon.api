alter table public.order_data add column if not exists info_id varchar(255);

alter table order_data drop constraint if exists fkh35r77fclsn5w8extdmrx7imm;
alter table order_data add constraint fkh35r77fclsn5w8extdmrx7imm foreign key (info_id) references info;