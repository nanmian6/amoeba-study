package com.nanmian.mongodb;

import com.mongodb.client.result.UpdateResult;
import com.nanmian.mongodb.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
class MongodbApplicationTests {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void contextLoads() {
    }

    /**
     *
     * 测试连接
     */
    @Test
    public void testCollection(){

        boolean exists = mongoTemplate.collectionExists("emp");

        if (exists) {
            //删除集合
            mongoTemplate.dropCollection("emp");
        }
        //创建集合
        mongoTemplate.createCollection("emp");
    }

    /**
     *
     * 添加测试
     */
    @Test
    public void tesInsert(){

        //添加单条
        Employee employee = new Employee(1, "小明", 20, 10000.00, new Date());
        mongoTemplate.insert(employee);

        List<Employee> list = Arrays.asList(
                new Employee(2, "张三", 21,5000.00, new Date()),
                new Employee(3, "李四", 26,8000.00, new Date()),
                new Employee(4, "王五",22, 8000.00, new Date()),
                new Employee(5, "张龙",28, 6000.00, new Date()),
                new Employee(6, "赵虎",24, 7000.00, new Date()),
                new Employee(7, "赵六",28, 12000.00, new Date()));

        //插入多条数据
        mongoTemplate.insert(list,Employee.class);
    }

    /**
     *
     * 测试查询
     */
    @Test
    public void testFind(){
        System.out.println("-----查询所有文档-----");
        List<Employee> all = mongoTemplate.findAll(Employee.class);
        all.forEach(System.out::println);

        System.out.println("-----根据_id查询-----");
        Employee byId = mongoTemplate.findById(1, Employee.class);
        System.out.println(byId);

        System.out.println("-----findOne返回第一个文档-----");
        Employee employee = mongoTemplate.findOne(new Query(), Employee.class);
        System.out.println(employee);

        System.out.println("-----条件查询-----");
        //new Query()表示没有条件

        //查询薪资大于等于8000的员工
        Query query = new Query(Criteria.where("salary").gte(8000));
        //查询薪资大于4000小于10000的员工
        //Query query = new Query(Criteria.where("salary").gt(4000).lt(10000));
        //正则查询（模糊查询） java中正则不需要有//
        //Query query = new Query(Criteria.where("name").regex("张"));

        //and or 多条件查询
        //Criteria criteria = new Criteria();
        //and 查询年龄大于25&薪资大于8000的员工
        //criteria.andOperator(Criteria.where("age").gt(25),Criteria.where("salary").gt(8000));
        //or 查询姓名是张三或者薪资大于8000的员工
        //criteria.orOperator(Criteria.where("name").is("张 三"),Criteria.where("salary").gt(5000));
        //Query query = new Query(criteria);

        //sort排序
        query.with(Sort.by(Sort.Order.desc("salary")));

        //skip limit 分页 skip用于指定跳过记录数，limit则用于限定返回结果数量。
        query.with(Sort.by(Sort.Order.desc("salary")))
                                .skip(0) //指定跳过记录数
                                .limit(4); //每页显示记录数

        //查询结果
        List<Employee> employees = mongoTemplate.find( query, Employee.class);
        employees.forEach(System.out::println);
    }

    /**
     *
     * 测试查询
     */
    @Test
    public void testFindByJson(){
        //使用json字符串方式查询
        //等值查询
        //String json = "{name: '张三'}";

        //多条件查询
        String json = "{$or:[{age: {$gt: 25}}, {salary: {$gte:8000}}]}";
        Query query = new BasicQuery(json);

        //查询结果
        List<Employee> employees = mongoTemplate.find(query, Employee.class);
        employees.forEach(System.out::println);
    }

    /**
     *
     * 测试修改
     */
    @Test
    public void testUpdate(){

        Query query = new Query(Criteria.where("salary").gte(8000));

        List<Employee> list = mongoTemplate.find(query, Employee.class);
        System.out.println("-----更新前-----");
        list.forEach(System.out::println);

        Update update = new Update();
        update.set("salary", 13000);

        //只更新满足条件的第一条记录
        //UpdateResult result = mongoTemplate.updateFirst(query, update, Employee.class);


        //更新所有满足条件的记录
        UpdateResult result = mongoTemplate.updateMulti(query, update, Employee.class);

        /*
            AcknowledgedUpdateResult{
                      matchedCount=4,  //匹配的记录数
                      modifiedCount=3, //真实修改的记录数
                      upsertedId=null } //
            System.out.println(result);
        */

        // upsert() 没有符合条件的记录则插入数据
        // update.setOnInsert("id",11);// 指定_id
        // UpdateResult updateResult = mongoTemplate.upsert(query, update, Employee.class);
        // 返回修改的记录数
        // System.out.println(updateResult.getModifiedCount());

         System.out.println("==========更新后===========");
         list = mongoTemplate.find(query, Employee.class);
         list.forEach(System.out::println);
    }

    /**
     *
     * 测试删除
     */
    public void testDelete(){

        //删除所有文档
        // mongoTemplate.remove(new Query(),Employee.class);

        // 条件删除
        Query query = new Query(Criteria.where("salary").gte(10000));
        mongoTemplate.remove(query,Employee.class);

    }



}
