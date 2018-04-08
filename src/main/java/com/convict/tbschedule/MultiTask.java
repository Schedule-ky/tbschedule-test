package com.convict.tbschedule;

import com.convict.model.OrderInfo;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.TaskItemDefine;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Jamin on 2016/9/14.
 */
public class MultiTask implements IScheduleTaskDealMulti<OrderInfo> {
    @Override
    public boolean execute(OrderInfo[] orderInfos, String s) throws Exception {
        return false;
    }

    @Override
    public List<OrderInfo> selectTasks(String s, String s1, int i, List<TaskItemDefine> list, int i1) throws Exception {
        return null;
    }

    @Override
    public Comparator<OrderInfo> getComparator() {
        return null;
    }
}
