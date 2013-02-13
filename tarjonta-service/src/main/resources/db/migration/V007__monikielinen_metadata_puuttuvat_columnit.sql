--
-- Unohdettuja kolumneja...  "created", "modified"
--

ALTER TABLE monikielinen_metadata
ADD COLUMN created TIMESTAMP NOT NULL DEFAULT NOW(),
ADD COLUMN modified TIMESTAMP NOT NULL DEFAULT NOW();
