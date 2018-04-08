淘宝定时任务 tbschedule实战
说明：

tbschedule项目其实可以分为两部分：
1）schedule管理控制台。负责控制、监控任务执行状态2）实际执行job的客户端程序。在实际使用时，首先要启动zookeeper, 然后部署tbschedule web界面的管理控制台，最后启动实际执行job的客户机器。这里zookeeper并不实际控制任务调度，它只是负责与N台执行job的客户端通讯，协调、管理、监控这些机器的运行信息。实际分配任务的是tbschedule管理控制台，控制台从zookeeper获取job的运行信息。tbSchedule通过控制ZNode的创建、修改、删除来间接控制Job的执行，执行Job的客户端会监听它们对应ZNode的状态更新事件，从而达到通过tbSchedule控制Job执行的目的。

解决项目依赖：

下载源码tbschedule 源码（使用svn 直接checkout）


进入到 D:\tbschedule\trunk 目录(根据自己下载的源码目录)，运行打包命令：
>mvn clean install -Dmaven.test.skip=true
运行后tbscheduling.jar 被install 到本地仓库（也可以安装到私服），搭建项目即可依赖。（注意打包的版本）

[html] view plain copy
<dependency>  
    <groupId>com.taobao.pamirs.schedule</groupId>  
    <artifactId>tbschedule</artifactId>  
    <version>3.3.3.2</version>  
</dependency>  

启动schedule管理控制台：
源码中 console/ScheduleConsole.war 包提制到tomcat，启动即可。（要提前安装zookeeper，这里使用的本机的zookeeper。多个ZKServer可以使用逗号分隔。）
访问schedule管理控制台，配置zookeeper：http://localhost:8080/ScheduleConsole/schedule/config.jsp



配置完后，可以访问“管理页面”
http://localhost:8080/ScheduleConsole/schedule/index.jsp?manager=true
TbSchedule Console Web站点对应的两个地址
[监控页面]       http://localhost:8081/ScheduleConsole/schedule/index.jsp
[管理页面]       http://localhost:8081/ScheduleConsole/schedule/index.jsp?manager=true
如果以上地址能正常访问则TbSchedule Console的部署配置完成。

tbscheduling.jar接口说明:
包含三个业务接口：
1、IScheduleTaskDeal 调度器对外的基础接口，是一个基类，并不能被直接使用
2、IScheduleTaskDealSingle 单任务处理的接口,继承 IScheduleTaskDeal
3、IScheduleTaskDealMulti 可批处理的任务接口,继承 IScheduleTaskDeal
IScheduleTaskDeal 调度器对外的基础接口

[java] view plain copy
public interface IScheduleTaskDeal<T> {  
/** 
 * 根据条件，查询当前调度服务器可处理的任务  
 * @param taskParameter 任务的自定义参数 
 * @param ownSign 当前环境名称 
 * @param taskQueueNum 当前任务类型的任务队列数量 
 * @param taskQueueList 当前调度服务器，分配到的可处理队列 
 * @param eachFetchDataNum 每次获取数据的数量 
 * @return 
 * @throws Exception 
 */  
public List<T> selectTasks(String taskParameter,String ownSign,int taskQueueNum,List<TaskItemDefine> taskItemList,int eachFetchDataNum) throws Exception;  
  
/** 
 * 获取任务的比较器,只有在NotSleep模式下需要用到 
 * @return 
 */  
public Comparator<T> getComparator();  
}  
IScheduleTaskDealSingle 单任务处理的接口
[java] view plain copy
public interface IScheduleTaskDealSingle<T> extends IScheduleTaskDeal<T> {  
  /** 
   * 执行单个任务 
   * @param task Object 
   * @param ownSign 当前环境名称 
   * @throws Exception 
   */  
  public boolean execute(T task,String ownSign) throws Exception;  
    
}   
IScheduleTaskDealMulti 可批处理的任务接口
[java] view plain copy
public interface IScheduleTaskDealMulti<T>  extends IScheduleTaskDeal<T> {  
   
/** 
 *  执行给定的任务数组。因为泛型不支持new 数组，只能传递OBJECT[] 
 * @param tasks 任务数组 
 * @param ownSign 当前环境名称 
 * @return 
 * @throws Exception 
 */  
  public boolean execute(Object[] tasks,String ownSign) throws Exception;  
}  


配置说明：



任务名称：用于标识“任务”和策略的关联关系；





任务名称：对应调度策略中的任务名称，标识任务和策略的关联关系；

任务处理的SpringBean：对应代码中调度任务的bean

每次获取数据量：调度任务中selectTasks方法中 eachFetchDataNum参数接收

自定义参数：调度任务中selectTasks方法中 taskParameter 参数接收

任务分隔：就是“TaskItem任务项”的说明，参考：http://code.taobao.org/p/tbschedule/wiki/index/  中的“TaskItem任务项”

处理模式：Sleep模式和NotSleep模式的区别
1、ScheduleServer启动的工作线程组线程是共享一个任务池的。
2、在Sleep的工作模式：当某一个线程任务处理完毕，从任务池中取不到任务的时候，检查其它线程是否处于活动状态。如果是，则自己休眠；
   如果其它线程都已经因为没有任务进入休眠，当前线程是最后一个活动线程的时候，就调用业务接口，获取需要处理的任务，放入任务池中，
   同时唤醒其它休眠线程开始工作。
3、在NotSleep的工作模式：当一个线程任务处理完毕，从任务池中取不到任务的时候，立即调用业务接口获取需要处理的任务，放入任务池中。
4、Sleep模式在实现逻辑上相对简单清晰，但存在一个大任务处理时间长，导致其它线程不工作的情况。
5、在NotSleep模式下，减少了线程休眠的时间，避免大任务阻塞的情况，但为了避免数据被重复处理，增加了CPU在数据比较上的开销。
   同时要求业务接口实现对象的比较接口。
6、如果对任务处理不允许停顿的情况下建议用NotSleep模式，其它情况建议用sleep模式。   



测试代码：http://code.taobao.org/svn/tbschedule-test/

参考：

http://code.taobao.org/p/tbschedule/wiki/index/

http://www.111cn.net/jsp/Jsp-Servlet/72059.htm

