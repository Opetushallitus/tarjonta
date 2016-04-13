var app = angular.module('app.import.ctrl', []);
app.directive('fileSelect', function() {
    var tpl = '<input class="form-control" type="file" name="files" />';
    return function(scope, elem, attrs) {
        var d = $(tpl);
        elem.prepend(d);
        d.bind('change', function(event) {
            scope.$apply(function() {
               scope[attrs.fileSelect] = event.originalEvent.target.files;
            });

        });
        scope.$watch(attrs.fileSelect, function(file) {
            if (file == null) {
                d.val(file);
            }
            scope.readFile();
        });
    };
}).controller('ImportController', function($scope, XLSXReaderService) {
    $scope.importedApplicationSystems = [];
    $scope.importedEducations = [];
    $scope.errors = [];
    $scope.documentLoaded = false;
    $scope.documentParsed = false;
    $scope.selected = {};
    $scope.file = null;
    $scope.importableEducationTypes = [
        {name: 'Tutkintoon johtamaton korkeakoulutus', id: 'KORKEAKOULUOPINTO', sampleFile: 'korkeakouluopinto.xlsx'}
    ];

    var aoId2Row = {},
        eduId2Row = {},
        educationDependencies = null;

    /**
     * Define the magic values
     *
     * Due to the fact that the rows are processed as dependency graph we speak about dependencies.
     * The problem domain language would be "the education X is included in the education Y"
     */
    var idIdx = 0, // column index of the education id
        applicationOptionDependenciesIdx = 3,
        educationDependenciesIdx = 7, // column index of the dependencies of the education
        numberOfColumnsEducationsSheet = 48,
        numberOfColumnsApplicationOptionSheet = 19;

    function trimEndOfArray(arr) {
        arr = arr || [];
        for (var i = arr.length - 1; arr[i] === undefined && i >= 0; i--) {
            arr.pop();
        }
        return arr;
    }

    function reset() {
        aoId2Row = {};
        eduId2Row = {};
        $scope.documentParsed = $scope.documentLoaded = false;
        $scope.errors = [];
        $scope.file = null;
    }
    $scope.reset = reset;

    function toposort(graph) {
        function toposort(nodes, edges) {
            var cursor = nodes.length,
                sorted = new Array(cursor),
                visited = {},
                i = cursor;

            while (i--) {
                if (!visited[i]) visit(nodes[i], i, [])
            }

            return sorted;

            function visit(node, i, predecessors) {
                if (predecessors.indexOf(node) >= 0) {
                    throw new Error('Cyclic dependency: ' + JSON.stringify(node))
                }

                if (visited[i]) {
                    return;
                }
                visited[i] = true;

                // outgoing edges
                var outgoing = edges.filter(function (edge) {
                    return edge[0] === node
                });
                if (i = outgoing.length) {
                    var preds = predecessors.concat(node);
                    do {
                        var child = outgoing[--i][1];
                        visit(child, nodes.indexOf(child), preds);
                    } while (i)
                }

                sorted[--cursor] = node
            }
        }

        function throwIdentifierNotFoundError(identifier) {
            throw new Error('Identifier not defined: ' + identifier);
        }

        function validateEdges(vertices, edges) {
            _.each(edges, function (edge) {
                if (vertices.indexOf(edge[0]) < 0) throwIdentifierNotFoundError(JSON.stringify(edge[0]));
                if (vertices.indexOf(edge[1]) < 0) throwIdentifierNotFoundError(JSON.stringify(edge[1]));
            });
        }

        var vertices = graph.vertices,
            edges = graph.edges;

        validateEdges(vertices, edges);

        return toposort(vertices, edges);
    }

    var parseEducations2DependencyGraph = function (educationRows) {
        var result = {
            vertices: [],
            edges: []
        };
        _(educationRows).forEach(function (row) {
            if (!row.rowdata) {
                return;
            }
            var rawEduId = row.rowdata[idIdx];
            if (!rawEduId) {
                row.ui.hasErrors = true;
                $scope.errors.push("Some fields contain invalid/missing data");
                return;
            }

            var eduId = ("" + rawEduId).trim();
            result.vertices.push(eduId);

            var rawDependencies = row.rowdata[educationDependenciesIdx];
            var dependencies = [];
            if (rawDependencies) {
                rawDependencies = "" + rawDependencies;
                dependencies = rawDependencies.trim().split(",");
            }
            _(dependencies).forEach(function (dependency) {
                result.edges.push([eduId, dependency.trim()])
            });
        });
        return result;
    };

    var validateEducationReferences = function (rows, educations) {
        var keys = _.keys(educations);

        _(rows).forEach(function (row) {
            if (_.isEmpty(row[applicationOptionDependenciesIdx])) {
                return;
            }

            var dependencies = row[applicationOptionDependenciesIdx].split(",");
            dependencies.forEach(function(dependency) {
                if (!_.contains(keys, dependency)) {
                    row.ui.hasErros = true;
                    $scope.errors.push("Reference to non-existing education (" + dependency + ") on row with id: " + row[idIdx]);
                }
            });
        });
    };

    var throwSheetsMissingException = function() {
        throw new Error('Excel sheets missing, required sheets must be named as "Koulutukset" and "Hakukohteet"');
    };

    $scope.readFile = function () {
        $scope.importedEducations = null;
        $scope.documentLoaded = false;
        $scope.importedApplicationSystems = null;
        $scope.errors = [];

        if (!$scope.file) {
            return;
        }

        XLSXReaderService
            .readFile($scope.file[0], true)
            .then(function (xlsx) {
                var sheets = xlsx.sheets;
                try {
                    if (!sheets) {
                        throwSheetsMissingException();
                    }

                    $scope.documentLoaded = true;
                    var educations = sheets["Koulutukset"].data;
                    var applicationOptions = sheets["Hakukohteet"].data;

                    if (!educations || !applicationOptions) {
                        throwSheetsMissingException();
                    }

                    $scope.educationsHeaderRow = trimEndOfArray(_.head(educations).rowdata);
                    var educationsHeaderRowLength = $scope.educationsHeaderRow.length;
                    if (educationsHeaderRowLength != numberOfColumnsEducationsSheet) {
                        throw new Error("Invalid input file, number of columns does not match the expected count (expected: " + numberOfColumnsEducationsSheet + " actual: " + educationsHeaderRowLength + ")");
                    }
                    $scope.educationRows = _.tail(educations)
                        .filter(function(row) { return ! _.isUndefined(row); })
                        .map(function (row) {
                            return {rowdata: trimEndOfArray(row.rowdata), ui: {hasErrors: false}};
                        });
                    educationDependencies = toposort(parseEducations2DependencyGraph($scope.educationRows)).reverse();
                    eduId2Row = _.object(_($scope.educationRows).map(function (row) {
                        return [row.rowdata[idIdx], row];
                    }));

                    $scope.applicationOptionsHeaderRow = trimEndOfArray(_.head(applicationOptions).rowdata);
                    var applicationOptionsHeaderRowLength = $scope.applicationOptionsHeaderRow.length;
                    if (applicationOptionsHeaderRowLength != numberOfColumnsApplicationOptionSheet) {
                        throw new Error("Invalid input file, number of columns does not match the expected count (expected: " + numberOfColumnsApplicationOptionSheet + " actual: " + applicationOptionsHeaderRowLength + ")");
                    }
                    $scope.applicationOptionRows = _.tail(applicationOptions)
                        .filter(function(row) { return !_.isUndefined(row); })
                        .map(function (row) {
                            return {rowdata: trimEndOfArray(row.rowdata), ui: {hasErrors: false}};
                        });
                    validateEducationReferences($scope.applicationOptionRows, eduId2Row);
                    aoId2Row = _.object(_($scope.educationRows).map(function (row) {
                        return [row.rowdata[idIdx], row];
                    }));

                    $scope.documentParsed = true;
                } catch (err) {
                    $scope.hasErrors = true;
                    $scope.errors.push(err.message);
                }

            });
    };

    var applyToKey = function (col, k, v) {
        var prev = col,
            cur = col,
            prev_k = null;

        _(k.split('.')).forEach(function (part) {
            if (!_.contains(_.keys(cur), part)) {
                cur[part] = {};
            }
            prev = cur;
            prev_k = part;
            cur = cur[part];
            return cur[part];
        });


        if (_.isArray(v)) {
            prev[prev_k] = v;
        } else if (_.isObject(v)) {
            _.extend(cur, v);
        } else {
            prev[prev_k] = v;
        }
    };



    var identity = function (value) {
        if (value) {
            if (_.isString(value)) {
                return value.trim();
            }
            return value;
        }
    };

    var setPrice = function (value, result) {
        if (value) {
            result['opintojenMaksullisuus'] = true;
        } else {
            result['opintojenMaksullisuus'] = false;
        }
        return value;
    };

    var splitToDictKeys = function (value) {
        if (value) {
            return splitToObjectKeys(value);
        }
    };

    var splitToObjectKeys = function (value) {
        if (value) {
            return _.object(_.map(value.split(','), function (x) {
                return [x.trim(), 1];
            }));
        }
    };

    var splitToUniqueExternalIdList = function(value) {
        if (value) {
            return _.map(value.split(','), function (x) {
                return {uniqueExternalId: x.trim()};
            });
        }
    };

    var splitToTopicList = function (value) {
        if (value) {
            return _.map(value.split(','), function (x) {
                return {oppiaine: x.trim(), kieliKoodi: 'kieli_fi'};
            });
        }
    };

    var parseToTimestamp = function (value) {
        if (value) {
            return new Date(value);
        }
    };

    var parseKoulutuksenAlkamisPvms = function (value) {
        if (!_.isString(value) && !_.isEmpty()) {
            throw new Error('Invalid value in column "koulutuksen alkamispvm"');
        }
        if (value) {
            var parts = _.filter(value.split(","), function (x) {
                return _.isString(x) && !_.isEmpty(x);
            });

            return parts.map(function (x) {
                return new Date(x.trim());
            });
        }
    };

    var toUriObject = function (value) {
        if (value) {
            if (_.isString(value)) {
                value = value.trim();
            }
            return {uri: value, versio: 1};
        }
    };

    var splitToList = function (value) {
        if (value) {
            return _.map(value.split(','), function(x) { return x.trim(); });
        }
    };

    var parseExternalIdOrOid = function(value) {
        if (value) {
            if (_.isString(value)) {
                value = value.trim();
            }
            return {uniqueExternalId: value.trim()};
        }
    };

    var parseEducationDependencies = function(value, result) {
        if (value) {
            result['koulutusmoduuliTyyppi'] = 'OPINTOJAKSO';
            return _.map(value.split(','), function(x) { return parseExternalIdOrOid(x); });
        } else {
            result['koulutusmoduuliTyyppi'] = 'OPINTOKOKONAISUUS';
        }
    };

    var setOrganisaatio = function (value) {
        if (value) {
            if (_.isString(value)) {
                value = value.trim();
            }
            return {oid: value, nimi: ""};
        }
    };

    var filterDependantFields = function(obj) {
        var result = obj;
        if (_.has(obj, 'koulutuksenAlkamisPvms')) {
            result = _.omit(obj, 'koulutuksenAlkamisvuosi', 'koulutuksenAlkamiskausi');
        }
        if (_.has(obj, 'yhteyshenkilos')) {
            result.yhteyshenkilos = [result.yhteyshenkilos];
        }
        return result;
    };

    var transform = function(rowdata, colId2Key) {
        var result = {};

        for (var i = 0, len = rowdata.length; i < len; i++) {
            var inputValue = rowdata[i];
            var columnTransformations = colId2Key[i];

            if (_.isEmpty(columnTransformations)) {
                continue;
            }

            var key = columnTransformations[0];
            var transformed = columnTransformations[1](inputValue, result);
            if (transformed !== undefined) {
                applyToKey(result, key, transformed);
            }
        }
        return result;
    };

    var mapEducationDataRow2JsonPayload = function(educationRow) {
        var colId2Key = {
            0:  ['uniqueExternalId', identity],                             // Koulutuksen tunniste
            1:  ['tila', identity],                                         // Koulutuksen tila
            2:  ['organisaatio', setOrganisaatio],                          // Tarjoaja
            3:  ['opetusJarjestajat', splitToList],                         // Järjestävät organisaatiot (oidit pilkulla erotettuna)
            4:  ['koulutusohjelma.tekstis.kieli_fi', identity],             // Koulutuksen nimi (fi)
            5:  ['koulutusohjelma.tekstis.kieli_sv', identity],             // Koulutuksen nimi (sv)
            6:  ['koulutusohjelma.tekstis.kieli_en', identity],             // Koulutuksen nimi (en)
            7:  ['sisaltyyKoulutuksiin', parseEducationDependencies],       // Sisältyy kokonaisuuksiin (tunnisteet pilkulla erotettuna)
            8:  ['hakijalleNaytettavaTunniste', identity],                  // Hakijalle näytettävä tunniste
            9:  ['opinnonTyyppiUri', identity],                             // Opinnon tyyppi
            10: ['opintojenLaajuusPistetta', identity],                     // Opintojen laajuus
            11: ['koulutuksenAlkamisvuosi', parseInt],                      // Alkamisvuosi
            12: ['koulutuksenAlkamiskausi', toUriObject],                   // Alkamiskausi
            13: ['koulutuksenAlkamisPvms', parseKoulutuksenAlkamisPvms],    // Koulutuksen alkamispvm
            14: ['koulutuksenLoppumisPvm', parseToTimestamp],               // Koulutuksen loppupvm
            15: ['opetuskielis.uris', splitToDictKeys],                     // Opetuskielet (pilkulla erotettuna) esim. "opetuskielis: {uris: {kieli_fi: 1}}"
            16: ['hintaString', setPrice],                                  // Koulutuksen maksu, if empty -> opintojenMaksullisuus=false, else opintojenMaksullisuus=true
            17: ['opetusAikas.uris', splitToDictKeys],                      // Opetusaika esim. "opetusAikas: {uris: {opetusaikakk_1: 1}}"
            18: ['opetusmuodos.uris', splitToDictKeys],                     // Opetusmuoto esim. "opetusmuodos: {uris: {opetusmuotokk_2: 1}}"
            19: ['opetusPaikkas.uris', splitToDictKeys],                    // Opetuspaikka
            20: ['aihees.uris', splitToDictKeys],                           // Teemat ja aiheet (pilkulla erotettuna)
            21: ['oppiaineet', splitToTopicList],                           // Oppiaineet/Avainsanat (pilkulla erotettuna)
            22: ['opettaja', identity],                                     // Opettaja
            23: ['yhteyshenkilos.nimi', identity],                          // Yhteyshenkilön nimi
            24: ['yhteyshenkilos.sahkoposti', identity],                    // Yhteyshenkilön s-posti
            25: ['yhteyshenkilos.titteli', identity],                       // Yhteyshenkilön tehtävänimike
            26: ['yhteyshenkilos.puhelin', identity],                       // Yhteyshenkilön puhelinnumero
            27: ['kuvausKomoto.SISALTO.tekstis.kieli_fi', identity],        // Opinnon sisältö (fi)
            28: ['kuvausKomoto.SISALTO.tekstis.kieli_sv', identity],        // Opinnon sisältö (sv)
            29: ['kuvausKomoto.SISALTO.tekstis.kieli_en', identity],        // Opinnon sisältö (en)
            30: ['kuvausKomo.TAVOITTEET.tekstis.kieli_fi', identity],       // Opinnon tavoite (fi)
            31: ['kuvausKomo.TAVOITTEET.tekstis.kieli_sv', identity],       // Opinnon tavoite (sv)
            32: ['kuvausKomo.TAVOITTEET.tekstis.kieli_en', identity],       // Opinnon tavoite (en)
            33: ['kuvausKomoto.KOHDERYHMA.tekstis.kieli_fi', identity],                 // Kohderyhmä (fi)
            34: ['kuvausKomoto.KOHDERYHMA.tekstis.kieli_sv', identity],                 // Kohderyhmä (sv)
            35: ['kuvausKomoto.KOHDERYHMA.tekstis.kieli_en', identity],                 // Kohderyhmä (en)
            36: ['kuvausKomoto.EDELTAVAT_OPINNOT.tekstis.kieli_fi', identity],          // Edeltävät opinnot (fi)
            37: ['kuvausKomoto.EDELTAVAT_OPINNOT.tekstis.kieli_sv', identity],          // Edeltävät opinnot (sv)
            38: ['kuvausKomoto.EDELTAVAT_OPINNOT.tekstis.kieli_en', identity],          // Edeltävät opinnot (en)
            39: ['kuvausKomoto.ARVIOINTIKRITEERIT.tekstis.kieli_fi', identity],         // Arviointi (fi)
            40: ['kuvausKomoto.ARVIOINTIKRITEERIT.tekstis.kieli_sv', identity],         // Arviointi (sv)
            41: ['kuvausKomoto.ARVIOINTIKRITEERIT.tekstis.kieli_en', identity],         // Arviointi (en)
            42: ['kuvausKomoto.OPETUKSEN_AIKA_JA_PAIKKA.tekstis.kieli_fi', identity],   // Aika ja paikka (fi)
            43: ['kuvausKomoto.OPETUKSEN_AIKA_JA_PAIKKA.tekstis.kieli_sv', identity],   // Aika ja paikka (sv)
            44: ['kuvausKomoto.OPETUKSEN_AIKA_JA_PAIKKA.tekstis.kieli_en', identity],   // Aika ja paikka (en)
            45: ['kuvausKomoto.LISATIEDOT.tekstis.kieli_fi', identity],                 // Lisätietoja (fi)
            46: ['kuvausKomoto.LISATIEDOT.tekstis.kieli_sv', identity],                 // Lisätietoja (sv)
            47: ['kuvausKomoto.LISATIEDOT.tekstis.kieli_en', identity]                  // Lisätietoja (en)
        };

        return filterDependantFields(transform(educationRow.rowdata, colId2Key));
    };

    var mapApplicationOptionRow2JsonPayload = function(applicationOptionRow) {
        var colId2Key = {
            0: ['uniqueExternalId', identity],                      // Hakukohteen tunniste
            1: ['tila', identity],                                  // Hakukohteen tila
            2: ['hakuOid', identity],                               // Haun OID
            3: ['koulutukset', splitToUniqueExternalIdList],                     // Hakukohteen koulutukset (koulutusten tunnisteet pilkulla erotettuna)
            4: ['hakukohteenNimet.kieli_fi', identity],             // Hakukohteen nimi (fi)
            5: ['hakukohteenNimet.kieli_sv', identity],	            // Hakukohteen nimi (sv)
            6: ['hakukohteenNimet.kieli_en', identity],             // Hakukohteen nimi (en)
            7: ['hakuaikaAlkuPvm', identity],                       // Ilmoittautumisen alkamispvm
            8: ['hakuaikaLoppuPvm', identity],                      // Ilmoittautumisen loppumispvm
            9: ['aloituspaikatLkm', identity],                      // Hakijoille ilmoitettavat aloituspaikat
            10: ['hakuMenettelyKuvaukset.kieli_fi', identity],      // Haku tai ilmoittautumismenettely (fi)
            11: ['hakuMenettelyKuvaukset.kieli_sv', identity],      // Haku tai ilmoittautumismenettely (sv)
            12: ['hakuMenettelyKuvaukset.kieli_en', identity],      // Haku tai ilmoittautumismenettely (en)
            13: ['peruutusEhdotKuvaukset.kieli_fi', identity],      // Peruutusehdot (fi)
            14: ['peruutusEhdotKuvaukset.kieli_sv', identity],      // Peruutusehdot (sv)
            15: ['peruutusEhdotKuvaukset.kieli_en', identity],      // Peruutusehdot (en)
            16: ['lisatiedot.kieli_fi', identity],                  // Lisätietoja hakemisesta tai ilmoittautumisesta (fi)
            17: ['lisatiedot.kieli_sv', identity],                  // Lisätietoja hakemisesta tai ilmoittautumisesta (sv)
            18: ['lisatiedot.kieli_en', identity]                   // Lisätietoja hakemisesta tai ilmoittautumisesta (en)
        };
        return transform(applicationOptionRow.rowdata, colId2Key);
    };

    $scope.processImport = function() {
        var i = 0, j = 0,
            educationsLength = _.keys(eduId2Row).length,
            applicationOptionsLength = $scope.applicationOptionRows.length,
            uploadProcessingLength = educationsLength + applicationOptionsLength,
            deferred = $.Deferred();

        $scope.uploadProgress = 0;
        $scope.uploadInProgress = true;

        var processNextEducation = function(deferred) {
            if (! educationsLength) {
                deferred.resolve();
                return deferred;
            }
            var education = eduId2Row[educationDependencies[i]];
            var handleImportError = function(error) {
                education.ui.hasErrors = true;
                $scope.errors.push(error.responseText || "Unexpected error: " + error);
                $scope.uploadInProgress = false;
                $scope.$apply();
                deferred.reject();
            };
            var payload;
            try {
                payload = mapEducationDataRow2JsonPayload(education);
            } catch (error) {
                return handleImportError(error);
            }
            payload.toteutustyyppi = $scope.selected.education.id;

            $.ajax({
                url: '/tarjonta-service/rest/v1/koulutus',
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(payload)})
                .fail(function(error) {
                    handleImportError(error);
                })
                .done(function() {
                    education.ui.uploadOk = true;
                    i = i + 1;
                    $scope.uploadProgress = parseInt((i / uploadProcessingLength) * 100);
                    $scope.$apply();
                    if (i < educationsLength) {
                        processNextEducation(deferred);
                    } else {
                        deferred.resolve();
                    }
                });
            return deferred;
        };

        var processNextApplicationOption = function(deferred) {
            if (! applicationOptionsLength) {
                deferred.resolve();
                return deferred;
            }
            var applicationOption = $scope.applicationOptionRows[j];
            var payload = mapApplicationOptionRow2JsonPayload(applicationOption);
            $.ajax({
                url: '/tarjonta-service/rest/v1/hakukohde',
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(payload)})
                .fail(function(error){
                    applicationOption.ui.hasErrors = true;
                    $scope.errors.push(error.responseText || "Unexpected error: " + error);
                    $scope.uploadInProgress = false;
                    $scope.$apply();
                    deferred.reject();
                })
                .done(function() {
                    applicationOption.ui.uploadOk = true;
                    j = j + 1;
                    $scope.uploadProgress = parseInt(((i + j) / uploadProcessingLength) * 100);
                    $scope.$apply();
                    if (j < applicationOptionsLength) {
                        processNextApplicationOption(deferred);
                    } else {
                        deferred.resolve();
                    }
                return deferred;
            });

            return deferred;
        };

        processNextEducation(deferred)
            .then(function() { processNextApplicationOption(deferred)})
            .then(function() { $scope.uploadInProgress = false; });
    }

});