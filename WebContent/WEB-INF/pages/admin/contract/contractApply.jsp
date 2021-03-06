<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../common/meta.jsp"%>
<script type="text/javascript" src="${root }/public/js/customer.js"></script>

</head>
<body>
		<%@ include file="../../common/header.jsp"%>
		<div id="content" class="main" ng-app = "myApp" ng-controller="ContractController as vm">
		  <div class="main-inner">
			<div class="container">
				<div class="row">
					<div class="span12">
						<div class="widget">
							<!-- 标题 -->
							<div class="widget-header">
								<i class="icon-pushpin"></i>
								<h3>合同</h3>
							</div>
							
							<div class="widget-content" style="padding-bottom:100px;">
								<div class="row">
                                    <div class="span4">
										<span class="searchSpan">合同类型 :</span>
                                        <select id="contractType" ng-model="contractType"  class="form-control span2" 
                                                ng-options="cType.contractType as cType.name group by cType.group for cType in contractTypeList">
                                            <option value="">--请选择合同类型--</option>
                                        </select>
                                    </div>
                                    
									<div class="span6" style="height:37px;">
										<span class="searchSpan pull-left">申请日期 :</span>
										<span class="pull-left">
											<mb-datepicker input-class="mb-date" date="costStartDate" date-format="YYYY-MM-DD" class="pull-left" ></mb-datepicker>
										</span>
										<span class="searchSpanMid pull-left">~</span>
										<span class="pull-left">
											<mb-datepicker input-class="mb-date" date="costEndDate" date-format="YYYY-MM-DD" class="pull-left" ></mb-datepicker>
										</span>
									</div>
									<div class="span1 pull-right"><input type="button"  class="btn btn-large btn-success btn-support-ask" name="query" ng-click="vm.getContractList()" value="查询" /></div>
									
								</div>
								<div class="row">
                                    <div class="span4">
                                        <span class="searchSpan">单号 :</span>
                                        <input type="text" ng-model="contractId" class="span2" id="contractId">
                                    </div>
                                    <div class="span6">
                                    	<span class="searchSpan">内容 :</span>
                                        <input type="text" ng-model="content" class="span4">
                                    </div>
								</div>
								<hr>
								<!-- 查询结果 -->
								<div class="row">
									<div class="span12">
										<div style="width:1134px;">
											<button class="btn btn-invert" ng-click="vm.addContract()"><i class="icon-plus"></i> 新增</button>
										</div>
										<div style="overflow:scroll">
										<table class="table table-condensed table-bordered table-striped" style="width:1300px;margin-top:7px;" >
											<thead>
												<tr>
													<th  style="text-align:center;font-size:14px; width:170px;">操作</th> 
												 	<th  style="text-align:center;font-size:14px; width:90px">单号</th>
													<th style="text-align:center;font-size:14px;width:40px">类型</th>
													<th  style="text-align:center;font-size:14px;width:90px">内容</th>
													<th style="text-align:center;font-size:14px;width:60px">部门</th>
													<th style="text-align:center;font-size:14px;width:90px">总金额</th>
													<th style="text-align:center;font-size:14px;width:90px">已付</th>
													<th  style="text-align:center;font-size:14px;width:40px">附件</th>
													<th style="text-align:center;font-size:14px;width:90px">收款方</th>
													<th style="text-align:center;font-size:14px;width:100px">签订时间</th>
													<th style="text-align:center;font-size:14px;width:90px">外部订单</th>
													<th style="text-align:center;font-size:14px;width:90px">备注</th>
												
												</tr>
											</thead>
											<tbody>
												<tr class="odd gradeX" ng-repeat="item in vm.list" ng-switch="item.editMode" ng-class="item.status=='99' ? 'ScrapBackground' : ''" >
													<!-- view -->
													<td ng-switch-when="view" >
														<a href="javascript:;" class="btn btn-xs  " ng-click="vm.editContract(item,$index)" ng-if="item.status=='00'"><i class='icon-edit'></i></a>
														<a href="javascript:;" class="btn btn-xs btn-danger" ng-click="vm.scrap(item)" ng-if="item.status=='00' && item.status!='99'"><i class='icon-remove-sign'></i></a>
														<button type="button" class="btn btn-xs btn-success  " ng-click="vm.contractAttachmentList(item.contractId)"><i class='icon-file'></i></button>
													</td>
													<td ng-switch-when="view"><p ng-bind="item.contractId"></p></td>
													<td ng-switch-when="view"><p ng-bind="item.contractType"></p></td>
													<td ng-switch-when="view"><p  class="line-limit-length span2"  title="{{item.content}}" ng-bind="item.content"></p></td>
													<td ng-switch-when="view"><p ng-bind="item.dept"></p></td>
													<td ng-switch-when="view"><p ng-bind="item.cost"></p></td>
													<td ng-switch-when="view"><p ng-bind="item.costNumAll"></p></td>
													<td ng-switch-when="view"><p ng-bind="item.attachmentCnts"></p></td>
													<td ng-switch-when="view">
														<a href="javascript:;" class="line-limit-length span2"  title="{{item.company}}" ng-if="item.company" ng-click="vm.editPayment(item,'.widget-content','view')" >{{item.company}}</a>
														<a href="javascript:;" class="line-limit-length span2"   ng-if="!item.company"  >未选择</a>
													</td>
													<td ng-switch-when="view"><p ng-bind="item.url | date:'yyyy-MM-dd'"></p></td>
													<td ng-switch-when="view"><p  class="line-limit-length span2"  title="{{item.serialid}}" ng-bind="item.serialid"></p></td>
													<td ng-switch-when="view"><p  class="line-limit-length span2"  title="{{item.remakes}}" ng-bind="item.remakes"></p></td>
												
													<!-- edit -->
													<td ng-switch-when="edit"  >
														<a href="javascript:;" class="btn btn-xs btn-success " ng-click="vm.save(item)" style="width:100px;"><i class='icon-ok'></i></a>
													</td>
													<td ng-switch-when="edit"><p ng-bind="item.contractId" style="width:90px;"></p></td>
													<td ng-switch-when="edit">
				                                        <select id="type" style="width:60px;" ng-model="item.contractType"  class="form-control" 
				                                            ng-options="cType.contractType as cType.name group by cType.group for cType in contractTypeList">
				                                            <option value="">类型</option>
				                                        </select>
													</td>
													<td ng-switch-when="edit">
														<input type="text" ng-model="item.content" style="width:100px;" >
													</td>
													<td ng-switch-when="edit">
														<select id="dept" ng-model="item.dept" ng-disabled="${sessionScope.adminUser.userId != 'admin'}" style="width:80px;" class="form-control select2" 
														ng-options="cType.depId as cType.name group by cType.group for cType in depList">
														<option value="">部门</option>
														</select>
													</td>
													<td ng-switch-when="edit">
														<input type="number" ng-model="item.cost" min="-1" style="width:70px;">
													</td>
													<td ng-switch-when="edit"><p ng-bind="item.costNumAll"></p></td>
													<td ng-switch-when="edit"><p ng-bind="item.attachmentCnts"></p></td>
													<td ng-switch-when="edit">
														<a href="javascript:;" class="line-limit-length span2"  title="{{item.company}}"   ng-if="item.company" ng-click="vm.editPayment(item,'.widget-content','edit')">{{item.company}}</a>
														<a href="javascript:;" style="width:200px;" ng-if="!item.company" ng-click="vm.editPayment(item,'.widget-content','edit')">未选择</a>
													</td>
													<td ng-show="item.editMode == 'edit'">
														<!-- <input type="text" id="datepicker{{$index}}" ng-model="item.contractDate" style="width:120px;"> -->
														<mb-datepicker input-class="mb-date" date="item.contractDate" class="pull-left"  date-format="YYYY-MM-DD"></mb-datepicker>
													</td>
													<td ng-switch-when="edit">
														<input type="text" ng-model="item.serialid"  style="width:80px;" />
													</td>
													
													<td ng-switch-when="edit">
														<input type="text" ng-model="item.remakes"  style="width:80px;" />
													</td>
													
												</tr>
											</tbody>
										</table>
										</div>
										<div class="g-no-content" ng-if="vm.list && vm.list.length === 0">没有相关数据</div>
										<div style="width:1134px;">
											<%@ include file="../../common/page.jsp"%>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

	<!-- 弹框-删除 -->
    <script type="text/ng-template" id="myModalDelContent.html">
			<div class="modal-header">
				<div class="row ">
					<div class="col-lg-12 heading">
						<ul id="crumb" class="breadcrumb">
							确定删除合同记录吗？
						</ul>
					</div>
				</div>
			</div>
        <div class="modal-footer">
            <button class="btn btn-primary" type="button" ng-click="$ctrl.ok()">确定</button>
            <button class="btn btn-warning" type="button" ng-click="$ctrl.cancel()">取消</button>
        </div>
    </script>
    
    <script type="text/ng-template" id="myModalEditContent.html">
		  <div class="main-inner">
			<div class="container">
				<div class="row">
					<div class="span12">
						<div class="widget">
							<!-- 标题 -->
							
							<div class="widget-header">
								<i class="icon-pushpin"></i>
								<h3>付款信息</h3>
							</div>
							<div class="widget-content">
								<div class="row">
                                    <div class="span4">
                                        <span class="searchSpan">公司名称:</span>
                                        <input type="text" ng-model="company" class="span2" ng-change="$ctrl.getPaymentList()">
                                    </div>
									<!-- <div class="span1 pull-right"><input type="button"  class="btn btn-large btn-success btn-support-ask" name="query" ng-click="$ctrl.getPaymentList()" value="查询" /></div> -->
								</div>
								<hr>
								<!-- 查询结果 -->
								<div class="row">
									<div class="span12">
										<table class="table table-condensed table-bordered table-striped" style="width:97%;margin-top:7px;" >
											<thead>
												<tr>
													<th width="20%">公司名称</th>
													<th width="20%">税号</th>
													<th width="20%">银行账号</th>
													<th width="20%">开户行</th>
													<th width="20%" style="min-width：100px">操作</th>
												</tr>
											</thead>
											<tbody>
												<tr class="odd gradeX" ng-repeat="item in list" ng-class="item.status=='99' ? 'ScrapBackground' : ''" >
													<!-- view -->
													<td><p ng-bind-html="item.name"></p></td>
													<td><p ng-bind-html="item.taxNo"></p></td>
													<td><p ng-bind-html="item.bankNo"></p></td>
													<td><p ng-bind-html="item.bankName"></p></td>
													<td>
														<a href="javascript:;" class="btn btn-small btn-success" ng-click="Choose(item)" ng-if="mode=='edit'">选择</a>
													</td>
												</tr>
											</tbody>
										</table>
										<div class="g-no-content" ng-if="list && list.length === 0">没有相关数据</div>
										<div style="width:1134px;">
											<%@ include file="../../common/page.jsp"%>
										</div>
									</div>
								</div>
        						<div class="modal-footer">
            						<button class="btn btn-warning" type="button" ng-click="cancel()">取消</button>
        						</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
    </script>
    
    </div>
	<input type="hidden" id="rootUrl" value="${root}">

	<script src="${root}/public/js/pages/contract/script.js" type="text/javascript"></script>
</body>
</html>