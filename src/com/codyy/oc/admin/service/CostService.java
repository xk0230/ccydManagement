package com.codyy.oc.admin.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codyy.commons.CommonsConstant;
import com.codyy.commons.page.Page;
import com.codyy.commons.utils.DateUtils;
import com.codyy.oc.admin.dao.CostDaoMapper;
import com.codyy.oc.admin.dto.JsonDto;
import com.codyy.oc.admin.entity.AdminUser;
import com.codyy.oc.admin.entity.CostDepEntityBean;
import com.codyy.oc.admin.entity.CostEntityBean;
import com.codyy.oc.admin.entity.CostSeqBean;
import com.codyy.oc.admin.entity.CostSubTypeBean;
import com.codyy.oc.admin.entity.Department;
import com.codyy.oc.admin.vo.CostChartVO;
import com.codyy.oc.admin.vo.CostChartsData;
import com.codyy.oc.admin.vo.CostChartsSeriesData;
import com.codyy.oc.admin.vo.CostInOutlayType;
import com.codyy.oc.admin.vo.CostMonthInOut;
import com.codyy.oc.admin.vo.CostTotalInOut;
import com.codyy.oc.admin.vo.CostVO;
import com.codyy.oc.admin.vo.CostYearVO;

/**
 * 成本控制server
 * @author Administrator
 *
 */

@Service("costServer")
public class CostService {
	
	private static final String INSERT_SUCCESS = "新增成功";
	private static final String INSERT_ERROR = "新增失败";
	private static final String UPDATE_SUCCESS = "修改成功";
	private static final String UPDATE_ERROR = "修改失败";
	private static final String DEL_SUCCESS = "删除成功";
	private static final String DEL_ERROR = "删除失败";
	private static final String NO_EXIT_DATA = "数据不存在";
	private static final String SCRAP_SUCCESS = "报废成功";
	private static final String SCRAP_ERROR = "报废失败";
	private static final String SUB_SUCCESS = "提交成功";
	private static final String SUB_ERROR = "提交失败";
	private static final String REJ_SUCCESS = "提交成功";
	private static final String REJ_ERROR = "提交失败";
	@Autowired
	private CostDaoMapper costDaoMapper; 
	
	@Autowired
	DepartmentService departmentService;
	
	@Autowired
	private DepartmentService depService;
	
	public JsonDto getCostSubTypeList(int castType){
		
		JsonDto jsonDto = new JsonDto();
		List<CostSubTypeBean> costSubTypeList = costDaoMapper.getCostSubTypeList(castType);
		if(CollectionUtils.isNotEmpty(costSubTypeList)){
			jsonDto.setCode(0);
			jsonDto.setObjData(costSubTypeList);
		}
		
		return jsonDto;
	}
	
	/**
	 * 插入，更新成本数据
	 * @param user
	 * @param costEntityBean
	 * @return
	 */
	public JsonDto insertOrUpdateCostEntity(AdminUser user,CostEntityBean costEntityBean){
		
		JsonDto jsonDto = new JsonDto();
		//成本产生时间
		costEntityBean.setCostTime(DateUtils.stringToTimestamp((DateUtils.format(costEntityBean.getCostTime()))));
		//所有部门列表
		List<Department> depList = depService.getAllDepartment();
		//成本部门明细类
		CostDepEntityBean costDep = new CostDepEntityBean();
	    Calendar calendar = Calendar.getInstance(); 
	    calendar.setTime(costEntityBean.getCostTime());
	    costDep.setCostYear(String.valueOf(calendar.get(Calendar.YEAR)));
	    costDep.setCostMonth(String.valueOf(calendar.get(Calendar.MONTH) + 1));
	    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
	    costDep.setCostDate(format.format(calendar.getTime()));
	    
		//CostID为空时执行插入
		if(StringUtils.isBlank(costEntityBean.getCostId())){
			//部门ID=当前用户的部门
		    String depId = user.getDepId();
			//创建时间和创建者
			costEntityBean.setCreateTime(DateUtils.getCurrentTimestamp());
			costEntityBean.setCreateUserId(user.getUserId());
			if(user.getPosition().equals("MANAGER")) {
				costEntityBean.setStatus("03");
			}else if(user.getUserId().equals("admin")) {
				costEntityBean.setStatus("05");
			}else {
				costEntityBean.setStatus("00");
				if(departmentService.getDepManagerId(user.getDepId()) == null){
					costEntityBean.setStatus("03");
				}
			}
		    if(StringUtils.isNotBlank(depId)) {
		    	//对CostID设定UUID
		    	costEntityBean.setCostId(UUID.randomUUID().toString());
		    	//生成新的costNo
		    	costEntityBean.setCostNo(CreateCostNo(costEntityBean));
		    	//设置部门
		    	costEntityBean.setDepId(depId);
		    	//执行插入
		    	int insertCostEntityNum = costDaoMapper.insertCostEntity(costEntityBean);
		    	
		    	if(insertCostEntityNum == 1) {
					jsonDto.setCode(0);
					jsonDto.setMsg(INSERT_SUCCESS);
		    	}else {
					jsonDto.setMsg(INSERT_ERROR);
				}
			}
		    //设置costID
		    costDep.setCostId(costEntityBean.getCostId());
		    
		    for(Department dep : depList) {
		    	costDep.setCostDep(dep.getDepId());
		    	costDep.setCostNum(dep.getDepId().equals(costEntityBean.getDepId())?costEntityBean.getCostNum():0);
		    	costDaoMapper.insertCostDepEntity(costDep);
		    }
		}else{
			int updateCostEntityNum = costDaoMapper.updateCostEntity(costEntityBean);
			costDep.setCostId(costEntityBean.getCostId());
			costDep.setCostDep(user.getDepId());
		    for(Department dep : depList) {
		    	if(dep.getDepId().equals(user.getDepId())) {
		    		costDep.setCostNum(costEntityBean.getCostNum());
		    	}else {
		    		costDep.setCostNum(0);
		    	}
		    	costDep.setCostDep(dep.getDepId());
		    	costDaoMapper.updateCostDepEntity(costDep);
		    }
			if(updateCostEntityNum == 1){
				jsonDto.setCode(0);
				jsonDto.setMsg(UPDATE_SUCCESS);
			}else{
				jsonDto.setMsg(UPDATE_ERROR);
			}
		}
		return jsonDto;
	}
	
	/**
	 * 插入，更新成本数据
	 * @param user
	 * @param costEntityBean
	 * @return
	 */
	public JsonDto adminUpdateCostEntity(AdminUser user,CostEntityBean costEntityBean){
		
		JsonDto jsonDto = new JsonDto();
		int updateCostEntityNum = costDaoMapper.updateCostEntity(costEntityBean);

		//成本部门明细类
	    Calendar calendar = Calendar.getInstance(); 
	    calendar.setTime(costEntityBean.getCostTime());
	    
	    String year = String.valueOf(calendar.get(Calendar.YEAR));
	    String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
	    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
	    String date = format.format(calendar.getTime());

	    for(CostDepEntityBean costDep : costEntityBean.getCosDepList()) {
	    	costDep.setCostYear(year);
	    	costDep.setCostMonth(month);
	    	costDep.setCostDate(date);
	    	costDaoMapper.updateCostDepEntity(costDep);
	    }
		if(updateCostEntityNum == 1){
			jsonDto.setCode(0);
			jsonDto.setMsg(UPDATE_SUCCESS);
		}else{
			jsonDto.setMsg(UPDATE_ERROR);
		}
		return jsonDto;
	}
	
	

	/**
	 * 生成成本单号
	 * @param costEntityBean
	 * @return
	 */
	private String CreateCostNo(CostEntityBean costEntityBean) {
		//当前日期
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd"); 
		String today = dateFormat.format(new Date());

		//取番
		CostSeqBean costSeqBean = costDaoMapper.getCostNoSeq(costEntityBean.getCostType());
		
		if(costSeqBean != null) {
			if(costSeqBean.getDate().equals(today)) {
				costSeqBean.setSeq(costSeqBean.getSeq() + 1);
				costDaoMapper.updateCostNoSeq(costSeqBean);
			}else {
				costSeqBean.setDate(today);
				costSeqBean.setSeq(1);
				costDaoMapper.updateCostNoSeq(costSeqBean);
			}
		}else {
			costSeqBean = new CostSeqBean();
			costSeqBean.setType(costEntityBean.getCostType());
			costSeqBean.setDate(today);
			costSeqBean.setSeq(1);
			costDaoMapper.insertCostNoSeq(costSeqBean);
		}
		return costEntityBean.getCostType() + today + "_" + String.format("%04d", costSeqBean.getSeq());
	}

	/**
	 * 更新成本状态
	 * @param user
	 * @param costEntityBean
	 * @return
	 */
	public JsonDto updateCostStatus(AdminUser user,CostEntityBean costEntityBean){
		
		JsonDto jsonDto = new JsonDto();
		
		String sucessResult = "";
		String errResult = "";
		
		if(costEntityBean.getStatus().equals("99")) {
			sucessResult = SCRAP_SUCCESS;
			errResult = SCRAP_ERROR;
		}else if(costEntityBean.getStatus().equals("03") || costEntityBean.getStatus().equals("05")|| costEntityBean.getStatus().equals("01")) {
			sucessResult = SUB_SUCCESS;
			errResult = SUB_ERROR;
			
		}else if(costEntityBean.getStatus().equals("02")|| costEntityBean.getStatus().equals("04")) {
			sucessResult = REJ_SUCCESS;
			errResult = REJ_ERROR;
		}
		if(user.getPosition().equals("MANAGER") && (costEntityBean.getStatus().equals("03") || costEntityBean.getStatus().equals("02"))) {
			costEntityBean.setAuditUser(user.getUserId());
		}
		
		int updateCostEntityNum = costDaoMapper.updateCostStatus(costEntityBean);
		if(updateCostEntityNum == 1){
			jsonDto.setCode(0);
			jsonDto.setMsg(sucessResult);
		}else{
			jsonDto.setMsg(errResult);
		}
		return jsonDto;
	}
	
	
	public JsonDto getCostEntityById(String costId){
		
		JsonDto jsonDto = new JsonDto();
		CostEntityBean cost = costDaoMapper.getCostEntityById(costId);
		if(cost != null){
			jsonDto.setCode(0);
			jsonDto.setObjData(cost);
		}else{
			jsonDto.setMsg(NO_EXIT_DATA);
		}
		
		return jsonDto;
		
	}

	public JsonDto delCostEntityById(String costId){
		
		JsonDto jsonDto = new JsonDto();
		int delNum = costDaoMapper.delCostEntityById(costId);
		if(delNum == 1){
			jsonDto.setCode(0);
			jsonDto.setMsg(DEL_SUCCESS);
		}else{
			jsonDto.setMsg(DEL_ERROR);
		}
		
		return jsonDto;
	}
	
	/**
	 * 成本列表查询
	 * @param cost
	 * @return
	 */
	public Page getCostPageList(CostVO cost){
	    Page page = new Page();
	    page.setStart(cost.getStart());
	    page.setEnd(cost.getEnd());
	    
	    Map<String, Object> map = new HashMap<String, Object>();
	    
	    map.put("costType", cost.getCostType());
	    map.put("costSubtypeId", cost.getCostSubtypeId());
	    map.put("userId", cost.getUserId());
	    map.put("startTime", cost.getStartDate());
	    map.put("endTime", cost.getEndDate());
	    map.put("costNo", cost.getCostNo());
	    map.put("remark", cost.getRemark());
	    
	    page.setMap(map);
	    
	    List<CostVO> costPageList = costDaoMapper.getCostPageList(page);
	    page.setData(costPageList);
	    
	    return page;
	}
	
	/**
	 * 成本审核查询
	 * @param cost
	 * @return
	 */
	public Page getCostAuditList(CostVO cost){
	    Page page = new Page();
	    page.setStart(cost.getStart());
	    page.setEnd(cost.getEnd());
	    
	    Map<String, Object> map = new HashMap<String, Object>();
	    
	    map.put("costType", cost.getCostType());
	    map.put("costSubtypeId", cost.getCostSubtypeId());
	    map.put("depId", cost.getDepId());
	    map.put("status", cost.getStatus());
	    map.put("startTime", cost.getStartDate());
	    map.put("endTime", cost.getEndDate());
	    map.put("userId", cost.getUserId());
	    map.put("costNo", cost.getCostNo());
	    map.put("remark", cost.getRemark());
	    map.put("auditStatus", cost.getAuditStatus());
	    map.put("userPostion", cost.getUserPostion());
	    
	    page.setMap(map);
	    
	    List<CostVO> costPageList = costDaoMapper.getCostAuditPageList(page);
	    
	    for(CostVO costVO : costPageList) {
	    	costVO.setCostDepList(costDaoMapper.getCostDepList(costVO.getCostId()));
	    }
	    page.setData(costPageList);
	    return page;
	}
	
	/**
	 * 成本审核查询
	 * @param cost
	 * @return
	 */
	public Page getCostViewList(CostVO cost){
	    Page page = new Page();
	    page.setStart(cost.getStart());
	    page.setEnd(cost.getEnd());
	    
	    Map<String, Object> map = new HashMap<String, Object>();
	    
	    map.put("costType", cost.getCostType());
	    map.put("costSubtypeId", cost.getCostSubtypeId());
	    map.put("depId", cost.getDepId());
	    map.put("status", cost.getStatus());
	    map.put("startTime", cost.getStartDate());
	    map.put("endTime", cost.getEndDate());
	    map.put("userId", cost.getUserId());
	    map.put("costNo", cost.getCostNo());
	    map.put("remark", cost.getRemark());
	    map.put("userPostion", cost.getCreateUserPosition());
	    map.put("searchMonth", cost.getSearchMonth());
	    
	    page.setMap(map);
	    
	    List<CostVO> costPageList = costDaoMapper.getCostViewPageList(page);
	    
	    for(CostVO costVO : costPageList) {
	    	costVO.setCostDepList(costDaoMapper.getCostDepList(costVO.getCostId()));
	    }
	    page.setData(costPageList);
	    return page;
	}
	
	/**
	 * 成本审核查询
	 * @param cost
	 * @return
	 */
	public List<String> getMonthList(){
		return costDaoMapper.getMonthList();
	}
	
	/**
	 * @param sessionUser
	 * @param costEntityBean
	 * @return
	 */
	public JsonDto viewChart(AdminUser sessionUser, CostVO cost) {
		
		JsonDto jsonDto = new JsonDto();
		
		cost.setUserId(sessionUser.getUserId());
		cost.setDepId(sessionUser.getDepId());
		cost.setCreateUserPosition(sessionUser.getPosition());
		
		Map<String, Object> map = new HashMap<String, Object>();
	    map.put("costType", cost.getCostType());
	    map.put("costSubtypeId", cost.getCostSubtypeId());
	    map.put("startTime", cost.getStartDate());
	    map.put("endTime", cost.getEndDate());
	    map.put("costNo", cost.getCostNo());
	    map.put("remark", cost.getRemark());
	    map.put("userPostion", cost.getCreateUserPosition());
	    map.put("depId", cost.getDepId());
	    map.put("userId", cost.getUserId());
	    
	    List<CostEntityBean> costPageList = costDaoMapper.getViewChart(map);
	    
	    jsonDto.setCode(0);
	    jsonDto.setMsg("获取成功");
	    jsonDto.setObjData(costPageList);
	    
	    return jsonDto;
	}
	
	public JsonDto getCostChartData(AdminUser user,int type,int curYear,String depIds){
		JsonDto jsonDto = new JsonDto();
		Map<String, Object> resultMap = new HashMap<String, Object>();
	    
	    /*子分类列表*/
	    List<String> costSubStrList = costDaoMapper.getCostSubTypeStrList(type);
	    resultMap.put("costSubStrList", costSubStrList);
	    
	    List<CostSubTypeBean> costSubList = costDaoMapper.getCostSubTypeList(type);
	    resultMap.put("costSubList", costSubList);

	    /*查询结果列表*/
	    Map<String, Object> costMap = new HashMap<String, Object>();
	    /*参数设定*/
	    Map<String, Object> paraMap = new HashMap<String, Object>();
	    paraMap.put("costType", type);
	    paraMap.put("year", curYear);
	    paraMap.put("depIds",depIds);
	    for(int i=1;i<=12;i++) {
		    paraMap.put("month", i);
		    if(CommonsConstant.USER_TYPE_MANAGER.equalsIgnoreCase(user.getPosition())){
		    	paraMap.put("depId", user.getDepId());
		    }else {
		    	paraMap.put("depId", "");
		    }
		    List<CostMonthInOut> costChartList = costDaoMapper.getCostChart(paraMap);
		    costMap.put("costChartList"+i, costChartList);
	    }
	    resultMap.put("costMaps", costMap);
	    
	    jsonDto.setCode(0);
	    jsonDto.setObjData(resultMap);
	    
	    return jsonDto;
	}
	
	/**
	 * 获取成本支出类型图表数据
	 * @param user
	 * @return
	 */
	private List<CostInOutlayType> getChartDataByOutlayType(AdminUser user,int curYear){
        
	    CostVO cost = new CostVO();
	    cost.setCostType("1");
	    
        boolean flag = false;
        String position = user.getPosition();
        if(CommonsConstant.USER_TYPE_MANAGER.equalsIgnoreCase(position)){
            cost.setDepId(user.getDepId());
            flag = true;
        }else if(CommonsConstant.USER_TYPE_ADMIN.equalsIgnoreCase(position)){
            flag = true;
        }
        
        List<CostInOutlayType> outlayTypeList = new ArrayList<CostInOutlayType>();
        if(flag){
            cost.setStartTime(DateUtils.stringToTimestamp(curYear+"-01-01 00:00:00"));
            cost.setEndTime(DateUtils.stringToTimestamp(curYear+"-12-31 23:59:59"));
            
            List<CostMonthInOut> costList = costDaoMapper.getCostOutlayType(cost);
            if(CollectionUtils.isNotEmpty(costList)){
                Map<String,List<CostMonthInOut>> map = new HashMap<String,List<CostMonthInOut>>();
                for(CostMonthInOut monthOutlay : costList){
                    List<CostMonthInOut> list = map.get(monthOutlay.getName());
                    if(list == null){
                        list = new ArrayList<CostMonthInOut>();
                    }
                    list.add(monthOutlay);
                    
                    map.put(monthOutlay.getName(), list);
                }
                
                if(map.size() > 0){
                    for(String name : map.keySet()){
                        CostInOutlayType costOutlayType = new CostInOutlayType();
                        costOutlayType.setName(name);
                        costOutlayType.setMonthInOut(map.get(name));
                        
                        outlayTypeList.add(costOutlayType);
                    }
                }
            }
        }
	    
	    return outlayTypeList;
	}
	
	
	/**
     * 获取部门收入图表数据
     * @param user
     * @return
     */
    private List<CostInOutlayType> getChartDataByDepIncome(AdminUser user,int curYear){
        
        CostVO cost = new CostVO();
        cost.setCostType("0");
        
        boolean flag = false;
        String position = user.getPosition();
        if(CommonsConstant.USER_TYPE_MANAGER.equalsIgnoreCase(position)){
            cost.setDepId(user.getDepId());
            flag = true;
        }else if(CommonsConstant.USER_TYPE_ADMIN.equalsIgnoreCase(position)){
            flag = true;
        }
        
        List<CostInOutlayType> departIncomeList = new ArrayList<CostInOutlayType>();
        if(flag){
        	cost.setStartTime(DateUtils.stringToTimestamp(curYear+"-01-01 00:00:00"));
            cost.setEndTime(DateUtils.stringToTimestamp(curYear+"-12-31 23:59:59"));
            
            List<CostMonthInOut> costList = costDaoMapper.getCostDepartIncomeType(cost);
            if(CollectionUtils.isNotEmpty(costList)){
                Map<String,List<CostMonthInOut>> map = new HashMap<String,List<CostMonthInOut>>();
                for(CostMonthInOut monthOutlay : costList){
                    List<CostMonthInOut> list = map.get(monthOutlay.getName());
                    if(list == null){
                        list = new ArrayList<CostMonthInOut>();
                    }
                    list.add(monthOutlay);
                    
                    map.put(monthOutlay.getName(), list);
                }
                
                if(map.size() > 0){
                    for(String name : map.keySet()){
                        CostInOutlayType costOutlayType = new CostInOutlayType();
                        costOutlayType.setName(name);
                        costOutlayType.setMonthInOut(map.get(name));
                        
                        departIncomeList.add(costOutlayType);
                    }
                }
            }
            
        }
        Collections.sort(departIncomeList);
        return departIncomeList;
    }
    
    /**
     * 获取部门支出图表数据
     * @param user
     * @return
     */
    private List<CostInOutlayType> getChartDataByDepOutcome(AdminUser user,int curYear){
        
        CostVO cost = new CostVO();
        cost.setCostType("1");
        
        boolean flag = false;
        String position = user.getPosition();
        if(CommonsConstant.USER_TYPE_MANAGER.equalsIgnoreCase(position)){
            cost.setDepId(user.getDepId());
            flag = true;
        }else if(CommonsConstant.USER_TYPE_ADMIN.equalsIgnoreCase(position)){
            flag = true;
        }
        
        List<CostInOutlayType> departIncomeList = new ArrayList<CostInOutlayType>();
        if(flag){
            cost.setStartTime(DateUtils.stringToTimestamp(curYear+"-01-01 00:00:00"));
            cost.setEndTime(DateUtils.stringToTimestamp(curYear+"-12-31 23:59:59"));
            
            List<CostMonthInOut> costList = costDaoMapper.getCostDepartIncomeType(cost);
            if(CollectionUtils.isNotEmpty(costList)){
                Map<String,List<CostMonthInOut>> map = new HashMap<String,List<CostMonthInOut>>();
                for(CostMonthInOut monthOutlay : costList){
                    List<CostMonthInOut> list = map.get(monthOutlay.getName());
                    if(list == null){
                        list = new ArrayList<CostMonthInOut>();
                    }
                    list.add(monthOutlay);
                    
                    map.put(monthOutlay.getName(), list);
                }
                
                if(map.size() > 0){
                    for(String name : map.keySet()){
                        CostInOutlayType costOutlayType = new CostInOutlayType();
                        costOutlayType.setName(name);
                        costOutlayType.setMonthInOut(map.get(name));
                        
                        departIncomeList.add(costOutlayType);
                    }
                }
            }
            
        }
        Collections.sort(departIncomeList);
        return departIncomeList;
    }
    
	
    /**
     * 获取成本收入支出
     * @param user
     * @return
     */
    public CostTotalInOut getCostTotalInOut(AdminUser user,int curYear){
        
        CostVO cost = new CostVO();
        
        boolean flag = false;
        String position = user.getPosition();
        if(CommonsConstant.USER_TYPE_MANAGER.equalsIgnoreCase(position)){
            cost.setDepId(user.getDepId());
            flag = true;
        }else if(CommonsConstant.USER_TYPE_ADMIN.equalsIgnoreCase(position)){
            flag = true;
        }
        
        CostTotalInOut costTotalInOut = new CostTotalInOut();
        
        if(flag){
            cost.setStartTime(DateUtils.stringToTimestamp(curYear+"-01-01 00:00:00"));
            cost.setEndTime(DateUtils.stringToTimestamp(curYear+"-12-31 23:59:59"));
            cost.setCostType("0");
            
            CostMonthInOut costMonthInOut = costDaoMapper.getCostInOutType(cost);
            if(null != costMonthInOut){
                costTotalInOut.setTotalIncome(costMonthInOut.getTotal().setScale(1, RoundingMode.HALF_UP));
            }
            
            cost.setCostType("1");
            costMonthInOut = costDaoMapper.getCostInOutType(cost);
            if(null != costMonthInOut){
                costTotalInOut.setTotalOut(costMonthInOut.getTotal().setScale(1, RoundingMode.HALF_UP));
            }
            
            costTotalInOut.setBalance(costTotalInOut.getTotalIncome().subtract(costTotalInOut.getTotalOut()).setScale(1, RoundingMode.HALF_UP));
            
        }
        
        return costTotalInOut;
    }
    
    public CostYearVO getRecentYear(int recentYear){
        if(recentYear < 0 || recentYear == 0){
            recentYear = 5;
        }
        
        CostYearVO costYear = new CostYearVO();
        List<Integer> years = new ArrayList<Integer>();
        int currentYear = DateUtils.getCurrentYear();
        
        for(int i= -recentYear;i<0;i++){
            years.add(currentYear + i);
        }
        
        for(int i= 0;i<recentYear;i++){
            years.add(currentYear + i);
        }
        
        costYear.setCurYear(currentYear);
        costYear.setYears(years);
        
        return costYear;
    }

    /**
     * 获取部门月份收入支出图表数据
     * @param sessionUser
     * @param curYear
     * @return
     */
	public List<CostChartsData> getDepCostChartData(AdminUser user, int curYear) {
		
		List<CostChartsData> datas = new ArrayList<CostChartsData>();
		
		CostVO cost = new CostVO();
        boolean flag = false;
        String position = user.getPosition();
        if(CommonsConstant.USER_TYPE_MANAGER.equalsIgnoreCase(position)){
            cost.setDepId(user.getDepId());
            flag = true;
        }else if(CommonsConstant.USER_TYPE_ADMIN.equalsIgnoreCase(position)){
            flag = true;
        }
		
        if(flag){
        	cost.setStartTime(DateUtils.stringToTimestamp(curYear+"-01-01 00:00:00"));
            cost.setEndTime(DateUtils.stringToTimestamp(curYear+"-12-31 23:59:59"));
             
            List<CostMonthInOut> costList = costDaoMapper.getCostDepartInOutcome(cost);
        	if(CollectionUtils.isNotEmpty(costList)){
        		List<String> depMonths = new ArrayList<String>();
        		Map<String,BigDecimal> months = new HashMap<String,BigDecimal>();
        		for(CostMonthInOut costInOut : costList){
        			String name = costInOut.getName();
        			String month = costInOut.getMonth();
        			String type = costInOut.getType();
        			if(null == months.get(name+"-"+month+"-"+type)){
        				months.put(name+"-"+month+"-"+type, costInOut.getTotal());
        				depMonths.add(name+"-"+month+"-"+type);
        			}
        		}
        		//每个月份对应收入和支出
        		List<String> depInOutMonths = new ArrayList<String>();
        		Map<String,BigDecimal> inOutmonths = new HashMap<String,BigDecimal>();
        		for(String key : depMonths){
        			String keyMonth = "";
        			String[] splits = key.split("-");
        			boolean tag = false;
        			if("0".equals(splits[2])){
        				keyMonth = splits[0]+"-"+splits[1]+"-1";
        				depInOutMonths.add(key);
        			}else{
        				tag = true;
        				keyMonth = splits[0]+"-"+splits[1]+"-0";
        			}
        			
        			BigDecimal bigDecimal = months.get(keyMonth);
        			if(null == bigDecimal){
        				//补月份缺失的收入或支出 默认0
        				inOutmonths.put(keyMonth, new BigDecimal(0));
        				depInOutMonths.add(keyMonth);
        			}
        			inOutmonths.put(key, months.get(key));
        			if(tag){
        				depInOutMonths.add(key);
        			}
        			
        		}
        		
        		List<CostMonthInOut> costMonthInOutList = new ArrayList<CostMonthInOut>();
        		String month = null;
        		CostMonthInOut costMonthInOut = null;
        		for(String key : depInOutMonths){
        			String[] splits = key.split("-");
        			if("0".equals(splits[2])){
        				month = splits[1] + "-收入";
        			}else{
        				month = splits[1] + "-支出";
        			}
        			costMonthInOut = new CostMonthInOut();
        			costMonthInOut.setName(splits[0]);
        			costMonthInOut.setMonth(month);
        			costMonthInOut.setTotal(inOutmonths.get(key));
        			
        			costMonthInOutList.add(costMonthInOut);
        		}
        		
        		CostChartsData costChartsData = null;
        	    List<String> xcategories = null;
        	    List<CostChartsSeriesData> seriesDatas = null;
        		
        	    Map<String,Integer> depNameMap = new HashMap<String,Integer>();
        	    int size = costMonthInOutList.size();
        	    for(int i =0;i<size;i++){
        	    	CostMonthInOut costInOut = costMonthInOutList.get(i);
        	    	if(null == depNameMap.get(costInOut.getName())){
        	    		if(CollectionUtils.isNotEmpty(seriesDatas)){
        	    			costChartsData.setSeriesData(seriesDatas);
        	    			datas.add(costChartsData);
        	    		}
        	    		
        	    		depNameMap.put(costInOut.getName(), 1);
        	    		
        	    		xcategories = new ArrayList<String>();
        	    		xcategories.add(costInOut.getName());
        	    		seriesDatas = new ArrayList<CostChartsSeriesData>();
        	    		
        	    		costChartsData = new CostChartsData();
        	    		costChartsData.setXcategories(xcategories);
        	    	}
        	    	
    	    		CostChartsSeriesData costChartsSeriesData = new CostChartsSeriesData();
    	    		List<BigDecimal> data = new ArrayList<BigDecimal>();
    	    		data.add(costInOut.getTotal());
    	    		
    	    		costChartsSeriesData.setName(costInOut.getMonth());
    	    		costChartsSeriesData.setData(data);
        	    	
    	    		seriesDatas.add(costChartsSeriesData);
    	    		
    	    		if( i == (size -1)){
	    				costChartsData.setSeriesData(seriesDatas);
	    				datas.add(costChartsData);
    	    		}
        		}
        	}
        }
        
        //计算部门总收入总支出
        datas = getCostInOutTotal(datas);
        
        //部门排序
        datas = sortByDepartment(datas);
        
		return datas;
	}
    
	private List<CostChartsData> sortByDepartment(List<CostChartsData> datas) {
		String[] departments = new String[]{"GROUP","YSEC","GMO","FDD","CES","ATD","SSC"};
		List<CostChartsData> datasList = new ArrayList<CostChartsData>();
		for(String department : departments){
			for(CostChartsData costData : datas){
				List<String> xcategories = costData.getXcategories();
				if(xcategories.get(0).startsWith(department)){
					datasList.add(costData);
				}
			}
		}
		
		return datasList;
	}

	/**
	 * 计算部门总收入总支出
	 * @param datas
	 * @return
	 */
	private List<CostChartsData> getCostInOutTotal(List<CostChartsData> datas){
	    
	    for(CostChartsData costData : datas){
	        List<CostChartsSeriesData> seriesDatas = costData.getSeriesData();
	        int size = seriesDatas.size();
	        
	        List<BigDecimal> dataIn = new ArrayList<>();
	        List<BigDecimal> dataOut = new ArrayList<>();
	        for(int i = 0; i< size;i++){
	            CostChartsSeriesData costChartsSeriesData = seriesDatas.get(i);
	            List<BigDecimal> data = costChartsSeriesData.getData();
	            if(i % 2 == 0){
	                dataIn.addAll(data);
	            }else{
	                dataOut.addAll(data);
	            }
	        }
	        
	        
	        List<BigDecimal> dataInTotal = new ArrayList<>();
            List<BigDecimal> dataOutTotal = new ArrayList<>();
            BigDecimal intotal = new BigDecimal(0);
            BigDecimal outTotal = new BigDecimal(0);
            
	        for(BigDecimal data : dataIn){
	            intotal = intotal.add(data);
	        }
	        
	        for(BigDecimal data : dataOut){
	            outTotal = outTotal.add(data);
            }
	        
	        dataInTotal.add(intotal);
	        dataOutTotal.add(outTotal);
	        
	        CostChartsSeriesData costChartsSeriesDataIn = new CostChartsSeriesData();
	        costChartsSeriesDataIn.setName("收入");
	        costChartsSeriesDataIn.setData(dataInTotal);
	        
	        CostChartsSeriesData costChartsSeriesDataOut = new CostChartsSeriesData();
	        costChartsSeriesDataOut.setName("支出");
	        costChartsSeriesDataOut.setData(dataOutTotal);
	        
//	        seriesDatas.add(costChartsSeriesDataIn);
//	        seriesDatas.add(costChartsSeriesDataOut);
	        costData.getXcategories().set(0, costData.getXcategories().get(0)+"  (收入 : "+dataInTotal.get(0).setScale(1, RoundingMode.HALF_UP)+"   支出 : "+dataOutTotal.get(0).setScale(1, RoundingMode.HALF_UP)+")");
	    }
	    
	    return datas;
	}

	public JsonDto getDepMonthTotalOutcome(AdminUser user,int curYear,int costType) {
		JsonDto jsonDto = new JsonDto();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("costType", costType);
		paraMap.put("curYear", curYear);

		/*取所有部门*/
		List<Department> depList = depService.getAllDepartment();
		
		Map<String, Object> costMap = new HashMap<String, Object>();
		
		/*设定到最终结果*/
		Map<String, Object> costDepMap = new HashMap<String, Object>();
		//部门经理时设定所属部门
		if(CommonsConstant.USER_TYPE_MANAGER.equalsIgnoreCase(user.getPosition())){
			paraMap.put("depId", user.getDepId());
			CostChartVO costChartVO = new CostChartVO();
			costChartVO.setDepId(user.getDepId());
			costChartVO.setDepName(user.getDepName());
			costChartVO.setCostNum(0);
			
			List<CostChartVO> chartList = costDaoMapper.getCostVOList(paraMap);
			if(chartList.size()==0) {
				/*补全12个月的数据*/
				for(int i=1;i<=12;i++) {
					costChartVO.setCostMonth(String.valueOf(i));
					chartList.add(costChartVO);
				}
				/*设定到最终结果*/
				costDepMap.put("depTotal", 0);
			}else {
				double depCostNum = 0.00;
				for(CostChartVO costChart : chartList) {
					depCostNum = depCostNum + costChart.getCostNum();
				}
				List<CostChartVO> chartLisResutl = new ArrayList<CostChartVO>();
				
				for(int i=1;i<=12;i++) {
					boolean hasTheMonth = false;
					int index = 0;
					for(int j=0;j<chartList.size();j++) {
						if(chartList.get(j).getCostMonth().equals(String.valueOf(i))) {
							hasTheMonth = true;
							index = j;
						}
					}
					if(hasTheMonth) {
						chartLisResutl.add(chartList.get(index));
					}else {
						costChartVO.setCostMonth(String.valueOf(i));
						chartLisResutl.add(costChartVO);
					}
				}
				
				chartList = chartLisResutl;
				
				
				/*设定到最终结果*/
				costDepMap.put("depTotal", depCostNum);
				costDepMap.put("chartList", chartList);
			}
			costMap.put(user.getDepName(), costDepMap);
			
		}else {
			for(Department dep : depList) {
				paraMap.put("depId", dep.getDepId());
				List<CostChartVO> chartList = costDaoMapper.getCostVOList(paraMap);
				
				/*设定到最终结果*/
				costDepMap = new HashMap<String, Object>();
				costDepMap.put("depId", dep.getDepId());
				costDepMap.put("depName", dep.getName());
				/*成本数据为空时填补一条空白数据*/
				CostChartVO costChartVO = new CostChartVO();
				costChartVO.setDepId(dep.getDepId());
				costChartVO.setDepName(dep.getName());
				costChartVO.setCostNum(0);
				if(chartList.size()==0) {
					/*补全12个月的数据*/
					for(int i=1;i<=12;i++) {
						
						chartList.add(costChartVO);
					}
					/*设定到最终结果*/
					costDepMap.put("depTotal", 0);
					costDepMap.put("chartList", chartList);
				}else {
					double depCostNum = 0.00;
					for(CostChartVO costChart : chartList) {
						depCostNum = depCostNum + costChart.getCostNum();
					}
					List<CostChartVO> chartLisResutl = new ArrayList<CostChartVO>();
					
					for(int i=1;i<=12;i++) {
						boolean hasTheMonth = false;
						int index = 0;
						for(int j=0;j<chartList.size();j++) {
							if(chartList.get(j).getCostMonth().equals(String.valueOf(i))) {
								hasTheMonth = true;
								index = j;
							}
						}
						if(hasTheMonth) {
							chartLisResutl.add(chartList.get(index));
						}else {
							chartLisResutl.add(costChartVO);
						}
					}
					
					chartList = chartLisResutl;
					
					
					/*设定到最终结果*/
					costDepMap.put("depTotal", depCostNum);
					costDepMap.put("chartList", chartList);
				}
				costMap.put(dep.getName(), costDepMap);
			}
		}
		
		
		resultMap.put("costs", costMap);

		/*设置返回值*/
		jsonDto.setCode(0);
		jsonDto.setObjData(resultMap);
		return jsonDto;
	}
}
