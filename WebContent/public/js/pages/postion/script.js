﻿var myAppModule = angular.module("myApp",['ui.bootstrap'])

myAppModule.controller('PostionController',
	function costListController($scope,$http,$uibModal,$document){
		var self = this;
		$scope.totalItems = 0;
		$scope.currentPage = 1;
		$scope.itemsPerPage = 10;
		
		this.$onInit = function(){

			self.getDeparts();
			self.statusList = [
				{statusId: "audited", statusName : "审批通过"},
				{statusId : "auditing", statusName : "审批中"},
				{statusId : "reject", statusName : "审批拒绝"}
				];
			self.getList();
		};
		
		//页数变化
		$scope.setPage = function (pageNo) {
			$scope.currentPage = pageNo;
		};
		
		//换页
		$scope.pageChanged = function() {
			self.getList();
		};
		
		// 获取数据列表
		this.getList = function(){
			$http({
				method:'POST',
				url:$("#rootUrl").val()+'/admin/position/getPositionPageList.do',
				params:{
					name:$scope.name,
					depId:$scope.depId,
					status:$scope.status,
					start:(($scope.currentPage - 1) * $scope.itemsPerPage),
					end:$scope.currentPage * $scope.itemsPerPage -1
				}
			
			}).then(function(res){
				if(res){
					self.list = res.data.data || [];
					$scope.totalItems = res.data.total;
				}else{
					self.list = [];
					$scope.totalItems = 0;
				}
			});
		};
		
		//获取部门下拉框
		this.getDeparts = function(){
			$http({
				method:'POST',
				url:$("#rootUrl").val()+'/admin/dep/getAlldep.do',
				params:{
				}
			
			}).then(function(res){
				if(res){
					$scope.depList = res.data || [];
				}else{
					$scope.depList = [];
				}
			});
		};
		//编辑岗位
		this.edit = function (id, parentSelector) {
			var parentElem = parentSelector ? angular.element($document[0].querySelector('.content-wrapper ' + parentSelector)) : undefined;
					var modalInstance = $uibModal.open({
						animation: true,
						ariaLabelledBy: 'modal-title',
						ariaDescribedBy: 'modal-body',
						templateUrl: 'myModalContent.html',
						controller: 'ModalInstanceCtrl',
						controllerAs: '$ctrl',
						size: 'lg',
						appendTo: parentElem,
						//参数
						resolve: {
							//好像必须得这么写
						items: function () {
							return id;
						}
						}
					});

					modalInstance.result.then(function (selectedItem) {
						self.getList();
					}, function () {
						//取消的回调函数
						
					});
					};
					
		this.delCost = function (costId,size) {

			alert("删除");
			
		};
		
	}
);

//编辑页面的control
angular.module('myApp').controller('ModalInstanceCtrl', function ($scope,$http,$uibModalInstance, items) {
		var $ctrl = this;
		$ctrl.id = items;
		$ctrl.costEntity = {
			};
		
		$ctrl.$onInit = function(){
			
			if($ctrl.id != ''){
				$http({
					method:'POST',
					url:$("#rootUrl").val()+"/admin/position/getPositionById.do",
					params:{
						id:$ctrl.id
					}
				
				}).then(function(res){
					if(res.status){
						$scope.costEntity = res.data;
					}
					
				}).catch(function(res){
					if(res.data.code == 0){
						$scope.costEntity = res.data.objData;
					}
					
				});
			}
		
			$ctrl.getDeparts();
			
		};

		$ctrl.ok = function () {

		$http({
			method:'POST',
			url:'/ccydManagement/admin/position/saveOrUpdatePosition.do',
			params:{
				postId:$scope.costEntity.postId,
				name:$scope.costEntity.name,
				depId:$scope.costEntity.depId,
				onDuty:$scope.costEntity.onDuty,
				organization:$scope.costEntity.organization,
				vacancy:$scope.costEntity.vacancy
			}
		}).then(function(res){
			if(res){
				self.postlist = res.data || [];
			}else{
				self.list = [];
			}
		})
		
		 var params = {
				postId:$scope.costEntity.postId,
				name:$scope.costEntity.name,
				depId:$scope.costEntity.depId,
				onDuty:$scope.costEntity.onDuty,
				organization:$scope.costEntity.organization,
				vacancy:$scope.costEntity.vacancy
			} 
			
		$uibModalInstance.close(params);
		};
		
		
	//计算空缺人数
		$ctrl.cau = function () {
		if(typeof($scope.costEntity.organization) == "undefined"){
			$scope.costEntity.organization = 0;
			}
			$scope.costEntity.vacancy = $scope.costEntity.onDuty - $scope.costEntity.organization;
		};
		
		

		$ctrl.cancel = function () {
		$uibModalInstance.dismiss('cancel');
		};
		
		$ctrl.getDeparts = function(){
			
			$http({
				method:'POST',
				url:$("#rootUrl").val()+'/admin/dep/getAlldep.do',
				params:{
				}
			
			}).then(function(res){
				if(res){
					$scope.depList = res.data || [];
				}else{
					$scope.depList = [];
				}
			});
			
		};
		
	//获取岗位
		$ctrl.getPostionById = function(){
		$http({
			method:'POST',
			url:'/ccydManagement/admin/position/getPositionByDepId.do',
			params:{
				depId: $scope.costEntity.depId
			}
		}).then(function(res){
			if(res){
				$ctrl.postlist = res.data || [];
			}else{
				$ctrl.postlist = [];
			}
		})
	}
		
		$scope.costTypeChange = function() {
			
			var costTypeValue = $scope.costType;
			if(costTypeValue == 0 || costTypeValue == 1){
				
				$http({
					method:'POST',
					url:$("#rootUrl").val()+"/admin/cost/subType/"+costTypeValue+".do",
					params:{
					}
				
				}).then(function(res){
					
					if(res.data.code == 0){
						$scope.costSubTypeList = res.data.objData;
					}else{
						$scope.costSubTypeList = [];
					}
					
				});
				
			}else{
				$scope.costSubTypeList = [];
			}
			
		};
		
		//日期模块加载
		$scope.today = function() {
			$scope.dt = new Date();
		};
		$scope.clear = function() {
			$scope.dt = null;
		};

		$scope.dateOptions = {
			dateDisabled: "",
			formatYear: 'yyyy',
			maxDate: new Date(9999, 12, 31),
			minDate: new Date(1000, 1,1),
			startingDay: 1,
		};

		$scope.open = function(mode) {
			if(mode == 1){
				$scope.popup.opened1 = true;
			}else if (mode == 2){
				$scope.popup.opened2 = true;
			}else if (mode == 3){
				$scope.popup.opened3 = true;
			}else if (mode == 4){
				$scope.popup.opened4 = true;
			}
			
		};
	
		$scope.popup = {
			opened1: false,
			opened2: false,
			opened3: false,
			opened4: false
		};
		
	});