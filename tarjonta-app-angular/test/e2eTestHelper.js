var async = require('async');
var Q = require('q');

var testHelper = {

    editInputs: function editInputs(inputs, revert) {
        Object.keys(inputs).forEach(function(key) {
            var input = inputs[key];
            var newVal = input.val;

            if ( input.editFunc ) {
                input.editFunc(input.el, newVal);
            }
            else {
                input.el.clear().sendKeys(newVal);
            }
        });
    },

    mapTinyMCE: function(inputs, callback) {
        if ( this.map ) {
            callback(null, this.map);
            return;
        }
        var map = this.map = {};
        var driver = protractor.getInstance().driver;

        function mapElementAtIndex(i, editorCount) {
            var editorExpr = 'tinyMCE.editors[' + i + ']';
            var expression = 'return $(' + editorExpr + '.getContainer())' +
                '.parents(\'[ng-repeat="lisatieto in lisatiedot"]:first\').find("h4").text();';

            driver.executeScript(expression).then(function (lisatietoType) {
                if (inputs[lisatietoType]) {
                    map[lisatietoType] = editorExpr;
                }

                if ( i === editorCount - 1) {
                    callback(null, map);
                }
            });
        }

        driver.executeScript('return tinyMCE.editors.length').then(function(editorCount) {
            for ( var i = 0; i < editorCount; i ++ ) {
                mapElementAtIndex(i, editorCount);
            }
        });
    },

    languageSelection: {
        editFunc: function (el, editVal) {
            var languages = editVal.split('|');

            el.$$('.combobox span a').then(function (elements) {
                for (var i = 0; i < elements.length; i ++) {
                    elements[0].click();
                }
            });

            languages.forEach(function(lang) {
                if ( lang !== '' ) {
                    el.all(by.cssContainingText('option', lang)).then(function(options) {
                        options.forEach(function(option) {
                            option.getText().then(function(text) {
                                if ( text === lang ) {
                                    option.click();
                                }
                            });
                        });
                    });
                }
            });
        },
        getValue: function (el) {
            return el.$('.combobox').getText().then(function (text) {
                text = text.replace(/\n/g, '');
                var langListTextEnd = 'kafferi, hosazulu';
                text = text.substring(text.indexOf(langListTextEnd) + langListTextEnd.length)
                    .replace(/ \[x\] /g, '|').trim().substring(1);
                return text;
            });
        }
    },

    editTinyMCE: function editTinyMCE(inputs, done, revert) {
        var driver = protractor.getInstance().driver;
        var map = testHelper.map;
        var keys = Object.keys(inputs);
        var count = 0;

        async.whilst(
            function() {
                return count < keys.length;
            },
            function(callback) {
                var key = keys[count];
                var input =  inputs[key];
                var val = revert ? input.val : input.val + '__EDIT__';
                // Muokkaa wysiwyg-tekstiä
                driver.executeScript(map[key] + '.setContent("' + val + '");').then(function() {
                    count ++;
                    callback();
                });
            },
            done
        );
    },

    checkTinyMCE: function checkTinyMCE(inputs, done, revert) {
        var driver = protractor.getInstance().driver;
        var keys = Object.keys(inputs);
        var count = 0;

        async.whilst(
            function() {
                return count < keys.length;
            },
            function(callback) {
                var key = keys[count];

                driver.executeScript('return ' + testHelper.map[key] + '.getContent();').then(function(content) {
                    if ( revert ) {
                        expect(content).not.toMatch('__EDIT__');
                    }
                    else {
                        expect(content).toMatch('__EDIT__');
                    }
                    count ++;
                    callback();
                });
            },
            done
        );
    },

    checkValues: function checkValues(inputs, done) {
        var keys = Object.keys(inputs);
        var count = 0;

        async.whilst(
            function() {
                return count < keys.length;
            },
            function(callback) {
                var key = keys[count];
                var input = inputs[key];
                var shouldEqual = input.val;

                function check(value) {
                    expect(value).toEqual(shouldEqual);
                    count ++;
                    callback();
                }

                if ( input.getValueCallback ) {
                    input.getValueCallback(input.el, check);
                }
                else if ( input.getValue ) {
                    if (input.getValue === 'skip') {
                        check(input.val);
                    }
                    else {
                        input.getValue(input.el).then(check);
                    }
                }
                else {
                    input.el.getAttribute('value').then(check);
                }
            },
            done
        );
    },

    submitForm: function submitForm() {
        var firstSaveBtn = element.all(by.cssContainingText('a', 'Tallenna valmiina')).first();
        firstSaveBtn.click();

        var saveMsg = element.all(by.css('.msgOk p')).first();

        expect(saveMsg.isDisplayed()).toBe(true);
        expect(saveMsg.getText()).toEqual('Tallennettu');
    },

    getValueCallbackMultiSelect: function(el, cb) {
        var foundValues = [];
        el.$$('input:checked + span').then(function(elements) {
            for ( var i = 0; i < elements.length; i ++ ) {
                var el = elements[i];
                el.getText().then(function (text) {
                    foundValues.push(text);
                    if (foundValues.length === elements.length) {
                        cb(foundValues.join('|'));
                    }
                });
            }
        });
    },

    editFuncMultiSelect: function(el, value) {
        // Clear all selected
        el.$$('input').then(function(elements) {
            elements.forEach(function(el) {
                el.getAttribute('checked').then(function(checked) {
                    if (checked) {
                        el.click(); // uncheck checkbox
                    }
                });
            });
        });

       // Select specified checkboxes
       value.split('|').forEach(function (label) {
           el.element(by.cssContainingText('span', label)).element(by.xpath('..')).$('input').click();
       });
    },

    testCreateKoulutus: function (type, inputs, setOid, done) {
        var self = this;

        self.map = null; // reset tinyMCE map

        async.series(
            [
                // Aseta kentille muokatut arvot
                function (callback) {
                    self.editInputs(inputs.perustiedot);
                    // Tallenna perustietojen jälkeen. Vasta tämän jälkeen voi syöttää kuvailevat tiedot
                    self.submitForm();
                    callback();
                },
                function (callback) {
                    protractor.getInstance().driver
                        .executeScript('return angular.element("#editKoulutus").scope().model.oid')
                        .then(setOid);

                    $('.nav-tabs li[heading="Kuvailevat tiedot"]').click(); // näytä kuvailevat tiedot -tab
                    self.mapTinyMCE(inputs.kuvailevatTiedot.tinyMCE, callback);
                },
                function (callback) {
                    self.editInputs(inputs.kuvailevatTiedot.otherFields);
                    self.editTinyMCE(inputs.kuvailevatTiedot.tinyMCE, callback);
                },
                function (callback) {
                    browser.sleep(500); // Tarvitaan pieni viive tinyMCE:n muokkauksen ja lomakkeen tallentamisen välillä.
                    self.submitForm();
                    callback();
                },

                // Tarkista muokatut arvot
                function (callback) {
                    self.checkValues(inputs.kuvailevatTiedot.otherFields, callback);
                },
                function (callback) {
                    self.checkTinyMCE(inputs.kuvailevatTiedot.tinyMCE, callback);
                },
                function (callback) {
                    $('.nav-tabs li[heading="Perustiedot"]').click(); // näytä perustiedot -tab
                    self.checkValues(inputs.perustiedot, callback);
                }
            ],
            done
        );
    },

    testEditKoulutus: function (inputs, done) {
        var self = this;
        self.map = null; // reset tinyMCE map

        async.series(
            [
                // Aseta kentille muokatut arvot
                function (callback) {
                    self.submitForm();
                    callback();
                },
                function (callback) {
                    $('.nav-tabs li[heading="Kuvailevat tiedot"]').click(); // näytä kuvailevat tiedot -tab
                    self.mapTinyMCE(inputs.kuvailevatTiedot.tinyMCE, callback);
                },

                // Tarkista muokatut arvot
                function (callback) {
                    self.checkValues(inputs.kuvailevatTiedot.otherFields, callback);
                },
                function (callback) {
                    self.checkTinyMCE(inputs.kuvailevatTiedot.tinyMCE, callback);
                },
                function (callback) {
                    $('.nav-tabs li[heading="Perustiedot"]').click(); // näytä perustiedot -tab
                    self.checkValues(inputs.perustiedot, callback);
                }
            ],
            done
        );
    },

    testOrganizationSearch: function(organizationName) {
        var organizationSearch = {
            input: element(by.model('hakuehdot.searchStr')),
            btn: $('#orgSearch .buttons a:nth-child(2)'),
            firstResult: $('#orgSearchResults li:first-child span')
        };

        browser.get('/tarjonta-app');

        organizationSearch.input.sendKeys(organizationName);
        organizationSearch.btn.click();

        expect(organizationSearch.firstResult.getText()).toEqual(organizationName);

        organizationSearch.firstResult.click();
    },

    findKoulutusInList: function(oid) {
        var deferred = Q.defer();

        $$('.active .resultsTreeTable a.fold').then(function(elements){
            elements.forEach(function(element) {
                element.click();
            });
        });

        $$('.active .resultsTreeTable a.options + a').then(function(links) {
            links.forEach(function(link) {
                link.getAttribute('href').then(function(href) {
                    if(href.indexOf(oid) !== -1) {
                        deferred.resolve(function() {
                            return link.element(By.xpath('preceding-sibling::a'));
                        });
                    }
                });
            });
        });

        return deferred.promise;
    }
};

module.exports = testHelper;