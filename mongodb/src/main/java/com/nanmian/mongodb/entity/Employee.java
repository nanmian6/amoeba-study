package com.nanmian.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author nanmian
 * @Description:
 * @date 2022/6/5 15:56
 */
@Document("emp") //映射为一条文档数据
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id //映射文档中的_id
    private Integer id;

    @Field("username") //映射为文档中的一个key:value对
    private String name;

    @Field
    private int age;

    @Field
    private Double salary;

    @Field
    private Date birthday;

}
