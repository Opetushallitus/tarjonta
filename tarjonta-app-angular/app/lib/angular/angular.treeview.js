/*
	Original work:
	
	@license Angular Treeview version 0.1.6
	ⓒ 2013 AHN JAE-HA http://github.com/eu81273/angular.treeview
	License: MIT

    Enhanced by OPH, 

	[TREE attribute]
	angular-treeview: the treeview directive
	tree-id : each tree's unique id.  //NOT implemented
	tree-model : the tree model on $scope. 
	node-id : each node's id //NOT implemented
	node-label : each node's label 
	node-children: each node's children

    <div id="orgSearchResults" class="treeview" data-angular-treeview="true"
         data-tree-model="tulos"
         data-node-label="nimi"
         data-node-children="children" >
    </div>
*/

(function ( angular ) {
	'use strict';

	angular.module( 'angularTreeview', [] ).directive( 'treeModel', ['$compile','$rootScope', function( $compile, $rootScope ) {
		return {
			restrict: 'A',
			link: function ( scope, element, attrs ) {

				
				/**
				 * "piirtää" organisaation dom puussa
				 */
				var redraw = function(org){
					// dom elementin id... 
					var eid = angular.copy(org.oid).replace(/\./g,'-');
					
					var template = drawChildren([org]);
					var dom = $compile(template);
					//poista c-<oid>, päivitä o-<oid>
					//console.log("opening org, template:" , drawChildren([org]));
					//organisaatio auki
					$("#c-" + eid).detach();
					$("#o-" + eid).replaceWith( dom ($rootScope.puut[treeId].scope));
				};
				

				function getOrg(id, list) {
//					console.log("data has:", list.length ," entries");
					for(var i=0;i<list.length;i++) {
						var org = list[i];
//						console.log("comparing:",id," with ", org.oid);
						if(id===org.oid) {
//							console.log("found org!", org);
							return org;
						}
						org = getOrg(id, org.children); //rekursio, flättää?
						if(org!==undefined) {
							return org;
						}
					}
					
//					console.log("Organisaatiota ei löytynyt!", id);
				}

				//liitä scopeen puun handlaamisessa tarvittavat metodit
				/**
				 * Solmu avataan/suljetaan
				 */
				scope.toggleOrg=function(id, element){
//					console.log("toggle valittu!");

//					console.log("id param:", id);
					var org = getOrg(id, $rootScope.puut[treeId].data);
					//console.log("selected org:", org);
					if(org.open===undefined){
						org.open=true;
					} else {
						org.open=!org.open;
					};
					
					redraw(org);
				};

				/**
				 * Organisaatio valitaan
				 */
				scope.selectOrg=function(oid){
//					console.log("organisaatio valittu!", oid);
					var current = $rootScope.puut[treeId].selected;
					
//					console.log("vanha valinta", current);
					if(current!==undefined) {
//						console.log("etsitään vanhaa", current);
						var org = getOrg(current, $rootScope.puut[treeId].data);
//						console.log("vanha:", org);
						if(org!==undefined){
							org.selected=false;
							redraw(org);
							current = undefined;
						}
					}
//					console.log("etsitään uutta");
					var org = getOrg(oid, $rootScope.puut[treeId].data);
//					console.log("uusi:", org);

					org.selected="true";
					$rootScope.puut[treeId].selected=oid;
					redraw(org);
					
					//aseta valittu organisaatio scopeen jotta voidaan watchilla seurata kun organisaatio valitaan puusta
					scope.organisaatio.currentNode=org;
				};
				
				
				var bind = attrs.ng-bind;
				
				//tree id
				var treeId = attrs.treeId;
				console.log("treeid:", treeId);
			
				//tree model
				var treeModel = attrs.treeModel;

				//node id
//				var nodeId = attrs.nodeId || 'id';

				//node label
				var nodeLabel = attrs.nodeLabel || 'label';

				//children
				var nodeChildren = attrs.nodeChildren || 'children';

				var drawChildren=function(children){

					var orgToString = function(eid, oid, label, cssclass, selected){
						return "<li id=\"o-" + eid +  "\" ><i ng-click=\"toggleOrg('" + oid + "')\" class='" + cssclass + "'/></i><span" + (selected?" class=\"selected\"":"") + " ng-click=\"selectOrg('" + oid + "')\">" + label + "</span></li>";
					};

					var template="";
					if(children!==undefined) {
					for(var i=0;i<children.length;i++){
						var org = children[i];
						var hasChildren = org[nodeChildren]!==undefined && org[nodeChildren].length>0;
						var open = org.open===true;
						var eid=angular.copy(org.oid).replace(/\./g,'-'); //element id millä oikea dom node löydetään
						if(hasChildren) {
							if(open) {
								//auki
								template = template + orgToString(eid, org.oid,org[nodeLabel], "expanded", org.selected);
								//lapset
								template = template + "<div id=\"c-" + eid + "\" class=\"treeview\"><ul>" +  drawChildren(org[nodeChildren]) + "</ul></div>";
							} else {
								//kiinni
								template = template + orgToString(eid, org.oid,org[nodeLabel], "collapsed", org.selected);
							}
						} else {
							//lehti
							template = template + orgToString(eid, org.oid,org[nodeLabel], "normal", org.selected);
						}
					};
					}				
					//console.log("template:", template);
					return template;
				};
				
				
				//alusta tietorakenne
				$rootScope.puut=$rootScope.puut||{};
				$rootScope.puut[treeId]=$rootScope.puut[treeId]||{};
				$rootScope.puut[treeId].data = [];
				$rootScope.puut[treeId].selected=undefined;
				$rootScope.puut[treeId].scope = scope;

				/**
				 * Watchi puun datalle
				 */
				scope.$watch(treeModel, function (newList, oldList) {
//					console.log("hakutulos päivittyi!", newList);
					$rootScope.puut[treeId].data = newList;
//					var start = new Date().getTime();
					element.html('');
//					console.log(new Date().getTime()-start + " to clear old result");
//					start = new Date().getTime();
					var template = "<ul>" + drawChildren(newList) + "</ul>";
//					console.log(new Date().getTime()-start + " to create template");
//					start = new Date().getTime();
					var dom = $compile( template );
//					console.log(new Date().getTime()-start + " to compile template");
//					start = new Date().getTime();
					element.html('').append( dom (scope) );
//					console.log(element);
//					console.log(new Date().getTime()-start + " to append dom, from my pow the tree is now done");
					});

			}
		};
	}]);
})( angular );