

create table localisation (
  id int8 not null unique,
  version int8 not null,

  created timestamp not null,
  createdBy varchar(256),
  modified timestamp not null,
  modifiedBy varchar(256),

  xkey varchar(512) not null,
  xlanguage varchar(32) not null,
  xvalue text,

  primary key (id),
  unique (xkey, xlanguage)
);

CREATE INDEX localisation_key_idx ON localisation (xkey);
CREATE INDEX localisation_language_idx ON localisation (xlanguage);
