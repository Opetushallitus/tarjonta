
--
-- Lisätään "MonikielinenMetadata" taulu
--

create table monikielinen_metadata (
  id int8 not null unique,
  version int8 not null,
  arvo text,
  avain varchar(255),
  kategoria varchar(255),
  uri varchar(255),
  primary key (id),
  unique (avain, kategoria, uri)
);

CREATE INDEX monikielinen_metadata_avain_idx ON monikielinen_metadata (avain);
CREATE INDEX monikielinen_metadata_kategoria_idx ON monikielinen_metadata (kategoria);
CREATE INDEX monikielinen_metadata_kieli_idx ON monikielinen_metadata (uri);
