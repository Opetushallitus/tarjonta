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

	angular.module( 'orgAngularTreeview', [] ).directive( 'orgTreeModel', function( SharedStateService, $compile ) {
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
//					console.log("opening org:", org );
					//organisaatio auki
					$("#" + treeId + "-c-" + eid).detach();
					$("#" + treeId + "-o-" + eid).replaceWith( dom (SharedStateService.state.puut[treeId].scope));
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
				scope.toggleOrg=function(id){
//					console.log("toggle valittu!");

//					console.log("id param:", id);
					var org = getOrg(id, SharedStateService.state.puut[treeId].data);
					//console.log("selected org:", org);
					if(org.open===undefined){
						org.open=true;
					} else {
						org.open=!org.open;
					}

					redraw(org);
				};

				/**
				 * Organisaatio valitaan
				 */
				scope.selectOrg=function(oid){
                    var org;
					console.log("selecting from puu "+treeId, SharedStateService.state.puut[treeId]);

					console.log("organisaatio valittu!", treeId);
					var current = SharedStateService.state.puut[treeId].selected;

					console.log("vanha valinta", current);
					if(current!==undefined) {
//						console.log("etsitään vanhaa", current);
						org = getOrg(current, SharedStateService.state.puut[treeId].data);
//						console.log("vanha:", org);
						if(org!==undefined){
							org.selected=false;
							redraw(org);
						}
					}
					var unselect = (oid===SharedStateService.state.puut[treeId].selected);
					console.log("unselect:" + unselect);
					console.log("scope check:", (scope == SharedStateService.state.puut[treeId].scope));
					if (unselect) {
						SharedStateService.state.puut[treeId].selected=undefined;
						scope[treeId].currentNode=undefined;
						return;
					}
//					console.log("etsitään uutta");
					org = getOrg(oid, SharedStateService.state.puut[treeId].data);
//					console.log("uusi:", org);

					org.selected=true;
					SharedStateService.state.puut[treeId].selected=oid;
					redraw(org);

					//aseta valittu organisaatio scopeen jotta voidaan watchilla seurata kun organisaatio valitaan puusta
					scope[treeId].currentNode=org;
				};

				//tree id
				var treeId = attrs.treeId;

				//tree model
				var treeModel = attrs.orgTreeModel;

				//node label
				var nodeLabel = attrs.nodeLabel || 'label';

				var autoSelect = scope[attrs.autoselect];

				//children
				var nodeChildren = attrs.nodeChildren || 'children';

				var drawChildren=function(children){

					var orgToString = function(eid, oid, label, cssclass, selected){
						return "<li id=\"" + treeId + "-o-" + eid +  "\" ><i ng-click=\"toggleOrg('"+ oid + "')\" class='" + cssclass + "'/></i><span" + (selected?" class=\"selected\"":"") + " ng-click=\"selectOrg('" + oid + "')\">" + label + "</span></li>";
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
                                    template = template + "<div id=\"" + treeId + "-c-" + eid + "\" class=\"treeview\"><ul>" +  drawChildren(org[nodeChildren]) + "</ul></div>";
                                } else {
                                    //kiinni
                                    template = template + orgToString(eid, org.oid,org[nodeLabel], "collapsed", org.selected);
                                }
                            } else {
                                //lehti
                                template = template + orgToString(eid, org.oid,org[nodeLabel], "normal", org.selected);
                            }
                        }
					}
					//console.log("template:", template);
					return template;
				};


				//alusta tietorakenne
				console.log("org-puu.init()" + treeId, SharedStateService.state.puut);
				SharedStateService.state.puut = SharedStateService.state.puut || {};
				//if (!SharedStateService.state.puut[treeId]) {
					SharedStateService.state.puut[treeId] = {scope:scope};
				///}

				/**
				 * Watchi puun datalle
				 */
				console.log("adding watch to:" + treeModel);
				scope.$watch(treeModel, function (newList, oldList) {
					SharedStateService.state.puut[treeId].data = newList;
					//element.html('');
					var template = "<ul>" + drawChildren(newList) + "</ul>";
					var dom = $compile( template );
					element.html('').append( dom (scope) );
                    //autoselect
                    if(autoSelect && newList && newList.length>0) {
                      scope.selectOrg(autoSelect);
                      autoSelect = undefined;
                    }
				});

			}
		};
	});
})( angular );
