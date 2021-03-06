﻿var myAppModule = angular.module("myApp",['ui.bootstrap']);
myAppModule.controller('UserListController',
	function UserListController($scope,$http){
		var self = this;
		$scope.totalItems = 0;
		$scope.currentPage = 1;
		$scope.itemsPerPage = 100;
		
		this.$onInit = function(){

			//设置部门下拉框
			this.getDepList();
			
			//获取角色
			self.admin =$("#sessionUserType").val();
			if(self.admin=="MANAGER"){
				$scope.depId =$("#sessionUserDepId").val();
				$scope.depIdChangeAble = true;
				self.getPostionById();
			}else if(self.admin=="ADMIN"){
				$scope.depIdChangeAble = false;
			}
			//查询
			self.getFinancingInfoList();
		}
		
		$scope.setPage = function (pageNo) {
			$scope.currentPage = pageNo;
		};

		$scope.pageChanged = function() {
			self.getFinancingInfoList();
		};
		
		// 获取数据列表
		this.getFinancingInfoList = function(){
			$http({
				method:'POST',
				url:'/ssc/admin/adminuser/getadminlist.do',
				params:{
					userName: $scope.userName,
					realName: $scope.realName,
					isIpe: 'ture',
					depId   : $scope.depId,
					postId: $scope.postId,
					start:(($scope.currentPage - 1) * $scope.itemsPerPage),
					end:$scope.currentPage * $scope.itemsPerPage -1
				}
			}).then(function(res){
				if(res){
					self.list = res.data.data || [];
					$scope.totalItems = res.data.total
				}else{
					self.list = [];
					$scope.totalItems = 0
				}
			})
		};
		
		//获取部门
		self.getDepList = function(){
			$http({
				method:'POST',
				url:'/ssc/admin/dep/getAlldep.do',
				params:{
				}
			}).then(function(res){
				if(res){
					self.deplist = res.data || [];
				}else{
					self.list = [];
				}
			})
		}
		
		//获取岗位
		self.getPostionById = function(){
			$http({
				method:'POST',
				url:'/ssc/admin/position/getPositionByDepId.do',
				params:{
					depId: $scope.depId
				}
			}).then(function(res){
				if(res){
					self.postlist = res.data || [];
				}else{
					self.postlistpostlist = [];
				}
			})
		}
		
		this.searchIpe = function(userId){
			window.location.href="/ssc/admin/ipe/getIpe.do?userId="+userId; 
		};
	}
);

angular.bootstrap(document.getElementById("content"), ['myApp']);