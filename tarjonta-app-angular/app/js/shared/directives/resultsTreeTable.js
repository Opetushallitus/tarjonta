var app = angular.module('ResultsTreeTable', [
    'ngResource',
    'localisation'
]);
/**
 * !!! DOKUMENTAATIO LÖYTYY TIEDOSTON LOPUSTA !!!
 */
app.directive('resultsTreeTable', function(LocalisationService, loadingService, $log) {
    function forceClear(em) {
        // angular koukuttaa jquery-kutsu $(...).clear():in, josta seuraa
        // delete-tapahtuma joka dom-nodelle, joka puolestaan aiheuttaa
        // vakavia suorituskykyongelmia (ui hyytyy n. minuutin ajaksi)
        em.each(function(i, e) {
            while (e.firstChild) {
                e.removeChild(e.firstChild);
            }
        });
    }
    function box(a, b, c) {
        return a < b ? b : a > c ? c : a;
    }
    function controller($scope) {
        $scope.serial = 1;
        $scope.menuOptions = [];
        // VAKIOARVOT
        if (!$scope.columns) {
            $scope.columns = [];
        }
        if (!$scope.columnTitles) {
            $scope.columnTitles = {};
        }
        if (!$scope.model) {
            $scope.model = [];
        }
        if (!$scope.selection) {
            $scope.selection = {};
        }
        if (!$scope.column) {
            $scope.column = [];
        }
        // SCOPE-FUNKTIOIDEN WRAPPERIT
        function getTableEm() {
            return $('table', $scope.element);
        }
        function getSelectAll() {
            return $('input.selectAll', $scope.element);
        }
        function getAppendRows() {
            return $scope.appendRows || 20;
        }
        function getAppendIntervalMs() {
            return $scope.appendIntervalMs || 1;
        }
        function getLink(row) {
            //console.log("DELEGATE getLink", $scope.getLink);
            try {
                return $scope.getLink() && $scope.getLink()(row);
            }
            catch (err) {
                console.log(err);
                return null;
            }
        }
        function getIdentifier(row) {
            //console.log("DELEGATE getIdentifier", $scope.getIdentifier);
            try {
                return $scope.getIdentifier() && $scope.getIdentifier()(row);
            }
            catch (err) {
                console.log(err);
                return null;
            }
        }
        function getChildren(row) {
            //console.log("DELEGATE getChildren", $scope.getChildren);
            try {
                return $scope.getChildren() && $scope.getChildren()(row);
            }
            catch (err) {
                console.log(err);
                return null;
            }
        }
        function escapeHtml(unsafe) {
            return ('' + unsafe)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
        }
        function getContent(row, col) {
            //console.log("DELEGATE getContent", $scope.getContent);
            var ret = $scope.getContent() && $scope.getContent()(row, col);
            try {
                return ret === null || ret === undefined ? '' : escapeHtml(ret);
            }
            catch (err) {
                console.log(err);
                return null;
            }
        }
        function getOptions(row, actions) {
            //console.log("DELEGATE getOptions", $scope.getOptions);
            try {
                return $scope.getOptions() && $scope.getOptions()(row, actions);
            }
            catch (err) {
                console.log(err);
                return null;
            }
        }
        function getCssClass(row, col) {
            //console.log("DELEGATE getCssClass", $scope.getCssClass);
            try {
                return $scope.getCssClass() && $scope.getCssClass()(row, col);
            }
            catch (err) {
                console.log(err);
                return null;
            }
        }
        // KIRJAPINOVALIKKO
        function showMenu(ev) {
            var menu = $('ul.dropdown-menu', $scope.element);
            console.log('menu', [
                ev,
                menu
            ]);
            // TODO päättele nämä jostain jotenkin?
            var xmargin = 32;
            var ymargin = 16;
            var by = $('#tarjonta-body').offset().top;
            menu.toggleClass('display-block', true);
            menu.css('left', box(ev.pageX - xmargin, 0, $(document).width() - menu.width() - xmargin));
            menu.css('top', box(ev.pageY - ymargin - by, 0, $(document).height() - menu.height() - ymargin));
            // automaattinen sulkeutuminen hiiren kursorin siirtyessä muualle
            menu.mouseenter(function() {
                var timer = menu.data('popupTimer');
                if (timer !== null) {
                    clearTimeout(timer);
                    menu.data('popupTimer', null);
                }
            });
            menu.mouseleave(function() {
                menu.data('popupTimer', setTimeout(function() {
                    menu.toggleClass('display-block', false);
                }, 500));
            });
            return menu;
        }
        // VALINTA
        function setSelected(row, selected) {
            handleSelected(row, selected, true);
        }
        function handleSelected(row, selected, doApply) {
            /*console.log('ResultsTreeTable.handleSelected()', [
                row,
                selected,
                doApply
            ]);*/
            var id = getIdentifier(row);
            var cb = row.$checkbox;
            if (cb) {
                cb.prop('checked', selected);
            }
            if (id) {
                // lisää/poista valinta
                if (!$scope.selection[id] && selected) {
                    parents=[];
                    if (row.tarjoajat) {
                        parents = row.tarjoajat;
                    } else {
                        console.log('no parents available! in ', row);
                    }
                    $scope.selection[id] = parents;
                    //console.log('after add: ', $scope.selection);
                } else {
                    delete $scope.selection[id];
                    //console.log('after delete: ', $scope.selection);
                }
            } else {
                // delegoi aliriveille
                var ch = getChildren(row);
                for (var i in ch) {
                    handleSelected(ch[i], selected, false); //Ei haluta $apply()-kutsua tästä lähtevässä rekursiossa
                }
            }
            if (!$scope.$$phase && doApply) {
                $scope.$apply();
            }
        }
        $scope.canSelectAll = function() {
            return $scope.model.length > 0;
        };
        $scope.doSelectAll = function() {
            var sel = getSelectAll().prop('checked');
            for (var i in $scope.model) {
                setSelected($scope.model[i], sel);
            }
        };
        function onToggleSelect(row, ev) {
            var selected = $(ev.target).prop('checked');
            setSelected(row, selected);
            if (!selected) {
                getSelectAll().prop('checked', false);
            }
        }
        // FOLDAUS
        function foldRowTree(root, folded, recurse) {
            if (recurse) {
                root.toggleClass('hidden', folded);
                var childEms = root.data('childElements');
                for (var i in childEms) {
                    foldRowTree(childEms[i], folded, folded);
                }
            }
        }
        function foldOpen(root, first) {
            if (!first) {
                root.toggleClass('hidden', false);
            }
            else {
                root.toggleClass('unfolded', true);
                var childEms = root.data('childElements');
                for (var i in childEms) {
                    foldOpen(childEms[i], false);
                }
            }
        }
        function foldClose(root, first) {
            root.toggleClass('unfolded', false);
            if (!first) {
                root.toggleClass('hidden', true);
            }
            var childEms = root.data('childElements');
            for (var i in childEms) {
                foldClose(childEms[i], false);
            }
        }
        function onToggleFold(row, ev, $row) {
            //var tbody = $(ev.currentTarget.parentElement.parentElement.parentElement);
            var rowBody = $row || $(ev.currentTarget.parentElement.parentElement);
            //rowBody.toggleClass("folded");
            if (!rowBody.data('expanded')) {
                var childEms = [];
                rowBody.data('unfolded', true);
                rowBody.data('expanded', true);
                rowBody.data('childElements', childEms);
                var children = getChildren(row);
                // sama serial jotta useampi haara voi latautua samanaikaisesti
                appendRows(0, children, $scope.serial, rowBody, childEms, rowBody.data('INDENT') + 1, rowBody);
            }
            if (rowBody.hasClass('unfolded')) {
                foldClose(rowBody, true);
            }
            else {
                foldOpen(rowBody, true);
            }
        }
        // SISÄLLÖN ESIKÄSITTELY
        function nextSerial() {
            var serial = $scope.serial + 1;
            $scope.serial = serial;
            return serial;
        }
        function bindRow(row, element, indent, parent) {
            element.data('ROW', row);
            element.data('INDENT', indent);
            element.data('PARENT', parent);
            var cb = $('input[type=checkbox]', element);
            cb.click(function(ev) {
                ev.stopPropagation();
                onToggleSelect(row, ev);
            });
            row.$checkbox = cb;
            $('td, th', element).click(function(ev) {
                //$("input[type=checkbox]", ev.currentTarget.parentNode)
                cb.trigger('click');
            });
            $('a.fold', element).click(function(ev) {
                if (ev.button !== 0) {
                    return;
                }
                ev.preventDefault();
                ev.stopPropagation();
                onToggleFold(row, ev);
            });
            row.$update = function() {
                replaceRow(row, element);
            };
            row.$delete = function() {
                element.detach();
            };
            $('a.options', element).click(function(ev) {
                if (ev.button !== 0) {
                    return;
                }
                ev.preventDefault();
                ev.stopPropagation();
                var actions = {
                    update: function() {
                        console.log('actions.update() is deprecated, use row.$update()');
                        row.$update();
                    },
                    delete: function() {
                        console.log('actions.delete() is deprecated, use row.delete()');
                        row.$delete();
                    }
                };
                var opts = getOptions(row, actions);
                if (opts) {
                    $scope.menuOptions = opts;
                    $scope.$apply();
                    setTimeout(function() {
                        showMenu(ev);
                    });
                }
            });
        }
        // palauttaa listan sarakkeista object-arrayna: [ {id: ..., title: ...}, ... ]
        function aggregateColumns() {
            var ct = $scope.columnTitles();
            var ret = [];
            for (var i in $scope.columns) {
                var title = ct && ct[$scope.columns[i]] ? ct[$scope.columns[i]] : null;
                ret.push({
                    id: $scope.columns[i],
                    title: title ? LocalisationService.t(title) : '[' + $scope.columns[i] + ']'
                });
            }
            return ret;
        }
        // HTML-RENDERÖINTI
        function renderClasses(cs) {
            if (!(cs instanceof Array) || cs.length === 0) {
                return '';
            }
            var ret = null;
            for (var i in cols) {
                var cn = cols[i];
                ret = ret === null ? cn : ret + ' ' + cn;
            }
            return ' class="' + ret + '"';
        }
        function renderTableHeaderHtml() {
            var cols = aggregateColumns();
            var html = '<tr class="header">' + '<th class="main"></th>';
            for (var i in cols) {
                var c = cols[i];
                html = html + '<th class="column-' + c.id + '">' + c.title + '</th>';
            }
            return html + '</tr>';
        }
        function renderTableRowHtml(row, indent) {
            var link = getLink(row);
            var id = getIdentifier(row);
            var html = '<tr' + renderClasses(getCssClass(row)) + '>';
            html = html + '<td';
            if (!link) {
                html = html + ' colspan="' + ($scope.columns.length + 1) + '"';
            }
            html = html + renderClasses(getCssClass(row, null)) + '>';
            for (var i = 0; i < indent; i++) {
                html = html + '<span class="indent"></span>';
            }
            // foldaus
            if (getChildren(row)) {
                html = html + '<a class="fold" href>' + '<img src="img/triangle_down.png" class="folded"/>' +
                    '<img src="img/triangle_right.png" class="unfolded"/>' + '</a> ';
            }
            else {
                html = html + '<span class="leaf">&nbsp;</span>';
            }
            //var selected = id && $scope.selection.indexOf(id) != -1;
            var selected = id && $scope.selection[id];
            html = html + '<input type="checkbox"' + (selected ? 'checked' : '') + '/> ';
            // valikko
            if (getIdentifier(row)) {
                html = html + '<a href class="options"><img src="img/icon-treetable-button.png"/></a> ';
            }
            if (link) {
                html = html + '<a href="' + link + '">' + getContent(row) + '</a>';
            }
            else {
                html = html + getContent(row);
            }
            html = html + '</td>';
            if (link) {
                for (var j in $scope.columns) {
                    var cn = $scope.columns[j];
                    html = html + '<td' + renderClasses(getCssClass(row, cn)) + '>' + getContent(row, cn) + '</td>';
                }
            }
            return html + '</tr>';
        }
        // SISÄLLÖN PÄIVITYS
        function initContent() {
            console.log('ResultsTreeTable.initContent()', [
                $scope.serial,
                $scope.model
            ]);
            // kasvatetaan sarjanumeroa (keskeyttää käynnissäolevan päivityksen)
            $scope.serial = nextSerial();
            // tyhjennetään
            forceClear(getTableEm());
            getSelectAll().prop('checked', false);
            if (!($scope.model instanceof Array)) {
                if ($scope.model !== null && $scope.model !== undefined) {
                    throw new Error('Unsupported model: ' + $scope.model);
                }
                $scope.model = [];
            }
            if ($scope.model.length === 0) {
                return;
            }
            loadingService.beforeOperation();
            getTableEm().toggleClass('loading', true);
            getTableEm().html(renderTableHeaderHtml());
            appendRows(0, $scope.model, $scope.serial, null, [], 0, null);

            // Unfold results if only one group
            if ($scope.model.length === 1) {
                onToggleFold($scope.model[0], null, $scope.model[0].$checkbox.parents('tr:first'));
            }
        }
        function appendRows(index, model, serial, dst, ems, indent, parent) {
            //console.log("ResultsTreeTable.appendRows()",[index, model, serial, dst, ems, indent, parent]);
            var first = index === 0;
            if ($scope.serial != serial) {
                // uusi päivitys käynnistynyt -> keskeytetään
                loadingService.afterOperation();
                $scope.$apply();
                return;
            }
            for (var i = 0; i < getAppendRows() && model.length > index; i++) {
                dst = appendRow(model[index], dst, ems, indent, parent);
                index++;
            }
            if (index === 0) {
                throw new Error('Invalid amount of append-rows: ' + getAppendRows());
            }
            if (model.length > index) {
                setTimeout(function() {
                    appendRows(index, model, serial, dst, ems, indent, parent);
                }, getAppendIntervalMs());
            }
            else {
                getTableEm().toggleClass('loading', false);
                loadingService.afterOperation();
                if (!first && !$scope.$$phase) {
                    $scope.$apply();
                }
            }
        }
        function appendRow(row, dst, ems, indent, parent) {
            //console.log("ResultsTreeTable.appendRow()", [row, dst, ems, indent, parent]);
            var html = $(renderTableRowHtml(row, indent));
            bindRow(row, html, indent, parent);
            ems.push(html);
            if (dst === null) {
                getTableEm().append(html);
                return null;
            }
            else {
                return dst.after(html).next();
            }
        }
        function replaceRow(row, dst) {
            //console.log("ResultsTreeTable.replaceRow()", [row, dst]);
            var indent = dst.data('INDENT');
            var parent = dst.data('PARENT');
            var children = parent ? parent.data('childElements') : [];
            var p = children.indexOf(dst);
            var html = $(renderTableRowHtml(row, indent));
            bindRow(row, html, indent, parent);
            dst.after(html);
            dst.detach();
            if (p >= 0) {
                children[p] = html;
            }
        }
        // tyhjentää taulukot hakusivulta poistuessa, estäen angularia jumittamasta ui:ta
        $scope.$on('$destroy', function() {
            forceClear(getTableEm());
        });
        // sisällön päivitys
        $scope.$watch('model', function(nv, ov) {
            initContent();
        }); // TODO valinnan watch
    }
    return {
        restrict: 'E',
        templateUrl: 'js/shared/directives/resultsTreeTable.html',
        replace: true,
        scope: {
            // perusparametrit
            model: '=',
            // (array) sisältö joka näytetään
            //  - jokaiselle rivi-oliolle luodaan näyttämisen yhteydessä funktiot $update() ja $delete(),
            //    joilla rivi voidaan päivittää tai poistaa (näitä voidaan kutsua mistä tahansa mutta
            //    tällöin tulee aina ottaa huomioon että riviä ei ole välttämättä ehditty vielä näyttää
            //    jolloin em. funktioitakaan ei ole).
            columns: '=',
            // (array) lista sarakkeiden nimistä jotka näytetään (poislukien otsikko), siinä
            //   järjestyksessä kun ne näytetään; HUOM! muutokset sarakkeisiin tulevat voimaan modelin
            //   vaihtuessa, joten näiden mahdollinen piilotus yms. toiminnalisuus on tehtävä
            //   rivi/solu-kohtaisia css-luokkia säätämällä (getCssClass)
            selection: '=',
            // (array) valittujen rivien tunnisteet
            // funktiot modelin tietojen hakemiseen
            getLink: '&',
            // function(row): hakee rivikohtaisen linkin (joka vie kohdesivulle tjsp.) tai kohdefunktion
            //  - jos null|undefined|false, näytetään otsikkorivinä (eli ilman linkkiä)
            getIdentifier: '&',
            // function(row): hakee rivin tunnisteen valintaa varten
            //  - jos null|undefined, valinta(checkbox) valitsee kaikki alarivit
            getChildren: '&',
            // function(row): hakee riville alarivit (array)
            getContent: '&',
            // function(row, col): hakee solun (column!=undefined) tai otsikkorivin (column==undefined) sisällön
            getOptions: '&',
            // function(row,actions): hakee valikkotoiminnot riville (ei kutsuta jos getIdentifier(...)==null)
            //  arrayna, joissa olioita, joilla kullakin propertyt:
            //
            //    title: otsikko (localisoitu), joka näytetään
            //    href:	linkin url, johon siirrytään klikkauksella
            //	  action: js-funktio, joka suoritetaan klikattaessa
            //
            // toiminto voi päivittää tai poistaa rivin kutsumalla sen callback-funktioita, joko rivioliosta
            // tai actions-oliosta (HUOM! deprekoitu toiminto):
            //    row.$update() ( tai actions.update() ): päivittää rivin
            //    row.$delete() ( tai actions.delete() ): poistaa rivin
            //
            // - Valikon sisältö voidaan hakea asynkronisesti siten, että palautetaan tyhjä array, jota
            //   promiset valmistuessaan täydentävät; valikko täydentyy tällöin automaattisesti angularin
            //   logiikan mukaan; esim. hakujen hallinta
            // ulkoasu
            columnTitles: '&',
            // (map) sarakkeiden otsikot muodossa tunniste -> lokalisaatioavain
            getCssClass: '&',
            // function(row, col): palauttaa css-luokan/luokat sarakkeelle (kun col===undefined),
            //   pääsolulle (col===null) tai nimetylle solulle (col!=null|undefined)
            // suorituskykyparametrit
            appendRows: '@',
            // taulukkoon kerrallaan lisättävien rivien määrä (vakio 20)
            appendIntervalMs: '@' // viive rivienlisäyksien välillä (vakio 1 ms)
        },
        link: function(scope, element, attrs, controller) {
            scope.element = element;
        },
        controller: controller
    };
});