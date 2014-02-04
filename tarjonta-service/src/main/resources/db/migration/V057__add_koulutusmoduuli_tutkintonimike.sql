create table koulutusmoduuli_tutkintonimike (
	koulutusmoduuli_id int8 not null,
	koodi_uri varchar(255) not null,
	primary key (koulutusmoduuli_id, koodi_uri)
);

alter table koulutusmoduuli_tutkintonimike
	add constraint FK5F38FC61EDC3A30B 
	foreign key (koulutusmoduuli_id) 
	references koulutusmoduuli;

-- original tutkintonimike columns to a row
INSERT INTO koulutusmoduuli_tutkintonimike (koulutusmoduuli_id, koodi_uri) SELECT kt.id, kt.tutkintonimike FROM koulutusmoduuli kt WHERE kt.tutkintonimike IS NOT NULL;