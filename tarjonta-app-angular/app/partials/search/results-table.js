
var app = angular.module('ResultsTable', ['ngResource','localisation']);

app.directive('resultsTable',function(LocalisationService, loadingService, $log) {


    var rowsPerAppend = 20; // TODO konfiguraatioon?
    var delayPerAppend = 1; // TODO konfiguraatioon?

    function box(a,b,c) {
    	return a<b ? b : a>c ? c : a;
    }
    
    function initResultsTableBindings(em,scope, root) {
    	// lapsinodejen valitse-kaikki
    	$("input.selectRows", em).click(function(ev){
			var sel = $(ev.currentTarget).is(":checked");
			//console.log("select="+sel, ev.currentTarget.parentNode.parentNode.parentNode);
			$("input.selectRow", $(ev.currentTarget.parentNode.parentNode.parentNode)).prop("checked", sel);
    	});
    	
    	$("input[type=checkbox]", em).click(function(ev){
    		ev.stopPropagation();
    		updateSelection(em, scope);
    		scope.$apply();
    	});

    	// kirjapinovalikot
    	// - sisältö angularilla, sijoittelu jqueyryllä
    	$(".options", em).click(function(ev){
    		ev.preventDefault();
    		ev.stopPropagation();

    		var menu = $(".dropdown-menu", root);
    		
    		// popup-valikon sisältö
    		var row = $(ev.currentTarget.parentNode.parentNode);
    		var hkOid = row.attr("hakukohde-oid");
    		var kmOid = row.attr("koulutus-oid");
    		var tila = row.attr("tila");

    		var menuOptions = [];

		    var selectedRow = {
		    		oid:null,
		    		prefix:null,
		    		nimi:$(".nimi", row).text(),
		    		element:null
		    };
		    
			var snimi = $(".nimi", row).text();
			var sprefix = null;
			var soid = null;
			
    		if (!hkOid && !kmOid) {
    			console.log("row has no oid", row);
    			return; // ei oidia? -> ei näytetä valikkoa
    		}
    	    
			scope.menuOptions = scope.options(hkOid ? hkOid : kmOid, tila, snimi, {
				update: function(state) {
					//console.log("UPDATE");
	    	    	row.attr("tila", nstate);
	    	    	var em = $("td.state", row);
	    	    	em.text(LocalisationService.t("tarjonta.tila."+nstate));
				},
				remove: function() {
					//console.log("DETACH");
					row.detach();
				}
			});

    		scope.$apply();
    		    		
    		// sijoittelu
    		
    		var by = $("#tarjonta-body").offset().top;
    		menu.toggleClass("display-block",true);
    		menu.css("left", box(ev.pageX-4, 0, $(document).width() - menu.width() - 4));
    		menu.css("top", box(ev.pageY-4-by, 0, $(document).height() - menu.height() - 4));
    		
    		    	
    		// automaattinen sulkeutuminen hiiren kursorin siirtyessä muualle
    		menu.mouseenter(function(){
    			var timer = menu.data("popupTimer");
    			if (timer!=null) {
    				clearTimeout(timer);
    				menu.data("timer", null);
    			}
    		});
    		
    		menu.mouseleave(function(){
    			menu.data("timer", setTimeout(function(){
    				menu.toggleClass("display-block",false);
    			}, 500));
    		});
    		
    		$("a", menu).click(function(ev){
    			// jos url on #, estetään selainta seuraamasta linkkiä (oletetaan, että action on määritelty)
    			if ($(ev.currentTarget).attr("href") == "#") {
    				//ev.stopPropagation();
    				ev.preventDefault();
    			}

        		// sulkeutuminen linkkiä klikkaamalla yms.
    			menu.toggleClass("display-block",false);
    		});

    	});

        // valinta riviä klikkaamalla
    	$("td, th", em).click(function(ev){
    		$("input[type=checkbox]", ev.currentTarget.parentNode).trigger("click");
    	});
    	
    	// foldaus
    	$("a.fold",em).click(function(ev){
    		//console.log("fold ", ev.currentTarget);
    		ev.preventDefault();
    		ev.stopPropagation();
    		$(ev.currentTarget.parentElement.parentElement.parentElement).toggleClass("folded");
    	});
   	
    }

    // taulukon renderöinti    
    function resultsToTable(results, props, prefix) {

    	var html = "";
    	for (var ti in results.tulokset) {
    		var tarjoaja = results.tulokset[ti];
    		html = html+"<tbody class=\"folded tresult\" tarjoaja-oid=\""
    			+tarjoaja.oid
    			+"\">"
    			+"<tr class=\"tgroup\"><th colspan=\""+(3 + props.length)+"\">"
    			+"<a href=\"#\" class=\"fold\">"
    			+"<img src=\"img/triangle_down.png\" class=\"folded\"/>"
    			+"<img src=\"img/triangle_right.png\" class=\"unfolded\"/>"
    			+"</a>"
    			+"<input type=\"checkbox\" class=\"selectRows\"/>"
    			+tarjoaja.nimi
    			+"</th></tr>";

    		for (var ri in tarjoaja.tulokset) {
    			var tulos = tarjoaja.tulokset[ri];
                        
                        if(angular.isUndefined(tulos.kausi)){
                            tulos.kausi = {}; //a quick fix for missing data.
                        }
                        
    			html = html+"<tr class=\"tresult\" "+prefix+"-oid=\""+tulos.oid+"\" tila=\""+tulos.tila+"\">"
					+"<td><input type=\"checkbox\" class=\"selectRow\"/>"
					+"<a href=\"#\" class=\"options\"><img src=\"img/icon-treetable-button.png\"/></a>"
					+"<a href=\"#/"+prefix+"/"+tulos.oid+"\" class=\"nimi\">"	// linkki
					+tulos.nimi
					+"</a></td>"
					+"<td>" + (tulos.kausi.fi||tulos.kausi.sv||tulos.kausi.en) + "&nbsp;" + tulos.vuosi + "</td>"; //TODO lokalisoi!

    			for (var pi in props) {
    				var prop = props[pi];
    				html = html + "<td>" + (tulos[prop]==undefined ? "" : (tulos[prop]+"").replace(" ", "&nbsp;")) + "</td>";
    			}

    			html = html
    				+"<td class=\"state\">" + tulos.tilaNimi + "</td>"
    				+"</tr>";
    		}

    		html = html+"</tbody>"
    	}

    	return html;
    }

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
    
    function updateSelection(element, scope) {
    	var oids = [];
    	$("input.selectRow:checked", element).each(function(i, em){
    		var tr = $(em.parentNode.parentNode);
    		oids.push(tr.attr(scope.prefix+"-oid"));
    	});
    	scope.selection = oids;
    }
    
    function appendTableRow(row, em, root, data, cols, sn, scope) {
    	    	
    	if (scope.serial!=sn) {
            //console.log("abort "+prefix+" @ "+new Date());
        	loadingService.afterOperation();
        	scope.$apply();
    		return; // uusi haku -> keskeytetään
    	}
    	
    	var first = row==0;
    	
    	var rdata = {
    			tuloksia:0,
    			tulokset:[]
    	};
    	  	
    	for (var i=0; i<rowsPerAppend && data.tulokset[row]; i++) {
    		rdata.tulokset.push(data.tulokset[row]);
    		row++;
    	}

		rdata.tuloksia = rdata.tulokset.length;

    	var html = $(resultsToTable(rdata, cols, scope.prefix));
    	initResultsTableBindings(html, scope, root);
    	em.append(html);
    	
    	if (data.tulokset.length>row) {
    		setTimeout(function(){
    			appendTableRow(row, em, root, data, cols, sn, scope);
    		},delayPerAppend);
    	} else {
            //console.log("done "+prefix+" @ "+new Date());
        	em.toggleClass("loading", false);    	
        	loadingService.afterOperation();
        	if (!first && !scope.$$phase) {
            	scope.$apply();
        	}
    	}
    }
    
    function createTableHeader(selector, cols) {
    	//console.log("cols", cols);
    	var html = "<tr class=\"header\">"
    		+"<th class=\"nimi\"></th>"
    		+"<th class=\"kausi\">"+LocalisationService.t("tarjonta.hakutulokset.kausi")+"</th>";
    	
    	for (var i in cols) {
    		var c = cols[i];
    		html = html+"<th class=\""+c+"\">"+LocalisationService.t("tarjonta.hakutulokset."+c)+"</th>";
    	}
    	
    	return html
    		+"<th class=\"tila\">"+LocalisationService.t("tarjonta.hakutulokset.tila")+"</th>"
    		+"</tr>";
    }
    
    function initTable(em, root, data, cols, sn, scope) {
    	
    	if (sn!=scope.serial) {
    		return; // uusi haku -> keskeytetään
    	}

    	
    	console.log("INIT ",data);
    	
    	forceClear(em);
        $("input.selectAll", em).prop("checked", false);
        
    	if (data.tuloksia==undefined || data.tuloksia==0) {
    		return;
    	}
    	
    	em.toggleClass("loading", true);
    	loadingService.beforeOperation();

    	em.html(createTableHeader(em, cols));
    	
    	// valitse-kaikki-nappi päälle/pois tulosten mukaan
    	$("input.selectAll", em.parent())
    		.prop("disabled", data.tuloksia==0) // TODO ei toimi, miksi
    		.click(function(ev){
    			var sel = $(ev.currentTarget).is(":checked");
    			//console.log("select/unselect all", sel);
    			$("input.selectRows, input.selectRow", em).prop("checked", sel);

    			updateSelection(em, scope);
        		scope.$apply();
    		});
    	
        updateSelection(em, scope);

        if (data.tuloksia==0) {
    		// TODO näytä "ei tuloksia" tjsp..
        	em.toggleClass("loading", false);
        	loadingService.afterOperation();
    	} else {
    		appendTableRow(0, em, root, data, cols, sn, scope);
    	}
    }
 
	return {
		restrict: 'E',
		templateUrl: 'partials/search/results-table.html',
		replace:true,
		scope: {
			prefix: "@",
			columns: "=",
			content: "=",
			options: "=",
			selection: "="
		},
		controller: function($scope) {
			$scope.valitseKaikkiMsg = LocalisationService.t("tarjonta.toiminnot.valitse_kaikki");
			$scope.serial = 1;			
		},
		
		link: function(scope, element, attrs, controller) {
			console.log("LINK", scope);
			
		    // tyhjentää taulukot hakusivulta poistuessa, estäen angularia jumittamasta ui:ta
		    scope.$on("$destroy", function(){
				forceClear($("table", element));
		    });
		    
			scope.$watch("content", function(val, old){
				scope.serial = scope.serial+1;
				initTable($("table", element), element, val, scope.columns, scope.serial, scope);
				//console.log("UPDATE "+old+" -> ",val);
			});
			
		}
		
	};
	
});