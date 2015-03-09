/* Haun hakuajat sortataan alkamispvmn mukaan, joten indeksoidaan alkamispvm */
CREATE INDEX "hakuaika_alkamispvm" ON "hakuaika" ("alkamispvm");