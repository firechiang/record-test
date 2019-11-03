##### Spring 定时任务表达式：http://cron.qqe2.com/

##### Eclipse快速Building
```bash
方法1.修改eclipse启动文件 eclipse.ini 中添加启动参数参数: -vmargs -Xmx512m

方法2.关闭自动构建工作区: project -> build Auto…..
　
方法3.在eclipse.ini式中添加了一个命令 -clean
```

##### Eclipse热部署
```bash
<!-- 方案1 -->  注：标签放在<Host>下

<Context docBase="XXAPP" path="/XXAPP" reloadable="true" source="org.eclipse.jst.jee.server:XXAPP"/>
上面这段代码是从我的项目配置中复制的，把其中的/XXAPP换成项目名，重点是设置reloadable="true"，这样每次文件有更改，
保存的时候，就会自动部署到Tomcat服务器，实现热部署。不过，这种部署设置，每次保存的时候，都会重新启动服务器。


<!-- 方案2 -->

<Context docBase="XXAPP" path="/XXAPP" reloadable="false" crossContext="true" source="org.eclipse.jst.jee.server:XXAPP"/>
如上代码，设置reloadable="false" crossContext="true"，那么就可实现每次保存文件的时候，自动部署到服务器中，并且不会重新启动。


<!-- 方案3 -->
-javaagent:D:\springloaded\springloaded-1.2.5.RELEASE.jar -noverify
```

##### Maven 相关
```bash
mvn eclipse:clean eclipse:eclipse -U clean package -Dskip Tests                                                                            # maven重置环境

mvn install:install-file -Dfile=jar包的位置 -DgroupId=上面的groupId -DartifactId=上面的artifactId -Dversion=上面的version -Dpackaging=jar    # maven安装本地jar包命令

mvn clean install -Dmaven.test.skip                                                                                                        # 打包到本地仓库

mvn clean install -Dmaven.test.skip=true -Dmaven.javadoc.skip=true                                                                         # 打包到target文件夹
```

##### lombok使用
```bash
eclipse.ini 最后添加： -javaagent:lombok.jar
```

##### maven 打包插件 maven-assembly-plugin,maven-dependency-plugin
```bash
<build>
    <resources>
        <resource>
            <directory>/src/main/resources</directory>
            <includes>
                <include>*.dll</include>
            </includes>
            <filtering>true</filtering>          
        </resource>
    </resources>
</build>
```

##### tomcat7-maven-plugin常用命令
```bash
tomcat:deploy	                        # 部署一个web war包
tomcat:reload	                        # 重新加载web war包
tomcat:start                            # 启动tomcat
tomcat:stop                             # 停止tomcat
tomcat:undeploy                         # 停止一个war包
tomcat:run	                            # 启动嵌入式tomcat ，并运行当前项目
```
