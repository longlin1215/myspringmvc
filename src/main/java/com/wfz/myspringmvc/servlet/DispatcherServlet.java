package com.wfz.myspringmvc.servlet;

import com.wfz.myspringmvc.annotation.*;
import com.wfz.myspringmvc.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IDEA
 * author:weifuzhi
 * Date:2018/10/7
 * Time:16:36
 *
 * DispatacherServlet  核心实现
 **/

/*
* @WebServlet是什么？以前我们定义一个Servlet，需要在web.xml中去配置，
* 在Servlet3.0后出现了基于注解的Servlet。
* */
@WebServlet(name = "dispatcherServlet",urlPatterns = "/*",loadOnStartup = 1,
        initParams = {@WebInitParam(name = "base-package",value = "com.wfz.myspringmvc")})
public class DispatcherServlet extends HttpServlet {
    //扫描基础包
    private  String basePackage="";

    //基包下面所有的带包路径全限定类名
    private List<String> packageNames=new ArrayList<String>();

    //注解实例化，注解上的名称：实例化对象
    private Map<String,Object> instanceMap=new HashMap<String, Object>();

    //带包路径的全限定名称：注解上的名称
    private  Map<String,String> nameMap=new HashMap<String, String>();

    //URL地址：Method方法 映射关系，springmvc就是方法调用链
    private  Map<String,Method> urlMethodMap=new HashMap<String, Method>();

    //Method：全限定类名 映射关系，主要是为了Method找到该方法的对象利用反射执行
    private  Map<Method,String> methodPackageMap=new HashMap<Method, String>();

    /**
     * 初始化处理
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        basePackage=config.getInitParameter("base-package");
        try {
            //1.扫描基包得到全部的带包路径全限定名
            scanBasePackage(basePackage);

            //2.把带有@Controller等注解的类实例化放入map中，key为注解上的名称
            instance(packageNames);

            //3.spring IOC注入
            springIOC();

            //4.完成url地址和方法的映射关系
            handlerUrlMethodMap();


        }catch (ClassCastException e){
            e.printStackTrace();
        }catch (InstantiationException e){
            e.printStackTrace();
        }catch (IllegalAccessError e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1.扫描基包得到全部的带包路径全限定名
     * @param basePackage
     */
    private  void scanBasePackage(String basePackage){

        //将路径.替换为/
        //基包是X.Y.Z的形式，而URL是X/Y/Z的形式，需要转换
        URL url=this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.","/"));
        File basePackageFile=new File(url.getPath());

        System.out.println("Scan:"+basePackageFile);

        File[] childFiles=basePackageFile.listFiles();

        for (File file:childFiles){
            if (file.isDirectory()){//如果是目录，递归遍历
                scanBasePackage(basePackage+"."+file.getName());
            }else if(file.isFile()){
                //如果是文件，去掉后缀
                packageNames.add(basePackage+"."+file.getName().split("\\.")[0]);
            }
        }
    }

    /**
     * 2.把带有@Controller等注解的类实例化放入map中，key为注解上的名称
     * @param packageNames
     */
    public  void  instance(List<String> packageNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        if(packageNames.size()<1){
            return;
        }
        for (String string:packageNames){
            Class c=Class.forName(string);

            if(c.isAnnotationPresent(Controller.class)){
                Controller controller= (Controller) c.getAnnotation(Controller.class);
                String controllerName=controller.value();

                instanceMap.put(controllerName,c.newInstance());
                nameMap.put(string,controllerName);
                System.out.println("Controller:"+string+",value:"+controller.value());
            }else if(c.isAnnotationPresent(Service.class)){
                Service service= (Service) c.getAnnotation(Service.class);
                String serviceName=service.value();

                instanceMap.put(serviceName,c.newInstance());
                nameMap.put(string,serviceName);
                System.out.println("Service:"+string+",value:"+service.value());
            }else if(c.isAnnotationPresent(Repository.class)){
                Repository repository= (Repository) c.getAnnotation(Repository.class);
                String repositoryName=repository.value();

                instanceMap.put(repositoryName,c.newInstance());
                nameMap.put(string,repositoryName);
                System.out.println("Repository:"+string+",value:"+repository.value());
            }
        }
    }

    /**
     * 3.spring IOC注入
     * @throws IllegalAccessException
     */
    public  void  springIOC() throws IllegalAccessException {
        for (Map.Entry<String,Object> entry:instanceMap.entrySet()){

            Field[] fields=entry.getValue().getClass().getDeclaredFields();

            for (Field field:fields){
                if(field.isAnnotationPresent(Qualifier.class)){
                    String name=field.getAnnotation(Qualifier.class).value();
                    field.setAccessible(true);
                    field.set(entry.getValue(),instanceMap.get(name));
                }
            }
        }
    }


    /**
     * 4.完成url地址和方法的映射关系
     * @throws ClassNotFoundException
     */
    public  void  handlerUrlMethodMap() throws ClassNotFoundException {

        if ((packageNames.size()<1)){
            return;
        }

        for (String  string:packageNames){

            Class c=Class.forName(string);
            if(c.isAnnotationPresent(Controller.class)){

                Method[] methods=c.getMethods();
                StringBuffer baseUrl=new StringBuffer();

                if (c.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping requestMapping= (RequestMapping) c.getAnnotation(RequestMapping.class);
                    baseUrl.append(requestMapping.value());
                }

                for (Method method:methods){

                    if (method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping= (RequestMapping) method.getAnnotation(RequestMapping.class);

                        baseUrl.append(requestMapping.value());
                        urlMethodMap.put(baseUrl.toString(),method);
                        methodPackageMap.put(method,string);
                    }
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri=req.getRequestURI();
        String contextPath=req.getContextPath();//应该是获取项目名称
        contextPath="/myspringmvc";
        String path=uri.replaceAll(contextPath,"");//将路径的项目名称去掉，因为urlMethodMap存储的路径格式没有项目

        //根据path找到method
        // path:user/insert
        // method:public void com.wfz.myspringmvc.controller.UserController.insert()
        Method method= (Method) urlMethodMap.get(path);


        if(method!=null){
            //通过method获取Controller对象
            String packageName= (String) methodPackageMap.get(method);
            String controllerName= (String) nameMap.get(packageName);

            //获取Controller对象
            UserController userController= (UserController) instanceMap.get(controllerName);
            try {
                method.setAccessible(true);
                method.invoke(userController);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
