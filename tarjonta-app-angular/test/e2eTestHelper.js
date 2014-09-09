var async = require('async');

var testHelper = {

    editInputs: function editInputs(inputs, revert) {
        Object.keys(inputs).forEach(function(key) {
            var input = inputs[key];
            var newVal = revert ? input.val : input.editVal;

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

        driver.executeScript('return tinyMCE.editors.length').then(function(editorCount) {
            for ( var i = 0; i < editorCount; i ++ ) {
                (function(i) {
                    var editorExpr = 'tinyMCE.editors[' + i + ']';
                    var expression = 'return $(' + editorExpr + '.getContainer())' +
                        '.parents("[lisatieto-type]:first").attr("lisatieto-type");';

                    driver.executeScript(expression).then(function (lisatietoType) {
                        if (inputs[lisatietoType]) {
                            map[lisatietoType] = editorExpr;
                        }

                        if ( i === editorCount - 1) {
                            callback(null, map);
                        }
                    });
                })(i);
            }
        });
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

    revertInputs: function revertInputs(inputs) {
        this.editInputs(inputs, true);
    },

    checkValues: function checkValues(inputs, shouldBeEditedValues, done) {
        var keys = Object.keys(inputs);
        var count = 0;

        async.whilst(
            function() {
                return count < keys.length;
            },
            function(callback) {
                var key = keys[count];
                var input = inputs[key];
                var shouldEqual = shouldBeEditedValues ? input.editVal : input.val;

                function check(value) {
                    expect(value).toEqual(shouldEqual);
                    count ++;
                    callback();
                };

                if ( input.getValueCallback ) {
                    input.getValueCallback(input.el, check);
                }
                else if ( input.getValue ) {
                    input.getValue(input.el).then(check);
                }
                else {
                    input.el.getAttribute('value').then(check);
                }
            },
            done
        );
    },

    checkValuesEdited: function(inputs, done) {
        this.checkValues(inputs, true, done);
    },

    checkValuesOriginal: function(inputs, done) {
        this.checkValues(inputs, null, done);
    },

    submitForm: function submitForm() {
        var firstSaveBtn = element.all(by.css('[data-action="koulutus.edit.tallenna.valmis"]')).first();
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
        value.split('|').forEach(function(label) {
            el.element(by.cssContainingText('span', label)).element(by.xpath('..')).$('input').click();
        });
    },

    testEditPage: function testEditPage(inputs) {
        var self = this;

        it('should show edit page for koulutus', function(done) {
            self.map = null; // reset tinyMCE map
            var firstEditBtn = element.all(by.css('[tt="koulutus.review.muokkaa"]')).first();
            firstEditBtn.click();

            async.series(
                [
                    // Aseta kentille muokatut arvot
                    function (callback) {
                        self.editInputs(inputs.perustiedot);
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
                        self.checkValuesEdited(inputs.kuvailevatTiedot.otherFields, callback);
                    },
                    function (callback) {
                        self.checkTinyMCE(inputs.kuvailevatTiedot.tinyMCE, callback);
                    },
                    function (callback) {
                        $('.nav-tabs li[heading="Perustiedot"]').click(); // näytä perustiedot -tab
                        self.checkValuesEdited(inputs.perustiedot, callback);
                    },

                    // Palauta alkuperäiset arvot
                    function (callback) {
                        self.revertInputs(inputs.perustiedot);
                        $('.nav-tabs li[heading="Kuvailevat tiedot"]').click();
                        self.revertInputs(inputs.kuvailevatTiedot.otherFields);
                        self.editTinyMCE(inputs.kuvailevatTiedot.tinyMCE, callback, true);
                    },
                    function (callback) {
                        browser.sleep(500); // Tarvitaan pieni viive tinyMCE:n muokkauksen ja lomakkeen tallentamisen välillä.
                        self.submitForm();
                        callback();
                    },

                    // Tarkista, että alkuperäiset arvot palautettiin
                    function (callback) {
                        self.checkValuesOriginal(inputs.kuvailevatTiedot.otherFields, callback);
                    },
                    function (callback) {
                        self.checkTinyMCE(inputs.kuvailevatTiedot.tinyMCE, callback, true);
                    },
                    function (callback) {
                        $('.nav-tabs li[heading="Perustiedot"]').click(); // näytä perustiedot -tab
                        self.checkValuesOriginal(inputs.perustiedot, callback);
                    }
                ],
                done
            );
        });
    }

};

module.exports = testHelper;