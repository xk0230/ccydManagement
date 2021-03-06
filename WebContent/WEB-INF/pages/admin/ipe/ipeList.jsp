<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/meta.jsp"%>
</head>
<body	>
		<%@ include file="../../common/header.jsp"%>
		<div id="content" ng-app = "myApp" ng-controller="IpeController as vm">
			<div class="main-inner">
				<div class="container">
					<div class="row">
						<div class="span12">
							<div class="widget">
								<!-- 标题 -->
								<div class="widget-header">
									<i class="icon-pushpin"></i>
									<h3>员工IPE信息</h3>
								</div>
								
								<div class="widget-content" style="padding-bottom: 100px;">
									<!-- 查询结果 -->
									<div class="row">
										<div class="span12">
											<div style="width:1134px;">
												<input type="button" class="btn btn-lg btn-success col-lg-12"  ng-click="vm.editIpe('', '.widget')" value="添加" />
											</div>
											<table class="table table-condensed table-bordered table-striped" style="width:1134px;margin-top:7px;" >
												<thead >
													<tr>
														<th>操作</th>
														<th>岗位</th>
														<th>姓名</th>
														<th>阶段</th>
														<th>pc等级</th>
														<th>建议年薪</th>
														<th>实际年薪</th>
														<th>组织规模</th>
														<th>影响</th>
														<th>贡献</th>
														<th>沟通</th>
														<th>框架</th>
														<th>创新</th>
														<th>复杂度</th>
														<th>知识</th>
														<th>团队</th>
														<th>宽度</th>
													</tr>
												</thead>
												<tbody>
													<tr class="odd gradeX" ng-repeat="item in vm.list">
														<td>
															<button type="button" class="btn btn-small btn-success" ng-click="vm.editIpe(item.id,'.widget')"><i class="icon-edit"></i></button>
															<button type="button" class="btn btn-small btn-danger" ng-click="vm.delIpe(item.id)"><i class="icon-remove-sign"></i></button>
														</td>
														<td><p ng-bind="item.depName"></td>
														<td><p ng-bind="item.realname"></td>
														<td><p ng-bind="item.workStatus"></td>
														<td><p ng-bind="item.pc"></td>
														<td><p ng-bind="item.adviseSalary"></td>
														<td><p ng-bind="item.salary"></td>
														<td><p ng-bind="item.org"></td>
														<td><p ng-bind="item.yx"></td>
														<td><p ng-bind="item.gx"></td>
														<td><p ng-bind="item.gt"></td>
														<td><p ng-bind="item.kj"></td>
														<td><p ng-bind="item.cx"></td>
														<td><p ng-bind="item.fzd"></td>
														<td><p ng-bind="item.zs"></td>
														<td><p ng-bind="item.td"></td>
														<td><p ng-bind="item.kd"></td>
													</tr>
												</tbody>
											</table>
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
			
			<script type="text/ng-template" id="myModalEditContent.html">
			<div class="main-inner">
				<div class="container">
					<div class="row">
						<div class="span12">
							<div class="widget">
								<!-- 标题 -->
								<div class="widget-header">
									<i class="icon-pushpin"></i>
									<h3>IPE记录</h3>
								</div>
								
								<div class="widget-content">
									<div class="row">
										<div class="span359 form-group">
											<span class="searchSpan">阶段:</span>
											<input type="text" class="form-control" ng-model="ipe.workStatus" />
										</div>
										<div class="span359 form-group">
											<span class="searchSpan">组织规模:</span>
											<input type="text" class="form-control" ng-model="ipe.org" ng-readonly="true"/>
										</div>
										<div class="span359 form-group">
											<span class="searchSpan">影响:</span>
											<select ng-model="ipe.yx"  class="form-control select2" >
												<option value="">--请选择--</option>
												<option value="交付">交付</option>
												<option value="操作">操作</option>
												<option value="战术">战术</option>
												<option value="战略">战略</option>
												<option value="远见">远见</option>
											</select>
										</div>
									</div>
									<div class="row">
										<div class="span359 form-group">
											<span class="searchSpan">贡献:</span>
											<select ng-model="ipe.gx"  class="form-control select2">
												<option value="">--请选择--</option>
												<option value="yx,有限" >有限</option>
												<option value="bf,部分" >部分</option>
												<option value="zj,直接" >直接</option>
												<option value="xz,显著" >显著</option>
												<option value="sy,首要" >首要</option>
											</select>
										</div>
										<div class="span359 form-group">
											<span class="searchSpan">沟通:</span>
											<select ng-model="ipe.gt"  class="form-control select2" >
												<option value="">--请选择--</option>
												<option value="传达" >传达</option>
												<option value="传达-交互" >传达-交互</option>
												<option value="交互" >交互</option>
												<option value="交互-影响" >交互-影响</option>
												<option value="影响" >影响</option>
												<option value="影响-谈判" >影响-谈判</option>
												<option value="谈判" >谈判</option>
												<option value="谈判-长期影响的谈判" >谈判-长期影响的谈判</option>
												<option value="长期影响的谈判" >长期影响的谈判</option>
											</select>
										</div>
										<div class="span359 form-group">
											<span class="searchSpan">框架:</span>
											<select ng-model="ipe.kj"  class="form-control select2" >
												<option value="">--请选择--</option>
												<option value="a,内部共享" >内部共享</option>
												<option value="b,外部共享" >外部共享</option>
												<option value="c,内部分歧" >内部分歧</option>
												<option value="d,外部分歧" >外部分歧</option>
											</select>
										</div>
									</div>
									<div class="row">
										<div class="span359 form-group">
											<span class="searchSpan">创新:</span>
											<select ng-model="ipe.cx"  class="form-control select2" >
												<option value="">--请选择--</option>
												<option value="跟从">跟从</option>
												<option value="跟从-核查">跟从-核查</option>
												<option value="核查">核查</option>
												<option value="核查-改进">核查-改进</option>
												<option value="改进">改进</option>
												<option value="改进-提高">改进-提高</option>
												<option value="提高">提高</option>
												<option value="提高-概念化">提高-概念化</option>
												<option value="创造/概念化">创造/概念化</option>
												<option value="概念化-技术突破">概念化-技术突破</option>
												<option value="科学的/技术的突破">科学的/技术的突破</option>
											</select>
										</div>
										<div class="span359 form-group">
											<span class="searchSpan">复杂度:</span>
											<select ng-model="ipe.fzd"  class="form-control select2" >
												<option value="">--请选择--</option>
												<option value="a,明确的" >明确的</option>
												<option value="b,明确-复杂" >明确-复杂</option>
												<option value="c,困难的" >困难的</option>
												<option value="d,困难-符合" >困难-符合</option>
												<option value="e,复合的" >复合的</option>
												<option value="f,复合-多维" >复合-多维</option>
												<option value="g,多维的" >多维的</option>
											</select>
										</div>
										<div class="span359 form-group">
											<span class="searchSpan">知识:</span>
											<select ng-model="ipe.zs"  class="form-control select2" >
												<option value="">--请选择--</option>
												<option value="有限职位知识">有限职位知识</option>
												<option value="有限-基本">有限-基本</option>
												<option value="基本职位知识">基本职位知识</option>
												<option value="基本-宽泛">基本-宽泛</option>
												<option value="宽泛岗位知识">宽泛岗位知识</option>
												<option value="宽泛-专门">宽泛-专门</option>
												<option value="专门技术知识">专门技术知识</option>
												<option value="专门技术-专门标准">专门技术-专门标准</option>
												<option value="专门标准知识">专门标准知识</option>
												<option value="专门标准-职能专才/组织通才">专门标准-职能专才/组织通才</option>
												<option value="职能专才/组织通才">职能专才/组织通才</option>
												<option value="职能专才/组织通才-职能杰出/宽泛实践经验">职能专才/组织通才-职能杰出/宽泛实践经验</option>
												<option value="职能杰出/宽泛实践经验">职能杰出/宽泛实践经验</option>
												<option value="职能杰出/宽泛实践经验-广博">职能杰出/宽泛实践经验-广博</option>
												<option value="广博而深入的实践经验">广博而深入的实践经验</option>
											</select>
										</div>
									</div>
									<div class="row">
										<div class="span359 form-group">
											<span class="searchSpan">团队宽度:</span>
											<select ng-model="ipe.td"  class="form-control select2" >
												<option value="">--请选择--</option>
												<option value="a,团队成员-本土" >团队成员-本土</option>
												<option value="b,团队成员-地区" >团队成员-地区</option>
												<option value="c,团队成员-全球" >团队成员-全球</option>
												<option value="d,成员-领导-本土" >成员-领导-本土</option>
												<option value="e,成员-领导-地区" >成员-领导-地区</option>
												<option value="f,成员-领导-全球" >成员-领导-全球</option>
												<option value="g,团队领导-本土" >团队领导-本土</option>
												<option value="h,团队领导-地区" >团队领导-地区</option>
												<option value="i,团队领导-全球" >团队领导-全球</option>
												<option value="j,领导-多团队-本土" >领导-多团队-本土</option>
												<option value="k,领导-多团队-地区" >领导-多团队-地区</option>
												<option value="l,领导-多团队-全球" >领导-多团队-全球</option>
												<option value="m,多团队领导-本土" >多团队领导-本土</option>
												<option value="n,多团队领导-地区" >多团队领导-地区</option>
												<option value="o,多团队领导-全球" >多团队领导-全球</option>
											</select>
										</div>
										<div class="span359 form-group">
											<span class="searchSpan">实际年薪:</span>
											<input type="text" class="form-control" ng-model="ipe.salary" />
										</div>
									</div>
	        						<div class="modal-footer">
							            <button class="btn btn-primary" type="button" ng-click="$ctrl.ok()">保存</button>
							            <button class="btn btn-warning" type="button" ng-click="$ctrl.cancel()">取消</button>
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
	<input type="hidden" id="userId" value="${userId}">
	<script src="${root}/public/js/pages/ipe/edit.js" type="text/javascript"></script>
</body>
</html>