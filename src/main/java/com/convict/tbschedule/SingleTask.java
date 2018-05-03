package com.convict.tbschedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.convict.model.OrderInfo;
import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;

/**
 * Created by Jamin on 2016/9/14.
 */
@Component("singleTask")
public class SingleTask implements IScheduleTaskDealSingle<OrderInfo> {

    /**
     * 执行单个任务，执行 selectTasks 返回的集合
     * @param orderInfo
     * @param ownSign 当前环境名称
     * @throws Exception
     */
    @Override
    public boolean execute(OrderInfo orderInfo, String ownSign) throws Exception {
        //System.out.println("execute : "+orderInfo.getId());
        return true;
    }

    /**
     * 根据条件，查询当前调度服务器可处理的任务
     * @param taskParameter 任务的自定义参数
     * @param ownSign 当前环境名称
     * @param taskQueueNum 当前任务类型的任务队列数量
     * @param taskItemList 当前调度服务器，分配到的可处理队列
     * @param eachFetchDataNum 每次获取数据的数量
     * @return
     * @throws Exception
     */
    @Override
    public List<OrderInfo> selectTasks(String taskParameter,String ownSign,int taskQueueNum,
                                       List<TaskItemDefine> taskItemList,int eachFetchDataNum) throws Exception {
        System.out.println(taskParameter);
        List<OrderInfo> list = new ArrayList<>();
        OrderInfo o = new OrderInfo();
        o.setId("abc");
        list.add(o);

        System.out.println("taskItemList : " + taskItemList.toString());
        System.out.println("eachFetchDataNum : "+eachFetchDataNum);

        return null;
    }

    /**
     * 获取任务的比较器,主要在NotSleep模式下需要用到
     * @return
     */
    @Override
    public Comparator<OrderInfo> getComparator() {
        return null;
    }

}
