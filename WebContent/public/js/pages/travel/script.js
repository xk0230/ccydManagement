var myAppModule = angular.module("myApp",['ui.bootstrap','materialDatePicker','ngSanitize'])
myAppModule.config(['$locationProvider', function($locationProvider) {  
	  $locationProvider.html5Mode(true);  
	}]); 
myAppModule.controller('TravelController',
	function travelListController($scope,$http,$location,$uibModal,$document,$filter){
		var self = this;
		$scope.totalItems = 0;
		$scope.currentPage = 1;
		$scope.itemsPerPage = 20;
		
		this.$onInit = function(){
			
			$scope.auditStatusList = [
				{costType : "0", name : "未审核"},
				{costType : "1", name : "已审核"}
			];
			
			if($("#auditStatus").val() != null && $("#auditStatus").val()!= ''){
				$scope.auditStatus = $("#auditStatus").val();
			}

			self.getDeparts();
			
			//获取角色
			self.admin =$("#sessionUserType").val();
			if(self.admin=="MANAGER"){
				$scope.dep =$("#sessionUserDepId").val();
				$scope.depIdChangeAble = true;
			}else if(self.admin=="ADMIN"){
				$scope.depIdChangeAble = false;
			}
			self.getTravelList();
		};
		
		$scope.setPage = function (pageNo) {
			$scope.currentPage = pageNo;
		};

		$scope.pageChanged = function() {
			self.getTravelList();
		};
		
		//打印
		this.print= function(key){
			console.log(key)
				$("#printDiv" + key).print({
				    globalStyles: true,
				    mediaPrint: false,
				    stylesheet: null,
				    noPrintSelector: ".no-print",
				    iframe: true,
				    append: null,
				    prepend: null,
				    manuallyCopyFormValues: true,
				    deferred: $.Deferred()
            });
			
		}
		
		
		// 获取数据列表
		this.getTravelList = function(){
			console.log($("#mode").val());
			var url = '/travel/page.do';
			if($("#mode").val() == 'audit'){
				url = '/travel/auditPage.do';
			}else if($("#mode").val() == 'view'){
				url = '/travel/viewPage.do';
			}
			
			$http({
				method:'POST',
				url:$("#rootUrl").val()+url,
				params:{
//					createUser:$scope.createUser,
					auditStatus:$scope.auditStatus,
					place:$scope.place,
					startDate:$filter('date')($scope.startDate, "yyyy-MM-dd"),
					endDate:$filter('date')($scope.endDate, "yyyy-MM-dd"),
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
		
		this.editPayment = function (item, parentSelector, mode) {
		    var parentElem = parentSelector ? angular.element($document[0].querySelector(parentSelector)) : undefined;
		    	    var modalInstance = $uibModal.open({
		    	      animation: true,
		    	      ariaLabelledBy: 'modal-title',
		    	      ariaDescribedBy: 'modal-body',
		    	      templateUrl: 'myModalEditContent.html',
		    	      controller: 'ModalInstanceCtrl',
		    	      controllerAs: '$ctrl',
		    	      size: 'lg',
		    	      appendTo: parentElem,
		    	      //参数
		    	      resolve: {
		    			//好像必须得这么写
		    	        item: function () {
		    	        	item.mode = mode;
		    	        	return item;
		    	        }
		    	      }
		    	    });

		    	    modalInstance.result.then(function (selectedItem) {
		    	    	item.mode = mode;
		    	    	item.company = selectedItem.company;
		    	    	item.companyid = selectedItem.id;
		    	    }, function () {
		    	    	//取消的回调函数
		    	    	
		    	    });
		   };

		
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
		
		//添加新申请
		this.addTravel = function(){
			var newItem = {
				startTime:$filter('date')(new Date(), "yyyy-MM-dd")
				,createTime:$filter('date')(new Date(), "yyyy-MM-dd")
				,create_user:""
				,place:""
				,depId:$("#sessionUserDepId").val()
				,endTime:$filter('date')(new Date(), "yyyy-MM-dd")
				,remark:""	
				,editMode:"edit"
			};
			var myArray=new Array()
			myArray.push(newItem);
			
			$.each(self.list, function(index, value) {
				myArray.push(value);
			}
			);
			self.list = myArray;
		};
		
		//点击编辑
		this.editTravel = function (travelItem,index) {
			travelItem.editMode = "edit";
			//设置时间控件
			setDatepicker("datepicker" + index)
			var depid;
			for(var i=0;i<$scope.depList.length;i++){
				 if ($scope.depList[i].name == travelItem.depName){
				    	depid = $scope.depList[i].depId;
				    	break;
				 };
			}
			travelItem.depId= depid;

		};
		
		//查看出差详情
		this.toDetail = function (travelItem,index) {
			window.open("/ssc/traveldetail/traveldetailApply.do?travelId="+travelItem.id);  
		};
		
		self.updSucess = false;
		//点击保存
		this.save = function (travelItem) {
			self.updSucess = false;
			this.updateData(0);
		};
		
		//报废
		this.updateData = function (index) {
			if(index == self.list.length){
				if(self.updSucess){
					swal("保存成功！")
					self.getTravelList();
					return;
				}else{
					swal("保存失败！")
					return;
				}
			}
			var travelItem = self.list[index];
			if(travelItem.editMode == "edit"){
				if(!travelItem.place){
					alert("请填写出差地");
					return ;
				}
				if(!travelItem.depId){
					alert("请填写部门");
					return ;
				}
				if(!travelItem.startTime){
					alert("请填写开始时间");
					return ;
				}
				if(!travelItem.endTime){
					alert("请填写结束时间");
					return ;
				}
				var params = {
					id:travelItem.id,
					createUser:travelItem.createUser,
					place:travelItem.place,
					depId:travelItem.depId,
					startTime:$filter('date')(travelItem.startTimeStr, "yyyy-MM-dd"),
					endTime:$filter('date')(travelItem.endTimeStr, "yyyy-MM-dd"),
					status:travelItem.status,
					remark:travelItem.remark
				};
			
				$http({
					method:'POST',
					url:$("#rootUrl").val()+"/travel/saveOrUpdate.do",
					params:params
				}).then(function(res){
					if(res.data.code == 0){
						self.updSucess = true;
					}else{
						self.updSucess = false;
					}
					self.updateData(index+1);
				});
			}else{
				self.updateData(index+1);
			}
		};
		
		//报废
		this.scrap = function (TravelItem) {
			swal({ 
					title: "确定报废吗？", 
					text: "你将无法恢复该出差信息！", 
					type: "warning", 
					showCancelButton: true, 
					closeOnConfirm: true, 
					showLoaderOnConfirm: true, 
			},
			function(){ 
				var params = {
						id:TravelItem.id
					};
				$http({
					method:'POST',
					url:$("#rootUrl").val()+"/travel/deleteTravel.do",
					params:params
					
				}).then(function(res){
					if(res.data.code == 0){
						swal(res.data.msg);
						self.getTravelList();
						//costItem.editMode="view";
					}else{
						swal(res.data.msg);
					}
				})
			});
		};
		
		
		//经理审核提交
		this.managerSub = function (item) {
			swal({ 
					title: "确定提交吗？", 
					text: "将提交给管理员审核！", 
					type: "info", 
					showCancelButton: true, 
					closeOnConfirm: false, 
					showLoaderOnConfirm: true, 
			},
			function(){ 
				var params = {
						id:item.id,
						status:"03"
					};
				self.updateStatus(params);
			});
		};

		//经理驳回
		this.managerRej = function (item) {
			swal({ 
					title: "确定驳回吗？", 
					text: "将驳回给申请者修改！", 
					type: "info", 
					showCancelButton: true, 
					closeOnConfirm: false, 
					showLoaderOnConfirm: true, 
			},
			function(){ 
				var params = {
						id:item.id,
						status:"02"
					};
				self.updateStatus(params);
			});
		};
		
		//管理员审核提交
		this.adminSub = function (item) {
			swal({ 
					title: "确定提交吗？", 
					text: "将通过审核并计入成本！", 
					type: "info", 
					showCancelButton: true, 
					closeOnConfirm: false, 
					showLoaderOnConfirm: true, 
			},
			function(){ 
				var params = {
						id:item.id,
						status:"05"
					};
				self.updateStatus(params);
			});
		};

		//管理员驳回
		this.adminRej = function (item) {
			swal({ 
					title: "确定驳回吗？", 
					text: "将驳回至部门经理！", 
					type: "info", 
					showCancelButton: true, 
					closeOnConfirm: false, 
					showLoaderOnConfirm: true, 
			},
			function(){ 
				var params = {
						id:item.id,
						status:"04"
					};
				self.updateStatus(params);
			});
		};
		//更新状态
		this.updateStatus = function (params) {
			$http({
				method:'POST',
				url:$("#rootUrl").val()+"/travel/updateStatus.do",
				params:params
				
			}).then(function(res){
				if(res.data.code == 0){
					swal(res.data.msg);
					//重新加载列表
					self.getTravelList();
				}else{
					swal(res.data.msg);
				}
			})
		};
		
		
		//提交
		this.submitTravel = function (travelItem) {
			swal({ 
					title: "确定提交吗？", 
					text: "将提交给您的部门领导审核！", 
					type: "info", 
					showCancelButton: true, 
					closeOnConfirm: false, 
					showLoaderOnConfirm: true, 
			},
			function(){ 
				var params = {
						id:travelItem.id,
						status:"01"
					};
				$http({
					method:'POST',
					url:$("#rootUrl").val()+"/travel/updateStatus.do",
					params:params
					
				}).then(function(res){
					if(res.data.code == 0){
						swal("提交成功！");
						self.getTravelList();
						//costItem.editMode="view";
					}else{
						swal("提交失败！");
					}
				})
			});
		};
		
	}
);

angular.module('myApp').controller('ModalInstanceCtrl',
		function ($scope,$http,$uibModalInstance,$filter, item){
			var $ctrl = this;
			$scope.item = item;
			$scope.totalItems = 0;
			$scope.currentPage = 1;
			$scope.itemsPerPage = 10;
			
			$ctrl.$onInit = function(){
				$scope.mode = item.mode;
				$scope.name = item.name;
				$ctrl.getTravelList();

			};
			
			$ctrl.setPage = function (pageNo) {
				$scope.currentPage = pageNo;
			};

			$ctrl.pageChanged = function() {
				$ctrl.getTravelList();
			};
			
			// 获取数据列表
			/*$ctrl.getTravelList = function(){
				console.log("查询啦");
				
				$http({
					method:'POST',
					url:$("#rootUrl").val()+'/payment/page.do',
					params:{
						place:$scope.place,
						start:(($scope.currentPage - 1) * $scope.itemsPerPage),
						end:$scope.currentPage * $scope.itemsPerPage -1
					}
				}).then(function(res){
					if(res){
						$scope.list = res.data.data || [];
						$scope.totalItems = res.data.total;
					}else{
						$scope.list = [];
						$scope.totalItems = 0;
					}
				});
			};*/
				
			$scope.Choose = function (item) {
				var selectItem = {id : item.id, company:item.name};
				$uibModalInstance.close(selectItem);
			};

			$scope.cancel = function () {
				$uibModalInstance.dismiss('cancel');
			};
			
		});

angular.bootstrap(document.getElementById("content"), ['myApp']);