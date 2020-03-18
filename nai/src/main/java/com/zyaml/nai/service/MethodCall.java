package com.zyaml.nai.service;

import com.zyaml.nai.Exception.Resp;
import com.zyaml.nai.entry.dto.Words;
import com.zyaml.nai.entry.node.Problem;
import com.zyaml.nai.entry.vo.ProblemVO;
import com.zyaml.nai.service.neo.DiffService;
import com.zyaml.nai.service.neo.ProblemService;
import com.zyaml.nai.util.Mould;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author: 994
 * @Date: 2020-03-10 22:25
 */
@Log4j2
@Component
public class MethodCall{
    @Autowired
    private ProblemService problemService;

    @Autowired
    private DiffService diffService;

    /**
     * 找到模板对应的方法
     *
     * <步骤>
     * 1. 匹配每个方法上的 mould 注解的 formal信息
     * 2. 找到对应的方法后调用接口
     * </步骤>
     *
     * @param words
     * @return
     */
    public Resp methodCall(Words words){
        try {
            //获取类名的包名地址
            Class<?> printClass = this.getClass();
            //java反射机制获取所有方法名
            Method[] declaredMethods = printClass.getDeclaredMethods();
            //遍历循环方法并获取对应的注解名称
            for (Method declaredMethod : declaredMethods) {
                // 判断是否方法上存在注解  Mould
                if(declaredMethod.isAnnotationPresent(Mould.class)){
                    // 获取自定义注解对象
                    Mould methodAnno = declaredMethod.getAnnotation(Mould.class);
                    if(methodAnno.format().equals(words.getFormat())){
                        log.info("=====> methodCall.format:"+methodAnno.format());
                        Resp invoke = (Resp) declaredMethod.invoke(this,words);
                        return invoke;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Mould(format = "PID+")
    private Resp pid(Words<String,String> words){
        return problemService.getProblemByPid(words.get("PID"));
    }

    @Mould(format = "PID+dif+")
    private Resp pidDif(Words<String,String> words){
        return problemService.getDiffName(words.get("PID"));
    }

    @Mould(format = "PID+name+")
    private Resp pidName(Words<String,String> words){
        return problemService.getTitle(words.get("PID"));
    }

    @Mould(format = "PID+source+")
    private Resp pidSource(Words words){
        return problemService.getType((String) words.get("PID"));
    }

    @Mould(format = "PID+alg+")
    private Resp pidAlg(Words<String,String> words){
        return problemService.getAlg(words.get("PID"));
    }

    @Mould(format = "PID+time+")
    private Resp pidTime(Words<String,String> words){
        return problemService.getTime(words.get("PID"));
    }

    @Mould(format = "PID+region+")
    private Resp pidRegion(Words<String,String> words){
        return problemService.getRegion(words.get("PID"));
    }

    @Mould(format = "DIF+num+")
    private Resp difCount(Words<String,String> words){
        return diffService.getCount(words.get("DIF"));
    }

    @Mould(format = "DIF+ORI+")
    private Resp difSource1(Words<String,String> words){
        return diffService.getDifAndSource(words.get("DIF"),words.get("source"));
    }

    @Mould(format = "DIF+dif+ORI+")
    private Resp difSource2(Words<String,String> words){
        return diffService.getDifAndSource(words.get("DIF"),words.get("source"));
    }


}