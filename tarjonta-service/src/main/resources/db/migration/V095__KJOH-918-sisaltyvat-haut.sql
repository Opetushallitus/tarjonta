alter table haku add column parent_haku_id int8 default null;

alter table haku
add constraint parent_haku_constraint
foreign key (parent_haku_id)
references haku;